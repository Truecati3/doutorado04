/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.intrasitemapping.auto;

import br.edimarmanica.trinity.extract.Extract;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class MappingController {

    private Site site;

    private List<List<Integer>> mappings = new ArrayList<>(); //Dimensões {<index=offset<index=MappedIndexRule, value=CurrentIndexRule>>}
    private List<Map<Integer, Integer>> mappingsHash = new ArrayList<>(); //Dimensões {offset, <CurrentIndexRule, MappedIndexRule>}
    private List<List<List<String>>> offsets = new ArrayList<>(); //dimensões {offset,regra,registro}

    public MappingController(Site site) {
        this.site = site;
    }

    private void reading() {
        /**
         * Lendos os Run02.NR_SHARED_PAGES primeiros elementos de cada offset
         */
        File dir = new File(Paths.PATH_TRINITY + site.getPath() + "/offset");

        for (int nrOffset = 0; nrOffset < dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        }).length; nrOffset++) {
            List<List<String>> offset = new ArrayList<>(); //cada arquivo é um offset

            try (Reader in = new FileReader(dir.getAbsoluteFile() + "/result_" + nrOffset + ".csv")) {
                try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL)) {
                    int nrRegistro = 0;
                    for (CSVRecord record : parser) {
                        if (nrRegistro >= Extract.NR_SHARED_PAGES) {
                            break;
                        }

                        for (int nrRegra = 0; nrRegra < record.size(); nrRegra++) {
                            if (nrRegistro == 0) {
                                List<String> regra = new ArrayList<>();
                                try {
                                    regra.add(Preprocessing.filter(record.get(nrRegra)));
                                } catch (InvalidValue ex) {
                                    regra.add("");
                                }
                                offset.add(regra);
                            } else {
                                try {
                                    offset.get(nrRegra).add(Preprocessing.filter(record.get(nrRegra)));
                                } catch (InvalidValue ex) {
                                    offset.get(nrRegra).add("");
                                }
                            }
                        }
                        nrRegistro++;
                    }
                }
                offsets.add(offset);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MappingController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MappingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Mostrando a leitura
         */
        /*for (int i = 1; i < offsets.size(); i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(offsets.get(i).get(0).get(j) + " - ");
            }
            System.out.println("");
        }*/
    }

    private void mappings() {
        List<List<String>> offset0 = offsets.get(0);

        //o mapeamento do offset0 é ele mesmo
        List<Integer> mapOff0 = new ArrayList<>();
        for (int i = 0; i < offset0.size(); i++) {
            mapOff0.add(i);
        }
        mappings.add(mapOff0);

        // o mapeamento dos demais offsets
        for (int i = 1; i < offsets.size(); i++) {
            Mapping map = new Mapping(offset0, offsets.get(i));
            mappings.add(map.mappings());
        }

        //elimina (coloca -1) as regras do offset0 que não tiveram mapeamento em todos os offsets
        for (int i = 0; i < mappings.get(0).size(); i++) {
            boolean flag = false;
            for (int j = 1; j < mappings.size(); j++) {
                if (mappings.get(j).get(i) == -1) { //se a regra do offset0 ficou sem mapeamento em 1 outro offset já elimina (agressivo)
                    flag = true;
                    break;
                }
            }
            if (flag) {
                mappings.get(0).set(i, -1);
            }
        }

        //elimina (coloca -1) em todos os mapeamentos onde a regra do ofset0 é -1
        for (int i = 0; i < mappings.get(0).size(); i++) {
            if (mappings.get(0).get(i) == -1) {
                for (int j = 1; j < mappings.size(); j++) {
                    mappings.get(j).set(i, -1);
                }
            }
        }

        /**
         * criando hash para encontrar mais rápido o mapeamento para cada
         * posição
         */
        for (int i = 0; i < mappings.size(); i++) {
            Map<Integer, Integer> offset = new HashMap<>();
            for (int j = 0; j < mappings.get(i).size(); j++) {
                if (mappings.get(i).get(j) != -1) {
                    offset.put(mappings.get(i).get(j), j);
                }
            }
            mappingsHash.add(offset);
        }

    }

    public void execute() {
        reading();
        mappings();
    }

    public void showMappings() {

        for (List<Integer> offset : mappings) {
            for (Integer index : offset) {
                if (index != -1) { //só porque se algum for menos 1 todos devem ser
                    System.out.print(index + ",");
                }
            }
            System.out.println("");
        }
    }

    public List<List<Integer>> getMappings() {
        return mappings;
    }

    /**
     * @param indexOffset
     * @param indexRule
     * @return retorna o índice da regra do offset0 correspondente a regra
     * indexRule no offset indexOffset, ou seja, retorna a regra que ela ficará
     */
    public int getSpecificMap(int indexOffset, int indexRule) {

        Integer aux = mappingsHash.get(indexOffset).get(indexRule);
        if (aux == null) {
            return -1;
        } else {
            return aux;
        }
    }
    
    /**
     * imprime as regras mapeadas para a regra (ruleID) do offset0
     * @param ruleID --> rule do offset0 -- é a que vai aparecer no results.
     */
    public void printSpecificMap(int ruleID){
        int i=0;
        for(Map<Integer, Integer> map: mappingsHash){
            for(Integer key: map.keySet()){
                if (map.get(key) == ruleID){
                    System.out.println("Offset: "+i+" - Rule: "+key);
                }
            }
            i++;
        }
    }

    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.swde.auto.Site.CARS;
        MappingController mc = new MappingController(site);
        mc.execute();
        //mc.showMappings();
        //System.out.println(mc.getSpecificMap(15, 177)); //186
        mc.printSpecificMap(57);
    }

}
