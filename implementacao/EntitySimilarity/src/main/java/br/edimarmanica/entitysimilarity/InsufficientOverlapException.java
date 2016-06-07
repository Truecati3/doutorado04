/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.entitysimilarity;

/**
 *
 * @author edimar
 */
public class InsufficientOverlapException extends Exception {
    
    public InsufficientOverlapException(int nrSharedEntities) {
        super("The number of shared entities is insufficient: "+nrSharedEntities);
    }
    
    
}
