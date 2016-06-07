/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.GroundTruth;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.trinity.intrasitemapping.auto.InvalidValue;
import br.edimarmanica.trinity.intrasitemapping.auto.Preprocessing;
import com.wcohen.ss.Jaccard;
import com.wcohen.ss.tokens.NGramTokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
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
public abstract class CheckAttributeNotFound {

    private Site site;
    private Attribute attribute;
    private List<List<Map<String, String>>> offsets = new ArrayList<>(); //dimensões {offset,regra,registro} --> <offset[0]<rule[0]<url,value>>>
    protected Jaccard sim;
    protected Map<String, String> groundTruth;

    public CheckAttributeNotFound(Site site, Attribute attribute) {
        this.site = site;
        this.attribute = attribute;
        
        NGramTokenizer tokenizer = new NGramTokenizer(3, 3, false, SimpleTokenizer.DEFAULT_TOKENIZER);
        sim = new Jaccard(tokenizer);
    }

    private void loadGabarito() throws SiteWithoutThisAttribute {
        GroundTruth gd = GroundTruth.getInstance(site, attribute);
        gd.load();
        groundTruth = gd.getGroundTruth();
    }

    private void readOffSets() {
        /**
         * Lendos os Run02.NR_SHARED_PAGES primeiros elementos de cada offset
         */
        File dir = new File(Paths.PATH_TRINITY + site.getPath() + "/offset");

        for (int nrOffset = 0; nrOffset < dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        }).length; nrOffset++) {
            List<Map<String, String>> offset = new ArrayList<>(); //cada arquivo é um offset

            try (Reader in = new FileReader(dir.getAbsoluteFile() + "/result_" + nrOffset + ".csv")) {
                try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL)) {
                    int nrRegistro = 0;
                    for (CSVRecord record : parser) {

                        for (int nrRegra = 0; nrRegra < record.size(); nrRegra++) {
                            String value;
                            try {
                                value = formatValue(Preprocessing.filter(record.get(nrRegra)));
                            } catch (InvalidValue ex) {
                                value = "";
                            }

                            if (nrRegistro == 0) {
                                Map<String, String> regra = new HashMap<>();
                                regra.put(record.get(0), value);
                                offset.add(regra);
                            } else {
                                offset.get(nrRegra).put(record.get(0), value);
                            }
                        }
                        nrRegistro++;
                    }
                }
                offsets.add(offset);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CheckAttributeNotFound.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CheckAttributeNotFound.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int getBestGroup(List<Map<String, String>> offset) {
        double maxScore = getGroupScore(offset.get(0));
        int indexBestGroup = 0;

        for (int i = 1; i < offset.size(); i++) {
            double score = getGroupScore(offset.get(i));

            if (score > maxScore) {
                maxScore = score;
                indexBestGroup = i;
            }
        }
        System.out.println("\t "+indexBestGroup+" = > " + maxScore);
        return indexBestGroup;
    }

    /**
     *
     * @param group a group of a offset (equivalente a uma regra)
     */
    protected abstract double getGroupScore(Map<String, String> group);

    public void execute() throws SiteWithoutThisAttribute {
        loadGabarito();
        readOffSets();

        List<Integer> bestGroupByOffset = new ArrayList<>();
        for (int i = 0; i < offsets.size(); i++) {
            System.out.println("Offset: "+i);
            bestGroupByOffset.add(getBestGroup(offsets.get(i)));
        }

        print(bestGroupByOffset);
    }

    private void print(List<Integer> bestGroupByOffset) {
        System.out.println("site;attribute;offset;group;url;real value; expected value");
        for (int i = 0; i < bestGroupByOffset.size(); i++) {
            int group = bestGroupByOffset.get(i);
            for (String url : offsets.get(i).get(group).keySet()) {
                System.out.println(site + ";" + attribute + ";" + i + ";" + group + ";" + url + ";" + offsets.get(i).get(group).get(url) + ";" + groundTruth.get(formatGroundTruthURL(url)));
            }
        }
    }
    
    protected abstract String formatGroundTruthURL(String url);
    protected abstract String formatValue(String value);
}
