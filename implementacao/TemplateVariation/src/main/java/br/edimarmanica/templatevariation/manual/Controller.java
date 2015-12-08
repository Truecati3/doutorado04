/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.intrasite.evaluate.EvaluateWEIR;
import br.edimarmanica.intrasite.evaluate.MergeResults;
import br.edimarmanica.intrasite.evaluate.SiteWithoutThisAttribute;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.Value;
import br.edimarmanica.weir.util.ValueNormalizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
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
public class Controller {

    private Site site;
    RulesUnionManual rulesUnion;
    boolean append = false;

    public Controller(Site site) {
        this.site = site;

        rulesUnion = new RulesUnionManual(site);
    }

    public void execute() {
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/result.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {

                for (CSVRecord record : parser) {
                    try {
                        execute(record.get("RULE"), record.get("ATTRIBUTE"));
                    } catch (SiteWithoutThisAttribute | NumberFormatException ex) {
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void execute(String masterRuleID, String attributeID) throws SiteWithoutThisAttribute, NumberFormatException {
        Attribute attribute = null;
        for (Attribute at : site.getDomain().getAttributes()) {
            if (at.getAttributeID().equals(attributeID)) {
                attribute = at;
                break;
            }
        }

        Rule masterRule = rulesUnion.getRule(new Integer(masterRuleID.replaceAll("rule_", "").replaceAll("\\.csv", "")));
        Rule complementaryRule = rulesUnion.getComplementaryRule(masterRule, attribute);
        print(attribute, masterRule, complementaryRule);

    }

    public void print(Attribute attribute, Rule masterRule, Rule complementaryRule) {
        String[] header = {"DATASET", "DOMAIN", "SITE", "ATTRIBUTE", "MASTER_RULE_ID", "MASTER_RULE_LABEL", "COMPLEMENTARY_RULE_ID", "COMPLEMENTARY_RULE_LABEL", "RELEVANTS", "RETRIEVED", "RETRIEVED RELEVANTS", "RECALL", "PRECISION"};
        File dir = new File(Paths.PATH_TEMPLATE_VARIATION + "/" + site.getPath());
        dir.mkdirs();
        CSVFormat format;
        if (append) {
            format = CSVFormat.EXCEL;
        } else {

            format = CSVFormat.EXCEL.withHeader(header);
        }

        try (Writer out = new FileWriter(dir.getAbsolutePath() + "/result.csv", append)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                csvFilePrinter.printRecord(getDataRecord(attribute, masterRule, complementaryRule));
            }
        } catch (IOException ex) {
            Logger.getLogger(MergeResults.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        append = true;
    }

    private List<String> getDataRecord(Attribute attribute, Rule masterRule, Rule complementaryRule) {
        /**
         * compute metrics *
         */
        Set<String> groundTruth = null;
        try {
            groundTruth = EvaluateWEIR.loadGroundTruth(site, attribute);
        } catch (SiteWithoutThisAttribute ex) {
            //não vai entrar
        }
        Set<String> myResults = getMyResults(masterRule, complementaryRule);

        Set<String> intersection = new HashSet<>();
        intersection.addAll(myResults);
        intersection.retainAll(groundTruth);

        /*for (String st : myResults) {
         if (!groundTruth.contains(st)) {
         System.out.println("Problema de precision: " + st);
         }
         }

         for (String st : groundTruth) {
         if (!myResults.contains(st)) {
         System.out.println("Problema de recall: " + st);
         }
         }*/

        double recall = (double) intersection.size() / groundTruth.size();
        double precision = (double) intersection.size() / myResults.size();
        double F1 = (2 * (recall * precision)) / (recall + precision);
        int relevants = groundTruth.size();
        int retrieved = myResults.size();
        int relevantsRetrieved = intersection.size();

        /**
         * montando linha de saída
         */
        List<String> dataRecord = new ArrayList<>();
        dataRecord.add(site.getDomain().getDataset().getFolderName());
        dataRecord.add(site.getDomain().getFolderName());
        dataRecord.add(site.getFolderName());
        dataRecord.add(attribute.getAttributeID());
        dataRecord.add(masterRule.getRuleID() + "");
        dataRecord.add(masterRule.getLabel() + "");
        if (complementaryRule == null) {
            dataRecord.add("-");
            dataRecord.add("-");
        } else {
            dataRecord.add(complementaryRule.getRuleID() + "");
            dataRecord.add(complementaryRule.getLabel() + "");
        }
        dataRecord.add(relevants + "");
        dataRecord.add(retrieved + "");
        dataRecord.add(relevantsRetrieved + "");
        dataRecord.add(recall + "");
        dataRecord.add(precision + "");
        return dataRecord;
    }

    private Set<String> getMyResults(Rule masterRule, Rule complementaryRule) {
        Set<String> results = getMyResults(site, masterRule.getRuleID());

        if (complementaryRule != null) {
            results.addAll(getMyResults(site, complementaryRule.getRuleID()));
        }

        return results;
    }

    public static Set<String> getMyResults(Site site, int ruleID) {
        Set<String> results = new HashSet<>();

        File file = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values/rule_" + ruleID + ".csv");
        try (Reader in = new FileReader(file)) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    results.add(record.get("URL").replaceAll("/sdd/edimar/Desktop/doutorado/doutorado04/bases/", "/media/Dados/bases/") + General.SEPARADOR + record.get("EXTRACTED VALUE"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        }
        return results;
    }

    public static void main(String[] args) {
        Domain domain = br.edimarmanica.dataset.weir.Domain.SOCCER;

        for (Site site : domain.getSites()) {
            Controller c = new Controller(site);
            c.execute();
        }
    }
}
