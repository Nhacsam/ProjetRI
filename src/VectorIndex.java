import java.util.Hashtable;



/**
 * Index ne comprenant qu'un sul Document
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
	
	
	
	public void addWord( String w ){
		
		w = w.toLowerCase();
		if( m_vector.containsKey(w) )
			m_vector.get(w).addOccurence();
		else {
			Occurence occ = new Occurence(m_document, w) ;
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
