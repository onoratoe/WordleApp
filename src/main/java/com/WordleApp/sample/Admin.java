package com.WordleApp.sample;

import javafx.scene.control.Alert;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author vanzante
 * @version 1.0
 * @created 13-Feb-2024 4:30:19 PM
 */
public class Admin extends User {
	private static HashMap<Character, Integer> globalLetterStats;
	private static HashMap<String, Integer> globalWordStats;
	private static HashMap<Integer, Integer> globalVictoryStats;


	public Admin(String username){
		super(username);
	}

	@Override
	public void loadStatsFromFile() {
		System.out.println("loading global stats");
		letterGuesses = new HashMap<>();
		wordGuesses = new HashMap<>();
		victoryStats = new HashMap<>();
		for (int i = 0; i < 6; i++) {
			victoryStats.put(i + 1, 0);
		}
		try (BufferedReader in = new BufferedReader(new FileReader("saveData/stats/stats.txt"))) {
			String line;
			while ((line = in.readLine()) != null) {

				interpretStatsLine(line);

			}

		} catch (IOException e) {
			System.out.println("Warning no stats file detected: creating new file");
			try {
				new File("saveData/stats").mkdirs();
				new File("saveData/stats/stats.txt").createNewFile();
			} catch (IOException f) {
				System.out.println("failed to create a global stats file");
			}
		}
	}



	public String leastGuessed(){
		return "";
	}

	public static void uploadVocab(File file) {
		if (validVocab(file)) {
			String directory = "saveData/vocab/";
			String fileName = file.getName();

			File newVocab = new File(directory + fileName);

			try (Scanner in = new Scanner(file); PrintWriter pw = new PrintWriter(newVocab)) {
				while (in.hasNextLine()) {
					pw.println(in.nextLine());
				}
			} catch (FileNotFoundException e) {
				throw new RuntimeException();
			}
		}
	}

	public static void uploadGeneralVocab(String fileName) {
		if (fileName.equals("words.txt")){
			defaultCurrentVocab(new File(fileName), "general");
		} else {
			updateCurrentVocab(new File(fileName), "general");
		}
	}


	public static void uploadTargetVocab(String fileName) {
		if (fileName.equals("words.txt")){
			defaultCurrentVocab(new File(fileName), "target");
		} else {
			updateCurrentVocab(new File(fileName), "target");
		}
	}

	private static void defaultCurrentVocab(File file, String type) {
		String currentVocab = "saveData/CurrentVocab";
		try (Scanner in = new Scanner(new File(currentVocab))) {
			if (type.equals("general")) {
				in.nextLine();
				String save = in.nextLine();
				PrintWriter pw = new PrintWriter(currentVocab);
				pw.println("resources/data/" + file.getName());
				pw.println(save);
				pw.close();
			} else if (type.equals("target")) {
				String save = in.nextLine();
				in.nextLine();
				PrintWriter pw = new PrintWriter(currentVocab);
				pw.println(save);
				pw.println("resources/data/" + file.getName());
				pw.close();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	private static void updateCurrentVocab(File file, String type){
		String currentVocab = "saveData/CurrentVocab";
		try (Scanner in = new Scanner(new File(currentVocab))) {
			if (type.equals("general")) {
				in.nextLine();
				String save = in.nextLine();
				PrintWriter pw = new PrintWriter(currentVocab);
				pw.println("saveData/vocab/" + file.getName());
				pw.println(save);
				pw.close();
			} else if (type.equals("target")) {
				String save = in.nextLine();
				in.nextLine();
				PrintWriter pw = new PrintWriter(currentVocab);
				pw.println(save);
				pw.println("saveData/vocab/" + file.getName());
				pw.close();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static boolean validVocab(File file) {
		//TODO add alert for small .txt file
		if (file.getName().length() > 4 && file.getName().endsWith(".txt")) {
			try (Scanner in = new Scanner(file)) {
				int lines = 0;
				while (in.hasNextLine()) {
					in.nextLine();
					lines++;
				}
				if (lines >= 5) {
					return true;
				} else {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Invalid amount of words");
					alert.setHeaderText("The file selected has an invalid amount of words");
					alert.setContentText("Please select a .txt file with at least 5 words in it");

					alert.showAndWait();
					return false;
				}
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Invalid File Input");
			alert.setHeaderText("The file selected was invalid");
			alert.setContentText("Please select a .txt file for vocab files");

			alert.showAndWait();
		}
		return false;
	}

	public static File[] getVocabFiles(){
		File[] inSaveData = (new File("saveData/vocab")).listFiles();
		try {
			File[] ret = new File[inSaveData.length + 1];
			for (int i = 0; i < inSaveData.length; i++) {
				ret[i] = inSaveData[i];
			}
			ret[ret.length - 1] = new File("resources/data/words.txt");
			return ret;
		} catch (NullPointerException e) {
			return (new File("resources/data")).listFiles((dir, name) -> !name.equals("stats.txt"));
		}
	}

	public static int[][] getAllWordLengths(){
		ArrayList<int[]> wordLengths = new ArrayList<>();
		for(File file : getVocabFiles()){
			wordLengths.add(getWordLengths(file.getPath()));
		}
		return wordLengths.toArray(new int[0][0]);
	}

	public static int[] getWordLengths(String fileName){
		ArrayList<Integer> wordLengths = new ArrayList<>();
		if (fileName == null) {
			return new int[] {};
		}
		if (fileName.equals("words.txt")) {
			try(Scanner in = new Scanner(new FileReader("resources/data/" + fileName))) {
				while (in.hasNext()) {
					int len = in.next().length();
					if (!wordLengths.contains(len)) {
						wordLengths.add(len);
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println(e);
			}
		} else {
			try (Scanner in = new Scanner(new FileReader("saveData/vocab/" + fileName))){
				while (in.hasNext()) {
					int len = in.next().length();
					if (!wordLengths.contains(len)) {
						wordLengths.add(len);
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println(e);
			}
		}
		return wordLengths.stream().mapToInt(i -> i).toArray();
	}

}