/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2020.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ulb.lisa.infoh400.labs2020.model.Person;
import ulb.lisa.infoh400.labs2020.model.Image;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import ulb.lisa.infoh400.labs2020.controller.exceptions.IllegalOrphanException;
import ulb.lisa.infoh400.labs2020.controller.exceptions.NonexistentEntityException;
import ulb.lisa.infoh400.labs2020.model.Appointment;
import ulb.lisa.infoh400.labs2020.model.Patient;

/**
 *
 * @author Adrien Foucart
 */
public class PatientJpaController implements Serializable {

    public PatientJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Patient patient) {
        if (patient.getImageList() == null) {
            patient.setImageList(new ArrayList<Image>());
        }
        if (patient.getAppointmentList() == null) {
            patient.setAppointmentList(new ArrayList<Appointment>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person idperson = patient.getIdperson();
            if (idperson != null) {
                idperson = em.getReference(idperson.getClass(), idperson.getIdperson());
                patient.setIdperson(idperson);
            }
            List<Image> attachedImageList = new ArrayList<Image>();
            for (Image imageListImageToAttach : patient.getImageList()) {
                imageListImageToAttach = em.getReference(imageListImageToAttach.getClass(), imageListImageToAttach.getIdimage());
                attachedImageList.add(imageListImageToAttach);
            }
            patient.setImageList(attachedImageList);
            List<Appointment> attachedAppointmentList = new ArrayList<Appointment>();
            for (Appointment appointmentListAppointmentToAttach : patient.getAppointmentList()) {
                appointmentListAppointmentToAttach = em.getReference(appointmentListAppointmentToAttach.getClass(), appointmentListAppointmentToAttach.getIdappointment());
                attachedAppointmentList.add(appointmentListAppointmentToAttach);
            }
            patient.setAppointmentList(attachedAppointmentList);
            em.persist(patient);
            if (idperson != null) {
                idperson.getPatientList().add(patient);
                idperson = em.merge(idperson);
            }
            for (Image imageListImage : patient.getImageList()) {
                Patient oldIdpatientOfImageListImage = imageListImage.getIdpatient();
                imageListImage.setIdpatient(patient);
                imageListImage = em.merge(imageListImage);
                if (oldIdpatientOfImageListImage != null) {
                    oldIdpatientOfImageListImage.getImageList().remove(imageListImage);
                    oldIdpatientOfImageListImage = em.merge(oldIdpatientOfImageListImage);
                }
            }
            for (Appointment appointmentListAppointment : patient.getAppointmentList()) {
                Patient oldIdpatientOfAppointmentListAppointment = appointmentListAppointment.getIdpatient();
                appointmentListAppointment.setIdpatient(patient);
                appointmentListAppointment = em.merge(appointmentListAppointment);
                if (oldIdpatientOfAppointmentListAppointment != null) {
                    oldIdpatientOfAppointmentListAppointment.getAppointmentList().remove(appointmentListAppointment);
                    oldIdpatientOfAppointmentListAppointment = em.merge(oldIdpatientOfAppointmentListAppointment);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Patient patient) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Patient persistentPatient = em.find(Patient.class, patient.getIdpatient());
            Person idpersonOld = persistentPatient.getIdperson();
            Person idpersonNew = patient.getIdperson();
            List<Image> imageListOld = persistentPatient.getImageList();
            List<Image> imageListNew = patient.getImageList();
            List<Appointment> appointmentListOld = persistentPatient.getAppointmentList();
            List<Appointment> appointmentListNew = patient.getAppointmentList();
            List<String> illegalOrphanMessages = null;
            for (Appointment appointmentListOldAppointment : appointmentListOld) {
                if (!appointmentListNew.contains(appointmentListOldAppointment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Appointment " + appointmentListOldAppointment + " since its idpatient field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idpersonNew != null) {
                idpersonNew = em.getReference(idpersonNew.getClass(), idpersonNew.getIdperson());
                patient.setIdperson(idpersonNew);
            }
            List<Image> attachedImageListNew = new ArrayList<Image>();
            for (Image imageListNewImageToAttach : imageListNew) {
                imageListNewImageToAttach = em.getReference(imageListNewImageToAttach.getClass(), imageListNewImageToAttach.getIdimage());
                attachedImageListNew.add(imageListNewImageToAttach);
            }
            imageListNew = attachedImageListNew;
            patient.setImageList(imageListNew);
            List<Appointment> attachedAppointmentListNew = new ArrayList<Appointment>();
            for (Appointment appointmentListNewAppointmentToAttach : appointmentListNew) {
                appointmentListNewAppointmentToAttach = em.getReference(appointmentListNewAppointmentToAttach.getClass(), appointmentListNewAppointmentToAttach.getIdappointment());
                attachedAppointmentListNew.add(appointmentListNewAppointmentToAttach);
            }
            appointmentListNew = attachedAppointmentListNew;
            patient.setAppointmentList(appointmentListNew);
            patient = em.merge(patient);
            if (idpersonOld != null && !idpersonOld.equals(idpersonNew)) {
                idpersonOld.getPatientList().remove(patient);
                idpersonOld = em.merge(idpersonOld);
            }
            if (idpersonNew != null && !idpersonNew.equals(idpersonOld)) {
                idpersonNew.getPatientList().add(patient);
                idpersonNew = em.merge(idpersonNew);
            }
            for (Image imageListOldImage : imageListOld) {
                if (!imageListNew.contains(imageListOldImage)) {
                    imageListOldImage.setIdpatient(null);
                    imageListOldImage = em.merge(imageListOldImage);
                }
            }
            for (Image imageListNewImage : imageListNew) {
                if (!imageListOld.contains(imageListNewImage)) {
                    Patient oldIdpatientOfImageListNewImage = imageListNewImage.getIdpatient();
                    imageListNewImage.setIdpatient(patient);
                    imageListNewImage = em.merge(imageListNewImage);
                    if (oldIdpatientOfImageListNewImage != null && !oldIdpatientOfImageListNewImage.equals(patient)) {
                        oldIdpatientOfImageListNewImage.getImageList().remove(imageListNewImage);
                        oldIdpatientOfImageListNewImage = em.merge(oldIdpatientOfImageListNewImage);
                    }
                }
            }
            for (Appointment appointmentListNewAppointment : appointmentListNew) {
                if (!appointmentListOld.contains(appointmentListNewAppointment)) {
                    Patient oldIdpatientOfAppointmentListNewAppointment = appointmentListNewAppointment.getIdpatient();
                    appointmentListNewAppointment.setIdpatient(patient);
                    appointmentListNewAppointment = em.merge(appointmentListNewAppointment);
                    if (oldIdpatientOfAppointmentListNewAppointment != null && !oldIdpatientOfAppointmentListNewAppointment.equals(patient)) {
                        oldIdpatientOfAppointmentListNewAppointment.getAppointmentList().remove(appointmentListNewAppointment);
                        oldIdpatientOfAppointmentListNewAppointment = em.merge(oldIdpatientOfAppointmentListNewAppointment);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = patient.getIdpatient();
                if (findPatient(id) == null) {
                    throw new NonexistentEntityException("The patient with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Patient patient;
            try {
                patient = em.getReference(Patient.class, id);
                patient.getIdpatient();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The patient with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Appointment> appointmentListOrphanCheck = patient.getAppointmentList();
            for (Appointment appointmentListOrphanCheckAppointment : appointmentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Patient (" + patient + ") cannot be destroyed since the Appointment " + appointmentListOrphanCheckAppointment + " in its appointmentList field has a non-nullable idpatient field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Person idperson = patient.getIdperson();
            if (idperson != null) {
                idperson.getPatientList().remove(patient);
                idperson = em.merge(idperson);
            }
            List<Image> imageList = patient.getImageList();
            for (Image imageListImage : imageList) {
                imageListImage.setIdpatient(null);
                imageListImage = em.merge(imageListImage);
            }
            em.remove(patient);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Patient> findPatientEntities() {
        return findPatientEntities(true, -1, -1);
    }

    public List<Patient> findPatientEntities(int maxResults, int firstResult) {
        return findPatientEntities(false, maxResults, firstResult);
    }

    private List<Patient> findPatientEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Patient.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Patient findPatient(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Patient.class, id);
        } finally {
            em.close();
        }
    }

    public int getPatientCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Patient> rt = cq.from(Patient.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
