/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.swde.university;





/**
 *
 * @author edimar
 */
public enum Attribute implements br.edimarmanica.dataset.Attribute {

    NAME("name", "name"), PHONE("phone", "phone"),
    TYPE("type", "type"), WEBSITE("website", "website");
    
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
