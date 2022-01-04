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
import ulb.lisa.infoh400.labs2020.model.Doctor;

/**
 *
 * @author Adrien Foucart
 */
public class DoctorJpaController implements Serializable {

    public DoctorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Doctor doctor) {
        if (doctor.getImageList() == null) {
            doctor.setImageList(new ArrayList<Image>());
        }
        if (doctor.getAppointmentList() == null) {
            doctor.setAppointmentList(new ArrayList<Appointment>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person idperson = doctor.getIdperson();
            if (idperson != null) {
                idperson = em.getReference(idperson.getClass(), idperson.getIdperson());
                doctor.setIdperson(idperson);
            }
            List<Image> attachedImageList = new ArrayList<Image>();
            for (Image imageListImageToAttach : doctor.getImageList()) {
                imageListImageToAttach = em.getReference(imageListImageToAttach.getClass(), imageListImageToAttach.getIdimage());
                attachedImageList.add(imageListImageToAttach);
            }
            doctor.setImageList(attachedImageList);
            List<Appointment> attachedAppointmentList = new ArrayList<Appointment>();
            for (Appointment appointmentListAppointmentToAttach : doctor.getAppointmentList()) {
                appointmentListAppointmentToAttach = em.getReference(appointmentListAppointmentToAttach.getClass(), appointmentListAppointmentToAttach.getIdappointment());
                attachedAppointmentList.add(appointmentListAppointmentToAttach);
            }
            doctor.setAppointmentList(attachedAppointmentList);
            em.persist(doctor);
            if (idperson != null) {
                idperson.getDoctorList().add(doctor);
                idperson = em.merge(idperson);
            }
            for (Image imageListImage : doctor.getImageList()) {
                Doctor oldIddoctorOfImageListImage = imageListImage.getIddoctor();
                imageListImage.setIddoctor(doctor);
                imageListImage = em.merge(imageListImage);
                if (oldIddoctorOfImageListImage != null) {
                    oldIddoctorOfImageListImage.getImageList().remove(imageListImage);
                    oldIddoctorOfImageListImage = em.merge(oldIddoctorOfImageListImage);
                }
            }
            for (Appointment appointmentListAppointment : doctor.getAppointmentList()) {
                Doctor oldIddoctorOfAppointmentListAppointment = appointmentListAppointment.getIddoctor();
                appointmentListAppointment.setIddoctor(doctor);
                appointmentListAppointment = em.merge(appointmentListAppointment);
                if (oldIddoctorOfAppointmentListAppointment != null) {
                    oldIddoctorOfAppointmentListAppointment.getAppointmentList().remove(appointmentListAppointment);
                    oldIddoctorOfAppointmentListAppointment = em.merge(oldIddoctorOfAppointmentListAppointment);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Doctor doctor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doctor persistentDoctor = em.find(Doctor.class, doctor.getIddoctor());
            Person idpersonOld = persistentDoctor.getIdperson();
            Person idpersonNew = doctor.getIdperson();
            List<Image> imageListOld = persistentDoctor.getImageList();
            List<Image> imageListNew = doctor.getImageList();
            List<Appointment> appointmentListOld = persistentDoctor.getAppointmentList();
            List<Appointment> appointmentListNew = doctor.getAppointmentList();
            List<String> illegalOrphanMessages = null;
            for (Appointment appointmentListOldAppointment : appointmentListOld) {
                if (!appointmentListNew.contains(appointmentListOldAppointment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Appointment " + appointmentListOldAppointment + " since its iddoctor field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idpersonNew != null) {
                idpersonNew = em.getReference(idpersonNew.getClass(), idpersonNew.getIdperson());
                doctor.setIdperson(idpersonNew);
            }
            List<Image> attachedImageListNew = new ArrayList<Image>();
            for (Image imageListNewImageToAttach : imageListNew) {
                imageListNewImageToAttach = em.getReference(imageListNewImageToAttach.getClass(), imageListNewImageToAttach.getIdimage());
                attachedImageListNew.add(imageListNewImageToAttach);
            }
            imageListNew = attachedImageListNew;
            doctor.setImageList(imageListNew);
            List<Appointment> attachedAppointmentListNew = new ArrayList<Appointment>();
            for (Appointment appointmentListNewAppointmentToAttach : appointmentListNew) {
                appointmentListNewAppointmentToAttach = em.getReference(appointmentListNewAppointmentToAttach.getClass(), appointmentListNewAppointmentToAttach.getIdappointment());
                attachedAppointmentListNew.add(appointmentListNewAppointmentToAttach);
            }
            appointmentListNew = attachedAppointmentListNew;
            doctor.setAppointmentList(appointmentListNew);
            doctor = em.merge(doctor);
            if (idpersonOld != null && !idpersonOld.equals(idpersonNew)) {
                idpersonOld.getDoctorList().remove(doctor);
                idpersonOld = em.merge(idpersonOld);
            }
            if (idpersonNew != null && !idpersonNew.equals(idpersonOld)) {
                idpersonNew.getDoctorList().add(doctor);
                idpersonNew = em.merge(idpersonNew);
            }
            for (Image imageListOldImage : imageListOld) {
                if (!imageListNew.contains(imageListOldImage)) {
                    imageListOldImage.setIddoctor(null);
                    imageListOldImage = em.merge(imageListOldImage);
                }
            }
            for (Image imageListNewImage : imageListNew) {
                if (!imageListOld.contains(imageListNewImage)) {
                    Doctor oldIddoctorOfImageListNewImage = imageListNewImage.getIddoctor();
                    imageListNewImage.setIddoctor(doctor);
                    imageListNewImage = em.merge(imageListNewImage);
                    if (oldIddoctorOfImageListNewImage != null && !oldIddoctorOfImageListNewImage.equals(doctor)) {
                        oldIddoctorOfImageListNewImage.getImageList().remove(imageListNewImage);
                        oldIddoctorOfImageListNewImage = em.merge(oldIddoctorOfImageListNewImage);
                    }
                }
            }
            for (Appointment appointmentListNewAppointment : appointmentListNew) {
                if (!appointmentListOld.contains(appointmentListNewAppointment)) {
                    Doctor oldIddoctorOfAppointmentListNewAppointment = appointmentListNewAppointment.getIddoctor();
                    appointmentListNewAppointment.setIddoctor(doctor);
                    appointmentListNewAppointment = em.merge(appointmentListNewAppointment);
                    if (oldIddoctorOfAppointmentListNewAppointment != null && !oldIddoctorOfAppointmentListNewAppointment.equals(doctor)) {
                        oldIddoctorOfAppointmentListNewAppointment.getAppointmentList().remove(appointmentListNewAppointment);
                        oldIddoctorOfAppointmentListNewAppointment = em.merge(oldIddoctorOfAppointmentListNewAppointment);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = doctor.getIddoctor();
                if (findDoctor(id) == null) {
                    throw new NonexistentEntityException("The doctor with id " + id + " no longer exists.");
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
            Doctor doctor;
            try {
                doctor = em.getReference(Doctor.class, id);
                doctor.getIddoctor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The doctor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Appointment> appointmentListOrphanCheck = doctor.getAppointmentList();
            for (Appointment appointmentListOrphanCheckAppointment : appointmentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Doctor (" + doctor + ") cannot be destroyed since the Appointment " + appointmentListOrphanCheckAppointment + " in its appointmentList field has a non-nullable iddoctor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Person idperson = doctor.getIdperson();
            if (idperson != null) {
                idperson.getDoctorList().remove(doctor);
                idperson = em.merge(idperson);
            }
            List<Image> imageList = doctor.getImageList();
            for (Image imageListImage : imageList) {
                imageListImage.setIddoctor(null);
                imageListImage = em.merge(imageListImage);
            }
            em.remove(doctor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Doctor> findDoctorEntities() {
        return findDoctorEntities(true, -1, -1);
    }

    public List<Doctor> findDoctorEntities(int maxResults, int firstResult) {
        return findDoctorEntities(false, maxResults, firstResult);
    }

    private List<Doctor> findDoctorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Doctor.class));
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

    public Doctor findDoctor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Doctor.class, id);
        } finally {
            em.close();
        }
    }

    public int getDoctorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Doctor> rt = cq.from(Doctor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
