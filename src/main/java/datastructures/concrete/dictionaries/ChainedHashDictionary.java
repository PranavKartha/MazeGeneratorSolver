    package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    // More fields
    private int hashFactor;
    private int numElements;
    private double loadFactor;
    private static final double LOAD_FACTOR_THRESHOLD = 3;
    private static final int STARTING_SIZE = 10;
    
    
    public ChainedHashDictionary() {
        chains = makeArrayOfChains(STARTING_SIZE);
        hashFactor = chains.length;
        numElements = 0;
        loadFactor = 0;
    }
    
    private void reLoadFactor() {
        this.loadFactor = (1.0 * this.numElements) / (1.0 * this.chains.length);
    }
    
    private int hashKey(K key) {
        if (key == null) {
            return 0;
        } else {
            return Math.abs(key.hashCode()) % this.hashFactor;
        }
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
        this.keyCheck(key);
        int hashKey = this.hashKey(key);
        return this.chains[hashKey].get(key);
    }

    @Override
    public void put(K key, V value) {
        if (this.loadFactor >= LOAD_FACTOR_THRESHOLD) {
            this.reSizeReHash();
        }
        
        int hashKey = this.hashKey(key);
        
        if (this.containsKey(key)) {
            // we have el Key
            this.chains[hashKey].put(key, value);
        } else {
            // we don't have el Key

            if (this.chains[hashKey] == null) {
                this.chains[hashKey] = new ArrayDictionary<K, V>();
            }
            this.chains[hashKey].put(key, value);
            
            this.numElements++;
            this.reLoadFactor();
        }
        
        if (this.loadFactor >= LOAD_FACTOR_THRESHOLD) {
            this.reSizeReHash();
        }
    }
    
    private void reSizeReHash() {

        IDictionary<K, V>[] newChains = makeArrayOfChains(chains.length * 2);
        this.hashFactor = newChains.length;

        for (int i = 0; i < this.chains.length; i++) {
            if (this.chains[i] != null) {
                for (KVPair<K, V> pair: this.chains[i]) {
                    int hashKey = this.hashKey(pair.getKey());
                    
                    if (newChains[hashKey] == null) {
                        newChains[hashKey] = new ArrayDictionary<K, V>();
                    }
                    
                    newChains[hashKey].put(pair.getKey(), pair.getValue());
                }
            }
        }
        this.chains = newChains;
        this.reLoadFactor();
    }

    @Override
    public V remove(K key) {
        this.keyCheck(key);
        int hashKey = this.hashKey(key);
        this.numElements--;
        return this.chains[hashKey].remove(key);
    }
    
    private void keyCheck(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException();
        }
    }

    @Override
    public boolean containsKey(K key) {
        int hashKey = this.hashKey(key);
        
        return this.chains[hashKey] != null && this.chains[hashKey].containsKey(key);
    }

    @Override
    public int size() {
        return this.numElements;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        // other fieldy-bois 1 and 2...
        private int index;
        private Iterator<KVPair<K, V>> currentIterator;
        

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            // instantiate other fieldy-bois
            this.index = this.findFirstHash(0);
            if (this.index > -1 && this.index < this.chains.length) {
                this.currentIterator = this.chains[this.index].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            // make sure of:
            // 1. still within chains.length
            // 2. make sure there is a value past the
            //   current one <not necessarily next to>
            return (this.index < this.chains.length && this.index >= 0) && 
                    (this.currentIterator.hasNext() || this.findFirstHash(this.index + 1) != -1);
        }

        @Override
        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            
            // assuming we're on a valid dictionary
            
            // if we've completely traversed the current dictionary
            if (!this.currentIterator.hasNext()) {
                this.index = this.findFirstHash(this.index + 1);
                // what about when index == -1
                if (this.index == -1) {
                    return null;
                }
                // we move to traversing the next valid one, assuming it exists.
                // This can be assumed because of the above check, the method
                // would return null if no such iterator existed
                this.currentIterator = this.chains[this.index].iterator();
            }
            // At this point, either the initial iterator (which has a next) would increment,
            // or the currentIterator now refers to a newly-initialized iterator that hasn't
            // done any traversal. Both cases guarantee a value exists, as the iterators do not
            // allow the traversal of null objects.
            return this.currentIterator.next();
        }
        
        private int findFirstHash(int start) {
            for (int i = start; i < this.chains.length; i++) {
                if (this.chains[i] != null && this.chains[i].iterator().hasNext()) {
                    return i;
                }
            }
            return -1;
        }
    }
}
