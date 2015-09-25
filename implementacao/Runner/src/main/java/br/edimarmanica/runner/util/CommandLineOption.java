package br.edimarmanica.runner.util;


import java.util.HashMap;
import java.util.Map;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author edimar
 */
public class CommandLineOption {

    private Map<String, String> argMap;
    private String usage;

    public CommandLineOption(String[] args) {
        argMap = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    argMap.put(args[i].substring(2), args[i + 1]);
                } else {
                    argMap.put(args[i].substring(2), "");
                }
            }
        }
    }

    public void require(String[] requirements) {
        for (int i = 0; i < requirements.length; i++) {
            if (!argMap.containsKey(requirements[i])) {
                printUsage();
                System.err.println("Argument " + requirements[i] + " is required");
                System.exit(1);
            }
        }
    }

    public String getString(String key) {
        return argMap.get(key);
    }

    /*
     * get comma separated strings
     */
    public String[] getStrings(String key) {
        if (argMap.get(key) == null) {
            return new String[]{};
        } else {
            return argMap.get(key).split(",");
        }
    }

    public Integer getInt(String key) {
        return Integer.parseInt(argMap.get(key));
    }

    public Integer getInt(String key, int defaultValue) {
        if (argMap.containsKey(key)) {
            return Integer.parseInt(argMap.get(key));
        } else {
            return defaultValue;
        }
    }

    public Double getDouble(String key) {
        return Double.parseDouble(argMap.get(key));
    }

    public boolean hasArg(String key) {
        return argMap.containsKey(key);
    }

    public void setUsage(String u) {
        usage = u;
    }

    public void printUsage() {
        System.out.println(usage);
    }
}
