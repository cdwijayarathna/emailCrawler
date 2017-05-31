/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus.sinhala.crawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dimuthuupeksha
 */
public class SysProperty {
    public static String getProperty(String key){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("/home/maduranga/project.properties");
            Properties p = new Properties();
            p.load(fis);
            return p.getProperty(key);
        } catch (IOException ex) {
            Logger.getLogger(SysProperty.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(SysProperty.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
