/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.configuration;

/**
 *
 * @author edimar
 */
public class IntrasiteExtraction {

    /**
     * PR_NULL_VALUES: percentual máximo de null values admitido para uma regra
     */
    public static final double PR_NULL_VALUES = 80; //according to WEIR
    
    /**
     * MAX_DISTANCE: max distance between the value and the label
     */
    public static final int MAX_DISTANCE = 9;//a base finance.freerealtime precisa de 9
    
    public static final int PR_TEMPLATE = 40; //percentual de ocorrência do mesmo valor no mesmo path (sem índice) para ser considerado template
}
