import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Classe gérant la lecture du fichier de config et les paramètres
 * @author nikkidbz
 */
public class ConfigHandler {
	
	/**
	 * Tableau des configurations
	 */
	private Hashtable<String, String > m_config ;
	
	
	/**
	 * Liste des paramêtre possible en -p ou --param
	 */
	static private String[][] m_simplesParams= {
		{ "i", "indexOnly", "Construit simplement l'index." },
		{ "l", "loadIndex", "Charge l'index en mémoire plutôt que de le reconstruire." },
		{ "s", "saveIndex", "Enregistre l'index après sa constuction eventuelle."},
		{ "x", "xml", "Fait la recherche en prenant compte des balises xml." },
		{ "h", "help", "Affiche l'aide." },
		{ "R", "racin", "Racinisation de l'index et de la recherche." },
		{ "L", "links", "Suis les liens pour l'indexation et lles utilise lors de la pondération." }
	};
	
	/**
	 *  Liste des paramètres possibles en -p=value ou --param=value
	 *  Merci de faire préfixer la forme longue des paramètre en rapport avec une méthode par son nom
	 */
	static private String[][] m_affectParams = {
		{ "s", "src", "Fichier source contenant les requêtes." },
		{ "t", "target", "Fichier de destination pour les résultats." },
		{ "n", "nbresult", "Nombre maximum de résultat pour une requête." },
		{ "d", "documents", "Dossier contenant les documents." },
		{ "I", "indexFile", "Fichier où charger ou enregistrer l'index." },
		{ "c", "indexFileRac", "Fichier où charger ou enregistrer l'index racinisé (défaut = indexFile)." },
		{ "e", "state", "No de l'étape." },
		{ "r", "run", "No du run." },
		{ "t", "team", "Nom de l'équipe." },
		{ "m", "method", "Methode de calcul des poids." },
		{ "b", "keybonus", "Bonus à associer aux éléments de la requête commençant par un +." },
		{ "M", "linksMethod", "Méthode de calcul des pondération à partir des liens." },
		{ "K", "bm25k", "Paramètre k1 de bm25" },
		{ "B", "bm25b", "Paramètre b de bm25" }
	};
	
	
	static private String[][] m_defaultValues = {
		{"src", "data/query.txt"},
		{"state", "01"},
		{"run", "01"},
		{"method", "ltn"},
		{"team", "DjambazianHunglerLopViricelle"},
		{"indexFile", "data/index.bin"},
		{"documents", "data/documents/coll" },
		{"nbresult", "1500" },
		{"keybonus", "2" },
		{"linksMethod", "n" }
	};
	
	
	
	
	public ConfigHandler() {
		m_config = new Hashtable<String, String >();
		for( int i = 0; i < m_defaultValues.length; i++) {
			m_config.put(m_defaultValues[i][0], m_defaultValues[i][1]);
		}
	}
	
	/**
	 * Lit le fichier de configuration
	 * @param path Chemin du fichier de configuration
	 */
	public void readConfFile( String path ) {
		
		Properties conf = new Properties();
		String elmt = "" ; 
		
		try {
			conf.load( new FileInputStream( path ) );
			
			Enumeration<?> keys = conf.propertyNames();
			while( keys.hasMoreElements() ) {
				elmt = keys.nextElement().toString() ;
				m_config.put( elmt, conf.getProperty(elmt) ) ;
			}
			
		} catch (IOException ex) {
			
			ex.printStackTrace();
		}
		
	}
	
	/**
	 *  Lit les arguments de la ligne de commande 
	 * @param args Arguments du main
	 * @throws HelpException
	 */
	public void readParams(String[] args) throws HelpException{
		
		Options options = new Options();
		
		
		for (int i =0; i < m_simplesParams.length; i++) {
			options.addOption(m_simplesParams[i][0], m_simplesParams[i][1], false, m_simplesParams[i][2]);
		}
		
		for (int i =0; i < m_affectParams.length; i++) {
			options.addOption(m_affectParams[i][0], m_affectParams[i][1], true, m_affectParams[i][2]);
		}
		
		CommandLineParser parser = new GnuParser();
		try {
			
			CommandLine line = parser.parse( options, args );
			Iterator<Option> ite = line.iterator();
			
			while( ite.hasNext() ) {
				Option opt = ite.next() ;
				
				if( opt.getLongOpt() == "help") {
					HelpFormatter formatter = new HelpFormatter();
					formatter.printHelp( "RI", options );
					throw new HelpException() ;
				} else if( opt.getValue() == null )
					m_config.put(opt.getLongOpt(), "false");
				else
					m_config.put(opt.getLongOpt(), opt.getValue() );
			}
			
		} catch( ParseException e ){
			e.printStackTrace();
		}
		
		if ( ! m_config.containsKey("indexFileRac") )
			m_config.put("indexFileRac", m_config.get("indexFile") );
		
		
	}
	
	/**
	 * Récupère les configurations parsées
	 * @return configs
	 */
	public Hashtable<String, String > getConfig() {
		return m_config ;
	}
	
}
