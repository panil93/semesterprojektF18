package dk.sdu.mmmi.cbse.ai;

/**
 *
 * @author Group 7
 */
public class PQHeap {

	private Element[] elements;
	private int size = 0;

	/**
	 * This is the contructor. The maxElements parameter is used to define the
	 * maximum size of the list
	 *
	 * @param maxElements
	 */
	public PQHeap(int maxElements) {
		this.elements = new Element[maxElements];
	}

	public Element extractMin() {
		Element min = elements[0]; // the minimum element is the element at the first index
		elements[0] = elements[size - 1]; // Now the last element replaces the first element
		size--; // The size is descreased by one because we are extracting an element
		minHeapify(0); // minHeapify is called in order to mantain the min-heap property

		return min; //the minimum element is returned
	}

	public void insert(Element e) {
		this.size++; //Size is increased because a new element is added
		int i = this.size - 1; //i is set to point at last index
		elements[i] = e; //The element is first inserted at the last index
		while (i > 0 && elements[getParentIndex(i)].key > elements[i].key) { //Loop continues until i is either the first index or if the element is larger than its parent
			Element temporaryElement = elements[i]; //Placeholder for when exchanging element at index i and its parent element
			elements[i] = elements[getParentIndex(i)]; // Parent element is now moved to index i
			elements[getParentIndex(i)] = temporaryElement; // and the given element i moved to parent slot
			i = getParentIndex(i); // Updating index
		}
	}

	public void insert(Node node) {
		insert(new Element(node.getFScore(), node));
	}

	private void minHeapify(int i) {
		int l = getLeftChildIndex(i); // gets index of left child
		int r = getRightChildIndex(i); // gets index of right child

		int smallest; // index of element with the smallest key
		if (l < size && elements[l].key < elements[i].key) { // If left child is within the list of elements and that childs key is smaller, then ...
			smallest = l; // smallest is set to l
		} else {
			smallest = i; // else i is smallest
		}
		if (r < size && elements[r].key < elements[smallest].key) { // If right child is within the list of elements and that childs key is smaller, then ...
			smallest = r; // smallest is set to r
		}
		if (smallest != i) { // if one of the child element keys are smallest
			Element temporaryElement = elements[i]; // Placeholder for exchaning element at index i with the smallest element
			elements[i] = elements[smallest]; // Exchanging
			elements[smallest] = temporaryElement;
			minHeapify(smallest); // Update list with new index
		}
	}

	private int getParentIndex(int i) {
		return ((i + 1) / 2) - 1; // Adjusted for index 0 start
	}

	private int getLeftChildIndex(int i) {
		return ((i + 1) * 2) - 1; // Adjusted for index 0 start
	}

	private int getRightChildIndex(int i) {
		return (i + 1) * 2; // Adjusted for index 0 start
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean containsNode(Node node) {
		boolean t = false;
		for (int i = 0; i < size; i++) {
			if (elements[i].data.equals(node)) {
				t = true;
			}
		}
		return t;
	}
}
