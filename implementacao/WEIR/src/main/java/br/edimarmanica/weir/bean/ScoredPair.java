/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.bean;

/**
 *
 * @author edimar
 */
public class ScoredPair implements Comparable<ScoredPair>{
    private Rule r1;
    private Rule s1;
    private Double distance;

    public ScoredPair(Rule r1, Rule s1, Double distance) {
        this.r1 = r1;
        this.s1 = s1;
        this.distance = distance;
    }

    public Rule getR1() {
        return r1;
    }

    public void setR1(Rule r1) {
        this.r1 = r1;
    }

    public Rule getS1() {
        return s1;
    }

    public void setS1(Rule s1) {
        this.s1 = s1;
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
}
