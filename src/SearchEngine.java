import java.util.Hashtable;


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
		Result[] testArr = new Result[2] ;
		testArr[0] = new Result( "file1.xml" );
		testArr[1] = new Result( "file2" );
		return testArr;
	}
	
	
}
