/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics;

/**
 *
 * @author edimar
 */
public abstract class RuleMetrics {

    private double recall;
    private double precision;
    private double F1;
    private int relevantRetrieved;

    public abstract void computeMetrics();

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
}
