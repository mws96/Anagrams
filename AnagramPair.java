/**
 * AnagramPair
 * 
 * Author: Makenzie Schwartz
 * Student ID: 28830751
 * CS311
 * Programming Assignment 1
 * 
 * AnagramPair is a simple wrapper class that contains a word and its anagram equivalence class key, which is
 * simply the same word with its letters rearranged in alphabetical order.
 * 
 */

public class AnagramPair {
	public String word;
	public String key;
	
	public AnagramPair(String w, String k) {
		word = w;
		key = k;
	}
	
	public String toString() {
		return word;
	}
}
