/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.htmltocsvtoneo4j.html2csv;

import br.edimarmanica.configuration.Html2Neo4j;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.htmltocsvtoneo4j.html2csv.CsvController;
import br.edimarmanica.htmltocsvtoneo4j.util.FormatTextNode;
import br.edimarmanica.htmltocsvtoneo4j.util.FormatUniquePath;
import br.edimarmanica.htmltocsvtoneo4j.util.InvalidTextNode;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXException;
import org.cyberneko.html.parsers.DOMParser;

/**
 *
 * load a webPage to the neo4j
 *
 * @author edimar
 */
public class HtmlToCSV {

    private String pageURL;
    private Map<Long, Long> relationships = new HashMap<>();
    private FormatUniquePath formatter;
    private CsvController csv;
    private long currentNodeID = 0;

    /**
     * 
     * @param pageURL
     * @param site
     * @param rootID ID para a raiz, de modo a comportar a inserção em diversas páginas
     * @param append false create a new file, true append in the current file
     */
    public HtmlToCSV(String pageURL, Site site, long rootID, boolean append) {
        this.pageURL = pageURL;

        formatter = new FormatUniquePath(pageURL); //só pode instanciar uma vez por página para funcionar corretamente
        csv = new CsvController(site, append);
        
        this.currentNodeID = rootID;
    }

    private void insertAllChildren(Element el, Long parentNodeID) {
        for (Iterator i = el.nodeIterator(); i.hasNext();) {
            Node child = (Node) i.next();

            if (child.getNodeType() == Node.ELEMENT_NODE) {

                /**
                 * blacklist *
                 */
                if (new HashSet<>(Arrays.asList(Html2Neo4j.BLACK_TAGS)).contains(child.getName().toLowerCase())) {
                    continue;
                }

                Long newNodeID;
                try {
                    newNodeID = insertNode(child, parentNodeID);
                    insertAllChildren((Element) child, newNodeID);
                } catch (InvalidTextNode ex) {
                    //só ignora o nodo
                    //  Logger.getLogger(HtmlToCSV.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (child.getNodeType() == Node.TEXT_NODE) {
                //      System.out.println("child: "+child.getPath()+"-"+child.getName()+""+"-"+child.getStringValue());
                try {
                    insertNode(child, parentNodeID);
                } catch (InvalidTextNode ex) {
                    //só ignora o nodo
                    //   Logger.getLogger(HtmlToCSV.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * 
     * @return the ID of the last inserted node
     */
    public Long insertAllNodes() {
        Element root = getDocument().getRootElement();
        Long rootNodeID;
        try {
            rootNodeID = insertNode(root, null);
            insertAllChildren(root, rootNodeID);
            csv.addRelationShips(relationships);
        } catch (InvalidTextNode ex) {
            //só ignora o nodo
            //Logger.getLogger(HtmlHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return currentNodeID-1;
    }

    /**
     *
     * @param node DOM node to be inserted
     * @param parentNodeID ID of the parent node (in the neo4j)
     * @return ID of the inserted node (in the neo4j)
     * @throws InvalidTextNode
     */
    private Long insertNode(Node node, Long parentNodeID) throws InvalidTextNode {
        Map<String, String> properties = new HashMap<>();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            properties.put("VALUE", node.getName());
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            properties.put("VALUE", FormatTextNode.format(node.getStringValue()));
        } else {
            return null;
        }

        String formattedUniquePath = formatter.format(node.getUniquePath(), node.getNodeType());

        properties.put("NODE_TYPE", node.getNodeType() + "");
        properties.put("PATH", node.getPath());
        properties.put("UNIQUE_PATH", formattedUniquePath);
        properties.put("URL", pageURL);
        properties.put("POSITION", FormatUniquePath.getNodePosition(formattedUniquePath) + "");


        long newNodeID = currentNodeID;
        currentNodeID++;
        csv.addNode(properties);

        if (parentNodeID != null) { //root não tem pai
            relationships.put(newNodeID, parentNodeID);
        }
        return newNodeID;
    }

    private Document getDocument() {
        DOMParser parser = new DOMParser();

        try {
            // parser.parse(new org.xml.sax.InputSource(new InputStreamReader(new FileInputStream(url), "ISO-8859-15")));
            parser.parse(pageURL);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(HtmlToCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
        org.w3c.dom.Document w3cDoc = parser.getDocument();
        DOMReader domReader = new DOMReader();
        return domReader.read(w3cDoc);
    }

    public static void main(String[] args) {
        HtmlToCSV html = new HtmlToCSV("/media/edimar/SAMSUNG/doutorado04/bases/SWDE/nbaplayer/nbaplayer-usatoday/0275.htm", br.edimarmanica.dataset.swde.nba.Site.USATODAY, 0, false);
        html.insertAllNodes();
    }
}
