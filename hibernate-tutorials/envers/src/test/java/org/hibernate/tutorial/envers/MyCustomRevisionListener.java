package org.hibernate.tutorial.envers;

import org.hibernate.envers.RevisionListener;

public class MyCustomRevisionListener implements RevisionListener {

	public void newRevision(Object revisionEntity) {
		Revision exampleRevEntity = (Revision) revisionEntity;
		exampleRevEntity.setUser("sfincke");
		exampleRevEntity.setDate(exampleRevEntity.getRevisionDate());
	}

}
