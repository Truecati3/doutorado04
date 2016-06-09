/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.integration.beans;

import br.edimarmanica.dataset.Site;
import java.util.Objects;

/**
 *
 * @author edimar
 */
public class Rule {

    private Site site;
    private String ruleID;

    public Rule(Site site, String ruleID) {
        this.site = site;
        this.ruleID = ruleID;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getRuleID() {
        return ruleID;
    }

    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
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
        if (!this.ruleID.equals(other.ruleID)) {
            return false;
        }
        return other.site == this.site;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.site);
        hash = 73 * hash + Objects.hashCode(this.ruleID);
        return hash;
    }

    @Override
    public String toString() {
        return "Rule{" + "site=" + site + ", ruleID=" + ruleID + '}';
    }
    
    
}
