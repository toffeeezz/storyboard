package com.storyboard.graphx;


import com.storyboard.graphx.input.NodeLinking;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class DialogueEntry extends VBox {

    private String character;
    private String dialogue;
    private final DialogueNode parent;

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
    @FXML private Circle startPort;
    @FXML private Circle endPort;


    public DialogueEntry(DialogueNode dialogueNode){
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
        parent = dialogueNode;

        deleteButton.setCursor(Cursor.HAND);
        startPort.setCursor(Cursor.HAND);

        setStartPort();

        deleteButton.setOnMousePressed(_ -> dialogueNode.removeEntry(this));
    }


    private void setStartPort(){



        startPort.setOnMouseMoved(e -> {
            if(!parent.getEditor().getCommandHandler().isActive())
                parent.getEditor().getCommandHandler().start(new NodeLinking(startPort, endPort, parent));
            parent.getEditor().getCommandHandler().hover(e);
        });

        startPort.setOnDragDetected(_ -> startPort.startFullDrag());
        startPort.setOnMouseDragged(parent.getEditor().getCommandHandler()::drag);
        startPort.setOnMousePressed(parent.getEditor().getCommandHandler()::press);
        startPort.setOnMouseExited(parent.getEditor().getCommandHandler()::exit);
        startPort.setOnMouseReleased(e -> parent.getEditor().getCommandHandler().release(e));
        endPort.setOnMouseDragEntered(parent.getEditor().getCommandHandler()::dragEnter);
        endPort.setOnMouseDragExited(parent.getEditor().getCommandHandler()::dragExit);
    }
}
