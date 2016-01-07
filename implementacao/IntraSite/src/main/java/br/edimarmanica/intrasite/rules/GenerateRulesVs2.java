/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.rules;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.CypherNotation;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
import br.edimarmanica.htmltocsvtoneo4j.html2csv.CsvController;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;
import br.edimarmanica.intrasite.evaluate.EvaluateWEIR;
import br.edimarmanica.intrasite.extract.ExtractValues;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author edimar
 */
public class GenerateRulesVs2 {

    private Neo4jHandler neo4j;
    private Site site;
    private org.neo4j.graphdb.Label labelTemplate = DynamicLabel.label(Label.Template.name());
    private boolean append = false;
    private Set<String> tmp = new HashSet<>();

    public GenerateRulesVs2(Site site) {
        this.site = site;
    }

    public void execute() {
        neo4j = new Neo4jHandler(site);
        try (Transaction transaction = neo4j.beginTx()) {
            generate();
            transaction.success();
        }
        neo4j.shutdown();
    }

    private void generate() {

        //seleciona os candValues
        String cypherQuery = "match (v:CandValue) return v as candValue";
        Iterator<Map<String, Object>> iterator = neo4j.executeCypher(cypherQuery);
        int i = 0;
        while (iterator.hasNext()) { //para cada CandValue com o mesmo UNIQUE_PATH
            System.out.println("CandValue: " + i);
            i++;
            Map<String, Object> map = iterator.next();
            Node candNode = (Node) map.get("candValue");

            //seleciona o nodo mais próximo
            Node closestNode = closestTemplateNode(getNodeById(candNode.getId()));

            //imprime label, UP_label, UP_value
            tmp.add(closestNode.getProperty("VALUE").toString() + General.SEPARADOR + closestNode.getProperty("UNIQUE_PATH").toString() + General.SEPARADOR + candNode.getProperty("UNIQUE_PATH").toString());
        }

        System.out.println("Tmp: " + tmp.size());
        i = 0;
        for (String t : tmp) {
            String partes[] = t.split(General.SEPARADOR);
            CypherNotation cypherNotation = new CypherNotation(partes[0], partes[1], partes[2]);
            printRuleInfo(cypherNotation.getNotation(), i);
            i++;
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

    private void printRuleInfo(CypherRule rule, int ruleID) {
        File dir = new File(Paths.PATH_INTRASITE + "/" + site.getPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        CSVFormat format;
        if (append) {
            format = CSVFormat.EXCEL;
        } else {
            String[] header = {"ID", "LABEL", "RULE"};
            format = CSVFormat.EXCEL.withHeader(header);
        }

        try (Writer out = new FileWriter(dir.getAbsolutePath() + "/rule_info.csv", append)) {

            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                List<String> dataRecord = new ArrayList<>();
                dataRecord.add(ruleID + "");
                dataRecord.add(rule.getLabel());
                dataRecord.add(rule.getQueryWithoutParameters());
                csvFilePrinter.printRecord(dataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(ExtractValues.class.getName()).log(Level.SEVERE, null, ex);
        }

        append = true;
    }
    
  /*  public static Set<CypherRule> readRules(Site site, String ruleInfoPath){
        Set<CypherRule> rules = new HashSet<>();
         File dir = new File(Paths.PATH_INTRASITE + "/" + site.getPath()+"/rule_info.csv");
        for (File rule : dir.listFiles()) {

            Set<String> values = new HashSet<>();
            try (Reader in = new FileReader(rule.getAbsolutePath())) {
                try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                    for (CSVRecord record : parser) {
                        rules.add(new CypherRule);
                        values.add(record.get("URL").replaceAll("/sdd/edimar/Desktop/doutorado/doutorado04/bases/", "/media/Dados/bases/") + General.SEPARADOR + record.get("EXTRACTED VALUE"));
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
            }
            myResults.put(rule.getName(), values);
        }
    }*/

    public static void main(String[] args) {
        GenerateRulesVs2 g = new GenerateRulesVs2(br.edimarmanica.dataset.swde.auto.Site.AOL);
        g.execute();
    }
}
