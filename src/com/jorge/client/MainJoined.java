package com.jorge.client;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.jorge.entity.Animal;
import com.jorge.entity.Cat;
import com.jorge.entity.Dog;

/**
 * Inheritancy with JOINED strategy in Animal superclass: => @Inheritance(strategy=InheritanceType.JOINED)
 * 
 * 		1.The superclass has a table and each subclass has a table that contains only un-inherited properties
 * 		(the subclass tables have a primary key that is a foreign key of the superclass)
 * 		
 * 		2.Poor performance for polymorphic queries. Check queries below
 * 
 * 		3.Not too bad performance for derived class queries. Check queries below
 *
 */
public class MainJoined {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		BasicConfigurator.configure(); // Necessary for configure log4j. It must be the first line in main method
	       					           // log4j.properties must be in /src directory

		Logger  logger = Logger.getLogger(MainJoined.class.getName());
		logger.debug("log4j configured correctly and logger set");

		// How make the same things with JPA and Hibernate (commented)
		logger.debug("creating entity manager factory");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("helloworld"); // => SessionFactory sf = HibernateUtil.getSessionFactory(); HibernateUtil is a class created by us.
																						 // Persistence is imported from javax.persistence.Persistence package, it is not a class created by us
																						 // "helloworld" persistence unit name is the same name than "<persistence-unit name="helloworld"...>" element in persistence.xml file 
		logger.debug("creating entity manager");
		EntityManager em = emf.createEntityManager(); // => Session session = sf.openSession();
		
		logger.debug("getting transaction");
		EntityTransaction txn = em.getTransaction(); // => Transaction txn = session.getTransaction();
		
		try{
			/************************************
			 * Inheritance mapping
			 */
			
			logger.debug("beginning inheritance mapping transaction");
			txn.begin();
			
			logger.debug("setting cat name");
			Cat cat = new Cat();
			cat.setName("Garfield");
			
			logger.debug("setting dog name");
			Dog dog = new Dog();
			dog.setName("Max");
			
			em.persist(cat);
			em.persist(dog);
			
			logger.debug("making inheritance mapping commit");
			txn.commit();
			
			
			
			
			/************************************
			 * Polymorphic query
			 */
			
			logger.debug("beginning polymorphic query transaction");
			txn.begin();
			
			Query query = em.createQuery("select animal from Animal animal"); // HQL: select animal from com.jorge.entity.Animal animal
																			  // SQL: select animal0_.id as id1_0_, animal0_.name as name2_0_, case when animal0_1_.id is not null then 1 when animal0_2_.id is not null then 2 when animal0_.id is not null then 0 end as clazz_ from Animal animal0_ left outer join Dog animal0_1_ on animal0_.id=animal0_1_.id left outer join Cat animal0_2_ on animal0_.id=animal0_2_.id
			List<Animal> animals = query.getResultList();
			
			for(Animal animal : animals)
				System.out.println(animal); // Garfield making meow, meow... noises
											// Max making woof, woof... noises
											// This is a Polymorphic Query
			
			
			
			
			/************************************
			 * Querying derived class => this means making an query on a subclass (Dog or Cat)
			 */
			
			logger.debug("beginning querying derived class transaction");
			
			query = em.createQuery("select dog from Dog dog"); // HQL: select dog from com.jorge.entity.Dog dog
															   // SQL: select dog0_.id as id1_0_, dog0_1_.name as name2_0_ from Dog dog0_ inner join Animal dog0_1_ on dog0_.id=dog0_1_.id
			List<Dog> dogs = query.getResultList();
			
			for(Dog dog2 : dogs)
				System.out.println(dog2); // Max making woof, woof... noises
			
			
			logger.debug("making commits");
			txn.commit();
			
		}
		catch (Exception e) {
			if (txn != null) {
				logger.error("something was wrong, making rollback of transactions");
				txn.rollback(); // If something was wrong, we make rollback
			}
			logger.error("Exception: " + e.getMessage().toString());
		} finally {
			if (em != null) { // => if (session != null) {
				logger.debug("close session");
				em.close(); // => session.close();
			}
		}
	}

}
  