package com.storyboard.graphx.ui;

import com.storyboard.graphx.Editor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class EditorUI extends StackPane {

    @FXML private Pane inspectorWindow;
    @FXML private Pane editorPane;

    private final Inspector inspector;
    private final Editor editor;

    public EditorUI(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/ui/EditorUI.fxml"));

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        editor = new Editor();
        inspector = new Inspector();

        editor.selectedNodeProperty().addListener(_ -> inspector.showProperties(editor.getSelectedNode(), editor.getSelectedNode() != null));



        editorPane.getChildren().addAll(editor);
        inspectorWindow.getChildren().addAll(inspector);

    }
}
