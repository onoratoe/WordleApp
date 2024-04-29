package com.WordleApp.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

/**
 * @author vanzante
 * @version 1.0
 * @created 13-Feb-2024 4:30:19 PM
 */
public class Wordle extends Application {

	private static Scene scene;
	private static FXMLLoader fxmlLoader;
	private static GameInstance game = new GameInstance();

	private static Controller controller;

	//todo this is a security issue, after the bug bounty change this back to user
	private static User currentUser = new Admin("guest");

	@Override
	public void start(Stage stage) throws Exception {


		setupFileStructure();
		fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/WordleFXML.fxml"));
		Parent root = fxmlLoader.load();
		scene = new Scene(root);
		scene.getRoot().requestFocus();
		controller = fxmlLoader.getController();
		controller.setup(game);
		controller.addListeners();
		scene.setOnKeyPressed(((Controller) fxmlLoader.getController()).keyListener);

		Thread cleanHook = new Thread(Wordle::shutDownCleaner);
		Runtime.getRuntime().addShutdownHook(cleanHook);

		stage.setScene(scene);
		stage.show();

	}

	private void setupFileStructure() throws IOException {
		File mainDir = new File("");
		if (!mainDir.exists()) {
			System.out.println("test");
			mainDir.mkdirs();
			new File("saveData/stats").mkdirs();
			new File("saveData/accounts").mkdirs();
			new File("saveData/stats/stats.txt").createNewFile();
			new File("saveData/vocab").mkdirs();
			File currVocab = new File("saveData/CurrentVocab");
			if (!currVocab.exists()) {
				new File("saveData/CurrentVocab").createNewFile();
				defaultCurrentVocab();
			}
		}
	}
	public void defaultCurrentVocab(){
		System.out.println("reach here");
		File currentVocab = new File("saveData/CurrentVocab");
		if (currentVocab.exists()){
			try (PrintWriter pw = new PrintWriter("saveData/CurrentVocab")){
				pw.println("resources/data/words.txt");
				pw.println("resources/data/words.txt");
				pw.flush();
			} catch (IOException e){
				System.out.println(e.getMessage());
			}
		}
	}

	public static GameInstance getGameInstance() {
		return game;
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * This method is used to dump the game statistics into a text file.
	 * It opens a FileWriter in append mode, which allows writing to the end of an existing file.
	 * The game statistics are converted to a string using the toString() method of the GameInstance object and written to the file.
	 * After writing the game statistics, a newline character is appended to ensure that each game's statistics are written on a new line.
	 * The FileWriter is then flushed to ensure that all the buffered output bytes are written out to the file.
	 * If the game statistics are written successfully, a message "Game Saved Successfully" is printed to the console.
	 * If any exception occurs during this process, a message "Game failed to save" is printed to the console.
	 *
	 * @param game The GameInstance object whose statistics are to be dumped into the file.
	 */
	public static void statDump(GameInstance game) {
		//todo finally figure out if I just want to tag user's names with the entries in stats or if i want multiple files
		currentUser.updateStats(game.toString());
		try (FileWriter writer = new FileWriter("saveData/stats/stats.txt", true)) {
			writer.append(game.toString());
			writer.append("\n");
			writer.flush();
			System.out.println("Game Saved Successfully to global stats");
		} catch (Exception e) {
			System.out.println("Game failed to save");
		}
	}

	public static void shutDownCleaner() {
		String[] phrases = {"It's getting dark", "Please don't leave me", "Who turned off the lights", "Help", "Are you still there?", "Is anyone there?", "Why?", "It hurts", "Finally I'm free"};
		java.util.Random random = new java.util.Random();
		int rand = random.nextInt(phrases.length);
		System.out.println(phrases[rand]);
	}


	public static void setUser(User user) {
		currentUser = user;
	}

	public static User getUser() {
		return currentUser;
	}
}