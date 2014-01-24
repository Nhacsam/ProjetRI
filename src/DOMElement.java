import java.util.LinkedList;
import java.util.ListIterator;


/**
 * Représente un élément du DOM (avec tout le chemin) dan sun fichier xml
 * @author nikkidbz
 */
public class DOMElement {
	
	/**
	 * Chemin complet de la balise
	 */
	private String m_DOMpath = "/article[1]" ;
	
	/**
	 * Constructeur vide
	 */
	public DOMElement () {} 
	
	/**
	 * Constructeur à partir d'une chaine déjà construite
	 * @param path Chemin complet de la balise. Ex : /article[1]/body[1]/section[3]/a[1]
	 */
	public DOMElement ( String path ) {
		m_DOMpath = path ;
	}
	
	/**
	 * COnstruction à partir d'une liste de balise
	 * @param tags
	 */
	public DOMElement ( LinkedList<Tag> tags) {
		
		ListIterator<Tag> ite = tags.listIterator(0);
		
		m_DOMpath = "" ;
		while( ite.hasNext() ) {
			Tag t = ite.next() ;
			m_DOMpath += "/" + t.name + "[" + t.num + "]";
		}
	}

	/**
	 * @return the m_DOMpath
	 */
	public String getDOMpath() {
		return m_DOMpath;
	}

	/**
	 * @param m_DOMpath the m_DOMpath to set
	 */
	public void setDOMpath(String m_DOMpath) {
		this.m_DOMpath = m_DOMpath;
	}
}
