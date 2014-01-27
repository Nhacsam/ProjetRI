import java.io.Serializable;
import java.util.LinkedList;


/**
 * Elément de la liste chainée de l'index
 * Contient les informations sur le terme et le document
 * @author nikkidbz
 *
 */
public class Occurence implements Serializable {
	
	static private final long serialVersionUID = 1L ;
	
	/**
	 * @var m_document qui contient l'occurence du mot
	 */
	private Document m_document ;
	
	/**
	 * @var m_tf Therme frequency
	 */
	private int m_tf ;
	
	
	/**
	 * Balises dans lesquelles sont contenue les mots 
	 */
	private LinkedList<DOMElement> m_tag ;
	
	/**
	 * Constructeur 
	 * @param document Document qui contient l'occurence du mot
	 */
	public Occurence (Document document) {
		m_document = document ;
		m_tf = 1 ;
		
		m_tag = new LinkedList<DOMElement>() ;
	}
	
	/**
	 * Constructeur 
	 * @param document Document qui contient l'occurence du mot
	 * @param tag Balise dans laquelle est contenue le premier mot
	 */
	public Occurence (Document document, DOMElement tag) {
		m_document = document ;
		m_tf = 1 ;
		
		m_tag = new LinkedList<DOMElement>() ;
		m_tag.add(tag) ;
	}
	
	/**
	 * Constructeur 
	 * @param document Document qui contient l'occurence du mot
	 * @param tag Balise dans laquelle est contenue le premier mot
	 */
	public Occurence (Document document, DOMElement tag, int RobersonV ) {
		m_document = document ;
		m_tf = RobersonV ;
		
		m_tag = new LinkedList<DOMElement>() ;
		m_tag.add(tag) ;
	}
	
	/**
	 *  Indique qu'on a trouvé une occurence de plus
	 * @param tag Balise dans laquelle est contenue le mot
	 */
	public void addOccurence() {
		m_tf++ ;
	}
	

	/**
	 *  Indique qu'on a trouvé une occurence de plus
	 */
	public void addOccurence( DOMElement tag) {
		m_tf++ ;
		m_tag.add(tag) ;
	}
	
	/**
	 *  Indique qu'on a trouvé une occurence de plus
	 *  Permet de spécifier un poid de pour roberson
	 */
	public void addOccurence( DOMElement tag, int RobersonV) {
		m_tf += RobersonV ;
		m_tag.add(tag) ;
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
	
	/**
	 * @return tags
	 */
	public LinkedList<DOMElement> getTags() {
		return m_tag ;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "\n\tOccurence [ \n\t\tm_document=" + m_document
				+ "\n\t, m_tf=" + m_tf + "]\n";
	}
	
}
