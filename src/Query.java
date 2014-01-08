import java.util.Arrays;


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
	
	
	public Query(){}
	
	public Query(String[] m_keywords, String m_queryCode) {
		super();
		this.m_keywords = m_keywords;
		this.m_queryCode = m_queryCode;
	}

	/**
	 * @return the m_keywords
	 */
	public String[] getKeywords() {
		return m_keywords;
	}

	/**
	 * @param m_keywords the m_keywords to set
	 */
	public void setKeywords(String[] m_keywords) {
		this.m_keywords = m_keywords;
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
