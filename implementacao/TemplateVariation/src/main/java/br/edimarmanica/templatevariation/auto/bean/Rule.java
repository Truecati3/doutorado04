/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.bean;

import java.util.Map;

/**
 *
 * @author edimar
 */
public class Rule {

    private String ruleID;
    private String XPath;
    private String label;
    private Map<String, String> urlValues; //Map<URL,Value>
    private Map<String, String> entityValues;//Map<Entity,Value>

    public Rule(String ruleID, String XPath, String label, Map<String, String> urlValues, Map<String, String> entityValues) {
        this.ruleID = ruleID;
        this.XPath = XPath;
        this.label = label;
        this.urlValues = urlValues;
        this.entityValues = entityValues;
    }

    public Rule() {
    }

    public String getRuleID() {
        return ruleID;
    }

    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }

    public String getXPath() {
        return XPath;
    }

    public void setXPath(String XPath) {
        this.XPath = XPath;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, String> getUrlValues() {
        return urlValues;
    }

    public void setUrlValues(Map<String, String> urlValues) {
        this.urlValues = urlValues;
    }

    public Map<String, String> getEntityValues() {
        return entityValues;
    }

    public void setEntityValues(Map<String, String> entityValues) {
        this.entityValues = entityValues;
    }
}
