import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

/**
 * Classe enregistrant les informations sur un Document
 * @author nikkidbz
 *
 */
public class Document implements Serializable {
	
	static private final long serialVersionUID = 1L ;
	
	
	/**
	 * Id du document
	 */
	private int m_id ;
	
	/**
	 *  Nom DU document
	 *  @var m_name
	 */
	private String m_name ;
	
	/**
	 * Chemin vers le fichier
	 * @var m_path
	 */
	private String m_path ;
	
	/**
	 * Longueur du fichier en nombre de mot
	 * @var m_length
	 */
	private int m_length ;
	
	/**
	 * Nombre de lien vers le fichier
	 */
	private int m_links ;
	
	/**
	 * Si il faut raciniser les mots
	 * @var m_stem
	 */
	private boolean m_stem = false;
	
	/**
	 * Fichier conant les poids de roberson
	 */
	private String m_robfile = "" ;
	
	/**
	 * Profondeur max
	 */
	private int m_maxdepth ;
	
	/**
	 * Constructeur par défaut (à éviter)
	 */
	public Document(){}
	
	/**
	 * Constructeur
	 * @var name Nom du fichier
	 * @var path Chemin relatif vers le fichier
	 */
	public Document(int id, String name, String path){
		m_id = id ;
		m_name = name ;
		m_path = path ;
	}
	
	/**
	 * Constructeur
	 * @var file Fichier 
	 */
	public Document(int id, File file){
		m_id = id ;
		m_name = file.getName() ;
		m_path = file.getPath() ;
	}
	
	
	
	
	
	/**
	 * Parse un fichier texte pur et l'ajoute à l'index
	 * @param path Nom du fichier à parser (facultatif)
	 * @return Index du document
	 */
	public VectorIndex parseTxtFile (){ return parseTxtFile(m_path); }
	public VectorIndex parseTxtFile (String path) {
		
		// Lecteur du fichier
		BufferedReader reader ;
		// Ligne lue
		String line ;
		// Index de retour
		VectorIndex documentIndex = new VectorIndex( this ) ;
		
		
		try {
			// Ouverture du fichier
			reader = new BufferedReader( new FileReader( path ) ) ;
			
			// Tant qu'il y a des lignes à lire
			while( (line = reader.readLine()) != null ) {
				
				// Séparation en mots
				String[] words = line.split("(\\p{Blank}|\\p{Punct})+");
				
				// Ajout de la taille (pour le calcul de df)
				m_length +=  words.length ;
				
				// Ajout des mots dans l'index
				for( int i=0; i < words.length; i++)
					documentIndex.addWord(words[i], m_stem) ;
				
			}
			reader.close();
			
		} catch (IOException e ) {
			System.err.println("Une erreur est survenue.") ;
		}
		
		return documentIndex;
	}
	
	/**
	 * Parse un fichier xml et l'ajoute à l'index
	 * @param path Nom du fichier à parser (facultatif)
	 * @param usedElement Liste des éléments déjà utilisé
	 * @param docs Liste des documents de la collection
	 * @return Index du document
	 */
	public VectorIndex parseXmlFile (Hashtable<String, DOMElement> usedElement, Hashtable<String, Document> docs)
	{ return parseXmlFile(m_path, usedElement, docs); }
	
	public VectorIndex parseXmlFile (String path, Hashtable<String, DOMElement> usedElement, Hashtable<String, Document> docs) {
		
		VectorIndex index = new VectorIndex( this );
		XmlParser parser = new XmlParser( index, usedElement, docs , m_stem, m_robfile, m_maxdepth ) ;
		
		
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance(); 
			SAXParser sp = spf.newSAXParser(); 
			XMLReader saxReader = sp.getXMLReader(); 
	        saxReader.setContentHandler( parser );
	        saxReader.parse(path);
	        
	        
		} catch (Exception e ) {
			e.printStackTrace() ;
			return new VectorIndex( this );
			
		}
		return index ;
	}
	
	
	/**
	 * Set racinination
	 */
	public void setStem( boolean stem ) {
		m_stem = stem ;
	}
	
	/**
	 * Set roberson file
	 */
	public void setRob( String robFile ) {
		m_robfile = robFile ;
	}
	
	/**
	 * Increment le nombre de lien vers le document
	 */
	public void addLinks() {
		m_links++ ;
	}
	
	/**
	 * get nombre de lien
	 */
	public int getLinks() {
		return m_links ;
	}
	
	/**
	 * @return the m_name
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * @return the m_path
	 */
	public String getPath() {
		return m_path;
	}
	
	/**
	 * @param length Valeur à ajouté à la longeur du doc
	 */
	public void addLength( int length ) {
		m_length += length;
	}
	
	
	/**
	 * @return the m_length
	 */
	public int getLength() {
		return m_length;
	}
	
	
	public void setMaxdepth(int d) {
		m_maxdepth = d ;
	}
	
	/**
	 * @return the m_id
	 */
	public int getId() {
		return m_id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Document [m_name=" + m_name + ", m_path=" + m_path
				+ ", m_length=" + m_length + "]";
	}
}
