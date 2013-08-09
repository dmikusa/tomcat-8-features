package com.pivotal.demos.el;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELProcessor;

public class ElStandAloneDemo {
	
	private Person person;
	
	public static void main(String[] args) throws Exception {
		ElStandAloneDemo demo = new ElStandAloneDemo();
		demo.setupAndUse();
		demo.collectionsDemo();
	}
	
	/**
	 * Simple demonstration of setting up and using EL in a stand-alone environment
	 */
	public void setupAndUse() {
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
		// 1. Create an ELProcessor, provides the API for using EL
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
