import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;


/**
 * Classe parsant le xml pour l'indexation
 */
public class XmlParser  implements ContentHandler {
	
	/**
	 * Index 1 colonne du document que l'on parse
	 * @var vectIndex
	 */
	private VectorIndex m_vectIndex ;

	/**
	 * Liste des balise pour arriver à là où on en est
	 * Par exemple, si on en est à /article[1]/body[1]/section[2]/c[1]/b[1]
	 * m_DOMPos = { {article, 1}, {body, 1}, {section, 2}, {c, 1}, {b,1} }
	 */
	private LinkedList<Tag> m_DOMPos ;
	
	/**
	 * Liste des éléments déjà utilisé (dans ce fichier ou un autre)
	 * Que l'on peut réetulisé
	 */
	private Hashtable<String, DOMElement> m_usedElement ;
	
	/**
	 * Elément courant
	 */
	private DOMElement m_currentDOMElement = null;
	
	/**
	 * Si on doit raciniser les mots
	 */
	private boolean m_stem = false ;

	/**
	 * Definition du locator qui permet a tout moment pendant l'analyse, de localiser
	 * le traitement dans le flux. Le locator par defaut indique, par exemple, le numero
	 * de ligne et le numero de caractere sur la ligne.
	 */
	private Locator locator;
	
	/**
	 * Documents de la collection
	 */
	private Hashtable<String, Document> m_docs ;
	
	/**
	 * Fichier contanant les poids de roberson
	 */
	String m_robersonFile = "";
	
	
	/**
	 * Profondeur max de parcours
	 */
	private int m_maxdepth = 4 ;
	
	/**
	 * Liste des balises interressante (pas de fichier .ini >< )
	 */
	private static final String[] relevantTags = { "article", "bdy",
		"p", "ss1", "ss2", "table", "list", "sec", "header", "title"
	};
	
	
	
	/**
	 * COnstructeur
	 * @param v VectorIndex à utiliser
	 * @param usedElement Liste des éléments déjà utilisés
	 * @param docs Documents de la collection
	 * @param stem Si il faut raciniser les mots.
	 */
	public XmlParser( VectorIndex v, Hashtable<String, DOMElement> usedElement, Hashtable<String, Document> docs ,boolean stem, String RobersonFile, int maxdepth ) {
		super();
		m_vectIndex = v ;
		m_usedElement = usedElement ;
		m_stem = stem ;
		m_docs = docs ;
		m_robersonFile = RobersonFile ;
		m_maxdepth = maxdepth ;
		
		m_DOMPos = new LinkedList<Tag>() ;
		m_currentDOMElement = null ;
		
		locator = new LocatorImpl();
	}
	

