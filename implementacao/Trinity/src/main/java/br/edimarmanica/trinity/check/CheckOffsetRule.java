/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class CheckOffsetRule {

    private Site site;
    private int indexOffset;
    private int indexRule;
    private int nrRegistros;

    public CheckOffsetRule(Site site, int indexOffset, int indexRule, int nrRegistros) {
        this.site = site;
        this.indexOffset = indexOffset;
        this.indexRule = indexRule;
        this.nrRegistros = nrRegistros;
    }

    public void execute() {

        File dir = new File(Paths.PATH_TRINITY + site.getPath() + "/offset");

        List<List<String>> offset = new ArrayList<>(); //cada arquivo é um offset

        try (Reader in = new FileReader(dir.getAbsoluteFile() + "/result_" + indexOffset + ".csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL)) {
                int nrRegistro = 0;
                for (CSVRecord record : parser) {
                    if (nrRegistro >= nrRegistros) {
                        break;
                    }

                    System.out.println(record.get(0) + ";" + record.get(indexRule));
                    nrRegistro++;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CheckOffsetRule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CheckOffsetRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.swde.book.Site.AMAZON;
        int indexOffset = 0;
        int indexRule = 40;
        int nrRegistros = 10;
        CheckOffsetRule check = new CheckOffsetRule(site, indexOffset, indexRule, nrRegistros);
        check.execute();
    }

}
