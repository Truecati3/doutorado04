/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.swde;

import br.edimarmanica.configuration.General;
import br.edimarmanica.metrics.RuleMetrics;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class RuleMetricsSwde extends RuleMetrics {

    public RuleMetricsSwde(Map<String, String> ruleValues, Map<String, String> groundTruth) {
        super(ruleValues, groundTruth);
    }

    /**
     *
     * encontra o conjunto de paǵinas cujo valor extraído pela regra casa com o
     * valor do gabarito
     */
    @Override
    protected void computeIntersection() {
        for (String pageId : groundTruth.keySet()) {

            /**
             * Tratamento que no gabarito atributo pode ter multivalues. Nesse
             * caso se o valor extraído for um deles basta
             */
            String partes[] = groundTruth.get(pageId).split(General.SEPARADOR);
            int nrValues = Integer.parseInt(partes[0]);
            boolean match = false;
            for (int i = 0; i < nrValues; i++) {
                if (ruleValues.containsKey(pageId) && ruleValues.get(pageId).equals(partes[1 + i])) {
                    match = true;
                    break;
                }
            }
            if (match) {
                intersection.add(pageId);
            }
        }
    }

}
