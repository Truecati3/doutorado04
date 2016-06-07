/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.intrasitemapping.manual;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Formatter;
import br.edimarmanica.metrics.GroundTruth;
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.trinity.intrasitemapping.auto.InvalidValue;
import br.edimarmanica.trinity.intrasitemapping.auto.Preprocessing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class Mapping {

    private Site site;
    private List<Map<String, String>> offset;

    public Mapping(Site site, File offsetFile) {
        this.site = site;

        this.offset = readOffset(offsetFile);
    }

    private Map<String, String> getGroundTruth(Attribute attribute) throws SiteWithoutThisAttribute {
        GroundTruth gt = GroundTruth.getInstance(site, attribute);
        gt.load();
        
        return gt.getGroundTruth();
    }

    private List<Map<String, String>> readOffset(File offsetFile) {
        List<Map<String, String>> offset = new ArrayList<>(); //cada arquivo é um offset

        try (Reader in = new FileReader(offsetFile)) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL)) {
                int nrRegistro = 0;
                for (CSVRecord record : parser) {

                    for (int nrRegra = 0; nrRegra < record.size(); nrRegra++) {
                        String value;
                        try {
                            value = Formatter.formatValue(Preprocessing.filter(record.get(nrRegra)));
                        } catch (InvalidValue ex) {
                            value = "";
                        }

                        if (nrRegistro == 0) {
                            Map<String, String> regra = new HashMap<>();
                            regra.put(Formatter.formatURL(record.get(0)), value);
                            offset.add(regra);
                        } else {
                            offset.get(nrRegra).put(Formatter.formatURL(record.get(0)), value);
                        }
                    }
                    nrRegistro++;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Mapping.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Mapping.class.getName()).log(Level.SEVERE, null, ex);
        }

        return offset;
    }

    /**
     *
     * @return Map<Attribute,GroupID>
     */
    public Map<Attribute, Integer> getBestGroups() {
        Map<Attribute, Integer> maps = new HashMap<>();

        for (Attribute attr : site.getDomain().getAttributes()) {
            try {
                Map<String, String> groundTruth = getGroundTruth(attr);
                
                if (groundTruth.isEmpty()){
                    continue;//não tem gabarito para esse atributo
                }
                
                maps.put(attr, getBestGroup(groundTruth));
            } catch (SiteWithoutThisAttribute ex) {
                maps.put(attr, -1);
            }
        }
        return maps;
    }

    /**
     * @return the group with the highest F1 or -1 if the F1 <= 0
     */
    private Integer getBestGroup(Map<String, String> groundTruth) {
        double maxF1 = -1;
        int indexMaxGroup = -1;
        int i = 0;
        for (Map<String, String> group : offset) {
            double f1 = getF1(group, groundTruth);
            if (f1 > maxF1) {
                maxF1 = f1;
                indexMaxGroup = i;
            }
            i++;
        }

        if (maxF1 <= 0) {
            return -1;
        }

        return indexMaxGroup;
    }

    private double getF1(Map<String, String> group, Map<String, String> groundTruth) {
        RuleMetrics metrics = RuleMetrics.getInstance(site, group, groundTruth);
        metrics.computeMetrics();
        return metrics.getF1();
    }
}
