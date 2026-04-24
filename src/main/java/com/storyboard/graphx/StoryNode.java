package com.storyboard.graphx;

import com.storyboard.utils.Vector2;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class StoryNode extends StackPane {

    protected Vector2 positionInWorld; //Already at the center of the node
    protected Vector2 positionInPixel;
    protected StoryNode parentNode;
    protected List<StoryNode> children;
    protected Vector2 origin;

    Vector2 lastMousePos;

    protected StoryNode(){
        setViewOrder(-10);
        setOnMousePressed(mouseEvent -> {
            lastMousePos = new Vector2(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            mouseEvent.consume();
        });
        setOnMouseDragged(mouseEvent -> {
            double currScale = Editor.worldPane.getScaleX();

            Vector2 rawDelta = Vector2.subtract(new Vector2(mouseEvent.getSceneX(), mouseEvent.getSceneY()), lastMousePos);
            Vector2 scaledDelta = rawDelta.divideBy(currScale);

            Vector2 moveDir = Vector2.add(positionInPixel, scaledDelta);
            setLayoutX(moveDir.x.get());
            setLayoutY(moveDir.y.get());
            updatePosition();

            lastMousePos = new Vector2(mouseEvent.getSceneX(), mouseEvent.getSceneY());

            mouseEvent.consume();
        });

        children = new ArrayList<>();
    }

    protected void setParentNode(StoryNode node){parentNode = node;}
    public StoryNode getParentNode(){return parentNode;}

    protected void addChildren(StoryNode node){children.add(node);}

    protected void updatePosition() {

        Vector2 pixelPos = new Vector2(getLayoutX(), getLayoutY());

        positionInPixel = pixelPos;
        positionInWorld = new Vector2(pixelPos.x.get() - Editor.pixelOrigin.x.get(), Editor.pixelOrigin.y.get() - pixelPos.y.get());

        positionInPixel.x.bind(layoutXProperty());
        positionInPixel.y.bind(layoutYProperty());
    }
}
