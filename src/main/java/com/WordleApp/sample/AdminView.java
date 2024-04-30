package com.WordleApp.sample;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.ResourceBundle;

public class AdminView {

    public View getView() {
        try {
            FXMLLoader loader = new FXMLLoader(AdminView.class.getResource("GameScreen.fxml"), ResourceBundle.getBundle("com.WordleApp.sample.main"));
            View view = loader.load();
            GameController cont = loader.getController();
            cont.setup(new GameInstance());
            cont.addListeners();
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }

}
