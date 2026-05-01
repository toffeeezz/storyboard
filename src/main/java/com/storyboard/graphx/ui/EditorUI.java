package com.storyboard.graphx.ui;

import com.storyboard.graphx.Editor;
import com.storyboard.graphx.Editor.Camera;
import com.storyboard.utils.Vector2;
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
        inspector.setOnParentLabelPressed(parent -> {
            if(parent == null)
                return;
            Camera camera = editor.getCamera();
            Vector2 dir = Vector2.subtract(parent.getPositionInPixel(), parent.getOrigin());
            camera.focus(dir);
        });


        editorPane.getChildren().addAll(editor);
        inspectorWindow.getChildren().addAll(inspector);

    }
}
