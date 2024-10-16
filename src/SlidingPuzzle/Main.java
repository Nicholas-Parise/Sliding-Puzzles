package SlidingPuzzle;

public class Main {

    public static void main(String[] args) {
        Puzzle start = new Puzzle();
        start.loadPuzzle("puzzle_input.txt");

        Puzzle goal = new Puzzle(start.getSize());
        goal.createSolution();

        System.out.println("Iterative depth first search");
        Puzzle solution = Solver.IDDFS(start, goal, 15);
        Puzzle.print(solution);

        System.out.println("A*");
        Puzzle solution2 = Solver.AStar(start, goal);
        Puzzle.print(solution2);

        System.out.println("Iterative deepening A*");
        Puzzle solution3 = Solver.IDAStar(start, goal);
        Puzzle.print(solution3);
    }
}
