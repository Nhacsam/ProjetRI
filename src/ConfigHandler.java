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
		{ "I", "indexOnly", "Construit simplement l'index." },
		{ "l", "loadIndex", "Charge l'index en mémoire plutôt que de le reconstruire." },
		{ "S", "saveIndex", "Enregistre l'index après sa constuction eventuelle."},
		{ "x", "xml", "Fait la recherche en prenant compte des balises xml." },
		{ "h", "help", "Affiche l'aide." },
		{ "R", "racin", "Racinisation de l'index et de la recherche." },
		{ "L", "links", "Suis les liens pour l'indexation et lles utilise lors de la pondération." },
		{ "v", "roberson", "Fait le calcul des poids de Roberson." }
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
		{ "e", "state", "No de l'étape." },
		{ "r", "run", "No du run." },
		{ "t", "team", "Nom de l'équipe." },
		{ "m", "method", "Methode de calcul des poids." },
		{ "b", "keybonus", "Bonus à associer aux éléments de la requête commençant par un +." },
		{ "M", "linksMethod", "Méthode de calcul des pondération à partir des liens." },
		{ "V", "robersonFile", "Fichier contenant les informations des poids de roberson." },
		{ "K", "bm25k", "Paramètre k1 de bm25" },
		{ "B", "bm25b", "Paramètre b de bm25" },
		{ "D", "maxdepth", "Profondeur maximum de parcours des fichiers" },
		
		{ "i", "indexFile", "Fichier où charger ou enregistrer l'index." },
		{ "ic", "indexFileRac", "Fichier où charger ou enregistrer l'index racinisé (défaut = indexFile)." },
		{ "ix", "indexFileXml", "Fichier où charger ou enregistrer l'index xml (défaut = indexFile)." },
		{ "ixc", "indexFileXmlRac", "Fichier où charger ou enregistrer l'index xml racinisé (défaut = indexFile)." },
		{ "ixr", "indexFileXmlRob", "Fichier où charger ou enregistrer l'index avec roberson (défaut = indexFile)." },
		{ "ixl", "indexFileXmlLink", "Fichier où charger ou enregistrer l'index avec gestion des liens (défaut = indexFile)." },
		{ "ixcr", "indexFileXmlRacRob", "Fichier où charger ou enregistrer l'index xml racinisé  avec roberson (défaut = indexFile)." },
		{ "ixcl", "indexFileXmlRacLink", "Fichier où charger ou enregistrer l'index xml racinisé avec gestion des liens (défaut = indexFile)." },
		{ "ixrl", "indexFileXmlRobLink", "Fichier où charger ou enregistrer l'index avec roberson et gestion des liens (défaut = indexFile)." },
		{ "ixcrl", "indexFileXmlRacRobLink", "Fichier où charger ou enregistrer l'index xml racinisé avec roberson et gestion des liens (défaut = indexFile)." }
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
		{"linksMethod", "n" },
		{"robersonFile", "data/rob1.txt" }
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
	}
	
	/**
	 * Récupère les configurations parsées
	 * @return configs
	 */
	public Hashtable<String, String > getConfig() {
		
		boolean xml = false ;
		boolean rac = false ;
		boolean rob = false ;
		boolean link = false ;
		if ( m_config.containsKey("xml") && m_config.get("xml") != "false" )
			xml = true ;
		if ( m_config.containsKey("racin") && m_config.get("racin") != "false" )
			rac = true ;
		if ( m_config.containsKey("roberson") && m_config.get("roberson") != "false" )
			rob = true ;
		if ( m_config.containsKey("links") && m_config.get("links") != "false" )
			link = true ;
		
		
		if ( ! xml && rac && m_config.containsKey("indexFileRac") )
			m_config.put("indexFile", m_config.get("indexFileRac") );
		
		else if ( xml && !rac && !rob && !link && m_config.containsKey("indexFileXml") )
			m_config.put("indexFile", m_config.get("indexFileXml") );
		
		else if ( xml && rac && !rob && !link && m_config.containsKey("indexFileXmlRac") )
			m_config.put("indexFile", m_config.get("indexFileXmlRac") );
		
		else if ( xml && !rac && rob && !link && m_config.containsKey("indexFileXmlRob") )
			m_config.put("indexFile", m_config.get("indexFileXmlRob") );
		
		else if ( xml && !rac && !rob && link && m_config.containsKey("indexFileXmlLink") )
			m_config.put("indexFile", m_config.get("indexFileXmlLink") );
		
		else if ( xml && rac && rob && !link && m_config.containsKey("indexFileXmlRacRob") )
			m_config.put("indexFile", m_config.get("indexFileXmlRacRob") );
		
		else if ( xml && rac && !rob && link && m_config.containsKey("indexFileXmlRacLink") )
			m_config.put("indexFile", m_config.get("indexFileXmlRacLink") );
		
		else if ( xml && !rac && rob && link && m_config.containsKey("indexFileXmlRobLink") )
			m_config.put("indexFile", m_config.get("indexFileXmlRobLink") );
		
		else if ( xml && rac && rob && link && m_config.containsKey("indexFileXmlRacRobLink") )
			m_config.put("indexFile", m_config.get("indexFileXmlRacRobLink") );
		
		return m_config ;
	}
	
	public Hashtable<String, String > getOtherConfig() {
		return m_config ;
	}
	
}
