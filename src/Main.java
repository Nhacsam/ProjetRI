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
		/*
		Index index = new Index();
		index.constructIndex("data/documents/coll");
		//index.load( "data/index.bin" );
		index.test();
		index.save( "data/index.bin" );
		*/

		ConfigHandler conf = new ConfigHandler() ;
		conf.readConfFile("data/conf.ini");
		try {
			conf.readParams(args);
		} catch( HelpException e ) {
			return;
		}
		
		Hashtable<String, String > confHash =  conf.getConfig() ;
		
		FilesHandler f = new FilesHandler (confHash);
		
		
		
		//System.out.println( Arrays.toString(f.ReadQueries()) );
		
		System.out.println( "fini" );
	}
	
	
	
	
	
}
