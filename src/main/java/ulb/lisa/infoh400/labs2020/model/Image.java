/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2020.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Adrien Foucart
 */
@Entity
@Table(name = "image")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Image.findAll", query = "SELECT i FROM Image i"),
    @NamedQuery(name = "Image.findByIdimage", query = "SELECT i FROM Image i WHERE i.idimage = :idimage"),
    @NamedQuery(name = "Image.findByInstanceuid", query = "SELECT i FROM Image i WHERE i.instanceuid = :instanceuid"),
    @NamedQuery(name = "Image.findByStudyuid", query = "SELECT i FROM Image i WHERE i.studyuid = :studyuid"),
    @NamedQuery(name = "Image.findBySeriesuid", query = "SELECT i FROM Image i WHERE i.seriesuid = :seriesuid"),
    @NamedQuery(name = "Image.findByPatientDicomIdentifier", query = "SELECT i FROM Image i WHERE i.patientDicomIdentifier = :patientDicomIdentifier")})
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idimage")
    private Integer idimage;
    @Basic(optional = false)
    @Column(name = "instanceuid")
    private String instanceuid;
    @Column(name = "studyuid")
    private String studyuid;
    @Column(name = "seriesuid")
    private String seriesuid;
    @Column(name = "patient_dicom_identifier")
    private String patientDicomIdentifier;
    @JoinColumn(name = "idappointment", referencedColumnName = "idappointment")
    @ManyToOne
    private Appointment idappointment;
    @JoinColumn(name = "iddoctor", referencedColumnName = "iddoctor")
    @ManyToOne
    private Doctor iddoctor;
    @JoinColumn(name = "idpatient", referencedColumnName = "idpatient")
    @ManyToOne
    private Patient idpatient;

    public Image() {
    }

    public Image(Integer idimage) {
        this.idimage = idimage;
    }

    public Image(Integer idimage, String instanceuid) {
        this.idimage = idimage;
        this.instanceuid = instanceuid;
    }

    public Integer getIdimage() {
        return idimage;
    }

    public void setIdimage(Integer idimage) {
        this.idimage = idimage;
    }

    public String getInstanceuid() {
        return instanceuid;
    }

    public void setInstanceuid(String instanceuid) {
        this.instanceuid = instanceuid;
    }

    public String getStudyuid() {
        return studyuid;
    }

    public void setStudyuid(String studyuid) {
        this.studyuid = studyuid;
    }

    public String getSeriesuid() {
        return seriesuid;
    }

    public void setSeriesuid(String seriesuid) {
        this.seriesuid = seriesuid;
    }

    public String getPatientDicomIdentifier() {
        return patientDicomIdentifier;
    }

    public void setPatientDicomIdentifier(String patientDicomIdentifier) {
        this.patientDicomIdentifier = patientDicomIdentifier;
    }

    public Appointment getIdappointment() {
        return idappointment;
    }

    public void setIdappointment(Appointment idappointment) {
        this.idappointment = idappointment;
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
        hash += (idimage != null ? idimage.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Image)) {
            return false;
        }
        Image other = (Image) object;
        if ((this.idimage == null && other.idimage != null) || (this.idimage != null && !this.idimage.equals(other.idimage))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ulb.lisa.infoh400.labs2020.model.Image[ idimage=" + idimage + " ]";
    }
    
}
