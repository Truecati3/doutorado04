/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.rule.type;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
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
public class RulesDataTypeController {

    /**
     * Persiste the datatype of each rule
     *
     * @param site
     */
    public static void persiste(Site site) {
        Map<String, DataType> ruleType = new HashMap<>();

        File dirInput = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values");
        for (File rule : dirInput.listFiles()) {
            ruleType.put(rule.getName(), RuleDataType.getMostFrequentType(rule));
        }

        File dirOutput = new File(Paths.PATH_WEIR_V2 + "/" + site.getPath());
        dirOutput.mkdirs();

        File file = new File(dirOutput.getAbsolutePath() + "/types.csv");
        String[] HEADER = {"RULE", "TYPE"};
        CSVFormat format = CSVFormat.EXCEL.withHeader(HEADER);

        try (Writer out = new FileWriter(file)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                for (String rule : ruleType.keySet()) {
                    List<String> dataRecord = new ArrayList<>();
                    dataRecord.add(rule);
                    dataRecord.add(ruleType.get(rule).name());
                    csvFilePrinter.printRecord(dataRecord);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RulesDataTypeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param site
     * @return the type of the rules of the site that was persisted
     */
    public static Map<String, DataType> load(Site site) {
        Map<String, DataType> ruleType = new HashMap<>();

        try (Reader in = new FileReader(new File(Paths.PATH_WEIR_V2 + "/" + site.getPath() + "/types.csv"))) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) { //para cada value
                    String rule = record.get("RULE");
                    String type = record.get("TYPE");
                    ruleType.put(rule, DataType.valueOf(type));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RulesDataTypeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RulesDataTypeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ruleType;
    }

    public static void main(String[] args) {
        for (Dataset dataset : Dataset.values()) {
            System.out.println("Dataset: " + dataset);
            for (Domain domain : dataset.getDomains()) {
                if (domain != br.edimarmanica.dataset.swde.Domain.UNIVERSITY){
                    continue;
                }
                
                System.out.println("\tDomain: " + domain);
                for (Site site : domain.getSites()) {
                    /*if (site != br.edimarmanica.dataset.swde.restaurant.Site.TRIPADVISOR){
                        continue;
                    }*/
                    System.out.println("\t\tSite: " + site);
                    RulesDataTypeController.persiste(site);
                }
            }
        }

        //Site site = br.edimarmanica.dataset.swde.auto.Site.AOL;
        //System.out.println(RulesDataTypeController.load(site));
    }
}
