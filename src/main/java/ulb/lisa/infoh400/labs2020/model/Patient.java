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
@Table(name = "patient")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Patient.findAll", query = "SELECT p FROM Patient p"),
    @NamedQuery(name = "Patient.findByIdpatient", query = "SELECT p FROM Patient p WHERE p.idpatient = :idpatient"),
    @NamedQuery(name = "Patient.findByPhonenumber", query = "SELECT p FROM Patient p WHERE p.phonenumber = :phonenumber"),
    @NamedQuery(name = "Patient.findByStatus", query = "SELECT p FROM Patient p WHERE p.status = :status")})
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idpatient")
    private Integer idpatient;
    @Column(name = "phonenumber")
    private String phonenumber;
    @Basic(optional = false)
    @Column(name = "status")
    private String status;
    @OneToMany(mappedBy = "idpatient")
    private List<Image> imageList;
    @JoinColumn(name = "idperson", referencedColumnName = "idperson")
    @ManyToOne(optional = false)
    private Person idperson;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idpatient")
    private List<Appointment> appointmentList;

    public Patient() {
    }

    public Patient(Integer idpatient) {
        this.idpatient = idpatient;
    }

    public Patient(Integer idpatient, String status) {
        this.idpatient = idpatient;
        this.status = status;
    }

    public Integer getIdpatient() {
        return idpatient;
    }

    public void setIdpatient(Integer idpatient) {
        this.idpatient = idpatient;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlTransient
    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    public Person getIdperson() {
        return idperson;
    }

    public void setIdperson(Person idperson) {
        this.idperson = idperson;
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
        hash += (idpatient != null ? idpatient.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Patient)) {
            return false;
        }
        Patient other = (Patient) object;
        if ((this.idpatient == null && other.idpatient != null) || (this.idpatient != null && !this.idpatient.equals(other.idpatient))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idperson.toString();
    }
    
}
