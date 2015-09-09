/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness.tool;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Configuration;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.evaluate.EvaluateWEIR;
import br.edimarmanica.expressiveness.extract.QueryNeo4J;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class Controller {

    private final String LOCAL_SEPARATOR = "!_!";
    private Tela frame;
    private static Set<String> urlsOpened = new HashSet<>();
    private QueryNeo4J query;

    public Controller(Tela frame) {
        this.frame = frame;
    }

    public Controller() {
    }

    private Map<Attribute, String> loadAttributes(Site site) {
        Map<Attribute, String> attrs = new HashMap<>(); //<Attribute,pair(URL$_$Value) of first page>
        try (Reader in = new FileReader(Configuration.PATH_BASE + site.getGroundTruthPath())) {
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
                            attrs.put(attr, Configuration.PATH_BASE + site.getDomain().getDataset().getFolderName() + "/" + record.get("url") + LOCAL_SEPARATOR + record.get(attr.getAttributeIDbyDataset()).trim());
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

    private Set<String> getAttributeInfo(Site site) {
        Set<String> attributesInfo = new HashSet<>();

        /**
         * verificando os atributos no gabarito e carregando os valores
         */
        Map<Attribute, String> attrs = loadAttributes(site);


        for (Attribute attr : attrs.keySet()) {
            String[] partes = attrs.get(attr).split(LOCAL_SEPARATOR);//URL - value
            openBrowser(partes[0]);

            List<String> uniquePathsLabel;
            String label;
            do {
                /**
                 * Encontrando o label
                 */
                label = JOptionPane.showInputDialog(frame, "Informe o label para o Atributo: '" + attr.getAttributeID() + "' com valor: '" + partes[1] + "'").trim();
                label = label.replaceAll("'", "\\\\'");


                /**
                 * Encontrando os possíveis Unique Paths do Label *
                 */
                uniquePathsLabel = getUniquePaths(label, partes[0]);

                if (uniquePathsLabel.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Ops! Confira o label!");
                }
            } while (uniquePathsLabel.isEmpty());

            /**
             * Selecionando um Unique Path do Label*
             */
            String uniquePathLabel = selectUniquePath(uniquePathsLabel, "Selecione o Unique Path para o label: " + label);


            List<String> uniquePathsValue;

            /**
             * Encontrando os possíveis Unique Paths do Label *
             */
            uniquePathsValue = getUniquePaths(partes[1], partes[0]);

            if (uniquePathsValue.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Ops! Confira o value: " + partes[1]);
                return null;
            }


            /**
             * Selecionando um Unique Path do Value*
             */
            String uniquePathValue = selectUniquePath(uniquePathsValue, "Selecione o Unique Path para o Value: " + partes[1]);

            /**
             * adicionando na resposta *
             */
            attributesInfo.add(attr.getAttributeID() + LOCAL_SEPARATOR + label + LOCAL_SEPARATOR + uniquePathLabel + LOCAL_SEPARATOR + uniquePathValue);

        }

        return attributesInfo;
    }

    private List<String> getUniquePaths(String value, String URL) {
        String columnName = "UP";
        String cypherQuery = "MATCH n WHERE n.VALUE='" + value + "' and n.URL='" + URL + "' RETURN n.UNIQUE_PATH AS " + columnName;

        return query.querySingleColumn(cypherQuery, columnName);
    }

    private String selectUniquePath(List<String> options, String msg) {
        if (options.size() == 1) {
            return options.get(0);
        }

        String txt = msg;
        txt += "\nSelecione:\n -1 - Para informar outro valor!";
        for (int i = 0; i < options.size(); i++) {
            txt += "\n " + i + " - " + options.get(i);
        }

        String result = JOptionPane.showInputDialog(frame, txt);
        Integer intResult = new Integer(result);
        if (intResult == -1) {
            return JOptionPane.showInputDialog(frame, "Informe manualmente: ").trim();
        } else {
            return options.get(intResult);
        }
    }

    private static void openBrowser(String url) {

        if (urlsOpened.contains(url)) {
            return;
        }

        try {
            Runtime.getRuntime().exec("google-chrome " + url);
            urlsOpened.add(url);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printAttributeInfo(Site site) throws FileNotFoundException, IOException {
        frame.jtaLog.setText(frame.jtaLog.getText() + "\n**** Criando diretórios");
        File dir = new File(Configuration.PATH_EXPRESSIVENESS + site.getPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        query = new QueryNeo4J();
        frame.jtaLog.setText(frame.jtaLog.getText() + "\n**** Verificando attribute info");
        Set<String> attrsInfo = getAttributeInfo(site);
        try (Writer out = new FileWriter(dir.getAbsolutePath() + "/attributes_info.csv")) {
            String[] header = {"ATTRIBUTE", "LABEL", "UNIQUE PATH LABEL", "UNIQUE PATH VALUE"};
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, CSVFormat.EXCEL.withHeader(header))) {
                for (String attrInfo : attrsInfo) {
                    List<String> dataRecord = new ArrayList<>();
                    String[] partes = attrInfo.split(LOCAL_SEPARATOR);
                    dataRecord.addAll(Arrays.asList(partes));
                    csvFilePrinter.printRecord(dataRecord);
                }
            }
        }

        frame.jtaLog.setText(frame.jtaLog.getText() + "\n**** attributes_info.csv gerado!");
        query.shutdown();
    }

    public static void csvToJTable(JTable table, File csv) throws FileNotFoundException, IOException {
        DefaultTableModel modelo = new DefaultTableModel();

        try (Reader in = new FileReader(csv)) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {

                for (String header : parser.getHeaderMap().keySet()) {
                    modelo.addColumn(header);
                }

                String[] recordValues = new String[parser.getHeaderMap().size()];
                for (CSVRecord record : parser) {
                    int i = 0;
                    for (String header : parser.getHeaderMap().keySet()) {
                        recordValues[i] = record.get(header);
                        i++;
                    }
                    modelo.addRow(recordValues);
                }
            }
        }


        table.setModel(modelo);
    }

    public static void txtToJTextArea(JTextArea jta, File txt) throws FileNotFoundException, IOException {
        jta.setText("");
        try (BufferedReader buff = new BufferedReader(new FileReader(txt))) {
            String line;
            while ((line = buff.readLine()) != null) {
                jta.append(line + "\n");
            }
        }
    }
}
