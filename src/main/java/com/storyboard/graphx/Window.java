package com.storyboard.graphx;

import com.storyboard.constants.Settings;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Window extends Stage {

    public Window(){


        setMaximized(false);
        setResizable(false);
        setTitle(Settings.windowTitle);

        Scene scene = new Scene(new Editor(), Settings.windowWidth, Settings.windowHeight);

        setScene(scene);

        show();
    }
}
