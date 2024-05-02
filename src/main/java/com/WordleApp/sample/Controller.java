package com.WordleApp.sample;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * @author onoratoe
 * @version 1.0
 * @created 13-Feb-2024 4:30:19 PM
 */
public class Controller {
	@FXML
	public AnchorPane anchor;
	@FXML
	private VBox homeBox;
	private histView hist;
	@FXML
	private VBox helpBox;
	@FXML
	View main;
	public void initialize() {
		main.showingProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				AppBar appBar = AppManager.getInstance().getAppBar();
				appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> AppManager.getInstance().getDrawer().open()));
				appBar.setTitleText("WordleApp");
			}
		});
	}
	@FXML
	private void help(){
		helpBox.setVisible(true);
		homeBox.setVisible(false);
	}
	@FXML
	private void home(){
		goHome();
	}
	@FXML
	private void goHome() {
		homeBox.setVisible(true);
		helpBox.setVisible(false);
	}
}