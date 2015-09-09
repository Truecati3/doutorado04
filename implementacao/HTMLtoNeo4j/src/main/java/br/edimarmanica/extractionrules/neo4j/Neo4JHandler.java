/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.neo4j;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.StringLogger;

/**
 *
 * @author edimar
 */
public class Neo4JHandler {

    public final static String DB_PATH = "/home/edimar/neo4j-community-2.2.3/data/graph.db"; //cuidar versão neo4j
    private GraphDatabaseService graphDb;
    private ExecutionEngine engine;

    public Neo4JHandler() {
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM_ERR);

        registerShutdownHook(graphDb);
    }

    public void deleteAll() {
        engine.execute("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r");
    }

    public static void deleteDatabase() {
        try {
            Runtime.getRuntime().exec("rm -rf " + DB_PATH);
        } catch (IOException ex) {
            Logger.getLogger(Neo4JHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ExecutionResult executeCypher(String cypher) {
        return engine.execute(cypher);
    }

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
}
