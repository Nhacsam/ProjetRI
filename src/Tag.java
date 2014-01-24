import java.util.LinkedList;
import java.util.ListIterator;


/**
 * Représente une balise de la forme /name[num]
 * par exemple /article[1] correspondra à :
 * name = article
 * num = 1
 * @author nikkidbz
 *
 */
public class Tag {
	
	/**
	 * Nom de la balise
	 */
	public String name = "article";
	
	/**
	 * Numéro de la balise dans le DOM
	 */
	public int num = 1 ;
	
	/**
	 * Balise inclues dans celle-ci
	 */
	public LinkedList<Tag> children ;
	
	/**
	 * Constructeurs
	 */
	public Tag() {}
	public Tag( String name, int num) {
		this.name = name ;
		this.num = num ;
		children = new  LinkedList<Tag>();
	}
	
	/**
	 * Cherche dans les balises inclues les balises de même nom
	 * @param name Nom des balises à chercher
	 * @return Le numéro le plus élevé des balises trouvées. 0 sinon
	 */
	public int searchInChildren( String sname ) {
		int snum = 0 ;
		
		ListIterator<Tag> ite = children.listIterator(0);
		
		while( ite.hasNext() ) {
			Tag t = ite.next() ;
			if( t.name == sname && t.num > snum )
				snum = t.num ;
		}
		
		return snum ;
	}
	
}
