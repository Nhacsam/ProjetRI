import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;



/**
 * Gère la lecture et l'écriture dans les fichiers
 * @author nikkidbz
 *
 */
public class FilesHandler {
	
	private String m_queriesFile ;
	private String m_resultsFiles ;
	
	
	public FilesHandler (String queriesFile, String resultsFiles ) {
		m_queriesFile = queriesFile ;
		m_resultsFiles = resultsFiles ;
	}
	
	public Query[] ReadQueries() {
		
		BufferedReader file ;
		ArrayList<Query> queries = new ArrayList<Query>();
		
		try {
			file = new BufferedReader(new FileReader( m_queriesFile )) ;
			
			while ( file.ready() ) {
				
				String line = file.readLine() ;
				if( line != "") {
					
					String[] queryLine = line.split(" ");
					String[] keywords = new String[ queryLine.length -1 ];
					System.arraycopy(queryLine, 1, keywords, 0, queryLine.length -1);
					queries.add( new Query( keywords, queryLine[0] ) );
					
				}
			
			}
			file.close();
		} catch (NullPointerException a) {
			a.printStackTrace();
		} catch (IOException a) {
			a.printStackTrace();
		}
		
		Query[] queriesArray = new Query[ queries.size() ] ;
		queries.toArray( queriesArray );
		return queriesArray ;
	}
	
	
	
}
