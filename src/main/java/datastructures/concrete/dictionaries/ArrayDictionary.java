


package datastructures.concrete.dictionaries;

import java.util.Iterator;
import java.util.NoSuchElementException;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs; //stores associations between keys and values
    private int size; // number of key-value pairs stored
    private int openCell; //next cell to fill
    private int indexToReplace; 
    
    private static final int SIZE_AT_START = 10; //initial dictionary size
    private static final int RESIZE_FACTOR = 2; // dictionary resizing factor
    
    // You're encouraged to add extra fields (and helper methods) though!

    
    public ArrayDictionary() {
        pairs = makeArrayOfPairs(SIZE_AT_START);
        size = 0;
        openCell = 0;
        indexToReplace = 0;
        
    }

    /**
     *  This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);

    }

    private void keyCheck(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException();
        }
    }
    
    

    @Override
    public V get(K key) {
        this.keyCheck(key);
        for (Pair<K, V> p : this.pairs) {
            if (p != null) {
                if (p.key == key || p.key.equals(key)) {
                    return p.value;
                }
            }
        }
        return null;
    }


    @Override
    public void put(K key, V value) {
        if (this.containsKey(key)) {
            Pair<K, V> p = pairs[indexToReplace]; 
            if (p.key == key || p.key.equals(key)) {
                p.value = value;

            }

        } else {
            if (this.openCell < this.pairs.length) {
                this.pairs[this.openCell] = new Pair<K, V>(key, value);
                this.openCell = this.findNewOpenCell();
            } else {
                this.openCell = this.findNewOpenCell();
                this.pairs[this.openCell] = new Pair<K, V>(key, value);
            }
            size++;
        }
    }
    

    private int findNewOpenCell() {
        for (int i = 0; i < this.pairs.length; i++) {
            if (pairs[i] == null) {
                return i;
            }
        }
        Pair<K, V>[] newPairs = makeArrayOfPairs(this.size * RESIZE_FACTOR);
        for (int i = 0; i < this.pairs.length; i++) {
            newPairs[i] = pairs[i];
        }
        int newOpenCell =  this.pairs.length;
        this.pairs = newPairs;
        return newOpenCell;
    }

    

    @Override
    public V remove(K key) {
        this.keyCheck(key);
        for (int i = 0; i < this.pairs.length; i++) {
            if (this.pairs[i] != null) {
                if (pairs[i].key == key || pairs[i].key.equals(key)) {
                    V removed = pairs[i].value;
                    pairs[i] = null;
                    size--;
                    this.openCell = this.findNewOpenCell();
                    return removed;
                }
            }
        }
        
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        int replaceIndex = 0;
        for (Pair<K, V> p: this.pairs) {
            if (p != null) {
                if (p.key == key || (p.key != null && p.key.equals(key))) {
                    indexToReplace = replaceIndex;

                    return true;
                }
            }
            replaceIndex++;
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }
    
    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
    
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<K, V>(this.pairs); 
    }
    
    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        
        private  Pair<K, V>[] pairs;
        private int index;
        private KVPair<K, V> currentPair;
        
        public ArrayDictionaryIterator(Pair<K, V>[] pairs) {
            // You do not need to make any changes to this constructor.
            this.pairs = pairs;
            this.index = 0;
            this.currentPair = this.findFirstKVPair();
        }
        
        public boolean hasNext(){
            return this.index < this.pairs.length && this.currentPair != null;
        }
        
        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException(); 
            }
            if (this.currentPair != null){
                KVPair<K, V> oldPair = this.currentPair;
                this.index++;
                this.currentPair = this.findFirstKVPair();
                return oldPair;
            } else {
                this.index++;
            }
            
            return null;
        }
        
        // finds the first KVPair that exists starting from the inputed index
        private KVPair<K, V> findFirstKVPair() {
            for (int i = this.index; i < this.pairs.length; i++) {
                if (this.pairs[i] != null) {
                    this.index = i;
                    return new KVPair<K, V>(this.pairs[i].key, this.pairs[i].value);
                }
            }
            return null;
        }
    }
}

