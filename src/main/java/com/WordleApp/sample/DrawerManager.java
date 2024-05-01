package com.WordleApp.sample;

import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.attach.util.Services;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.control.NavigationDrawer.Item;
import com.gluonhq.charm.glisten.control.NavigationDrawer.ViewItem;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.scene.image.Image;

import static com.WordleApp.sample.Home.*;

public class DrawerManager {

    public static void buildDrawer(AppManager app) {
        NavigationDrawer drawer = app.getDrawer();

        NavigationDrawer.Header header = new NavigationDrawer.Header("Gluon Application",
                "WordleApp",
                new Avatar(21, new Image(DrawerManager.class.getResourceAsStream("/icon.png"))));
        drawer.setHeader(header);

        final Item mainItem = new ViewItem("Main Menu", MaterialDesignIcon.HOME.graphic(), MAIN_VIEW, ViewStackPolicy.SKIP);
        drawer.getItems().addAll(mainItem);

        final Item secondItem = new ViewItem("Game", MaterialDesignIcon.HOME.graphic(), SECOND_VIEW, ViewStackPolicy.SKIP);
        drawer.getItems().addAll(secondItem);

        if (Platform.isDesktop()) {
            final Item quitItem = new Item("Quit", MaterialDesignIcon.EXIT_TO_APP.graphic());
            quitItem.selectedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    Services.get(LifecycleService.class).ifPresent(LifecycleService::shutdown);
                    System.exit(0);
                }
            });
            drawer.getItems().add(quitItem);
        }
    }

}
