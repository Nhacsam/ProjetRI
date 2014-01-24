import java.util.Hashtable;



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
	 *  Constructeur
	 */
	public VectorIndex( Document document ){
		m_vector = new Hashtable<String, Occurence >();
		m_document = document ;
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
