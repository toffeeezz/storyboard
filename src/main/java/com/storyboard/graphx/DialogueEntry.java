package com.storyboard.graphx;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class DialogueEntry extends VBox {

    private String character;
    private String dialogue;

    public String getDialogue() {
        dialogue = dialogueField.getText();
        return dialogue;
    }

    public String getCharacter() {
        character = characterField.getText();
        return character;
    }



    @FXML private FontIcon deleteButton;
    @FXML private TextField characterField;
    @FXML private TextArea dialogueField;

    public DialogueEntry(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/DialogueEntry.fxml"));

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dialogue = dialogueField.getText();
        character = characterField.getText();

        deleteButton.setCursor(Cursor.HAND);
        deleteButton.setOnMousePressed(e -> {
            if(!(getParent() instanceof  VBox dialogueNode))
                return;
            dialogueNode.getChildren().remove(this);
            System.out.println("remove");
        });
    }
}
