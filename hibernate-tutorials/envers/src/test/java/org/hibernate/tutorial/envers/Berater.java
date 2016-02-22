package org.hibernate.tutorial.envers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;

@Entity
@Audited
public class Berater {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;

	@OneToMany(mappedBy = "berater", cascade = CascadeType.PERSIST)
	private List<Hierarchie> hierarchieByBerater;

	public Berater() {
		super();
	}

	public void addHierarchie(Hierarchie h) {
		if (getHierarchieByBerater() == null) {
			setHierarchieByBerater(new ArrayList<Hierarchie>());
		}
		getHierarchieByBerater().add(h);
	}

	public Berater(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return "Berater [id=" + id + ", name=" + name + ", aktuellerVorgesetzter="
				+ (getVorgesetzter() == null ? "[null]" : getVorgesetzter().getName()) + "]";
	}

	public List<Hierarchie> getHierarchieByBerater() {
		return hierarchieByBerater;
	}

	public void setHierarchieByBerater(List<Hierarchie> hierarchieByBerater) {
		this.hierarchieByBerater = hierarchieByBerater;
	}

	public Berater getVorgesetzter() {
		Berater vorgesetzter = null;
		if (hierarchieByBerater != null) {
			for (Hierarchie hierarchie : hierarchieByBerater) {
				if (hierarchie.gueltigAm(new Date())) {
					vorgesetzter = hierarchie.getVorgesetzter();
				}
			}
		}
		return vorgesetzter;
	}

	public Hierarchie getHierarchie() {
		Hierarchie result = null;
		if (hierarchieByBerater != null) {
			for (Hierarchie hierarchie : hierarchieByBerater) {
				if (hierarchie.gueltigAm(new Date())) {
					result = hierarchie;
				}
			}
		}
		return result;
	}
}
