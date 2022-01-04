/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2020.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Adrien Foucart
 */
@Entity
@Table(name = "appointment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Appointment.findAll", query = "SELECT a FROM Appointment a"),
    @NamedQuery(name = "Appointment.findByIdappointment", query = "SELECT a FROM Appointment a WHERE a.idappointment = :idappointment"),
    @NamedQuery(name = "Appointment.findByAppointmenttime", query = "SELECT a FROM Appointment a WHERE a.appointmenttime = :appointmenttime"),
    @NamedQuery(name = "Appointment.findByPrice", query = "SELECT a FROM Appointment a WHERE a.price = :price")})
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idappointment")
    private Integer idappointment;
    @Column(name = "appointmenttime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointmenttime;
    @Lob
    @Column(name = "reason")
    private String reason;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private Float price;
    @OneToMany(mappedBy = "idappointment")
    private List<Image> imageList;
    @OneToMany(mappedBy = "idappointment")
    private List<Note> noteList;
    @JoinColumn(name = "iddoctor", referencedColumnName = "iddoctor")
    @ManyToOne(optional = false)
    private Doctor iddoctor;
    @JoinColumn(name = "idpatient", referencedColumnName = "idpatient")
    @ManyToOne(optional = false)
    private Patient idpatient;

    public Appointment() {
    }

    public Appointment(Integer idappointment) {
        this.idappointment = idappointment;
    }

    public Integer getIdappointment() {
        return idappointment;
    }

    public void setIdappointment(Integer idappointment) {
        this.idappointment = idappointment;
    }

    public Date getAppointmenttime() {
        return appointmenttime;
    }

    public void setAppointmenttime(Date appointmenttime) {
        this.appointmenttime = appointmenttime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @XmlTransient
    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    @XmlTransient
    public List<Note> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public Doctor getIddoctor() {
        return iddoctor;
    }

    public void setIddoctor(Doctor iddoctor) {
        this.iddoctor = iddoctor;
    }

    public Patient getIdpatient() {
        return idpatient;
    }

    public void setIdpatient(Patient idpatient) {
        this.idpatient = idpatient;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idappointment != null ? idappointment.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Appointment)) {
            return false;
        }
        Appointment other = (Appointment) object;
        if ((this.idappointment == null && other.idappointment != null) || (this.idappointment != null && !this.idappointment.equals(other.idappointment))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ulb.lisa.infoh400.labs2020.model.Appointment[ idappointment=" + idappointment + " ]";
    }
    
}
