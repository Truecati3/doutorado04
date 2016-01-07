/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.rules;

import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class SetTemplatesTest extends TestCase {

    public SetTemplatesTest(String testName) {
        super(testName);
    }

    /**
     * Test of execute method, of class SetTemplates.
     */
    public void testExecute() {
        //Verificando em quais sites os templates já foram setados
        Domain domain = br.edimarmanica.dataset.swde.Domain.AUTO;

        for (Site site : domain.getSites()) {
            
            if (site != br.edimarmanica.dataset.swde.auto.Site.AUTOMOTIVE){
                continue;
            }
            
            check(site);
        }
    }

    public void check(Site site) {
        System.out.println("Site: " + site.getFolderName());

        Neo4jHandler neo4j = new Neo4jHandler(site);

        String query = "MATCH (n) return count(distinct n.URL) AS urls";
        String countTotal = neo4j.querySingleColumn(query, "urls").get(0).toString();
        System.out.println("\t URLs: " + countTotal);
        
        
        query = "MATCH (n) WHERE n.NODE_TYPE = '3' RETURN count(n) AS countTextNodes";
        Long countTextNodes = (Long)neo4j.querySingleColumn(query, "countTextNodes").get(0);
        System.out.println("\t TextNodes: " + countTextNodes);
        
        query = "MATCH (c:"+Label.CandValue.name()+") RETURN count(c) AS countCandValues";
        Long countCandValues = (Long)neo4j.querySingleColumn(query, "countCandValues").get(0);
        System.out.println("\t CandValues: " + countCandValues);
        
        query = "MATCH (t:"+Label.Template.name()+") RETURN count(t) AS countTemplates";
        Long countTemplates = (Long)neo4j.querySingleColumn(query, "countTemplates").get(0);
        System.out.println("\t Templates: " + countTemplates);
        
        if (countTextNodes.longValue()- countCandValues.longValue()- countTemplates.longValue()== 0){
            System.out.println("\t OK!");
        }else{
            System.out.println("\t NOPS!");
        }

        neo4j.shutdown();
    }
}
