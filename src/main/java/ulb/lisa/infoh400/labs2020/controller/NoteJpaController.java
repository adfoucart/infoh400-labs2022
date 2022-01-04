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
import ulb.lisa.infoh400.labs2020.model.Note;

/**
 *
 * @author Adrien Foucart
 */
public class NoteJpaController implements Serializable {

    public NoteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Note note) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Appointment idappointment = note.getIdappointment();
            if (idappointment != null) {
                idappointment = em.getReference(idappointment.getClass(), idappointment.getIdappointment());
                note.setIdappointment(idappointment);
            }
            em.persist(note);
            if (idappointment != null) {
                idappointment.getNoteList().add(note);
                idappointment = em.merge(idappointment);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Note note) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Note persistentNote = em.find(Note.class, note.getIdnote());
            Appointment idappointmentOld = persistentNote.getIdappointment();
            Appointment idappointmentNew = note.getIdappointment();
            if (idappointmentNew != null) {
                idappointmentNew = em.getReference(idappointmentNew.getClass(), idappointmentNew.getIdappointment());
                note.setIdappointment(idappointmentNew);
            }
            note = em.merge(note);
            if (idappointmentOld != null && !idappointmentOld.equals(idappointmentNew)) {
                idappointmentOld.getNoteList().remove(note);
                idappointmentOld = em.merge(idappointmentOld);
            }
            if (idappointmentNew != null && !idappointmentNew.equals(idappointmentOld)) {
                idappointmentNew.getNoteList().add(note);
                idappointmentNew = em.merge(idappointmentNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = note.getIdnote();
                if (findNote(id) == null) {
                    throw new NonexistentEntityException("The note with id " + id + " no longer exists.");
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
            Note note;
            try {
                note = em.getReference(Note.class, id);
                note.getIdnote();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The note with id " + id + " no longer exists.", enfe);
            }
            Appointment idappointment = note.getIdappointment();
            if (idappointment != null) {
                idappointment.getNoteList().remove(note);
                idappointment = em.merge(idappointment);
            }
            em.remove(note);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Note> findNoteEntities() {
        return findNoteEntities(true, -1, -1);
    }

    public List<Note> findNoteEntities(int maxResults, int firstResult) {
        return findNoteEntities(false, maxResults, firstResult);
    }

    private List<Note> findNoteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Note.class));
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

    public Note findNote(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Note.class, id);
        } finally {
            em.close();
        }
    }

    public int getNoteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Note> rt = cq.from(Note.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
