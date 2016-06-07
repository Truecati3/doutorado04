/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.load;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Dataset;
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
    private int ruleID;
    public static final int MIN_PAGES = 20; //1%

    public LoadRule(Site site, int ruleID) {
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

                    if (Integer.parseInt(record.get("ID")) == ruleID) {
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

        rule.setUrlValues(loadURLValues());
        rule.setEntityValues(loadEntityValues(rule.getUrlValues()));
        rule.setRuleID(ruleID);
        return rule;
    }

    /**
     *
     * @param ruleID
     * @return Map<URL, Value>
     */
    private Map<String, String> loadURLValues() {

        Map<String, String> values = new HashMap<>();
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values/rule_" + ruleID + ".csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    values.put(formatUrl(record.get("URL")), record.get("EXTRACTED VALUE"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        }

        return values;
    }

    private String formatUrl(String url) {
        if (site.getDomain().getDataset() == Dataset.WEIR) {
            return url.replaceAll(".*" + site.getDomain().getDataset().getFolderName() + "/", "");
        } else if (site.getDomain().getDataset() == Dataset.SWDE) {
            return url.replaceAll(".*" + site.getPath() + "/", "").replaceAll(".htm", "");
        } else {
            return null;
        }
    }

    /**
     *
     * @param urlValues Map<PageID, Value>
     * @return Map<Entity, Value>
     */
    private Map<String, String> loadEntityValues(Map<String, String> urlValues) {
        Map<String, String> entityValues = new HashMap<>();

        try (Reader in = new FileReader(Paths.PATH_BASE + site.getEntityPath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    String pageID = null;
                    if (site.getDomain().getDataset() == Dataset.SWDE) {
                        pageID = record.get("url").replaceAll(".*" + site.getFolderName() + "/", "").replaceAll(".htm", "");
                    } else {//WEIR
                        pageID = record.get("url");
                    }

                    if (!urlValues.containsKey(pageID)) {
                        continue;
                    }

                    entityValues.put(record.get("entityID"), urlValues.get(pageID));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        }

        return entityValues;
    }

    public static Map<Integer, Rule> loadAllRules(Site site) {
        Map<Integer, Rule> rules = new HashMap<>();

        File rulesDir = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values/");
        for (String rule : rulesDir.list()) {
            LoadRule lr = new LoadRule(site, new Integer(rule.replaceAll("rule_", "").replaceAll(".csv", "")));
            Rule r = lr.loadRule();

            if (r.getUrlValues().size() >= MIN_PAGES) {
                rules.put(r.getRuleID(), r);
            }
        }
        return rules;
    }

    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.swde.movie.Site.MSN;

        LoadRule load = new LoadRule(site, 8);
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
