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
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ACK;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ulb.lisa.infoh400.labs2022.model.Patient;

/**
 *
 * @author Adrien Foucart
 */
public class HL7Services {
    private static final Logger LOGGER = LogManager.getLogger(HL7Services.class.getName());
    
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
    
}
