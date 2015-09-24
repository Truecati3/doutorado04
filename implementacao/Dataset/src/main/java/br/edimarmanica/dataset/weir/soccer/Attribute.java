/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.weir.soccer;

/**
 *
 * @author edimar
 */
public enum Attribute implements br.edimarmanica.dataset.Attribute {

    SHIRT("shirt", "NUMBER : shirt_number"), POSITION("position", "STRING : position"),
    HEIGHT("height", "SPACE : height"), WEIGHT("weight", "MASS : weight"), BIRTHDATE("birthdate", "DATE : birthdate"),
    NATIONAL_TEAM("national_team", "STRING : national_team"), CLUB("club", "STRING : club"),
    BIRTHPLACE("birthplace", "STRING : birthplace"), NATIONALITY("nationality", "STRING : nationality");
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
