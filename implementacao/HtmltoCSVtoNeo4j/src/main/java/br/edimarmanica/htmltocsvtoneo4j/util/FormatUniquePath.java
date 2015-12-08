/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.htmltocsvtoneo4j.util;

import java.util.HashMap;
import java.util.Map;
import org.dom4j.Node;

/**
 *
 * @author edimar
 */
public class FormatUniquePath {

    private String page;
    private Map<String, Integer> positions = new HashMap<>(); //Map<UniquePath,Occurrences>

    /**
     *
     * @param page só para lembrar o usuário de instânciar essa classe uma única
     * vez por página senão não funcionará corretamente devido ao cálculo da
     * posição de nodos texto em elementos compostos (com tags e nodos texto.
     * Ex: Ex: em "<p>One<b>Two</b>Three</p>")
     */
    public FormatUniquePath(String page) {
        this.page = page;
    }

    /**
     *
     * @param uniquePath (single tags do not have indexes. Ex:
     * /html/body/div[1])
     * @return the uniquePath with indexes in all tags. Ex:
     * /html[1]/body[1]/div[1]
     */
    public String format(String uniquePath, short nodeType) {
        String formatted = "";
        String[] partes = uniquePath.split("/");
        for (String st : partes) {
            if (st.isEmpty()) {
                continue;
            }

            if (!st.contains("[")) {
                st = st + "[1]";
            }
            formatted += "/" + st;
        }
        return updateTextualNodePosition(formatted, nodeType);
    }

    /**
     * tratamento especial para nodos texto em tags compostas (com tags e nodos
     * texto. Ex: em "<p>One<b>Two</b>Three</p>", o nodo "Three" é o segundo
     * text)
     *
     * @param formattedUniquePath
     * @param nodeType
     * @return
     */
    public String updateTextualNodePosition(String formattedUniquePath, short nodeType) {
        if (nodeType == Node.ELEMENT_NODE) {
            return formattedUniquePath;
        } else if (nodeType == Node.TEXT_NODE) {
            if (positions.containsKey(formattedUniquePath)) {//já teve uma ocorrência nessa página
                int currentPosition = positions.get(formattedUniquePath);
                positions.put(formattedUniquePath, currentPosition + 1);
                return formattedUniquePath.replaceAll("text\\(\\)\\[\\d+\\]", "text()[" + (currentPosition + 1) + "]");
            } else {
                positions.put(formattedUniquePath, 1);
                return formattedUniquePath;
            }
        }
        return null;
    }
    
     /**
     *
     * @param formattedUniquePath: uniquePath já formatado
     * @return a posição do último nodo em relação aos seus irmãos
     */
    public static int getNodePosition(String formattedUniquePath) {
        return new Integer(formattedUniquePath.substring(formattedUniquePath.lastIndexOf("[") + 1, formattedUniquePath.lastIndexOf("]")));
    }
}
