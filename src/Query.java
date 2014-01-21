import java.util.Arrays;
import java.util.Hashtable;


/**
 * Représente un requête à effectuer
 * @author nikkidbz
 */
public class Query {
	
	/**
	 * Tableau des mots clef
	 * @var m_keywords
	 */
	private String[] m_keywords ;
	
	/**
	 * Code de la requête
	 * @var m_queryCode
	 */
	private String m_queryCode ;
	
	/**
	 * Vecteur des poids des mots clefs de la requête ( "+" )
	 * @var m_keywordsWeight
	 */
	private int[] m_keywordsWeight ;
	
	/**
	 * Bonus apporté aux mots clefs précédé par un plus
	 */
	private int m_bonus = 2;
	
	public Query(){}
	public Query(Hashtable<String, String > conf, String[] m_keywords, String m_queryCode) {
		super();
		if ( conf.containsKey("keybonus") )
			m_bonus = Integer.parseInt(conf.get("keybonus")) ;
		
		setKeywords( m_keywords ) ;
		setQueryCode(m_queryCode ) ;
	}

	/**
	 * @return the m_keywords
	 */
	public String[] getKeywords() {
		return m_keywords;
	}

	/**
	 * Enregistre les mots et parse le +
	 * @param m_keywords the m_keywords to set
	 */
	public void setKeywords(String[] keywords) {
		m_keywords = keywords;
		
		m_keywordsWeight = new int[ keywords.length ] ;
		for( int i =0; i < m_keywords.length; i++ ) {
			if( m_keywords[i].charAt(0) == '+' ) {
				m_keywordsWeight[i] = m_bonus ;
				m_keywords[i] = m_keywords[i].substring(1) ;
			} else {
				m_keywordsWeight[i] = 1 ;
			}
		}
		for ( int i =0; i < m_keywordsWeight.length; i++)
			System.out.print( m_keywords[i] + " " );
		System.out.println("");
		for ( int i =0; i < m_keywordsWeight.length; i++)
			System.out.print( m_keywordsWeight[i] + " " );
		System.out.println("");
	}
	
	public int[] getKeywordsWeight () {
		return m_keywordsWeight ;
	}

	/**
	 * @return the m_queryCode
	 */
	public String getQueryCode() {
		return m_queryCode;
	}

	/**
	 * @param m_queryCode the m_queryCode to set
	 */
	public void setQueryCode(String m_queryCode) {
		this.m_queryCode = m_queryCode;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Query [m_keywords=" + Arrays.toString(m_keywords)
				+ ", m_queryCode=" + m_queryCode + "]";
	}
	
	
}
