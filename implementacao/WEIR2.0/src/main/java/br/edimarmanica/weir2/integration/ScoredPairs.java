/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.integration;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir2.distance.TypeAwareDistance;
import br.edimarmanica.weir2.integration.beans.Rule;
import br.edimarmanica.weir2.integration.beans.ScoredPair;
import br.edimarmanica.weir2.rule.Loader;
import br.edimarmanica.weir2.rule.filter.RulesFilter;
import br.edimarmanica.weir2.rule.type.DataType;
import br.edimarmanica.weir2.rule.type.RuleDataType;
import br.edimarmanica.weir2.rule.type.RulesDataTypeController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class ScoredPairs {

    private Domain domain;
    private boolean append = false;

    public ScoredPairs(Domain domain) {
        this.domain = domain;
    }

    public void execute() {
        List<List<String>> filteredRules = new ArrayList<>(); // List<Site<Rule>>
        List<Map<String, String>> entityIDsBySite = new ArrayList<>(); //List<Site<Map<Page,Entity>>>
        for (Site site : domain.getSites()) {
            filteredRules.add(RulesFilter.loadFilteredRules(site));

            entityIDsBySite.add(Loader.loadEntityID(site));
        }

        for (int indexSiteI = 0; indexSiteI < filteredRules.size(); indexSiteI++) {
            for (int indexRuleI = 0; indexRuleI < filteredRules.get(indexSiteI).size(); indexRuleI++) {

                File fileRi = new File(filteredRules.get(indexSiteI).get(indexRuleI));
                DataType typeRi = RuleDataType.getMostFrequentType(fileRi);
                Map<String, String> entityValuesRi = Loader.loadEntityValues(fileRi, entityIDsBySite.get(indexSiteI));

                for (int indexSiteJ = indexSiteI; indexSiteJ < filteredRules.size(); indexSiteJ++) {
                    for (int indexRuleJ = 0; indexRuleJ < filteredRules.get(indexSiteJ).size(); indexRuleJ++) {
                        if (indexSiteJ == indexSiteI && indexRuleJ <= indexRuleI) {
                            continue;
                        }

                        File fileRj = new File(filteredRules.get(indexSiteJ).get(indexRuleJ));
                        DataType typeRj = RuleDataType.getMostFrequentType(fileRj);
                        Map<String, String> entityValuesRj = Loader.loadEntityValues(fileRj, entityIDsBySite.get(indexSiteJ));
                        double distance = TypeAwareDistance.typeDistance(entityValuesRi, typeRi, entityValuesRj, typeRj);

                        if (General.DEBUG) {
                            System.out.println("i[" + indexSiteI + "][" + indexRuleI + "] X j[" + indexSiteJ + "][" + indexRuleJ + "] = " + distance);
                        }

                        if (distance < 1.00) { //os com distância = 1 não interessam
                            persiste(domain.getSites()[indexSiteI], filteredRules.get(indexSiteI).get(indexRuleI), domain.getSites()[indexSiteJ], filteredRules.get(indexSiteJ).get(indexRuleJ), distance);
                        }

                    }
                }
            }
        }
    }

    private void persiste(Site site1, String rule1, Site site2, String rule2, double score) {
        List<String> dataRecord = new ArrayList<>();
        dataRecord.add(site1.toString());
        dataRecord.add(rule1);
        dataRecord.add(site2.toString());
        dataRecord.add(rule2);
        dataRecord.add(score + "");

        File dirOutput = new File(Paths.PATH_WEIR_V2 + "/" + domain.getPath());
        dirOutput.mkdirs();

        File file = new File(dirOutput.getAbsolutePath() + "/scores.csv");

        CSVFormat format;
        if (append) {
            format = CSVFormat.EXCEL;
        } else {
            String[] HEADER = {"SITE1", "RULE1", "SITE2", "RULE2", "SCORE"};
            format = CSVFormat.EXCEL.withHeader(HEADER);
        }

        try (Writer out = new FileWriter(file, append)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                csvFilePrinter.printRecord(dataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(RulesFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        append = true;
    }

    public static List<ScoredPair> loadAndSort(Domain domain) {
        List<ScoredPair> pairs = new ArrayList<>();

        try (Reader in = new FileReader(new File(Paths.PATH_WEIR_V2 + "/" + domain.getPath() + "/scores.csv"))) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) { //para cada value
                    Rule rule1 = new Rule(domain.getSiteOf(record.get("SITE1")), record.get("RULE1"));
                    Rule rule2 = new Rule(domain.getSiteOf(record.get("SITE2")), record.get("RULE2"));
                    double score = Double.valueOf(record.get("SCORE"));
                    if (score == 1) {
                        continue;
                    }
                    ScoredPair pair = new ScoredPair(rule1, rule2, score);
                    pairs.add(pair);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RulesDataTypeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RulesDataTypeController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Collections.sort(pairs);

        return pairs;

    }

    public static void main(String[] args) {
        General.DEBUG = true;

        for (Dataset dataset : Dataset.values()) {
            System.out.println("Dataset: " + dataset);
            for (Domain domain : dataset.getDomains()) {
                System.out.println("\tDomain: " + domain);
                ScoredPairs sp = new ScoredPairs(domain);
                sp.execute();
            }
        }
    }

}
