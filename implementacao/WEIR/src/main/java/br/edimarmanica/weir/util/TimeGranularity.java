/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.util;

/**
 *
 * @author edimar
 */
public enum TimeGranularity {

    MILISECONDS(1), SECONDS(1000), MINUTES(60 * 1000);
    private int divisao;

    private TimeGranularity(int divisao) {
        this.divisao = divisao;
    }

    public int getDivisao() {
        return divisao;
    }
}
