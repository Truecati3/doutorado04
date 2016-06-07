/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.distance;

/**
 *
 * @author edimar
 */
public class InsufficientOverlapException extends Exception {
    
    public InsufficientOverlapException(int nrSharedEntities) {
        super("The number of shared entities is insufficient: "+nrSharedEntities);
    }
    
    
}
