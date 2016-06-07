/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.evaluate;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.bean.Mapping;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.Value;
import br.edimarmanica.weir.load.LoadMappings;
import br.edimarmanica.weir.util.Conjuntos;
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
public class Evaluate {

    private Domain domain;
    private Conjuntos<Value> util = new Conjuntos<>();

    public Evaluate(Domain domain) {
        this.domain = domain;
    }

    public void execute() {
        LoadMappings lm = new LoadMappings(domain);
        List<Mapping> maps = lm.getMappings();

        int i = 0;
        for (Attribute attr : domain.getAttributes()) {//para cada atributo
            //encontra o mapeamento com mais sites
            int mapID = getBetterMapping(attr, maps);
            for(Rule rule: maps.get(mapID).getRules()){
                Metrics metrics = getMetrics(rule, attr);
                
                if (i == 0){
                    printResults(mapID, rule, attr, metrics, false);
                }else{
                    printResults(mapID, rule, attr, metrics, true);
                }
                i++;
            }
        }
    }

    private Set<Value> getGabarito(Site site, Attribute attr) {
        Set<Value> values = new HashSet<>();

        try (Reader in = new FileReader(Paths.PATH_BASE + site.getGroundTruthPath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    if (!record.isMapped(attr.getAttributeIDbyDataset())) {
                        //System.out.println("Atributo " + attr.getAttributeID() + " não está no gabarito do Site "+site.getFolderName()+"!");
                        return new HashSet<>();
                    }
                    if (!record.get(attr.getAttributeIDbyDataset()).trim().isEmpty()) {
                        values.add(new Value(ValueNormalizer.normalize(record.get(attr.getAttributeIDbyDataset())), record.get("url")));
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values;
    }

    private int getBetterMapping(Attribute attr, List<Mapping> mappings) {

        int maxSites = -1;
        int maxSiteID = 0;
        for (int i = 0; i < mappings.size(); i++) {
            int nrSites = 0;
            for (Rule r : mappings.get(i).getRules()) {

                if (!util.intersection(getGabarito(r.getSite(), attr), r.getValues()).isEmpty()) {
                    nrSites++;
                }
            }

            if (nrSites > maxSites) {
                maxSites = nrSites;
                maxSiteID = i;
            }
        }
        return maxSiteID;
    }
    
    private Metrics getMetrics(Rule rule, Attribute attr){
        
         Set<Value> gabarito = getGabarito(rule.getSite(), attr);
         Set<Value> results = rule.getNotNullValues();
         
         if (results.isEmpty()){
             return new Metrics(0, 0, gabarito.size(), results.size(), 0);
         }

        Set<Value> intersection = new HashSet<>();
        intersection.addAll(results);
        intersection.retainAll(gabarito);

        double recall = (double) intersection.size() / gabarito.size();
        double precision = (double) intersection.size() / results.size();
        return new Metrics(recall, precision, gabarito.size(), results.size(), intersection.size());
    }
    
    private void printResults(int mapID, Rule rule, Attribute attribute, Metrics metrics, boolean append){
        String[] header = {"DATASET", "DOMAIN", "MAP_ID", "SITE", "ATTRIBUTE", "RULE", "RELEVANTS", "RETRIEVED", "RETRIEVED RELEVANTS", "RECALL", "PRECISION"};
        File file = new File(Paths.PATH_WEIR + "/mappings/" + rule.getSite().getDomain().getPath() +"/results.csv");

        CSVFormat format;
        if (append) {
            format = CSVFormat.EXCEL;
        } else {
            format = CSVFormat.EXCEL.withHeader(header);
        }

        try (Writer out = new FileWriter(file, append)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                List<String> dataRecord = new ArrayList<>();
                dataRecord.add(rule.getSite().getDomain().getDataset().getFolderName());
                dataRecord.add(rule.getSite().getDomain().getFolderName());
                dataRecord.add(mapID+"");
                dataRecord.add(rule.getSite().getFolderName());
                dataRecord.add(attribute.getAttributeID());
                dataRecord.add(rule.getRuleID()+"");
                dataRecord.add(metrics.getRelevants() + "");
                dataRecord.add(metrics.getRetrieved() + "");
                dataRecord.add(metrics.getRetrievedRelevants() + "");
                dataRecord.add(metrics.getRecall() + "");
                dataRecord.add(metrics.getPrecision() + "");
                csvFilePrinter.printRecord(dataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Evaluate eval = new Evaluate(br.edimarmanica.dataset.weir.Domain.BOOK);
        eval.execute();
    }
}

class Metrics {
    private double recall;
    private double precision;
    private int relevants;
    private int retrieved;
    private int retrievedRelevants;

    public Metrics(double recall, double precision, int relevants, int retrieved, int retrievedRelevants) {
        this.recall = recall;
        this.precision = precision;
        this.relevants = relevants;
        this.retrieved = retrieved;
        this.retrievedRelevants = retrievedRelevants;
    }

    public int getRelevants() {
        return relevants;
    }

    public int getRetrieved() {
        return retrieved;
    }

    public int getRetrievedRelevants() {
        return retrievedRelevants;
    }
   
    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }
    
    
}