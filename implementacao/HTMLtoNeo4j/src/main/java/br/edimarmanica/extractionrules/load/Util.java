/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.load;

/**
 *
 * @author edimar
 */
public class Util {

    /**
     *
     * @param formattedUniquePath: uniquePath já formatado
     * @return a posição do último nodo em relação aos seus irmãos
     */
    public static int getNodePosition(String formattedUniquePath) {
        return new Integer(formattedUniquePath.substring(formattedUniquePath.lastIndexOf("[") + 1, formattedUniquePath.lastIndexOf("]")));
    }

    /**
     * Cria o caminho relativo no neo4j
     *
     * @param formattedUniquePathValue uniquePath formatado (com índice em todos
     * os nodos) do valor
     * @param formattedUniquePathTemplate uniquePath formatado (com índice em
     * todos os nodos) do template
     * @param templateText texto do template
     * @param templatePath path sem índice do template
     * @return
     */
    public static String getCypherRelativePath(String formattedUniquePathValue, String formattedUniquePathTemplate, String templateText, String templatePath) {
        String[] partesTemplate = formattedUniquePathTemplate.split("/");
        String[] partesValue = formattedUniquePathValue.split("/");

        int i;
        for (i = 1; i < partesTemplate.length && i < partesValue.length; i++) {
            if (!partesTemplate[i].equals(partesValue[i])) {
                break;
            }
        }

        int nrElementsVolta = partesTemplate.length - i;
        int nrElementsVai = partesValue.length - i;
        String cypher = "MATCH ";
        for (int j = nrElementsVolta - 1; j >= 0; j--) {
            cypher += "(a" + j + ")<-[*]-";
        }
        cypher += "(b)";
        for (int j = 0; j < nrElementsVai; j++) {
            cypher += "-[*]->(c" + j + ")";
        }
        cypher += "\nWHERE a" + (nrElementsVolta - 1) + ".VALUE='" + templateText + "' AND a" + (nrElementsVolta - 1) + ".PATH='" + templatePath + "' ";
        for (int j = nrElementsVolta - 2; j >= 0; j--) {
            cypher += "\nAND a" + j + ".VALUE='" + partesTemplate[i + j].replaceAll("\\[.*", "") + "' AND a" + j + ".POSITION='" + partesTemplate[i + j].replaceAll(".*\\[", "").replaceAll("]", "") + "' ";
        }
        cypher += "\nAND b.VALUE='" + partesTemplate[i - 1].replaceAll("\\[.*", "") + "' AND b.POSITION='" + partesTemplate[i - 1].replaceAll(".*\\[", "").replaceAll("]", "") + "' ";

        for (int j = 0; j < nrElementsVai - 1; j++) {
            cypher += "\nAND c" + j + ".VALUE='" + partesValue[i + j].replaceAll("\\[.*", "") + "' AND c" + j + ".POSITION='" + partesValue[i + j].replaceAll(".*\\[", "").replaceAll("]", "") + "' ";
        }
        cypher += "\nAND c" + (nrElementsVai - 1) + ".NODE_TYPE='3'";
        cypher += "\n RETURN c" + (nrElementsVai - 1) + ".VALUE";
        return cypher;
    }
}
