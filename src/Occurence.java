
/**
 * Elément de la liste chainée de l'index
 * Contient les informations sur le terme et le document
 * @author nikkidbz
 *
 */
public class Occurence {
	
	/**
	 * @var m_document qui contient l'occurence du mot
	 */
	private Document m_document ;
	
	/**
	 * @var m_therme contenu dans le document
	 */
	private String m_word ;
	
	/**
	 * @var m_tf Therme frequency
	 */
	private int m_tf ;
	
	
	/**
	 * @var m_next Occurence suivante pour la Liste chainée
	 */
	private Occurence m_next ;
	
	
	/**
	 * Contructeur 
	 * @param document Document qui contient l'occurence du mot
	 * @param word  Therme contenu dans le document
	 */
	public Occurence (Document document, String word ) {
		m_document = document ;
		m_word = word ;
	}
	
	/**
	 * Passe à l'élément suivant
	 * @return Occurence
	 */
	public Occurence next() {
		return m_next ;
	}
	
	/**
	 * Insère l'occurence à la suite dans la liste chainée
	 * @param Occurence next Element à insérer à la suite
	 * @return Occurence this
	 */
	public Occurence insertAfter( Occurence next ) {
		next.m_next = m_next ;
		m_next = next ;
		return this ;
	}
	
	
	/* Getter et Setter */
	
	/**
	 * @return the m_document
	 */
	public Document getDocument() {
		return m_document;
	}

	/**
	 * @param m_document the m_document to set
	 */
	public void setDocument(Document m_document) {
		this.m_document = m_document;
	}

	/**
	 * @return the m_word
	 */
	public String getWord() {
		return m_word;
	}

	/**
	 * @param m_word the m_word to set
	 */
	public void setWord(String m_word) {
		this.m_word = m_word;
	}

	/**
	 * @return the m_tf
	 */
	public int getTf() {
		return m_tf;
	}

	/**
	 * @param m_tf the m_tf to set
	 */
	public void setTf(int m_tf) {
		this.m_tf = m_tf;
	}
	
}
