/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.weir.finance;

/**
 *
 * @author edimar
 */
public enum Attribute implements br.edimarmanica.dataset.Attribute {

    VALUE("value", "NUMBER : value"), VAR_PER("var_per", "NUMBER : var %"), VAR_VALUE("var_value", "NUMBER : var $"), OPEN("open", "NUMBER : open"),
    HIGH("high", "NUMBER : high"), LOW("low", "NUMBER : low"), VOLUME("volume", "NUMBER : volume"), WK_LOW("52wk_low", "NUMBER : 52wk low"), WK_HIGH("52wk_high", "NUMBER : 52wk high");
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
