package com.pivotal.demos.el;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELProcessor;
import javax.el.LambdaExpression;

public class ElStandAloneDemo {

	private Person person;

	public static void main(String[] args) throws Exception {
		ElStandAloneDemo demo = new ElStandAloneDemo();
		demo.setupAndUseDemo();
		demo.collectionsDemo();
		demo.lambdasDemo();
		demo.smallChangesDemo();
	}

	/**
	 * Simple demonstration of setting up and using EL in a stand-alone environment
	 */
	public void setupAndUseDemo() {
		System.out.println();
		System.out.println("--------------------------------------------------------------------------------------");
		System.out.println("     Simple EL Demo");
		System.out.println("--------------------------------------------------------------------------------------");

		// 1. Create an ELProcessor, provides the API for using EL
		ELProcessor processor = new ELProcessor();

		// 2. Make data available to the processor
		processor.defineBean("person", this.person);

		// 3. Run code
		System.out.println(processor.eval("person.name"));
	}

	/**
	 * Demonstration of collection and stream capabilities
	 */
	@SuppressWarnings("unchecked")
	public void collectionsDemo() {
		System.out.println();
		System.out.println("--------------------------------------------------------------------------------------");
		System.out.println("     Collections / Streams Demo");
		System.out.println("--------------------------------------------------------------------------------------");

		// Create an ELProcessor, provides the API for using EL
		ELProcessor processor = new ELProcessor();
		processor.defineBean("out", System.out);
		processor.defineBean("person", this.person);

		// EL allows you to easily create sets, lists and maps dynamically
		Set<Integer> set = (Set<Integer>) processor.eval("{1, 2, 3, 3, 2, 1}");
		System.out.println("Set -> " + set);

		List<Integer> list = (List<Integer>) processor.eval("[1, 2, 3, 3, 2, 1]");
		System.out.println("List -> " + list);

		Map<String,Integer> map = (Map<String, Integer>) processor.eval("{'one': 1, 'two': 2, 'three': 3}");
		System.out.println("Map -> " + map);

		// EL 3.0 adds a Stream & Pipeline API
		// To obtain a Stream from a Set or List, call .stream()
		System.out.println("Stream from Set ->" + processor.eval("{1,2,3}.stream()"));

		// To obtain a Stream from a Map, call .stream()
		System.out.println("Stream from Map -> " +
						processor.eval("{'one': 1, 'two': 2, 'three': 3}.entrySet().stream()"));

		// Some operations return a stream, which allows you to chain operations
		System.out.println("Chain of streams -> " +
						processor.eval("[1,4,5,3,2,2,4,5,7,9,8,6,7,0,9].stream().filter(x->x >3).distinct().sorted().toList()"));

		// Some more stream operations
		System.out.println("Map 1 -> " + processor.eval("[1,2,3,4].stream().map(x->x*2).toList()"));
		System.out.println("FlatMap -> " + processor.eval("['dog', 'cat', 'bird'].stream().flatMap(w->w.toCharArray().stream()).toList()"));
		System.out.println("Distinct -> " + processor.eval("[1,2,3,4,5,5,4,3,2,1].stream().distinct().toList()"));
		System.out.println("Sorted -> " + processor.eval("[1,2,3,4,5,5,4,3,2,1].stream().sorted().toList()"));
		System.out.println("Sorted -> " + processor.eval("[1,2,3,4,5,5,4,3,2,1].stream().sorted((x,y)->y-x).toList()")); // with comparator
		System.out.println("ForEach -> " + processor.eval("[1,2,3].stream().forEach(c->out.print(c))")); // always returns null
		System.out.println("Peek -> " + processor.eval("[1,2,3].stream().peek(c->out.print(c)).toList()")); // same as forEach, but returns stream
		System.out.print("Iterator ->");
		Iterator<Long> it = (Iterator<Long>) processor.eval("[1,2,3].stream().iterator()");
		while (it.hasNext()) {
			System.out.print(it.next());
		}
		System.out.println();
		System.out.println("Limit -> " + processor.eval("[1,2,3,4,5,5,4,3,2,1].stream().limit(5).toList()"));
		System.out.println("SubStream -> " + processor.eval("[1,2,3,4,5,5,4,3,2,1].stream().substream(5,8).toList()"));

		// Aggregate functions
		System.out.println("Reduce -> " + processor.eval("[1,2,3,4,5,6,5,4,3,2,1].stream().reduce((x,y)->x > y ? x : y).get()"));  // simulated max
		System.out.println("Max -> " + processor.eval("[1,2,3,4,5,6,5,4,3,2,1].stream().max().get()"));
		System.out.println("Min -> " + processor.eval("[0,2,3,4,5,6,5,4,3,2,1].stream().min().get()"));
		System.out.println("Average -> " + processor.eval("[0,2,3,4,5,6,5,4,3,2,1].stream().average().get()"));
		System.out.println("Sum -> " + processor.eval("[0,2,3,4,5,6,5,4,3,2,1].stream().sum()")); // returns num, not Optional
		System.out.println("Count -> " + processor.eval("[0,2,3,4,5,6,5,4,3,2,1].stream().count()")); // returns num, not Optional
		System.out.println("AnyMatch -> " + processor.eval("[0,2,3,4,5,6,5,4,3,2,1].stream().anyMatch(x->x<5).get()"));
		System.out.println("AllMatch -> " + processor.eval("[0,2,3,4,5,6,5,4,3,2,1].stream().allMatch(x->x<5).get()"));
		System.out.println("NoneMatch -> " + processor.eval("[0,2,3,4,5,6,5,4,3,2,1].stream().noneMatch(x->x<5).get()"));
		System.out.println("FindFirst -> " + processor.eval("[0,2,3,4,5,6,5,4,3,2,1].stream().findFirst().get()"));
	}

