/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness.tool;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.evaluate.EvaluateWEIR;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
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
public class ControllerWEIR extends Controller {

    public ControllerWEIR(Tela frame) {
        super(frame);
    }

    
    @Override
    protected Map<Attribute, String> loadAttributes(Site site) {
        Map<Attribute, String> attrs = new HashMap<>(); //<Attribute,pair(URL$_$Value) of first page>
        try (Reader in = new FileReader(Paths.PATH_BASE + site.getGroundTruthPath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    for (Attribute attr : site.getDomain().getAttributes()) {
                        if (attrs.containsKey(attr)) {
                            continue; //já achou valor para esse atributo
                        }

                        if (!record.isMapped(attr.getAttributeIDbyDataset())) {
                            //Não tem esse atributo no gabarito
                            continue;
                        }

                        if (!record.get(attr.getAttributeIDbyDataset()).trim().isEmpty()) {
                            attrs.put(attr, Paths.PATH_BASE + site.getDomain().getDataset().getFolderName() + "/" + record.get("url") + General.SEPARADOR + record.get(attr.getAttributeIDbyDataset()).trim());
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
        }

        return attrs;
    }
}
