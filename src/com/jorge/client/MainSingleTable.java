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
 * Inheritancy with SINGLE TABLE strategy in Animal superclass: => @Inheritance(strategy=InheritanceType.SINGLE_TABLE)
 * 
 * 		1.The class hierarchy is represented in one table. A discriminator column (DTYPE column) 
 * 		is created automatically in Animal table in DB and identifies the type and the subclass
 * 		
 * 		2.It is good for polymorphic queries and no joins required. Check queries below
 * 	    
 * 		3.All the properties in subclasses must not have not-null constraint
 * 
 * 		4.Good performance for derived class queries and no joins required. Check queries below
 *
 */
public class MainSingleTable {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		BasicConfigurator.configure(); // Necessary for configure log4j. It must be the first line in main method
	       					           // log4j.properties must be in /src directory

		Logger  logger = Logger.getLogger(MainSingleTable.class.getName());
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
			Cat cat = new Cat(); // DTYPE (discriminator) in DB Animal table will be Cat. Check Animal table in DB
			cat.setName("Garfield");
			
			logger.debug("setting dog name");
			Dog dog = new Dog(); // DTYPE (discriminator) in DB Animal table will be Dog. Check Animal table in DB
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
																			  // SQL: select animal0_.id as id2_0_, animal0_.name as name3_0_, animal0_.DTYPE as DTYPE1_0_ from Animal animal0_
			List<Animal> animals = query.getResultList();
			
			for(Animal animal : animals)
				System.out.println(animal); // For each animal in the return of Animal.java toString() method, calls to its own makeNoise() method in its respective entity
											// Garfield making meow, meow... noises => makeNoise() method in Cat entity, called from toString() method in Animal entity
											// Max making woof, woof... noises => makeNoise() method in Dog entity, called from toString() method in Animal entity
											// This is a Polymorphic Query
			
			
			
			
			/************************************
			 * Querying derived class => this means making an query on a subclass (Dog or Cat)
			 */
			
			logger.debug("beginning querying derived class transaction");
			
			query = em.createQuery("select dog from Dog dog"); // Dog table doesn't exist in DB. Check the queries below
															   // HQL: select dog from com.jorge.entity.Dog dog
															   // SQL: select dog0_.id as id2_0_, dog0_.name as name3_0_ from Animal dog0_ where dog0_.DTYPE='Dog'
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
  