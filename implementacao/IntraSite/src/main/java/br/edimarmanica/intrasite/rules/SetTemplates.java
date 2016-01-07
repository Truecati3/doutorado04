/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.rules;

import br.edimarmanica.configuration.IntrasiteExtraction;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;

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
        neo4j = new Neo4jHandler(site);
        findTemplates();
        findCandidateValues();
        neo4j.shutdown();
    }

    private void findTemplates() {
        String cypherTemplate = "MATCH m WITH count(DISTINCT m.URL) as nrpages MATCH (n {NODE_TYPE:'3'}) WITH n.VALUE as VALUE, n.PATH as PATH, COUNT(DISTINCT n.URL) as qtde, nrpages as nrpages WHERE qtde>=(nrpages*"+ IntrasiteExtraction.PR_TEMPLATE+"/100)  MATCH o WHERE o.VALUE=VALUE AND o.PATH=PATH AND o.NODE_TYPE='3' SET o:"+Label.Template+" RETURN count(o)";
        neo4j.executeCypher(cypherTemplate);
    }

    private void findCandidateValues() {
        String cypherValue = "MATCH (n) WHERE n.NODE_TYPE='3' AND NOT('"+Label.Template+"' in LABELS(n)) SET n:"+Label.CandValue+" RETURN count(n)";
        neo4j.executeCypher(cypherValue);
    }
    
    public static void main(String[] args) {
        SetTemplates st = new SetTemplates(br.edimarmanica.dataset.swde.auto.Site.AOL);
        st.execute();
    }
}
