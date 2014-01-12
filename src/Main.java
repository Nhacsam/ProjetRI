import java.util.Arrays;
import java.util.Hashtable;


/**
 * @author nikkidbz
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		System.out.print( "parsing the configuration ... " );
		// Parse le fichier de configuration et les paramêtres
		ConfigHandler conf = new ConfigHandler() ;
		conf.readConfFile("data/conf.ini");
		try {
			conf.readParams(args);
		} catch( HelpException e ) {
			return;
		}
		
		// Récupère la configuration
		Hashtable<String, String > confHash =  conf.getConfig() ;
		
		System.out.println( "done." );
		System.out.print( "creating the index ... " );
		
		// Crée l'index de la collection
		Index index = new Index( confHash );
		
		System.out.println( "done." );
		
		// Si on ne veux pas juste l'index
		if( ! (confHash.containsKey("indexOnly") && confHash.get("indexOnly") != "false") ) {
			
			System.out.print( "loading the query file ... " );
			
			// Prépare le gestionnaire de fichier
			FilesHandler fileHand = new FilesHandler (confHash);
			fileHand.cleanResultFile();
			
			// Récupère les requêtes
			Query[] queries = fileHand.ReadQueries();
			
			System.out.println( "done." );
			System.out.print( "making the query ... " );
			
			// Effectue les recherches et les enregistre
			SearchEngine engine = new SearchEngine(index, confHash );
			for (int i =0; i < queries.length; i++ ) {
				Result[] result = engine.makeQuery( queries[i]) ;
				fileHand.printResults(result, queries[i]);
			}
			
			System.out.println( "done." );
		}
		
		System.out.print( "closing the index ... " );
		
		// Ferme l'index et l'enregistre si besoins
		index.close();
		
		System.out.println( "done." );
	}
	
	
	
	
	
}
