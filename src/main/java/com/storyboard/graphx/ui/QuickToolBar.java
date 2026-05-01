package com.storyboard.graphx.ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class QuickToolBar extends VBox {

    @FXML private Button addDialogueButton;

    public QuickToolBar(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/ui/QuickToolBar.fxml"));

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    protected void onDialogueButtonPressed(EventHandler<MouseEvent> e){
        addDialogueButton.setOnMousePressed(e);
    }
}
