/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.runner;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.runner.util.CommandLineOption;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class Main {

    public static String USAGE = ""
            + "Required options: \n"
            + "\t --option [Options: (i) load - to load HTML pages to neo4j; (ii) intrasite - to extract attribute-values  \n"
            + "\t --dataset [Dataset: (i) WEIR; (ii) SWDE \n"
            + "\t --domain [Domain]\n"
            + "\t --site [Site]\n"
            + "Optional options: \n"
            + "\t --debug: print general debugging info \n"
            + "Ex: java -jar Runner.jar --option load --dataset WEIR --domain book --site bookmooch.com --debug";

    public static Dataset getDataset(String datasetFolderName) {
        for (Dataset dataset : Dataset.values()) {
            if (dataset.getFolderName().equals(datasetFolderName)) {
                return dataset;
            }
        }
        throw new UnsupportedOperationException("Invalid dataset!");
    }

    public static Domain getDomain(Dataset dataset, String domainFolderName) {
        for (Domain domain : dataset.getDomains()) {
            if (domain.getFolderName().equals(domainFolderName)) {
                return domain;
            }
        }

        throw new UnsupportedOperationException("Invalid domain!");
    }

    public static Site getSite(Domain domain, String siteFolderName) {
        for (Site site : domain.getSites()) {
            if (site.getFolderName().equals(siteFolderName)) {
                return site;
            }
        }
        throw new UnsupportedOperationException("Invalid site!");
    }

    public static void main(String[] args) {
        /**
         * Início da leitura dos argumentos de linha de comando
         */
        CommandLineOption options = new CommandLineOption(args);
        options.setUsage(USAGE);
        options.require(new String[]{"option", "dataset", "domain", "site"});

        String option = options.getString("option");

        Dataset dataset = getDataset(options.getString("dataset"));
        Domain domain = getDomain(dataset, options.getString("domain"));
        Site site = getSite(domain, options.getString("site"));

        
        if (options.hasArg("debug")) {
            System.out.println("Running debuging mode simplified");
            General.DEBUG = true;
        }

        if (option.equals("load")){
            HtmlToNeo4j h2n = new HtmlToNeo4j();
            h2n.execute(site);
        }else if (option.equals("intrasite")){
            Intrasite intrasite = new Intrasite();
            intrasite.execute(site);
        }
      
    }
}
