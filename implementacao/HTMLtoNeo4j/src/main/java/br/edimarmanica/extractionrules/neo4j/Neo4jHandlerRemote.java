/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.neo4j;

import br.edimarmanica.dataset.Site;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;

/**
 *
 * @author edimar
 */
public class Neo4jHandlerRemote extends Neo4jHandler {

    private static final String DB_URI = "http://143.54.12.208:7474/db/data";
    private QueryEngine engine;

    public Neo4jHandlerRemote(Site site) {
        super(site);

        RestGraphDatabase restGraphDb = new RestGraphDatabase(DB_URI);
        engine = new RestCypherQueryEngine(restGraphDb.getRestAPI());

        graphDb = restGraphDb;
    }

    @Override
    public Iterator<Map<String, Object>> executeCypher(String cypher) {
        return engine.query(cypher, Collections.EMPTY_MAP).iterator();
    }

    public static void main(String[] args) {
        Neo4jHandler neo4j = new Neo4jHandlerRemote(br.edimarmanica.dataset.weir.book.Site.AMAZON);
        List<Object> results = neo4j.querySingleColumn("match n return count(n) as total", "total");
        for (Object result : results) {
            System.out.println(result.toString());
        }
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<Map<String, Object>> executeCypher(String cypher, Map<String, Object> params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
