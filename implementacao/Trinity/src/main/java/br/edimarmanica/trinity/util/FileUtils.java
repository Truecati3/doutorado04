/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.util;

import java.io.File;

/**
 *
 * @author edimar
 */
public class FileUtils {

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (String children : dir.list()) {
                boolean success = deleteDir(new File(dir, children));
                if (!success) {
                    return false;
                }
            }
        }

        // Agora o diretório está vazio, restando apenas deletá-lo.
        return dir.delete();
    }
}
