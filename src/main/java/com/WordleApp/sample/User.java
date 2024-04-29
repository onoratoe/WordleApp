package com.WordleApp.sample;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author vanzante
 * @version 1.0
 * @created 13-Feb-2024 4:30:19 PM
 */
public class User {

	//todo i really should do the whole average guesses thing
	private double avgGuesses;
	private String username;
	protected HashMap<String, Integer> wordGuesses;
	protected HashMap<Character, Integer> letterGuesses;
	protected HashMap<Integer, Integer> victoryStats;


	public User(String username){
		this.username = username;

		this.loadStatsFromFile();
		//load stats.txt from the file
	}

	public User() {
		this("guest");
	}

	/**
	 * 
	 *
	 */
	public void loadStatsFromFile() {
		System.out.println("loading " +username+" stats");
		letterGuesses = new HashMap<>();
		wordGuesses = new HashMap<>();
		victoryStats = new HashMap<>();
		for (int i = 0; i < 6; i++) {
			victoryStats.put(i + 1, 0);
		}
		try (BufferedReader in = new BufferedReader(new FileReader("saveData/accounts/"+username+"/stats.txt"))) {
			String line;
			while ((line = in.readLine()) != null) {
				interpretStatsLine(line);
			}

		} catch (FileNotFoundException e) {
			System.out.println("Warning no stats file detected: creating new file");
			try {
				new File("saveData/accounts/"+username).mkdirs();
				new File("saveData/accounts/"+username+"/stats.txt").createNewFile();
			} catch (IOException f) {
				System.out.println("failed to create a stats file for user");
			}
		} catch (IOException e) {
			//TODO: throw exception better?!
			System.out.println("i don't know what to do here");
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param gameStats
	 */
	private void updateStatsFile(String gameStats){
		//assign stats.txt attributes to correct portion of gameStats
	}

	protected void interpretStatsLine(String line) {
		//TODO: get rid of magic numbers!!!!!!!!
		//todo possibly restructure stat file so that the metadata is at the front then the games can functionally be infinitely long in stats
		String[] lineArray = line.split(",");
		for (int i = 1; i < 7; i++) {
			if (lineArray[i].length() == 0) {
				break;
			}
			StringBuilder word = new StringBuilder();
			for (int t = 1; t < lineArray[i].length(); t += 2) {
				word.append(lineArray[i].charAt(t));
				letterGuesses.compute(lineArray[i].charAt(t), (k, v) -> (v == null) ? 1 : v+1);
			}
			wordGuesses.compute(word.toString(), (k, v) -> (v == null) ? 1 : v + 1);

		}
		victoryStats.compute(lineArray[8].charAt(0) - 48, (k, v) -> (lineArray[7].charAt(0) - 48 == 0) ? v : v + 1);
	}

	/**
	 * 
	 * @param gameStats
	 */
	public void updateStats(String gameStats){
		interpretStatsLine(gameStats);
		try (FileWriter writer = new FileWriter("saveData/accounts/"+username+"/stats.txt", true)) {
			writer.append(gameStats);
			writer.append("\n");
			writer.flush();
			System.out.println("Game Saved Successfully to user stats");
		} catch (Exception e) {
			System.out.println("Game failed to save");
		}
	}

	public HashMap<Integer, Integer> getVictoryStats() {
		return victoryStats;
	}

	public HashMap<Character, Integer> getLetterStats() {
		return letterGuesses;
	}

	public HashMap<String, Integer> getWordStats() {
		return wordGuesses;
	}


}