/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.load;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.templatevariation.auto.bean.Rule;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class LoadRule {

    private Site site;
    private String ruleID;

    public LoadRule(Site site, String ruleID) {
        this.site = site;
        this.ruleID = ruleID;
    }

    public Rule loadRule() {

        Rule rule = new Rule();

        /**
         * ** Loading Label and Cypher ***
         */
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/rule_info.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {

                    if (("rule_" + record.get("ID") + ".csv").equals(ruleID)) {
                        rule.setXPath(CypherToXPath.cypher2xpath(record.get("RULE")));
                        rule.setLabel(record.get("LABEL"));
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        }

        rule.setUrlValues(loadURLValues(ruleID));
        rule.setEntityValues(loadEntityValues(rule.getUrlValues()));
        rule.setRuleID(ruleID);
        return rule;
    }

    /**
     *
     * @param ruleID
     * @return Map<URL, Value>
     */
    public Map<String, String> loadURLValues(String ruleID) {
        File fileRule = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values/" + ruleID);
        Map<String, String> values = new HashMap<>();
        try (Reader in = new FileReader(fileRule)) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    values.put(record.get("URL").replaceAll(".*" + site.getPath() + "/", ""),
                            record.get("EXTRACTED VALUE"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values;
    }

    /**
     *
     * @param urlValues Map<Entity, Value>
     * @return
     */
    public Map<String, String> loadEntityValues(Map<String, String> urlValues) {
        Map<String, String> entityValues = new HashMap<>();

        try (Reader in = new FileReader(Paths.PATH_BASE + site.getEntityPath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    if (urlValues.get(record.get("url").replaceAll(".*" + site.getPath() + "/", "")) == null) {
                        continue;
                    }

                    entityValues.put(record.get("entityID"),
                            urlValues.get(record.get("url").replaceAll(".*" + site.getPath() + "/", "")));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return entityValues;
    }

    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.swde.camera.Site.THENERDS;

        LoadRule load = new LoadRule(site, "rule_2.csv");
        Rule rule = load.loadRule();
        System.out.println("RuleID: " + rule.getRuleID());
        System.out.println("XPath: " + rule.getXPath());
        System.out.println("Label: " + rule.getLabel());

        System.out.println("URL VALUES");
        for (String url : rule.getUrlValues().keySet()) {
            System.out.println("\t" + url + "->" + rule.getUrlValues().get(url));
        }

        System.out.println("ENTITY VALUES");
        for (String entity : rule.getEntityValues().keySet()) {
            System.out.println("\t" + entity + "->" + rule.getEntityValues().get(entity));
        }
    }
}
