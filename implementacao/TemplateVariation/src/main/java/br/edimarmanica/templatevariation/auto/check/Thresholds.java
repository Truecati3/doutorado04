/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.check;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Printer;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import static br.edimarmanica.templatevariation.auto.Ranking.T_domain;
import static br.edimarmanica.templatevariation.auto.Ranking.T_entity;
import static br.edimarmanica.templatevariation.auto.Ranking.T_label;
import static br.edimarmanica.templatevariation.auto.Ranking.T_size;
import static br.edimarmanica.templatevariation.auto.Ranking.T_xpath;
import br.edimarmanica.templatevariation.auto.UnionRules;
import br.edimarmanica.templatevariation.auto.bean.Rule;
import br.edimarmanica.templatevariation.auto.load.LoadRule;
import br.edimarmanica.templatevariation.auto.similarity.DomainSimilarity;
import br.edimarmanica.templatevariation.auto.similarity.EntitySimilarity;
import br.edimarmanica.templatevariation.auto.similarity.LabelSimilarity;
import br.edimarmanica.templatevariation.auto.similarity.RuleSimilarity;
import br.edimarmanica.templatevariation.auto.similarity.SizeSimilarity;
import br.edimarmanica.templatevariation.auto.similarity.XPathSimilarity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
public class Thresholds {

    boolean append = false;

    public void findThresholds() {
        for (Dataset dataset : Dataset.values()) {
            System.out.println("Dataset: " + dataset);
            for (Domain domain : dataset.getDomains()) {
                System.out.println("\tDomain: " + domain);
                for (Site site : domain.getSites()) {
                    System.out.println("\t\tSite: " + site);

                    readTemplateManual(site);
                }
            }
        }
    }

    private void readTemplateManual(Site site) {
        try (Reader in = new FileReader(Paths.PATH_TEMPLATE_VARIATION_MANUAL + "/" + site.getPath() + "/result.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    treatLine(site, record.get("ATTRIBUTE"), record.get("RULE"), record.get("LABEL"), record.get("RECALL"), record.get("PRECISION"));
                }
            } catch (SiteWithoutThisAttribute ex) {
                Logger.getLogger(Thresholds.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Attribute toAttribute(Domain domain, String attribute) {
        for (Attribute attr : domain.getAttributes()) {
            if (attr.getAttributeID().equals(attribute)) {
                return attr;
            }
        }
        return null;
    }

    private void treatLine(Site site, String attribute, String rules, String labels, String recall, String precision) throws SiteWithoutThisAttribute {
        String partesRule[] = rules.split(General.SEPARADOR);

        if (partesRule.length > 1) {
            String partesLabel[] = labels.split(General.SEPARADOR);
            Attribute attr = toAttribute(site.getDomain(), attribute);

            for (int i = 1; i < partesRule.length; i++) {
                treatRule(site, attr, partesRule[i], partesLabel[i], recall, precision);
            }
        }
    }

    private void treatRule(Site site, Attribute attribute, String rule, String label, String recall, String precision) throws SiteWithoutThisAttribute {
        //MasterRuleInSite
        Integer masterRuleID = UnionRules.getMasterRuleManual(site, attribute);
        LoadRule load = new LoadRule(site, masterRuleID);
        Rule masterRule = load.loadRule();

        //ComplementarRule
        LoadRule loadc = new LoadRule(site, Integer.parseInt(rule.replaceAll("rule_", "").replaceAll(".csv", "")));
        Rule candidateComplementaryRule = loadc.loadRule();

        //MasterRulesInOtherSites
        Set<Rule> masterRulesInOtherSites = UnionRules.getMasterRulesInOtherSitesManual(site, attribute);

        RuleSimilarity xpathSim = new XPathSimilarity(masterRule, candidateComplementaryRule);
        RuleSimilarity labelSim = new LabelSimilarity(masterRule, candidateComplementaryRule);
        RuleSimilarity domainSim = new DomainSimilarity(masterRule, candidateComplementaryRule);
        RuleSimilarity entitySim = new EntitySimilarity(masterRulesInOtherSites, masterRule, candidateComplementaryRule);
        RuleSimilarity sizeSim = new SizeSimilarity(UnionRules.getNrPages(site), masterRule, candidateComplementaryRule);

        double xpathScore = xpathSim.score();
        double labelScore = labelSim.score();
        double domainScore = domainSim.score();
        double entityScore = entitySim.score();
        double sizeScore = sizeSim.score();
        double finalScore = T_xpath * xpathScore + T_label * labelScore + T_domain * domainScore + T_entity * entityScore + T_size * sizeScore;

        List<String> dataRecord = new ArrayList<>();
        dataRecord.add(site.getDomain().getDataset().getFolderName());
        dataRecord.add(site.getDomain().getFolderName());
        dataRecord.add(site.getFolderName());
        dataRecord.add(attribute.getAttributeID());
        dataRecord.add(masterRuleID + "");
        dataRecord.add(masterRule.getLabel());
        dataRecord.add(candidateComplementaryRule.getRuleID() + "");
        dataRecord.add(candidateComplementaryRule.getLabel());
        dataRecord.add(recall);
        dataRecord.add(precision);
        dataRecord.add(xpathScore + "");
        dataRecord.add(labelScore + "");
        dataRecord.add(domainScore + "");
        dataRecord.add(entityScore + "");
        dataRecord.add(sizeScore + "");
        dataRecord.add(finalScore + "");
        printRule(dataRecord);
    }

    private void printRule(List<String> dataRecord) {
        String[] header = {"DATASET", "DOMAIN", "SITE", "ATTRIBUTE", "MASTER_RULE_ID", "MASTER_RULE_LABEL", "COMP_RULE_ID", "COMP_RULE_LABEL", "RECALL", "PRECISION",
            "XPATH_SCORE", "LABEL_SCORE", "DOMAIN_SCORE", "ENTITY_SCORE", "SIZE_SCORE", "FINAL_SCORE"};


        File file = new File(Paths.PATH_TEMPLATE_VARIATION_AUTO + "/limiares.csv");
        CSVFormat format;
        if (append) {
            format = CSVFormat.EXCEL;
        } else {
            format = CSVFormat.EXCEL.withHeader(header);
        }

        try (Writer out = new FileWriter(file, append)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                csvFilePrinter.printRecord(dataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        }
        append = true;
    }

    public static void main(String[] args) {
        Thresholds th = new Thresholds();
        th.findThresholds();
    }
}
