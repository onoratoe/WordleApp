package com.WordleApp.sample;

import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static com.gluonhq.charm.glisten.application.AppManager.HOME_VIEW;
import static com.gluonhq.charm.glisten.application.AppManager.SPLASH_VIEW;

public class Home extends Application {

    public static final String MAIN_VIEW = HOME_VIEW;
    public static final String SECOND_VIEW = SPLASH_VIEW;

    private final AppManager appManager = AppManager.initialize(this::postInit);

    @Override
    public void init() {
        appManager.addViewFactory(MAIN_VIEW, () -> new HomeView().getView());
        appManager.addViewFactory(SECOND_VIEW, () -> new GameView().getView());
        DrawerManager.buildDrawer(appManager);
    }

    @Override
    public void start(Stage stage) {
        appManager.start(stage);
    }

    private void postInit(Scene scene) {
        Swatch.LIGHT_GREEN.assignTo(scene);
        scene.getStylesheets().add(Home.class.getResource("styles.css").toExternalForm());
        ((Stage) scene.getWindow()).getIcons().add(new Image(Home.class.getResourceAsStream("/icon.png")));

        if (Platform.isDesktop()) {
            Dimension2D dimension2D = DisplayService.create()
                    .map(DisplayService::getDefaultDimensions)
                    .orElse(new Dimension2D(640, 480));
            scene.getWindow().setWidth(dimension2D.getWidth());
            scene.getWindow().setHeight(dimension2D.getHeight());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
