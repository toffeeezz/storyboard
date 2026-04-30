package com.storyboard.graphx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class UI extends StackPane {

    public static UI instance;

    @FXML private StackPane editorUI;


    @FXML private Pane editorPane;

    public Label getNameLabel() {
        return nameLabel;
    }

    @FXML private Label nameLabel;

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
        showEditor();
    }


    public void showEditor(){
        Editor editor = new Editor();
        editor.selectedNodeProperty().addListener(_ -> {
            if(editor.getSelectedNode() != null) {
                nameLabel.setText(editor.getSelectedNode().getId());
                editor.getSelectedNode().idProperty().addListener(_ -> nameLabel.setText(editor.getSelectedNode().getId()));
            }
            else
                nameLabel.setText("");
        });


        editorPane.getChildren().addAll(editor);
    }
}
