package com.WordleApp.sample;

import com.WordleApp.sample.Letter;
import com.WordleApp.sample.Word;

import java.io.*;
import java.util.*;


/**
 * @author vanzante
 * @version 1.0
 * @created 13-Feb-2024 4:30:19 PM
 */
public class GameInstance {

	public static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	private static int currentWordLength = 5;
	private static String currentVocabFile = "/saveData/vocab/words_alpha.txt";

	private Word[] board;
	private Letter[] keyboard;
	private boolean statistical;
	private String target;
	private HashSet<String> targetWordDict;
	private HashSet<String> wordDict;
	private int wordLength;
	private int currentGuess = 0;
	private boolean victory = false;
	private boolean gameEnded = false;
	private boolean hintUsed = false;


	public Wordle m_Wordle;
	public Controller m_Controller;
	public Word m_Word;
	public Letter m_Letter;


	public GameInstance(String targetWord, String wordDictFile, String targetDictFile, boolean statistical) {
		this.target = targetWord;
		this.wordLength = currentWordLength;
		this.statistical = statistical;

		this.wordDict = generateWordDict(wordDictFile, this.wordLength);
		this.targetWordDict = generateWordDict(targetDictFile, this.wordLength);
		this.board = new Word[6];

		this.keyboard = new Letter[26];
		for (int i = 0; i < 26; i++) {
			keyboard[i] = new Letter(alphabet[i], "gray");
		}

	}

	public GameInstance(){
		this.wordLength = currentWordLength;
		String[] dicts = loadDicts();
		this.wordDict = generateWordDict(dicts[0], this.wordLength);
		this.targetWordDict = generateWordDict(dicts[1], this.wordLength);
		this.target = generateTargetWord();
		assert target != null;
		System.out.println(this.target);
		this.statistical = true;

		this.board = new Word[6];
		this.keyboard = new Letter[26];
		for (int i = 0; i < 26; i ++) {
			keyboard[i] = new Letter(alphabet[i], "gray");
		}
	}

	public static void updateGlobalWordLength(int l){
		currentWordLength = l;
		System.out.println(currentWordLength);
	}


	public int getWordLength(){
		return wordLength;
	}
	public static void updateGlobalVocabFile(String f){
		currentVocabFile = "/data/" + f;
		System.out.println(currentVocabFile);
	}

	/**
	 * This method is used to generate a word dictionary.
	 * @param filename the file that contains the word dictionary
	 * @param wordLength the length of the words in the dictionary
	 * @return a hashset of the words in the dictionary
	 */
	private HashSet<String> generateWordDict(String filename, int wordLength) {
		HashSet<String> hold = new HashSet<>();
		try (InputStream fs = getClass().getResourceAsStream(filename)) {
			if (fs == null){
				Scanner in = new Scanner(new File(filename));
				String line;
				while (in.hasNextLine()) {
					line = in.nextLine();
					if (line.strip().length() == wordLength) {
						hold.add(line.toLowerCase().strip());
					}
				}
			} else {
				Scanner in = new Scanner(fs);
				String line;
				while (in.hasNextLine()) {
					line = in.nextLine();
					if (line.strip().length() == wordLength) {
						hold.add(line.toLowerCase().strip());
					}
				}
			}
		} catch (IOException e) {
			return generateWordDict("/data/words.txt", wordLength);
		}
		return hold;
	}

	private String[] loadDicts() {
		try (Scanner in = new Scanner(new File("saveData/CurrentVocab"))){
			String[] dicts = {in.nextLine(), in.nextLine()};
			return dicts;
		} catch (IOException e) {
			String[] defaultDicts = {"/data/words.txt", "/data/words.txt"};
			return defaultDicts;
		}
	}

	/**
	 * This method is used to generate a target word.
	 * @return the target word
	 */
	private String generateTargetWord(){
		String[] targetArr = new String[targetWordDict.size()];
		int i = 0;
		for (String word : targetWordDict) {
			targetArr[i++] = word;
		}
		boolean notFound = true;
		while (notFound) {
			Random rand = new Random();
			int n = rand.nextInt(targetWordDict.size());
			if (wordDict.contains(targetArr[n])) {
				return targetArr[n];
			}
			i++;
		}
		return null;
	}

	/**
	 * This method is used to validate a word.
	 * @param word the word that the user is guessing
	 * @return true if the word is valid, false if the word is invalid
	 */
	public boolean validate(String word) {
		word = word.toLowerCase();
		if (word.matches("\\A[a-z]+\\Z")) {
			if (word.length() == wordLength) {
				return wordDict.contains(word);
			}
		}
		return false;
	}


