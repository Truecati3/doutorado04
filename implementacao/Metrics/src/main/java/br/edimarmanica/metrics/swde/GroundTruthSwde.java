/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.swde;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.GroundTruth;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.Formatter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class GroundTruthSwde extends GroundTruth {


    public GroundTruthSwde(Site site, Attribute attribute) {
        super(site, attribute);
    }

    /**
     * Tratamento especial para multivalores.
     * GroundTruth => Map<PageID, NrValues + Values>
     * @throws SiteWithoutThisAttribute 
     */
    @Override
    public void load() throws SiteWithoutThisAttribute {

        try (Reader in = new FileReader(Paths.PATH_BASE + site.getGroundTruthPath(attribute))) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.MYSQL)) {
                int i = 0;
                for (CSVRecord record : parser) {
                    if (i < 2) {
                        i++;
                        continue; //ignore os cabeçalhos
                    }

                    String url = Formatter.formatURL(record.get(0));
                    int nrValues = Integer.parseInt(record.get(1));
                    if (nrValues == 0 || (nrValues == 1 && Formatter.formatValue(record.get(2)).isEmpty())) {
                        continue;
                    }
                    String value = "";
                    for (int j = 0; j < nrValues; j++) {
                        value += General.SEPARADOR + Formatter.formatValue(record.get(2 + j));
                    }
                    value = value.replaceFirst(General.SEPARADOR, "");

                    groundTruth.put(url, nrValues + General.SEPARADOR + value);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GroundTruthSwde.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GroundTruthSwde.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
