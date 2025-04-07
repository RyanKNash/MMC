/**
   This example demonstrates the use of stacks to journey from
   the (0,0) origin to the (SIZE-1,SIZE-1) destination.
   @author John Summers
 */

import java.util.Stack;
import java.util.Random;
import java.io.*;
import java.util.Scanner;
import sun.audio.*;

class Node {
    private Stack<Character> paths = new Stack<>();    
    private Random spawner = new Random();
    public boolean blocked;
    public boolean origin;
    public boolean destination;

    public Node() {
	//if(spawner.nextDouble() <= 0.4) blocked = true;
	//else blocked = false;
    }
    
    public void putPath(char c) {
	paths.push(c);
    }
    
    public char getPath() {
	return paths.pop();
    }

    public char peekPath() {
	if(paths.empty())
	    return '*';
	return paths.peek();
    }
}

class Board {
    private final int SIZE = 5;
    private Node[][] cells = new Node[SIZE][SIZE];
    private File input;
    private Scanner read;
    private String boardFileName;

    public Board(String bFN) {
	boardFileName = bFN;
	for(int i = 0; i < SIZE; i++)
	    for(int j = 0; j < SIZE; j++)
		cells[i][j] = new Node();
	
	try {
	    readBoard();
	}
	catch(Exception e) {
		System.out.println("*> COULD NOT READ THE BOARD FILE <*");
		System.exit(0);
	}

	for(int i = 0; i < SIZE; i++) {
	    for(int j = 0; j < SIZE; j++) {
		//cells[i][j] = new Node();
		if(cells[i][j].blocked == false) {
		    if(!(i == 0 || i == (SIZE-1)) && !(j == 0 || j == (SIZE-1))) {
		        if(cells[i][j-1].blocked == false)
			    cells[i][j].putPath('l');
			if(cells[i-1][j].blocked == false)
			    cells[i][j].putPath('u');
			if(cells[i+1][j].blocked == false)
			    cells[i][j].putPath('d');
			if(cells[i][j+1].blocked == false)
			    cells[i][j].putPath('r');
		    }
		    if(i == 0) {
			if(j > 0) {
			    if(cells[i][j-1].blocked == false)
				cells[i][j].putPath('l');
			    
			}
			if(cells[i+1][j].blocked == false)
			    cells[i][j].putPath('d');
			if(j < (SIZE-1)) {
			    if(cells[i][j+1].blocked == false)
				cells[i][j].putPath('r');
			}
		    }
		    if(i == (SIZE-1)) {
			if(cells[i-1][j].blocked == false)
			    cells[i][j].putPath('u');
			if(j < (SIZE-1)) {
			    if(cells[i][j+1].blocked == false)
				cells[i][j].putPath('r');
			}
		    }
		    if(j == 0) {
			if(i < (SIZE-1)) {
			    if(cells[i+1][j].blocked == false)
				cells[i][j].putPath('d');
			}
			if(cells[i][j+1].blocked == false)
			    cells[i][j].putPath('r');
		    }
		    if(j == (SIZE-1)) {
			if(i < (SIZE-1)) {
			    if(cells[i+1][j].blocked == false)
				cells[i][j].putPath('d');				
			}
			else if(i == (SIZE-1)) {
			    if(cells[i][j-1].blocked == false)
				cells[i][j].putPath('l');
			    if(cells[i-1][j].blocked == false)
				cells[i][j].putPath('u');
			}
		    }
		}
	    }
	}
	/*cells[0][0].putPath('d');
	cells[0][0].putPath('r');
	cells[0][1].putPath('l');
	cells[0][1].putPath('d');
	cells[1][1].putPath('u');
	cells[1][1].putPath('l');
	cells[1][0].putPath('r');
	cells[1][0].putPath('u');*/
    }

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
    
    public Node getOrigin() {
	return cells[0][0];
    }

    public Node accessCell(int i, int j) {
	return cells[i][j];
    }
}



class StackAITester {
    private static Board space;


    public StackAITester(String boardFileName) {
	space = new Board(boardFileName);

    }

    public static void walkBoard() throws Exception {
	Node walker = space.getOrigin();
	int x = 0, y = 0, count = 0;
	while(walker.peekPath() != '*') {
	    if(walker.destination == true) {
		System.out.println("*> THE DESTINATION WAS FOUND AT CELL (" + x +
				   ", " + y + ")" + " IN " + count + " MOVES! <*");
	        new PlayWavFile("complete.wav");
		walker = null;
		return;
	    }
	    char c = walker.getPath();
	    System.out.println("**> POPPED: " + c + " from cell (" + x + 
			       ", " + y + ")");
	    Thread.sleep(1500);
	    if(c == 'r') y++;
	    else if (c == 'd') x++;
	    else if (c == 'l') y--;
	    else if (c == 'u') x--;
	    
	    walker = space.accessCell(x,y);
	    //System.out.println("**> WALKER PATH " + walker.peekPath() + " <**");
	    count++;
	}
	System.out.println("*> THE AUTOMATON DID NOT REACH THE DESTINATION <*");
	new PlayWavFile("fail.wav");
	walker = null;
    }
}

public class StackAITesterApp {

    public static void main(String[] args) throws Exception {
	StackAITester maze = new StackAITester(args[0]);
	maze.walkBoard();
    }
}