package paketti;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class MazeSolver {
	
	// Käyttäjän syöttämä polku
	static String pathString = "";
	
    public static void main(String[] args) {
       
    	Maze maze = getMaze();

        if (maze != null) {
        	solveMaze(maze, 20);
        	solveMaze(maze, 150);
        	solveMaze(maze, 200);
        }
    }
    
    /**
     * Haetaan pulma
     * @return
     */
    public static Maze getMaze() {
        JFileChooser fileChooser = new JFileChooser();
        
        String userDesktop = System.getProperty("user.home") + "\\Desktop";
        fileChooser.setCurrentDirectory(new java.io.File(userDesktop));
        
        int result = fileChooser.showOpenDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            String inputFile = fileChooser.getSelectedFile().getAbsolutePath();
            pathString = inputFile;
            return readMazeFromFile(inputFile);
        } else {
            JOptionPane.showMessageDialog(null, "Tiedostoa ei valittu.");
            System.exit(0); // Sulje ohjelma, jos tiedostoa ei valita
            return null;   // Tätä ei koskaan suoriteta, mutta tarvitaan kompiloinnin kannalta
        }
    }


    /**
     *  Luetaan Pulma Stringi ja tekaistaan siitä Maze olio
     * @param filename
     * @return
     */
    public static Maze readMazeFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int row = 0;
            int numRows = 0;
            int numCols = 0;

            while ((line = reader.readLine()) != null) {
                if (numCols == 0) {
                    numCols = line.length();
                }
                numRows++;
            }

            char[][] map = new char[numRows][numCols];

            try (BufferedReader innerReader = new BufferedReader(new FileReader(filename))) {
                while ((line = innerReader.readLine()) != null) {
                    map[row++] = line.toCharArray();
                }
            }

            return new Maze(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //	DFS, Depth-First Search ei takaa nopeinta reittiä maaliin
    // Tuloste
    /*
    #######E########E####################
    #*### #   ######*#    #     #     # E
    #*### ### #******#  #    #     #    #
    #*###*#*#*#*######*##################
    #************#*******#****#***#***#*#
    #**#*##******#*#####*#**#*#*#*#*#*#*#
    #**#*********#***#***#**#*#*#*#*#***#
    #**######***###**#**###*#*#*#*#*###*#
    #**#****#***************#***#***#***#
    #**#*##*########***##*###########***#
    #****##**********###****************#
    #*##*#############**###***####***##*#
    #**### ##         #**#**#***********#
    #**#   ## ####     #****#******###**#
    #**# #### #  #     #****#####*******#
    #**#      #      ###***********##***#
    #**#####***********#***##***#***#***#
    #***********************************#
    ##################*##################
    */
    
    /*
    public static void solveMaze(Maze maze) {
        char[][] map = maze.getMap();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int[][] route = new int[map.length][map[0].length];
        int[] start = maze.findStart();

        Stack<int[]> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int row = current[0];
            int col = current[1];
            int moves = route[row][col];

            if (map[row][col] == 'E') {
                printMazeWithRoute(maze, route, row, col);
                return;
            }

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < map.length && newCol >= 0 && newCol < map[0].length
                        && map[newRow][newCol] != '#' && route[newRow][newCol] == 0) {
                    stack.push(new int[]{newRow, newCol});
                    route[newRow][newCol] = moves + 1;
                }
            }
        }

        System.out.println("No solution found.");
    }
    
    */
    
    /*
     *  BFS-algoritmilla polun löytäminen pitäisi olla nopeinta
     */
    public static void solveMaze(Maze maze, int maxMoves) {
        char[][] map = maze.getMap();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int[][] route = new int[map.length][map[0].length];
        
        if (maze.findStart() == null) {
        	System.out.println("No starting point found.");
        	return;
		}
        int[] start = maze.findStart();

        Stack<int[]> stack = new Stack<>();
        start[2] = 0; // Setting initial moves to 0
        stack.push(start);

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int row = current[0];
            int col = current[1];
            int moves = current[2];

            if (map[row][col] == 'E') {
                printMazeWithRoute(maze, route);
                saveMazeWithRouteToFile(maze, route, "solvedMaze.txt");
                return;
            }

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                int newMoves = moves + 1;

                if (newRow >= 0 && newRow < map.length && newCol >= 0 && newCol < map[0].length
                        && map[newRow][newCol] != '#' && route[newRow][newCol] == 0
                        && newMoves <= maxMoves) {
                    stack.push(new int[]{newRow, newCol, newMoves});
                    route[newRow][newCol] = newMoves;
                }
            }
        }

        System.out.println("No solution found.");
    }
    
    /**
     * Tallennetaan maze ja kuljettu reitti tekstitiedostoon
     * @param maze
     * @param route
     * @param filename
     */
    public static void saveMazeWithRouteToFile(Maze maze, int[][] route, String filename) {
        char[][] map = maze.getMap();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathString + filename))) {
            for (int row = 0; row < map.length; row++) {
                for (int col = 0; col < map[row].length; col++) {
                    if (map[row][col] == 'E' && route[row][col] > 0) {
                        writer.write("E");
                    } else if (route[row][col] > 0) {
                        writer.write("*");
                    } else {
                        writer.write(map[row][col]);
                    }
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Printataan maze ja kuljettu reitti konsoliin
     * @param maze
     * @param route
     */
    public static void printMazeWithRoute(Maze maze, int[][] route) {
        char[][] map = maze.getMap();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] == 'E' && route[row][col] > 0) {
                    System.out.print("E");
                } else if (route[row][col] > 0) {
                    System.out.print("*");
                } else {
                    System.out.print(map[row][col]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }


}

/**
 * @author jobapplicant #12321
 *
 */
class Maze {
    private char[][] map;

    public Maze(char[][] map) {
        this.map = map;
    }

    public char[][] getMap() {
        return map;
    }
    
    /**
     * Haetaan alkupiste labyrintistä
     * @return
     */
    public int[] findStart() {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] == '^') {
                    return new int[]{row, col, 0};
                }
            }
        }
        return null;
    }

}
