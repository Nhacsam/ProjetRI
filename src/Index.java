import java.io.File;
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
public class Index {
	
	
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Index [m_matrix=" + m_matrix + "]";
	}
	
	
}
