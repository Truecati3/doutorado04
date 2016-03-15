/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.extract;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 *
 * @author edimar
 */
public class ExtractValuesScalable {

    private Neo4jHandler neo4j;
    private Site site;
    private Set<CypherRule> rules;

    public ExtractValuesScalable(Site site, Set<CypherRule> rules) {
        this.site = site;
        this.rules = rules;
    }

    public void blocking() throws IncorrectBlockException {
        Blocking blocking = new Blocking(site);
        blocking.execute();
    }

    public void printExtractedValues() {

        deleteCurrentRules();
        try {
            blocking();
        } catch (IncorrectBlockException ex) {
            Logger.getLogger(ExtractValuesScalable.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        neo4j = new Neo4jHandler(site);

        boolean append = false;
        int i = 0;
        for (CypherRule rule : rules) {
            if (General.DEBUG) {
                System.out.println("Faltam: " + (rules.size() - i));
            }

            printRuleInfo(rule, i, append);
            printExtractedValues(rule, i);
            append = true;
            i++;
        }
        neo4j.shutdown();
    }

    private void printExtractedValues(CypherRule rule, int i) {

        File dir = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (Writer out = new FileWriter(dir.getAbsolutePath() + "/rule_" + i + ".csv")) {

            String[] header = {"URL", "EXTRACTED VALUE"};
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, CSVFormat.EXCEL.withHeader(header))) {
                Map<String, String> extractedValues = scaleQuery(rule);
                for (String url : extractedValues.keySet()) {
                    List<String> dataRecord = new ArrayList<>();
                    dataRecord.add(url);
                    dataRecord.add(extractedValues.get(url));
                    csvFilePrinter.printRecord(dataRecord);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ExtractValuesScalable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printRuleInfo(CypherRule rule, int ruleID, boolean append) {
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
            Logger.getLogger(ExtractValuesScalable.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void deleteCurrentRules() {
        File dir = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values/");
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }
    }

    /**
     *
     * @param originalQuery
     * @return originalQuery scalable
     */
    private Map<String, String> scaleQuery(CypherRule rule) {
        String scaleQuery = rule.getQuery().replaceAll("MATCH ", "MATCH (block:Block) WHERE block.KEY=LEFT({valuex}, " + Blocking.BLOCK_SIZE + ") WITH block \n"
                + "MATCH block-->");
        Map<String, Object> scaleParams = rule.getParams();
        scaleParams.put("valuex", rule.getLabel());

        return neo4j.extract(scaleQuery, scaleParams, "URL", "VALUE");
    }
}
