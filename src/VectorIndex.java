import java.util.Hashtable;
import java.util.Iterator;



/**
 * Index ne comprenant qu'un seul Document
 * @author nikkidbz
 *
 */
public class VectorIndex {
	
	

	/**
	 *  Table de hashage représentant le vecteur 
	 *  Chaque ligne se récupère grace au mot correspondant
	 *  La line comprend qu'une Occurences
	 *  @var m_vector
	 *  @see Occurence.java
	 */
	private Hashtable<String, Occurence > m_vector ;
	
	private Document m_document ;
	
	
	/**
	 * Poids pour les tf des différentes balise pour le calcul de roberson
	 */
	private Hashtable<String, Integer > m_robersonV = null ;
	
	
	/**
	 *  Constructeur
	 *  @param document Document que l'on indexe
	 */
	public VectorIndex( Document document ){
		m_vector = new Hashtable<String, Occurence >();
		m_document = document ;
	}
	
	/**
	 *  Constructeur
	 *  @param document Document que l'on indexe
	 *  @param robersonFile Fichier contenant les information sur les poid de roberson ( "" si pas de roberson)
	 */
	public VectorIndex( Document document, String robersonFile ){
		m_vector = new Hashtable<String, Occurence >();
		m_document = document ;
		
		if( robersonFile != "" && robersonFile != null ) {
			ConfigHandler confHan = new ConfigHandler() ;
			confHan.readConfFile(robersonFile);
			
			Hashtable<String, String > conf = confHan.getOtherConfig() ;
			
			Iterator<String> ite = conf.keySet().iterator() ;
			while( ite.hasNext() ) {
				String key = ite.next() ;
				m_robersonV.put(key, Integer.parseInt( conf.get(key) ) );
			}
			
			
		}
	}
	
	/**
	 * Ajoute un mot à l'index 
	 * @param w Mot à ajouter
	 * @param stem Si il faut racinimiser le mot
	 */
	public void addWord( String w, boolean stem ){
		
		w = w.toLowerCase();
		if( stem )
			w = SearchEngine.racinisation( w ) ;
		if( m_vector.containsKey(w) )
			m_vector.get(w).addOccurence();
		else {
			Occurence occ = new Occurence(m_document) ;
			m_vector.put(w, occ);
		}
	}
	
	/**
	 * Ajoute un mot à l'index avec sa balise associée
	 * @param w Mot à ajouter
	 * @param d Balise dans laquel est contenue le mot (adresse complète)
	 * @param stem Si il faut racinimiser le mot
	 */
	public void addWord( String w, DOMElement d, boolean stem ){
		
		w = w.toLowerCase();
		if( stem )
			w = SearchEngine.racinisation( w ) ;
		if( m_vector.containsKey(w) )
			m_vector.get(w).addOccurence( d );
		else {
			Occurence occ = new Occurence(m_document, d) ;
			m_vector.put(w, occ);
		}
	}
	
	/**
	 * Ajoute un mot à l'index avec sa balise associée avec le calcul de roberson
	 * @param w Mot à ajouter
	 * @param d Balise dans laquel est contenue le mot (adresse complète)
	 * @param stem Si il faut racinimiser le mot
	 */
	public void addRobersonWord( String w, DOMElement d, boolean stem, Tag t ){
		
		if( w.matches("^(.+)?\\d(.+)?$") )
			return ;
		
		w = w.toLowerCase();
		if( stem )
			w = SearchEngine.racinisation( w ) ;
		
		int v = 1 ;
		if( m_robersonV != null && m_robersonV.containsKey(t.name) ) {
			v = m_robersonV.get(t.name) ;
		}
			
		
		if( m_vector.containsKey(w) )
			m_vector.get(w).addOccurence( d, v );
		else {
			Occurence occ = new Occurence(m_document, d, v) ;
			m_vector.put(w, occ);
		}
	}
	
	
	
	/**
	 * @return the m_document
	 */
	public Document getDocument() {
		return m_document;
	}

	/**
	 * @return the m_vector
	 */
	public Hashtable<String, Occurence> getVector() {
		return m_vector;
	}
	
}
