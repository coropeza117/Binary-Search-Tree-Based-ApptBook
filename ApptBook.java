// This is an assignment for students to complete after reading Chapter 3 of
// "Data Structures and Other Objects Using Java" by Michael Main.

package edu.uwm.cs351;

import java.util.function.Consumer;

import edu.uwm.cs.junit.LockedTestCase;

/**
 * @author * CHRISTIAN OROPEZA CS-351 ...RECIEVED HELP FROM BOOK, LIBRARY TUTORS, ONLINE CS TUTOR, AND ADVICE FROM FRIENDS ON HOW TO APPROACH FIXING PERSISTENT BUGS.
 * COLLABORATORS: JOSHUA KNIGHT, JULLIAN MURENO, BIJAY PANTA, JIAHUI YANG , MIGUEL GARCIA, MARAWAN SALAMA (WHILE IN TUTORING LIBRARY SECTION) BUT NO CODE WAS SHARED.
 * Online Sources: https://www.geeksforgeeks.org/count-number-of-nodes-in-a-complete-binary-tree/
 * 				   https://stackoverflow.com/questions/36629442/how-to-get-the-first-element-when-going-through-a-binary-search-tree-with-inorde
 * NEXTINTREE->	   https://www.techiedelight.com/find-inorder-successor-given-key-bst/
 * 				   https://interview.hackingnote.com/en/problems/clone-binary-tree
 * 				   https://www.techiedelight.com/clone-binary-tree/
 */

/******************************************************************************
 * This class is a homework assignment;
 * An ApptBook ("book" for short) is a sequence of Appointment objects in sorted order.
 * The book can have a special "current element," which is specified and 
 * accessed through four methods that are available in the this class 
 * (start, getCurrent, advance and isCurrent).
 ******************************************************************************/
public class ApptBook implements Cloneable 
{
	
	 
	// TODO: Declare the private static Node class.
	// It should have a constructor but no methods.
	// The constructor should take an Appointment.
	// The fields of Node should have "default" access (neither public, nor private)
	// and should not start with underscores.

