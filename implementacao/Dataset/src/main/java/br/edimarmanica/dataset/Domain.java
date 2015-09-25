/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset;

/**
 *
 * @author edimar
 */
public interface Domain {

    public String getFolderName();

    public Dataset getDataset();

    /**
     *
     * @return the path from the dataset to the domain
     */
    public String getPath();
    
    /**
     *
     * @param attributeID attribute ID provided by Edimar Manica
     * @return
     */
    public Attribute getAttributebyMyID(String attributeID);

    /**
     *
     * @param attributeIDbyDataset attribute ID provided by the dataset
     * @return
     */
    public Attribute getAttributeIDbyDataset(String attributeIDbyDataset);
    
    public Attribute[] getAttributes();
    
    public Site[] getSites();
    
}
