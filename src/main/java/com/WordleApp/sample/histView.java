package com.WordleApp.sample;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.HashMap;

import static java.lang.Math.max;

public class histView extends Region {
    private VBox mainBox;
    private int total;
    private int max;
    private HashMap<Integer, Integer> data;
    private double prefWidth = 180;
    private double prefHeight = 200;

    public histView(HashMap<Integer, Integer> data) {
        mainBox = new VBox();
        this.data = data;
        this.setPrefWidth(180);
        this.setPrefHeight(200);
        mainBox.setPrefHeight(200);
        this.getChildren().add(mainBox);
        //test.prefHeightProperty().bind(test.heightProperty());
        this.heightProperty().addListener((ChangeListener) this::setBarHeights);
        this.widthProperty().addListener((ChangeListener) this::setBarWidths);
        setData(data);

    }

    private void setBarWidths(ObservableValue o, Object oldVal, Object newVal) {
        this.prefWidth = (double) newVal;
        System.out.println("called");
        System.out.println(prefWidth);
        for (Node child : mainBox.getChildren()) {
            Label childe = ((Label) child);
            int val = data.get(Integer.parseInt(childe.getId()));

            childe.setPrefWidth(max((double) newVal * (((double)val) / max), 30.0));
        }

    }

    private void setBarHeights(ObservableValue o,Object oldVal, Object newVal) {

        prefHeight = (Double) newVal;

        for (Node child : mainBox.getChildren()) {
            Region childe = ((Region) child);
            VBox.setMargin(childe, new Insets(prefHeight * 0.02, childe.getInsets().getRight(), prefHeight * 0.02, 5));
            //System.out.println(VBox.getMargin(childe));
            childe.setPrefHeight(prefHeight / mainBox.getChildren().size() - prefHeight * 0.02);

        }
    }

    public void setData(HashMap<Integer, Integer> data) {
        mainBox.getChildren().clear();

        total = 0;
        max = 0;
        for (Integer key : data.keySet()) {
            total += data.get(key);
            if (data.get(key) > max) {
                max = data.get(key);
            }
        }
        max = max + 2;

        for (Integer i = 1; i <= 6; i++) {
            Label bar = new Label(" " + i + " - " + data.get(i));

            bar.setId(i.toString());
            bar.setStyle("-fx-background-color: Green; -fx-text-fill: White");
            //TODO this
            bar.setPrefWidth(max(prefWidth * (((double)data.get(i)) / max), 30.0));

            bar.setPrefHeight(prefHeight / data.keySet().size());


            mainBox.getChildren().add(bar);

            VBox.setMargin(bar, new Insets(4, 0, 4, 5));
            //System.out.println(VBox.getMargin(bar));
            //bar.prefHeightProperty().bind(10);

        }
    }


}
