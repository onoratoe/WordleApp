package com.WordleApp.sample;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * @author onoratoe
 * @version 1.0
 * @created 13-Feb-2024 4:30:19 PM
 */
public class GameController {
	@FXML
	public AnchorPane anchor;
	@FXML
	private AnchorPane gameBox;
	@FXML
	private AnchorPane homeBox;



	@FXML
	private AnchorPane endDialog;
	@FXML
	private Label endDialogHeading;
	@FXML
	private Label endDialogContent;
	@FXML
	private AnchorPane gameGrayOut;
	private histView hist;

	@FXML
	private Button logInOpenButton;
	@FXML
	private VBox selectFileBox;
	@FXML
	private Button setVocabButton;

	@FXML
	private AnchorPane adminBox;
	@FXML
	private AnchorPane logInBox;
	@FXML
	private Label logInHeading;
	@FXML
	private Button createAccountButton;
	@FXML
	private Button backButton;
	@FXML
	private Label logInResult;
	@FXML
	private TextField usernameEntry;
	@FXML
	private TextField passwordEntry;
	@FXML
	private AnchorPane gameGrayOut1;
	@FXML
	private ListView<String> topLettersList;
	@FXML
	private ListView<String> topWordsList;
	@FXML
	private Button setAdminButton;

	@FXML
	private ImageView gameExample;

	@FXML
	private VBox helpBox;
	@FXML
	private VBox hintHelpBox;
	//TODO migrate off of global variables
	@FXML
	private HBox row1;
	@FXML
	private HBox row2;
	@FXML
	private HBox row3;
	@FXML
	private HBox row4;
	@FXML
	private HBox row5;
	@FXML
	private HBox row6;
	@FXML
	private HBox keyRow1;
	@FXML
	private HBox keyRow2;
	@FXML
	private HBox keyRow3;
	@FXML
	private Button hintButton;
	@FXML
	private Button hintHelpButton;
	@FXML
	private Button uploadVocabButton;
	@FXML
	private ChoiceBox<String> generalFileSelect;
	@FXML
	private ChoiceBox<String> targetFileSelect;
	@FXML
	private ChoiceBox<Integer> wordLengthSelect;
	@FXML
	private ChoiceBox<String> adminAccountSelect;
	@FXML
	private Button applyVocab;

	@FXML
	View main;


	private HBox[] gameRowArray;
	private final String style = "-fx-border-color: darkgrey; -fx-border-width: 2px; -fx-background-color:  %s; -fx-text-fill: white; -fx-alignment: center; -fx-font-size: 20px; -fx-font-family: 'Courier New';";
	private GameInstance currentGame;
	private final Stack<Character> inputStack = new Stack<Character>();
	private ArrayList<Button> buttons;
	private String qwertyKeyboard = "qwertyuiopasdfghjkl#zxcvbnm";

	public EventHandler<KeyEvent> keyListener = event -> {
		keyInput(event.getCode().toString());
	};


