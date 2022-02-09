/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2022.services;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ACK;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ulb.lisa.infoh400.labs2022.controller.PatientJpaController;
import ulb.lisa.infoh400.labs2022.controller.PersonJpaController;
import ulb.lisa.infoh400.labs2022.model.Patient;
import ulb.lisa.infoh400.labs2022.model.Person;

/**
 *
 * @author Adrien Foucart
 */
public class HL7Services {
    private static final Logger LOGGER = LogManager.getLogger(HL7Services.class.getName());

    private HL7Service hl7Listener; 
    
    public enum SendADTResults {
        SUCCESS, FAILED_TO_CREATE, FAILED_TO_SEND, OTHER_FAILURE
    }
    
    private int currentSequenceNumber = 0;
    
    public SendADTResults createAndSendADT_A01(Patient patient, String host, int port){
        ADT_A01 adt = createADT_A01(patient);
                
        if( adt == null ){
            return SendADTResults.FAILED_TO_CREATE;
        }
        
        ACK ack = sendMessage(adt, host, port);
        if( ack == null ){
            return SendADTResults.FAILED_TO_SEND;
        }
        
        currentSequenceNumber += 1;
        
        if( ack.getMSA().getAcknowledgementCode().getValue().equalsIgnoreCase("AA") ){
            return SendADTResults.SUCCESS;
        }
        else{
            return SendADTResults.OTHER_FAILURE;
        }
    }
    
    private ADT_A01 createADT_A01(Patient patient){
        ADT_A01 adt;

        adt = new ADT_A01();
        try {
            adt.initQuickstart("ADT", "A01", "D");
        } catch (HL7Exception | IOException ex) {
            LOGGER.error("Failed to create ADT_A01 message.", ex);
            
            return null;
        }
        
        try {
            MSH msh = adt.getMSH();
            msh.getSendingApplication().getNamespaceID().setValue("HIS");
            msh.getSequenceNumber().setValue(String.valueOf(currentSequenceNumber));
            
            PID pid = adt.getPID();
            pid.getPatientName(0).getFamilyName().getSurname().setValue(patient.getIdperson().getFamilyname());
            pid.getPatientName(0).getGivenName().setValue(patient.getIdperson().getFirstname());
            pid.getPatientIdentifierList(0).getID().setValue(String.valueOf(patient.getIdpatient()));
        } catch (DataTypeException ex) {
            LOGGER.error("Failed to create ADT_A01 message.", ex);
            
            return null;
        }
        
        return adt;
    }
    
    private ACK sendMessage(Message m, String host, int port){
        HapiContext ctxt = new DefaultHapiContext();
        Parser parser = ctxt.getPipeParser();

        Connection conn;
        try {
            conn = ctxt.newClient(host, port, false);
        } catch (HL7Exception ex) {
            LOGGER.error("Failed to connect to host.", ex);
            return null;
        }
        
        Initiator init = conn.getInitiator();
        Message response;
        try {
            response = init.sendAndReceive(m);
        } catch (LLPException | HL7Exception | IOException ex) {
            LOGGER.error("Failed to send ADT_A01 message.", ex);
            return null;
        }

        String encodedResponse;
        try {
            encodedResponse = parser.encode(response);
            LOGGER.debug("Response:" + encodedResponse);
        } catch (HL7Exception ex) {
            LOGGER.warn("Failed to parse ADT_A01 response.", ex);
        }

        if( response instanceof ACK ){
            return (ACK) response;
        }
        
        return null;
    }
    
    public void startHL7Listener(int port){
        LOGGER.debug("Starting HL7 Listener.");
        if( hl7Listener == null ){
            HapiContext ctxt = new DefaultHapiContext();
            hl7Listener = ctxt.newServer(port, false);
        }
        
        if( isListeningToHL7() ){
            LOGGER.debug("HL7 Server is already running.");
            return;
        }
        
        ReceivingApplication<ADT_A01> handler = new ADTReceiverApplication();
        hl7Listener.registerApplication("ADT", "A01", handler);
        try {
            hl7Listener.startAndWait();
        } catch (InterruptedException ex) {
            LOGGER.debug("Interrupted HL7 Listener");
        }
        
        LOGGER.debug("HL7 Server is started.");
    }
    
    public void stopHL7Listener(){
        hl7Listener.stop();
    }
    
    public boolean isListeningToHL7(){        
        return ( hl7Listener != null && hl7Listener.isRunning() );
    }
    
    private class ADTReceiverApplication implements ReceivingApplication<ADT_A01> {
        
        private final EntityManagerFactory emfac = Persistence.createEntityManagerFactory("infoh400_PU");
        private final PersonJpaController personCtrl = new PersonJpaController(emfac);
        private final PatientJpaController patientCtrl = new PatientJpaController(emfac);
    

        @Override
        public Message processMessage(ADT_A01 t, Map<String, Object> map) throws ReceivingApplicationException, HL7Exception {
            String encodedMessage = new DefaultHapiContext().getPipeParser().encode(t);
            System.out.println("Received message:\n" + encodedMessage + "\n\n");
            
            String firstName = t.getPID().getPatientName(0).getGivenName().getValue();
            String lastName = t.getPID().getPatientName(0).getFamilyName().getSurname().getValue();
            Date dateOfBirth = t.getPID().getDateTimeOfBirth().getTimeOfAnEvent().getValueAsDate();
            Person person = personCtrl.findDuplicate(firstName, lastName, dateOfBirth);
            if( person == null ){
                person = new Person();
                person.setDateofbirth(dateOfBirth);
                person.setFamilyname(lastName);
                person.setFirstname(firstName);
                
                personCtrl.create(person);
                
                Patient patient = new Patient();
                patient.setIdperson(person);
                patient.setStatus("active");
                
                patientCtrl.create(patient);
                
                LOGGER.info("Created new patient from HL7 message.");
            }
            else {
                LOGGER.info("Duplicate patient found: not adding.");
            }

            try {
                return t.generateACK();
            } catch (IOException e) {
                throw new HL7Exception(e);
            }
        }

        @Override
        public boolean canProcess(ADT_A01 t) {
            return true;
        }
        
    }
    
}
