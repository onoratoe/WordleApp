package com.WordleApp.sample;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.ResourceBundle;

public class GameView {

    public View getView() {
        try {
            View view = FXMLLoader.load(GameView.class.getResource("GameScreen.fxml"), ResourceBundle.getBundle("com.WordleApp.sample.main"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }

}
