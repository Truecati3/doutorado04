/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.evaluate;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
public class EvaluateWEIR {

    private Site site;
    private Map<String, Set<String>> myResults = new HashMap<>();//<RuleName,Set<ExtractedValues>>
    private Map<String, String> labels = new HashMap<>(); //<RuleName,Label>

    public EvaluateWEIR(Site site) {
        this.site = site;
    }

    private void loadMyResults() {

        File dir = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values");
        for (File rule : dir.listFiles()) {

            Set<String> values = new HashSet<>();
            try (Reader in = new FileReader(rule.getAbsolutePath())) {
                try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                    for (CSVRecord record : parser) {
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
    }

    private void loadLabels() {
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/rule_info.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    labels.put("rule_" + record.get("ID") + ".csv", record.get("LABEL"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Set<String> loadGroundTruth(Attribute attribute) throws SiteWithoutThisAttribute {
        Set<String> values = new HashSet<>();

        try (Reader in = new FileReader(Paths.PATH_BASE + site.getGroundTruthPath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    if (!record.isMapped(attribute.getAttributeIDbyDataset())) {
                        throw new SiteWithoutThisAttribute(attribute.getAttributeID(), site.getFolderName());
                    }
                    if (!record.get(attribute.getAttributeIDbyDataset()).trim().isEmpty()) {
                        values.add(Paths.PATH_BASE + site.getDomain().getDataset().getFolderName() + "/" + record.get("url") + General.SEPARADOR + record.get(attribute.getAttributeIDbyDataset()));
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        }

        return values;
    }

    private void printMetrics(Attribute attribute, boolean append) throws SiteWithoutThisAttribute {
        Set<String> groundtruth = loadGroundTruth(attribute);

        double maxRecall = 0;
        double maxPrecision = 0;
        double maxF1 = 0;
        int maxRetrieved = 0;
        int maxRelevantsRetrieved = 0;
        String maxRule = null;
        Set<String> maxExtractValues = new HashSet<>();

        for (String rule : myResults.keySet()) {

            Set<String> intersection = new HashSet<>();
            intersection.addAll(myResults.get(rule));
            intersection.retainAll(groundtruth);

            double recall = (double) intersection.size() / groundtruth.size();
            double precision = (double) intersection.size() / myResults.get(rule).size();

            if (recall == 0 || precision == 0) {
                continue;
            }
            double F1 = (2 * (recall * precision)) / (recall + precision);

            if (F1 > maxF1) {
                maxF1 = F1;
                maxRecall = recall;
                maxPrecision = precision;
                maxRule = rule;

                maxRetrieved = myResults.get(rule).size();
                maxRelevantsRetrieved = intersection.size();
                maxExtractValues = myResults.get(rule);
            }
        }

        if (maxRecall == 0) {
            maxRule = "Attribute not found";
        }

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = formatter.format(date);

        /**
         * ********************** results ******************
         */
        String[] header = {"DATASET", "DOMAIN", "SITE", "ATTRIBUTE", "RULE", "LABEL", "RELEVANTS", "RETRIEVED", "RETRIEVED RELEVANTS", "RECALL", "PRECISION", "DATE"};
        File file = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/result.csv");
        CSVFormat format;
        if (append) {
            format = CSVFormat.EXCEL;
        } else {
            format = CSVFormat.EXCEL.withHeader(header);
        }

        try (Writer out = new FileWriter(file, append)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                List<String> studentDataRecord = new ArrayList<>();
                studentDataRecord.add(site.getDomain().getDataset().getFolderName());
                studentDataRecord.add(site.getDomain().getFolderName());
                studentDataRecord.add(site.getFolderName());
                studentDataRecord.add(attribute.getAttributeID());
                studentDataRecord.add(maxRule);
                studentDataRecord.add(labels.get(maxRule));
                studentDataRecord.add(groundtruth.size() + "");
                studentDataRecord.add(maxRetrieved + "");
                studentDataRecord.add(maxRelevantsRetrieved + "");
                studentDataRecord.add(maxRecall + "");
                studentDataRecord.add(maxPrecision + "");
                studentDataRecord.add(formattedDate);
                csvFilePrinter.printRecord(studentDataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * ********************** log ******************
         */
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.PATH_INTRASITE + "/" + site.getPath() + "/log.txt", append))) {
            bw.write("\n\n*************" + formattedDate + " - " + attribute.getAttributeID() + "********************************\n");
            bw.write("dataset;" + site.getDomain().getDataset().getFolderName() + ";domain;" + site.getDomain().getFolderName()
                    + ";site;" + site.getFolderName() + ";attribute;"
                    + attribute.getAttributeID() + ";relevantes;"
                    + groundtruth.size() + ";recuperados;" + maxRetrieved + ";relevantes recuperados; " + maxRelevantsRetrieved + ";recall;" + maxRecall + ";precision;" + maxPrecision + ";date;" + formattedDate);
            bw.newLine();
            bw.write("--------------------------------------------\n");
            bw.write("Relevantes não recuperados (Problema de recall):\n");
            for (String rel : groundtruth) {
                if (!maxExtractValues.contains(rel)) {
                    String[] partes = rel.split(General.SEPARADOR);
                    bw.write("Faltando: [" + partes[1] + "] na página: " + partes[0]);
                    bw.newLine();
                }
            }
            bw.write("--------------------------------------------\n");
            bw.write("Irrelevantes recuperados (Problema de precision):\n");
            for (String rel : maxExtractValues) {
                if (!groundtruth.contains(rel)) {
                    String[] partes = rel.split(General.SEPARADOR);
                    bw.write("Faltando: [" + partes[1] + "] na página: " + partes[0]);
                    bw.newLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printMetrics() {
        loadMyResults();
        loadLabels();

        boolean append = false;
        for (Attribute attr : site.getDomain().getAttributes()) {
            try {
                printMetrics(attr, append);
                append = true;
            } catch (SiteWithoutThisAttribute ex) {
                //Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public static void main(String[] args) {
        //edition não pega pq identifica como label (template)
        Domain domain = br.edimarmanica.dataset.weir.Domain.FINANCE;
        for (Site site : domain.getSites()) {
            EvaluateWEIR eval = new EvaluateWEIR(site);
            eval.printMetrics();
        }
    }
}
