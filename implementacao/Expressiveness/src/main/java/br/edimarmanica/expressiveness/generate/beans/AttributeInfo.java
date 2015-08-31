/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness.generate.beans;

/**
 *
 * @author edimar
 */
public class AttributeInfo {
    private String attribute;
    private String label;
    private String uniquePathLabel;
    private String uniquePathValue;

    public AttributeInfo(String attribute, String label, String uniquePathLabel, String uniquePathValue) {
        this.attribute = attribute;
        this.label = label;
        this.uniquePathLabel = uniquePathLabel;
        this.uniquePathValue = uniquePathValue;
    }
    
    

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUniquePathLabel() {
        return uniquePathLabel;
    }

    public void setUniquePathLabel(String uniquePathLabel) {
        this.uniquePathLabel = uniquePathLabel;
    }

    public String getUniquePathValue() {
        return uniquePathValue;
    }

    public void setUniquePathValue(String uniquePathValue) {
        this.uniquePathValue = uniquePathValue;
    }
    
    
}
