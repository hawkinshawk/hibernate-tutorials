/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.tutorial.em;

import junit.framework.TestCase;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Illustrates basic use of Hibernate as a JPA provider.
 *
 * @author Steve Ebersole
 */
public class EntityManagerIllustrationTest extends TestCase {
    private EntityManagerFactory entityManagerFactory;

    @Override
    protected void setUp() throws Exception {
        // like discussed with regards to SessionFactory, an EntityManagerFactory is set up once for an application
        // 		IMPORTANT: notice how the name here matches the name we gave the persistence-unit in persistence.xml!
        entityManagerFactory = Persistence.createEntityManagerFactory("org.hibernate.tutorial.jpa");
    }

    @Override
    protected void tearDown() throws Exception {
        entityManagerFactory.close();
    }

	public void testBasicUsage() {
		// create a couple of events...
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist( new Event( "Our very first event!", new Date(), Status.APPROVED ) );
		entityManager.persist( new Event( "A follow up event", new Date(), Status.DENIED) );
		entityManager.getTransaction().commit();
		entityManager.close();

        // now lets pull events from the database and list them
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Event> result = entityManager.createQuery("from Event", Event.class).getResultList();
        for (Event event : result) {
            System.out.println("Event (" + event.getDate() + ") : " + event.getTitle());
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void testUnidirectional() {
        // create a couple of events...
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        AntragRef antragRef = new AntragRef(null, "123");
        Set antragRefs = new HashSet();
        antragRefs.add(antragRef);
        Kundendokumentation kundeninfo = new Kundendokumentation(null, "Kundeninfo", antragRefs);
        entityManager.persist(kundeninfo);
        entityManager.getTransaction().commit();
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
		kundeninfo = entityManager.find(Kundendokumentation.class, kundeninfo.getId());
		//kundeninfo.getAntragRefs().remove(0);
        kundeninfo.getAntragRefs().remove(antragRef);
		//antragRef = entityManager.find(AntragRef.class, antragRef.getId());
		//entityManager.remove(antragRef);
        entityManager.getTransaction().commit();
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
		kundeninfo = entityManager.find(Kundendokumentation.class, kundeninfo.getId());
		System.out.println(kundeninfo.toString());
		antragRef = entityManager.find(AntragRef.class, antragRef.getId());
		System.out.println(antragRef == null ? "Antrag Ref is [null]" : antragRef.toString());
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
