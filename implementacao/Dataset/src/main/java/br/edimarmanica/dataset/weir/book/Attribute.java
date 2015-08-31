/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.weir.book;

/**
 *
 * @author edimar
 */
public enum Attribute implements br.edimarmanica.dataset.Attribute{
    AUTHOR ("author", "STRING : AUTHOR"), TITLE ("title", "STRING : TITLE"), PUBLISHER ("publisher", "STRING : PUBLISHER"), ISBN_13 ("isbn13", "ISBN : ISBN13"),
            BINDING ("binding", "STRING : BINDING"), PUBLICATION_DATE ("date", "DATE : DATE"), EDITION("edition", "STRING : EDITION");
    
    private String attributeID;
    private String attributeIDbyDataset; //ID do atributo pelo domínio

    private Attribute(String attributeID, String attributeIDbyDataset) {
        this.attributeID = attributeID;
        this.attributeIDbyDataset = attributeIDbyDataset;
    }

    @Override
    public String getAttributeID() {
        return attributeID;
    }

    @Override
    public String getAttributeIDbyDataset() {
        return attributeIDbyDataset;
    }

    
    
}
