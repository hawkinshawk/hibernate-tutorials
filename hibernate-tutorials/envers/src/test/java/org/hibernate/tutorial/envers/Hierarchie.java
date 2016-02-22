package org.hibernate.tutorial.envers;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

@Entity
@Audited
public class Hierarchie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.DATE)
	@Column(name = "gueltig_von")
	private Date gueltigVon;

	@Temporal(TemporalType.DATE)
	@Column(name = "gueltig_bis")
	private Date gueltigBis;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "berater_id")
	private Berater berater;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vorgesetzter_id")
	private Berater vorgesetzter;

	public Hierarchie(Date gueltigVon, Date gueltigBis, Berater berater, Berater vorgesetzter) {
		super();
		this.gueltigVon = gueltigVon;
		this.gueltigBis = gueltigBis;
		this.berater = berater;
		this.vorgesetzter = vorgesetzter;
	}

	public Hierarchie() {
		super();
	}

	public Berater getVorgesetzter() {
		return vorgesetzter;
	}

	public boolean gueltigAm(Date date) {
		return (getGueltigVon().compareTo(date) <= 0) && (getGueltigBis().compareTo(date) >= 0);
	}

	public void setVorgesetzter(Berater vorgesetzter) {
		this.vorgesetzter = vorgesetzter;
	}

	public Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Hierarchie [id=" + id + ", gueltigVon=" + gueltigVon.toString() + ", gueltigBis="
				+ gueltigBis.toString() + ", berater=" + (berater == null ? "[null]" : berater.getName())
				+ ", vorgesetzter=" + (vorgesetzter == null ? "[null]" : vorgesetzter.getName()) + "]";
	}

	public Date getGueltigVon() {
		return gueltigVon;
	}

	public void setGueltigVon(Date gueltigVon) {
		this.gueltigVon = gueltigVon;
	}

	public Date getGueltigBis() {
		return gueltigBis;
	}

	public void setGueltigBis(Date gueltigBis) {
		this.gueltigBis = gueltigBis;
	}

	public Berater getBerater() {
		return berater;
	}

	public void setBerater(Berater berater) {
		this.berater = berater;
	}

}
