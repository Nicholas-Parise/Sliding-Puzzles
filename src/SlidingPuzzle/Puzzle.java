package SlidingPuzzle;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Puzzle implements Comparable<Puzzle>{

    private int[][] board;
    public Puzzle previous;
    private int size;
    private int emptyRow, emptyCol;

    private int heuristic;
    private int depth;
    private int score;


    public Puzzle(){}

    public Puzzle(int size){
        this.size = size;
    }

    /**
     * used to make a copy of the puzzle to make moves
     */
    public Puzzle(int[][] board, int size, int emptyRow, int emptyCol, Puzzle previous){
        this.size = size;
        this.emptyCol = emptyCol;
        this.emptyRow = emptyRow;
        this.board = new int[size][size];
        this.previous = previous;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.board[i][j] = board[i][j]; // we initialize the values in the new board
            }
        }
    }


    public int getSize() {
        return size;
    }

    public int[][] getBoard() {
        return board;
    }

    /**
     * Calculate the sum of both heuristics and set the heuristic value
     */
    public void calcHeuristic(){
        heuristic = h_misplaced() + h_distance() + h_blank();
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void calcScore(){
        score = heuristic + depth;
    }

    public int getDepth() {
        return depth;
    }

    public int getScore() {
        return score;
    }


    /**
     * read in the file and set this puzzle to the provided values
     * @param filename name of the puzzle input
     */
    public void loadPuzzle(String filename) {

        ArrayList<String> input = new ArrayList<>();

        try {
            FileReader myFile = new FileReader(filename);
            BufferedReader reader = new BufferedReader(myFile);
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                input.addAll(Arrays.asList(parts));
            }
            reader.close();
            }catch (IOException e) {
                e.printStackTrace();
            }

        size = Integer.valueOf(input.get(0));
        input.remove(0);
        board = new int[size][size];

        int index = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(input.get(index).equals("X")){
                    emptyRow = i;
                    emptyCol = j;
                    board[i][j] = -1;
                }else{
                    board[i][j] = Integer.valueOf(input.get(index));
                }
                index++;
            }
        }
        calcHeuristic(); // calculate the heuristic value of the start matrix
        setDepth(0); // distance from start is 0 because it is the start.
        calcScore(); // add g(n) + h(n) together to set score.
    }


    /**
     * changes the board to be the solution matrix
     */
    public void createSolution(){
        board = new int[size][size];

        int num = 1;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                board[i][j] = num;
                num++;
            }
        }
        board[size-1][size-1] = -1;
        emptyRow = size-1;
        emptyCol = size-1;

        // should be 0

        calcHeuristic(); // calculate the heuristic value of the end matrix
        setDepth(0); // No depth in this case
        calcScore(); // add g(n) + h(n) together to set score.
    }



    /**
     * determines if a row and col are valid positions in the matrix
     * @param row row
     * @param col col
     * @return true if position is valid
     */
    public boolean isValid(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }


    /**
     * This method returns all moves possible for the current puzzle.
     * It does this by checking a direction adjacent to the blank space,
     * if that direction is valid it will create a copy of the current puzzle,
     * and then make a move on that copy, calculates the heuristic and score.
     * Finally, it adds it to the array.
     *
     * @return an arraylist of possible moves, minimum of 2, max of 4
     */
    public ArrayList<Puzzle> availableDirections(){

        ArrayList<Puzzle> directions = new ArrayList<>();

        if(isValid(emptyRow-1,emptyCol)){  //up
            Puzzle p = new Puzzle(board, size, emptyRow, emptyCol,this);
            p.moveTile(emptyRow-1,emptyCol);
            p.calcHeuristic();
            p.setDepth(this.depth+1);
            p.calcScore();
            directions.add(p);
        }
        if(isValid(emptyRow+1,emptyCol)){  //down
            Puzzle p = new Puzzle(board, size, emptyRow, emptyCol,this);
            p.moveTile(emptyRow+1,emptyCol);
            p.calcHeuristic();
            p.setDepth(this.depth+1);
            p.calcScore();
            directions.add(p);
        }
        if(isValid(emptyRow,emptyCol-1)){  //left
            Puzzle p = new Puzzle(board, size, emptyRow, emptyCol,this);
            p.moveTile(emptyRow,emptyCol-1);
            p.calcHeuristic();
            p.setDepth(this.depth+1);
            p.calcScore();
            directions.add(p);
        }
        if(isValid(emptyRow,emptyCol+1)){  //right
            Puzzle p = new Puzzle(board, size, emptyRow, emptyCol,this);
            p.moveTile(emptyRow,emptyCol+1);
            p.calcHeuristic();
            p.setDepth(this.depth+1);
            p.calcScore();
            directions.add(p);
        }

        return directions;
    }


    /**
     * switch the empty spot with the desired given tile
     * @param row
     * @param col
     */
    public void moveTile(int row, int col){

        board[emptyRow][emptyCol] = board[row][col];
        board[row][col] = -1;

        emptyRow = row;
        emptyCol = col;
    }


    /**
     * Blank space penalty heuristic
     * @return 1 or 0 if space is in correct spot
     */
    private int h_blank(){

        if(board[size-1][size-1] == -1)
            return 0;

        return 1;
    }


    /**
     * Heuristic to determine how many tiles are in the wrong place
     * @return # of misplaced tiles
     */
    private int h_misplaced(){

        int count = 0;
        int expectedValue = 1;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(board[i][j] != -1)  {    // skip blank space
                    if(board[i][j] != expectedValue) {
                        count++;
                    }
                }
                expectedValue++;
            }
        }
        return count;
    }

    /**
     * Heuristic to measure the distance each tile is from its correct position
     * @return sum of distances
     */
    private int h_distance(){

        int total = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                int value = board[i][j];

                if(value != -1)  {    // skip blank space

                    // here we calculate where the tile value is supposed to be
                    int expectedRow = (value-1) / size;
                    int expectedCol = (value-1) % size;

                    // now we calculate the distance from the actual to current
                    total += Math.abs(expectedRow - i) + Math.abs(expectedCol - j);
                }
            }
        }
        return total;
    }



    public static void print(Puzzle puzzle){

        if(puzzle == null){
            System.out.println("No solution found");
            return;
        }

        printAll(puzzle);
        System.out.println("It took "+puzzle.getDepth()+" moves to find a solution");
        System.out.println();
    }


    /**
     * recursively print all steps in order
     * @param puzzle
     */
    public static void printAll(Puzzle puzzle){

        if(puzzle == null){
            return;
        }

        // base case, start node doesn't have a previous
        if(puzzle.previous == null) {
            System.out.println(puzzle);
            return;
        }

        printAll(puzzle.previous);
        System.out.println(puzzle);
    }


    @Override
    public String toString() {
        String temp = "";
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                if(i == emptyRow && j == emptyCol){
                    temp += "X\t";
                }else{
                    temp += board[i][j] + "\t";
                }
            }
            temp += "\n";
        }
        return temp;
    }

    @Override
    public int compareTo(Puzzle o) {
        return ((Integer)score).compareTo(o.score);
    }

    /**
     * determines if two puzzles are equal
     * used to test if current is equal to solution
     * @param puzzle
     * @return true if the puzzles are equivalent
     */
    public boolean equals(Puzzle puzzle){

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(board[i][j] != puzzle.getBoard()[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }


    // Generate hash based on the board
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

}
