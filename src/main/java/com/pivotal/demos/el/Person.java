package com.pivotal.demos.el;

import java.util.Date;

import javax.el.LambdaExpression;

public class Person {
	public static final String DEFAULT_GREETING = "Hello World!";

	private String name;
	private String address;
	private int age;
	private Date dob;

	public Person() {
	}

	public Person(String name, String address, int age, Date dob) {
		this.name = name;
		this.address = address;
		this.age = age;
		this.dob = dob;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", address=" + address + ", age=" + age
				+ ", dob=" + dob + "]";
	}

	public Person me() {
		return this;
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

	public boolean canDrink(LambdaExpression isOldEnough) {
		return (boolean) isOldEnough.invoke(this.age);
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public static long add(long x, long y) {
		return x + y;
	}
}
