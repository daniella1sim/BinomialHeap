package BinomialHeap;

import java.util.HashSet;
import java.util.Set;


/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers.
 * Based on exercise from previous semester.
 */
public class BinomialHeap
{
	public int size;
	public HeapNode last;
	public HeapNode min;
	
	public BinomialHeap(int size, HeapNode last, HeapNode min) {
		this.size = size;
		this.last = last;
		this.min = min;
	}
	
	public BinomialHeap() {
		this.size = 0;
		this.last = null;
		this.min = null;
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 *
	 */
	public HeapItem insert(int key, String info) 
	{    
		HeapNode node = new HeapNode();
		node.item = new HeapItem(node, key, info);
		node.rank = 0;
		node.next = node;		
				
		BinomialHeap heap2 = new BinomialHeap(1, node, node);
		
		this.meld(heap2);
		
		return node.item;
	}

	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin()
	{
		if(this.size == 1) {
			this.size = 0;
			this.last = null;
			this.min = null;
			return;
		}
		
		HeapNode child = this.min.child;
		HeapNode curr = this.min.next;
		
		BinomialHeap bin2 = new BinomialHeap((int)Math.pow(2, this.min.rank), child, child);
		
		while(curr.next != this.min) {
			curr = curr.next;
		}
		curr.next = this.min.next;
		this.findMin();
		
		
		if(child != null) {
			child.parent = null;
			curr = child;
			while(curr.next != child) {
				curr.parent = null;
				curr = curr.next;
			}
		}
		
		bin2.findMin();
		this.meld(bin2);
	}

	/**
	 * 
	 * Return the minimal HeapItem
	 *
	 */
	public HeapItem findMin()
	{
		HeapNode min = this.min;
		HeapNode curr = this.last;
		
		while(curr.next != this.last) {
			if(curr.item.key < min.item.key) {
				this.min = curr;
			}
			curr = curr.next;
		}
		return this.min.item;
	} 

	/**
	 * 
	 * pre: 0<diff<item.key
	 * 
	 * Decrease the key of item by diff and fix the heap. 
	 * 
	 */
	public void decreaseKey(HeapItem item, int diff) 
	{    
		item.key -= diff;
		HeapNode node = item.node;
		while(node.parent != null && node.parent.item.key > node.item.key) {
			HeapItem tmp = node.item;
			node.item = node.parent.item;
			node.parent.item = tmp;
			
			node = node.parent;
		}
		
		this.findMin();
	}

	/**
	 * 
	 * Delete the item from the heap.
	 *
	 */
	public void delete(HeapItem item) 
	{    
		decreaseKey(item, (item.key + 1));
		this.deleteMin();
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2)
	{
		
		if(this.size() == 0) {
			this.last = heap2.last;
			this.size = heap2.size;
			this.min = heap2.min;
			return;
		}
		
		/*
		 * if(heap2.size == 1) { tmp = this.last.next; this.last.next = heap2.last;
		 * heap2.last.next = tmp;
		 * 
		 * } else { if(heap2.min.item.key < this.min.item.key) { this.min = heap2.min; }
		 * 
		 * tmp = this.last.next; this.last.next = heap2.last; }
		 */
		
		int amountTrees1 = this.maxRank();
		int amountTrees2 = heap2.maxRank();
		
		int minAmount = Math.min(amountTrees1, amountTrees2); 
		
		this.size += heap2.size;
		int totalTrees = this.maxRank();
		
		HeapNode[] bucket1 = new HeapNode[amountTrees1 + 1];
		HeapNode[] bucket2 = new HeapNode[amountTrees2 + 1];
		HeapNode[] bucketTotal = new HeapNode[totalTrees + 1];
		
		HeapNode pointer1 = this.last;
		for(int i=0; i<amountTrees1; i++) {
			int tmpRank = pointer1.rank;
			bucket1[tmpRank] = pointer1;
			pointer1 = pointer1.next;
		}
		
		for(int i=0; i<bucket1.length; i++) {
			if(bucket1[i] != null) {
				System.out.println(bucket1[i].item.key);
			}
			
		}
		System.out.println("1 is done ");
		
		HeapNode pointer2 = heap2.last;
		for(int i=0; i<amountTrees2; i++) {
			int tmpRank = pointer2.rank;
			bucket2[tmpRank] = pointer2;
			pointer2 = pointer2.next;
		}
		
		for(int i=0; i<bucket2.length; i++) {
			if(bucket2[i] != null) {
				System.out.println(bucket2[i].item.key);
			}
		}
		
		for(int i=0; i<bucketTotal.length ;i++) {
			if(i > bucket1.length) {
				bucketTotal[i] = bucket2[i];
			}
			if(i > bucket2.length) {
				bucketTotal[i] = bucket1[i];
			}
			if (bucket1[i] != null && bucket2[i] == null) {
				bucketTotal[i] = bucket1[i];
			}
			else if (bucket1[i] == null && bucket2[i] != null) {
				bucketTotal[i] = bucket2[i];
			}
			else if(bucket1[i] != null && bucket2[i] != null) {
				bucketTotal[i+1] = link(bucket1[i], bucket2[i]);
			}
			
		}
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return this.size;
	}

	/**
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */
	public boolean empty()
	{
		return this.size == 0;
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees()
	{
		HeapNode curr = this.last;
		int counter = 0;
		while(curr.next != this.last) {
			counter ++;
		}
		
		return counter;
	}
	
	public HeapNode link(HeapNode node1, HeapNode node2) {
		HeapNode smaller;
		HeapNode bigger;
		
		if(node1.item.key < node2.item.key) {
			smaller = node1;
			bigger = node2;
		}
		else {
			smaller = node2;
			bigger = node1;
		}
		
		HeapNode childOfSmaller = smaller.child;
		smaller.child = bigger;
		bigger.parent = smaller;
		
		smaller.rank ++;
		
		if(childOfSmaller == null) {
			bigger.next = bigger;
		}
		else {
			bigger.next = childOfSmaller;
			childOfSmaller.next = bigger;
		}
		
		return smaller;
	}
	
	
	public int maxRank() {
		return (int)(Math.log(this.size)/Math.log(2));
	}
	
	public void print() {
		System.out.println("Binomial Heap:");
		System.out.println("Size: " + size);

		if (min != null) {
			System.out.println("Minimum Node: " + min.item.key);
		} else {
			System.out.println("No minimum node.");
		}

		System.out.println("Heap Nodes:");
		if (last != null) {
			Set<HeapNode> visited = new HashSet<>();
			printHeapNode(last, 0, visited);
		} else {
			System.out.println("No heap nodes.");
		}
	}

	private void printHeapNode(HeapNode node, int indentLevel, Set<HeapNode> visited) {
		StringBuilder indent = new StringBuilder();
		for (int i = 0; i < indentLevel; i++) {
			indent.append("    ");
		}

		System.out.println(indent + "Key: " + node.item.key);
		System.out.println(indent + "Info: " + node.item.info);
		System.out.println(indent + "Rank: " + node.rank);

		visited.add(node);

		if (node.child != null && !visited.contains(node.child)) {
			System.out.println(indent + "Child:");
			printHeapNode(node.child, indentLevel + 1, visited);
		}

		if (node.next != null && !visited.contains(node.next)) {
			System.out.println(indent + "Sibling:");
			printHeapNode(node.next, indentLevel, visited);
		}
	}


	
	public static void main(String args[]) {
		BinomialHeap bin = new BinomialHeap();
		for(int i=1; i<5; i++) {
			bin.insert(i, i+"'th");
		}
		
		
		bin.print();
	}
	
	
	
	
	
	
	
	/**
	 * Class implementing a node in a Binomial Heap.
	 *  
	 */
	public class HeapNode{
		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;
		
		public HeapNode() {
			this.item = null;
			this.child = null;
			this.next = null;
			this.parent = null;
			this.rank = -1;
		}
		
		public HeapNode(HeapItem item, HeapNode child, HeapNode next, HeapNode parent, int rank) {
			this.item = item;
			this.child = child;
			this.next = next;
			this.parent = parent;
			this.rank = rank;
		}
	}

	/**
	 * Class implementing an item in a Binomial Heap.
	 *  
	 */
	public class HeapItem{
		public HeapNode node;
		public int key;
		public String info;
		
		public HeapItem(HeapNode node, int key, String info) {
			this.node = node;
			this.key = key;
			this.info = info;
		}
	}

}





