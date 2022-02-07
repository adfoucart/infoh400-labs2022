/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2022.services;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.ReceivedObjectHandler;
import com.pixelmed.network.StorageSOPClassSCPDispatcher;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Adrien Foucart
 */
public class DicomProviderServices {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(DicomProviderServices.class.getName());
    
    private String AET;
    private int port;
    private File saveDirectory;
    private StorageSOPClassSCPDispatcher scpDispatcher;
    
    public DicomProviderServices(String AET, int port, File saveDirectory){
        this.AET = AET;
        this.port = port;
        this.saveDirectory = saveDirectory;
    }
    
    public void startSCPService(){
        try {
            scpDispatcher = new StorageSOPClassSCPDispatcher(
                    this.port, 
                    this.AET, 
                    this.saveDirectory, 
                    new DicomProviderServices.CStoreObjectHandler()
            );
            new Thread(scpDispatcher).start();
        }
        catch (IOException ex) {
            LOGGER.error("Couldn't send DICOM instance to the SCP.", ex);
        }
    }
    
    public void stopSCPService(){
        scpDispatcher.shutdown();
    }
    
    public boolean isReceiverThreadRunning(){
        if( scpDispatcher == null ) 
            return false;
        
        return scpDispatcher.isReady();
    }
    
    private class CStoreObjectHandler extends ReceivedObjectHandler{

        @Override
        public void sendReceivedObjectIndication(String dicomFileName, String transferSyntax, String callingAETitle) throws DicomNetworkException, DicomException, IOException {
            LOGGER.info("Received DICOM file: " + dicomFileName + " (calling AET = " + callingAETitle + ")");
            DicomInstanceServices dis = new DicomInstanceServices(new File(dicomFileName));
            if(dis.sendInstanceToSCP()){
                if( dis.saveInstanceToDatabase() ){
                    LOGGER.info("Received object forwared & saved.");
                }
                else{
                    LOGGER.error("Received object couldn't be added to database.");
                }
            } else {
                LOGGER.error("Received object couldn't be forwarded to central SCP.");
            }
        }
        
    }
}
