package com.WordleApp.sample;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.*;

/**
 * @author onoratoe
 * @version 1.0
 * @created 13-Feb-2024 4:30:19 PM
 */
public class GameController {


	@FXML
	private AnchorPane endDialog;
	@FXML
	private Label endDialogHeading;
	@FXML
	private Label endDialogContent;
	@FXML
	private AnchorPane gameGrayOut;
	@FXML
	private VBox gameBox;
	@FXML
	private histView hist;

	@FXML
	private VBox hintHelpBox;
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
	View main;


	private HBox[] gameRowArray;
	private final String style = "-fx-border-color: darkgrey; -fx-border-width: 2px; -fx-background-color:  %s; -fx-text-fill: white; -fx-alignment: center; -fx-font-size: 20px; -fx-font-family: 'Courier New';";
	private GameInstance currentGame;
	private final Stack<Character> inputStack = new Stack<Character>();
	private ArrayList<Button> buttons;
	private final String qwertyKeyboard = "qwertyuiopasdfghjkl#zxcvbnm";


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
	public void goHome(){
		AppBar appBar = AppManager.getInstance().getAppBar();
		appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> AppManager.getInstance().getDrawer().open()));
		appBar.setTitleText("WordleApp");
	}

	private void keyInput(String input){
        if (!currentGame.getGameEnded()) {
            switch (input) {
                case "⌫": {
                    if (!inputStack.isEmpty()) inputStack.pop();
                    backSpaceRow();
                }
				break;
                case "ENTER": {
                    if (inputStack.size() == currentGame.getTarget().length() && gameStep() != -1) {
                        inputStack.clear();
                        colorTiles();
                        if (currentGame.getHintUsed()) {
                            disableHint();
                        }
                    }
                }
				break;
                case "BACK_SPACE": {
                    if (!inputStack.isEmpty()) inputStack.pop();
                    backSpaceRow();
                }
                default: {
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
		hintHelpBox.setVisible(false);
	}
	@FXML
	private void displayHintHelp(){
		hintHelpBox.setVisible(true);
	}
	public void setup(GameInstance game){
		hintButton.setStyle(String.format(style, "black"));
		hintHelpButton.setStyle(String.format(style, "black"));
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

	private void fillRow3(HBox row){
		char[] qwertyRow3 = {'Z','X','C','V','B','N','M'};
		Button enterButton = new Button();
		enterButton.setText("Enter");
		enterButton.setPrefWidth(95);
		enterButton.setPrefHeight(50);
		enterButton.setStyle(String.format(style, "black"));
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

	public void addListeners() {
		gameBox.widthProperty().addListener((ChangeListener) this::scaleEndDialogBoxWidth);
		gameBox.heightProperty().addListener((ChangeListener) this::scaleEndDialogBoxHeight);
	}

	private void scaleEndDialogBoxWidth(ObservableValue o, Object oldVal, Object newVal) {
		AnchorPane.setLeftAnchor(endDialog, AnchorPane.getLeftAnchor(endDialog) + ((double)newVal - (double)oldVal)/2);
		AnchorPane.setRightAnchor(endDialog, AnchorPane.getRightAnchor(endDialog) + ((double)newVal - (double)oldVal)/2);
	}

	private void scaleEndDialogBoxHeight(ObservableValue o, Object oldVal, Object newVal) {
		AnchorPane.setTopAnchor(endDialog, AnchorPane.getTopAnchor(endDialog) + ((double)newVal - (double)oldVal)/2);
		AnchorPane.setBottomAnchor(endDialog, AnchorPane.getBottomAnchor(endDialog) + ((double)newVal - (double)oldVal)/2);

	}
}