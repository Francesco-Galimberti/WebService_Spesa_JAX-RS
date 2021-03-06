/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spesa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Galimberti Francesco
 */
@javax.ws.rs.ApplicationPath("spesa")
public class ApplicationConfig extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(spesa.Liste.class);
        resources.add(spesa.Prodotti.class);
        resources.add(spesa.Richieste.class);
        resources.add(spesa.Risposte.class);
        resources.add(spesa.Utenti.class);
    }
    
}
