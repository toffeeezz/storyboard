package com.storyboard.graphx;

import com.storyboard.utils.Vector2;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DialogueNode extends StoryNode {

    private final double defaultW = 300;
    private final double defaultH = 200;

    private final List<DialogueEntry> entryList = new ArrayList<>();

    public String getCompiledDialogue() {
        compileDialogues();
        return compiledDialogue;
    }

    protected String compiledDialogue;

    @FXML private VBox entryPane;
    @FXML private FontIcon addButton;

    @FXML private TextField textField;
    @FXML private AnchorPane topBar;

    public DialogueNode(){
        setDefault();

    }

    public DialogueNode(StoryNode parent){
        setParentNode(parent);
        parent.addChildren(this);

        setDefault();
    }




    private void setDefault(){
        setPrefSize(defaultW, defaultH);
        setBackground(Background.fill(Color.BLACK));
        setBorder(Border.stroke(Color.WHITE));
        getStyleClass().add("dialogue-card");


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/DialogueNode.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        origin = new Vector2(this.getPrefWidth() / 2, this.getPrefHeight() / 2);
        addButton.setCursor(Cursor.HAND);
        addButton.setOnMousePressed(e -> {
            DialogueEntry entry = new DialogueEntry();
            entryList.add(entry);
            entryPane.getChildren().add(entry);
        });
        setOnKeyPressed(this::onKeyPressed);

    }

    private void onKeyPressed(KeyEvent event){

        if(event.getCode() == KeyCode.ENTER && event.isShiftDown()){
            compileDialogues();
        }
        event.consume();
    }

    private void compileDialogues(){
        StringBuilder sb = new StringBuilder();
        for(DialogueEntry entry : entryList)
            sb.append("Character: ").append(entry.getCharacter()).append("\nDialogue: ").append(entry.getDialogue()).append("\n");
        compiledDialogue = sb.toString();
        System.out.println(compiledDialogue);
    }
}
