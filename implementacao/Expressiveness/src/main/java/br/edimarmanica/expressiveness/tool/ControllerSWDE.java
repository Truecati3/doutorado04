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
public class ControllerSWDE extends Controller {

    public ControllerSWDE(Tela frame) {
        super(frame);
    }

    @Override
    protected Map<Attribute, String> loadAttributes(Site site) {
        Map<Attribute, String> attrs = new HashMap<>(); //<Attribute,pair(URL$_$Value) of first page>
        for (Attribute attr : site.getDomain().getAttributes()) {

            try (Reader in = new FileReader(Paths.PATH_BASE + site.getGroundTruthPath(attr))) {
                try (CSVParser parser = new CSVParser(in, CSVFormat.MYSQL.withHeader())) {
                    int i = 0;
                    for (CSVRecord record : parser) {
                        if (i == 0) { //a primeira linha de dados (segunda considerando cabeçalhos) é de estatísticas
                            if (record.get(1).equals("0")) {
                                //Não tem esse atributo no gabarito
                                break;
                            }
                            i++;
                            continue;//essa linha só tem estatísticas
                        }
                        //if (record.get(1).equals("2")) {
                          //  throw new UnsupportedOperationException("Erro: atributo multivalorado");
                        //}

                        if (!record.get(2).equals("<NULL>")) {
                            attrs.put(attr, Paths.PATH_BASE + site.getPath() + "/" + record.get(0) + ".htm" + General.SEPARADOR + record.get(2).replaceAll("&nbsp;", ""));
                            break; //só pega a primeira linha de dados não nulos para anotação
                        }

                        i++;
                    }
                } 
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return attrs;
    }

    public static void main(String[] args) {
        ControllerSWDE c = new ControllerSWDE(null);
        Map<Attribute, String> map = c.loadAttributes(br.edimarmanica.dataset.swde.auto.Site.AOL);
        for (Attribute attr : map.keySet()) {
            System.out.println("Att: " + attr.getAttributeID() + " - " + map.get(attr));
        }
    }
}
