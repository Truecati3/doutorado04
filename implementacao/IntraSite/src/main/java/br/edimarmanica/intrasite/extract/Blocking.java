/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.extract;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class Blocking {

    private Site site;
    public static final int BLOCK_SIZE = 3;
    private Neo4jHandler neo4j;

    public Blocking(Site site) {
        this.site = site;
    }

    public void execute() throws IncorrectBlockException {
        neo4j = new Neo4jHandler(site);

        deleteAllBlocks();
        createNodes();
        createRelationShips();
        check();

        neo4j.shutdown();
    }

    private void createNodes() {
        String cypher = "MATCH (t:Template) WITH DISTINCT LEFT(t.VALUE, " + BLOCK_SIZE + ") AS key CREATE (b:Block {KEY:key})";
        neo4j.executeCypher(cypher);
    }

    private void createRelationShips() {
        long maxId = getMaxId();
        int increment = 100000;

        for (long i = 0; i <= maxId; i += increment) {
            String cypher = "MATCH (t:Template), (b:Block) WHERE b.KEY = LEFT(t.VALUE, " + BLOCK_SIZE + ") AND id(t) >=" + i + " AND id(t) <" + (i + increment)
                    + " CREATE (b)-[r:START]->(t)";
            neo4j.executeCypher(cypher);
        }

    }

    private long getMaxId() {
        String cypher = "MATCH (n) RETURN MAX(id(n)) as max";
        return (Long) neo4j.querySingleColumn(cypher, "max").get(0);
    }

    private void check() throws IncorrectBlockException {
        String cypher = "MATCH (t:Template) RETURN count(t) as c1";
        long template = (Long) neo4j.querySingleColumn(cypher, "c1").get(0);

        cypher = "MATCH p=(b:Block)-->(t:Template) RETURN count(p) as c2";
        long rels = (Long) neo4j.querySingleColumn(cypher, "c2").get(0);

        if (template != rels) {
            throw new IncorrectBlockException(rels, template);
        }
    }

    private void deleteAllBlocks() {
        String cypher = "MATCH (b:Block)-[r]->() "
                + "WITH r LIMIT 50000 "
                + "DELETE r "
                + "RETURN count(r) as deletedCount";
        long deletedNodesCount = (Long) neo4j.querySingleColumn(cypher, "deletedCount").get(0);
        while (deletedNodesCount > 0) {
            System.out.println("deletando");
            deletedNodesCount = (Long) neo4j.querySingleColumn(cypher, "deletedCount").get(0);
        }

        cypher = "MATCH (b:Block) DELETE b";
        neo4j.executeCypher(cypher);
    }

    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.swde.camera.Site.BEACHAUDIO;

        Blocking block = new Blocking(site);
        try {
            block.execute();
        } catch (IncorrectBlockException ex) {
            Logger.getLogger(Blocking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
