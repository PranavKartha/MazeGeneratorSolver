package mazes.generators.maze;

import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;
import misc.graphs.Graph;

import java.util.Random;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    
    private static Random r = new Random(0);
    
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {
        // Note: make sure that the input maze remains unmodified after this method is over.
        //
        // In particular, if you call 'wall.setDistance()' at any point, make sure to
        // call 'wall.resetDistanceToOriginal()' on the same wall before returning.
        
        ISet<Room> rooms = maze.getRooms();
        ISet<Wall> allWalls = maze.getWalls();
        

        for (Wall wall: allWalls) {
            double rand = r.nextDouble();
            wall.setDistance(rand);
        }
        
        
        Graph<Room, Wall> g = new Graph<>(rooms, allWalls);
        
        // make MST of wall weights
        ISet<Wall> mst = g.findMinimumSpanningTree();
        
        // reset distances
        for (Wall wall: mst) {
            wall.resetDistanceToOriginal();
        }

        for (Wall wall: allWalls) {
            wall.resetDistanceToOriginal();
        }
        
        return mst;
        
        /*
         *  For some reason, we get the same maze on grid every time. ASK TA'S BABHUJi
         *  
         *  note: got this to work without making new copies of each wall
         */
    }
}
