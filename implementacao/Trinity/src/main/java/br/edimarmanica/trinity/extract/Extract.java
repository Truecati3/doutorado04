package br.edimarmanica.trinity.extract;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import gnu.regexp.REException;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.xml.sax.SAXException;
import tdg.cedar.tokeniser.Token;
import tdg.cedar.tokeniser.Tokeniser;
import tdg.cedar.tokeniser.TokeniserConfig;
import tdg.cedar.utilities.Statistic;
import tdg.cedar.utilities.UTF8FileUtil;
import tdg.tex.Learner;
import tdg.tex.Node;
import tdg.tex.Text;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Não consigo treinar com 2 mil páginas, estoura memória. Vou rodar com 100
 * (y), e executar com 100 (y). Depois rodar com mais 100 (y) e executar Mas
 * manterei sempre 5 (x) páginas em comum, de forma que facilite a união dos
 * grupos
 *
 * @author edimar
 */
public abstract class Extract {

    private Site site;
    private Tokeniser tokeniser;
    private Node root = new Node();
    public static final int VALUE_MAX_LENGHT = 150; //um valor com mais caracteres é descartado
    public static final int NR_SHARED_PAGES = 5;  //número de páginas usadas no treinamento e execução em todas as iterações
    public static int WINDOW_SIZE = 100; //número de elementos usados em cada iteração
    private boolean append = false;

    public Extract(Site site) {
        this.site = site;
    }

    private void train(int offset) throws IOException {
        int start = ((WINDOW_SIZE - NR_SHARED_PAGES) * offset) + NR_SHARED_PAGES;
        int end = start + WINDOW_SIZE - NR_SHARED_PAGES;

        if (General.DEBUG) {
            System.out.println("Starting training");
        }
        TokeniserConfig tokeniserConf;
        try {
            tokeniserConf = new TokeniserConfig(new File(System.getProperty("user.dir") + "/Tokeniser.cfg"));
            tokeniser = new Tokeniser(tokeniserConf);
        } catch (ParserConfigurationException | SAXException | IOException | REException ex) {
            Logger.getLogger(Extract.class.getName()).log(Level.SEVERE, null, ex);
        }

        File dir = new File(Paths.PATH_BASE + site.getPath());
        int i = 0;
        for (File fPage : dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".html") || name.endsWith(".htm");
            }
        })) {
            if (i < NR_SHARED_PAGES || (i >= start && i < end)) {
                if (General.DEBUG) {
                    System.out.println("\t Page (" + i + "): " + fPage.getName());
                }
                train(fPage);
            }
            i++;
        }

        Statistic stat = new Statistic();
        if (General.DEBUG) {
            System.out.println("Learning extraction rules...");
        }
        stat.startCPUWatch();
        String regex = Learner.learn(root, 1, 100, tokeniser);
        //System.out.println(regex);
        stat.stopCPUWatch();
        if (General.DEBUG) {
            System.out.println("CPU Learning time: " + stat.getCPUInterval() / 1.0E10D);
        }

        compileRegex(regex);
        if (General.DEBUG) {
            System.out.println("Ending training");
        }
    }

    protected abstract void compileRegex(String regex);

    private void train(File fPage) throws IOException {
        String sPage = UTF8FileUtil.readStrippedHTML(fPage.toURI());

        TreeMap<Integer, Token> tokensPages = tokeniser.tokenise(sPage).getTokensMap();
        tokensPages.remove(tokensPages.lastKey());
        Text textPage = new Text(fPage, tokensPages.values());

        root.add(textPage);
    }

    protected String format(String value) {
        return value.replaceAll("\n", " ");
    }

    protected abstract void execute(File file, int offset) throws IOException;

    private void execute(int offset) throws IOException, REException {
        int start = ((WINDOW_SIZE - NR_SHARED_PAGES) * offset) + NR_SHARED_PAGES;
        int end = start + WINDOW_SIZE - NR_SHARED_PAGES;

        if (General.DEBUG) {
            System.out.println("Starting extracting");
        }
        File dir = new File(Paths.PATH_BASE + site.getPath());
        int i = 0;
        for (File page : dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".html") || name.endsWith(".htm");
            }
        })) {
            if (i < NR_SHARED_PAGES || (i >= start && i < end)) {
                if (General.DEBUG) {
                    System.out.println("\t Page (" + i + "): " + page.getName());
                }
                execute(page, offset);
            }
            i++;
        }
        if (General.DEBUG) {
            System.out.println("Ending extracting");
        }
    }

    public void execute() throws IOException, REException {
        File dir = new File(Paths.PATH_BASE + site.getPath());
        int nrPages = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".html") || name.endsWith(".htm");
            }
        }).length;

        for (int i = 0; (((WINDOW_SIZE - NR_SHARED_PAGES) * (i)) + NR_SHARED_PAGES) <= nrPages; i++) {
            append = false;
            train(i);
            execute(i);
        }
    }

    protected void printResults(List<String> dataRecord, int offset) {
        /**
         * ********************** results ******************
         */
        File dir = new File(Paths.PATH_TRINITY + site.getPath() + "/offset");
        dir.mkdirs();

        File file = new File(dir.getAbsolutePath() + "/result_" + offset + ".csv");
        CSVFormat format = CSVFormat.EXCEL;

        try (Writer out = new FileWriter(file, append)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                csvFilePrinter.printRecord(dataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(Extract.class.getName()).log(Level.SEVERE, null, ex);
        }
        append = true;
    }
}
