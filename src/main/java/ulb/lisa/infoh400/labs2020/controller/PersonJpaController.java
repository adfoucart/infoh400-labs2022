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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import ulb.lisa.infoh400.labs2020.controller.exceptions.IllegalOrphanException;
import ulb.lisa.infoh400.labs2020.controller.exceptions.NonexistentEntityException;
import ulb.lisa.infoh400.labs2020.model.Person;

/**
 *
 * @author Adrien Foucart
 */
public class PersonJpaController implements Serializable {

    public PersonJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Person person) {
        if (person.getDoctorList() == null) {
            person.setDoctorList(new ArrayList<Doctor>());
        }
        if (person.getPatientList() == null) {
            person.setPatientList(new ArrayList<Patient>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Doctor> attachedDoctorList = new ArrayList<Doctor>();
            for (Doctor doctorListDoctorToAttach : person.getDoctorList()) {
                doctorListDoctorToAttach = em.getReference(doctorListDoctorToAttach.getClass(), doctorListDoctorToAttach.getIddoctor());
                attachedDoctorList.add(doctorListDoctorToAttach);
            }
            person.setDoctorList(attachedDoctorList);
            List<Patient> attachedPatientList = new ArrayList<Patient>();
            for (Patient patientListPatientToAttach : person.getPatientList()) {
                patientListPatientToAttach = em.getReference(patientListPatientToAttach.getClass(), patientListPatientToAttach.getIdpatient());
                attachedPatientList.add(patientListPatientToAttach);
            }
            person.setPatientList(attachedPatientList);
            em.persist(person);
            for (Doctor doctorListDoctor : person.getDoctorList()) {
                Person oldIdpersonOfDoctorListDoctor = doctorListDoctor.getIdperson();
                doctorListDoctor.setIdperson(person);
                doctorListDoctor = em.merge(doctorListDoctor);
                if (oldIdpersonOfDoctorListDoctor != null) {
                    oldIdpersonOfDoctorListDoctor.getDoctorList().remove(doctorListDoctor);
                    oldIdpersonOfDoctorListDoctor = em.merge(oldIdpersonOfDoctorListDoctor);
                }
            }
            for (Patient patientListPatient : person.getPatientList()) {
                Person oldIdpersonOfPatientListPatient = patientListPatient.getIdperson();
                patientListPatient.setIdperson(person);
                patientListPatient = em.merge(patientListPatient);
                if (oldIdpersonOfPatientListPatient != null) {
                    oldIdpersonOfPatientListPatient.getPatientList().remove(patientListPatient);
                    oldIdpersonOfPatientListPatient = em.merge(oldIdpersonOfPatientListPatient);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Person person) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person persistentPerson = em.find(Person.class, person.getIdperson());
            List<Doctor> doctorListOld = persistentPerson.getDoctorList();
            List<Doctor> doctorListNew = person.getDoctorList();
            List<Patient> patientListOld = persistentPerson.getPatientList();
            List<Patient> patientListNew = person.getPatientList();
            List<String> illegalOrphanMessages = null;
            for (Doctor doctorListOldDoctor : doctorListOld) {
                if (!doctorListNew.contains(doctorListOldDoctor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Doctor " + doctorListOldDoctor + " since its idperson field is not nullable.");
                }
            }
            for (Patient patientListOldPatient : patientListOld) {
                if (!patientListNew.contains(patientListOldPatient)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Patient " + patientListOldPatient + " since its idperson field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Doctor> attachedDoctorListNew = new ArrayList<Doctor>();
            for (Doctor doctorListNewDoctorToAttach : doctorListNew) {
                doctorListNewDoctorToAttach = em.getReference(doctorListNewDoctorToAttach.getClass(), doctorListNewDoctorToAttach.getIddoctor());
                attachedDoctorListNew.add(doctorListNewDoctorToAttach);
            }
            doctorListNew = attachedDoctorListNew;
            person.setDoctorList(doctorListNew);
            List<Patient> attachedPatientListNew = new ArrayList<Patient>();
            for (Patient patientListNewPatientToAttach : patientListNew) {
                patientListNewPatientToAttach = em.getReference(patientListNewPatientToAttach.getClass(), patientListNewPatientToAttach.getIdpatient());
                attachedPatientListNew.add(patientListNewPatientToAttach);
            }
            patientListNew = attachedPatientListNew;
            person.setPatientList(patientListNew);
            person = em.merge(person);
            for (Doctor doctorListNewDoctor : doctorListNew) {
                if (!doctorListOld.contains(doctorListNewDoctor)) {
                    Person oldIdpersonOfDoctorListNewDoctor = doctorListNewDoctor.getIdperson();
                    doctorListNewDoctor.setIdperson(person);
                    doctorListNewDoctor = em.merge(doctorListNewDoctor);
                    if (oldIdpersonOfDoctorListNewDoctor != null && !oldIdpersonOfDoctorListNewDoctor.equals(person)) {
                        oldIdpersonOfDoctorListNewDoctor.getDoctorList().remove(doctorListNewDoctor);
                        oldIdpersonOfDoctorListNewDoctor = em.merge(oldIdpersonOfDoctorListNewDoctor);
                    }
                }
            }
            for (Patient patientListNewPatient : patientListNew) {
                if (!patientListOld.contains(patientListNewPatient)) {
                    Person oldIdpersonOfPatientListNewPatient = patientListNewPatient.getIdperson();
                    patientListNewPatient.setIdperson(person);
                    patientListNewPatient = em.merge(patientListNewPatient);
                    if (oldIdpersonOfPatientListNewPatient != null && !oldIdpersonOfPatientListNewPatient.equals(person)) {
                        oldIdpersonOfPatientListNewPatient.getPatientList().remove(patientListNewPatient);
                        oldIdpersonOfPatientListNewPatient = em.merge(oldIdpersonOfPatientListNewPatient);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = person.getIdperson();
                if (findPerson(id) == null) {
                    throw new NonexistentEntityException("The person with id " + id + " no longer exists.");
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
            Person person;
            try {
                person = em.getReference(Person.class, id);
                person.getIdperson();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The person with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Doctor> doctorListOrphanCheck = person.getDoctorList();
            for (Doctor doctorListOrphanCheckDoctor : doctorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Person (" + person + ") cannot be destroyed since the Doctor " + doctorListOrphanCheckDoctor + " in its doctorList field has a non-nullable idperson field.");
            }
            List<Patient> patientListOrphanCheck = person.getPatientList();
            for (Patient patientListOrphanCheckPatient : patientListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Person (" + person + ") cannot be destroyed since the Patient " + patientListOrphanCheckPatient + " in its patientList field has a non-nullable idperson field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(person);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Person> findPersonEntities() {
        return findPersonEntities(true, -1, -1);
    }

    public List<Person> findPersonEntities(int maxResults, int firstResult) {
        return findPersonEntities(false, maxResults, firstResult);
    }

    private List<Person> findPersonEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Person.class));
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

    public Person findPerson(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Person.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Person> rt = cq.from(Person.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
