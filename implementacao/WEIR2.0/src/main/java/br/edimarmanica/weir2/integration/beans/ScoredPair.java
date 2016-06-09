/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.integration.beans;

/**
 *
 * @author edimar
 */
public class ScoredPair implements Comparable<ScoredPair> {

    private Rule rule1;
    private Rule rule2;
    private Double distance;

    public ScoredPair(Rule rule1, Rule rule2, Double distance) {
        this.rule1 = rule1;
        this.rule2 = rule2;
        this.distance = distance;
    }

    public Rule getRule1() {
        return rule1;
    }

    public void setRule1(Rule rule1) {
        this.rule1 = rule1;
    }

    public Rule getRule2() {
        return rule2;
    }

    public void setRule2(Rule rule2) {
        this.rule2 = rule2;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(ScoredPair o) {
        return distance.compareTo(o.getDistance());
    }

    @Override
    public String toString() {
        return "ScoredPair{" + "rule1=" + rule1 + ", rule2=" + rule2 + ", distance=" + distance + '}';
    }
}
