/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.swde.book;


/**
 *
 * @author edimar
 */
public enum Attribute implements br.edimarmanica.dataset.Attribute {

    AUTHOR("author", "author"), ISBN13("isbn_13", "isbn_13"),
    PUBLICATION_DATE("publication_date", "publication_date"), PUBLISHER("publisher", "publisher"),
    TITLE("title", "title");
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
