import java.util.*;

/**
 * A B-Tree Collections class
 */
public class BTree <E extends Comparable<E>> implements Iterable<E>
{
	Comparator<? super E> comparator;
	private BTNode root;
	int order;
	int minKeyCount;

   /**
	* Creates a BTree that orders values according to their natural ordering.
	*
	* @param	order	the order of the B-Tree
	*/
	public BTree(int order) 
	{
		this.comparator = new NaturalOrderComparator<E>();
		this.root = null;
		this.order = order;
		this.minKeyCount = (int)Math.ceil(order / 2.0) - 1;
	}

   /**
	* Takes a comparator that will be used to order the values within the B-Tree.
	*
	* @param	order	the order of the B-Tree
	* @param	comparator	a comparator used to order the values in the B-Tree
	*/
	public BTree(int order, Comparator<? super E> comparator) 
	{
		this.comparator = comparator;
		this.root = null;
		this.order = order;
		this.minKeyCount = (int)Math.ceil(order / 2.0) - 1;
	}

   /**
	* Builds a B-Tree based on the structure provided
	*
	* @param	order	the order of the B-Tree
	* @param	structure	the desired structure of the tree represented as a multidimensionsal arraylist
	*/
	public BTree(int order, ArrayList<ArrayList<ArrayList<E>>> structure)
	{
		this.comparator = new NaturalOrderComparator<E>();
		this.root = buildTreeFromListStructure(structure);
		this.order = order;
		this.minKeyCount = (int)Math.ceil(order / 2.0) - 1;
	}

   /**
	* Takes a comparator that will be used to order the values within the BTree.
	*
	* @param	order	the order of the B-Tree
	* @param	structure	the desired structure of the tree represented as a multidimensionsal arraylist
	* @param	comparator	a comparator used to order the values in the BTree
	*/
	public BTree(int order, ArrayList<ArrayList<ArrayList<E>>> structure, Comparator<? super E> comparator) 
	{
		this.comparator = comparator;
		this.root = buildTreeFromListStructure(structure);
		this.order = order;
		this.minKeyCount = (int)Math.ceil(order / 2.0) - 1;
	}

	private BTNode buildTreeFromListStructure(ArrayList<ArrayList<ArrayList<E>>> structure)
	{
		BTNode currentNode = null;
		ListIterator<ArrayList<ArrayList<E>>> treeIterator = structure.listIterator(structure.size());
		ArrayList<BTNode> currentLevel = null;
		ArrayList<BTNode> childLevel = null;
		while (treeIterator.hasPrevious())
		{
			currentLevel = new ArrayList<BTNode>();
			ArrayList<ArrayList<E>> currentLevelList = treeIterator.previous();
			Iterator<ArrayList<E>> levelIterator = currentLevelList.iterator();
			int childIndex = 0;
			while (levelIterator.hasNext())
			{
				ArrayList<E> keys = levelIterator.next();
				currentNode = new BTNode(keys);
				if (childLevel != null)
				{
					for (int i = childIndex; i <= currentNode.keys.size() + childIndex; i++)
					{
						currentNode.children.add(childLevel.get(i));
					}
					childIndex += currentNode.children.size();
				}
				currentLevel.add(currentNode);
			}
			childLevel = currentLevel; 
		}
		return currentNode;
	}
	
   /**
	* Attempts to add a value to the B-Tree. Does not add duplicates.
	*
	* @param	value	the value to be added to the BST
	*/
	public void add(E value)
	{
		topDownInsert(root, null, value);
	}

	private void topDownInsert(BTNode localRoot, BTNode parent, E value)
	{
		if (localRoot == null)
		{
			root = new BTNode();
			root.keys.add(value);
			return;
		}

		//if localRoot is full then split
		if (localRoot.keys.size() == order - 1)
		{
			localRoot = splitNode(localRoot, parent);
		}	
			
		int targetKeyIndex = getKeyIndex(localRoot, value);	
		
		//checks if value is a duplicate
		if (localRoot.keys.size() > targetKeyIndex && comparator.compare(localRoot.keys.get(targetKeyIndex), value) == 0) return;

		//if local root is a leaf add to local root
		if (localRoot.children.size() == 0)
		{
			localRoot.keys.add(targetKeyIndex, value);
		}
		
		//if local root is not a leaf then move to child
		else
		{
			topDownInsert(localRoot.children.get(targetKeyIndex), localRoot, value);
		}
	}