	/**
	 * Demonstration of Lambdas
	 */
	public void lambdasDemo() {
		System.out.println();
		System.out.println("--------------------------------------------------------------------------------------");
		System.out.println("     Lambda Demo");
		System.out.println("--------------------------------------------------------------------------------------");
		// Create an ELProcessor, provides the API for using EL
		ELProcessor processor = new ELProcessor();
		processor.defineBean("out", System.out);
		processor.defineBean("person", this.person);

		// Basic Lambda Evaluations
		System.out.println("Returns 64 -> " + processor.eval("()->64"));
		System.out.println("Returns x+1 where x=1 -> " + processor.eval("(x->x+1)(1)")); // (..) not required with one arg
		System.out.println("Returns x+y where x=1 and y=3 -> " + processor.eval("((x,y)->x+y)(1,3)"));

		// Can be invoked immediately (like previous example) or stored and evaluated later
		System.out.println("Square: " + processor.eval("sq = x->x*x; sq(5)"));
		System.out.println("Factorial: " + processor.eval("fact = n -> n==0? 1: n*fact(n-1); fact(5)"));

		// Can be used by custom classes and called via EL
		System.out.println("Can drink in the US: " + processor.eval("person.canDrink(age -> age >= 21)"));
		System.out.println("Can drink in the Canada: " + processor.eval("person.canDrink(age -> age >= 19)"));
		System.out.println("Can drink in the Germany: " + processor.eval("person.canDrink(age -> age >= 16)"));

		// Can be used in Java by calling LambdaExpression.invoke(..)
		LambdaExpression cube = (LambdaExpression) processor.eval("x -> x*x*x");
		System.out.println("Cube: " + cube.invoke(3));

		// Lambda expressions can be nested
		System.out.println("Cube Different: " + processor.eval("cu = x -> (y -> y*y)(x) * x; cu(4)"));

		// Scope of args is the body of the lambda expression
		System.out.println("Cube Different Again: " + processor.eval("cu = x -> (()-> x*x) * x; cu(4)"));

		// Args in nested scope with same name, hide args or expressions of same name
		System.out.println("Cube Fail: " + processor.eval("cu = x -> ((x)-> x*x)(1) * x; cu(4)"));
	}

	/**
	 * Demo of various small changes
	 */
	public void smallChangesDemo() {
		System.out.println();
		System.out.println("--------------------------------------------------------------------------------------");
		System.out.println("     Small Changes Demo");
		System.out.println("--------------------------------------------------------------------------------------");

		// Create an ELProcessor, provides the API for using EL
		ELProcessor processor = new ELProcessor();
		processor.defineBean("out", System.out);
		processor.defineBean("person", this.person);

		// Import Person, by default ELContext only import "java.lang.*" (used by static access below)
		processor.getELManager().importClass(Date.class.getName());
		processor.getELManager().importClass(Person.class.getName());

		// String concatenation ( + and cat have been removed)
		System.out.println("Concat(abc, def): " + processor.eval("'abc' += 'def'"));

		// Constructors & New Objects
		// BUG: constructor is not working at this time
//		System.out.println("new Boolean(true): " + processor.eval("Boolean(true)"));
//		System.out.println("new Integer('100'): " + processor.eval("Integer('100')"));
//		System.out.println("new Date(): " + processor.eval("Date()"));

		// Basic Assignments
		System.out.println("Assigning value to 'test': " + processor.eval("test = 'Hello World!'"));
		System.out.println("Get value of test with Java: " + processor.getValue("test", String.class));
		System.out.println("Get value of test with EL: " + processor.eval("test"));

		// Assignment can also invoke method as long as it returns an object
		System.out.println("Get Name: " + processor.eval("person.me().dob"));
//		System.out.println("Update DOB: " + processor.eval("person.dob = Date(person.dob.time + 86400000)"));
		System.out.println("Get Name: " + processor.eval("person.dob"));

		// Multiple statements
		System.out.println("Only displays last result: " + processor.eval("x = 5; 'Hi!'; 1 + 1; x = x + 5; x"));

		// Static Fields & Methods
		System.out.println("Static Field: " + processor.eval("Boolean.TRUE"));
		System.out.println("Custom Static Field: " + processor.eval("Person.DEFAULT_GREETING"));
		System.out.println("Static Method add(1,1): " + processor.eval("Person.add(1,1)"));
	}

	/**
	 * Creates data used by the demos.
	 */
	public ElStandAloneDemo() {
		try {
			this.person = new Person(
							"Homer Simpson",
							"742 Evergreen Terrace",
							39,
							new SimpleDateFormat("YYYY-MM-DD").parse("1956-05-12"));
		} catch(ParseException ex) { /* ignore */ }
	}
}
