/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics;

import static br.edimarmanica.dataset.Dataset.SWDE;
import static br.edimarmanica.dataset.Dataset.WEIR;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.swde.RuleMetricsSwde;
import br.edimarmanica.metrics.weir.RuleMetricsWeir;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author edimar
 */
public abstract class RuleMetrics {

    protected Map<String, String> ruleValues; //Map<URL, Value>
    protected Map<String, String> groundTruth; //Map<URL, Value>
    private double recall;
    private double precision;
    private double F1;
    private int relevantRetrieved;
    protected Set<String> intersection = new HashSet<>();

    public RuleMetrics(Map<String, String> ruleValues, Map<String, String> groundTruth) {
        this.ruleValues = ruleValues;
        this.groundTruth = groundTruth;
    }
    
     public static RuleMetrics getInstance(Site site, Map<String, String> ruleValues, Map<String, String> groundTruth) {
        switch (site.getDomain().getDataset()) {
            case SWDE:
                return new RuleMetricsSwde(ruleValues, groundTruth);
            case WEIR:
                return new RuleMetricsWeir(ruleValues, groundTruth);
            default:
                return null;
        }
    }

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

    protected abstract void computeIntersection();

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getF1() {
        return F1;
    }

    public void setF1(double F1) {
        this.F1 = F1;
    }

    public void setRelevantRetrieved(int relevantRetrieved) {
        this.relevantRetrieved = relevantRetrieved;
    }

    public int getRelevantRetrieved() {
        return relevantRetrieved;
    }

    public Set<String> getIntersection() {
        return intersection;
    }
}
