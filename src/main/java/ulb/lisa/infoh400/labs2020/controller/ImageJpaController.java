/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2020.controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ulb.lisa.infoh400.labs2020.controller.exceptions.NonexistentEntityException;
import ulb.lisa.infoh400.labs2020.model.Appointment;
import ulb.lisa.infoh400.labs2020.model.Doctor;
import ulb.lisa.infoh400.labs2020.model.Image;
import ulb.lisa.infoh400.labs2020.model.Patient;

/**
 *
 * @author Adrien Foucart
 */
public class ImageJpaController implements Serializable {

    public ImageJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Image image) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Appointment idappointment = image.getIdappointment();
            if (idappointment != null) {
                idappointment = em.getReference(idappointment.getClass(), idappointment.getIdappointment());
                image.setIdappointment(idappointment);
            }
            Doctor iddoctor = image.getIddoctor();
            if (iddoctor != null) {
                iddoctor = em.getReference(iddoctor.getClass(), iddoctor.getIddoctor());
                image.setIddoctor(iddoctor);
            }
            Patient idpatient = image.getIdpatient();
            if (idpatient != null) {
                idpatient = em.getReference(idpatient.getClass(), idpatient.getIdpatient());
                image.setIdpatient(idpatient);
            }
            em.persist(image);
            if (idappointment != null) {
                idappointment.getImageList().add(image);
                idappointment = em.merge(idappointment);
            }
            if (iddoctor != null) {
                iddoctor.getImageList().add(image);
                iddoctor = em.merge(iddoctor);
            }
            if (idpatient != null) {
                idpatient.getImageList().add(image);
                idpatient = em.merge(idpatient);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Image image) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Image persistentImage = em.find(Image.class, image.getIdimage());
            Appointment idappointmentOld = persistentImage.getIdappointment();
            Appointment idappointmentNew = image.getIdappointment();
            Doctor iddoctorOld = persistentImage.getIddoctor();
            Doctor iddoctorNew = image.getIddoctor();
            Patient idpatientOld = persistentImage.getIdpatient();
            Patient idpatientNew = image.getIdpatient();
            if (idappointmentNew != null) {
                idappointmentNew = em.getReference(idappointmentNew.getClass(), idappointmentNew.getIdappointment());
                image.setIdappointment(idappointmentNew);
            }
            if (iddoctorNew != null) {
                iddoctorNew = em.getReference(iddoctorNew.getClass(), iddoctorNew.getIddoctor());
                image.setIddoctor(iddoctorNew);
            }
            if (idpatientNew != null) {
                idpatientNew = em.getReference(idpatientNew.getClass(), idpatientNew.getIdpatient());
                image.setIdpatient(idpatientNew);
            }
            image = em.merge(image);
            if (idappointmentOld != null && !idappointmentOld.equals(idappointmentNew)) {
                idappointmentOld.getImageList().remove(image);
                idappointmentOld = em.merge(idappointmentOld);
            }
            if (idappointmentNew != null && !idappointmentNew.equals(idappointmentOld)) {
                idappointmentNew.getImageList().add(image);
                idappointmentNew = em.merge(idappointmentNew);
            }
            if (iddoctorOld != null && !iddoctorOld.equals(iddoctorNew)) {
                iddoctorOld.getImageList().remove(image);
                iddoctorOld = em.merge(iddoctorOld);
            }
            if (iddoctorNew != null && !iddoctorNew.equals(iddoctorOld)) {
                iddoctorNew.getImageList().add(image);
                iddoctorNew = em.merge(iddoctorNew);
            }
            if (idpatientOld != null && !idpatientOld.equals(idpatientNew)) {
                idpatientOld.getImageList().remove(image);
                idpatientOld = em.merge(idpatientOld);
            }
            if (idpatientNew != null && !idpatientNew.equals(idpatientOld)) {
                idpatientNew.getImageList().add(image);
                idpatientNew = em.merge(idpatientNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = image.getIdimage();
                if (findImage(id) == null) {
                    throw new NonexistentEntityException("The image with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Image image;
            try {
                image = em.getReference(Image.class, id);
                image.getIdimage();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The image with id " + id + " no longer exists.", enfe);
            }
            Appointment idappointment = image.getIdappointment();
            if (idappointment != null) {
                idappointment.getImageList().remove(image);
                idappointment = em.merge(idappointment);
            }
            Doctor iddoctor = image.getIddoctor();
            if (iddoctor != null) {
                iddoctor.getImageList().remove(image);
                iddoctor = em.merge(iddoctor);
            }
            Patient idpatient = image.getIdpatient();
            if (idpatient != null) {
                idpatient.getImageList().remove(image);
                idpatient = em.merge(idpatient);
            }
            em.remove(image);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Image> findImageEntities() {
        return findImageEntities(true, -1, -1);
    }

    public List<Image> findImageEntities(int maxResults, int firstResult) {
        return findImageEntities(false, maxResults, firstResult);
    }

    private List<Image> findImageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Image.class));
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

    public Image findImage(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Image.class, id);
        } finally {
            em.close();
        }
    }

    public int getImageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Image> rt = cq.from(Image.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
