


package com.WordleApp.sample;
import javafx.scene.paint.Color;

import java.util.InputMismatchException;

/**
 * @author vanzante
 * @version 1.0
 * @created 13-Feb-2024 4:30:19 PM
 */
public class Word {

	private final Letter[] letters;
	private final String word;
	private final int wordLength;



	public Word(String word, String[] colors){
		this.word = word;
		this.wordLength = word.length();
		if(wordLength != colors.length){
			throw new InputMismatchException("Color array must be same length as word given!");
		}
		this.letters = new Letter[wordLength];

		for (int i = 0; i < wordLength; i++) {
			letters[i] = new Letter(word.charAt(i), colors[i]);
		}
	}


	public void updateColors(String[] colors) {
		for (int i = 0; i < this.wordLength; i++) {
			letters[i].updateColor(colors[i]);
		}
	}



	@Override
	public String toString() {
		StringBuilder returnal = new StringBuilder("");
		for (Letter l : letters) {
			returnal.append(l.toString());
		}
		return returnal.toString();
	}

	public Letter[] getLetters(){
		return letters;
	}

	public String getWord() {
		return this.word;
	}

	public int getWordLength(){
		return wordLength;
	}

}