package com.storyboard.graphx;

import com.storyboard.constants.Settings;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Window extends Stage {

    public Window(){

        setHeight(Settings.windowHeight);
        setWidth(Settings.windowWidth);
        setMaximized(false);
        setResizable(false);
        setTitle(Settings.windowTitle);

        setScene(new Scene(new Editor()));

        show();
    }
}

class SceneManager{

}