	// TODO: Declare the private fields of ApptBook needed for sequences
	// using a binary search tree.

	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };
	private boolean report(String error) 
	{
		reporter.accept(error);
		return false;
	}
	
	/***************************************************************************************************
	 * 
	 * Node Class below
	 * @param 
	 ***************************************************************************************************
	 */
	private static class Node
	{
		Appointment data;	
		Node left;			
		Node right;
	
		public Node (Appointment d) 
		{		
			this.data = d;
			left = null;
			right = null;
		}
	}
	//***************************************************************************************************
	
	public int manyItems;
	public Node root;
	public Node cursor;

	/**
	 * Return true if the given subtree has height no more than a given bound.
	 * In particular if the "tree" has a cycle, then false will be returned
	 * since it has unbounded height.
	 * @param r root of subtree to check, may be null
	 * @param max maximum permitted height (null has height 0)
	 * @return whether the subtree has at most this height
	 * first we check r for null value then make sure r or root has a valid max height
	 * a valid max height for root is 0-infinity
	 * if max at any point has a value less than 0 we return false
	 * after our initial checks we do a recursive return call where we compare the height
	 * of both (left & right) sub-trees of the tree below the root. We subtract 1 from max 
	 * since the level below the root has a max height that is 1 less than that of the root
	 */
	private boolean checkHeight(Node r, int max) 
	{
		if(r == null && max >= 0)	return true;
		
		if(max < 0)	return false;

		return checkHeight(r.left, max-1) && checkHeight(r.right, max-1);
	}
	
	/**
	 * Return the number of nodes in a subtree that has no cycles.
	 * @param r root of the subtree to count nodes in, may be null
	 * @return number of nodes in subtree
	 * base call - if (r == null)	return 0;
	 * recursive call - return 1 + countNodes(r.left) + countNodes(r.right);
	 * if r has a null value we return 0 and exit the recursive loop
	 * otherwise we loop through the # of nodes in the left and right BST sub-trees
	 * then add then together while also adding 1 as the result
	 */
	private int countNodes(Node r) 
	{		
		if (r == null)	return 0;
		
		return 1 + countNodes(r.left) + countNodes(r.right);
	}
	
	/**
	 * Return whether all the nodes in the subtree are in the given range,
	 * and also in their respective subranges.
	 * @param r root of subtree to check, may be null
	 * @param lo inclusive lower bound, may be null (no lower bound)
	 * @param hi exclusive upper bound, may be null (no upper bound)
	 * @return true or a report if false and lastly a recursive call
	 * null for r is valid so return true
	 * However if the Appointment data is null return a report since data can't be null
	 * If the lower bound of Appointment is greater that the root data then it should 
	 * not be the lower bound and we should return a report
	 * If the higher bound of Appointment is lesser or equal to the root data than that
	 * is not the highest bound and we should return a report
	 * return the recursive call for the left and right sub-true with the given parameters
	 * including the lower bound and root appointment data for the left tree and the upper bound
	 * with the root data for the right tree
	 */
	private boolean allInRange(Node r, Appointment lo, Appointment hi) 
	{
		if(r == null)	return true;
 
		if(r.data == null)	return report("r data null");

		if(lo != null && lo.compareTo(r.data) > 0)	return report("lo appointment data > r.data");
			
		if(hi != null && hi.compareTo(r.data) <= 0)	return report("hi appointment data < r.data");
		
		return allInRange(r.left, lo, r.data) && allInRange(r.right, r.data, hi);
	}
	
	/**
	 * Return whether the cursor was found in the tree.
	 * If the cursor is null, it should always be found since 
	 * a binary search tree has many null pointers in it.
	 * This method doesn't examine any of the data elements;
	 * it works fine even on trees that are badly structured, as long as
	 * they are height-bounded.
	 * @param r subtree to check, may be null, but must have bounded height
	 * @return true if the cursor was found in the subtree either left or right sub-tree
	 * if r is not but is not bounded to height of a cursor return false
	 */
	private boolean foundCursor(Node r) 
	{
		if(cursor == null || cursor == r)	return true;
		
		if(r == null)	return false;
		
		return foundCursor(r.left) || foundCursor(r.right); 
	}

	
	private boolean wellFormed() 
	{
		// Check the invariant.
		// Invariant:
		// 1. The tree must have height bounded by the number of items
		if(!checkHeight(root, manyItems))	return report("Check height not bounded by number of items !");
		
		// 2. The number of nodes must match manyItems
		if(countNodes(root) != manyItems)	return report("•countNodes != manyItems•");
		
		// 3. Every node's data must not be null and be in range.
		if(!allInRange(root, null, null))	return false;
		
		// 4. The cursor must be null or in the tree.
		if(!foundCursor(root))	return report("cursor is not null && not in the tree");
		// Implementation:
		// Do multiple checks: each time returning false if a problem is found.
		// (Use "report" to give a descriptive report while returning false.)
		// TODO: Use helper methods to do all the work.
		
		// If no problems found, then return true:
		return true;
	}

	// This is only for testing the invariant.  Do not change!
	private ApptBook(boolean testInvariant) { }

	/**
	 * Initialize an empty book. 
	 **/   
	public ApptBook( )
	{
		root = null;
		cursor = null;
		manyItems = 0;

		assert wellFormed() : "invariant failed at end of constructor";
	}

	/**
	 * Determine the number of elements in this book.
	 * @return
	 *   the number of elements in this book
	 **/ 
	public int size( )
	{
		assert wellFormed() : "invariant failed at start of size";

		return manyItems;
	}

	/**
	 * Return the first node in a non-empty subtree.
	 * It doesn't examine the data in the nodes; 
	 * it just uses the structure.
	 * @param r subtree, must not be null
	 * @return first node in the subtree
	 * if r is null simply return that node
	 * if the left sub tree is not null do a
	 * recursive call to return the first/most left node in the tree
	 * otherwise return the root node as usual 
	 */
	private Node firstInTree(Node r) 
	{
		if(r == null)	return r;

		if(r.left != null)	return firstInTree(r.left);
		
		else	return r;
	}
	
	/**
	 * Set the current element at the front of this book.
	 * @postcondition
	 *   The front element of this book is now the current element (but 
	 *   if this book has no elements at all, then there is no current 
	 *   element).
	 *   cursor is set to the first root node in the tree
	 **/ 
	public void start( )
	{
		assert wellFormed() : "invariant failed at start of start";
		
		cursor = firstInTree(root);
		
		assert wellFormed() : "invariant failed at end of start";
	}

	/**
	 * Accessor method to determine whether this book has a specified 
	 * current element that can be retrieved with the 
	 * getCurrent method. 
	 * @return
	 *   true (there is a current element) or false (there is no current element at the moment)
	 **/
	public boolean isCurrent( )
	{
		assert wellFormed() : "invariant failed at start of isCurrent";

		return cursor != null;
	}

	/**
	 * Accessor method to get the current element of this book. 
	 * @precondition
	 *   isCurrent() returns true.
	 * @return
	 *   the current element of this book
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   getCurrent may not be called.
	 **/
	public Appointment getCurrent( )
	{
		assert wellFormed() : "invariant failed at start of getCurrent";

		if(!isCurrent())	throw new IllegalStateException("no current");
		
		return cursor.data;
	}

	/**
	 * Find the node that has the appt (if acceptEquivalent) or the first thing
	 * after it.  Return that node.  Return the alternate if everything in the subtree
	 * comes before the given appt.
	 * @param r subtree to look into, may be null
	 * @param appt appointment to look for, must not be null
	 * @param acceptEquivalent whether we accept something equivalent.  Otherwise, only
	 * appointments after the appt are accepted.
	 * @param alt what to return if no node in subtree is acceptable.
	 * @return node that has the first element equal (if acceptEquivalent) or after
	 * the appt.
	 * if r is null return the alt node when no suitable node is found
	 * if r node data is equal to appt data while acceptEquivalent true return node r
	 * but when r.data is greater that appt data set alt node to and do recursive call to 
	 * next in tree searching the left sub-tree first using method parameters in call
	 * otherwise, recursively call next in tree search the right sub-tree 
	 */
	private Node nextInTree(Node r, Appointment appt, boolean acceptEquivalent, Node alt) 
	{
		if(r == null)	return alt;
		
		if(r.data.compareTo(appt) == 0 && acceptEquivalent == true)	return r;
			
		if(r.data.compareTo(appt) > 0)
		{
			alt = r;
			return nextInTree(r.left, appt, acceptEquivalent, alt);
		}
		
		return nextInTree(r.right, appt, acceptEquivalent, alt); 
	}
	
	/**
	 * Move forward, so that the current element will be the next element in
	 * this book.
	 * @precondition
	 *   isCurrent() returns true. 
	 * @postcondition
	 *   If the current element was already the end element of this book 
	 *   (with nothing after it), then there is no longer any current element. 
	 *   Otherwise, the new element is the element immediately after the 
	 *   original current element.
	 * @exception IllegalStateException
	 *   Indicates that there is no current element(cursor), so 
	 *   advance may not be called.
	 *   if node right of cursor is not null update cursor to first node in tree right of cursor using recursive call
	 *   otherwise, update cursor to node found by calling next recursively and searching using root, cursor appt data, false, and null alt
	 **/
	public void advance( )
	{
		assert wellFormed() : "invariant failed at start of advance";

		if(cursor == null)	throw new IllegalStateException("advancing past end");
		
		if(cursor.right != null)	cursor = firstInTree(cursor.right);
		
		else	cursor = nextInTree(root, cursor.data, false, null);
	
		assert wellFormed() : "invariant failed at end of advance";
	}

	/**
	 * Remove the current element from this book.
	 * NB: Not supported in Homework #8
	 **/
	public void removeCurrent( )
	{
		assert wellFormed() : "invariant failed at start of removeCurrent";
		throw new UnsupportedOperationException("remove is not implemented");
	}
	
	/**
	 * Set the current element to the first element that is equal
	 * or greater than the guide.  This operation will be efficient
	 * if the tree is balanced.
	 * @param guide element to compare against, must not be null.
	 * set current element (cursor) to nextInTree using the next recursive call and putting the root and guide parameters
	 */
	public void setCurrent(Appointment guide) 
	{
		assert wellFormed() : "invariant failed at start of setCurrent";

		if (guide == null) throw new NullPointerException("guide cannot be null");
		
		cursor = nextInTree(root, guide, true, null);
		
		assert wellFormed() : "invariant failed at end of setCurrent";
	}

	// OPTIONAL: You may define a helper method for insert.  The solution does
	private Node insertHelper(Node r, Appointment element)
	{
		if(r == null)	return new Node(element);
		
		if(element.compareTo(r.data) < 0)	r.left = insertHelper(r.left, element);
		
		else	r.right = insertHelper(r.right, element);
		
		return r;	
	}
	
	/**
	 * Add a new element to this book, in order.  If an equal appointment is already
	 * in the book, it is inserted after the last of these. 
	 * The current element (if any) is not affected.
	 * @param element
	 *   the new element that is being added, must not be null
	 * @postcondition
	 *   A new copy of the element has been added to this book. The current
	 *   element (whether or not is exists) is not changed.
	 * @exception IllegalArgumentException
	 *   indicates the parameter is null
	 *   // TODO: Implemented by student.
		/**
		 * public void add(Coin c)
		 * {
		 *  if(c == null)	throw new IAE or NPE();
		 *  
		 *  root = addHelper(root, c);
		 *  
		 * 	private Node addHelper(Node r, Coin c)
		 *  {
		 * 	if(r == null)	return new Node(c);
		 * 
		 *  if(c.getType().getValue < r.data.getType().getValue())	r.left = addHelper(r.left, c);
		 *  
		 *  else	r.right = addHelper(r.right, c);
		 *  
		 *  return r;
		 *  
		 *  //	Mistake: 
		 *  		1. ignore result of delegation 
		 *  		   -null fails to work
		 *  		   -micro manage / special case null
		 *  
		 *          2. -return it
		 *          	-DONT check wellFormed in private helper method
		 *  }
		 * }
		 * 
		 */
	public void insert(Appointment element)
	{
		assert wellFormed() : "invariant failed at start of insert";
		
		if(element == null)	throw new IllegalArgumentException("insert element is null");
		
		root = insertHelper(root, element);
		
		++manyItems;
		
		assert wellFormed() : "invariant failed at end of insert";
	}

	// TODO: recursive helper method for insertAll.  
	// - Must be recursive.
	// - Must add in "pre-order"
	// - Used MOVESTACK Logic
	private void insertAllHelper(Node r)
	{
		if(r == null)	 return;

		insert(r.data);
		
		insertAllHelper(r.left);
		insertAllHelper(r.right);
	}
	
	/**
	 * Place all the appointments of another book (which may be the
	 * same book as this!) into this book in order as in {@link #insert}.
	 * The elements should added one by one.
	 * @param addend
	 *   a book whose contents will be placed into this book
	 * @precondition
	 *   The parameter, addend, is not null. 
	 * @postcondition
	 *   The elements from addend have been placed into
	 *   this book. The current element (if any) is
	 *   unchanged.
	 *   Watch out for the this==addend case!
	 *   Cloning the addend is an easy way to avoid problems.
	 **/
	public void insertAll(ApptBook addend)
	{
		assert wellFormed() : "invariant failed at start of insertAll";

		if(addend.manyItems == 0)	return;
		
		if(addend == this)	addend = addend.clone();
		
		insertAllHelper(addend.root);
		
		assert wellFormed() : "invariant failed at end of insertAll";
		assert addend.wellFormed() : "invariant of addend broken in insertAll";
	}

	// TODO: private recursive helper method for clone.
	// - Must be recursive
	// - Take the answer as a parameter so you can set the cloned cursor
	// - HW4 Clone conditions for cursor and root update - other source cited above
	private Node cloneHelper(Node r, ApptBook answer)
	{
		if(r == null)	return r;
		
		Node newNode = new Node(r.data);
		
		if(r == root) answer.root = newNode;
		if(r == cursor)	answer.cursor = newNode;

		newNode.left = cloneHelper(r.left, answer);
		newNode.right = cloneHelper(r.right, answer);
		
		return newNode;
	}
	
	/**
	 * Generate a copy of this book.
	 * @return
	 *   The return value is a copy of this book. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 **/ 
	public ApptBook clone( ) 
	{ 
		assert wellFormed() : "invariant failed at start of clone";
		ApptBook answer;
	
		try
		{
			answer = (ApptBook) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  // This exception should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			throw new RuntimeException
			("This class does not implement Cloneable");
		}
	
		cloneHelper(root, answer);
	
		assert wellFormed() : "invariant failed at end of clone";
		assert answer.wellFormed() : "invariant on answer failed at end of clone";
		return answer;
	}

	// don't change this nested class:
	public static class TestInvariantChecker extends LockedTestCase 
	{
		protected ApptBook self;

		protected Consumer<String> getReporter() 
		{
			return reporter;
		}
		
		protected void setReporter(Consumer<String> c) 
		{
			reporter = c;
		}
		
		private static Appointment a = new Appointment(new Period(new Time(), Duration.HOUR), "default");
		
		protected class Node extends ApptBook.Node 
		{
			public Node(Appointment d, Node n1, Node n2) 
			{
				super(a);
				data = d;
				left = n1;
				right = n2;
			}
			public void setLeft(Node l) 
			{
				left = l;
			}
			public void setRight(Node r) 
			{
				right = r;
			}
		}
		
		protected Node newNode(Appointment a, Node l, Node r) 
		{
			return new Node(a, l, r);
		}
		
		protected void setRoot(Node n) 
		{
			self.root = n;
		}
		
		protected void setManyItems(int mi) 
		{
			self.manyItems = mi;
		}
		
		protected void setCursor(Node n) 
		{
			self.cursor = n;
		}
		
		protected void setUp() 
		{
			self = new ApptBook(false);
			self.root = self.cursor = null;
			self.manyItems = 0;
		}
		
		
		/// relay methods for helper methods:
		
		protected boolean checkHeight(Node r, int max) 
		{
			return self.checkHeight(r, max);
		}
		
		protected int countNodes(Node r) 
		{
			return self.countNodes(r);
		}
		
		protected boolean allInRange(Node r, Appointment lo, Appointment hi) 
		{
			return self.allInRange(r, lo, hi);
		}
		
		protected boolean foundCursor(Node r) 
		{
			return self.foundCursor(r);
		}
		
		protected boolean wellFormed() 
		{
			return self.wellFormed();
		}
		
		protected Node firstInTree(Node r) 
		{
			return (Node)self.firstInTree(r);
		}
		
		protected Node nextInTree(Node r, Appointment a, boolean acceptEquiv, Node alt) 
		{
			return (Node)self.nextInTree(r, a, acceptEquiv, alt);
		}
		
		
		/// Prevent this test suite from running by itself
		
		public void test() 
		{
			assertFalse("DOn't attempt to run this test", true);
		}
	}
}

