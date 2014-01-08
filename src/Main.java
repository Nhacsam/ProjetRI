import java.util.Arrays;


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
		FilesHandler f = new FilesHandler ("data/query.txt","");
		
		ConfigHandler conf = new ConfigHandler() ;
		conf.readConfFile("data/conf.ini");
		
		
		System.out.println( Arrays.toString(f.ReadQueries()) );
		
		System.out.println( "fini" );
	}
	
	
	
	
	
}
