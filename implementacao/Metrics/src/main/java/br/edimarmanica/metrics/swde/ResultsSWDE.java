/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.swde;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Results;
import br.edimarmanica.metrics.ValueFormatter;
import br.edimarmanica.metrics.weir.ResultsWeir;
import java.io.File;
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
public class ResultsSWDE extends Results {

    private ValueFormatter formatter = new ValueFormatterSwde();

    public ResultsSWDE(Site site) {
        super(site);
    }

    @Override
    protected String formatUrl(String url) {
        return url.replaceAll(".*" + site.getPath() + "/", "").replaceAll(".htm", "");
    }

    @Override
    protected String formatValue(String value) {
        return formatter.format(value);
    }
}
