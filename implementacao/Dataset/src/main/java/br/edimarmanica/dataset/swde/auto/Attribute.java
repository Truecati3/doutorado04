/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.swde.auto;

/**
 *
 * @author edimar
 */
public enum Attribute implements br.edimarmanica.dataset.Attribute {

    MODEL("model", "model"), PRICE("price", "price"),
    ENGINE("engine", "engine"), FUEL_ECONOMY("fuel_economy", "fuel_economy");
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
