package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        insert(this.size, item);
    }

    @Override
    public T remove() {
        if (size == 0) {
            throw new EmptyContainerException();
        }
        if (size == 1) {
            size--;
            Node<T> removed = front;
            front = null;
            back = null;
            return removed.data;
        } else {
            size--;
            Node<T> removed = back;
            back = removed.prev;
            return removed.data;
        }
    }

    private void outOfBounds(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }     
    }
    
    @Override
    public T get(int index) {
        outOfBounds(index);
        Node<T> current = getToIndex(index);
        return current.data; 
    }

    @Override
    public void set(int index, T item) {
        outOfBounds(index);
        if (index == 0) {
            front  = new Node<T>(null, item, front.next);
            if (front.next != null) {
                front.next.prev = front;
            }
        }else if (index == size - 1) {
            back.prev.next = new Node<T>(back.prev, item, null);
            back = back.prev.next;
        } else {
            Node<T> current = getToIndex(index);
            Node<T> newNode = new Node<T>(current.prev, item, current.next);
            current.prev.next = newNode;
            current.next.prev = newNode;
        }
    }

    private Node<T> getToIndex(int index){
        Node<T> current = front;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;   
    }
    
    @Override
    public void insert(int index, T item) {
        if (index >= this.size + 1 || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        
        if (this.front == null) {
            this.front = new Node<T>(item);
            this.back = this.front;
        } else if (index <= this.size / 2) {
            Node<T> finder = this.front;
            for (int i = 0; i < index; i++) {
                finder = finder.next;
            }
            Node<T> inserto = new Node<T>(finder.prev, item, finder);
            if (inserto.prev != null) {
                inserto.prev.next = inserto;
            }
            finder.prev = inserto;
            if (index == 0) {
                this.front = inserto;
            }
        } else if (index < this.size) {
            Node<T> finder = this.back;
            for (int i = this.size - 1; i > index; i--) {
                finder = finder.prev;
            }
            Node<T> inserto = new Node<T>(finder.prev, item, finder);
            finder.prev.next = inserto;
            finder.prev = inserto;
        }    
          else {
            this.back.next = new Node<T>(this.back, item, null);
            this.back = this.back.next;
        }
        this.size++;
    }

    @Override
    public T delete(int index) {
        outOfBounds(index);
        if (index == 0) {
            size--;
            T first = front.data;
            front = front.next;
            return first;
        } else if (index == size - 1){
            size--;
            T last = back.data;
            back = back.prev;
            back.next = null;
            return last;
        } else {
            Node<T> current = getToIndex(index);
            current.prev.next = current.next;
            current.next.prev = current.prev;
            current.next = null;
            current.prev = null;
            size--;
            return current.data;
        }
    }

    @Override
    public int indexOf(T item) {
        Node<T> current = front;
        if (current != null) {
            for (int index = 0; index < size; index++) {
                if (current.data == item || current.data.equals(item)) {
                    return index;
                } else {
                    current = current.next; 
                }
            }   
        }
        return -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(T other) {
        Node<T> current = front;
        if (current != null) {
            for (int i = 0; i < size; i++) {
                if (current.data == other ||(current.data != null && current.data.equals(other))) {
                    return true;
                }
                current = current.next;
            }     
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        //  Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            Node<T> newNode= current; 
            current = current.next;
            return newNode.data;
        }
    }
}
