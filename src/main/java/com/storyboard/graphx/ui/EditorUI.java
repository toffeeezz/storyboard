package com.storyboard.graphx.ui;

import com.storyboard.graphx.node.DialogueNode;
import com.storyboard.graphx.Editor;
import com.storyboard.graphx.Editor.Camera;
import com.storyboard.utils.Vector2;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class EditorUI extends StackPane {

    @FXML private Pane inspectorWindow;
    @FXML private Pane editorPane;

    private final Inspector inspector;
    private final Editor editor;
    private final QuickToolBar quickToolBar;

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
        quickToolBar = new QuickToolBar();

        setInspector(inspector);
        setQuickToolBar(quickToolBar);

        editorPane.getChildren().addAll(editor);
        inspectorWindow.getChildren().addAll(inspector);

        editorPane.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if(e.getButton() == MouseButton.SECONDARY){
                System.out.println("Right click");
                if(!editorPane.getChildren().contains(quickToolBar)) {
                    editorPane.getChildren().add(quickToolBar);
                    System.out.println(e.getSceneX());
                    double spawnX = e.getSceneX() > 790 ? e.getSceneX() - quickToolBar.getWidth() : e.getSceneX();
                    quickToolBar.relocate(spawnX, e.getSceneY());
                }
                else
                    editorPane.getChildren().remove(quickToolBar);
                e.consume();
            }else{
                editorPane.getChildren().remove(quickToolBar);
            }
        });

    }

    private void setQuickToolBar(QuickToolBar quickToolBar){
        quickToolBar.onDialogueButtonPressed(e -> {
            Vector2 pos = editor.camera.getMouseWorldPos(e);

            editor.addNode(new DialogueNode(editor), pos);
        });
    }

    private void setInspector(Inspector inspector){
        editor.selectedNodeProperty().addListener(_ -> inspector.showProperties(editor.getSelectedNode(), editor.getSelectedNode() != null));
        inspector.setOnParentLabelPressed(parent -> {
            if(parent == null)
                return;
            Camera camera = editor.getCamera();
            Vector2 dir = Vector2.subtract(parent.getPositionInPixel(), parent.getOrigin());
            camera.focus(dir);
        });
    }
}