	/*promote middle value*/
	private BTNode splitNode(BTNode node, BTNode parent)
	{
		//find middle value
		int midIndex = (int)Math.round(node.keys.size() / 2.0) - 1;
		E valueToPromote = node.keys.get(midIndex);
		
		//if node is root make new root
		if (parent == null)
		{
			root = parent = new BTNode();
			parent.children.add(node);
		}

		int targetKeyIndex = getKeyIndex(parent, valueToPromote);
		parent.keys.add(targetKeyIndex, valueToPromote);
		
		BTNode left = new BTNode();
		BTNode right = new BTNode();
		left.keys = new ArrayList<E>(node.keys.subList(0, midIndex));
		right.keys = new ArrayList<E>(node.keys.subList(midIndex + 1, node.keys.size()));

		if (node.children.size() > 0)
		{
			left.children = new ArrayList<BTNode>(node.children.subList(0, midIndex + 1));
			right.children = new ArrayList<BTNode>(node.children.subList(midIndex + 1, node.children.size()));
		}

		parent.children.set(targetKeyIndex, left);
		parent.children.add(targetKeyIndex + 1, right);

		return parent;
	}

	private int getKeyIndex(BTNode node, E value)
	{
		Iterator<E> keyIterator = node.keys.iterator();
		int index = 0;

		while (keyIterator.hasNext() && comparator.compare(value, keyIterator.next()) > 0)
		{
			index++;
		}

		return index;
	}

   /**
	* Attempts to remove a value from the B-Tree.
	* 
	* @param	value	the value to be removed
	* @return			the result of the removal
	*/
	public boolean remove(E value)
	{
		if (delete(root, null, value))
		{
			return true;
		}
		return false;
	}

	private boolean delete(BTNode localRoot, BTNode parent, E value)
	{
		int targetKeyIndex = getKeyIndex(localRoot, value);

		//localRoot does not contain key
		if (targetKeyIndex == localRoot.keys.size() || comparator.compare(localRoot.keys.get(targetKeyIndex), value) != 0)
		{
			if (localRoot.children.size() == 0) return false;
			if (!delete(localRoot.children.get(targetKeyIndex), localRoot, value)) return false;
		}
		//localRoot contains key, but is an internal node
		else if (localRoot.children.size() > 0)
		{
			//Replace target key with predecessor and recursively remove predecessor from its original position
			E predecessor = getPredecessor(localRoot, targetKeyIndex);
			localRoot.keys.set(targetKeyIndex, predecessor);
			delete(localRoot.children.get(targetKeyIndex), localRoot, predecessor);
		}
		//localRoot contains key, and is a leaf
		else
		{
			localRoot.keys.remove(targetKeyIndex);
		}
		
		if (localRoot.keys.size() < minKeyCount && parent != null)
		{
			redistributeKeys(localRoot, parent, getKeyIndex(parent, value));
		}
		return true;
	}

	private E getPredecessor(BTNode node, int targetKeyIndex)
	{
		if (node.children.size() == 0) return null;
		BTNode currentNode = node.children.get(targetKeyIndex);
		while (currentNode.children.size() > 0)
		{
			currentNode = currentNode.children.get(currentNode.children.size() - 1);
		}
		return currentNode.keys.get(currentNode.keys.size() - 1 );
	}

