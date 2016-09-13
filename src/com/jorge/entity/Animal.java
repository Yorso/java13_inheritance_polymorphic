package com.jorge.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
//@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
//@Inheritance(strategy=InheritanceType.JOINED)
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Animal {

	@Id
	//@GeneratedValue(strategy=GenerationType.AUTO) // Put this if @Inheritance(strategy=InheritanceType.SINGLE_TABLE) or @Inheritance(strategy=InheritanceType.JOINED)
	@GeneratedValue(strategy=GenerationType.TABLE) // Put this if @Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
	private Long id;

	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract String makeNoise();

	@Override
	public String toString() {
		return name + " making " + makeNoise() + " noises";
	}
	
	
}
