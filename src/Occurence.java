
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
	 * Constructeur 
	 * @param document Document qui contient l'occurence du mot
	 * @param word  Therme contenu dans le document
	 */
	public Occurence (Document document, String word ) {
		m_document = document ;
		m_word = word ;
		m_tf = 1 ;
	}
	
	/**
	 *  Indique qu'on a trouvé une occurence de plus
	 */
	public void addOccurence() {
		m_tf++ ;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "\n\tOccurence [ \n\t\tm_document=" + m_document + "\n\t, m_word=" + m_word
				+ "\n\t, m_tf=" + m_tf + "]\n";
	}
	
}
