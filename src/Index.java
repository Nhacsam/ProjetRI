import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;



/**
 * Classe gérant l'index des mots clefs pour le moteur de recherche
 *  
 * Implémente des méthodes pour enregistrer, charger, construire, ... l'index
 * @author nikkidbz
 *
 */
public class Index implements Serializable{
	
	static private final long serialVersionUID = 1L ;
	/**
	 *  Table de hashage représentant la matrice 
	 *  Chaque ligne se récupère grace au mot correspondant
	 *  La line est une liste chainée des Occurences
	 *  @var m_matrix
	 *  @see Occurence.java
	 */
	private Hashtable<String, LinkedList<Occurence> > m_matrix ;
	
	
	/**
	 * Définit si on doit parser les balises xml
	 * @var m_xml
	 */
	private boolean m_xml = false ;
	
	/**
	 * Définit si il faut enregistrer l'index à la fin
	 * @var m_toSave
	 */
	private boolean m_toSave = false ;
	
	/**
	 * Chemin vers la sauvegarde de l'index
	 * @var m_indexFilename
	 */
	private String m_indexFilename = "data/index.bin" ;
	
	/**
	 *  Constructeur
	 */
	public Index(){
		m_matrix = new Hashtable<String, LinkedList<Occurence> >();
	}
	
	public Index( Hashtable<String, String > conf ) {
		m_matrix = new Hashtable<String, LinkedList<Occurence> >();
		
		if ( conf.containsKey("xml") && conf.get("xml") != "false" )
			m_xml = true ;
		
		if ( conf.containsKey("saveIndex") && conf.get("saveIndex") != "false" )
			m_toSave = true ;
		
		if ( conf.containsKey("indexFile") )
			m_indexFilename =  conf.get("indexFile") ;
		
		String documentFolder = ( conf.containsKey("documents") ) ? conf.get("documents") : "data/documents/coll" ;
		
		if ( conf.containsKey("loadIndex") && conf.get("loadIndex") != "false" )
			load( m_indexFilename );
		else
			constructIndex( documentFolder );
			
	}
	
	
	
	/**
	 * Ajoute les fichiers contenus dans le dossier à l'index (récursif)
	 * @param String folder Dossier contenant les fichiers à indexer
	 */
	public void constructIndex( String folder) {
		
		File mainDir = new File( folder );
		if( !mainDir.exists() ) {
			System.err.println(	"Dossier "+folder+" inexistant." );
			return ;
		} else if( !mainDir.isDirectory() ) {
			System.err.println( "Le dossier principal n'est pas un dossier." );
			return ;
		} else {
			
			// Pour chaque fichier dans le dossier
			File[] files = mainDir.listFiles() ;
			for (int i = 0; i < files.length; i++ ) {
				
				// Si dossier, recursivité
				if( files[i].isDirectory() )
					this.constructIndex( files[i].getPath() );
				
				// Sinon, on indexe le fichier
				else
					addDocumentToIndex(  files[i] );
			}
		}
	}
	
	
	/**
	 * Ajoute un document à l'index
	 * @param file
	 */
	public void addDocumentToIndex( File file ) {
		Document newDoc = new Document(file) ;
		VectorIndex documentIndex = newDoc.parseTxtFile() ;
		mergeVector(documentIndex);
		
	}
	
	/**
	 * Fusionne un vecteur avec l'index
	 * @param v Vecteur à fusionner
	 */
	public void mergeVector( VectorIndex v ) {
		
		Hashtable<String, Occurence > Vector = v.getVector() ;
		
		// Crée l'itérateur
		Set<String> set = Vector.keySet();
		Iterator<String> itr = set.iterator();
		String w ;
		
		// Parcoure le vecteur
		while (itr.hasNext()) {
			w = itr.next();
			
			// Ajoute à la fin si la ligne existait déjà
			if( m_matrix.containsKey(w) )
				m_matrix.get(w).addLast( Vector.get(w) );
			
			// Si elle n'existait pas, crée la liste
			else {
				LinkedList<Occurence> list = new LinkedList<Occurence>();
				list.addFirst( Vector.get(w) );
				m_matrix.put(w, list);
			}
			
		}
	}
	
	
	/**
	 * Enregistre l'index si besoin
	 */
	public void close() {
		if( m_toSave )
			save( m_indexFilename );
	}
	
	
	/**
	 * Enregistre l'index dans le fichier filename
	 * @param filename Chemin vers le fichier à utiliser
	 */
	public void save ( String filename ) {
		try {
			
			// ouverture d'un flux de sortie vers le fichier filename
			FileOutputStream fos = new FileOutputStream(filename);
	
			// création d'un "flux objet" avec le flux fichier
			ObjectOutputStream oos= new ObjectOutputStream(fos);
			
			try {
				// sérialisation : écriture de l'objet dans le flux de sortie
				oos.writeObject(this); 
				// on vide le tampon
				oos.flush();
			} finally {
				//fermeture des flux
				try {
					oos.close();
				} finally {
					fos.close();
				}
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Charge l'index sauvegardé dans le fichier filename
	 * @param filename Chemin vers le fichier sauvegardé
	 */
	public void load (String filename) {
		Index loaded = null ;
		
		try {
			// ouverture d'un flux d'entrée depuis le fichier filename
			FileInputStream fis = new FileInputStream(filename);
			// création d'un "flux objet" avec le flux fichier
			ObjectInputStream ois= new ObjectInputStream(fis);
			try {	
				// désérialisation : lecture de l'objet depuis le flux d'entrée
				loaded = (Index) ois.readObject(); 
			} finally {
				// on ferme les flux
				try {
					ois.close();
				} finally {
					fis.close();
				}
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		
		m_matrix = loaded.m_matrix ;	
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Index [m_matrix=" + m_matrix + "]";
	}
	
	
}
