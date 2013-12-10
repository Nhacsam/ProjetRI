
/**
 * @author nikkidbz
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Index index = new Index();
		index.constructIndex("data/documents/coll");
		//index.load( "data/index.bin" );
		index.test();
		index.save( "data/index.bin" );
		
		
		System.out.println( "fini" );
	}

}
