/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.weir;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.ValueFormatter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class GroundTruthWeir {

    private Site site;
    private Attribute attribute;
    private ValueFormatter formatter = new ValueFormatterWeir();
    private Set<String> groundTruth = new HashSet<>();

    public GroundTruthWeir(Site site, Attribute attribute) {
        this.site = site;
        this.attribute = attribute;
    }

    public void load() throws SiteWithoutThisAttribute {
        try (Reader in = new FileReader(Paths.PATH_BASE + site.getGroundTruthPath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    if (!record.isMapped(attribute.getAttributeIDbyDataset())) {
                        throw new SiteWithoutThisAttribute(attribute.getAttributeID(), site.getFolderName());
                    }
                    if (!record.get(attribute.getAttributeIDbyDataset()).trim().isEmpty()) {
                        groundTruth.add(record.get("url") + General.SEPARADOR + formatter.format(record.get(attribute.getAttributeIDbyDataset())));
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GroundTruthWeir.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GroundTruthWeir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Set<String> getGroundTruth() {
        return groundTruth;
    }
}