	private void redistributeKeys(BTNode localRoot, BTNode parent, int childIndex)
	{
		if (parent == null) return;

		BTNode leftSibling = childIndex > 0 ? parent.children.get(childIndex - 1) : null;
		BTNode rightSibling = childIndex < parent.keys.size() ? parent.children.get(childIndex + 1) : null;
		
		//Steal from left
		if (leftSibling != null && leftSibling.keys.size() > minKeyCount)
		{
			//Pull down key from parent to localRoot
			localRoot.keys.add(0, parent.keys.get(childIndex - 1));

			//Pull up key from left sibling to parent
			E predecessor = leftSibling.keys.get(leftSibling.keys.size() - 1);
			parent.keys.set(childIndex - 1, predecessor);
			leftSibling.keys.remove(leftSibling.keys.size() - 1);

			//Add subtree of stolen key to original node
			if (localRoot.children.size() > 0)
			{
				localRoot.children.add(0, leftSibling.children.get(leftSibling.children.size() - 1));
				leftSibling.children.remove(leftSibling.children.size() - 1);
			}
		}
		//Steal from right
		else if (rightSibling != null && rightSibling.keys.size() > minKeyCount)
		{
			//Pull down key from parent to localRoot
			localRoot.keys.add(parent.keys.get(childIndex));

			//Pull up key from right sibling to parent
			E successor = rightSibling.keys.get(0);
			parent.keys.set(childIndex, successor);
			rightSibling.keys.remove(0);

			//Add subtree of stolen key to original node
			if (localRoot.children.size() > 0)
			{
				localRoot.children.add(rightSibling.children.get(0));
				rightSibling.children.remove(0);
			}
		}
		//Merge nodes
		//Merges with right node when possible
		else
		{
			//Steal the key to the right of the local root unless local root is the last key
			int indexOfKeyToDemote = childIndex < parent.keys.size() ? childIndex : childIndex - 1;
			
			BTNode leftNode = parent.children.get(indexOfKeyToDemote);
			BTNode rightNode = parent.children.get(indexOfKeyToDemote + 1);
		
			//Copy keyToDemote to leftNode
			leftNode.keys.add(parent.keys.get(indexOfKeyToDemote));
			parent.keys.remove(indexOfKeyToDemote);
		
			//Combine left and right nodes
			leftNode.keys.addAll(rightNode.keys);
			leftNode.children.addAll(rightNode.children);
			parent.children.remove(indexOfKeyToDemote + 1);		

			//BTNode mergedNode = parent.children.get(indexOfKeyToDemote);

			if (parent == root && parent.keys.size() == 0)
			{
				this.root = leftNode;
			}	
		}
	}

	private int getLastKeyIndex(BTNode node)
	{
		return node.keys.size() > 0 ? node.keys.size() - 1 : 0;
	}

   /**
	* Attempts to remove a value from the BST.
	* 
	* @param	value	the value to be removed
	* @return			the result of the removal
	*/
	public boolean contains(E value)
	{
		BTNode localRoot = root;
		int targetKeyIndex = getKeyIndex(root, value);
		while (targetKeyIndex == localRoot.keys.size() || comparator.compare(localRoot.keys.get(targetKeyIndex), value) != 0)
		{
			if (localRoot.children.size() == 0) return false;
			localRoot = localRoot.children.get(targetKeyIndex);
			targetKeyIndex = getKeyIndex(localRoot, value);
		}
		return true;
	}

   /**
	* Tests whether the B-Tree is empty.
	*
	* @return	whether the B-Tree is empty
	*/
	public boolean isEmpty()
	{
		return !(root != null && root.keys.size() > 0);
	}

   /**
	* Creates a BTreeIterator that traverses the tree in order
	*
	* @return	a BTreeIterator for the current B-Tree
	*/
	public Iterator<E> iterator()
	{
		return new BTreeIterator();
	}

 	/**
	* Creates a BTreeIterator that traverses the tree in level order
	*
	* @return	a BTreeIterator for the current B-Tree
	*/
	public Iterator<ArrayList<ArrayList<E>>> levelIterator()
	{
		return new BTreeLevelIterator();
	}

