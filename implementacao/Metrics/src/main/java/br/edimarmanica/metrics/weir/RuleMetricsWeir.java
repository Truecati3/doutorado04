/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.weir;

import br.edimarmanica.metrics.RuleMetrics;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class RuleMetricsWeir extends RuleMetrics {

    private Set<String> ruleValues;
    private Set<String> groundtruth;

    public RuleMetricsWeir(Set<String> ruleValues, Set<String> groundtruth) {
        this.ruleValues = ruleValues;
        this.groundtruth = groundtruth;
    }

    @Override
    public void computeMetrics() {

        Set<String> intersection = new HashSet<>();
        intersection.addAll(ruleValues);
        intersection.retainAll(groundtruth);

        setRelevantRetrieved(intersection.size());
        setRecall((double) intersection.size() / groundtruth.size());
        setPrecision((double) intersection.size() / ruleValues.size());

        if (getRecall() == 0 || getPrecision() == 0) {
            setRecall(0);
            setPrecision(0);
            setF1(0);
        } else {
            setF1((2 * (getRecall() * getPrecision())) / (getRecall() + getPrecision()));
        }
    }
}