	/**
	 * This method is used to compare the guess to the target word.
	 * @param guess the word that the user is guessing
	 * @return an array of strings that represent the colors of the letters in the guess
	 */
	private String[] compareWord(String guess){
		guess = guess.toLowerCase();
		//check if the words are the same
		String[] colors = new String[wordLength];
		for (int i = 0; i < wordLength; i++) {
			colors[i] = "green";
		}

		if (guess.equals(target)) {
			//Possibly make it, so it returns whether the guess is correct
			return colors;
		}

		HashMap<Character, Integer> target_dict = new HashMap<>();

		//generate target dict
		for (Character a : target.toCharArray()) {
			if (target_dict.containsKey(a)) {
				target_dict.put(a, target_dict.get(a) + 1);
			} else {
				target_dict.put(a, 1);
			}
		}

		//check for green letters
		for (int i = 0; i < wordLength; i++) {
			if (guess.charAt(i) != target.charAt(i)) {
				colors[i] = "gray";
			} else {
				target_dict.put(guess.charAt(i), target_dict.get(guess.charAt(i)) - 1);
			}
		}

		//check for yellow letters
		for (int i = 0; i < wordLength; i++) {
			if (guess.charAt(i) != target.charAt(i) && target_dict.containsKey(guess.charAt(i)) && target_dict.get(guess.charAt(i)) > 0) {
				target_dict.put(guess.charAt(i), target_dict.get(guess.charAt(i)) - 1);
				colors[i] = "yellow";
			}
		}
		return colors;
	}

	/**
	 * This method is used to make a guess in the game.
	 * @param guess the word that the user is guessing
	 * @return 0 if the guess is incorrect, 1 if the guess is the 6th incorrect guess, 2 if the guess is correct
	 */
	public int guess(String guess) {
		guess = guess.toLowerCase();
		if (!validate(guess)) {
			System.out.println("conditional");
			return -1;
		}

		updateBoard(new Word(guess, compareWord(guess)));


		if (guess.equals(target)) {
			victory = true;
			gameEnded = true;
			return 2;
		} else if (currentGuess == 6) {
			gameEnded = true;
			return 1;
		}
		return 0;
	}

	/**
	 * This method is used to check and see what information the user already has, and aims to give them a useful hint.
	 * This method checks the first letter of previous guesses, and checks to see if the letter is green
	 * return 0 if the first letter is not green, 1 if the first letter is green, and 2 if the second letter is green, and so on.
	 */
	public int checkHint(){
		int count = 0;
		for (int i = 0; i < wordLength; i++){
			if (checkColumn(i)){
				count++;
			} else {
				return count;
			}
		}
		return count;
	}

	/**
	 * This method is used to check and see if a letter in a column is green.
	 * @param column the column that is being checked
	 * @return true if a letter in the column is green otherwise false
	 */
	public Boolean checkColumn(int column){
		for (Word word : board) {
			if (word != null) {
				if (word.getLetters()[column].getColor().equals("#5a8d4b")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This method is used to retrieve the board of the game.
	 * This is the array of words that the user has guessed.
	 */
	private void updateBoard(Word word){
		board[currentGuess] = word;
		currentGuess++;
	}

	/**
	 * This method is used to retrieve the board of the game.
	 * This is the array of words that the user has guessed.
	 * @return the board of the game
	 */
	@Override
	public String toString() {
		StringBuilder returnal = new StringBuilder(target);

		//Board State
		for (Word word : board) {
			returnal.append(",");
			if (word != null) {
				returnal.append(word);
			}
		}

		//Game Victory
		returnal.append(",");
		returnal.append(victory ? 1 : 0);
		returnal.append(",");
		returnal.append(currentGuess);
		returnal.append(",");
		returnal.append(currentWordLength);
		return returnal.toString();
	}

	/**
	 * This method is used to retrieve the target word of the game.
	 * This is the word that the user is trying to guess.
	 * @return the target ord of the game
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * This method is used to retrieve the board of the game.
	 * This is the array of words that the user has guessed.
	 * @return the board of the game
	 */
	public Letter[] getKeyboard() {
		return keyboard;
	}

	public int getCurrentGuess() {
		return currentGuess;
	}

	public boolean getGameEnded() {
		return gameEnded;
	}

	public Word getLastGuess() {
		if (currentGuess <= 0 || currentGuess > 6) {
			return new Word("error", new String[]{"","","","",""});
		}
		return board[currentGuess - 1];
	}
	public boolean getVictory() { return victory; }

	public boolean getStatistical() {
		return statistical;
	}

	public boolean getHintUsed() {
		return hintUsed;
	}
	public void updateHintUsed(boolean b) {
		hintUsed = b;
	}
}