import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;


/**
 * Coeur du moteur de recherche
 * Cherche dans l'index les mots clefs de la recherche et compile les résultats
 * @author nikkidbz
 */
public class SearchEngine {
	
	/**
	 *  Index des documents
	 *  @var m_index
	 */
	private Index m_index ;
	
	/**
	 * Méthode de calul des pondérations
	 * @var m_method
	 */
	private String m_method = "ltn";
	
	/**
	 * Liste de méthode autorisé pour le calcul des poids
	 * @var m_autorizedMethods 
	 */
	static private String[] m_autorizedMethods = { "ltn" } ;
	
	/**
	 * Nombre maximum de résultats
	 * @var m_nbResultsMax
	 */
	private int m_nbResultsMax = 1500 ;
	
	
	
	public SearchEngine( Index index, Hashtable< String, String> params) {
		m_index = index ;
		
		if( params.containsKey("method") && Arrays.binarySearch(m_autorizedMethods, params.get("method")) >= 0 )
			m_method = params.get("method") ;
		
		if( params.containsKey("nbresult") ) {
			m_nbResultsMax =  Integer.parseInt(params.get("nbresult")) ;
		}
		
	}
	
	/**
	 * Effectue la requête et récupère les résultats
	 * @param q Requête à effectuer
	 * @return Result[] Résultats de la requête
	 */
	public Result[] makeQuery( Query q ) throws InvalidParameterException {
		
		
		LinkedList<Result> resultsList = new LinkedList<Result>();	// Liste des résultats à renvoyer
		
		String[] keywords = q.getKeywords() ;						// Tableau des mots clefs de la requête
		int n = keywords.length ;									// Nombre de mots clefs
		
		ListIterator<Occurence>[] linesIte = new ListIterator[n] ;	// Iterateurs qui vont parcourir les ligne de la matrice
		Occurence[] currentOcc = new Occurence[ n ];				// Occurence qu'on est en train de traitées
		boolean[] usedLastTime = new boolean[ n ];					// Si les occurences était les moins avancée au tours d'avant (ie on les a utilisées)
		int[] df = new int[ n ];									// df des mots clefs
		
		// Pour chaque mot clefs : Initialisation 
		for( int i=0; i< n; i++) {
			// Récupération des lignes
			LinkedList<Occurence> list = m_index.get(keywords[i]) ;
			
			if( list != null ) {
				// Initialisation des itérateurs, des occurences et du df
				linesIte[i] = list.listIterator(0);
				currentOcc[i] = linesIte[i].next() ;
				df[i] = list.size() ;
			} else {
				// Ligne inexistance (mot clef non trouvé dans la collection)
				linesIte[i] = null ;
				currentOcc[i] = null ;
				df[i] = 0 ;
			}
			// On les a pas encore utilisé
			usedLastTime[i] = false ;
		}
		
		// Tant que tous les itérateurs ne sont pas arrivé au bout
		while( OneHasNext( linesIte ) ) {
			
			int min = -1 ;						// Id du document à considéré (minimum des courants)
			boolean[] hasMin = new boolean[n];	// Si la ligne est la moins avancé (en est au document d'id le plus faible)
			clearArr( hasMin);
			
			// Pour chaque ligne
			for( int i=0; i< n; i++) {
				
				// On décale ceux qu'on a utilisé au tour précédent
				if( usedLastTime[i] ) {
					usedLastTime[i] = false ;
					if( linesIte[i].hasNext() )
						currentOcc[i] = linesIte[i].next() ;
					else
						currentOcc[i] = null ;
				}
				
				// Si on est arrivé au bout de la ligne, on passe à la suivante
				if( currentOcc[i] == null )
					continue ;
				
				// On cherche les ligne qui en sont au document d'id le plus faible et on les note comme tel.
				if( currentOcc[i].getDocument().getId() > min ) {
					min = currentOcc[i].getDocument().getId() ;
					clearArr( hasMin );
					hasMin[i] = true ;
				} else if ( currentOcc[i].getDocument().getId() == min ) {
					hasMin[i] = true ;
				}
				
			}
			
			// Résultat qui va contenir les infos sur le document courant (d'id = min)
			Result r = null ;
			
			// Pour chaque mot clef
			for ( int i=0; i< n; i++) {
				
				// Si la liste en est plus loins que le document considéré
				// On s'en occupe pas tout de suite.
				if( !hasMin[i] )
					continue;
				
				// On note qu'on s'en est servi pour passé au suivant au prochain tour
				usedLastTime[i] = true ;
				
				// Si r = null, on l'inicialise
				if( r == null ) {
					r = new Result( currentOcc[i].getDocument().getPath() );
					r.setWeight(0);
					r.setWeightToConsider(true);
				}
				
				// Calcul du poid pour le document et ajout 
				r.addWeight( computeWeight(currentOcc[i],  df[i]) );
			}
			// Ajout du résultat à la liste
			resultsList.addFirst(r);
		}
		
		// On transforme les résultats en tableau et on le trie
		Result[] resultsArray = new Result[ resultsList.size() ];
		resultsList.toArray( resultsArray ) ;
		Arrays.sort( resultsArray );
		
		// On fait gaffe à ne renvoyer que 1500 résultats
		if( resultsArray.length >  m_nbResultsMax )
			resultsArray = Arrays.copyOf(resultsArray,  m_nbResultsMax ) ;
		
		return resultsArray ;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Calcul du poid d'un terme dans le document 
	 * @param o	Occurence du terme
	 * @param df Document frequency du terme
	 * @return Poid Calculé
	 * @throws InvalidParameterException
	 */
	public double computeWeight( Occurence o, int df ) throws InvalidParameterException {
		
		switch (m_method) {
		
			case "ltn" :
				return ltnWeight(o, df) ;
		
			default :
				throw new InvalidParameterException("method");
		}
	}
	
	
	/**
	 * Calcul du poid d'un terme dans le document - Methode ltn
	 * @param o	Occurence du terme
	 * @param df Document frequency du terme
	 * @return Poid Calculé
	 */
	public double ltnWeight( Occurence o, int df ) {
		double w = 1 + Math.log( o.getTf() ) ;
		w *= Math.log(m_index.getNbDoc() / df );
		return w ;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Regarde si un des itérateurs du tableau l n'est pas finit
	 * @param l Tableau des itérateurs
	 * @return true Si un des itérateur n'est pas finit
	 */
	private boolean OneHasNext( ListIterator[] l ) {
		for (int i =0; i < l.length ; i++) {
			if ( l[i] != null && l[i].hasNext() )
				return true ;
		}
		return false ;
	}
	
	/**
	 * Met à false tous les éléments d'un tableau de booléen
	 * @param b Tableau
	 */
	private void clearArr( boolean[] b ) {
		for (int i =0; i < b.length ; i++) {
			b[i] = false ;
		}
	}
	
	
}
