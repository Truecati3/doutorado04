/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.neo4j;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Site;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author edimar
 */
public abstract class Neo4jHandler {

    GraphDatabaseService graphDb;
    Site site;

    public Neo4jHandler(Site site) {
        this.site = site;
    }

    public static Neo4jHandler getInstance(Site site) {
        switch (General.NEO4J_TYPE) {
            case LOCAL:
                return new Neo4jHandlerLocal(site);
            default:
                return null;
        }
    }

    public abstract Iterator<Map<String, Object>> executeCypher(String cypher);

    public abstract Iterator<Map<String, Object>> executeCypher(String cypher, Map<String, Object> params);

    /**
     *
     * @param properties
     * @return the node created
     */
    public Node insertNode(Map<String, String> properties) {

        Node node = graphDb.createNode();

        for (String key : properties.keySet()) {
            node.setProperty(key, properties.get(key));
        }
        return node;
    }

    /**
     * insert the new node and create a relationalShip with its parent node
     *
     * @param properties
     * @param parentNode
     * @return
     */
    public Node insertNode(Map<String, String> properties, Node parentNode) {
        Node node = graphDb.createNode();
        for (String key : properties.keySet()) {
            node.setProperty(key, properties.get(key));
        }

        if (parentNode != null) {
            insertRelationship(parentNode, node, properties);
        }
        return node;
    }

    public void insertRelationship(Node from, Node to, Map<String, String> properties) {
        Relationship relationship = from.createRelationshipTo(to, RelTypes.has_child);

        if (properties != null) {
            for (String key : properties.keySet()) {
                relationship.setProperty(key, properties.get(key));
            }
        }
    }

    public List<Object> querySingleColumn(String cypherQuery, Map<String, Object> parameters, String columnName) {
        List<Object> results = new ArrayList<>();

        Iterator<Map<String, Object>> iterator;
        if (parameters == null) {
            iterator = executeCypher(cypherQuery);
        } else {
            iterator = executeCypher(cypherQuery, parameters);
        }
        
        while (iterator.hasNext()) {
            Map<String, Object> st = iterator.next();
            results.add(st.get(columnName));
        }
        return results;
    }

    public List<Object> querySingleColumn(String cypherQuery, String columnName) {
        return querySingleColumn(cypherQuery, null, columnName);
    }

    /**
     *
     * @param cypherRule a cypher rule
     * @return the values extracted by the cypher rule (Map<URL,
     * ExtractedValue>).
     */
    public Map<String, String> extract(String cypherRule, String keyColumn, String valueColumn) {
        return extract(cypherRule, null, keyColumn, valueColumn);
    }

    public Map<String, String> extract(String cypherRule, Map<String, Object> params, String keyColumn, String valueColumn) {
        Map<String, String> extractedValues = new HashMap<>();

        Iterator<Map<String, Object>> iterator;
        if (params == null) {
            iterator = executeCypher(cypherRule);
        } else {
            iterator = executeCypher(cypherRule, params);
        }

        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            extractedValues.put(map.get(keyColumn).toString(), map.get(valueColumn).toString());
        }
        return extractedValues;
    }

    public static void registerShutdownHook(final GraphDatabaseService graphDb) {
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

    public abstract void shutdown();

    public Transaction beginTx() {
        return graphDb.beginTx();
    }
}
