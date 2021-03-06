package misc.graphs;


import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Searcher;
import misc.exceptions.NoPathExistsException;


/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated then usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've contrained Graph
    //   so that E *must* always be an instance of Edge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the Edge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.
    
    private IDictionary<V, ISet<E>> graph;
    private IList<V> vertices;
    private IList<E> edges;
    /**
     * 
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException  if any of the edges have a negative weight
     * @throws IllegalArgumentException  if one of the edges connects to a vertex not
     *                                   present in the 'vertices' list
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        graph = new ChainedHashDictionary<>();
        for (E edge:edges) {
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }
            if (!vertices.contains(edge.getVertex1()) || !vertices.contains(edge.getVertex2())) {
                throw new IllegalArgumentException();
            }
            
            
            if (!graph.containsKey(edge.getVertex1())) {
                ISet<E> newSet = new ChainedHashSet<>();
                newSet.add(edge);
                graph.put(edge.getVertex1(), newSet);
            }else {
                ISet<E> newSet = graph.get(edge.getVertex1());
                newSet.add(edge);
                graph.put(edge.getVertex1(), newSet);               
            }
            
            if (!graph.containsKey(edge.getVertex2())) {
                ISet<E> newSet = new ChainedHashSet<>();
                newSet.add(edge);
                graph.put(edge.getVertex2(), newSet);

            }else {
                ISet<E> newSet = graph.get(edge.getVertex2());
                newSet.add(edge);
                graph.put(edge.getVertex2(), newSet);  
            }           
        }
        this.vertices = vertices;
        this.edges = edges;
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.vertices.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.edges.size();

    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        IList<E> sortedEdges = this.sortedEdges();
        ISet<E> minTree = new ChainedHashSet<>();
        IDisjointSet<V> components = new ArrayDisjointSet<>();
        for (V vertex: this.vertices) {
            components.makeSet(vertex);
        }
        
        for (E edge:sortedEdges) {
            V v1 = edge.getVertex1();
            V v2 = edge.getVertex2();
            if (components.findSet(v1) != components.findSet(v2)) {
                components.union(v1, v2);
                minTree.add(edge);
            }
            
            /*
             * going through edges in increasing order
             * first check:
             *      do related vertices incest?
             *      if so
             *          nothing, joffrey was enough of a mistake
             *      otherwise
             *          add that shit, natural selection for the win
             *  DONE LIKE THE RED WEDDING
             *  next check:
             *      is there only one parent?
             *      if so
             *          end me
             *      otherwise
             *          continue the loop
             * 
             */
        }
        
        return minTree;
        
    }
    private IList<E> sortedEdges() {
        return Searcher.topKSort(edges.size(), edges);   
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     */
    
    //why is graph empty??
    @SuppressWarnings("unchecked")
    public IList<E> findShortestPathBetween(V start, V end) {
        if (start.equals(end)) {
            return new DoubleLinkedList<E>();
        }
        VNode startNode = new VNode(start, 0, null, null);
        IPriorityQueue<VNode> heap = new ArrayHeap<>();
        heap.insert(startNode);
        IDictionary<V, VNode> vNodes = new ChainedHashDictionary<>();        
        vNodes.put(start, startNode);
        for (V vertex:this.vertices) {
            if (!vertex.equals(start)){
                VNode inserted = new VNode(vertex, null, null);
                vNodes.put(vertex, inserted);
                heap.insert(inserted);
               }
            }
    

        
        
        while (!heap.isEmpty()) {
            V vertex= (V) heap.removeMin().vertex;
            VNode vertexNode = vNodes.get(vertex);
            ISet<E> vEdges = graph.get(vertex);
            for (E edge:vEdges) {
                V otherVertex = edge.getOtherVertex(vertex);
                VNode otherVertexNode = vNodes.get(otherVertex);
                double newDistance = edge.getWeight() + vertexNode.distance;
                if (newDistance < otherVertexNode.distance) {
                    otherVertexNode.distance = newDistance;
                    otherVertexNode.daddi = vertex;
                    otherVertexNode.edge = edge;
                    vNodes.put(otherVertex, otherVertexNode);
                    heap.insert(otherVertexNode);
                }
            }
        }
        
        IList<E> shortestPathEdge = new DoubleLinkedList<>();
        VNode current = vNodes.get(end);

        boolean hitsStart = false;
        while (current.daddi != null) {
            E currentEdge = (E) current.edge;
            if (currentEdge.getVertex1().equals(start) ||
                    currentEdge.getVertex2().equals(start)) {
                hitsStart = true;
            }
            V parent = (V) current.daddi;
            shortestPathEdge.insert(0, (E) current.edge);
            current = vNodes.get((V) current.daddi);
            current = vNodes.get(parent);
        }
        if (!hitsStart) {
           throw new NoPathExistsException(); 
        }
        return shortestPathEdge;
    }
        
        
    
    
    //  insert internal class for Heap structure here
    //  stores vertex and related distance
    //  all go in min-heap, top vertex is start 
    private static class VNode implements Comparable<VNode> {
        public double distance;
        public Object vertex;
        public Object daddi;
        public Object edge;
        public VNode(Object vertex, double distance, Object papa, Object edge) {
            this.vertex = vertex;
            this.distance = distance;
            this.daddi = papa;
            this.edge = edge;
        }
        
        public VNode(Object vertex, Object daddi, Object edge) {
            this(vertex, Double.POSITIVE_INFINITY, daddi, edge);
        }
        
        public int compareTo(VNode other) {
            if (this.distance > other.distance) {
                return 1;
            } else if (other.distance > this.distance) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
    

