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
import ulb.lisa.infoh400.labs2020.model.Doctor;
import ulb.lisa.infoh400.labs2020.model.Patient;
import ulb.lisa.infoh400.labs2020.model.Image;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import ulb.lisa.infoh400.labs2020.controller.exceptions.NonexistentEntityException;
import ulb.lisa.infoh400.labs2020.model.Appointment;
import ulb.lisa.infoh400.labs2020.model.Note;

/**
 *
 * @author Adrien Foucart
 */
public class AppointmentJpaController implements Serializable {

    public AppointmentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Appointment appointment) {
        if (appointment.getImageList() == null) {
            appointment.setImageList(new ArrayList<Image>());
        }
        if (appointment.getNoteList() == null) {
            appointment.setNoteList(new ArrayList<Note>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doctor iddoctor = appointment.getIddoctor();
            if (iddoctor != null) {
                iddoctor = em.getReference(iddoctor.getClass(), iddoctor.getIddoctor());
                appointment.setIddoctor(iddoctor);
            }
            Patient idpatient = appointment.getIdpatient();
            if (idpatient != null) {
                idpatient = em.getReference(idpatient.getClass(), idpatient.getIdpatient());
                appointment.setIdpatient(idpatient);
            }
            List<Image> attachedImageList = new ArrayList<Image>();
            for (Image imageListImageToAttach : appointment.getImageList()) {
                imageListImageToAttach = em.getReference(imageListImageToAttach.getClass(), imageListImageToAttach.getIdimage());
                attachedImageList.add(imageListImageToAttach);
            }
            appointment.setImageList(attachedImageList);
            List<Note> attachedNoteList = new ArrayList<Note>();
            for (Note noteListNoteToAttach : appointment.getNoteList()) {
                noteListNoteToAttach = em.getReference(noteListNoteToAttach.getClass(), noteListNoteToAttach.getIdnote());
                attachedNoteList.add(noteListNoteToAttach);
            }
            appointment.setNoteList(attachedNoteList);
            em.persist(appointment);
            if (iddoctor != null) {
                iddoctor.getAppointmentList().add(appointment);
                iddoctor = em.merge(iddoctor);
            }
            if (idpatient != null) {
                idpatient.getAppointmentList().add(appointment);
                idpatient = em.merge(idpatient);
            }
            for (Image imageListImage : appointment.getImageList()) {
                Appointment oldIdappointmentOfImageListImage = imageListImage.getIdappointment();
                imageListImage.setIdappointment(appointment);
                imageListImage = em.merge(imageListImage);
                if (oldIdappointmentOfImageListImage != null) {
                    oldIdappointmentOfImageListImage.getImageList().remove(imageListImage);
                    oldIdappointmentOfImageListImage = em.merge(oldIdappointmentOfImageListImage);
                }
            }
            for (Note noteListNote : appointment.getNoteList()) {
                Appointment oldIdappointmentOfNoteListNote = noteListNote.getIdappointment();
                noteListNote.setIdappointment(appointment);
                noteListNote = em.merge(noteListNote);
                if (oldIdappointmentOfNoteListNote != null) {
                    oldIdappointmentOfNoteListNote.getNoteList().remove(noteListNote);
                    oldIdappointmentOfNoteListNote = em.merge(oldIdappointmentOfNoteListNote);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Appointment appointment) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Appointment persistentAppointment = em.find(Appointment.class, appointment.getIdappointment());
            Doctor iddoctorOld = persistentAppointment.getIddoctor();
            Doctor iddoctorNew = appointment.getIddoctor();
            Patient idpatientOld = persistentAppointment.getIdpatient();
            Patient idpatientNew = appointment.getIdpatient();
            List<Image> imageListOld = persistentAppointment.getImageList();
            List<Image> imageListNew = appointment.getImageList();
            List<Note> noteListOld = persistentAppointment.getNoteList();
            List<Note> noteListNew = appointment.getNoteList();
            if (iddoctorNew != null) {
                iddoctorNew = em.getReference(iddoctorNew.getClass(), iddoctorNew.getIddoctor());
                appointment.setIddoctor(iddoctorNew);
            }
            if (idpatientNew != null) {
                idpatientNew = em.getReference(idpatientNew.getClass(), idpatientNew.getIdpatient());
                appointment.setIdpatient(idpatientNew);
            }
            List<Image> attachedImageListNew = new ArrayList<Image>();
            for (Image imageListNewImageToAttach : imageListNew) {
                imageListNewImageToAttach = em.getReference(imageListNewImageToAttach.getClass(), imageListNewImageToAttach.getIdimage());
                attachedImageListNew.add(imageListNewImageToAttach);
            }
            imageListNew = attachedImageListNew;
            appointment.setImageList(imageListNew);
            List<Note> attachedNoteListNew = new ArrayList<Note>();
            for (Note noteListNewNoteToAttach : noteListNew) {
                noteListNewNoteToAttach = em.getReference(noteListNewNoteToAttach.getClass(), noteListNewNoteToAttach.getIdnote());
                attachedNoteListNew.add(noteListNewNoteToAttach);
            }
            noteListNew = attachedNoteListNew;
            appointment.setNoteList(noteListNew);
            appointment = em.merge(appointment);
            if (iddoctorOld != null && !iddoctorOld.equals(iddoctorNew)) {
                iddoctorOld.getAppointmentList().remove(appointment);
                iddoctorOld = em.merge(iddoctorOld);
            }
            if (iddoctorNew != null && !iddoctorNew.equals(iddoctorOld)) {
                iddoctorNew.getAppointmentList().add(appointment);
                iddoctorNew = em.merge(iddoctorNew);
            }
            if (idpatientOld != null && !idpatientOld.equals(idpatientNew)) {
                idpatientOld.getAppointmentList().remove(appointment);
                idpatientOld = em.merge(idpatientOld);
            }
            if (idpatientNew != null && !idpatientNew.equals(idpatientOld)) {
                idpatientNew.getAppointmentList().add(appointment);
                idpatientNew = em.merge(idpatientNew);
            }
            for (Image imageListOldImage : imageListOld) {
                if (!imageListNew.contains(imageListOldImage)) {
                    imageListOldImage.setIdappointment(null);
                    imageListOldImage = em.merge(imageListOldImage);
                }
            }
            for (Image imageListNewImage : imageListNew) {
                if (!imageListOld.contains(imageListNewImage)) {
                    Appointment oldIdappointmentOfImageListNewImage = imageListNewImage.getIdappointment();
                    imageListNewImage.setIdappointment(appointment);
                    imageListNewImage = em.merge(imageListNewImage);
                    if (oldIdappointmentOfImageListNewImage != null && !oldIdappointmentOfImageListNewImage.equals(appointment)) {
                        oldIdappointmentOfImageListNewImage.getImageList().remove(imageListNewImage);
                        oldIdappointmentOfImageListNewImage = em.merge(oldIdappointmentOfImageListNewImage);
                    }
                }
            }
            for (Note noteListOldNote : noteListOld) {
                if (!noteListNew.contains(noteListOldNote)) {
                    noteListOldNote.setIdappointment(null);
                    noteListOldNote = em.merge(noteListOldNote);
                }
            }
            for (Note noteListNewNote : noteListNew) {
                if (!noteListOld.contains(noteListNewNote)) {
                    Appointment oldIdappointmentOfNoteListNewNote = noteListNewNote.getIdappointment();
                    noteListNewNote.setIdappointment(appointment);
                    noteListNewNote = em.merge(noteListNewNote);
                    if (oldIdappointmentOfNoteListNewNote != null && !oldIdappointmentOfNoteListNewNote.equals(appointment)) {
                        oldIdappointmentOfNoteListNewNote.getNoteList().remove(noteListNewNote);
                        oldIdappointmentOfNoteListNewNote = em.merge(oldIdappointmentOfNoteListNewNote);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = appointment.getIdappointment();
                if (findAppointment(id) == null) {
                    throw new NonexistentEntityException("The appointment with id " + id + " no longer exists.");
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
            Appointment appointment;
            try {
                appointment = em.getReference(Appointment.class, id);
                appointment.getIdappointment();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The appointment with id " + id + " no longer exists.", enfe);
            }
            Doctor iddoctor = appointment.getIddoctor();
            if (iddoctor != null) {
                iddoctor.getAppointmentList().remove(appointment);
                iddoctor = em.merge(iddoctor);
            }
            Patient idpatient = appointment.getIdpatient();
            if (idpatient != null) {
                idpatient.getAppointmentList().remove(appointment);
                idpatient = em.merge(idpatient);
            }
            List<Image> imageList = appointment.getImageList();
            for (Image imageListImage : imageList) {
                imageListImage.setIdappointment(null);
                imageListImage = em.merge(imageListImage);
            }
            List<Note> noteList = appointment.getNoteList();
            for (Note noteListNote : noteList) {
                noteListNote.setIdappointment(null);
                noteListNote = em.merge(noteListNote);
            }
            em.remove(appointment);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Appointment> findAppointmentEntities() {
        return findAppointmentEntities(true, -1, -1);
    }

    public List<Appointment> findAppointmentEntities(int maxResults, int firstResult) {
        return findAppointmentEntities(false, maxResults, firstResult);
    }

    private List<Appointment> findAppointmentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Appointment.class));
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

    public Appointment findAppointment(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Appointment.class, id);
        } finally {
            em.close();
        }
    }

    public int getAppointmentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Appointment> rt = cq.from(Appointment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