	/**
	 * Evenement recu a chaque fois que l'analyseur rencontre une balise XML ouvrante.
	 * @param nameSpaceURI l'URL de l'espace de nommage.
	 * @param localName le nom local de la balise.
	 * @param rawName nom de la balise en version 1.0 <code>nameSpaceURI + ":" + localName</code>
	 * @throws SAXException si la balise ne correspond pas a ce qui est attendu,
	 * comme non-respect d'une DTD.
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs) throws SAXException {

		// on ignore le contenu de la balise <math> (c'est du LaTeX)
		if( !m_DOMPos.isEmpty() && m_DOMPos.getLast().name == "math")
			return;
		
		
		// Gestion des liens
		if( rawName == "link")
			handlelinks( attributs ) ;
		
		// Création d'un nouveau tag
		// Utiliser Tag.children pour bien choisir le Tag.num
		int num = 1 ;
		if( !m_DOMPos.isEmpty() )
			num = m_DOMPos.getLast().searchInChildren(rawName) +1 ;
		
		Tag tag = new Tag(rawName, num) ;
		
		// ajout du nouveau tag dans m_DOMPos et dans le Tag.childre du précédent
		if( !m_DOMPos.isEmpty() )
			m_DOMPos.getLast().children.add(tag) ;
		m_DOMPos.addLast(tag);
		
		
		// Si la balise ne fait pas parti des balise pertinente, On ne met pas à jour m_currentDOMElement (on garde l'ancienne)
		if( Arrays.asList(relevantTags).contains(rawName) && m_DOMPos.size() < m_maxdepth ) {
		
			// Recherche dans la hashtable si l'arboréscence à déjà été rencontré
			String tagpath = DOMElement.tags2path( m_DOMPos ) ;
			
			if( m_usedElement.contains(tagpath) )	 // Si c'est le cas récup dans m_currentDOMElement
				m_currentDOMElement = m_usedElement.get(tagpath) ;
			else { 									// SInon création et ajout dans la hashtable
				m_currentDOMElement = new DOMElement(tagpath);
				m_usedElement.put(tagpath, m_currentDOMElement) ;
			}	
		}
		
		// Ex : Seul balise pertinente section
		
		// On en est à /article[1]/body[1]/section[2]/c[1]/b[1]
		// m_DOMPos = { {article, 1}, {body, 1}, {section, 2}, {c, 1}, {b,1} }
		// m_currentDOMElement = DOMElement( "/article[1]/body[1]/section[2]" )
		
		// On arrive ensuite à /article[1]/body[1]/section[2]/c[1]/b[1]/section[1]/foo[3]
		// m_DOMPos = { {article, 1}, {body, 1}, {section, 2}, {c, 1}, {b,1}, {section, 1}, {foo, 3} }
		// m_currentDOMElement = DOMElement( "/article[1]/body[1]/section[2]/b[1]/section[1]" )
		
		
		
	}

	/**
	 * Evenement recu a chaque fermeture de balise.
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
		// on ignore le contenu de la balise <math> (c'est du LaTeX)
		if( m_DOMPos.getLast().name == "math" && rawName != "math" )
			return;
		
		// retrait du dernier tag dans m_DOMPos
		Tag tag = m_DOMPos.getLast() ;
		m_DOMPos.removeLast() ;
		tag = null ;
		
		// Si la balise qui se ferme n'était pas pertinente, on garde m_currentDOMElement
		if (Arrays.asList(relevantTags).contains( rawName ) && m_DOMPos.size() < m_maxdepth ) {
		
			// récupération de l'indice de la dernière balise pertinente
			ListIterator<Tag> ite = m_DOMPos.listIterator(m_DOMPos.size()) ;
			int i = 0 ;
			boolean foundRelevant = false ;
			while( ite.hasPrevious() ) {
				Tag t = ite.previous() ;
				
				if( foundRelevant )
					i++ ;
				else if( Arrays.asList(relevantTags).contains( t.name ) )
					foundRelevant = true ;
			}
			
			
			String tagpath = DOMElement.tags2path( m_DOMPos, i ) ;
			
			// Recherche dans la hashtable si l'arboréscence à déjà été rencontré (normallement oui, à l'ouverture)
			if( m_usedElement.contains(tagpath) )	 // Si c'est le cas récup dans m_currentDOMElement
				m_currentDOMElement = m_usedElement.get(tagpath) ;
			else { 									// SInon création et ajout dans la hashtable
				m_currentDOMElement = new DOMElement(tagpath);
				m_usedElement.put(tagpath, m_currentDOMElement) ;
			}	
		}
		
	}
	
	
	
	
	
	/**
	 * Evenement recu a chaque fois que l'analyseur rencontre des caracteres (entre
	 * deux balises).
	 * @param ch les caracteres proprement dits.
	 * @param start le rang du premier caractere a traiter effectivement.
	 * @param end le rang du dernier caractere a traiter effectivement
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int end) throws SAXException {
		
		//System.out.println("#PCDATA <0 : " + new String(ch, start, end));
		if( m_DOMPos.getLast().name == "math")
			return;
		
		
		// Conversion en String
		String content = new String(ch, start, end) ;
		
		// Séparation en mot (Attention aux \n \t \r ... )
		String[] words = content.split("(\\p{Blank}|\\p{Punct})+");
		
		// Ajout de la taille (pour le calcul de df)
		m_vectIndex.getDocument().addLength(  words.length ) ;
		
		// Ajout des mots dans l'index
		for( int i=0; i < words.length; i++)
			m_vectIndex.addRobersonWord(words[i], m_currentDOMElement, m_stem, m_DOMPos.getLast() ) ;
	}
	
	
	
	
	/**
	 * Gère l'apparition de lien dans la collection
	 * @param attributs Attributs de la balise links
	 */
	private void handlelinks(  Attributes attributs ) {
		
		if( m_docs != null ) {
			// on parcourt la liste des attributs
			for (int i = 0; i < attributs.getLength(); i++) { 
				
				if( attributs.getLocalName(i).contains("href") ) {
					
					String name =  attributs.getValue(i) ;
					int lastSlash = name.lastIndexOf("/") ;
					if( lastSlash != -1 ) 
						name =  attributs.getValue(i).substring( lastSlash +1);
					if( m_docs.containsKey(name) ) {
						m_docs.get( name ).addLinks() ;
					}
					break ;
				}
			}
		}
	}
	
	
	
	
	
