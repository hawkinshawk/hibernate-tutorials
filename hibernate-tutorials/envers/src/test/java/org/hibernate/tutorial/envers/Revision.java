package org.hibernate.tutorial.envers;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.DefaultTrackingModifiedEntitiesRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@RevisionEntity(MyCustomRevisionListener.class)
public class Revision extends DefaultTrackingModifiedEntitiesRevisionEntity {

	private String user;
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private static final long serialVersionUID = 2874819091816063580L;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
