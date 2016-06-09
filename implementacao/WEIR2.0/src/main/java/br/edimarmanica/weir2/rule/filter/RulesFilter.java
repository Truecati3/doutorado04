/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.rule.filter;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.IntrasiteExtraction;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Formatter;
import br.edimarmanica.weir2.rule.Loader;
import br.edimarmanica.weir2.rule.type.RulesDataTypeController;
import br.edimarmanica.weir2.util.SiteInformation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class RulesFilter {

    private Site site;
    private int nrPages;
    private List<String> remainingRules = new ArrayList<>();
    private List<Integer> remainingRulesPageValuesHashCode = new ArrayList<>();

    public RulesFilter(Site site, int nrPages) {
        this.site = site;
        this.nrPages = nrPages;
    }

    public void filter() {
        nullValuesFilter();
        sameValuesFilter();
        persiste();
    }

    /**
     * remove regras com percentual de valores nulos maior que o limiar
     */
    private void nullValuesFilter() {
        File dirInput = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values");

        if (General.DEBUG) {
            System.out.println("\t\t\tNull Values Filter [Regras: " + dirInput.listFiles().length + "]");
        }

        for (File rule : dirInput.listFiles()) {
            Map<String, String> pageValues = Loader.loadPageValues(rule, true);
            if ((nrPages - pageValues.size()) * 100 / nrPages <= IntrasiteExtraction.PR_NULL_VALUES) {
                remainingRules.add(rule.getPath());
                //Maps com os mesmo valores de chave => valor tem os mesmos  hashcodes
                remainingRulesPageValuesHashCode.add(pageValues.hashCode());
            }
        }
    }

    private void sameValuesFilter() {
        if (General.DEBUG) {
            System.out.println("\t\t\tStarting Same Values Filter [regras restantes: " + remainingRules.size() + "]");
        }

        for (int i = 0; i < remainingRules.size(); i++) {
            for (int j = remainingRules.size() - 1; j > i; j--) {
                //mesmo hash code significa mesmo conjunto chave ==> valor
                if (remainingRulesPageValuesHashCode.get(i).intValue() == remainingRulesPageValuesHashCode.get(j).intValue()) {
                    remainingRules.remove(j);
                    remainingRulesPageValuesHashCode.remove(j);
                }
            }
        }

        if (General.DEBUG) {
            System.out.println("\t\t\tEnding Same Values Filter [regras restantes: " + remainingRules.size() + "]");
        }
    }

    private void persiste() {
        File dirOutput = new File(Paths.PATH_WEIR_V2 + "/" + site.getPath());
        dirOutput.mkdirs();

        File file = new File(dirOutput.getAbsolutePath() + "/filter.csv");
        String[] HEADER = {"RULE"};
        CSVFormat format = CSVFormat.EXCEL.withHeader(HEADER);

        try (Writer out = new FileWriter(file)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                for (String rule : remainingRules) {
                    List<String> dataRecord = new ArrayList<>();
                    dataRecord.add(rule);
                    csvFilePrinter.printRecord(dataRecord);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RulesFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<String> loadFilteredRules(Site site) {
        List<String> remainingRules = new ArrayList<>();

        try (Reader in = new FileReader(new File(Paths.PATH_WEIR_V2 + "/" + site.getPath() + "/filter.csv"))) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) { //para cada value
                    remainingRules.add(record.get("RULE"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RulesDataTypeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RulesDataTypeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return remainingRules;
    }

    public static void main(String[] args) {
        General.DEBUG = true;

        for (Dataset dataset : Dataset.values()) {
            System.out.println("Dataset: " + dataset);
            for (Domain domain : dataset.getDomains()) {
                System.out.println("\tDomain: " + domain);
                for (Site site : domain.getSites()) {

                    System.out.println("\t\tSite: " + site);
                    RulesFilter rf = new RulesFilter(site, SiteInformation.nrPages(site));
                    rf.filter();
                }
            }
        }
    }

}