	/**
	 * Recu chaque fois que des caracteres d'espacement peuvent etre ignores au sens de
	 * XML. C’est-a-dire que cet evenement est envoye pour plusieurs espaces se succedant,
	 * les tabulations, et les retours chariot se succedant ainsi que toute combinaison de ces
	 * trois types d'occurrence.
	 * Rien à faire !
	 * @param ch les caracteres proprement dits.
	 * @param start le rang du premier caractere a traiter effectivement.
	 * @param end le rang du dernier caractere a traiter effectivement
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int end) throws SAXException {}

	/**
	 * Rencontre une instruction de fonctionnement.
	 * Normalement, il n'y en a pas dans la collection
	 * @param target la cible de l'instruction de fonctionnement.
	 * @param data les valeurs associees a cette cible. En general, elle se presente sous la forme 
	 * d'une serie de paires nom/valeur.
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
	 */
	public void processingInstruction(String target, String data) throws SAXException {}

	/**
	 * Recu a chaque fois qu'une balise est evitee dans le traitement a cause d'un
	 * probleme non bloque par le parser.
	 * On s'en fou, il fallait faire du xml valide.
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String arg0) throws SAXException {}
	


	/**
	 * Definition du locator qui permet a tout moment pendant l'analyse, de localiser
	 * le traitement dans le flux. Le locator par defaut indique, par exemple, le numero
	 * de ligne et le numero de caractere sur la ligne.
	 * @author smeric
	 * @param value le locator a utiliser.
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator value) {
		locator =  value;
	}
	
	
	
	/**
	 * Evenement envoye au demarrage du parse du flux xml.
	 * Rien à faire ?
	 * @throws SAXException en cas de probleme quelconque ne permettant pas de
	 * se lancer dans l'analyse du document.
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {}

	/**
	 * Evenement envoye a la fin de l'analyse du flux XML.
	 * Rien à faire ?
	 * @throws SAXException en cas de probleme quelconque ne permettant pas de
	 * considerer l'analyse du document comme etant complete.
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {}
	
	
	/**
	 * Debut de traitement dans un espace de nommage.
	 * Rien à faire ?
	 * @param prefixe utilise pour cet espace de nommage dans cette partie de l'arborescence.
	 * @param URI de l'espace de nommage.
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
	 */
	public void startPrefixMapping(String prefix, String URI) throws SAXException {}

	/**
	 * Fin de traitement de l'espace de nommage.
	 * Rien à faire ?
	 * @param prefixe le prefixe choisi a l'ouverture du traitement de l'espace nommage.
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException {}
}
