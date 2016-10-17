/**
 * Anagrams
 * 
 * Author: Makenzie Schwartz
 * Student ID: 28830751
 * CS311
 * Programming Assignment 1
 * 
 * Anagrams uses a combination of Counting Sort and Radix Sort to sort an input dictionary into a list of anagram equivalence
 * classes, with the output file containing all the words in the same anagram equivalence class on one line, separated by spaces.
 * 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class Anagrams {
	
	// Maximum number of words that the algorithm will consider from the input file
	private static final int MAX_WORDS = 50000;

	// --------------------------------------
	// args[0] is the path to the input file
	// args[1] is the path to the output file
	// --------------------------------------
	public static void main(String[] args) {
		System.out.println("Now parsing " + args[0] + ".");
		
		try {
			parseAnagrams(args[0], args[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Finished parsing " + args[0] + ".");
	}
	
	// --------------------------------------
	// parseAnagrams takes an input file in the form of a list of words with one word on each line.
	// It then reads each word into an array and sorts the array by anagram equivalence classes.
	// The words are stored in AnagramPair objects, which is simply a wrapper class that contains the word and its key,
	// which is simply an anagram of the word with the letters in alphabetical order.
	// The letters of each word are sorted using Counting Sort, and the words are sorted by their keys using Radix Sort.
	// After the list of words have been sorted, they are written out to a file with all words of the same anagram
	// equivalence class on the same line.
	//
	// fileName is the path to the input file
	// outputName is the path to the output file
	// --------------------------------------
	public static void parseAnagrams(String fileName, String outputName) throws IOException {
		// Set up the Scanner to read in the input file
		File fin = new File(fileName);
		Scanner input = new Scanner(fin);
		
		// Set up for writing to the output file
		File fout = new File(outputName);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		while (input.hasNextLine()) {
			
			// Initialize pairs[], which will store the AnagramPair for each word read in, and
			// count, which keeps track of how many words we've read in
			AnagramPair[] pairs = new AnagramPair[MAX_WORDS];
			int count = 0;
		
			// Read in each word as long as there is another word to read and we don't exceed MAX_WORDS
			while(input.hasNextLine() && count < MAX_WORDS) {
				String word = input.nextLine().toLowerCase();
				// Compute the key, which is simply a string with the same letters as word, but in alphabetical order
				String key = countingSortString(word);
				// Pad the key to the right with underscores, as Radix Sort requires all the strings to be of the same length
				key = padRight(key, 20);
				// Update pairs[count] to point to the new AnagramPair and increment count
				pairs[count] = new AnagramPair(word, key);
				count++;
			}

			// Sort the array of AnagramPair objects by their keys, so that words in the same anagram equivalence class will
			// all be in consecutive order. The overall order of equivalence classes is unimportant
			radixSortStrings(pairs, 20, count);

			// Write each word to the output file, only adding a newline if the next word is in a different equivalence class
			for (int i = 0; i < count - 1; i++) {
				bw.write(pairs[i] + " ");
				if (!pairs[i].key.equals(pairs[i + 1].key))
					bw.write("\n");
			}

			// Need to write the last word as its an edge case
			bw.write(pairs[count - 1].toString());
		
		}
		
		input.close();
		bw.close();
	}
	
	// --------------------------------------
	// countingSortString performs a Counting Sort over word, using the values a = 0, b = 1, c = 2...z = 25
	//
	// word is the string which we will sort the letters of in alphabetical order
	// --------------------------------------
	public static String countingSortString(String word) {
		// Initialize an array to keep track of how many of each letter we see
		int[] counters = new int[26];
		// Instead of producing a number, we will return a string of the letters in sorted order
		String sorted = "";
		
		// Increment the counter for each letter each time that letter appears in the word
		for (int i = 0; i < word.length(); i++) {
			// We subtract by 97 as a = 97 in ASCII, so this will make a = 0, b = 1, etc.
			counters[(int)(word.charAt(i) - 97)]++;
		}
		
		// Iterate through the alphabet and add the appropriate amount of each letter
		for (int j = 0; j < 26; j++) {
			while(counters[j] > 0) {
				// Again, we add 97 to get the appropriate ASCII value of each letter
				sorted += (char)(j + 97);
				counters[j]--;
			}
		}
		
		return sorted;
	}
	
	// --------------------------------------
	// radixSortStrings sorts an array of AnagramPairs by their keys from 0 to length, considering the first d characters in each key
	//
	// a is the array of AnagramPairs that we will sort
	// d is the number of characters we will consider when sorting. Each key must be at least d in length
	// length is the the number of AnagramPairs in a that we will sort, i.e. we will sort from a[0] to a[length-1]
	// --------------------------------------
	public static void radixSortStrings(AnagramPair[] a, int d, int length) {
		// Initialize an second, temporary array for the radix sort 
		AnagramPair[] b = new AnagramPair[length];
		
		// Sort from the last letter of the word to the first
		for (int i = d-1; i >= 0; i--) {
			// Counts will store the frequency of each letter
			// We use 29 as we are using the '_' (underscore) as padding, and so we have a few extra characters besides the alphabet
			int[] counts = new int[29];
			// Calculate the frequencies of the letters
			for (int j = 0; j < length; j++)
				// We subtract by 95 here because we are using the '_' (underscore) as padding, which has ASCII value of 95
				counts[(a[j].key.charAt(i) + 1) - 95]++;
			// Determine the correct places for each word by considering how many words come before
			for (int k = 0; k < 27; k++)
				counts[k+1] += counts[k];
			// Move the data to the new places in the temporary array
			for (int m = 0; m < length; m++)
				b[counts[(a[m].key.charAt(i)) - 95]++] = a[m];
			// Copy the temporary array back to the original array
			for (int n = 0; n < length; n++)
				a[n] = b[n];
		}
	}

	// --------------------------------------
	// padRight is a helper function used to pad the keys to the right with underscores
	//
	// s is the string to pad
	// n is the number of underscores to add
	// --------------------------------------
	public static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s).replace(' ', '_');  
	}
	
}
