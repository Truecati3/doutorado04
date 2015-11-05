/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.util;

/**
 *
 * @author edimar
 */
public class Cronometer {

    private long tempoInicio;

    public void start() {
        tempoInicio = System.currentTimeMillis();
    }

    public void restart() {
        start();
    }

    /**
     *
     * @return duração em milisegundos
     */
    public float getDuration(TimeGranularity granularity) {
        return (float)(System.currentTimeMillis() - tempoInicio) / granularity.getDivisao();
    }
}


