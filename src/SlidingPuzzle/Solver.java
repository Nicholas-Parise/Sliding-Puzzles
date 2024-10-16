package SlidingPuzzle;

import java.util.*;

public class Solver {

    /**
     * Iterative Deepening Depth First Search
     * @param src start
     * @param target solution
     * @param max_depth max amount of iterations
     * @return The solved puzzle, with the previous steps to reconstruct
     */
    public static Puzzle IDDFS(Puzzle src, Puzzle target, int max_depth) {
        // Repeatedly depth-limit search till the maximum depth.
        for (int i = 0; i <= max_depth; i++) {
            HashSet<Puzzle> visited = new HashSet<Puzzle>();
            Puzzle temp = DLS(src, target, i, visited);
            if (temp != null)
                return temp;
        }
        return null;
    }

    /**
     * Recursively traverses all the neighbouring states of the puzzle
     * until it reaches either max depth or finds the solution.
     * to limit our branching we keep a list of all the visited puzzles so far
     *
     * @param vertex current puzzle
     * @param target solution
     * @param limit max amount of iterations
     * @param visited list of visited puzzles
     * @return The solved puzzle, with the previous steps to reconstruct
     */
    private static Puzzle DLS(Puzzle vertex, Puzzle target, int limit, HashSet<Puzzle> visited) {
        if (vertex.equals(target))
            return vertex;

        // If reached the maximum depth, stop recurring.
        if (limit <= 0)
            return null;

        visited.add(vertex); // add the current vertex to the visited hashmap

        // Recur for all the vertices adjacent to source vertex
        for (Puzzle p : vertex.availableDirections()) {

            if(visited.contains(p)){
                continue; // if we visited this vertex already skip it
            }

            Puzzle temp = DLS(p, target, limit - 1, visited);
            if (temp != null) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Iteratively traverse through all of the
     *
     * The heuristic and depth from start are members of the puzzle class
     * and are all calculated in the availableDirections() method
     * Overloading the compareTo method we can use a priority queue
     * @param src start tile
     * @param target goal state
     * @return
     */
    public static Puzzle AStar(Puzzle src, Puzzle target){

        PriorityQueue<Puzzle> pq = new PriorityQueue<Puzzle>();
        HashMap<Puzzle,Integer> visited = new HashMap<Puzzle,Integer>();

        pq.add(src);
        visited.put(src,src.getScore());

        while (!pq.isEmpty()){

            Puzzle current = pq.poll();

            if(current.equals(target)){
                return current;
            }

            for (Puzzle p : current.availableDirections()) {

                // test if we have seen this puzzle before
                if(visited.containsKey(p) && visited.get(p) <= p.getScore()){
                    // if we got there faster in the past continue
                    continue;
                }

                visited.put(p,p.getScore());
                pq.add(p);
            }
        }
        return null;
    }

    // private
    private static Puzzle IDAStarSolution;


    /**
     * Iterative Deepening A* Search
     * @param start input puzzle
     * @param end solution puzzle
     * @return
     */
    public static Puzzle IDAStar(Puzzle start, Puzzle end) {

        int cutoff = start.getScore();  // Start threshold with the score of the initial puzzle

        Stack<Puzzle> path = new Stack<Puzzle>();
        path.push(start);

        IDAStarSolution = null;

        while (true) {
            int cheapestCost = search(end, path, cutoff);
            cutoff = cheapestCost;

            if(cheapestCost == -1){
                break; // found solution
            }
        }
        return IDAStarSolution;
    }


    /**
     * recursive function to performe IDA*
     * @param target solution
     * @param path stack holding the path
     * @param cutoff our threshold
     * @return the cheapest cost found
     */
    private static int search(Puzzle target, Stack<Puzzle> path, int cutoff) {

        Puzzle current = path.peek();

        int newCost = current.getScore();  // f(n) = g(n) + h(n)

        if (newCost > cutoff) {
            return newCost;  // This path exceeded the threshold
        }

        if (current.equals(target)) { // We found the solution, return found flag
            IDAStarSolution = path.peek(); // save the final solution
            return -1;
        }

        int min = Integer.MAX_VALUE;

        // Explore all possible moves
        for (Puzzle child : current.availableDirections()) {

            if(!path.contains(child)){

                path.push(child);
                int childPathCost = search(target,path,cutoff);
                min = Math.min(childPathCost,min);
                path.pop();
            }
        }
        return min;
    }


}
