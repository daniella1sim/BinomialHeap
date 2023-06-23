package BinomialHeap;

/**
 * 
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers.
 * Based on exercise from previous semester.
 * 
 */
public class BinomialHeap
{
	public int size;
	public HeapNode last;
	public HeapNode min;
	public int numOfTrees;
	
	
	/**
	 * 
	 * BinomialHeap constructor.
	 * @param size - number of nodes in the binomial heap.
	 * @param last - the binomial node with the highest rank in the heap.
	 * @param min - the node with the minimal key.
	 * @param numOfTrees - number of roots in the heap.
	 * 
	 */
	public BinomialHeap(int size, HeapNode last, HeapNode min, int num) {
		this.size = size;
		this.last = last;
		this.min = min;
		this.numOfTrees = num;
	}
	
	
	/**
	 * 
	 * default BinomialHeap constructor.
	 * 
	 */
	public BinomialHeap() {
		this.size = 0;
		this.last = null;
		this.min = null;
		this.numOfTrees = 0;
	}

	/**
	 * 
	 * <pre> key > 0
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 * Time Complexity - O(log(n)).
	 * @param key - the key for the inserted node.
	 * @param info - the information that is stored in the inserted node
	 * @return inserted heapnode.
	 * 
	 */
	public HeapItem insert(int key, String info) 
	{    
		/*
		 * generate a new heapnode.
		 */
		HeapNode node = new HeapNode();
		node.item = new HeapItem(node, key, info);
		node.rank = 0;
		
		/*
		 * insert a node into a heap containing an even number of nodes in O(1) complexity.
		 * the insertion is done simply by connecting the node in the linked list and adding 1 to numOfTrees.
		 */
		
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
		
		/*
		 * insert a node using the meld method in O(log(n)) complexity.
		 */
		node.next = node;			
		BinomialHeap heap2 = new BinomialHeap(1, node, node, 1);
		
		this.meld(heap2);
		return node.item;
	}

	/**
	 * 
	 * Delete the minimal item.
	 * Time Complexity - O(log(n)).
	 *
	 */
	public void deleteMin()
	{
		/*
		 * returns an empty heap if size is 1 before deletion.
		 */
		if(this.size == 1) {
			this.size = 0;
			this.last = null;
			this.min = null;
			this.numOfTrees = 0;
			return;
		}
		
		/*
		 * only deleting the node and finding the new min if rank of min is 0.
		 */
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
		
		/*
		 * if current heap only has one tree 
		 * return the heap after setting all parameters and finding a new minimal node with all children.
		 */
		if (this.numOfTrees == 1) {
			this.numOfTrees = this.min.rank;
			this.min = child;
			this.size -= 1;
			this.last = child;
			this.resetParents(child);
			this.setMin();
			return;
		}
		
		/*
		 * creating a new binomial heap with the deleted node's children and reseting their fields.
		 */
		BinomialHeap bin2 = new BinomialHeap((int)Math.pow(2, this.min.rank)-1, child, child, this.min.rank);
		
		if(child != null) {
			resetParents(child);
			bin2.setMin();
		}
		
		/*
		 * deleting the minimal node from the current heap
		 */
		HeapNode curr = this.min.next;
		while(curr.next != this.min) {
			curr = curr.next;
		}
		curr.next = this.min.next;
		this.last = curr;
		this.min = curr;
		this.numOfTrees -= 1;
		this.size -= (1 + bin2.size);
		
		/*
		 * joining the two heaps together
		 */
		this.meld(bin2);
	}

	
	/**
	 * 
	 * resets the parent parameters from all nodes to null.
	 * Time Complexity: O(log(n))
	 * @param node, an index to the first node to start resetting from.
	 * 
	 */
	private void resetParents(HeapNode node) {
		HeapNode curr = node;
		while(curr.next != node) {
			curr.parent = null;
			curr = curr.next;	
		}
	}
	
	
	/**
	 * 
	 * Sets the minimal HeapItem.
	 * Time Complexity - O(log(n)).
	 * 
	 */
	public void setMin()
	{
		/*
		 * set min and current nodes Before iteration.
		 */
		HeapNode curr = this.last.next;
		HeapNode min = this.last;
		if (min.item.key > curr.item.key) {
			min = curr;
		}
		
		/*
		 * update if there is only one tree in the heap.
		 */
		if (this.numOfTrees == 1 && curr != this.min) {
			this.min = this.last;
		}
		
		/*
		 * find new min node while iterating on all roots from the heap.
		 */
		while(curr != this.last) {
			if(curr.item.key < min.item.key) {
				min = curr;
			}
			curr = curr.next;
		}
		this.min = min;
	} 
	
	
	/**
	 * 
	 * Sets the last HeapItem.
	 * Time Complexity - O(log(n)).
	 * 
	 */
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
	 * Time Complexity - O(1).
	 * @return the minimal HeapItem in the heap.
	 * 
	 */	
	public HeapItem findMin()
	{
		return this.min.item;
	} 

	
	/**
	 * 
	 * <pre>: 0<diff<item.key.
	 * Decrease the key of item by diff and fix the heap. 
	 * Time Complexity - O(log(n)).
	 * @param item - the heapitem the decrease key is performs on.
	 * @param diff - the difference in numbers between the current key and the future key value.
	 * 
	 */
	public void decreaseKey(HeapItem item, int diff) 
	{    
		item.key -= diff;
		HeapNode node = item.node;
		/*
		 * perform heapify up operation to current heap until the heap becomes legal, by switching the nodes items.
		 */
		while(node.parent != null && node.parent.item.key > node.item.key) {
			HeapItem tmp = node.item;
			node.item = node.parent.item;
			node.parent.item = tmp;
			
			node = node.parent;
		}
		/*/
		 * resetting the min field for the current heap.
		 */
		this.setMin();
	}

	
	/**
	 * 
	 * Delete the item from the heap.
	 * Time Complexity - O(log(n)).
	 * @param item - the item to delete.
	 *
	 */
	public void delete(HeapItem item) 
	{    
		decreaseKey(item, (item.key + 1));
		this.deleteMin();
	}

	
	/**
	 * 
	 * the function melds this heap this another heap. 
	 * time Complexity - O(log(n)).
	 * @param heap - the other heap to meld with.
	 * 
	 */
	public void meld(BinomialHeap heap2)
	{
		
		/*
		 * this heap is empty.
		 */
		if(this.size() == 0) {
			this.last = heap2.last;
			this.size = heap2.size;
			this.min = heap2.min;
			this.numOfTrees = heap2.numOfTrees;
			return;
		}
		
		/*
		 * other heap is empty.
		 */
		if(heap2.size == 0) {
			return;
		}
		
		/*
		 * creating  two empty arrays and filling them with all roots it each binomialheap.
		 * since the number of roots is bounded with log(n) when n is the total number of nodes we get that this operation costs O(log(n)).
		 */
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
		}
		
