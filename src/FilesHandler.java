import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;



/**
 * Gère la lecture et l'écriture dans les fichiers
 * @author nikkidbz
 *
 */
public class FilesHandler {
	
	private String m_queriesFile ;
	private String m_resultsFiles ;
	
	//private int m_maxNbResult = 1500 ;
	private String m_teamName = "DjambazianHunglerLopViricelle" ; 
	
	
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
	
	
	public void printResults(Result[] r) {
		printResults(r, null );
	}
	
	public void printResults(Result[] r, Query q ) {
			
		try {
			PrintWriter file = new PrintWriter(new BufferedWriter(new FileWriter(m_resultsFiles, true)));
		
			String line ;
			for ( int i =0; i < r.length; i++ ) {
				// Format : ID_requete Q0 Fichier Rang Poid NomduGroupe Balise
				
				// ID_requete
				if( r[i].getQuery() != null ) {
					line = r[i].getQuery().getQueryCode() + " " ;
				} else if( q != null ) {
					line = q.getQueryCode() + " " ;
				} else {
					throw new RuntimeException( "No Query given for wrinting output file." );
				}
				
				// Q0 
				line += "Q0 ";
				
				// Fichier
				line += r[i].getFileName()+" " ;
				
				// Rang
				if( r[i].getRank() != -1 ) {
					line += r[i].getRank()+" " ;
				} else {
					line += i + " " ;
				}
				
				// Poid
				if( r[i].isWeightToConsider() ) {
					line += r[i].getWeight()+" " ;
				} else {
					line += (r.length - i) + " " ;
				}
				
				// NomduGroupe
				line += m_teamName +" " ;
				
				// balise
				line += r[i].getTag() ;
				
				file.println(line);
				
			}
			file.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
