package com.storyboard.graphx.node.comp;


import com.storyboard.graphx.input.NodeLinking;
import com.storyboard.graphx.node.DialogueNode;
import com.storyboard.graphx.node.Port;
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
    private final DialogueNode dialogueNode;

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
    @FXML private Port startPort;
    @FXML private Port endPort;


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
        this.dialogueNode = dialogueNode;
        startPort.setStoryNode(this.dialogueNode);
        endPort.setStoryNode(this.dialogueNode);

        deleteButton.setCursor(Cursor.HAND);
        startPort.setCursor(Cursor.HAND);
        this.dialogueNode.getEditor().registerLinkPort(endPort);

        setStartPort();

        deleteButton.setOnMousePressed(_ -> dialogueNode.removeEntry(this));
    }


    private void setStartPort(){



        startPort.setOnMouseMoved(e -> {
            if(!dialogueNode.getEditor().getCommandHandler().isActive())
                dialogueNode.getEditor().getCommandHandler().start(new NodeLinking(startPort, dialogueNode));
            dialogueNode.getEditor().getCommandHandler().hover(e);
        });

        startPort.setOnDragDetected(_ -> startPort.startFullDrag());
        startPort.setOnMouseDragged(dialogueNode.getEditor().getCommandHandler()::drag);
        startPort.setOnMousePressed(dialogueNode.getEditor().getCommandHandler()::press);
        startPort.setOnMouseExited(dialogueNode.getEditor().getCommandHandler()::exit);
        startPort.setOnMouseReleased(e -> dialogueNode.getEditor().getCommandHandler().release(e));

    }
}
