/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.check;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import tdg.cedar.tokeniser.Token;
import tdg.cedar.tokeniser.Tokeniser;
import tdg.cedar.tokeniser.TokeniserConfig;
import tdg.cedar.utilities.Statistic;
import tdg.cedar.utilities.UTF8FileUtil;
import tdg.tex.Learner;
import tdg.tex.Node;
import tdg.tex.Text;

/**
 *
 * @author edimar
 */
public class CheckGNURegex {

    private String dir;
    private String[] pagesID;
    private String[] pagesContent;

    public CheckGNURegex(String dir, String[] pagesID) {
        this.dir = dir;
        this.pagesID = pagesID;
    }

    public void execute() throws REException, IOException, ParserConfigurationException, SAXException {
        pagesContent = new String[pagesID.length];

        for (int i = 0; i < pagesID.length; i++) {
            pagesContent[i] = UTF8FileUtil.readStrippedHTML(new File(dir + "/" + pagesID[i]).toURI());
        }

        TokeniserConfig tokeniserConf = new TokeniserConfig(new File(System.getProperty("user.dir") + "/Tokeniser.cfg"));
        Tokeniser tokeniser = new Tokeniser(tokeniserConf);

        Node root = new Node();
        for (int i = 0; i < pagesID.length; i++) {
            TreeMap<Integer, Token> tokens = tokeniser.tokenise(pagesContent[i]).getTokensMap();
            tokens.remove(tokens.lastKey());
            Text text = new Text(new File(dir + "/" + pagesID[i]), tokens.values());
            root.add(text);
        }

        Statistic stat = new Statistic();
        System.out.println("Learning extraction rules...");
        stat.startCPUWatch();
        String regex = Learner.learn(root, 1, 100, tokeniser);
        stat.stopCPUWatch();
        System.out.println("CPU Learning time: " + stat.getCPUInterval() / 1.0E10D);

        System.out.println("Extracting");
        RE reRegex = new RE(regex);
        for (int j = 0; j < pagesID.length; j++) {
            REMatch match = reRegex.getMatch(pagesContent[j]);

            System.out.print(pagesID[j]);
            if (match == null) {
                System.out.println(";ignored");
                continue;
            }
            for (int i = 1; i <= reRegex.getNumSubs(); i++) {
                if (match.getStartIndex(i) > -1) {
                    System.out.print(";" + match.toString(i).replaceAll("\\s+", " ").replaceAll("<[^>]*>", "").replaceAll(";", ","));
                } else {
                    System.out.print(";");
                }
            }
            System.out.println("");

        }

        root.drawTree("Result");
    }

    public static void main(String[] args) {
        String dir = "/media/Dados/doutorado04/bases/WEIR/book/bookmooch.com/";
        String[] pages = {"0001049305.html", "0001049313.html", "0004140273.html",
            "0001049321.html", "0001049356.html", "0001049410.html", "0001049429.html"};
        CheckGNURegex cr = new CheckGNURegex(dir, pages);
        try {
            cr.execute();
        } catch (REException | IOException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(CheckGNURegex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
