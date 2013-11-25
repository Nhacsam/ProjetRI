import java.io.* ;
import java.util.StringTokenizer;

/**
 *  Classe gérant l'index des mots clefs pour le moteur de recherche
 *  
 *  Implémente des méthodes pour enregistrer, charger, construire, ... l'index
 * @author nikkidbz
 *
 */
public class Index {
	
	/**
	 *  Constructeur
	 *  TODO
	 */
	public Index(){}
	
	/**
	 * Ajoute les fochiers contenus dans le dossier à l'index (récursif)
	 * TODO
	 * @param String folder Dossier contenant les fichiers à indexer
	 */
	public void constructIndex( String folder) {
		
		
		
	}
	
	/**
	 * Parse un fichier texte pur et l'ajoute à l'index
	 * @param String filename Nom du fichier à parser
	 */
	public void parseTxtFile (String filename) {
		
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
			
		} catch (IOException e ) {
			System.out.print("Une erreur est survenue") ;
		}
		
		
	}
	
	
	
	/**
	 * Ajoute un document à l'index
	 * TODO
	 * @param fileName
	 */
	public void addDocumentToIndex( String fileName ) {
		
	
	}
}
