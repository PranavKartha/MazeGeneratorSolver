package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;
//
/**
 * See IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    // Feel free to add more fields and constants.
    private static final int STARTING_SIZE = 5;
    private int count;
    
    public ArrayHeap() {
        this.heap = makeArrayOfT(STARTING_SIZE);
        this.count = 0;
    }

    /**
     * This method will return a new, empty array of the given count
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int size) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[size]);
    }

    @Override
    public T removeMin() {
        if (this.isEmpty()) {
            throw new EmptyContainerException();
        }
        T result = this.heap[0];
        this.heap[0] = this.heap[this.count - 1];
        this.heap[this.count - 1] = null;

        this.count--;
        
        percolate(heap[0], 0);
        
        return result;
    }

    @Override
    public T peekMin() {
        if (this.isEmpty()) {
            throw new EmptyContainerException();
        }
        
        return this.heap[0];
    }
    


    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        
        // reorganizing happens here
        // increment count
        
        
        // insertion
        insertHelper(item);
        this.count++;

        //traverse to correct place, relocate shit as needed
        // put that boi down
    }
    
    private void insertHelper(T item) {
        if (heap[0] == null) {
            heap[0] = item;
        } else {
            if (count >= this.heap.length) {
                this.recount();
            }
            heap[count] = item;
            relocate(item, count);
            

        }    
    }
    

    
    
    private void relocate(T item, int i) {
        T parentData = this.heap[(i - 1) / ArrayHeap.NUM_CHILDREN];
        if (item.compareTo(parentData) < 0) {
            heap[(i - 1) / ArrayHeap.NUM_CHILDREN] = item;
            heap[i] = parentData;
            relocate(item, (i - 1) / ArrayHeap.NUM_CHILDREN);
        }
    }
    
    private void percolate(T item, int i) {
        // NOTE: always percolate down, and replace with SMALLEST child
        
        // first check
        // base: not all kids are there
        // aka the kids arent all right
        if (count <= ArrayHeap.NUM_CHILDREN + 1) {
            for (int i1 = 0; i1 < count; i1++) {
                if (heap[0].compareTo(heap[i1]) > 0) {
                    T top = heap[0];
                    heap[0] = heap[i1];
                    heap[i1] = top;        
                }
            }
        } else {
       
            if (this.hasMaxKids(i)) {
                // #hail4Kids
            
                // case where we do shit
                
                int smallIndex = i;
                for (int j = 1; j <= 4; j++) {
                    if (heap[smallIndex].compareTo(this.heap[ArrayHeap.NUM_CHILDREN * i + j]) > 0) {
                        smallIndex = ArrayHeap.NUM_CHILDREN * i + j;
                    }
                }
                
                if (smallIndex != i) {
                  this.heap[i] = heap[smallIndex];
                  this.heap[smallIndex] = item;
                  percolate(item, smallIndex);
                } 

            } else {
                if (!this.hasNoKids(i)) {
  
                    int smallIndex = i;
                    for (int j = 1; j <= 3; j++) {
                         
                        if (size() < ArrayHeap.NUM_CHILDREN
                                && heap[ArrayHeap.NUM_CHILDREN * i + j] != null
                                && heap[smallIndex].compareTo(this.heap[ArrayHeap.NUM_CHILDREN * i + j]) > 0) {
                            smallIndex = ArrayHeap.NUM_CHILDREN * i + j;
                        }
                    }
                    
                    if (smallIndex != i) {
                      this.heap[i] = heap[smallIndex];
                      this.heap[smallIndex] = item;
                    }      

                }
            }
        }
    }    
    
    private boolean hasNoKids(int i) {
        return ArrayHeap.NUM_CHILDREN * i + 1 < this.count;
    }
    
    private boolean hasMaxKids(int i) {
        return ArrayHeap.NUM_CHILDREN * i + 4 < this.count;
    }
    
    private void recount() {
        T[] newHeap = this.makeArrayOfT(this.heap.length * (ArrayHeap.NUM_CHILDREN + 1));
        for (int i = 0; i < this.heap.length; i++) {
            newHeap[i] = this.heap[i];
        }
        this.heap = newHeap;
    }

    @Override
    public int size() {
        return this.count;
    }
}
