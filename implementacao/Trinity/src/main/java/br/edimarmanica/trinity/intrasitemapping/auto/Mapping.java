/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.intrasitemapping.auto;

import br.edimarmanica.trinity.extract.Extract;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edimar
 */
public class Mapping {

    private List<List<String>> offset0; // Dimensões {regra, registro}
    private List<List<String>> offsetX; // Dimensões {regra, registro}

    public Mapping(List<List<String>> offset0, List<List<String>> offsetX) {
        this.offset0 = offset0;
        this.offsetX = offsetX;
    }

    private int nrMatches(List<String> regraOffset0, List<String> regraOffsetX) {
        int nrMatches = 0;

        for (int nrRegistro = 0; nrRegistro < regraOffset0.size(); nrRegistro++) {
            if (regraOffset0.get(nrRegistro).equals(regraOffsetX.get(nrRegistro))) {
                nrMatches++;
            }
        }

        return nrMatches;
    }

    /**
     * @param ruleIndex índice da regra do offset0 que está sendo avaliada
     * @return a regra do offsetX que deve ser mapeada para a regra ruleIndex do
     * offset0
     */
    private int mapping(int ruleIndex) throws MappingNotFoundException {
        int maxNrMatches = 0;
        int positionMaxMatches = -1;

        for (int nrRegra = 0; nrRegra < offsetX.size(); nrRegra++) {
            int nrMatches = nrMatches(offset0.get(ruleIndex), offsetX.get(nrRegra));
            if (nrMatches > maxNrMatches) {
                maxNrMatches = nrMatches;
                positionMaxMatches = nrRegra;
            }
        }

        //tem que casar com pelo menos 50% + 1. Não pode ser todos pois existem casos onde não extrai pq os NR_SHARED_PAGES são diferentes
        if (positionMaxMatches == -1 || maxNrMatches <= (Extract.NR_SHARED_PAGES / 2)) {
            throw new MappingNotFoundException();
        }

        return positionMaxMatches;
    }

    public List<Integer> mappings() {
        List<Integer> mapping = new ArrayList<>();
        for (int nrRegra = 0; nrRegra < offset0.size(); nrRegra++) {
            int map;
            try {
                checkEmpty(offset0.get(nrRegra));
                mapping.add(nrRegra, mapping(nrRegra));
            } catch (MappingNotFoundException ex) {
                mapping.add(nrRegra, -1);
            }
        }
        return mapping;
    }

    private void checkEmpty(List<String> regraOffset0) throws MappingNotFoundException {
        boolean flag = false;
        for (String st : regraOffset0) {
            if (!st.trim().isEmpty()) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            throw new MappingNotFoundException();
        }
    }
}
