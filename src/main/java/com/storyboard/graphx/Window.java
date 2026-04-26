package com.storyboard.graphx;

import com.storyboard.constants.Settings;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Window extends Stage {

    public Window(){


        setMaximized(false);
        setResizable(false);
        setTitle(Settings.windowTitle);



        Scene scene = new Scene(new Editor(), Settings.windowWidth, Settings.windowHeight);

        String css = Objects.requireNonNull(this.getClass().getResource("/css/style.css")).toExternalForm();
        scene.getStylesheets().add(css);

        setScene(scene);

        show();
    }
}
