package edu.uwm.cs351;

import java.util.AbstractCollection;
import java.util.function.Consumer;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/*
 * Andrew Le
 * Homework 5, CS 351
 */

/**
 * A variant of the ApptBook ADT that follows the Collection model.
 * In particular, it has no sense of a current element.
 * All access to elements by the client must be through the iterator.
 * The {@link #add(Appointment)} method should add at the correct spot in sorted order in the collection.
 */
public class NewApptBook extends AbstractCollection<Appointment> implements Cloneable {
	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };
	
	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	// TODO: Add all the contents here.
	// Remember:
	// - All public methods not marked @Override must be fully documented with javadoc
	// - A @Override method must be marked 'required', 'implementation', or 'efficiency'
	// - You need to define and check the data structure invariant
	//   (quite different from Homework #4)
	// - You should define a nested iterator class called MyIterator (with its own data structure), 
	//   and then the iterator() method simply returns a new instance.
	// You are permitted to copy in any useful code/comments from the Homework #3 or #4 solution.
	// But do not include any of the cursor-related methods, and in particular,
	// make sure you have no "cursor" field.
	
	private static class Node {
		Appointment data;
		Node next;
		Node prev;
		
		/*
		 * Constructs a new Node object given an Appointment, a next node,
		 * and a previous node.
		 */
		public Node(Appointment a) {
			data = a;
			next = prev = null;
		}
		
	}
	
	private int manyItems;
	private int version;
	Node head;
	Node tail;
	
	public NewApptBook() {
		
		manyItems = 0;
		version = 0;
		head = tail = null;
		
		assert wellFormed() : "invariant failed at end of constructor";
	}
	
	/*
	 * wellFormed method that checks for 7 invariants
	 * 1. The prev and next links must match up: whenever one node's next
	 * field points to another, the one's prev field must point back.
	 * 2. The tail pointer can be null if and only if the head pointer is null.
	 * 3. If the tail pointer is not null, it must be reachable from the head pointer
	 * and have no nodes after it.
	 * 4. Similarly, the head cannot have any nodes before it. (But this should be
	 * checked at the beginning so we can avoid getting suck in a cycle)
	 * 5. The declared number of items must be the same as the actual number of nodes
	 * in the list, starting from the head.
	 * 6. None of the element ("data" of the nodes) can be null.
	 * 7. The elements must be in non-decreasing order according to natural ordering.
	 */
	public boolean wellFormed() {
		
		//Invariant 4
		if (head.prev != null) {
			return report("the head has a previous node before it, head.prev != null.");
		}
		
		//invariant 3
		if (tail.next != null) {
			return report("the tail has a next node after it, tail.next != null.");
		}
		Node t;
		for(t = head; t != null; t = t.next) {
			if (t == tail) {
				break;
			}
		}
		if (t != tail) {
			return report("tail was not reachable by head.");
		}
		
		//invariant 5
		int count = 0;
		for (Node i = head; i != null; i = i.next) {
			if (i != null) {
				count++;
			}
		}
		if (count != manyItems) {
			return report("manyItems is not equal to the amount of elements.");
		}
		
		//invariant 1
		for (Node i = head; i != null; i = i.next) {
			if (i.next != null && i.next.prev != i) {
				return report("the next Node's prev does not point back.");
			}
		}
		
		//invariant 2
		if (head == null && tail != null) {
			return report("head is null, but tail is not.");
		}
		
		//invariant 6
		for (Node i = head; i != null; i = i.next) {
			if (i.data == null) {
				return report("a data in a Node is null.");
			}
		}
		
		//invariant 7
		for (Node i = head; i != null; i = i.next) {
			if (i.next != null && i.data.compareTo(i.next.data) > 0) {
				return report("not well ordered.");
			}
		}
		
		return true;
	}
	
	public void add(){
		
	}
	
	@Override //Implementation
	public int size() {
		// TODO Auto-generated method stub
		return manyItems;
	}
	
	@Override //required
	public Iterator<Appointment> iterator() {
		// TODO Auto-generated method stub
		MyIterator it = new MyIterator();
		return it;
	}

	private class MyIterator implements Iterator<Appointment> 
	{
		private Node cursor;
		private boolean canRemove = false;
		private int colVersion = version;

		public boolean wellFormed() {
			// TODO
			// - same first two tests as in Homework #3
			// - then check if the cursor is in the list
			// - it must be in list if we can remove the 
			//   current element or if it's not null
			return true;
		}
		
		private void checkVersion() {
			if (colVersion != version) throw new ConcurrentModificationException("stale iterator");
		}
		
		private static boolean doReport = true;
		private boolean report(String error) {
			//Prints out stuff
			if (doReport) {
				System.out.println("Invariant error: " + error);
			}
			return false;
		}
		
		@Override //required
		public boolean hasNext() {
			//Returns true if next is less than many items, and false otherwise
			assert wellFormed();
			

			return false;
		}

		@Override //required
		public Appointment next() {
			//Checks to see if there exists an element beyond
			assert wellFormed();
			
			
			assert wellFormed();
			
			return null;
		}

		@Override //required
		public Iterator<Appointment> iterator() {
			// TODO Auto-generated method stub
			return this;

		}
		
		public MyIterator() {

		}
		
		public MyIterator(int Index) {


		}

	}
	
	public static class TestInvariantChecker extends TestCase {
		Time now = new Time();
		Appointment e1 = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Appointment e2 = new Appointment(new Period(now,Duration.DAY),"2: current");
		Appointment e3 = new Appointment(new Period(now.add(Duration.HOUR),Duration.HOUR),"3: eat");
		Appointment e4 = new Appointment(new Period(now.add(Duration.HOUR.scale(2)),Duration.HOUR.scale(8)),"4: sleep");
		Appointment e5 = new Appointment(new Period(now.add(Duration.DAY),Duration.DAY),"5: tomorrow");

		private int reports = 0;
		
		private void assertWellFormed(Object s, boolean expected) {
			reports = 0;
			Consumer<String> savedReporter = reporter;
			try {
				reporter = (String message) -> {
					++reports;
					if (message == null || message.trim().isEmpty()) {
						assertFalse("Uninformative report is not acceptable", true);
					}
					if (expected) {
						assertFalse("Reported error incorrectly: " + message, true);
					}
				};
				if (s instanceof NewApptBook) {
					assertEquals(expected, ((NewApptBook)s).wellFormed());
				} else {
					assertEquals(expected, ((NewApptBook.MyIterator)s).wellFormed());
				}
				if (!expected) {
					assertEquals("Expected exactly one invariant error to be reported", 1, reports);
				}
				reporter = null;
			} finally {
				reporter = savedReporter;
			}
		}
		
		protected Node newNode(Appointment a, Node p, Node n) {
			Node result = new Node(a);
			result.prev = p;
			result.next = n;
			result.data = a;
			return result;
		}
		
		protected Node newNode(Appointment a) {
			return newNode(a, null, null);
		}

		NewApptBook self;
		NewApptBook.MyIterator selfit;
		
		protected void setUp() {
			self = new NewApptBook();
			self.head = self.tail = null;
			self.manyItems = 0;
			self.version = 17;
			selfit = self.new MyIterator();
			selfit.canRemove = false;
			selfit.cursor = null;
			selfit.colVersion = 17;
		}

		public void testA0() {
			assertWellFormed(self, true);
		}
		
		public void testA1() {
			self.tail = new Node(e1);
			assertWellFormed(self, false);
		}
		
		public void testA2() {
			self.manyItems = -1;
			assertWellFormed(self, false);
			self.manyItems = 1;
			assertWellFormed(self, false);
		}
		
		public void testA3() {
			self.head = self.tail = newNode(null);
			assertWellFormed(self, false);
			self.manyItems = 1;
			assertWellFormed(self, false);
		}
		
		public void testB0() {
			self.head = self.tail = newNode(e2);
			assertWellFormed(self, false);
			self.manyItems = 1;
			assertWellFormed(self, true);
		}
		
		public void testB1() {
			self.head = newNode(e3);
			self.tail = newNode(e3);
			self.manyItems = 1;
			assertWellFormed(self, false);
			self.manyItems = 2;
			assertWellFormed(self, false);
		}
		
		public void testB2() {
			self.head = newNode(e3);
			self.tail = null;
			self.manyItems = 1;
			assertWellFormed(self, false);
			self.manyItems = 0;
			assertWellFormed(self, false);
		}
		
		public void testB3() {
			self.head = self.tail = newNode(e2);
			self.head.prev = newNode(e1,null,self.head);
			self.tail.next = newNode(e3,self.tail,null);
			self.manyItems = 0;
			assertWellFormed(self, false);
			self.manyItems = 1;
			assertWellFormed(self, false);
			self.manyItems = 2;
			assertWellFormed(self, false);
			self.manyItems = 3;
			assertWellFormed(self, false);			
		}
		
		public void testB4() {
			self.head = self.tail = newNode(e1);
			self.head.prev = self.head;
			self.tail.next = self.tail;
			self.manyItems = 0;
			assertWellFormed(self, false);
			self.manyItems = 1;
			assertWellFormed(self, false);
			self.manyItems = 2;
			assertWellFormed(self, false);
			self.manyItems = 3;
			assertWellFormed(self, false);
		}
		
		public void testC0() {
			self.head = newNode(e4);
			self.tail = newNode(e5);
			self.manyItems = 2;
			assertWellFormed(self, false);
			self.head.next = self.tail;
			assertWellFormed(self, false);
			self.tail.prev = self.head;
			assertWellFormed(self, true);
		}
		
		public void testC1() {
			self.head = newNode(e2);
			self.tail = newNode(e1);
			self.head.next = self.tail;
			self.tail.prev = self.head;
			self.manyItems = 2;
			assertWellFormed(self, false);
		}
		
		public void testC2() {
			self.head = newNode(e3);
			self.tail = newNode(e3);
			self.head.prev = self.head.next = self.tail;
			self.tail.prev = self.tail.next = self.head;
			self.manyItems = 2;
			assertWellFormed(self, false);			
			self.manyItems = 3;
			assertWellFormed(self, false);			
			self.manyItems = Integer.MAX_VALUE;
			assertWellFormed(self, false);			
		}
		
		public void testC3() {
			self.head = newNode(e3);
			self.tail = newNode(e3);
			self.manyItems = 2;
			self.head.next = self.tail;
			self.tail.prev = self.head;
			assertWellFormed(self, true);
			self.tail.next = newNode(e3,self.tail,null);
			assertWellFormed(self, false);
			self.tail.next = null;
			self.tail = null;
			assertWellFormed(self, false);
			self.tail = new Node(e3);
			self.tail.prev = self.head;
			assertWellFormed(self, false);
			self.tail.next = self.tail;
			self.tail.prev = self.tail;
			assertWellFormed(self, false);
		}
		
		public void testD0() {
			Node n1 = newNode(e1);
			Node n2 = newNode(e2);
			Node n3 = newNode(e3);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			self.head = n1;
			self.tail = n3;
			self.manyItems = 3;
			assertWellFormed(self, true);
		}
		
		public void testD1() {
			Node n1 = newNode(e1);
			Node n2 = newNode(e2);
			Node n3 = newNode(e3);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; // n3.prev = n2;
			self.head = n1;
			self.tail = n3;
			self.manyItems = 3;
			assertWellFormed(self, false);
			
			n3.prev = n2;
			n2.prev = null;
			assertWellFormed(self, false);
			
			n2.prev = newNode(e1,null,n2);
			assertWellFormed(self, false);
			
			n2.prev = n1;
			n3.prev = newNode(e2,n1,n3);
			assertWellFormed(self, false);
		}
		
		public void testD2() {
			Node n1 = newNode(e4);
			Node n2 = newNode(e4);
			Node n3 = newNode(e4);
			Node n4 = newNode(e4);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			n3.next = n4; n4.prev = n3;
			self.manyItems = 4;
			self.head = n1;
			self.tail = n4;
			assertWellFormed(self, true);
			
			self.tail = n3;
			assertWellFormed(self, false);
			self.manyItems = 3;
			assertWellFormed(self, false);
			
			self.tail = n4;
			self.head = n2;
			assertWellFormed(self, false);
		}
		
		public void testD3() {
			Node n1 = newNode(e4);
			Node n2 = newNode(e4);
			Node n3 = newNode(e4);
			Node n4 = newNode(e4);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			n3.next = n4; n4.prev = n3;
			self.manyItems = 4;
			self.head = n1;
			self.tail = n4;
			assertWellFormed(self, true);
			
			n1.data = null;
			assertWellFormed(self, false);
			n1.data = e4;
			
			n2.data = null;
			assertWellFormed(self, false);
			n2.data = e4;
			
			n3.data = null;
			assertWellFormed(self, false);
			n3.data = e4;
			
			n4.data = null;
			assertWellFormed(self, false);
			n4.data = e4;
			
			assertWellFormed(self, true);
		}
		
		public void testE0() {
			Node n1 = newNode(e1);
			Node n2 = newNode(e2);
			Node n3 = newNode(e3);
			Node n4 = newNode(e4);
			Node n5 = newNode(e5);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			n3.next = n4; n4.prev = n3;
			n4.next = n5; n5.prev = n4;
			self.head = n1;
			self.tail = n5;
			self.manyItems = 5;
			assertWellFormed(self, true);
			
			n1.prev = newNode(null,null,n1);
			assertWellFormed(self, false);
			n1.prev = null;
			
			n2.prev = newNode(e1,null,n2);
			assertWellFormed(self, false);
			n2.prev = null;
			assertWellFormed(self, false);
			n2.prev = n1;
			
			n3.prev = newNode(e2,null,n3);
			assertWellFormed(self, false);
			n3.prev = null;
			assertWellFormed(self, false);
			n3.prev = n2;
			
			n4.prev = newNode(e3,null,n4);
			assertWellFormed(self, false);
			n4.prev = null;
			assertWellFormed(self, false);
			n4.prev = n3;
			
			n5.prev = newNode(e4,null,n5);
			assertWellFormed(self, false);
			n5.prev = null;
			assertWellFormed(self, false);
			n5.prev = n4;
			
			assertWellFormed(self, true);
		}
		
		public void testE1() {
			Node n1 = newNode(e5);
			Node n2 = newNode(e5);
			Node n3 = newNode(e5);
			Node n4 = newNode(e5);
			Node n5 = newNode(e5);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			n3.next = n4; n4.prev = n3;
			n4.next = n5; n5.prev = n4;
			self.head = n1;
			self.tail = n5;
			self.manyItems = 5;
			assertWellFormed(self, true);
			
			n1.next = n1;
			assertWellFormed(self, false);
			n1.next = n2;
			
			n2.next = n1;
			assertWellFormed(self, false);
			n2.next = n2;
			assertWellFormed(self, false);
			n2.next = n3;
			
			n3.next = n1;
			assertWellFormed(self, false);
			n3.next = n2;
			assertWellFormed(self, false);
			n3.next = n3;
			assertWellFormed(self, false);
			n3.next = n4;
			
			n4.next = n1;
			assertWellFormed(self, false);
			n4.next = n2;
			assertWellFormed(self, false);
			n4.next = n3;
			assertWellFormed(self, false);
			n4.next = n4;
			assertWellFormed(self, false);
			n4.next = n5;
			
			n5.next = n1;
			assertWellFormed(self, false);
			n5.next = n2;
			assertWellFormed(self, false);
			n5.next = n3;
			assertWellFormed(self, false);
			n5.next = n4;
			assertWellFormed(self, false);
			n5.next = n5;
			assertWellFormed(self, false);
			n5.next = null;
			
			assertWellFormed(self, true);
		}
		
		public void testE2() {
			Node n1 = newNode(e5);
			Node n2 = newNode(e5);
			Node n3 = newNode(e5);
			Node n4 = newNode(e5);
			Node n5 = newNode(e5);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			n3.next = n4; n4.prev = n3;
			n4.next = n5; n5.prev = n4;
			self.head = n1;
			self.tail = n5;
			self.manyItems = 5;
			assertWellFormed(self, true);

			n1.prev = n1;
			assertWellFormed(self, false);
			n1.prev = n2;
			assertWellFormed(self, false);
			n1.prev = n3;
			assertWellFormed(self, false);
			n1.prev = n4;
			assertWellFormed(self, false);
			n1.prev = n5;
			assertWellFormed(self, false);
			n1.prev = null;
			
			n2.prev = null;
			assertWellFormed(self, false);
			n2.prev = n2;
			assertWellFormed(self, false);
			n2.prev = n3;
			assertWellFormed(self, false);
			n2.prev = n4;
			assertWellFormed(self, false);
			n2.prev = n5;
			assertWellFormed(self, false);
			n2.prev = n1;
			
			n3.prev = null;
			assertWellFormed(self, false);
			n3.prev = n1;
			assertWellFormed(self, false);
			n3.prev = n3;
			assertWellFormed(self, false);
			n3.prev = n4;
			assertWellFormed(self, false);
			n3.prev = n5;
			assertWellFormed(self, false);
			n3.prev = n2;
			
			n4.prev = null;
			assertWellFormed(self, false);
			n4.prev = n1;
			assertWellFormed(self, false);
			n4.prev = n2;
			assertWellFormed(self, false);
			n4.prev = n4;
			assertWellFormed(self, false);
			n4.prev = n5;
			assertWellFormed(self, false);
			n4.prev = n3;
			
			n5.prev = null;
			assertWellFormed(self, false);
			n5.prev = n1;
			assertWellFormed(self, false);
			n5.prev = n2;
			assertWellFormed(self, false);
			n5.prev = n3;
			assertWellFormed(self, false);
			n5.prev = n5;
			assertWellFormed(self, false);
			n5.prev = n4;
			
			assertWellFormed(self, true);
		}
		
		public void testE3() {
			Node n1 = newNode(e5);
			Node n2 = newNode(e5);
			Node n3 = newNode(e5);
			Node n4 = newNode(e5);
			Node n5 = newNode(e5);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			n3.next = n4; n4.prev = n3;
			n4.next = n5; n5.prev = n4;

			Node m1 = newNode(e5);
			Node m2 = newNode(e5);
			Node m3 = newNode(e5);
			Node m4 = newNode(e5);
			Node m5 = newNode(e5);
			m1.next = m2; m2.prev = m1;
			m2.next = m3; m3.prev = m2;
			m3.next = m4; m4.prev = m3;
			m4.next = m5; m5.prev = m4;
			
			self.manyItems = 5;
			self.head = n1;
			self.tail = m5;
			assertWellFormed(self, false);

			m2.prev = n1;
			assertWellFormed(self, false);
			m3.prev = n2;
			assertWellFormed(self, false);
			m4.prev = n3;
			assertWellFormed(self, false);
			m5.prev = n4;
			assertWellFormed(self, false);
			
			n4.next = m5;
			assertWellFormed(self, true);			
		}

		public void testE4() {
			Node n1 = newNode(e5);
			Node n2 = newNode(e5);
			Node n3 = newNode(e5);
			Node n4 = newNode(e5);
			Node n5 = newNode(e5);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			n3.next = n4; n4.prev = n3;
			n4.next = n5; n5.prev = n4;
			self.head = n1;
			self.tail = n5;
			self.manyItems = 5;
			
			assertWellFormed(self, true);
			
			n3.next = n1;
			n1.prev = n3;
			assertWellFormed(self, false);
			
			Node m1 = newNode(e5);
			Node m2 = newNode(e5);
			Node m3 = newNode(e5);
			Node m4 = newNode(e5);
			Node m5 = newNode(e5);
			m1.next = m2; m2.prev = m1;
			m2.next = m3; m3.prev = m2;
			m3.next = m4; m4.prev = m3;
			m4.next = m5; m5.prev = m4;
			
			n3.next = n4;
			n1.prev = null;
			assertWellFormed(self, true);
			
			n5.next = n1;
			n1.prev = n5;
			self.tail = m5;
			assertWellFormed(self, false);
		}
		
		public void testI0() {
			selfit.canRemove = false;
			assertWellFormed(selfit, true);
		}
		
		public void testI1() {
			selfit.cursor = newNode(e1);
			assertWellFormed(selfit, false);
			selfit.colVersion = 16;
			assertWellFormed(selfit, true);
			self.head = selfit.cursor;
			assertWellFormed(selfit, false);
			selfit.cursor = null;
			assertWellFormed(selfit, false);
			selfit.colVersion = 17;
			assertWellFormed(selfit, false);
		}
		
		public void testI2() {
			selfit.canRemove = true;
			assertWellFormed(selfit, false);
			selfit.cursor = newNode(e2);
			assertWellFormed(selfit, false);
			selfit.colVersion = 0;
			assertWellFormed(selfit, true);
		}
		
		public void testI3() {
			self.head = self.tail = newNode(e3);
			self.manyItems = 1;
			assertWellFormed(self, true);
			
			selfit.canRemove = false;
			assertWellFormed(selfit, true);
			selfit.cursor = self.head;
			assertWellFormed(selfit, true);
			selfit.canRemove = true;
			assertWellFormed(selfit, true);
			selfit.cursor = null;
			assertWellFormed(selfit, false);
		}
		
		public void testI4() {
			self.head = newNode(e1);
			self.tail = newNode(e2);
			self.head.next = self.tail;
			self.tail.prev = self.head;
			self.manyItems = 2;
			assertWellFormed(self, true);
			
			selfit.canRemove = false;
			selfit.cursor = null;
			assertWellFormed(selfit, true);
			selfit.cursor = self.head;
			assertWellFormed(selfit, true);
			selfit.cursor = self.tail;
			assertWellFormed(selfit, true);
			
			selfit.canRemove = true;
			assertWellFormed(selfit, true);
			selfit.cursor = self.head;
			assertWellFormed(selfit, true);
			selfit.cursor = null;
			assertWellFormed(selfit, false);
		}
		
		public void testI5() {
			Node n1 = newNode(e1);
			Node n2 = newNode(e2);
			Node n3 = newNode(e3);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			self.head = n1;
			self.tail = n3;
			self.manyItems = 3;
			assertWellFormed(self, true);
			
			selfit.canRemove = false;
			
			selfit.cursor = newNode(e1,null,n2);
			assertWellFormed(selfit, false);
			selfit.cursor = newNode(e2,n1,n3);
			assertWellFormed(selfit, false);
			selfit.cursor = newNode(e3,n2,null);
			assertWellFormed(selfit, false);
			selfit.cursor = null;
			
			assertWellFormed(selfit, true);
		}
		
		public void testI6() {
			Node n1 = newNode(e4);
			Node n2 = newNode(e4);
			Node n3 = newNode(e4);
			Node n4 = newNode(e4);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			n3.next = n4; n4.prev = n3;
			self.manyItems = 4;
			self.head = n1;
			self.tail = n4;
			assertWellFormed(self, true);

			selfit.canRemove = true;
			
			selfit.cursor = newNode(e1,null,n2);
			assertWellFormed(selfit, false);
			selfit.cursor = newNode(e2,n1,n3);
			assertWellFormed(selfit, false);
			selfit.cursor = newNode(e3,n2,n4);
			assertWellFormed(selfit, false);
			selfit.cursor = newNode(e4,n3,null);
			assertWellFormed(selfit, false);
			selfit.cursor = null;
			assertWellFormed(selfit, false);
		}
		
		public void testI7() {
			Node n1 = newNode(e1);
			Node n2 = newNode(e2);
			Node n3 = newNode(e3);
			Node n4 = newNode(e4);
			Node n5 = newNode(e5);
			n1.next = n2; n2.prev = n1;
			n2.next = n3; n3.prev = n2;
			n3.next = n4; n4.prev = n3;
			n4.next = n5; n5.prev = n4;
			self.head = n1;
			self.tail = n5;
			self.manyItems = 5;
			assertWellFormed(self, true);

			selfit.colVersion = 14;
			selfit.canRemove = true;
			
			selfit.cursor = newNode(e1,null,n2);
			assertWellFormed(selfit, true);
			selfit.cursor = newNode(e2,n1,n3);
			assertWellFormed(selfit, true);
			selfit.cursor = newNode(e3,n2,n4);
			assertWellFormed(selfit, true);
			selfit.cursor = newNode(e4,n3,n4);
			assertWellFormed(selfit, true);
			selfit.cursor = newNode(e5,n4,null);
			assertWellFormed(selfit, true);
			
			selfit.colVersion = self.version;
			assertWellFormed(selfit, false);
			selfit.cursor = null;
			assertWellFormed(selfit, false);
			selfit.canRemove = false;
			assertWellFormed(selfit, true);
		}
	}


}
