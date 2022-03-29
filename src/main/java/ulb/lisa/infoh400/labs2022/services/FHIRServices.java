/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2022.services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.ArrayList;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ContactPoint;
import ulb.lisa.infoh400.labs2022.model.Patient;
import ulb.lisa.infoh400.labs2022.model.Person;

/**
 *
 * @author Adrien Foucart
 */
public class FHIRServices {
    
    public static Patient getPatient(org.hl7.fhir.r4.model.Patient patientResource){
        Patient patient = new Patient();
        Person person = new Person();
        person.setFirstname(patientResource.getNameFirstRep().getGivenAsSingleString());
        person.setFamilyname(patientResource.getNameFirstRep().getFamily());
        person.setDateofbirth(patientResource.getBirthDate());
        patient.setIdperson(person);
        for( ContactPoint contact: patientResource.getTelecom() ){
            if( contact.getSystem() == ContactPoint.ContactPointSystem.PHONE ){
                patient.setPhonenumber(contact.getValue());
            }
        }
        
        return patient;
    }
    
    public ArrayList<Patient> searchPatient(String familyName, String fhirBase){
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirBase);
        
        Bundle results = client.search().forResource(org.hl7.fhir.r4.model.Patient.class)
                .where(org.hl7.fhir.r4.model.Patient.FAMILY.matches().value(familyName))
                .returnBundle(Bundle.class)
                .execute();
        
        ArrayList<Patient> foundPatients = new ArrayList();
        for( Bundle.BundleEntryComponent entry : results.getEntry() ){
            org.hl7.fhir.r4.model.Patient patientResource = (org.hl7.fhir.r4.model.Patient) entry.getResource();
            foundPatients.add(getPatient(patientResource));
        }
        
        return foundPatients;
    }
    
}
