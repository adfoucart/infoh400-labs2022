/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2020.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Adrien Foucart
 */
@Entity
@Table(name = "note")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Note.findAll", query = "SELECT n FROM Note n"),
    @NamedQuery(name = "Note.findByIdnote", query = "SELECT n FROM Note n WHERE n.idnote = :idnote"),
    @NamedQuery(name = "Note.findByDateadded", query = "SELECT n FROM Note n WHERE n.dateadded = :dateadded")})
public class Note implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idnote")
    private Integer idnote;
    @Basic(optional = false)
    @Column(name = "dateadded")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateadded;
    @Lob
    @Column(name = "content")
    private String content;
    @JoinColumn(name = "idappointment", referencedColumnName = "idappointment")
    @ManyToOne
    private Appointment idappointment;

    public Note() {
    }

    public Note(Integer idnote) {
        this.idnote = idnote;
    }

    public Note(Integer idnote, Date dateadded) {
        this.idnote = idnote;
        this.dateadded = dateadded;
    }

    public Integer getIdnote() {
        return idnote;
    }

    public void setIdnote(Integer idnote) {
        this.idnote = idnote;
    }

    public Date getDateadded() {
        return dateadded;
    }

    public void setDateadded(Date dateadded) {
        this.dateadded = dateadded;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Appointment getIdappointment() {
        return idappointment;
    }

    public void setIdappointment(Appointment idappointment) {
        this.idappointment = idappointment;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idnote != null ? idnote.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Note)) {
            return false;
        }
        Note other = (Note) object;
        if ((this.idnote == null && other.idnote != null) || (this.idnote != null && !this.idnote.equals(other.idnote))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ulb.lisa.infoh400.labs2020.model.Note[ idnote=" + idnote + " ]";
    }
    
}
