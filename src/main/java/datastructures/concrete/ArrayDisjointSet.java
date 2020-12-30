package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private int counter;
    private IDictionary<T, Integer> converter;
    
    private static final int STARTING_SIZE = 10;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        this.pointers = this.makeNewArray(STARTING_SIZE);
        this.counter = 0;
        this.converter = new ChainedHashDictionary<>();
    }
    
    private int[] makeNewArray(int size) {
        //  "empty" slots of array filled with value of array length
        int[] bic = new int[size];
        for (int i = 0; i < size; i++) {
            bic[i] = size;
        }
        return bic;
    }

    @Override
    public void makeSet(T item) {
        //  codes based on count
        int code = this.counter;
        if (code >= this.pointers.length) {
            this.resize(this.pointers.length * 2);
        }
        
        int emptySlot = this.pointers.length;
        if (this.pointers[code] != emptySlot) { 
            throw new IllegalArgumentException();
        }
        
        //  item is new to Set
        //  add -1 to code index
        this.pointers[code] = -1;
        this.converter.put(item, code);
        this.counter++;
    }
    
    private void resize(int newSize) {
        int[] newBoi = this.makeNewArray(newSize);
        int oldSize = this.pointers.length;
        for (int i = 0; i < oldSize; i++) {
            if (this.pointers[i] != oldSize) {
                newBoi[i] = this.pointers[i];
            }
        }
        this.pointers = newBoi;
    }

    @Override
    public int findSet(T item) {
        if (!this.converter.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        
        int code = this.converter.get(item);
        
        //  find parent of ting
        return this.findParent(code);
    }
    
    private int findParent(int kidCodey) {
        if (this.pointers[kidCodey] < 0) {
            // if the value at this kidCodey is < 0, return -1 * pointers[kidCodey]
            return kidCodey;
        } else {
            //  otherwise, keep checkin ya dumb kreken
            int newCode = this.pointers[kidCodey];
            return this.findParent(newCode);
        }
    }

    @Override
    public void union(T item1, T item2) {
        
        //  if items aren't legal... OR
        int parent1 = this.findSet(item1);
        int parent2 = this.findSet(item2);
        //  if share same parent *cough*incesting*cough*
        if (parent1 == parent2) {
            throw new IllegalArgumentException();
        }
        
        //  keeping things positive b/c there's enough negativity in life
        int rank1 = this.pointers[parent1] * -1;
        int rank2 = this.pointers[parent2] * -1;
        
        if (rank1 > rank2) {
            this.pointers[parent2] = parent1;
        } else if (rank1 < rank2) {
            this.pointers[parent1] = parent2;
        } else {
            //  rank-tie case
            //  resolve tie via index value
            //  largest index value wins
            if (parent1 > parent2) {
                this.pointers[parent1]--;
                this.pointers[parent2] = parent1;
            } else {
                this.pointers[parent2]--;
                this.pointers[parent1] = parent2;
            }
        }
    }
}
