/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2022.services;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.StorageSOPClassSCU;
import java.io.File;
import java.io.IOException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import ulb.lisa.infoh400.labs2022.controller.ImageJpaController;
import ulb.lisa.infoh400.labs2022.model.Image;

/**
 *
 * @author Adrien Foucart
 */
public class DicomInstanceServices {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(DicomInstanceServices.class.getName());
    
    private File instanceFile;
    private Image image;
    
    public DicomInstanceServices(File instanceFile){
        this.instanceFile = instanceFile;
    }
    
    public DicomInstanceServices(Image image){
        this.image = image;
        this.instanceFile = new File("e:/data/pacs", this.image.getInstanceuid());
    }

    public java.awt.Image getDisplayableImage() {
        if( instanceFile == null ){
            return null;
        }
        try {
            SourceImage dicomImg = new SourceImage(instanceFile.toString());
            return dicomImg.getBufferedImage();
        } catch (IOException | DicomException ex) {
            LOGGER.error("Couldn't get Image from instance file.", ex);
        }
        
        return null;
    }
    
    public String getAttributesAsString(){
        try {
            if( instanceFile == null ){
                return "No instance file.";
            }
            
            AttributeList al = new AttributeList();
            al.read(instanceFile);
            String out = "";
            
            for( AttributeTag tag: al.keySet() ){
                out += tag + " : " + al.get(tag).getDelimitedStringValuesOrEmptyString() + "\n";
            }
            
            return out;
        } catch (IOException | DicomException ex) {
            LOGGER.error("Couldn't get instance attributes.", ex);
        }
        
        return "Couldn't get instance attributes.";
    }

    public boolean saveInstanceToDatabase() {
        try {
            AttributeList al = new AttributeList();
            al.read(instanceFile);
            
            EntityManagerFactory emfac = Persistence.createEntityManagerFactory("infoh400_PU");
            ImageJpaController imageCtrl = new ImageJpaController(emfac);
            
            String instanceUID = al.get(TagFromName.SOPInstanceUID).getSingleStringValueOrEmptyString();
            String studyUID = al.get(TagFromName.StudyInstanceUID).getSingleStringValueOrEmptyString();
            String seriesUID = al.get(TagFromName.SeriesInstanceUID).getSingleStringValueOrEmptyString();
            String patientID = al.get(TagFromName.PatientID).getSingleStringValueOrEmptyString();
            
            this.image = new Image();
            image.setInstanceuid(instanceUID);
            image.setStudyuid(studyUID);
            image.setSeriesuid(seriesUID);
            image.setPatientDicomIdentifier(patientID);
            
            imageCtrl.create(image);
            LOGGER.info("Saved instance to the database (instanceUID=" + instanceUID + ").");
            
            return true;
        } catch (IOException | DicomException ex) {
            LOGGER.error("Couldn't save DICOM instance to the database.", ex);
        }
        
        return false;
    }
    
    public boolean sendInstanceToSCP(){
        return sendInstanceToSCP("localhost", 11112, "STORESCP");
    }
    
    public boolean sendInstanceToSCP(String host, int port, String calledAET){
        try {
            AttributeList al = new AttributeList();
            al.read(instanceFile);
            
            new StorageSOPClassSCU(host, port, calledAET, "HISSCU", instanceFile.toString(), al.get(TagFromName.SOPClassUID).getDelimitedStringValuesOrEmptyString(), al.get(TagFromName.SOPInstanceUID).getDelimitedStringValuesOrEmptyString(), 0);
            
            return true;
        } catch (IOException | DicomException | DicomNetworkException ex) {
            LOGGER.error("Couldn't send DICOM instance to the SCP.", ex);
        }
        
        return false;
    }
}
