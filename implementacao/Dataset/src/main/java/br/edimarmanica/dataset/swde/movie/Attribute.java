/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.swde.movie;




/**
 *
 * @author edimar
 */
public enum Attribute implements br.edimarmanica.dataset.Attribute {

    DIRECTOR("director", "director"), GENRE("genre", "genre"),
    RATING("rating", "mpaa_rating"), TITLE("title", "title");
    
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
