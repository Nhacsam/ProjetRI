import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Document {
	
	private String m_name ;
	
	private String m_path ;
	
	private int m_length ;
	
	
	
	/**
	 * Constructeur
	 */
	public Document(){}
	
	
	/**
	 * Parse un fichier texte pur et l'ajoute à l'index
	 * @param String filename Nom du fichier à parser
	 * @return Index Index du document
	 */
	public Index parseTxtFile (String filename) {
		
		// Lecteur du fichier
		BufferedReader reader ;
		// Ligne lue
		String line ;
		
		try {
			// Ouverture du fichier
			reader = new BufferedReader( new FileReader( filename ) ) ;
			
			// Tant qu'il y a des lignes à lire
			while( (line = reader.readLine()) != null ) {
				
				String[] words = line.split("(\\p{Blank}|\\p{Punct})+");
				for( int i=0; i < words.length; i++)
					System.out.print( words[i] + " - ");
				/*
				// Crée un tokenizer qui sépare les mots
				StringTokenizer token = new StringTokenizer( line, " ,.;:!?()\"'«»") ;
				
				while( token.hasMoreTokens() )
					System.out.print( token.nextToken() + " - ");
				//*/
			}
			reader.close();
			
		} catch (IOException e ) {
			System.out.print("Une erreur est survenue") ;
		}
		
		return new Index();
	}
	
	
}
