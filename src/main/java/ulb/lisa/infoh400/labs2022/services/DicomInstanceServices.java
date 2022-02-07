/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2022.services;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import ulb.lisa.infoh400.labs2022.controller.ImageJpaController;

/**
 *
 * @author Adrien Foucart
 */
public class DicomInstanceServices {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(DicomInstanceServices.class.getName());
    
    private File instanceFile;
    
    public DicomInstanceServices(File instanceFile){
        this.instanceFile = instanceFile;
    }

    public Image getImage() {
        try {
            SourceImage dicomImg = new SourceImage(instanceFile.toString());
            return dicomImg.getBufferedImage();
        } catch (IOException | DicomException ex) {
            LOGGER.error("Couldn't get Image from instance file.", ex);
        }
        
        return null;
    }

    public void saveInstanceToDatabase() {
        try {
            AttributeList al = new AttributeList();
            al.read(instanceFile);
            
            EntityManagerFactory emfac = Persistence.createEntityManagerFactory("infoh400_PU");
            ImageJpaController imageCtrl = new ImageJpaController(emfac);
            
            String instanceUID = al.get(TagFromName.SOPInstanceUID).getSingleStringValueOrEmptyString();
            String studyUID = al.get(TagFromName.StudyInstanceUID).getSingleStringValueOrEmptyString();
            String seriesUID = al.get(TagFromName.SeriesInstanceUID).getSingleStringValueOrEmptyString();
            String patientID = al.get(TagFromName.PatientID).getSingleStringValueOrEmptyString();
            
            ulb.lisa.infoh400.labs2022.model.Image image = new ulb.lisa.infoh400.labs2022.model.Image();
            image.setInstanceuid(instanceUID);
            image.setStudyuid(studyUID);
            image.setSeriesuid(seriesUID);
            image.setPatientDicomIdentifier(patientID);
            
            imageCtrl.create(image);
            LOGGER.info("Saved instance to the database (instanceUID=" + instanceUID + ").");
        } catch (IOException | DicomException ex) {
            LOGGER.error("Couldn't save DICOM instance to the database.", ex);
        }
    }
    
    
    
}
