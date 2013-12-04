import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;



/**
 *  Classe gérant l'index des mots clefs pour le moteur de recherche
 *  
 *  Implémente des méthodes pour enregistrer, charger, construire, ... l'index
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
	 * TODO
	 * @param String folder Dossier contenant les fichiers à indexer
	 */
	public void constructIndex( String folder) {
		
		
		
	}
	
	
	/**
	 * Ajoute un document à l'index
	 * TODO
	 * @param fileName
	 */
	public void addDocumentToIndex( String fileName ) {
		
		
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
		
		// Parcour le vecteur
		while (itr.hasNext()) {
			w = itr.next();
			
			// AJoute à la fin si la ligne existait déjà
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
	
	
}
