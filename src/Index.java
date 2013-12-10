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
	 *  Constructeur
	 */
	public Index(){
		m_matrix = new Hashtable<String, LinkedList<Occurence> >();
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
	
	public void save ( String filename ) {
		try {
			
			// ouverture d'un flux de sortie vers le fichier "personne.serial"
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
	
	
	public void load (String filename) {
		Index loaded = null ;
		
		try {
			// ouverture d'un flux d'entrée depuis le fichier "personne.serial"
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
	
	
	
	
	
	public void test(){
		System.out.println( m_matrix.get("usually") );
	}
	
	
	
	
	
	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Index [m_matrix=" + m_matrix + "]";
	}
	
	
}