	public void initialize() {
		main.showingProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				AppBar appBar = AppManager.getInstance().getAppBar();
				appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> AppManager.getInstance().getDrawer().open()));
				appBar.setTitleText("Game");
			}
		});
	}

	@FXML
	public void applyVocab(){
		if(generalFileSelect.getValue() == null || wordLengthSelect.getValue() == null){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Please select a vocab file and word length");
			alert.showAndWait();
		} else {
			GameInstance.updateGlobalVocabFile(generalFileSelect.getValue());
			GameInstance.updateGlobalWordLength(wordLengthSelect.getValue());
			restartGame();
		}
	}


	private void keyInput(String input){
        if (!currentGame.getGameEnded()) {
            switch (input) {
                case "⌫": {
                    if (!inputStack.isEmpty()) inputStack.pop();
                    backSpaceRow();
                }
                case "ENTER": {
                    if (inputStack.size() == currentGame.getTarget().length() && gameStep() != -1) {
                        inputStack.clear();
                        colorTiles();
                        if (currentGame.getHintUsed()) {
                            disableHint();
                        }
                    }
                }
                case "BACK_SPACE": {
                    if (!inputStack.isEmpty()) inputStack.pop();
                    backSpaceRow();
                }
                default: {
//System.out.println("DEFAULT");
                    if (input.length() == 1 && inputStack.size() < currentGame.getWordLength()) {
                        inputStack.add(input.charAt(0));
                        updateRow();
                    }
                }
                break;
            }
        } else {
            displayEndDialog();
        }
    }

	private void disableHint(){
		currentGame.updateHintUsed(true);
		hintButton.setVisible(false);
		hintHelpButton.setVisible(false);
	}

	private void enableHint(){
		currentGame.updateHintUsed(false);
		hintButton.setVisible(true);
		hintHelpButton.setVisible(true);
	}

	@FXML
	private void play(){
		letsPlay();
	}

	@FXML
	private void help(){
		helpBox.setVisible(true);
		homeBox.setVisible(false);
		gameBox.setVisible(false);
	}
	@FXML
	private void home(){
		goHome();
	}

	/**
	 * This method is used to give the hint to the user
	 * This method will give the first available letter that is green
	 */
	@FXML
	private void hint(){

		while (!inputStack.isEmpty()){
			inputStack.pop();
			backSpaceRow();
		}
		int letterIndex = currentGame.checkHint();
		char[] targetChars = currentGame.getTarget().toCharArray();
		for(int i = 0; i <= letterIndex; i++){
			inputStack.add(Character.toUpperCase(targetChars[i]));
			updateRow();
		}
		Button currentButton = buttons.get(qwertyKeyboard.indexOf(targetChars[letterIndex]));
		// color the letter at the index yellow on the keyboard
		currentButton.setStyle(String.format(style, "#b29f31"));
		currentGame.updateHintUsed(true);
	}
	@FXML
	private void letsPlay(){
		gameBox.setVisible(true);
		homeBox.setVisible(false);
		helpBox.setVisible(false);
		hintHelpBox.setVisible(false);
		adminBox.setVisible(false);
	}

	@FXML
	private void goHome(){
		homeBox.setVisible(true);
		helpBox.setVisible(false);
		gameBox.setVisible(false);
		hintHelpBox.setVisible(false);
		adminBox.setVisible(false);

	}

	@FXML
	private void displayHintHelp(){
		hintHelpBox.setVisible(true);
		helpBox.setVisible(false);
		gameBox.setVisible(false);
		homeBox.setVisible(false);
		adminBox.setVisible(false);
	}

	@FXML
	private void logInOpen(){
		if (loggedIn) {
			Wordle.setUser(new Admin("guest"));
			loggedIn = false;
			logInOpenButton.setText("Log in");
		} else {
			gameGrayOut1.setVisible(true);
			logInBox.setVisible(true);
		}
	}

	@FXML
	private void usernameEntryKeyPress(String input) {
		if (input.equals("TAB")) {
			passwordEntry.requestFocus();
		}
	}

	public EventHandler<KeyEvent> usernameEntryInput = event -> {
		usernameEntryKeyPress(event.getCode().toString());
	};



	boolean createAccount = false;
	boolean loggedIn = false;

	@FXML
	private void logInButton() {

		if (createAccount) {
			createAccount();
		} else {
			logIn();
		}
	}


	@FXML
	private void createAccountButton(){
		// set the create account button to invisible, and set the log in heading to create account
		createAccountButton.setVisible(false);
		logInHeading.setText("Create Account");
		backButton.setVisible(true);
		createAccount = true;
	}

	@FXML
	private void logIn(){


		//User account check
		File userFolder = new File("saveData/accounts/" + usernameEntry.getText());
		if (!userFolder.exists()) {
			System.out.println("doesn't Exist");
			usernameEntry.setText("");
			passwordEntry.setText("");
			usernameEntry.setPromptText("Unknown Account");
			return;
		}

		File passFile = new File("saveData/accounts/" + usernameEntry.getText() + "/pass");

		if (!passFile.exists()) {
			usernameEntry.setText("");
			passwordEntry.setText("");
			usernameEntry.setPromptText("Error with account");
			return;
		}
		//password check

		try {

			//todo encrypt before equals
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedHash = digest.digest(
					passwordEntry.getText().getBytes(StandardCharsets.UTF_8));


			File hold = new File("saveData/accounts/" + usernameEntry.getText() + "/pass");
			FileInputStream in = new FileInputStream(hold);

			if (!Arrays.equals(in.readAllBytes(), encodedHash)) {
				passwordEntry.setText("");
				passwordEntry.setPromptText("Incorrect Password");
				return;
			}

			in.close();
		} catch (NullPointerException e) {
			passwordEntry.setText("");
			passwordEntry.setPromptText("No password file");
			return;
		} catch (Exception e) {
			System.out.println("git gud");
		}

		//todo possibly change this from a strict admin not admin to a permissions system
		//For the sake of my sanity I'm going to pretend that all user account information is not stored on the user's computer
		//admin check
		try {

			BufferedReader reader = new BufferedReader(new FileReader("saveData/accounts/" + usernameEntry.getText() + "/config"));
			if (reader.readLine().equals("1")) {
				System.out.println("Logging in as Admin");
				Wordle.setUser(new Admin(usernameEntry.getText()));
			} else {
				System.out.println("Logging in as User");
				Wordle.setUser(new User(usernameEntry.getText()));
			}
			reader.close();
		} catch (Exception e) {
			Wordle.setUser(new User(usernameEntry.getText()));
			System.out.println("Warning: no config file detected");
			try {
				new File("saveData/accounts/" + usernameEntry.getText()).mkdirs();
				File hold = new File("saveData/accounts/" + usernameEntry.getText() + "/config");
				hold.createNewFile();

				FileWriter writer = new FileWriter(hold);
				writer.append("0");
				writer.flush();
				writer.close();
				System.out.println("Successfully created new config file");
			} catch (IOException f) {
				System.out.println("Warning: failed to create user config file");
			}
		}
		//maybe leave username there for quick sign in?
		//possibly log back in user upon restart
		usernameEntry.setText("");
		usernameEntry.setPromptText("");

		passwordEntry.setText("");
		passwordEntry.setPromptText("");

		logInOpenButton.setText("Log out");
		loggedIn = true;


		this.exitButtonL();
	}

	@FXML
	private void createAccount() {
		File userFolder = new File("saveData/accounts/" + usernameEntry.getText());
		if (userFolder.exists()) {
			System.out.println("Warning: username already in use");
			usernameEntry.setText("");
			passwordEntry.setText("");
			usernameEntry.setPromptText("Username in use");
			return;
		}

		//possibly add more password security checks like contains a number
		if (passwordEntry.getText().isEmpty()) {
			passwordEntry.setPromptText("Select password");
			return;
		}

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedHash = digest.digest(
					passwordEntry.getText().getBytes(StandardCharsets.UTF_8));
			new File("saveData/accounts/" + usernameEntry.getText()).mkdirs();
			File passFile = new File("saveData/accounts/" + usernameEntry.getText() + "/pass");
			passFile.createNewFile();
			FileOutputStream out = new FileOutputStream(passFile);
			out.write(encodedHash);
			out.flush();
			out.close();

			File hold = new File("saveData/accounts/" + usernameEntry.getText() + "/config");
			hold.createNewFile();

			FileWriter writer = new FileWriter(hold);
			writer.append("0");
			writer.flush();
			writer.close();
			System.out.println("Successfully created new account");
		} catch (Exception e) {
			System.out.println("Failed to create account");
			usernameEntry.setText("");
			passwordEntry.setText("");
			usernameEntry.setPromptText("ERROR");
			return;
		}

		Wordle.setUser(new User(usernameEntry.getText()));

		usernameEntry.setText("");
		passwordEntry.setText("");
		usernameEntry.setPromptText("");
		passwordEntry.setPromptText("");
		loggedIn = true;
		logInOpenButton.setText("Log out");
		backButtonL();
		exitButtonL();
	}


	@FXML
	private void backButtonL(){
		// undo create account
		createAccountButton.setVisible(true);
		logInHeading.setText("Log In");
		backButton.setVisible(false);
		createAccount = false;
	}
	@FXML
	private void exitButtonL(){
		logInBox.setVisible(false);
		gameGrayOut1.setVisible(false);
	}

	public void setup(GameInstance game){
		currentGame = game;
		gameRowArray = new HBox[] {row1, row2, row3, row4, row5, row6};
		int wordLength = currentGame.getTarget().length();

		// Add TextFields to the rows
		fillRow(wordLength, row1);
		fillRow(wordLength, row2);
		fillRow(wordLength, row3);
		fillRow(wordLength, row4);
		fillRow(wordLength, row5);
		fillRow(wordLength, row6);

		// fill the keyboard with buttons
		fillRow1(keyRow1);
		fillRow2(keyRow2);
		fillRow3(keyRow3);



		enableHint();
		//Debug feature

	}

	public void debugPos(MouseEvent e) {
		System.out.println(e.getX() + "," + e.getY());
	}

	private void updateRow() {
		((Label) gameRowArray[currentGame.getCurrentGuess()].getChildren().get(inputStack.size() - 1)).setText(""+inputStack.peek());
	}

	private void backSpaceRow() {
		((Label) gameRowArray[currentGame.getCurrentGuess()].getChildren().get(inputStack.size())).setText("");
	}

	private int gameStep() {
		StringBuilder word = new StringBuilder();
		for (Character character : inputStack) {
			word.append(character);
		}
		int status = currentGame.guess(word.toString());
		colorTiles();
		colorKeyboard();
		if (status >= 1) {
			if (currentGame.getStatistical()) {
				saveGame();
			}
			displayEndDialog(status);
		}
		return status;
	}

	private void displayEndDialog(int status) {
		if (hist == null) {
			hist = new histView(Wordle.getUser().getVictoryStats());
			endDialog.getChildren().add(hist);
			AnchorPane.setTopAnchor(hist, 100.0);
			AnchorPane.setLeftAnchor(hist, 36.0);
			hist.setPrefWidth(260);
		} else {
			hist.setData(Wordle.getUser().getVictoryStats());
		}


		if(status == 1){
			displayLoss();
		} else if (status == 2) {
			displayWin();
		}
		inputStack.clear();
		disableHint();
	}

	private void displayEndDialog() {
		displayEndDialog(currentGame.getVictory() ? 2 : 1);
	}

	private void saveGame() {
		//TODO add boolean return
		Wordle.statDump(currentGame);
	}



	private void colorTiles() {
		Letter[] letters = currentGame.getLastGuess().getLetters();
		for (int i = 0; i < letters.length; i++) {
			gameRowArray[currentGame.getCurrentGuess() - 1].getChildren().get(i).setStyle(String.format(style, letters[i].getColor()));
		}
	}

	private void colorKeyboard(){
		Letter[] letters = currentGame.getLastGuess().getLetters();
		for (Letter letter : letters) {
			Button currentButton = buttons.get(qwertyKeyboard.indexOf(letter.getLetter()));
			if (currentButton.getStyle().equals(String.format(style, "#b29f31")) && !letter.getColor().equals("#3a3a3c")) {
				currentButton.setStyle(String.format(style, letter.getColor()));
			} else if (currentButton.getStyle().equals(String.format(style, "#3a3a3c")) && !letter.getColor().equals("#b29f31")) {
				currentButton.setStyle(String.format(style, letter.getColor()));
			} else if (currentButton.getStyle().equals(String.format(style, "black"))) {
				currentButton.setStyle(String.format(style, letter.getColor()));
			}
		}
	}

	/**
	 * This method is used to fill the rows with text fields
	 * @param wordLength the length of the word
	 * @param row the row to fill
	 */
	private void fillRow(int wordLength, HBox row){
		//fill rows with text fields with the amount of word length
		Queue<Label> textFields = new LinkedList<>();
		for(int i = 0; i < wordLength; i++){
			Label textField = new Label();
			textField.setPrefWidth(50);
			textField.setPrefHeight(60);
			//textField.setEditable(false);
			textField.setFocusTraversable(false);
			textField.setAlignment(javafx.geometry.Pos.CENTER);

			// set the styling for the text field
			textField.setStyle(String.format(style, "black"));
			if (i !=0 ){
				row.setSpacing(10);
			}
			textFields.add(textField);
			row.getChildren().add(textField);
		}
	}
	private void fillRow1(HBox row){
		char[] qwertyRow1 = {'Q','W','E','R','T','Y','U','I','O','P'};
		buttons = new ArrayList<>();
		for(int i = 0; i < 10; i++){
			Button button = new Button();
			button.setText(String.valueOf(qwertyRow1[i]));
			button.setPrefWidth(50);
			button.setPrefHeight(50);
			button.setStyle(String.format(style, "black"));
			button.setFocusTraversable(false);
			button.setOnAction(event -> keyInput(((Button)event.getSource()).getText()));
			if(i != 0){
				row.setSpacing(5);
			}
			buttons.add(button);
			row.getChildren().add(button);
		}
	}
	private void fillRow2(HBox row){
		char[] qwertyRow2 = {'A','S','D','F','G','H','J','K','L'};
		for(int i = 0; i < 9; i++){
			Button button = new Button();
			button.setText(String.valueOf(qwertyRow2[i]));
			button.setPrefWidth(50);
			button.setPrefHeight(50);
			button.setStyle(String.format("black"));
			button.setFocusTraversable(false);
			button.setOnAction(event -> keyInput(((Button)event.getSource()).getText()));
			if(i != 0){
				row.setSpacing(5);
			}
			buttons.add(button);
			row.getChildren().add(button);
		}
	}
	private void fillRow3(HBox row){
		char[] qwertyRow3 = {'Z','X','C','V','B','N','M'};
		Button enterButton = new Button();
		enterButton.setText("Enter");
		enterButton.setPrefWidth(95);
		enterButton.setPrefHeight(50);
		enterButton.setStyle(String.format("black"));
		enterButton.setFocusTraversable(false);
		enterButton.setOnAction(event -> keyInput("ENTER"));
		buttons.add(enterButton);
		row.getChildren().add(enterButton);
		for(int i = 0; i < 7; i++){
			Button button = new Button();
			button.setText(String.valueOf(qwertyRow3[i]));
			button.setPrefWidth(50);
			button.setPrefHeight(50);
			button.setStyle(String.format(style, "black"));
			button.setFocusTraversable(false);
			button.setOnAction(event -> keyInput(((Button)event.getSource()).getText()));
			buttons.add(button);
			row.getChildren().add(button);
			row.setSpacing(5);
		}
		Button backSpaceButton = new Button();
		backSpaceButton.setText("⌫");
		backSpaceButton.setPrefWidth(60);
		backSpaceButton.setPrefHeight(50);
		backSpaceButton.setStyle(String.format(style, "black"));
		backSpaceButton.setFocusTraversable(false);
		backSpaceButton.setOnAction(event -> keyInput("BACK_SPACE"));
		row.setSpacing(5);
		buttons.add(backSpaceButton);
		row.getChildren().add(backSpaceButton);
	}

	public void displayWin(){
		//alert.setTitle("Winner Winner");
		endDialogHeading.setText("Congrats You Won!");
		endDialogContent.setText("What do you want to do next with your life.");


		endDialog.setVisible(true);
		gameGrayOut.setVisible(true);
	}

	public void displayLoss(){

		endDialogHeading.setText("You lost :(");
		endDialogContent.setText("The word was: " + currentGame.getTarget());
		endDialog.setVisible(true);
		gameGrayOut.setVisible(true);
	}

	private void closeDialog() {
		gameGrayOut.setVisible(false);
		endDialog.setVisible(false);
	}

	public void reviewButtonHit() {
		//printReviewData();
		closeDialog();
	}

	public void newGameButtonHit() {
		restartGame();
		closeDialog();

	}

	public void exitButtonHit() {
		System.exit(1);
	}



	public void restartGame() {
		//TODO exception handling for failed generation
		currentGame = new GameInstance();

		for (HBox row : gameRowArray) {
			row.getChildren().clear();
		}

		keyRow1.getChildren().clear();
		keyRow2.getChildren().clear();
		keyRow3.getChildren().clear();

		setup(currentGame);
		enableHint();
	}

	private void setFileSelects() {

		generalFileSelect.setItems(FXCollections.observableList((Arrays.stream(Admin.getVocabFiles()).map(File::getName)).toList()));
		generalFileSelect.getSelectionModel()

				.selectedItemProperty()
				.addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
							wordLengthSelect.setItems(FXCollections.observableList(Arrays.stream(Admin.getWordLengths(newValue)).boxed().toList()));
							generalSelect();
						}
				);
		targetFileSelect.setItems(FXCollections.observableList(Arrays.stream(Admin.getVocabFiles()).map(File::getName).toList()));
		targetFileSelect.getSelectionModel()
				.selectedItemProperty()
				.addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
							wordLengthSelect.setItems(FXCollections.observableList(Arrays.stream(Admin.getWordLengths(newValue)).boxed().toList()));
							targetSelect();
						}
				);
	}

	public void adminMenuButton(){
		//TODO change method to have logic in admin class, and only manipulate gui in controller.

		homeBox.setVisible(false);
		adminBox.setVisible(true);


		if (!Wordle.getUser().getClass().equals(Admin.class)) {
			setVocabButton.setVisible(false);
			selectFileBox.setVisible(false);
		} else {
			setFileSelects();
			setVocabButton.setVisible(true);
			selectFileBox.setVisible(true);
			try {
				File usersFolder = new File("saveData/accounts");
				File[] hold = usersFolder.listFiles();
				adminAccountSelect.getItems().clear();
				for (File i : hold) {
					adminAccountSelect.getItems().add(i.getName());
				}
			} catch (Exception e) {
				System.out.println("Error loading accounts");
			}
		}





		topLettersList.getItems().clear();
		topWordsList.getItems().clear();


		HashMap<Character, Integer> letterStats = Wordle.getUser().getLetterStats();
		ArrayList<Integer> values = new ArrayList<>();

		for (Character key : letterStats.keySet()) {
			String hold = String.format("%c: %d", key, letterStats.get(key));
			int val = letterStats.get(key);
			boolean sorted = false;

			for (int i = 0; i < values.size(); i++) {
				if (val > values.get(i)) {
					topLettersList.getItems().add(i, hold);
					values.add(i, val);
					sorted = true;
					break;
				}
			}
			if (!sorted) {
				values.add(val);
				topLettersList.getItems().add(hold);
			}
		}

		values.clear();
		HashMap<String, Integer> wordStats = Wordle.getUser().getWordStats();
		for (String key : wordStats.keySet()) {
			String hold = String.format("%s: %d", key, wordStats.get(key));
			int val = wordStats.get(key);
			boolean sorted = false;

			for (int i = 0; i < values.size(); i++) {
				if (val > values.get(i)) {
					topWordsList.getItems().add(i, hold);
					values.add(i, val);
					sorted = true;
					break;
				}
			}
			if (!sorted) {
				values.add(val);
				topWordsList.getItems().add(hold);
			}
		}


	}


	public void runFromFile(){

	}

	@FXML
	public void setVocabFileButton(){
		FileChooser fc = new FileChooser();
		fc.setTitle("Select File");
		fc.setInitialDirectory(new File("C: //"));
		File file = fc.showOpenDialog(new Popup());
		if (file != null){
			Admin.uploadVocab(file);
		}
		setFileSelects();
	}
	@FXML
	public void generalSelect() {
		if (generalFileSelect.getValue() != null) {
			Admin.uploadGeneralVocab(generalFileSelect.getValue());
		}
	}

	@FXML
	public void targetSelect() {
		if (targetFileSelect.getValue() != null) {
			Admin.uploadTargetVocab(targetFileSelect.getValue());
		}
	}
	public void addListeners() {
		endDialog.setOnMouseClicked(this::debugPos);
		gameBox.widthProperty().addListener((ChangeListener) this::scaleEndDialogBoxWidth);
		gameBox.heightProperty().addListener((ChangeListener) this::scaleEndDialogBoxHeight);

		adminAccountSelect.getSelectionModel().selectedIndexProperty().addListener(this::setSetAdminButton);
	}

	private void scaleEndDialogBoxWidth(ObservableValue o, Object oldVal, Object newVal) {
		AnchorPane.setLeftAnchor(endDialog, AnchorPane.getLeftAnchor(endDialog) + ((double)newVal - (double)oldVal)/2);
		AnchorPane.setRightAnchor(endDialog, AnchorPane.getRightAnchor(endDialog) + ((double)newVal - (double)oldVal)/2);
		AnchorPane.setLeftAnchor(logInBox, AnchorPane.getLeftAnchor(logInBox) + ((double)newVal - (double)oldVal)/2);
		AnchorPane.setRightAnchor(logInBox, AnchorPane.getRightAnchor(logInBox) + ((double)newVal - (double)oldVal)/2);
	}

	private void scaleEndDialogBoxHeight(ObservableValue o, Object oldVal, Object newVal) {
		AnchorPane.setTopAnchor(endDialog, AnchorPane.getTopAnchor(endDialog) + ((double)newVal - (double)oldVal)/2);
		AnchorPane.setBottomAnchor(endDialog, AnchorPane.getBottomAnchor(endDialog) + ((double)newVal - (double)oldVal)/2);

	}
	private String setAdminSelectedUser;

	private void setSetAdminButton(ObservableValue o, Number oldVal, Number newVal) {
		setAdminSelectedUser = adminAccountSelect.getItems().get((Integer) newVal);
		int usertype;
		try (BufferedReader in = new BufferedReader(new FileReader("saveData/accounts/" + setAdminSelectedUser + "/config"))) {
			if (in.readLine().equals("1")) {
				usertype = 1;
			} else {
				usertype = 0;
			}
		} catch (Exception e) {
			System.out.println("Warning: no config file detected");
			usertype = 0;
			try {
				new File("saveData/accounts/" + setAdminSelectedUser).mkdirs();
				File hold = new File("saveData/accounts/" + setAdminSelectedUser + "/config");
				hold.createNewFile();

				FileWriter writer = new FileWriter(hold);
				writer.append("0");
				writer.flush();
				writer.close();
				System.out.println("Successfully created new config file");
			} catch (IOException f) {
				System.out.println("Warning: failed to create user config file");
			}
		}
		setAdminButton.setDisable(false);
		if (usertype == 1) {
			setAdminButton.setText("Set to User");
		} else {
			setAdminButton.setText("Set to Admin");
		}
	}

	@FXML
	private void setAdminButtonPressed() {

		String usertype;
		try (BufferedReader in = new BufferedReader(new FileReader("saveData/accounts/" + setAdminSelectedUser + "/config"))) {
			if (in.readLine().equals("1")) {
				usertype = "0";
			} else {
				usertype = "1";
			}
		} catch (Exception e) {
			System.out.println("Warning: no config file detected");
			usertype = "1";
		}
		try {
			new File("saveData/accounts/" + setAdminSelectedUser).mkdirs();
			File hold = new File("saveData/accounts/" + setAdminSelectedUser + "/config");
			hold.createNewFile();

			FileWriter writer = new FileWriter(hold);
			writer.append(usertype);
			writer.flush();
			writer.close();
			System.out.println("Successfully updated");
		} catch (IOException f) {
			System.out.println("Warning: failed to updated config file");
		}
		if (usertype.equals("1")) {
			setAdminButton.setText("Set to User");
		} else {
			setAdminButton.setText("Set to Admin");
		}

	}

}