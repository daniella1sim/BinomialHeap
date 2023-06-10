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
	
	public BinomialHeap(int size, HeapNode last, HeapNode min, int numOfTrees) {
		this.size = size;
		this.last = last;
		this.min = min;
		this.numOfTrees = numOfTrees;
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
		node.item = new HeapItem(node, key, info)
		node.rank = 0;
				
				
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
		
		HeapNode child = this.min.child;
		HeapNode curr = this.min.next;
		
		BinomialHeap bin2 = new BinomialHeap(Math.pow(2, this.min.rank), child, child, this.min.rank);
		
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
				this.min = curr
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
		if(heap2.size == 1) {
			HeapNode tmp = this.last.next;
			this.last.next = heap2.last;
			heap2.last.next = tmp;
			
		}
		else {
			if(heap2.min < this.min) {
				this.min = heap2.min;
			}
			this.size += heap2.size;
			
			HeapNode curr = heap2.last.next;
			while(curr.next != heap2.last) {
				curr = curr.next;
			}
			HeapNode tmp = this.last.next;
			this.last.next = heap2.last;
		}
		HeapNode curr = heap2.last.next;
		
		HeapNode[] buckets = new HeapNode[this.numOfTrees()];
		buckets[curr.rank] = curr;
		curr = curr.next;
		while(curr.next != tmp) {
			int tmpRank = curr.rank;
			if (buckets[tmpRank] == null) {
				buckets[tmpRank] = curr;
			}
			else {
				curr = link(curr, buckets[tmpRank]);
			}
		}
		
		//MAINTAIN LAST AND PARENTS/CHILDREN
		
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
		if(node1.item.key > node2.item.key) {
			HeapItem tmp = node1.item;
			node1.item = node2.item;
			node2.item = tmp;
		}
		node2.next = node1.child;
		node1.child = node2;
		this.numOfTrees --;
		node1.rank ++;
		return node1;
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
