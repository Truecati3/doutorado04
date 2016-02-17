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
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author edimar
 */
public class EvaluateSWDE {

    private Site site;
    private Map<String, Map<Integer, String>> myResults = new HashMap<>();//<RuleName,Map<PageID,Value>>
    private Map<String, String> labels = new HashMap<>(); //<RuleName,Label>

    public EvaluateSWDE(Site site) {
        this.site = site;
    }

    /**
     *
     * @param attr
     * @return Map<URL, Value>
     */
    private void loadMyResults() {

        File dir = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values");
        System.out.println("DIR:" + dir.getAbsolutePath());
        for (File rule : dir.listFiles()) {

            Map<Integer, String> values = new HashMap<>();
            try (Reader in = new FileReader(rule.getAbsolutePath())) {
                try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                    for (CSVRecord record : parser) {
                        values.put(Integer.parseInt(record.get("URL").replaceAll(".*" + site.getPath() + "/", "").replaceAll(".htm", "")), formatGroundTruthValue(record.get("EXTRACTED VALUE")));
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EvaluateSWDE.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EvaluateSWDE.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(EvaluateSWDE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EvaluateSWDE.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Map<Integer, String> loadGroundTruth(Site site, Attribute attribute) throws SiteWithoutThisAttribute {
        Map<Integer, String> values = new HashMap<>();

        try (Reader in = new FileReader(Paths.PATH_BASE + site.getGroundTruthPath(attribute))) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.MYSQL)) {
                int i = 0;
                for (CSVRecord record : parser) {
                    if (i < 2) {
                        i++;
                        continue; //ignore os cabeçalhos
                    }

                    String url = record.get(0);
                    int nrValues = Integer.parseInt(record.get(1));
                    if (nrValues == 0) {
                        continue;
                    }
                    String value = "";
                    for (int j = 0; j < nrValues; j++) {
                        value += General.SEPARADOR + formatGroundTruthValue(record.get(2 + j));
                    }
                    value = value.replaceFirst(General.SEPARADOR, "");

                    values.put(Integer.parseInt(url), nrValues + General.SEPARADOR + value);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EvaluateSWDE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EvaluateSWDE.class.getName()).log(Level.SEVERE, null, ex);
        }

        return values;
    }
    
    public static String formatGroundTruthValue(String value){
        /*return value.replaceAll("&#039;", "'")
                .replaceAll("&amp;", "&")
                .replaceAll("&quot;", "\"")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&#46;", ".")
                .replaceAll("&#45;", "-")
                .replaceAll("&#40;", "(")
                .replaceAll("&#41;", ")")
                .replaceAll("&reg;", "®")
                .replaceAll("&#34;", "\"")
                .replaceAll("&#47;", "/")
                .replaceAll("&#43;", "+")
                .trim();*/
        
         return StringEscapeUtils.unescapeHtml(value)
                 .replaceAll(" ", " ")
                 .replaceAll("\\\\", "")
                 .replaceAll("\"", "")
                 .replaceAll("\\s+", " ")
                 .replaceAll("[^(a-zA-Z)|\\d|\\.]", ""); //só deixa números, letras e o ponto
    }

    /**
     *
     * @param groundtruth o gabarito para um atributo X
     * @param ruleValues os valores extraídos por uma regra Y
     * @return o conjunto de paǵinas cujo valor extraído pela regra casa com o
     * valor do gabarito
     */
    private Set<Integer> getIntersection(Map<Integer, String> groundtruth, Map<Integer, String> ruleValues) {
        Set<Integer> pageIds = new HashSet<>();
        for (Integer pageId : groundtruth.keySet()) {

            /**
             * Tratamento que no gabarito atributo pode ter multivalues. Nesse
             * caso se o valor extraído for um deles é ok
             */
            String partes[] = groundtruth.get(pageId).split(General.SEPARADOR);
            int nrValues = Integer.parseInt(partes[0]);
            boolean match = false;
            for (int i = 0; i < nrValues; i++) {
                if (ruleValues.containsKey(pageId) && ruleValues.get(pageId).equals(partes[1 + i])) {
                    match = true;
                    break;
                }
            }
            if (match) {
                pageIds.add(pageId);
            }
        }
        return pageIds;
    }

    private void printMetrics(Attribute attribute, boolean append) throws SiteWithoutThisAttribute {
        Map<Integer, String> groundtruth = loadGroundTruth(site, attribute);
        
        if (groundtruth.isEmpty()){
            return;
        }

        double maxRecall = 0;
        double maxPrecision = 0;
        double maxF1 = 0;
        int maxRetrieved = 0;
        int maxRelevantsRetrieved = 0;
        String maxRule = null;
        Map<Integer, String> maxExtractValues = new HashMap<>();
        Set<Integer> maxIntersection = new HashSet<>();

        for (String rule : myResults.keySet()) {//para cada regra

            Set<Integer> intersection = getIntersection(groundtruth, myResults.get(rule));

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
                maxIntersection = intersection;
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
            Logger.getLogger(EvaluateSWDE.class.getName()).log(Level.SEVERE, null, ex);
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
            for (Integer rel : groundtruth.keySet()) {
                if (!maxIntersection.contains(rel)) {
                    bw.write("Faltando: [" + groundtruth.get(rel) + "] na página: " + rel);
                    bw.newLine();
                }
            }
            bw.write("--------------------------------------------\n");
            bw.write("Irrelevantes recuperados (Problema de precision):\n");
            for (Integer ret : maxExtractValues.keySet()) {
                if (!maxIntersection.contains(ret)) {
                    bw.write("Faltando: [" + maxExtractValues.get(ret) + "] na página: " + ret);
                    bw.newLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(EvaluateSWDE.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {

        //edition não pega pq identifica como label (template)
      /*  Domain domain = br.edimarmanica.dataset.swde.Domain.AUTO;
         for (Site site : domain.getSites()) {
         System.out.println("Site: " + site);
         EvaluateSWDE eval = new EvaluateSWDE(site);
         eval.printMetrics();
         }*/

        EvaluateSWDE eval = new EvaluateSWDE(br.edimarmanica.dataset.swde.movie.Site.HOLLYWOOD);
        eval.printMetrics();
    }
}
