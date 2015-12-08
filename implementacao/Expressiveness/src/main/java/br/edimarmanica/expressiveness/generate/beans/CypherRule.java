/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness.generate.beans;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @author edimar
 */
public class CypherRule {

    private String query;
    private Map<String, Object> params;
    private String label;

    public CypherRule() {
    }

    public CypherRule(String query, Map<String, Object> params, String label) {
        this.query = query;
        this.params = params;
        this.label = label;
    }

    public String getQuery() {
        return query;
    }

    public String getQueryWithoutParameters() {
        String result = query;

        for (String key : params.keySet()) {
            //tem que ser replace senão dá problemas com valores como "$19.4" devido ao $ ser expressão regular
            result = result.replace("{" + key + "}", "'" + params.get(key).toString().replaceAll("'", "\\\\'") + "'");
        }
        return result;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
    public boolean equals(Object obj) {
        CypherRule newObj;
        if (obj instanceof CypherRule) {
            newObj = (CypherRule) obj;
        }else{
            return false;
        }
        return getQueryWithoutParameters().equals(newObj.getQueryWithoutParameters()); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(getQueryWithoutParameters());
        return hash;
    }
}
