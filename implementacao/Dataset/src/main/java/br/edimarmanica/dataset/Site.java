/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset;

/**
 *
 * @author edimar
 */
public interface Site {

    public String getFolderName();

    public Domain getDomain();

    /**
     *
     * @return the path from the dataset to the site
     */
    public String getPath();

    public String getGroundTruthPath();
}
