
import java.util.HashMap;
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
	public int numOfTrees;
	
	public BinomialHeap(int size, HeapNode last, HeapNode min, int num) {
		this.size = size;
		this.last = last;
		this.min = min;
		this.numOfTrees = num;
	}
	
	public BinomialHeap() {
		this.size = 0;
		this.last = null;
		this.min = null;
		this.numOfTrees = 0;
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
		
		if (this.size % 2 == 0 && this.size > 0) {
			HeapNode next = this.last.next;
			this.last.next = node;
			node.next = next;
			if (this.min.item.key > node.item.key) {
				this.min = node;
			}
			
			this.numOfTrees ++;
			this.size ++;
			
			return node.item;
		}
		
		
		node.next = node;		
				
		BinomialHeap heap2 = new BinomialHeap(1, node, node, 1);
		
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
			this.numOfTrees = 0;
			return;
		}
		else if(this.min.rank == 0){
			HeapNode curr = this.min.next;
			while (curr.next!= this.min) {
				curr = curr.next;
			}
			curr.next = this.min.next;
			setMin();
			this.size --;
			this.numOfTrees --;
			return;
			
			
		}
		HeapNode child = this.min.child;
		child.parent = null;
		
		if (this.numOfTrees == 1) {
			this.numOfTrees = this.min.rank;
			this.min = child;
			this.size -= 1;
			this.last = child;
			this.setMin();
			return;
		}
		
		BinomialHeap bin2 = new BinomialHeap((int)Math.pow(2, this.min.rank)-1, child, child, this.min.rank);
		//BinomialHeap bin2 = new BinomialHeap((int)Math.pow(2, this.min.rank-1), child, child, this.min.rank);
		HeapNode curr = child;
		if(curr != null) {
			while(curr.next != child) {
				curr.parent = null;
				curr = curr.next;
			}
			bin2.setMin();
		}
		
		curr = this.min.next;
		while(curr.next != this.min) {
			curr = curr.next;
		}
		curr.next = this.min.next;
		this.last = curr;
		this.min = curr;
		this.numOfTrees -= 1;
		this.size -= (1 + bin2.size);
		
		this.meld(bin2);
	}

	
	/**
	 * 
	 * Sets the minimal HeapItem
	 *
	 */
	public void setMin()
	{
		HeapNode curr = this.last.next;
		HeapNode min = this.last;
		if (min.item.key > curr.item.key) {
			min = curr;
		}
		
		if (this.numOfTrees == 1 && curr != this.min) {
			this.min = this.last;
		}
		
		while(curr != this.last) {
			if(curr.item.key < min.item.key) {
				min = curr;
			}
			curr = curr.next;
		}
		this.min = min;
	} 
	
	public void setLast()
	{
		HeapNode last = this.last;
		HeapNode curr = last.next;
		
		if(curr.rank > last.rank) {
			last = curr;
		}
		
		while(curr != this.last) {
			if(curr.rank > this.last.rank) {
				last = curr;
			}
			curr = curr.next;
		}
		this.last = last;
	}
	
	/**
	 * 
	 * Sets the minimal HeapItem
	 *
	 */	
	public HeapItem findMin()
	{
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
		
		this.setMin();
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
			this.numOfTrees = heap2.numOfTrees;
			return;
		}

		if(heap2.size == 0) {
			return;
		}
		
		int amountTrees1 = this.maxRank();
		int amountTrees2 = heap2.maxRank();
		
		this.size += heap2.size;
		int totalTrees = amountTrees1 + amountTrees2;
		
		HeapNode[] bucket1 = new HeapNode[totalTrees];
		HeapNode[] bucket2 = new HeapNode[totalTrees];
		HeapNode[] bucketTotal = new HeapNode[totalTrees];
		
		HeapNode pointer1 = this.last;
		for(int i=0; i<amountTrees1; i++) {
			int tmpRank = pointer1.rank;
			bucket1[tmpRank] = pointer1;
			pointer1 = pointer1.next;
			//maybe add while pointer1 != this.last;
		}
		
		HeapNode pointer2 = heap2.last;
		for(int i=0; i<amountTrees2; i++) {
			int tmpRank = pointer2.rank;
			bucket2[tmpRank] = pointer2;
			pointer2 = pointer2.next;
		}
		
		HeapNode carry = null;
		for(int i=0; i<bucketTotal.length ;i++) {
			if (bucket1[i] != null && bucket2[i] == null && carry == null) {
				bucketTotal[i] = bucket1[i];
			}
			else if (bucket1[i] == null && bucket2[i] != null && carry == null) {
				bucketTotal[i] = bucket2[i];
			}
			else if(bucket1[i] != null && bucket2[i] != null && carry == null) {
				carry = link(bucket1[i], bucket2[i]);
			}
			else if(bucket1[i] == null && bucket2[i] != null && carry != null) {
				carry = link(bucket2[i], carry);
			}
			else if(bucket1[i] != null && bucket2[i] == null && carry != null) {
				carry = link(bucket1[i], carry);
			}
			else if(bucket1[i] != null && bucket2[i] != null && carry != null) {
				bucketTotal[i] = carry;
				carry = link(bucket1[i], bucket2[i]);
			}
			else if(bucket1[i] == null && bucket2[i] == null && carry != null) {
				bucketTotal[i] = carry;
				carry = null;
			}
		}	
		
		int counter = 0;
		for(int i=0; i<bucketTotal.length ;i++) {
			if(bucketTotal[i] != null) {
				counter ++;
			}
		}
		
		this.numOfTrees = counter;
		HeapNode[] cleanBucket = new HeapNode[counter];
		
		int indexer = 0;
		for(int i=0; i<bucketTotal.length ;i++) {
			if(bucketTotal[i] != null) {
				cleanBucket[indexer] = bucketTotal[i];
				indexer ++;
			}
		}
		
		this.last = cleanBucket[cleanBucket.length-1];
		this.last.next = cleanBucket[0];
		
		for(int i=0; i<cleanBucket.length-1; i++) {
			cleanBucket[i].next = cleanBucket[i+1];
		}
		this.setMin();
		this.setLast();
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
		return this.numOfTrees;
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
			return smaller;
		}
		bigger.next = childOfSmaller;
		
		HeapNode lastChildOfSmaller = childOfSmaller;
		while (lastChildOfSmaller.next != childOfSmaller) {
			lastChildOfSmaller = lastChildOfSmaller.next;
		}
		lastChildOfSmaller.next = bigger;
		
		return smaller;
	}
	
	
	public int maxRank() {
		return (int)(Math.log(this.size)/Math.log(2)) + 1;
	}
	
	public void print() {
		System.out.println("Binomial Heap:");
		System.out.println("Size: " + size);
		System.out.println("num of trees: " + numOfTrees);
		

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
		BinomialHeap bh1 = new BinomialHeap();
        int n=0;
        for (int i=1; i<6; i++) {
        	bh1 = new BinomialHeap();
        	n = (int) Math.pow(3, i+5);
        	n--;
        	for(int j=1; j<n+1; j++) {
            	bh1.insert(j, "i");
        	}
        	
        }
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
