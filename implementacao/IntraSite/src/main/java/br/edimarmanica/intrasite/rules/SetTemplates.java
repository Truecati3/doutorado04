/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.rules;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.IntrasiteExtraction;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
public class SetTemplates {

    private Site site;
    private long currentPageId = -1;
    private Map<String, Long> pageIds = new HashMap<>(); //<URL, page_id>
    private Map<String, Set<Long>> mapPageIds = new HashMap<>(); //PATH_VALUE, Set<page_id>
    private Map<String, Set<Long>> mapNodeIds = new HashMap<>(); //PATH_VALUE, Set<node_id>
    private final static String NODE_FILE_NAME = "nodes.csv";
    private Neo4jHandler neo4j;

    public SetTemplates(Site site) {
        this.site = site;
    }

    public void execute() {
        neo4j = new Neo4jHandler(site);
        processCSV();
        neo4j.shutdown();
    }

    private void processCSV() {
        long recordID = 0;
        try (Reader in = new FileReader(Paths.PARTIAL_CSV_PATH + "/" + site.getPath() + "/" + NODE_FILE_NAME)) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.TDF.withHeader())) {
                for (CSVRecord record : parser) {
                    int type = Integer.valueOf(record.get("NODE_TYPE"));
                    if (type == 3) {
                        add(recordID, record.get("PATH"), record.get("VALUE"), record.get("URL"));
                    }
                    recordID++;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SetTemplates.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SetTemplates.class.getName()).log(Level.SEVERE, null, ex);
        }

        int i = 0;
        for (String key : mapNodeIds.keySet()) {
            System.out.println("Faltam: " + (mapNodeIds.size() - i));
            i++;
            //template é um valor que ocorre no mesmo path em pelo menos X páginas
            if (mapPageIds.get(key).size() >= (pageIds.size() * IntrasiteExtraction.PR_TEMPLATE / 100)) {
                addTemplateLabel(new ArrayList<>(mapNodeIds.get(key)));
            } else {
                addCandValueLabel(new ArrayList<>(mapNodeIds.get(key)));
            }
        }

    }

    private void add(long nodeId, String path, String value, String url) {
        long pageId = getPageId(url);
        String key = path + General.SEPARADOR + value;
        if (mapPageIds.containsKey(key)) {
            mapPageIds.get(key).add(pageId);
        } else {
            Set<Long> set = new HashSet<>();
            set.add(pageId);
            mapPageIds.put(key, set);
        }

        if (mapNodeIds.containsKey(key)) {
            mapNodeIds.get(key).add(nodeId);
        } else {
            Set<Long> set = new HashSet<>();
            set.add(nodeId);
            mapNodeIds.put(key, set);
        }
    }

    private void addTemplateLabel(List<Long> nodeID) {
        String cypherTemplate = "MATCH o WHERE id(o) = {id} SET o:" + Label.Template + " RETURN count(o)";
        Map<String, Object> params = new HashMap<>();
        params.put("id", nodeID);
        neo4j.executeCypher(cypherTemplate, params);
    }

    private void addCandValueLabel(List<Long> nodeID) {
        String cypherTemplate = "MATCH o WHERE id(o) = {id} SET o:" + Label.CandValue + " RETURN count(o)";
        Map<String, Object> params = new HashMap<>();
        params.put("id", nodeID);
        neo4j.executeCypher(cypherTemplate, params);
    }

    private Long getPageId(String url) {
        if (pageIds.containsKey(url)) {
            return pageIds.get(url);
        }

        pageIds.put(url, ++currentPageId);
        return currentPageId;
    }

    public static void main(String[] args) {
    /*    Domain domain = br.edimarmanica.dataset.swde.Domain.JOB;
        for (Site site : domain.getSites()) {
            System.out.println("Site: "+site.getFolderName());
            SetTemplate02 st = new SetTemplate02(site);
            st.execute();
        }*/


        SetTemplates st = new SetTemplates(br.edimarmanica.dataset.swde.movie.Site.ALLMOVIE);
        st.execute();
    }
}
