/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2020.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Adrien Foucart
 */
@Entity
@Table(name = "doctor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Doctor.findAll", query = "SELECT d FROM Doctor d"),
    @NamedQuery(name = "Doctor.findByIddoctor", query = "SELECT d FROM Doctor d WHERE d.iddoctor = :iddoctor"),
    @NamedQuery(name = "Doctor.findByInami", query = "SELECT d FROM Doctor d WHERE d.inami = :inami"),
    @NamedQuery(name = "Doctor.findBySpecialty", query = "SELECT d FROM Doctor d WHERE d.specialty = :specialty")})
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "iddoctor")
    private Integer iddoctor;
    @Basic(optional = false)
    @Column(name = "inami")
    private String inami;
    @Column(name = "specialty")
    private String specialty;
    @JoinColumn(name = "idperson", referencedColumnName = "idperson")
    @ManyToOne(optional = false)
    private Person idperson;
    @OneToMany(mappedBy = "iddoctor")
    private List<Image> imageList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iddoctor")
    private List<Appointment> appointmentList;

    public Doctor() {
    }

    public Doctor(Integer iddoctor) {
        this.iddoctor = iddoctor;
    }

    public Doctor(Integer iddoctor, String inami) {
        this.iddoctor = iddoctor;
        this.inami = inami;
    }

    public Integer getIddoctor() {
        return iddoctor;
    }

    public void setIddoctor(Integer iddoctor) {
        this.iddoctor = iddoctor;
    }

    public String getInami() {
        return inami;
    }

    public void setInami(String inami) {
        this.inami = inami;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Person getIdperson() {
        return idperson;
    }

    public void setIdperson(Person idperson) {
        this.idperson = idperson;
    }

    @XmlTransient
    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    @XmlTransient
    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iddoctor != null ? iddoctor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Doctor)) {
            return false;
        }
        Doctor other = (Doctor) object;
        if ((this.iddoctor == null && other.iddoctor != null) || (this.iddoctor != null && !this.iddoctor.equals(other.iddoctor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idperson.toString();
    }
    
}
