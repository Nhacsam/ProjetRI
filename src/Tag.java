import java.util.LinkedList;


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
	
}
