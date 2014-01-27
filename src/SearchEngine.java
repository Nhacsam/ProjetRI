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
	static private String[] m_autorizedMethods = { "ltn", "ltc", "stn", "lfn", "bm25" } ;
	
	/**
	 * Nombre maximum de résultats
	 * @var m_nbResultsMax
	 */
	private int m_nbResultsMax = 1500 ;
	
	/**
	 * Définit si on doit renvoyer des balises xml précises dans les résultats
	 * @var m_xml
	 */
	private boolean m_xml = false ;
	
	
	private boolean m_links  = false ;
	
	private String m_linksMethod = "n" ;
	
	
	/**
	 * Paramètre de bm25
	 * @var bm25k1
	 * @var bm25b
	 */
	private double m_bm25k1 = 0.;
	private double m_bm25b = 0.;
	
	
	
	public SearchEngine( Index index, Hashtable< String, String> params) {
		m_index = index ;
		
		if ( params.containsKey("xml") && params.get("xml") != "false" )
			m_xml = true ;
		
		if( params.containsKey("method") && Arrays.asList(m_autorizedMethods).contains(params.get("method")) )
			m_method = params.get("method") ;
		
		if( params.containsKey("nbresult") ) {
			m_nbResultsMax =  Integer.parseInt(params.get("nbresult")) ;
		}
		
		if ( params.containsKey("links") && params.get("links") != "false" )
			m_links = true ;
		
		if( params.containsKey("linksMethod") )
			m_linksMethod =  params.get("linksMethod") ;
		
		
		if( params.containsKey("bm25k") ) {
			m_bm25k1 =  Double.parseDouble(params.get("bm25k")) ;
		}
		
		if( params.containsKey("bm25b") ) {
			m_bm25b =  Double.parseDouble(params.get("bm25b")) ;
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
				if( currentOcc[i].getDocument().getId() < min || min < 0) {
					min = currentOcc[i].getDocument().getId() ;
					clearArr( hasMin );
					hasMin[i] = true ;
				} else if ( currentOcc[i].getDocument().getId() == min ) {
					hasMin[i] = true ;
				}
				
			}
			
			// Résultat qui va contenir les infos sur le document courant (d'id = min)
			Result r = null ;
			// liste des occurences considérés
			LinkedList<Occurence> OccList = new LinkedList<Occurence>();
			
			// Liste des poids tels que ajouté
			LinkedList< Double > WeightList = new LinkedList< Double>();
			
			// Docment courant
			Document currentDoc  = null ;
			
			// Pour chaque mot clef
			for ( int i=0; i< n; i++) {
				
				// Si la liste en est plus loins que le document considéré
				// On s'en occupe pas tout de suite.
				if( !hasMin[i] )
					continue;
				
				// On note qu'on s'en est servi pour passé au suivant au prochain tour
				usedLastTime[i] = true ;
				OccList.addFirst( currentOcc[i] );
				
				// Si r = null, on l'inicialise
				if( r == null ) {
					currentDoc = currentOcc[i].getDocument() ;
					r = new Result( currentDoc.getPath() );
					r.setWeight(0);
					r.setWeightToConsider(true);
				}
				
				// Calcul du poid pour le document et ajout 
				double w = computeWeight(currentOcc[i],  df[i]) ;
				int[] keyW = q.getKeywordsWeight() ;
				
				WeightList.addFirst(w * keyW[i]);
				r.addWeight(w * keyW[i]);
			}
			
			r.setWeight( linksWeight( normalizeWeight( r.getWeight(), WeightList ), currentDoc ) );
			
			if( m_xml )
				r.setTag( getMostRelevantTag(OccList) );
			
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
	 * Calcule quelle est la balise la plus pertinente 
	 * pour le document correspondant aux occurences
	 * @param occs Liste d'occurences correspondant aux différents mots clef de la requête et au même document
	 * @return Chaine formatée correspondant à la balise
	 */
	private String getMostRelevantTag( LinkedList<Occurence> occs ) {
		
		// Liste de toutes les balises utiles
		LinkedList<String> tags = new LinkedList<String>() ;
		
		int minlength = 0 ;
		
		ListIterator<Occurence> ite1 = occs.listIterator() ;
		while( ite1.hasNext() ) {
			ListIterator<DOMElement> ite2 = ite1.next().getTags().listIterator();
			while( ite2.hasNext() ) {
				DOMElement d = ite2.next() ;
				if (d != null) {
					String p = d.getDOMpath() ;
					tags.add(p) ;
					
					if( minlength == 0 || p.length() < minlength )
						minlength = p.length() ;
				}
			}
		}
		
		ListIterator<String> ite ;
		String s ;
		String out = "" ;
		boolean stop = false ;
		
		// Comparaison lettre par lettre
		for( int i = 0; i < minlength; i++ ) {
			ite = tags.listIterator() ;
			char c = '\n' ;
			while( ite.hasNext() ) {
				s = ite.next() ;
				if( c == '\n' )
					c = s.charAt(i) ;
				else if (c != s.charAt(i) ) {
					stop = true ;
					break ;
				}	
			}
			if( stop )
				break ;
			out += c ;
		}
		
		if( out == "" ) {
			out = "/article[1]" ;
		}
		
		if( out.charAt(out.length() -1 ) != ']' )
			out = out.substring(0, out.lastIndexOf("/") ) ;
		
		if( out == "" ) {
			out = "/article[1]" ;
		}
		
		return out ;
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
			case "ltc" : 
				return ltWeight(o, df) ;
			
			case "stn" :
				return stWeight(o, df) ;
			
			case "lfn" :
				return lfWeight(o, df) ;
			
			case "bm25" :
				return bm25Weight(o, df) ;
		
			default :
				throw new InvalidParameterException("method");
		}
	}
	
	/**
	 * Normalise le calcul du poid
	 * @param w	Somme des poids précédement calculé
	 * @param wList Liste des poids qui ont permit de calculer la somme
	 * @return Poid Calculé
	 */
	public double  normalizeWeight( double w, LinkedList< Double >  wList ) {
		
		switch (m_method) {
		
		case "ltc" :
			return cNorm(w, wList) ;
	
		default :
			return w ;
	}
	}
	
	/**
	 * Calcul du poid d'un terme dans le document - Methode ltn ou ltc
	 * @param o	Occurence du terme
	 * @param df Document frequency du terme
	 * @return Poid Calculé
	 */
	public double ltWeight( Occurence o, int df ) {
		double w = 1 + Math.log( o.getTf() ) ;
		w *= Math.log(m_index.getNbDoc() / df );
		return w ;
	}
	
	/**
	 * Normalisation du poid d'un terme dans le document - Methode ltc
	 * @param w	Somme des poids précédement calculé
	 * @param wList Liste des poids qui ont permit de calculer la somme
	 * @return Poid Calculé
	 */
	public double cNorm( double w, LinkedList< Double >  wList ) {
		
		if( w == 0 )
			return 0. ;
		
		double squareSum = 0. ;
		
		ListIterator<Double> ite =  wList.listIterator(0);
		while ( ite.hasNext() ) {
			double wi = ite.next();
			squareSum += wi*wi ;
		}
		
		return w/Math.sqrt(squareSum) ;
	}
	
	
	/**
	 * Calcul du poid d'un terme dans le document - Methode stn
	 * @param o	Occurence du terme
	 * @param df Document frequency du terme
	 * @return Poid Calculé
	 */
	public double stWeight( Occurence o, int df ) {
		double w = o.getTf() * o.getTf() ;
		w *= Math.log(m_index.getNbDoc() / df );
		return w ;
	}
	
	/**
	 * Calcul du poid d'un terme dans le document - Methode lfn
	 * @param o	Occurence du terme
	 * @param df Document frequency du terme
	 * @return Poid Calculé
	 */
	public double lfWeight( Occurence o, int df ) {
		double w = 1 + Math.log( o.getTf() ) ;
		w /=  df ;
		return w ;
	}
	
	/**
	 * Calcul du poid d'un terme dans le document - Methode bm25
	 * @param o	Occurence du terme
	 * @param df Document frequency du terme
	 * @return Poid Calculé
	 */
	public double bm25Weight( Occurence o, int df ) {
		
		int Ld = o.getDocument().getLength() ;
		double Lave = m_index.getLave() ;
		
		double w = Math.log( m_index.getNbDoc() / df ) ;
		w *=  o.getTf()*( m_bm25k1 + 1) ;
		w /= o.getTf() + m_bm25k1*(1- m_bm25b+m_bm25k1*( Ld /Lave ) );
		return w ;
	}
	
	
	
	public double linksWeight( double oldw, Document d ) {
		
		if( m_links && d != null ) {
			
			double w = oldw ;
			
			switch( m_linksMethod ) {
				
				case "log" :
					w = w* Math.log( 1+ d.getLinks() ) ;
					break ;
				
				default : 
				case "n" :
					w = w * d.getLinks() ;
					break ;	
			}
			
			return w;
		} else
			return oldw ;
	}
	
	
	
	/**
	 * Regarde si un des itérateurs du tableau l n'est pas finit
	 * @param l Tableau des itérateurs
	 * @return true Si un des itérateur n'est pas finit
	 */
	private boolean OneHasNext( ListIterator<?>[] l ) {
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
	
	
	/**
	 * Calcule la racinisation d'un mot
	 * @param s Mot à raciniser
	 * @return Mot Racinisé
	 */
	static String racinisation( String s) {
		
		Stemmer stem = new Stemmer();
		stem.add(s.toCharArray(), s.length() );
		stem.stem();
		return stem.toString();
		
		/*
		Acienne version fait main. Incomplète !
		String r =  s ;
		
		if ( r.endsWith( "sses" ) )
			r = r.substring(0, r.length() -2 );
		else if ( r.endsWith( "ies" ) )
			r = r.substring(0, r.length() -2 );
		else if ( r.endsWith( "s" ) && !r.endsWith( "ss" ) )
			r = r.substring(0, r.length() -1 );
		else if ( r.matches("^(.+)?[aeiou].+eed$") )
			r = r.substring(0, r.length() -1 );
		else if ( r.matches("^(.+)?[aeiou].+ed$") )
			r = r.substring(0, r.length() -2 );
		else if ( r.matches("^(.+)?[aeiou].+ing$") )
			r = r.substring(0, r.length() -3 );
		else if ( r.matches("^(.+)?[aeiou].+y$"))
			r = r.substring(0, r.length() -1 ) + 'i';
		
		if( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+)+ational$") )
			r = r.substring(0, r.length() -5) + "ate";
		else if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+)+tional$") )
			r = r.substring(0, r.length() -2) ;
		else if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+)+enci$") )
			r = r.substring(0, r.length() -1) + "e";
		else if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+)+anci$") )
			r = r.substring(0, r.length() -1) + "e";
		
		if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+)+icate$") )
			r = r.substring(0, r.length() -3) ;
		else if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+)+ative$") )
			r = r.substring(0, r.length() -5) ;
		else if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+)+alize$") )
			r = r.substring(0, r.length() -3) ;
		else if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+)+iciti$") )
			r = r.substring(0, r.length() -3) ;
		
		if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+){2,}al$") )
			r = r.substring(0, r.length() -2) ;
		else if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+){2,}ance$") )
			r = r.substring(0, r.length() -4) ;
		else if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+){2,}ence$") )
			r = r.substring(0, r.length() -4) ;
		else if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+){2,}er$") )
			r = r.substring(0, r.length() -2) ;
		
		if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+){2,}$") )
			r = r.substring(0, r.length() -1) ;
		else if ( r.matches("^([^aeiou]+)?([aeiou]+[^aeiou]+)+([aeiou]+[^aeiouwxy]+)[^aeiou].\\1$") )
			r = r.substring(0, r.length() -1) ;
		
		return r ;*/
	}
	
	
	
}
