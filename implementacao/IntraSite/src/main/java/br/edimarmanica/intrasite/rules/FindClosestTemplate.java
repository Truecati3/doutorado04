/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.rules;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;
import br.edimarmanica.intrasite.extract.ExtractValues;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author edimar
 */
public class FindClosestTemplate {

    private Neo4jHandler neo4j;
    private Site site;
    private org.neo4j.graphdb.Label labelTemplate = DynamicLabel.label(Label.Template.name());
    private boolean append = false;

    public FindClosestTemplate(Site site) {
        this.site = site;
    }

    /**
     * Find the closest template to each candidate value
     */
    public void execute() {
        neo4j = new Neo4jHandler(site);
        try (Transaction transaction = neo4j.beginTx()) {
            generate();
            transaction.success();
        }
        neo4j.shutdown();
    }

    private void generate() {

        Long count = new Long(0);
        if (General.DEBUG) {
            String query = "MATCH (v:CandValue) RETURN count(v) as count";
            count = (Long) neo4j.querySingleColumn(query, "count").get(0);
            System.out.println("Total CandValue: " + count);
        }

        //seleciona os candValues
        String cypherQuery = "MATCH (v:CandValue) RETURN v as candValue";
        Iterator<Map<String, Object>> iterator = neo4j.executeCypher(cypherQuery);
        int i = 1;
        while (iterator.hasNext()) { //para cada CandValue

            if (General.DEBUG) {
                System.out.println("Faltam: " + (count - i));
            }

            i++;
            Map<String, Object> map = iterator.next();
            Node candNode = (Node) map.get("candValue");

            //seleciona o nodo mais próximo
            Node closestNode = closestTemplateNode(getNodeById(candNode.getId()));

            if (closestNode != null) { 
                //imprime label, UP_label, UP_value
                printRuleInfo(closestNode.getProperty("VALUE").toString(), closestNode.getProperty("UNIQUE_PATH").toString(), candNode.getProperty("UNIQUE_PATH").toString());
            }
        }
    }

    public Node getNodeById(long id) {
        Node node = neo4j.getGraphDb().getNodeById(id);
        return node;
    }

    private Node closestTemplateNode(Node candValueNode) {
        Queue<Node> fila = new LinkedList<>();
        Set<Long> visitedNodes = new HashSet<>();
        fila.add(candValueNode);

        while (!fila.isEmpty()) {
            Node targetNode = fila.poll();

            Iterator<Relationship> rels = targetNode.getRelationships().iterator();
            while (rels.hasNext()) {


                Relationship rel = rels.next();
                Node currentNode;
                if (rel.getStartNode().getId() == targetNode.getId()) {
                    currentNode = rel.getEndNode();
                } else {
                    currentNode = rel.getStartNode();
                }

                if (currentNode.hasLabel(labelTemplate)) {
                    return currentNode;
                } else {
                    if (!visitedNodes.contains(currentNode.getId())) {
                        fila.add(currentNode);
                        visitedNodes.add(currentNode.getId());
                    }
                }
            }
        }
        return null;
    }

    private void printRuleInfo(String label, String upLabel, String upValue) {
        File dir = new File(Paths.PATH_INTRASITE + "/" + site.getPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        CSVFormat format;
        if (append) {
            format = CSVFormat.EXCEL;
        } else {
            String[] header = {"LABEL", "UP_LABEL", "UP_VALUE"};
            format = CSVFormat.EXCEL.withHeader(header);
        }

        try (Writer out = new FileWriter(dir.getAbsolutePath() + "/closest_template.csv", append)) {

            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                List<String> dataRecord = new ArrayList<>();
                dataRecord.add(label);
                dataRecord.add(upLabel);
                dataRecord.add(upValue);
                csvFilePrinter.printRecord(dataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(ExtractValues.class.getName()).log(Level.SEVERE, null, ex);
        }

        append = true;
    }

    public static void main(String[] args) {
        FindClosestTemplate g = new FindClosestTemplate(br.edimarmanica.dataset.swde.book.Site.ADEBOOKS);
        g.execute();
    }
}
