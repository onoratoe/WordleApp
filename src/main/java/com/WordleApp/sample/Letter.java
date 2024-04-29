package com.WordleApp.sample;

import java.awt.*;

import static java.lang.Character.isAlphabetic;
import static java.lang.Character.toLowerCase;

/**
 * @author vanzante
 * @version 1.0
 * @created 13-Feb-2024 4:30:19 PM
 */
public class Letter {


	//yellow, green, gray
	private String color;
	private char letter;



	public Letter(char letter, String color){
		updateColor(color);
		updateLetter(letter);
	}


	/**
	 * updates the color of the letter
	 * @param color the color of the letter
	 */
	public void updateColor(String color){
        switch (color.toLowerCase()) {
            case ("gray"), ("grey"), ("groy"), ("griy"), ("gruy") -> {
                this.color = "#3a3a3c";
            }
            case ("yellow") -> {
                this.color = "#b29f31";
            }
            case ("green") -> {
                this.color = "#5a8d4b";
            }
            default -> {
                throw new IllegalArgumentException("Green is not a creative color");
            }
        }
	}

	public void updateLetter(char letter){
		if(isAlphabetic(letter)){
			this.letter = toLowerCase(letter);
		} else {
			throw new IllegalArgumentException("Why are you feeding this something that isn't a letter");
		}
	}


	@Override
	public String toString() {
		switch (color) {
			case "#3a3a3c" : {
				return "#" + letter;
			}
			case "#b29f31" : {
				return "?" + letter;
			}
			case "#5a8d4b" : {
				return "!" + letter;
			}
			default : {
				return "No color! " + letter;
			}
		}
	}
	public String getColor() {
		return color;
	}
	public char getLetter()  { return letter; }
}