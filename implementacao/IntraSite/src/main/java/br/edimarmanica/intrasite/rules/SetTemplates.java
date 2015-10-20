/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.rules;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandler;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;

/**
 *
 * @author edimar
 */
public class SetTemplates {

    private Neo4jHandler neo4j;
    private Site site;

    public SetTemplates(Site site) {
        this.site = site;
    }

    public void execute() {
        neo4j = Neo4jHandler.getInstance(site);
        findTemplates();
        findCandidateValues();
        neo4j.shutdown();
    }

    private void findTemplates() {
        String cypherTemplate = "MATCH m WITH count(DISTINCT m.URL) as nrpages MATCH (n {NODE_TYPE:'3'}) WITH n.VALUE as VALUE, n.PATH as PATH, COUNT(DISTINCT n.URL) as qtde, nrpages as nrpages WHERE qtde>=(nrpages*40/100)  MATCH o WHERE o.VALUE=VALUE AND o.PATH=PATH AND o.NODE_TYPE='3' SET o:Template RETURN count(o)";
        neo4j.executeCypher(cypherTemplate);
    }

    private void findCandidateValues() {
        String cypherValue = "MATCH (n) WHERE n.NODE_TYPE='3' AND NOT('Template' in LABELS(n)) SET n:CandValue RETURN count(n)";
        neo4j.executeCypher(cypherValue);
    }
}
