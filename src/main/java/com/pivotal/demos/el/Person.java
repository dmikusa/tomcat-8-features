package com.pivotal.demos.el;

import java.util.Date;

public class Person {
	private String name;
	private String address;
	private int age;
	private Date dob;
	
	public Person() {
	}

	public Person(String name, String address, int age, Date date) {
		this.name = name;
		this.address = address;
		this.age = age;
		this.dob = date;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", address=" + address + ", age=" + age
				+ ", dob=" + dob + "]";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}
	
}
