import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;


/**
 * Classe gérant la lecture du fichier de config et les paramètres
 * @author nikkidbz
 */
public class ConfigHandler {
	
	/**
	 * Tableau des configurations
	 */
	private Hashtable<String, String > m_config ;
	
	public ConfigHandler() {}
	
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
	
	
}
