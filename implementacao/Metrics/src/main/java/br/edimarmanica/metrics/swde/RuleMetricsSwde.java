/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.swde;

import br.edimarmanica.configuration.General;
import br.edimarmanica.metrics.RuleMetrics;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class RuleMetricsSwde extends RuleMetrics {

    private Map<Integer, String> ruleValues; //Map<URL, Value>
    private Map<Integer, String> groundTruth; //Map<URL, Value>
    private Set<Integer> intersection = new HashSet<>();

    public RuleMetricsSwde(Map<Integer, String> ruleValues, Map<Integer, String> groundTruth) {
        this.ruleValues = ruleValues;
        this.groundTruth = groundTruth;
    }

    @Override
    public void computeMetrics() {
        computeIntersection();

        setRelevantRetrieved(intersection.size());
        setRecall((double) intersection.size() / groundTruth.size());
        setPrecision((double) intersection.size() / ruleValues.size());

        if (getRecall() == 0 || getPrecision() == 0) {
            setRecall(0);
            setPrecision(0);
            setF1(0);
        } else {
            setF1((2 * (getRecall() * getPrecision())) / (getRecall() + getPrecision()));
        }
    }

    /**
     *
     * @param groundtruth o gabarito para um atributo X
     * @param ruleValues os valores extraídos por uma regra Y
     * @return o conjunto de paǵinas cujo valor extraído pela regra casa com o
     * valor do gabarito
     */
    private void computeIntersection() {
        for (Integer pageId : groundTruth.keySet()) {

            /**
             * Tratamento que no gabarito atributo pode ter multivalues. Nesse
             * caso se o valor extraído for um deles é ok
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

    public Set<Integer> getIntersection() {
        return intersection;
    }
}
