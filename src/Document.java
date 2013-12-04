import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Document {
	
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
	 * longueur du fichier en nombre de mot
	 */
	private int m_length ;
	
	
	/**
	 * Constructeur par défaut (à éviter)
	 */
	public Document(){}
	
	/**
	 * Constructeur
	 * @var name Nom du fichier
	 * @var path Chemin relatif vers le fichier
	 */
	public Document(String name, String path){
		m_name = name ;
		m_path = path ;
	}
	
	/**
	 * Constructeur
	 * @var file Fichier 
	 */
	public Document(File file){
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
					documentIndex.addWord(words[i]) ;
				
			}
			reader.close();
			
		} catch (IOException e ) {
			System.out.print("Une erreur est survenue") ;
		}
		
		return documentIndex;
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
	 * @return the m_length
	 */
	public int getLength() {
		return m_length;
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
