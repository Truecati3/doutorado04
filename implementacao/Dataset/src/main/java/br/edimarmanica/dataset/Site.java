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
    
    public String getGroundTruthPath(Attribute attr);
    
    /**
     * 
     * @return the path with the file that contains the entity described by each page
     */
    public String getEntityPath();
}
