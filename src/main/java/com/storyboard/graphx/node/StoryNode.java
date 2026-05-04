package com.storyboard.graphx.node;

import com.storyboard.graphx.Editor;
import com.storyboard.graphx.input.NodeDragging;
import com.storyboard.utils.Vector2;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class StoryNode extends StackPane {

    private final Editor editor;
    protected Vector2 positionInWorld; //Already at the center of the node
    protected Vector2 positionInPixel;

    private final List<StoryNode> children;

    Vector2 origin;
    protected Vector2 center;

    public Editor getEditor(){
        return editor;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public Vector2 getPositionInPixel() {
        return positionInPixel.multiplyBy(-1);
    }

    public List<StoryNode> getChildrenList() {
        return children;
    }

    protected StoryNode(Editor editor){
        this.editor = editor;
        children = new ArrayList<>();
        setDefaults();
    }

    private void setDefaults(){
        setViewOrder(Editor.nodeViewOrder);
        setOnMousePressed(mouseEvent -> {
            if(editor.getCommandHandler().isActive()) return;

            editor.getCommandHandler().start(new NodeDragging(this));
            editor.getCommandHandler().press(mouseEvent);

            requestFocus();
            mouseEvent.consume();
        });
        setOnMouseDragged(mouseEvent -> {
            editor.getCommandHandler().drag(mouseEvent);
            mouseEvent.consume();
        });
        setOnMouseReleased(e -> {
            editor.getCommandHandler().release(e);
            editor.getCommandHandler().end();
        });

        setOnKeyPressed(this::onKeyPressed);

        focusWithinProperty().addListener((_, _, isFocused) -> {
            if(isFocused){
                if(!getStyleClass().contains("focused"))
                    getStyleClass().add("focused");
                setViewOrder(Editor.nodeViewOrder - 1);
                editor.setSelectedNode(this);
            }else{
                getStyleClass().remove("focused");
                setViewOrder(Editor.nodeViewOrder);
            }
        });
    }

    public void updatePosition() {

        Vector2 pixelPos = new Vector2(getLayoutX(), getLayoutY());

        positionInPixel = pixelPos;
        positionInWorld = new Vector2(pixelPos.getX() - editor.getPixelOrigin().getX(), editor.getPixelOrigin().getY() - pixelPos.getY());

        positionInPixel.x.bind(layoutXProperty());
        positionInPixel.y.bind(layoutYProperty());
    }

    protected void onKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.DELETE) {
            editor.removeNode(this);
        }
    }
}
