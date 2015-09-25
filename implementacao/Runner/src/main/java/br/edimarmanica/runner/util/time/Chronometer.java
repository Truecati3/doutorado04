/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.time;

/**
 *
 * @author edimar
 */
public class Chronometer {

    private long initialHour = 0;

    public Chronometer() {
    }

    /**
     * inicia o cronômetro
     */
    public void start() {
        initialHour = System.currentTimeMillis();
    }


    /**
     * 
     * @return retorna o tempo em milissegundos decorrido desde o início do cronômetro
     */
    public Time elapsedTime() {
        long currHour = System.currentTimeMillis();
        return new Time(currHour - initialHour);
    }
}
