/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.weir;

import br.edimarmanica.metrics.RuleMetrics;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class RuleMetricsWeir extends RuleMetrics {

    public RuleMetricsWeir(Map<String, String> ruleValues, Map<String, String> groundTruth) {
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
            if (ruleValues.containsKey(pageId) && ruleValues.get(pageId).equals(groundTruth.get(pageId))) {
                intersection.add(pageId);
            }
        }
    }
}
