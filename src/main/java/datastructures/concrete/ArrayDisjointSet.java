package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import misc.exceptions.NotYetImplementedException;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private IDictionary<Integer, Integer> ranks;
    //  note: might just be totally vestigial... FIX IF NEEDED
    
    private static final int STARTING_SIZE = 100;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        this.pointers = new int[STARTING_SIZE];
        this.ranks = new ChainedHashDictionary<>();
    }

    @Override
    public void makeSet(T item) {

        //  assuming all hash codes are unique and positive
        int code = item.hashCode();
        if (code >= this.pointers.length) {
            this.resize(code + 1);
        }
        
        if (this.pointers[code] != 0) { 
            //  what if 0 is a parent
            //  fix later...
            throw new IllegalArgumentException();
        }
        
        //  item is new to Set
        // add -1 to code index
        // put code, 1 to ranks
        this.pointers[code] = -1;
        this.ranks.put(code, 1);
    }
    
    private void resize(int newSize) {
        int[] newBoi = new int[newSize];
        for (int i = 0; i < this.pointers.length; i++) {
            newBoi[i] = this.pointers[i];
        }
        this.pointers = newBoi;
    }

    @Override
    public int findSet(T item) {
        int code = item.hashCode();
        
        //  0 is default initial value.... for now
        if (this.pointers.length >= code || this.pointers[code] == 0) {
            throw new IllegalArgumentException();
        }
        //  ting should never have a parent with index 0...
        
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
            //  resolve tie via hashCodes
            //  largest hashCode wins
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
