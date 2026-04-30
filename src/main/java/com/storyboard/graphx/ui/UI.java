package com.storyboard.graphx.ui;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class UI extends StackPane {

    public static UI instance;

    private final EditorUI editorUI = new EditorUI();


    public UI(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/UI.fxml"));

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        instance = this;
        toggleEditor();
    }


    public void toggleEditor(){
        if(getChildren().contains(editorUI))
            getChildren().remove(editorUI);
        else
            getChildren().add(editorUI);
    }
}