	private class BTNode
	{
		public ArrayList<E> keys;
		public ArrayList<BTNode> children;

		public BTNode()
		{
			keys = new ArrayList<E>();
			children = new ArrayList<BTNode>();
		} 
		
		public BTNode(ArrayList<E> keys)
		{
			this.keys = new ArrayList<E>(keys);
			children = new ArrayList<BTNode>(); 
		}		
	}

   /**
	* An iterator that traverses the tree in order
	*/
	public class BTreeIterator implements Iterator<E>
	{
		Stack<E> values;

	   /**
		* No arg constructor
		*/
		public BTreeIterator()
		{
			values = new Stack<E>();
			traverseTreeInReverseOrder(root);
		}

		private void traverseTreeInReverseOrder(BTNode localRoot)
		{
			if (localRoot == null)
			{
				return;
			}
			ListIterator<E> keyIterator = localRoot.keys.listIterator(localRoot.keys.size());
			ListIterator<BTNode> childIterator = localRoot.children.listIterator(localRoot.children.size());

			while (keyIterator.hasPrevious())
			{
				if (childIterator.hasPrevious())
				{
					traverseTreeInReverseOrder(childIterator.previous());
				}
				values.push(keyIterator.previous());
			}
			if (childIterator.hasPrevious())
			{
				traverseTreeInReverseOrder(childIterator.previous());
			}
		}
		
	   /**
		* Returns the next value in the tree
		*
	    * @return	the next value in the tree
		*/
		public E next()
		{
			if (!this.hasNext())
			{
				throw new NoSuchElementException();
			}
			return values.pop();
		}

	   /**
		* Returns whether there is a next value in the tree
		*
	    * @return	a boolean indicating whether there is a next value in the tree
		*/
		public boolean hasNext()
		{
			return !values.empty();
		}

	   /**
		* Unsupported Operation
		*
		*/
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	/**
	* An iterator that iterates throught the levels of the tree
	*/
	public class BTreeLevelIterator implements Iterator<ArrayList<ArrayList<E>>>
	{
		LinkedList<ArrayList<ArrayList<E>>> levels;
		
	   /**
		* No arg constructor
		*/
		public BTreeLevelIterator()
		{
			levels = new LinkedList<ArrayList<ArrayList<E>>>();
			LinkedList<BTNode> currentLevel = new LinkedList<BTNode>();
			if (root != null)
			{
				currentLevel.add(root);
			}
			traverseTreeInLevelOrder(currentLevel);
		}

		private void traverseTreeInLevelOrder(LinkedList<BTNode> currentLevel)
		{
			if (currentLevel.size() == 0)
			{
				return;
			}

			ArrayList<ArrayList<E>> currentLevelKeys = new ArrayList<ArrayList<E>>();
			LinkedList<BTNode> nextLevel = new LinkedList<BTNode>();

			Iterator<BTNode> levelIterator = currentLevel.iterator();
			while (levelIterator.hasNext())
			{
				BTNode currentNode = levelIterator.next();
				ArrayList<E> currentNodeKeys = new ArrayList<E>();
				for (int i = 0; i < currentNode.keys.size(); i++)
				{
					currentNodeKeys.add(currentNode.keys.get(i));
				}			
				currentLevelKeys.add(currentNodeKeys);	
				nextLevel.addAll(currentNode.children);
			}
			traverseTreeInLevelOrder(nextLevel);

			levels.push(currentLevelKeys);
		}
		
	   /**
		* Returns the values of the next level in the tree
		*
	    * @return	an ArrayList containing the values in the next level of the tree
		*/
		public ArrayList<ArrayList<E>> next()
		{
			if (!this.hasNext())
			{
				throw new NoSuchElementException();
			}
			return levels.poll();
		}

	   /**
		* Returns whether there is a next level in the tree
		*
	    * @return	a boolean indicating whether there is a next level in the tree
		*/
		public boolean hasNext()
		{
			return levels.size() != 0;
		}

	   /**
		* Unsupported Operation
		*
		*/
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}
