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
	 * Nombre maximum de résultats
	 * @var m_nbResultsMax
	 */
	private int m_nbResultsMax = 1500 ;
	
	
	
	public SearchEngine( Index index, Hashtable< String, String> params) {
		m_index = index ;
		
		if( params.containsKey("method"))
			m_method = params.get("method") ;
	}
	
	/**
	 * Effectue la requête et récupère les résultats
	 * @param q Requête à effectuer
	 * @return Result[] Résultats de la requête
	 */
	public Result[] makeQuery( Query q ) {
		
		switch (m_method) {
		
			case "ltn" :
				return ltnQuery(q) ;
		
			default :
				Result[] testArr = new Result[2] ;
				testArr[0] = new Result( "file1.xml" );
				testArr[1] = new Result( "file2" );
				return testArr;
		}
	}
	
	
	public Result[] ltnQuery( Query q ) {
		
		LinkedList<Result> resultsList = new LinkedList<Result>();
		String[] keywords = q.getKeywords() ;
		
		int n = keywords.length ;
		
		ListIterator<Occurence>[] linesIte = new ListIterator[n] ;
		Occurence[] currentOcc = new Occurence[ n ];
		boolean[] usedLastTime = new boolean[ n ];
		int[] df = new int[ n ];
		
		for( int i=0; i< n; i++) {
			 LinkedList<Occurence> list = m_index.get(keywords[i]) ;
			
			if( list != null ) {
				linesIte[i] = list.listIterator(0);
				currentOcc[i] = linesIte[i].next() ;
				df[i] = list.size() ;
			} else {
				linesIte[i] = null ;
				currentOcc[i] = null ;
				df[i] = 0 ;
			}
			usedLastTime[i] = false ;
			
			
		}
		
		while( OneHasNext( linesIte ) ) {
			
			int min = -1 ;
			boolean[] hasMin = new boolean[n];
			clearArr( hasMin);
			
			for( int i=0; i< n; i++) {
				if( usedLastTime[i] ) {
					usedLastTime[i] = false ;
					if( linesIte[i].hasNext() )
						currentOcc[i] = linesIte[i].next() ;
					else
						currentOcc[i] = null ;
				}
				
				if( currentOcc[i] == null )
					continue ;
				
				if( currentOcc[i].getDocument().getId() > min ) {
					min = currentOcc[i].getDocument().getId() ;
					clearArr( hasMin );
					hasMin[i] = true ;
				} else if ( currentOcc[i].getDocument().getId() == min ) {
					hasMin[i] = true ;
				}
				
			}
			
			Result r = null ;
			
			for ( int i=0; i< n; i++) {
				if( !hasMin[i] )
					continue;
				
				usedLastTime[i] = true ;
				
				if( r == null ) {
					r = new Result( currentOcc[i].getDocument().getPath() );
					r.setWeight(0);
					r.setWeightToConsider(true);
				}
				
				//System.out.println( currentOcc[i].getTf() + " - " + m_index.getNbDoc() + " - " + df[i] ) ;
				
				double w = 1 + Math.log( currentOcc[i].getTf() ) ;
				w *= Math.log(m_index.getNbDoc() / df[i] );
				
				r.addWeight(w);
			}
			resultsList.addFirst(r);
		}
		
		Result[] resultsArray = new Result[ resultsList.size() ];
		resultsList.toArray( resultsArray ) ;
		Arrays.sort( resultsArray );
		
		if( resultsArray.length >  m_nbResultsMax )
			resultsArray = Arrays.copyOf(resultsArray,  m_nbResultsMax ) ;
		
		return resultsArray ;
	}
	
	private boolean OneHasNext( ListIterator[] l ) {
		for (int i =0; i < l.length ; i++) {
			if ( l[i] != null && l[i].hasNext() )
				return true ;
		}
		return false ;
	}
	
	private void clearArr( boolean[] b ) {
		for (int i =0; i < b.length ; i++) {
			b[i] = false ;
		}
	}
	
	
}
