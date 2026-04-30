package com.storyboard.graphx;

import com.storyboard.logic.Dialogue;
import com.storyboard.utils.Vector2;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DialogueNode extends StoryNode {

    private final List<DialogueEntry> entryList = new ArrayList<>();

    @FXML private TextField nameField;
    @FXML private VBox entryPane;
    @FXML private FontIcon addButton;

    public List<Dialogue> getDialogues(){
        List<Dialogue> dialogues = new ArrayList<>();

        for(DialogueEntry entry : entryList)
            dialogues.add(new Dialogue(entry.getCharacter(), entry.getDialogue()));

        return dialogues;
    }

    public DialogueNode(Editor editor){
        super(editor);
        setDefault();

    }

    public DialogueNode(Editor editor, StoryNode parent){
        super(editor, parent);
        setDefault();
    }

    private void setDefault(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/DialogueNode.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setId(nameField.getText());
        setBackground(Background.fill(Color.BLACK));
        setBorder(Border.stroke(Color.WHITE));
        getStyleClass().add("dialogue-card");

        origin = new Vector2(this.getPrefWidth() / 2, this.getPrefHeight() / 2);
        addButton.setCursor(Cursor.HAND);
        addButton.setOnMousePressed(_ -> addEntry(new DialogueEntry(this)));
        setOnKeyPressed(this::onKeyPressed);

        nameField.focusedProperty().addListener((_, _, isFocused) -> {
            if(!isFocused){
                setId(nameField.getText());
                System.out.println("Exited");
            }
        });
    }

    protected void removeEntry(DialogueEntry entry){
        entryList.remove(entry);
        entryPane.getChildren().remove(entry);
    }

    private void addEntry(DialogueEntry entry){
        entryList.add(entry);
        entryPane.getChildren().add(entry);
    }
}
