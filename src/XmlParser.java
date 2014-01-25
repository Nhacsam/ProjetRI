import java.util.Hashtable;
import java.util.LinkedList;
import org.xml.sax.*;
import org.xml.sax.helpers.LocatorImpl;

import org.xml.sax.ContentHandler;


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
	 * COnstructeur
	 * @param v VectorIndex à utiliser
	 * @param usedElement Liste des éléments déjà utilisés
	 * @param stem Si il faut raciniser les mots.
	 */
	public XmlParser( VectorIndex v, Hashtable<String, DOMElement> usedElement, boolean stem ) {
		super();
		m_vectIndex = v ;
		m_usedElement = usedElement ;
		m_stem = stem ;
		
		m_DOMPos = new LinkedList<Tag>() ;
		m_DOMPos.add( new Tag("article", 1) ) ;
		
		// Vérification de l'existance de /article[1] dans la hashtable et création ou récupération
		if( m_usedElement.contains("/article[1]") )
			m_currentDOMElement = m_usedElement.get("/article[1]") ;
		else {
			m_currentDOMElement = new DOMElement("/article[1]");
			m_usedElement.put("/article[1]", m_currentDOMElement) ;
		}
		
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
		
		// Code d'exemple
		System.out.println("Ouverture de la balise : " + localName);
		System.out.println("Ouverture de la balise : " + rawName);
		if ( ! "".equals(nameSpaceURI)) { // espace de nommage particulier
			System.out.println("  appartenant a l'espace de nom : "  + nameSpaceURI);
		}

		System.out.println("  Attributs de la balise : ");

		for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
			System.out.println(" - " +  attributs.getLocalName(index) + " = " + attributs.getValue(index));
		}
		
		
		
		
		
		
		// Création d'un nouveau tag
		// Utiliser Tag.children pour bien choisir le Tag.num
		
		// ajout du nouveau tag dans m_DOMPos et dans le Tag.childre du précédent
		
		
		
		// Recherche dans la hashtable si l'arboréscence à déjà été rencontré
		
			// Si c'est le cas récup dans m_currentDOMElement
		
			// SInon création et ajout dans la hashtable
		// voir le constructeur. On peut sauter cette etape si la balise n'est pas pertinente (voir plus bas)
		
		// AU pasage, je pense que tu peux ignorer le contenu de la balise <math> (c'est du LaTeX)
		
		// Pour ne retenir que les balises pertinentes
		// tu peux t'arreter à la dernière balise pertinente quand tu cherche dans la hashtable
		// Tu dois par contre tout garder dans m_DOMPos
		
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
		// Code d'exemple
		System.out.print("Fermeture de la balise : " + localName);

		if ( ! "".equals(nameSpaceURI)) { // name space non null
			System.out.print("appartenant a l'espace de nommage : " + localName);
		}

		System.out.println();
		
		
		// retrait du dernier tag dans m_DOMPos
		
		
		
		// Recherche dans la hashtable si l'arboréscence à déjà été rencontré (normallement oui, à l'ouverture)
		
			// Si c'est le cas récup dans m_currentDOMElement
		
			// SInon création et ajout dans la hashtable
		// voir le constructeur. On peut sauter cette etape si la balise qui se ferme n'était pas pertinente
				
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
		
		if( end - start > 0 )
			System.out.println("#PCDATA >0 : " + new String(ch, start, end));
		if( end - start == 0 )
			System.out.println("#PCDATA =0 : " + new String(ch, start, end));
		if( end - start < 0 )
			System.out.println("#PCDATA <0 : " + new String(ch, start, end));
		
		// Conversion en String (voir exemple)
		
		// Séparation en mot (Attention aux \n \t \r ... )
		
		// Ajout des mots dans le vectorIndex
		
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
