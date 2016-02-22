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
package org.hibernate.tutorial.envers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;

import junit.framework.TestCase;

/**
 * Illustrates the set up and use of Envers.
 * <p>
 * This example is different from the others in that we really need to save
 * multiple revisions to the entity in order to get a good look at Envers in
 * action.
 *
 * @author Steve Ebersole
 */
public class EnversIllustrationTest extends TestCase {
	private EntityManagerFactory entityManagerFactory;

	@Override
	protected void setUp() throws Exception {
		// like discussed with regards to SessionFactory, an
		// EntityManagerFactory is set up once for an application
		// IMPORTANT: notice how the name here matches the name we gave the
		// persistence-unit in persistence.xml!
		entityManagerFactory = Persistence.createEntityManagerFactory("org.hibernate.tutorial.envers");
	}

	@Override
	protected void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	public void testBasicUsage() {
		// create a couple of events
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(new Event("Our very first event!", new Date()));
		entityManager.persist(new Event("A follow up event", new Date()));
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

		// so far the code is the same as we have seen in previous tutorials.
		// Now lets leverage Envers...

		// first lets create some revisions
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		Event myEvent = entityManager.find(Event.class, 2L);
		myEvent.setDate(new Date());
		myEvent.setTitle(myEvent.getTitle() + " (rescheduled)");
		entityManager.getTransaction().commit();
		entityManager.close();

		// and then use an AuditReader to look back through history
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		myEvent = entityManager.find(Event.class, 2L);
		assertEquals("A follow up event (rescheduled)", myEvent.getTitle());
		AuditReader reader = AuditReaderFactory.get(entityManager);
		Event firstRevision = reader.find(Event.class, 2L, 1);
		System.out.println("First Revision (" + firstRevision.getDate() + ") : " + firstRevision.getTitle());
		assertFalse(firstRevision.getTitle().equals(myEvent.getTitle()));
		assertFalse(firstRevision.getDate().equals(myEvent.getDate()));
		Event secondRevision = reader.find(Event.class, 2L, 2);
		System.out.println("Second Revision (" + secondRevision.getDate() + ") : " + secondRevision.getTitle());
		assertTrue(secondRevision.getTitle().equals(myEvent.getTitle()));
		assertTrue(secondRevision.getDate().equals(myEvent.getDate()));
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private Date getDateFromString(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Date result = null;
		try {
			result = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void testBeraterhierarchie() {
		// create a couple of entitys
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		Berater junior = new Berater("Juniorberater");
		Berater senior1 = new Berater("Seniorberater1");
		Berater senior2 = new Berater("Seniorberater2");
		Berater hk = new Berater("Hauptkonto");
		Hierarchie h1 = new Hierarchie(getDateFromString("01.02.2016"), getDateFromString("31.12.9999"), hk,
				null);
		hk.addHierarchie(h1);
		Hierarchie h2 = new Hierarchie(getDateFromString("01.02.2016"), getDateFromString("31.12.9999"), senior1,
				hk);
		senior1.addHierarchie(h2);
		
    	Hierarchie h3 = new Hierarchie(getDateFromString("01.02.2016"), getDateFromString("31.12.9999"), junior,
				senior1);
		senior1.addHierarchie(h3);
		
		Hierarchie h4 = new Hierarchie(getDateFromString("01.02.2016"), getDateFromString("31.12.9999"), senior2,
				hk);
		senior2.addHierarchie(h4);


		entityManager.persist(hk);
		entityManager.persist(senior1);
		entityManager.persist(senior2);
		entityManager.persist(junior);
		entityManager.getTransaction().commit();
		entityManager.close();

		// now lets pull events from the database and list them
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<Berater> result = entityManager.createQuery("from Berater", Berater.class).getResultList();
		System.out.println("alle Berater im System:");
		for (Berater b : result) {
			System.out.println(b.toString());
		}
		entityManager.getTransaction().commit();
		entityManager.close();

		// so far the code is the same as we have seen in previous tutorials.
		// Now lets leverage Envers...
		System.err.println("SLEEPING!!!");
		try {
			Thread.sleep(1000); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		// berater neu laden und hierarchie vom junior ändern
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		junior = query(entityManager, "Juniorberater");
		senior2 = query(entityManager, "Seniorberater2");

		Date hierarchieAltDatum = new Date();
		Hierarchie hierarchieAlt = junior.getHierarchie();
		hierarchieAlt.setGueltigBis(getDateFromString("15.02.2016"));

		Hierarchie hierarchieNeu = new Hierarchie(getDateFromString("16.02.2016"), getDateFromString("31.12.9999"),
				junior, senior2);

		junior.addHierarchie(hierarchieNeu);
		entityManager.persist(junior);

		entityManager.getTransaction().commit();
		entityManager.close();

		Date hierarchieNeuDatum = new Date();
		//
		// // Änderung am neuen Vorgesetzten
		// entityManager = entityManagerFactory.createEntityManager();
		// entityManager.getTransaction().begin();
		// willhelm = entityManager.find(Berater.class, 6L);
		// hauptkonto2 = entityManager.find(Berater.class, 2L);
		// willhelm.setVorgesetzter(hauptkonto2);
		// willhelm.setName("Willhelm2");
		// entityManager.getTransaction().commit();
		// entityManager.close();

		// and then use an AuditReader to look back through history
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<Hierarchie> helmutsHierarchie = entityManager
				.createQuery("from Hierarchie h where h.berater.id = 6 order by h.gueltigBis desc", Hierarchie.class)
				.getResultList();
		System.out.println("Helmuts Hierarchie aktuell:");
		for (Hierarchie hierarchie : helmutsHierarchie) {
			System.out.println(hierarchie.toString());
		}

		AuditReader reader = AuditReaderFactory.get(entityManager);
    	hierarchieAlt = reader.find(Hierarchie.class, 5L, hierarchieAltDatum);
		System.out.println("Zustand der Hierarchie zum Datum: " + hierarchieAltDatum.toString() + "; " + hierarchieAlt);
		hierarchieNeu = reader.find(Hierarchie.class, 5L, hierarchieNeuDatum);
		System.out.println("Zustand der Hierarchie zum Datum: " + hierarchieNeuDatum.toString() + "; " + hierarchieNeu);
	
		Number revisionNumberForAltDatum= reader.getRevisionNumberForDate(hierarchieAltDatum);
		System.out.println("Revision alt:" + revisionNumberForAltDatum.toString());
		Number revisionNumberForNeuDatum= reader.getRevisionNumberForDate(hierarchieNeuDatum);
		System.out.println("Revision neu:" + revisionNumberForNeuDatum.toString());
		
		List<Hierarchie> hierarchienAlt = reader.createQuery().forEntitiesAtRevision(Hierarchie.class, revisionNumberForAltDatum).getResultList();
		
		System.out.println("Hierarchien alt:");
		for (Hierarchie hierarchie : hierarchienAlt) {
			System.out.println(hierarchie.toString());
		}
		
		List<Hierarchie> hierarchienNeu = reader.createQuery().forEntitiesAtRevision(Hierarchie.class, revisionNumberForNeuDatum).getResultList();
		
		System.out.println("Hierarchien neu:");
		for (Hierarchie hierarchie : hierarchienNeu) {
			System.out.println(hierarchie.toString());
		}
		
		// Berater firstRevisionBerater = reader.find(Berater.class, 4L, 5);
		// System.out.println("Erste Version: " +
		// firstRevisionBerater.toString());
		// Berater secondRevisionBerater = reader.find(Berater.class, 4L, 7);
		// System.out.println("Zweite Version: " +
		// secondRevisionBerater.toString());
		// Berater thirdRevisionBerater = reader.find(Berater.class, 4L, 8);
		// System.out.println("Dritte Version: " +
		// thirdRevisionBerater.toString());
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private Berater query(EntityManager entityManager, String parameter) {
		Berater result;
		TypedQuery<Berater> query = entityManager.createQuery("from Berater b where b.name = :name", Berater.class);
		query.setParameter("name", parameter);
		result = query.getSingleResult();
		return result;
	}
}
