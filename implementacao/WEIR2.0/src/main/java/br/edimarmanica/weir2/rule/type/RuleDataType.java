/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.rule.type;

import br.edimarmanica.weir2.rule.Loader;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class RuleDataType {

    /**
     *
     * @param ruleValues values extracted by the rule
     * @return o DataType mais frequente (Primeiro acha os data type mais
     * específicos para cada valor, depois acha o data type que mais ocorre. No
     * WEIR original achava o datatype mais genérico entre esses, porém os
     * vários formatos de data no mesmo site (só mês e ano, ex.) dava problemas)
     */
    public static DataType getMostFrequentType(Collection<String> ruleValues) {
        Map<DataType, Integer> dataTypes = new HashMap<>();
        for (String value : ruleValues) {
            DataType type = ValueDataType.getDataType(value);
            if (dataTypes.containsKey(type)) {
                dataTypes.put(type, dataTypes.get(type) + 1);
            } else {
                dataTypes.put(type, 1);
            }
        }

        int maxOccurrence = 0;
        DataType maxType = null;
        for (DataType type : dataTypes.keySet()) {
            if (maxOccurrence < dataTypes.get(type)) {
                maxOccurrence = dataTypes.get(type);
                maxType = type;
            }
        }

        return maxType;
    }

    public static DataType getMostFrequentType(File rule) {
        return getMostFrequentType(Loader.loadPageValues(rule, false).values());
    }
}
