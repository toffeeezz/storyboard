package com.storyboard.graphx.input;

import com.storyboard.graphx.node.StoryNode;
import com.storyboard.utils.Vector2;
import javafx.scene.input.MouseEvent;

public class NodeDragging implements Command{

    private Vector2 lastMousePos;
    private final StoryNode node;

    public NodeDragging(StoryNode node){
        this.node = node;
    }

    @Override
    public void onPress(MouseEvent e) {
        lastMousePos = new Vector2(e.getSceneX(), e.getSceneY());
    }

    @Override
    public void onDragged(MouseEvent e) {
        double currScale = node.getEditor().getWorldPane().getScaleX();

        Vector2 rawDelta = Vector2.subtract(new Vector2(e.getSceneX(), e.getSceneY()), lastMousePos);
        Vector2 scaledDelta = rawDelta.divideBy(currScale);
        Vector2 moveDir = Vector2.add(node.getPositionInPixel().multiplyBy(-1), scaledDelta);

        node.setLayoutX(moveDir.getX());
        node.setLayoutY(moveDir.getY());
        node.updatePosition();

        lastMousePos = new Vector2(e.getSceneX(), e.getSceneY());
    }

}
