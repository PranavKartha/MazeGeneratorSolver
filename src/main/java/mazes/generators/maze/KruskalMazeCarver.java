package mazes.generators.maze;

import datastructures.concrete.ChainedHashSet;
import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;
import misc.exceptions.NotYetImplementedException;
import misc.graphs.Graph;

import java.util.Random;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    
    private static final int RANDOM_CAP = 100;
    
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {
        // Note: make sure that the input maze remains unmodified after this method is over.
        //
        // In particular, if you call 'wall.setDistance()' at any point, make sure to
        // call 'wall.resetDistanceToOriginal()' on the same wall before returning.
        
        ISet<Room> rooms = maze.getRooms();
        ISet<Wall> walls = maze.getWalls();
        
        // make copy of walls
        ISet<Wall> wallCopy = new ChainedHashSet<>();
        for (Wall wall: walls) {
            wallCopy.add(new Wall(wall.getRoom1(), wall.getRoom2(), wall.getDividingLine()));
        }
        
        // randomly assign wall weights
        for (Wall wall: wallCopy) {
            Random r = new Random(RANDOM_CAP);
            double rand = r.nextDouble();
            wall.setDistance(rand);
        }
        
        Graph<Room, Wall> g =  new Graph<>(rooms, wallCopy);
        
        // make MST of wall weights
        ISet<Wall> mst = g.findMinimumSpanningTree();
        
        // reset distances
        for (Wall wall: mst) {
            wall.resetDistanceToOriginal();
        }
        for (Wall wall: wallCopy) {
            wall.resetDistanceToOriginal();
        }
        
        return mst;
        
        /*
         *  For some reason, we get the same maze on grid every time. ASK TA'S BABHUJi
         */
    }
}
