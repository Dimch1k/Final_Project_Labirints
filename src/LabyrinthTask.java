import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class LabyrinthTask {


    public static void main(String[] args) {
        // leave uncommented only one line with file name file you need

//        char[][] board = writeLabyrinthBoardToArray("test.txt");

//        char[][] board = writeLabyrinthBoardToArray("maze_11x11.txt"); // res = 99 coins
//        char[][] board = writeLabyrinthBoardToArray("maze_31x31.txt"); // res = 1076 coins
        char[][] board = writeLabyrinthBoardToArray("maze_101x101.txt"); // res = 1763 coins

        System.out.println("Coins count=" + algorithm(board));
    }



    /*
    function that reads Labyrinth Board form file and returns char array
    initially lines are entered into the ArrayList to reduce readings (of rows and columns)
    */
    public static char[][] writeLabyrinthBoardToArray(String fileName) {
        List<char[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) { // writes into ArrayList
                rows.add(line.toCharArray());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        char[][] board = new char[rows.size()][rows.size()]; // creates array of chars from ArrayList size

        for (int i = 0; i < rows.size(); i++) { // reads from ArrayList - writes into char array
            board[i] = rows.get(i);
        }

        return board; // function returns char array
    }

    /*
    'LabyrinthNode' class contains data about any possible outcome on the labyrinth board.
    'row' and 'col' are position like x and y.
    'coins' is a sum of coins when field is reached.
    */
    static class LabyrinthNode implements Comparable<LabyrinthNode> { // implements interface to compare node using coins

        int row;
        int col;
        int coins;

        // mb -
        LabyrinthNode parentNode;

        LabyrinthNode(int row, int col, int coins, LabyrinthNode parentNode) {
            this.row = row;
            this.col = col;
            this.coins = coins;
            this.parentNode = parentNode;
        }

        public int compareTo(LabyrinthNode other) { // method to compare node using coins sum
            return Double.compare(this.coins, other.coins);
        }

    }

    /*
    method finds start and end point
    method use PriorityQueue adds and goes through possible moves
    if found -> outputs steps and amount of coins
    if not found -> outputs Integer.MIN_VALUE
    worst case time_comp=O(E log(V))=O( n * log(n)), space_comp=O(n) (used n=rows*columns)
    includes O(rows*columns) initial scan to find S and G
    */
    public static int algorithm(char[][] board) {
        int rows = board.length;
        int cols = board[0].length;

        int startRow = Integer.MIN_VALUE;
        int startCol = Integer.MIN_VALUE;
        int goalRow = Integer.MIN_VALUE;
        int goalCol = Integer.MIN_VALUE;

        // finds 'S' and 'G' - basic O(n*n) :D
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 'S') {
                    startRow = i;
                    startCol = j;
                } else if (board[i][j] == 'G') {
                    goalRow = i;
                    goalCol = j;
                }
            }
        }

        PriorityQueue<LabyrinthNode> priorityQueue = new PriorityQueue<>(); // creation of priority queue from LabyrinthNode objects (sorted by coins)
        boolean[][] visitedNodes = new boolean[rows][cols]; // creation of array to track cell that has already been visited. this prevents loops
        priorityQueue.add(new LabyrinthNode(startRow, startCol, 0, null)); // add the starting point 'S'

        // worst case time_comp=O(E log(V))=O( n * log(n)), space_comp=O(n) (used n=rows*columns)
        while (!priorityQueue.isEmpty()) { // pathfinding loop
            LabyrinthNode currNode = priorityQueue.poll(); // gets field with the least coins

            if (currNode.row == goalRow && currNode.col == goalCol) { // check if we reached 'G'
                printPath(currNode); // prints path 'S' -> 'G'
                return currNode.coins; // if yes -> returns min sum of coins to reach 'G'
            }

            if (visitedNodes[currNode.row][currNode.col]) { // check if we already visited this node
                continue; // skips if yes
            } else {
                visitedNodes[currNode.row][currNode.col] = true; // keeps working if no
            }

            for (int[] ways : waysToGo) { // in loop checks for all possible ways to go from array 'waysToGo' with directions
                int nextRow = currNode.row + ways[0]; // calculates new row
                int nextCol = currNode.col + ways[1]; // calculates new column

                // check that field have not gone out of the board (beyond the border) and the field is not a fence
                if (nextRow >= 0 && nextRow < rows && nextCol >= 0 && nextCol < cols && board[nextRow][nextCol] != 'X') {

                    // gets coin of field - checks for digit (takes value if digit), equates to 0, if field is 'S' or 'G'
                    int nextCoinVal = Character.isDigit(board[nextRow][nextCol]) ? board[nextRow][nextCol] - '0' : 0;

                    int nextCoinsSum = currNode.coins + nextCoinVal; // calculates new coins sum
                    priorityQueue.add(new LabyrinthNode(nextRow, nextCol, nextCoinsSum, currNode));
                }
            }
        }

        return Integer.MIN_VALUE; // if no ways found :(
    }

    /*
    an array with possible directions - to avoid code duplication
    */
    static int[][] waysToGo = {
            {1, 0},     // →
            {-1, 0},    // ←
            {0, 1},     // ↑
            {0, -1},    // ↓
            {1, 1},     // 🡥
            {1, -1},    // 🡦
            {-1, 1},    // 🡤
            {-1, -1}    // 🡧
    };

    /*
    function gets last node (Goal - field 'G') and creates a chain of parents
    then does a reverse collection (the beginning becomes the end, and the end becomes the beginning) and produces print
    */
    private static void printPath(LabyrinthNode node) {
        List<LabyrinthNode> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parentNode;
        }
        Collections.reverse(path);
        System.out.print("Steps '|row-col|=coins' : ");

        for (LabyrinthNode n : path) {
            System.out.print("|" + n.row + "-" + n.col + "|=" + n.coins + " -> ");
        }
        System.out.println();
    }

}


