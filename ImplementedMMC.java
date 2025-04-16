import java.util.Stack;
import java.util.Random;
import java.io.*;
import java.util.Scanner;


private void readBoard() throws Exception {
	input = new File(boardFileName);
	read = new Scanner(input);
	int i = 0, j = 0;
	String c;
	while(read.hasNext()) {
	    c = read.next();
	    for(int k = 0; k < c.length(); k++) {
		if(c.charAt(k) == 'S') {
		    cells[i][j].blocked = false;
		    cells[i][j].origin = true;
		    cells[i][j].destination = false;
		    j++;
	        }
		else if(c.charAt(k) == 'O') {
		    cells[i][j].blocked = false;
		    cells[i][j].origin = false;
		    cells[i][j].destination = false;
		    j++;
		}
		else if(c.charAt(k) == 'X') {
		    cells[i][j].blocked = true;
		    cells[i][j].origin = false;
		    cells[i][j].destination = false;
		    j++;
		}
		else if(c.charAt(k) == 'F') {
		    cells[i][j].blocked = false;
		    cells[i][j].origin = false;
		    cells[i][j].destination = true;
		}
	    }
	    i++;
	    j = 0;
	}
	read.close();
    }

public class ImplementedMMC {
    public static void main(String[] args) throws Exception {
        StackAITester maze = new StackAITester(args[0]);
        maze.walkBoard();
        }
}
