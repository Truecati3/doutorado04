/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.bean;

import br.edimarmanica.dataset.Site;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class Rule {

    private int ruleID;
    private String label;
    private String ruleCypher;
    private Set<Value> values;
    private Site site;

    public Rule(int ruleID, Site site, String label, String ruleCypher) {
        this.ruleID = ruleID;
        this.label = label;
        this.ruleCypher = ruleCypher;
        this.site = site;
    }

    public Rule(int ruleID, Site site) {
        this.ruleID = ruleID;
        this.site = site;
    }

    public String getLabel() {
        return label;
    }

    public String getRuleCypher() {
        return ruleCypher;
    }

    public int getRuleID() {
        return ruleID;
    }

    public Set<Value> getValues() {
        return values;
    }
    
    public Set<Value> getNotNullValues() {
        Set<Value> notNullValues = new HashSet<>();
        for(Value v: values){
            if (v.getValue() != null){
                notNullValues.add(v);
            }
        }
        return notNullValues;
    }

    public void setValues(Set<Value> values) {
        this.values = values;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public void addValue(String value, String pageID) {
        if (values == null) {
            values = new HashSet<>();
        }

        values.add(new Value(value, pageID));
    }

    public void addValue(String value, String pageID, String entityID) {
        if (values == null) {
            values = new HashSet<>();
        }

        values.add(new Value(value, pageID, entityID));
    }

    public void addValues(Set<Value> values) {
        if (values == null) {
            values = new HashSet<>();
        }

        values.addAll(values);
    }

    public void addValue(Value value) {
        if (values == null) {
            values = new HashSet<>();
        }

        values.add(value);
    }

    public Set<String> getPairsPageValue() {
        Set<String> valuesString = new HashSet<>();

        for (Value v : values) {
            valuesString.add(v.getPageID() + "_" + v.getValue());
        }

        return valuesString;
    }

    /**
     *
     * @return the set of pages that the rule extract a not null value
     */
    public Set<String> getPagesWithNotNullValues() {
        Set<String> pages = new HashSet<>();

        for (Value v : values) {
            if (v.getValue() != null) {
                pages.add(v.getPageID());
            }
        }

        return pages;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.ruleID);
        hash = 79 * hash + Objects.hashCode(this.site.getPath());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rule other = (Rule) obj;
        if (this.ruleID != other.ruleID) {
            return false;
        }
        if (this.site != other.site) {
            return false;
        }
        return true;
    }

    public int getNrNotNullValues() {
        int nrNotNullValues = 0;
        for (Value v : getValues()) {
            if (v.getValue() != null) {
                nrNotNullValues++;
            }
        }
        return nrNotNullValues;
    }
}
