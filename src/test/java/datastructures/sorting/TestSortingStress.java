package datastructures.sorting;

import misc.BaseTest;
import misc.Searcher;

import org.junit.Test;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;

import static org.junit.Assert.assertTrue;

///
/**
 * See spec for details on what kinds of tests this class should include. 
 */
public class TestSortingStress extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }
    
    @Test(timeout=10*SECOND)
    public void testHeapInsertPeekRemoveStress() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        assertTrue(heap.isEmpty());
        int cap = 50000;
        for (int i = 0; i < cap; i++) {
            heap.insert(i);
        }
        
        assertTrue(!heap.isEmpty());
        
        for (int i = 0; i < cap; i++) {
            assertEquals(i, heap.removeMin());
        }
        
        assertTrue(heap.isEmpty());
    }
    
    @Test(timeout=10*SECOND)
    public void testTopKSortStress() {
        IList<String> list = new DoubleLinkedList<>();
        int cap = 50000;
        for (int i = 0; i < cap; i++) {
            list.add("" + i);
        }
        assertEquals(cap, list.size());
        
        int repeats = 2;
        for (int i = 0; i < repeats; i++) {
            IList<String> top = Searcher.topKSort(cap/3, list);
            assertEquals(cap/3, top.size());
        }
    }
}
