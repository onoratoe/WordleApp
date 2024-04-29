package com.WordleApp.sample;

import com.gluonhq.charm.glisten.mvc.View;
import java.util.ResourceBundle;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class MainView {

    public View getView() {
        try {
            View view = FXMLLoader.load(MainView.class.getResource("MainMenu.fxml"), ResourceBundle.getBundle("com.WordleApp.sample.main"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }

}