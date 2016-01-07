/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.dataset.swde.auto.Site;
import br.edimarmanica.expressiveness.generate.CypherNotation;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;
import java.io.File;
import java.util.List;

/**
 *
 * @author edimar
 */
public class Teste {

    public static void main(String[] args) {
        Neo4jHandler neo = new Neo4jHandler(Site.AOL);
        List<Object> results = neo.querySingleColumn("MATCH (a2)<--(a1)<--(a0)<--(b)-->(c0)-->(c1) WHERE a2.VALUE='MSRP:' AND a2.PATH='/HTML/BODY/DIV/DIV/DIV/DIV/DIV/DIV/DIV/DIV/SPAN/SPAN/text()' AND a2.POSITION='1' AND a1.VALUE='SPAN' AND a1.POSITION='1' AND a0.VALUE='SPAN' AND a0.POSITION='1' AND b.VALUE='DIV' AND c0.VALUE='SPAN' AND c0.POSITION='2' AND c1.NODE_TYPE='3' AND c1.POSITION='1'  RETURN c1.VALUE AS VALUE, c1.URL AS URL, 'Template' in LABELS(c1) as template", "VALUE");
        for (Object obj : results) {
            System.out.println(obj.toString());
        }
    }
}
