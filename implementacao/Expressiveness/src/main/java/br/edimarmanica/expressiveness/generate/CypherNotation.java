/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness.generate;

/**
 *
 * @author edimar
 */
public class CypherNotation {
     
    public static String getNotation(String label, String uniquePathLabel, String uniquePathValue) {
        String[] partesLabel = uniquePathLabel.split("/");
        String[] partesValue = uniquePathValue.split("/");

        int i;
        for (i = 1; i < partesLabel.length && i < partesValue.length; i++) {
            if (!partesLabel[i].equals(partesValue[i])) {
                break;
            }
        }

        int nrElementsVolta = partesLabel.length - i;
        int nrElementsVai = partesValue.length - i;
        String cypher = "MATCH ";
        for (int j = nrElementsVolta - 1; j >= 0; j--) {
            cypher += "(a" + j + ")<--";
        }
        cypher += "(b)";
        for (int j = 0; j < nrElementsVai; j++) {
            cypher += "-->(c" + j + ")";
        }
        cypher += "\nWHERE a" + (nrElementsVolta - 1) + ".VALUE='" + label + "' AND a" + (nrElementsVolta - 1) + ".PATH='" + uniquePathLabel.replaceAll("\\[\\d+\\]", "") + "' ";
        for (int j = nrElementsVolta - 2; j >= 0; j--) {
            cypher += "\nAND a" + j + ".VALUE='" + partesLabel[i + j].replaceAll("\\[.*", "") + "' AND a" + j + ".POSITION='" + partesLabel[i + j].replaceAll(".*\\[", "").replaceAll("]", "") + "' ";
        }
        cypher += "\nAND b.VALUE='" + partesLabel[i - 1].replaceAll("\\[.*", "") + "' "; // o B não deve ter posição senão perde em generalização AND b.POSITION='" + partesLabel[i - 1].replaceAll(".*\\[", "").replaceAll("]", "") + "' ";

        for (int j = 0; j < nrElementsVai - 1; j++) {
            cypher += "\nAND c" + j + ".VALUE='" + partesValue[i + j].replaceAll("\\[.*", "") + "' AND c" + j + ".POSITION='" + partesValue[i + j].replaceAll(".*\\[", "").replaceAll("]", "") + "' ";
        }
        cypher += "\nAND c" + (nrElementsVai - 1) + ".NODE_TYPE='3'";
        cypher += "\n RETURN c" + (nrElementsVai - 1) + ".VALUE AS VALUE, c" + (nrElementsVai - 1) + ".URL AS URL";
        return cypher;
    }
}
