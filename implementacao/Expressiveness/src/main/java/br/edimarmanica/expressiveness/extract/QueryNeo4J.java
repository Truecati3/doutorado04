/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness.extract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.StringLogger;

/**
 *
 * @author edimar
 */
public class QueryNeo4J {

    public static String DB_PATH = "/home/edimar/neo4j-community-2.2.3/data/graph.db"; //cuidar versão neo4j
    private GraphDatabaseService graphDb;
    private ExecutionEngine engine;

    public QueryNeo4J() {
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM_ERR);

        registerShutdownHook(graphDb);
    }

    public ExecutionResult executeCypher(String cypher) {
        return engine.execute(cypher);
    }
    
    public List<String> querySingleColumn(String cypherQuery, String columnName){
        List<String> results = new ArrayList<>();
        
        ExecutionResult result = executeCypher(cypherQuery);
        ResourceIterator<String> iterator =  result.javaColumnAs(columnName);
        while(iterator.hasNext()){
            String st = iterator.next();
            results.add(st);
        }
        return results;
    }
    
    /**
     * 
     * @param cypherRule a cypher rule
     * @return the values extracted by the cypher rule (Map<URL, ExtractedValue>).
     */
    public Map<String, String> extract(String cypherRule){
        Map<String, String> extractedValues = new HashMap<>();

        ExecutionResult result = executeCypher(cypherRule);
        ResourceIterator<Map<String,Object>> iterator =  result.javaIterator();
        while(iterator.hasNext()){
            Map<String, Object> map = iterator.next();
            extractedValues.put(map.get("URL").toString(), map.get("VALUE").toString());
            
        }
        return extractedValues;
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
    
     public void shutdown() {
        graphDb.shutdown();
    }

    public Transaction beginTx() {
        return graphDb.beginTx();
    }
    
    
    public static void main(String[] args) {
        QueryNeo4J query = new QueryNeo4J();
        query.extract("MATCH (a1)<-[*]-(a0)<-[*]-(b)-[*]->(c0)-[*]->(c1) WHERE a1.VALUE='Edition:' AND a1.PATH='/HTML/BODY/FONT/TABLE/TBODY/TR/TD/TABLE/TBODY/TR/TD/TABLE/TBODY/TR/TD/TABLE/TBODY/TR/TD/text()'  AND a0.VALUE='TD' AND a0.POSITION='1'  AND b.VALUE='TR' AND b.POSITION='15'  AND c0.VALUE='TD' AND c0.POSITION='3'  AND c1.NODE_TYPE='3'  RETURN c1.VALUE AS VALUE, c1.URL AS URL");
    }
}