		HeapNode pointer2 = heap2.last;
		for(int i=0; i<amountTrees2; i++) {
			int tmpRank = pointer2.rank;
			bucket2[tmpRank] = pointer2;
			pointer2 = pointer2.next;
		}
		
		/*
		 *merging both arrays into one array with heapnodes in each cell.
		 *Time Complexity: O(log(n))
		 *during the operation the function performs O(log(n)) iterations, in each iteration there are O(1) operations.
		 */
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
		
		/*
		 * creating a new array with all heap roots in order.
		 */
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
		
		/*
		 * generating a legal BinaryHeap from all nodes in the above array.
		 */
		this.last = cleanBucket[cleanBucket.length-1];
		this.last.next = cleanBucket[0];
		
		for(int i=0; i<cleanBucket.length-1; i++) {
			cleanBucket[i].next = cleanBucket[i+1];
		}
		
		/*
		 * resetting the heap's fields.
		 */
		this.setMin();
		this.setLast();
	}

	
	/**
	 * 
	 * Time Complexity: O(1).
	 * @return the number of elements in the heap.
	 * 
	 */
	public int size()
	{
		return this.size;
	}

	
	/**
	 * 
	 * Time Complexity: O(1).
	 * @return true if and only if the heap is empty.
	 * 
	 */
	public boolean empty()
	{
		return this.size == 0;
	}

	
	/**
	 * 
	 * Time Complexity: O(1).
	 * @return the number of trees in the heap.
	 * 
	 */
	public int numTrees()
	{	
		return this.numOfTrees;
	}
	
	
	/**
	 * 
	 * the function links two heapnodes with the same rank into a single heapnode with rank+1.
	 * Time complexity: O(1)
	 * @param node1 - first node to link.
	 * @param node2 - second node to link.
	 * @return the linked Heapnode.
	 * 
	 */
	public HeapNode link(HeapNode node1, HeapNode node2) {
		HeapNode smaller;
		HeapNode bigger;
		
		/*
		 * Assigning the nodes depending on their their size.
		 */
		if(node1.item.key < node2.item.key) {
			smaller = node1;
			bigger = node2;
		}
		else {
			smaller = node2;
			bigger = node1;
		}
		
		/*
		 * updating parameters of both nodes and joining them.
		 */
		HeapNode childOfSmaller = smaller.child;
		smaller.child = bigger;
		bigger.parent = smaller;
		smaller.rank ++;
		
		/*
		 * adding the bigger node as the biggest child of smaller.
		 */
		if(childOfSmaller == null) {
			bigger.next = bigger;
			return smaller;
		}
		bigger.next = childOfSmaller.next;
		childOfSmaller.next = bigger;
		
		return smaller;
	}
	
	
	/**
	 * 
	 * calculate max possible rank of a heap.
	 * @return an int of the max rank of a heap. calculated using the formula bellow.
	 * 
	 */
	public int maxRank() {
		return (int)(Math.log(this.size)/Math.log(2)) + 1;
	}
	
	
	/**
	 * 
	 * Class implementing a node in a Binomial Heap.
	 *  
	 */
	public class HeapNode{
		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;
		
		
		/**
		 * 
		 * default HeapNode constructor.
		 * 
		 */
		public HeapNode() {
			this.item = null;
			this.child = null;
			this.next = null;
			this.parent = null;
			this.rank = -1;
		}
		
		
		/**
		 * 
		 * @param item - matching heapitem.
		 * @param child - the child with the highest rank of current heapnode.
		 * @param next - the next heapnode at the same level in the linked list.
		 * @param parent - parent of node.
		 * @param rank - number of node's children.
		 * 
		 */
		public HeapNode(HeapItem item, HeapNode child, HeapNode next, HeapNode parent, int rank) {
			this.item = item;
			this.child = child;
			this.next = next;
			this.parent = parent;
			this.rank = rank;
		}
	}

	
	/**
	 * 
	 * Class implementing an item in a Binomial Heap.
	 *  
	 */
	public class HeapItem{
		public HeapNode node;
		public int key;
		public String info;

		
		/**
		 * 
		 * HeapItem Class Constructor
		 * @param node - id to matching heapnode.
		 * @param key - key of heapnode.
		 * @param info - contains the information from heapnode.
		 * 
		 */	
		public HeapItem(HeapNode node, int key, String info) {
			this.node = node;
			this.key = key;
			this.info = info;
		}
	}

}
