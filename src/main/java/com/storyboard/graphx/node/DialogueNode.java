package com.storyboard.graphx.node;

import com.storyboard.graphx.node.comp.DialogueEntry;
import com.storyboard.graphx.Editor;
import com.storyboard.graphx.node.comp.Dialogue;
import com.storyboard.utils.Vector2;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A story node that holds a sequence of dialogue entries.
 * Each entry contains a character name and their dialogue text.
 *
 * The node's visual layout is defined in DialogueNode.fxml.
 * Its ID is kept in sync with the nameField so it can be referenced
 * by name during export and linking.
 */
public class DialogueNode extends StoryNode {

    private final List<DialogueEntry> entryList = new ArrayList<>();

    @FXML private TextField nameField;
    @FXML private VBox entryPane;
    @FXML private FontIcon addButton;

    public DialogueNode(Editor editor) {
        super(editor);
        setDefault();
    }

    private void setDefault() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/DialogueNode.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Sync the node ID with the nameField text after FXML is loaded
        setId(nameField.getText());
        setBackground(Background.fill(Color.BLACK));
        setBorder(Border.stroke(Color.WHITE));
        getStyleClass().add("dialogue-card");

        origin = new Vector2(this.getPrefWidth() / 2, this.getPrefHeight() / 2);

        // Bind center to the node's layout position plus its origin offset
        center = new Vector2();
        center.x.bind(layoutXProperty().add(origin.x));
        center.y.bind(layoutYProperty().add(origin.y));

        addButton.setCursor(Cursor.HAND);
        addButton.setOnMousePressed(_ -> addEntry(new DialogueEntry(this)));
        setOnKeyPressed(this::onKeyPressed);

        // Update the node ID whenever the user finishes editing the name field
        nameField.focusedProperty().addListener((_, _, isFocused) -> {
            if (!isFocused)
                setId(nameField.getText());
        });
    }

    /**
     * Returns a snapshot list of all current dialogue entries as {@link Dialogue} objects.
     * Used during export to collect character and dialogue text from each entry.
     */
    public List<Dialogue> getDialogues() {
        List<Dialogue> dialogues = new ArrayList<>();
        for (DialogueEntry entry : entryList)
            dialogues.add(new Dialogue(entry.getCharacter(), entry.getDialogue()));
        return dialogues;
    }

    /**
     * Starts the dialogue playback cycle by finding the first entry
     * that has a linked next entry and beginning the chain from there.
     */
    public void startCycle() {
        for (DialogueEntry entry : entryList) {
            if (entry.getNextEntry() != null) {
                entry.cycleEntry();
                break;
            }
        }
    }

    @Override
    void onKeyPressed(KeyEvent e) {
        super.onKeyPressed(e);
        if (e.getCode() == KeyCode.S && e.isControlDown())
            startCycle();
    }

    public void removeEntry(DialogueEntry entry) {
        entryList.remove(entry);
        entryPane.getChildren().remove(entry);
    }

    private void addEntry(DialogueEntry entry) {
        entryList.add(entry);
        entryPane.getChildren().add(entry);
    }
}