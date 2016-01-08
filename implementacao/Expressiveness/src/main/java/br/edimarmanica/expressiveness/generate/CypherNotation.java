/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness.generate;

import br.edimarmanica.expressiveness.generate.beans.CypherRule;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class CypherNotation {

    private String label;
    private String uniquePathLabel;
    private String uniquePathValue;
    private Map<String, Object> params = new HashMap<>();

    public CypherNotation(String label, String uniquePathLabel, String uniquePathValue) {
        this.label = label;
        this.uniquePathLabel = uniquePathLabel;
        this.uniquePathValue = uniquePathValue;
    }

    public CypherRule getNotation() {

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
            if (j == nrElementsVolta - 1) {
                cypher += "(a" + j + ":Template)<--";//adicionando o label, a consulta fica muito mais rápida
            } else {
                cypher += "(a" + j + ")<--";
            }
        }
        cypher += "(b)";
        for (int j = 0; j < nrElementsVai; j++) {
            cypher += "-->(c" + j + ")";
        }
        cypher += "\nWHERE a" + (nrElementsVolta - 1) + ".VALUE=" + add(label) + " AND a" + (nrElementsVolta - 1) + ".PATH=" + add(uniquePathLabel.replaceAll("\\[\\d+\\]", "")) + " AND a" + (nrElementsVolta - 1) + ".POSITION=" + add(partesLabel[i + nrElementsVolta - 1].replaceAll(".*\\[", "").replaceAll("]", "")) + " ";
        for (int j = nrElementsVolta - 2; j >= 0; j--) {
            cypher += "\nAND a" + j + ".VALUE=" + add(partesLabel[i + j].replaceAll("\\[.*", "")) + " AND a" + j + ".POSITION=" + add(partesLabel[i + j].replaceAll(".*\\[", "").replaceAll("]", "")) + " ";
        }
        cypher += "\nAND b.VALUE=" + add(partesLabel[i - 1].replaceAll("\\[.*", "")) + " "; // o B não deve ter posição senão perde em generalização AND b.POSITION='" + partesLabel[i - 1].replaceAll(".*\\[", "").replaceAll("]", "") + "' ";

        int j = 0;
        for (j = 0; j < nrElementsVai - 1; j++) {
            cypher += "\nAND c" + j + ".VALUE=" + add(partesValue[i + j].replaceAll("\\[.*", "")) + " AND c" + j + ".POSITION=" + add(partesValue[i + j].replaceAll(".*\\[", "").replaceAll("]", "")) + " ";
        }
        cypher += "\nAND c" + (nrElementsVai - 1) + ".NODE_TYPE=" + add("3") + " AND c" + (nrElementsVai - 1) + ".POSITION=" + add(partesValue[i + j].replaceAll(".*\\[", "").replaceAll("]", "")) + " ";
        cypher += "\n RETURN c" + (nrElementsVai - 1) + ".VALUE AS VALUE, c" + (nrElementsVai - 1) + ".URL AS URL, 'Template' in LABELS(c" + (nrElementsVai - 1) + ") as template";

        return new CypherRule(cypher, params, label);
    }

    private String add(String param) {
        String id = "value" + params.size();

        params.put(id, param);

        return "{" + id + "}";
    }
}
