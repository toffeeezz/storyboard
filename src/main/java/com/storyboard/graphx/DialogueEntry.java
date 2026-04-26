package com.storyboard.graphx;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class DialogueEntry extends VBox {

    public String character;
    public String dialogue;

    @FXML private FontIcon deleteButton;

    public DialogueEntry(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/DialogueEntry.fxml"));

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        deleteButton.setCursor(Cursor.HAND);

        deleteButton.setOnMousePressed(e -> {
            if(!(getParent() instanceof  VBox dialogueNode))
                return;
            dialogueNode.getChildren().remove(this);
            System.out.println("remove");
        });
    }
}
