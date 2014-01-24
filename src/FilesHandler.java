import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;



/**
 * Gère la lecture et l'écriture dans les fichiers
 * @author nikkidbz
 *
 */
public class FilesHandler {
	
	/**
	 * Fichier source contenant les requêtes
	 * @var m_queriesFile
	 */
	private String m_queriesFile ;
	
	/**
	 * Fichier où enregistrer les résultats de la recherche
	 * @var m_resultsFiles
	 */
	private String m_resultsFiles = "data/results_NomAFormater.txt" ;
	
	/**
	 * Nom de l'équipe
	 * @var m_teamName
	 */
	private String m_teamName = "DjambazianHunglerLopViricelle" ;
	
	public FilesHandler (String queriesFile, String resultsFiles ) {
		m_queriesFile = queriesFile ;
		m_resultsFiles = resultsFiles ;
	}
	
	public FilesHandler (String queriesFile ) {
		m_queriesFile = queriesFile ;
	}
	
	public FilesHandler ( Hashtable<String, String > config ) {
		
		if( config.containsKey("src") )
			m_queriesFile = config.get("src");
		
		if( config.containsKey("team") )
			m_teamName = config.get("team");
		
		if( config.containsKey("target") )
			m_resultsFiles = config.get("target");
		
		else if (config.containsKey("state") && config.containsKey("run") && config.containsKey("method") ) {
			// Format : NomEquipe_n°etape_n°run_pondération_granularité_paramètres.txt
			
			String state = config.get("state");
			if( state.length() == 0) {
				state = "00";
			} else if (state.length() == 1) {
				state = "0" + state ;
			} else if ( state.length() > 2) {
				state = state.charAt(0) +""+ state.charAt(1) ;
			}
			
			String run = config.get("run");
			if( run.length() == 0) {
				run = "00";
			} else if (run.length() == 1) {
				run = "0" + state ;
			} else if ( run.length() > 2) {
				run = run.charAt(0) +""+ run.charAt(1) ;
			}
			
			String method = config.get("method");
			String gran = ( config.containsKey("xml") && config.get("xml") != "false" ) ? "elements" : "articles" ;
			
			String params = "" ;
			Enumeration<String> keys = config.keys();
			while( keys.hasMoreElements() ) {
				String k = keys.nextElement() ;
				if( k.startsWith(method) ) {
					params += k.replaceFirst(method, "")+config.get(k);
				}
			}
			if( config.containsKey("racin") && config.get("racin") != "false" )
				params += "racin";
			
			
			if( params == "" )
				params = "none" ;
			
			// Format : NomEquipe_n°etape_n°run_pondération_granularité_paramètres.txt
			m_resultsFiles = "data/" + m_teamName+"_"+state+"_"+run+"_"+method+"_"+gran+"_"+params+".txt" ;
		}
	}
	
	
	/**
	 * Lit le fichier de requête
	 * @return Requête parsées
	 */
	public Query[] ReadQueries( Hashtable<String, String > config ) {
		
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
					queries.add( new Query( config, keywords, queryLine[0] ) );
					
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
	
	/**
	 * Crée le fichier de résultat ou le vide si il existait déjà
	 */
	public void cleanResultFile() {
		try {
			PrintWriter writer = new PrintWriter(m_resultsFiles);
			writer.print("");
			writer.close();
		} catch( FileNotFoundException e) {}
	}
	
	
	
	/**
	 * Ecrit les résulats des requêtes dans le fichier résultat
	 * La requête doit ête spécifier dans les obkets résultats
	 * @param r Tableau de résulats
	 */
	public void printResults(Result[] r) {
		printResults(r, null );
	}
	
	/**
	 * Ecrit les résulats des requêtes dans le fichier résultat
	 * @param r Tableau de résulats
	 * @param q Requête associée aux résultats
	 */
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
					line += (i+1) + " " ;
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
