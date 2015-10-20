/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness.evaluate;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
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
public class EvaluateWEIR {

    private Site site;

    public EvaluateWEIR(Site site) {
        this.site = site;
    }

    private Set<String> loadMyResults(Attribute attribute) {
        Set<String> values = new HashSet<>();

        try (Reader in = new FileReader(Paths.PATH_EXPRESSIVENESS + site.getPath() + "/extracted_values/" + attribute.getAttributeID() + ".csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    values.add(record.get("URL") + General.SEPARADOR + record.get("EXTRACTED VALUE"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values;
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

        Set<String> results = loadMyResults(attribute);

        Set<String> intersection = new HashSet<>();
        intersection.addAll(results);
        intersection.retainAll(groundtruth);

        double recall = (double) intersection.size() / groundtruth.size();
        double precision = (double) intersection.size() / results.size();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = formatter.format(date);

        /**
         * ********************** results ******************
         */
        String[] header = {"DATASET", "DOMAIN", "SITE", "ATTRIBUTE", "RELEVANTS", "RETRIEVED", "RETRIEVED RELEVANTS", "RECALL", "PRECISION", "DATE"};
        File file = new File(Paths.PATH_EXPRESSIVENESS + "/" + site.getPath() + "/result.csv");
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
                studentDataRecord.add(groundtruth.size() + "");
                studentDataRecord.add(results.size() + "");
                studentDataRecord.add(intersection.size() + "");
                studentDataRecord.add(recall + "");
                studentDataRecord.add(precision + "");
                studentDataRecord.add(formattedDate);
                csvFilePrinter.printRecord(studentDataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * ********************** log ******************
         */
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.PATH_EXPRESSIVENESS + "/" + site.getPath() + "/log.txt", true))) {
            bw.write("\n\n*************" + formattedDate + " - " + attribute.getAttributeID() + "********************************\n");
            bw.write("dataset;" + site.getDomain().getDataset().getFolderName() + ";domain;" + site.getDomain().getFolderName()
                    + ";site;" + site.getFolderName() + ";attribute;" + attribute.getAttributeID() + ";relevantes;" + groundtruth.size() + ";recuperados;" + results.size() + ";relevantes recuperados; " + intersection.size() + ";recall;" + recall + ";precision;" + precision + ";date;" + formattedDate);
            bw.newLine();
            bw.write("--------------------------------------------\n");
            bw.write("Relevantes não recuperados (Problema de recall):\n");
            for (String rel : groundtruth) {
                if (!results.contains(rel)) {
                    String[] partes = rel.split(General.SEPARADOR);
                    bw.write("Faltando: [" + partes[1] + "] na página: " + partes[0]);
                    bw.newLine();
                }
            }
            bw.write("--------------------------------------------\n");
            bw.write("Irrelevantes recuperados (Problema de precision):\n");
            for (String rel : results) {
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
        EvaluateWEIR eval = new EvaluateWEIR(br.edimarmanica.dataset.weir.finance.Site.BIGCHARTS);
        //eval.printMetrics(br.edimarmanica.dataset.weir.book.Attribute.AUTHOR);
        eval.printMetrics();
    }
}
