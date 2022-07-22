import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MemorySimulation {
	
	private int[] memory; //Array for the physical memory in the simulation
	private static ArrayList<Integer> utilization; //to keep track of memory utilization
	private static ArrayList<Integer> numHolesExamined; //to keep track of the number of holes
	private Random rng = new Random(); //RNG
	private static int numBlocks; //Number of blocks allocated

	public static void main(String[] args) throws IOException {
		FileWriter fwriter = new FileWriter("simulation.txt");
		MemorySimulation aSimulation = new MemorySimulation();
		aSimulation.run();
		
		
		
		for (int i = 0; i < utilization.size(); i ++) {
			fwriter.write(utilization.get(i));
		}
		for (int i = 0; i < numHolesExamined.size(); i ++) {
			fwriter.write(utilization.get(i));
		}

	}
	
	public MemorySimulation() {
		super();
		memory = null;
		utilization = new ArrayList<Integer>();
		numHolesExamined = new ArrayList<Integer>();
		numBlocks = 0;
	}
	
	private void run() {
		Scanner scnr = new Scanner(System.in);
		System.out.println("Which block should be examined: ");
		int block = 0;
		block = scnr.nextInt();
		initSimulation(); //initialize simulation values
		System.out.println("Number of blocks in memory: " + numBlocks);
		
		
		
		freeBlock(block);
		System.out.println("Number of blocks in memory: " + numBlocks);
		printMemory();
		scnr.close();
	}
	
	private void initSimulation() {
		Scanner scnr = new Scanner(System.in);
		int n = 0;
		
		while (n < 1) {
			System.out.println("Enter physical memory size, where n > 0:  ");
			n = scnr.nextInt();
		}
		
		memory = new int[n];
		memory[0] = -n;
		printMemory();
		System.out.println("Enter the average request size, d: ");
		int d = scnr.nextInt();
		System.out.println("Enter the request size standard dev, v: ");
		int v = scnr.nextInt();
		
		//Now, we fill the memory randomly until it is about half full
		float util = 0.0f;
		while(util < 0.25) {
			int s = gaussian(d, v); 
			boolean filled = false;
			while (!filled) {
				int loc = uniform(n);
				filled = insertRequest(loc, s);
				numBlocks++;
			}
			util += (float) s / memory.length;
		}
		printMemory();
		
		scnr.close();
		
	}
	
	//Method for inserting requests to block of memory
	private boolean insertRequest(int loc, int size) {
		boolean filled = false;
		
		int index = 0;
		while (index < memory.length && !filled) {
			if (memory[index] < 0 && (Math.abs(memory[index]) + index > loc + size)) {
				int next = Math.abs(memory[index]) + index;
				memory[index] = index - loc;
				memory[loc] = size;
				memory[loc + size] = (loc + size) - next;
				filled = true;
			}
			else {
				index = Math.abs(memory[index]) + index;
			}
		}
		return filled;
	}
	
	private boolean freeBlock(int blockIndex) {
		boolean freed = false;
		int prev = -1;
		int cur = 0;
		int index = 0;
		do {
			if (index == blockIndex) {
				if (prev == -1) {
					if (memory[memory[cur]] < 0) { //Coalesce with hole after this one
						memory[cur] = memory[memory[cur]] - memory[cur];
						numBlocks--;
					}
					
				}
				else {
					if (memory[prev] < 0) { //Coalesce with prev hole
						memory[prev] = memory[prev] - memory[cur];
						if (memory[cur + memory[cur]] < 0) { 
							memory[prev] = memory[prev] + memory[cur + memory[cur]];
							numBlocks--;
						}
					}
					//Coalesce with next hole
					if (memory[cur + memory[cur]] < 0) { //Coalesce with next hole
						memory[cur] = -memory[cur] - memory[cur + memory[cur]];
						numBlocks--;
						
						
					}
					
				}
				freed = true;
			}
			else {
				prev = cur;
				do {
					cur = cur + Math.abs(memory[cur]);
				}while (memory[cur] < 0); 
				index++;
			}
		} while(!freed && cur < memory.length);
		return freed;
	}
	
	private int gaussian(int d, int v) {
		int val = 0;
		
		val = (int) (rng.nextGaussian() * v + d);
		return val;
		
	}
	
	private int uniform(int n) {
		int val = 0;
		
		val = (int) (rng.nextFloat() * n);
		return val;
	}
	
	private void printMemory() {
		int ctr = 0;
		int block = 1;
		
		while (ctr < memory.length) {
			if(memory[ctr] > 0) {
				for (int i = 0; i < memory[ctr]; i++) {
					System.out.print(block);
				}
				block++;
			}
			else {
				for( int i = 0; i < Math.abs(memory[ctr]); i++) {
					System.out.print("-");
				}
			}
			ctr = ctr + Math.abs(memory[ctr]);
		}
		System.out.println();
	}
	
	public int bestFit (int[] memory, int requestSize, int numBlocks) {
		int placeHolder = 0;
		int gapSize = 0;
		
		for (int i = 0; i < memory.length; i++) {
			if (requestSize < gapSize) {
				if (placeHolder > gapSize - requestSize) {
					placeHolder = gapSize - requestSize;
				}
			}
		}
		memory[placeHolder] = requestSize;
			
		return numBlocks;
		
	}
	
	public int worstFit (int[] memory, int requestSize, int numBlocks) {
		int placeHolder = 0;
		int gapSize = 0;
		
		for (int i = 0; i < memory.length; i++) {
			if (requestSize < gapSize) {
				if (placeHolder < gapSize - requestSize) {
					placeHolder = gapSize - requestSize;
				}
			}
		}
		memory[placeHolder] = requestSize;
			
		return numBlocks;
		
	}

}
