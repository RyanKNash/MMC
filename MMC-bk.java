import java.util.*;
import java.io.*;
import javax.sound.sampled.*;

// Node represents a single cell in the maze
class Node {
    private Stack<Character> paths = new Stack<>(); // Possible movement directions
    private boolean blocked = false; // Whether this cell is blocked
    private boolean origin = false; // Start position
    private boolean destination = false; // End position (cheese)

    // Add a path direction ('u', 'd', 'l', 'r')
    public void putPath(char c) {
        paths.push(c);
    }

    // Get all available paths
    public Stack<Character> getPaths() {
        return paths;
    }

    // Getters and Setters for blocked, origin, destination
    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isOrigin() {
        return origin;
    }

    public void setOrigin(boolean origin) {
        this.origin = origin;
    }

    public boolean isDestination() {
        return destination;
    }

    public void setDestination(boolean destination) {
        this.destination = destination;
    }
}

// Board manages the 5x5 maze grid
class Board {
    private final int SIZE = 5;
    private Node[][] cells = new Node[SIZE][SIZE];

    // Constructor: initializes and fills the board
    public Board(List<String> boardLines) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new Node();
            }
        }
        populateCells(boardLines);
        setupPaths();
    }

    // Populate the cells based on input characters
    private void populateCells(List<String> lines) {
        for (int i = 0; i < SIZE; i++) {
            String line = lines.get(i).trim();
            for (int j = 0; j < SIZE; j++) {
                Node cell = cells[i][j];
                char c = line.charAt(j);
                if (c == 'S') {
                    cell.setBlocked(false);
                    cell.setOrigin(true);
                } else if (c == 'O') {
                    cell.setBlocked(false);
                } else if (c == 'X') {
                    cell.setBlocked(true);
                } else if (c == 'F') {
                    cell.setBlocked(false);
                    cell.setDestination(true);
                }
            }
        }
    }

    // Setup paths (directions you can move) based on neighboring cells
    private void setupPaths() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Node current = cells[i][j];
                if (current.isBlocked()) continue;

                if (i > 0 && !cells[i - 1][j].isBlocked())
                    current.putPath('u');
                if (i < SIZE - 1 && !cells[i + 1][j].isBlocked())
                    current.putPath('d');
                if (j > 0 && !cells[i][j - 1].isBlocked())
                    current.putPath('l');
                if (j < SIZE - 1 && !cells[i][j + 1].isBlocked())
                    current.putPath('r');
            }
        }
    }

    // Find the starting position (origin)
    public int[] getOrigin() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (cells[i][j].isOrigin()) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{0, 0};
    }

    // Access a specific cell by coordinates
    public Node accessCell(int x, int y) {
        return cells[x][y];
    }

    public int getSize() {
        return SIZE;
    }
}

// MouseBrain controls the search (Depth-First Search)
class MouseBrain {
    private Board board;
    private Set<String> visited = new HashSet<>(); // Tracks visited cells
    private Stack<int[]> stack = new Stack<>(); // Stack for DFS

    // Constructor: set origin as starting point
    public MouseBrain(Board board) {
        this.board = board;
        int[] origin = board.getOrigin();
        stack.push(origin);
    }

    // Get the next move (returns x, y, and foundGoal flag)
    public int[] getNextMove() {
        while (!stack.isEmpty()) {
            int[] pos = stack.pop();
            int x = pos[0];
            int y = pos[1];

            String key = x + "," + y;
            if (visited.contains(key)) {
                continue;
            }

            visited.add(key);
            Node cell = board.accessCell(x, y);

            if (cell.isDestination()) {
                return new int[]{x, y, 1}; // Found goal
            }

            // Explore paths
            List<Character> paths = new ArrayList<>(cell.getPaths());
            Collections.reverse(paths); // Reverse for DFS

            for (char dir : paths) {
                int nx = x, ny = y;
                if (dir == 'u') nx--;
                if (dir == 'd') nx++;
                if (dir == 'l') ny--;
                if (dir == 'r') ny++;

                if (0 <= nx && nx < board.getSize() && 0 <= ny && ny < board.getSize()) {
                    String nextKey = nx + "," + ny;
                    if (!visited.contains(nextKey)) {
                        stack.push(new int[]{nx, ny});
                    }
                }
            }

            return new int[]{x, y, 0}; // Not goal yet
        }
        return null; // No path found
    }
}

// Main program class
public class MMC {
    private Board space;
    private MouseBrain brain;

    // Constructor: initialize the board and brain
    public MMC(List<String> boardLines) {
        this.space = new Board(boardLines);
        this.brain = new MouseBrain(space);
    }

    // Print the board layout before starting
    private void printBoard() {
        for (int i = 0; i < space.getSize(); i++) {
            for (int j = 0; j < space.getSize(); j++) {
                Node cell = space.accessCell(i, j);
                if (cell.isOrigin()) {
                    System.out.print("S ");
                } else if (cell.isDestination()) {
                    System.out.print("F ");
                } else if (cell.isBlocked()) {
                    System.out.print("X ");
                } else {
                    System.out.print("O ");
                }
            }
        System.out.println();
        }
    }


    // Walk the board (solve the maze)
    public void walkBoard() {
        System.out.println("Initial Maze Layout:");
        printBoard();
        System.out.println(); // Blank line for spacing
        int count = 0;
        while (true) {
            int[] result = brain.getNextMove();
            if (result == null) {
                System.out.println("> The mouse goes hungry tonight! <");
                playSound("fail.wav");
                break;
            }

            int x = result[0];
            int y = result[1];
            boolean foundGoal = result[2] == 1;

            System.out.println("> MOVED TO: (" + (x+1) + "," + (y+1) + ")");

            if (foundGoal) {
                System.out.println("> The Mouse got the cheese at cell (" + (x+1) + "," + (y+1) + ") in " + count + " moves! <");
                playSound("complete.wav");
                break;
            }

            count++;
        }
    }

    // Play sound from a file
    private void playSound(String fileName) {
        try {
            File soundFile = new File(fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

            // Wait for clip to finish playing
            Thread.sleep(clip.getMicrosecondLength() / 1000);
        } catch (Exception e) {
            System.out.println("(Sound Error) " + e.getMessage());
        }
    }

    // Main method: Entry point
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java MMC <board_file>");
        } else {
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            MMC tester = new MMC(lines);
            tester.walkBoard();
        }
    }
}