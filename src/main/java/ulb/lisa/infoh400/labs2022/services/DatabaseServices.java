/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2022.services;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import ulb.lisa.infoh400.labs2022.controller.DoctorJpaController;
import ulb.lisa.infoh400.labs2022.controller.ImageJpaController;
import ulb.lisa.infoh400.labs2022.controller.PatientJpaController;
import ulb.lisa.infoh400.labs2022.controller.PersonJpaController;
import ulb.lisa.infoh400.labs2022.model.Doctor;
import ulb.lisa.infoh400.labs2022.model.Image;
import ulb.lisa.infoh400.labs2022.model.Patient;
import ulb.lisa.infoh400.labs2022.model.Person;

/**
 *
 * @author Adrien Foucart
 */
public class DatabaseServices {
    private final EntityManagerFactory emfac = Persistence.createEntityManagerFactory("infoh400_PU");
    
    public DatabaseServices() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public EntityManagerFactory getEntityManagerFactory(){
        return emfac;
    }
}
