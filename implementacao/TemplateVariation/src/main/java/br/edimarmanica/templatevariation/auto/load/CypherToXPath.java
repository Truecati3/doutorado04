/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.load;

/**
 *
 * @author edimar
 */
public class CypherToXPath {
    
    public static String cypher2xpath(String cypher){
        String lines[] = cypher.split("\n");
        String xPath = lines[0].replaceAll("MATCH", "");

        //tratamento para o nodo template
        xPath = xPath.replaceAll("\\(a(\\d+):Template\\)", lines[1].replaceAll(".*PATH='", "").replaceAll("'.*", ""));

        //tratamento para o nodo value
        String nodeValue = xPath.replaceAll(".*\\(", "").replaceAll("\\)", "");
        xPath = xPath.replaceAll("\\(" + nodeValue + "\\)", "text()[" + lines[lines.length - 2].replaceAll(".*POSITION='", "").replaceAll("'.*", "") + "]");

        //tratamento para os nodos intermediários
        String fragmentos[] = xPath.replaceFirst(".*/text\\(\\)", "").replaceAll("text.*", "").split("\\)");

        for (int i = 2; i < lines.length - 2; i++) {
            String nodeTag = fragmentos[i - 2].replaceAll(".*\\(", "");
            String tag = lines[i].replaceAll(".*VALUE='", "").replaceAll("'.*", "");
            String position;
            if (nodeTag.equals("b")) {
                position = "";
            } else {
                position = lines[i].replaceAll(".*POSITION='", "").replaceAll("'.*", "");
            }
            xPath = xPath.replaceAll("\\(" + nodeTag + "\\)", tag + "[" + position + "]");
        }
        
        xPath = xPath.replaceAll("-->", ">").replaceAll("<--", "<");

        return xPath.trim();
    }
}
