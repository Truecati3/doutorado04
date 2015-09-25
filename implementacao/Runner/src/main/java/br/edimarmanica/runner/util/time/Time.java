/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.time;

/**
 *
 * @author edimar
 */
public class Time {

    private final long FACTOR_SECOND = 1000;
    private final long FATOR_MINUTE = FACTOR_SECOND * 60;
    private final long FATOR_HOUR = FATOR_MINUTE * 60;
    private long time;

    /**
     *
     * @param time tempo em milissegundos absolutos, ou seja, ao completar 1
     * segundo NÃO zera os milisegundos
     */
    public Time(long time) {
        this.time = time;
    }

    /**
     *
     * @return retorna o número total de horas, ou seja, não zera ao mudar o dia
     */
    public long getAbsoluteHours() {
        return time / FATOR_HOUR;
    }

    /**
     *
     * @return retorna os minutos descontadas as horas, ou seja, ao completar 1
     * hora zera os minutos
     */
    public long getRelativeMinutes() {
        return (time % FATOR_HOUR) / FATOR_MINUTE;
    }

    /**
     *
     * @return retorna o número total de minutos, ou seja, ao completar 1 hora
     * NÃO zera os minutos
     */
    public long getAbsoluteMinutes() {
        return time / FATOR_MINUTE;
    }

    /**
     *
     * @return retorna os segundos descontados os minutos, ou seja, ao completar
     * 1 minuto zera os segundos
     */
    public long getRelativeSeconds() {
        return (time % FATOR_MINUTE) / FACTOR_SECOND;
    }

    /**
     *
     * @return retorna o número total de segundos, ou seja, ao completar 1
     * minuto NÃO zera os segundos
     */
    public long getAbsoluteSeconds() {
        return time / FACTOR_SECOND;
    }

    /**
     *
     * @return retorna os milisegundos descontados os segundos, ou seja, ao
     * completar 1 segundo zera os milisegundos
     */
    public long getRelativeMiliseconds() {
        return time % FACTOR_SECOND;
    }

    /**
     *
     * @return retorna o tempo total em milissegundos, ou seja, ao completar 1
     * segundo NÃO zera os milisegundos
     */
    public long getAbsoluteMiliseconds() {
        return time;
    }

    @Override
    public String toString() {
        return "Time{" + "hours=" + getAbsoluteHours() + ", minutes=" + getRelativeMinutes() + ", seconds=" + getRelativeSeconds() + ", miliseconds=" + getRelativeMiliseconds() + '}';
    }
}
