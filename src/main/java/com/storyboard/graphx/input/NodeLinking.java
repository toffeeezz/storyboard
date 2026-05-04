package com.storyboard.graphx.input;

import com.storyboard.graphx.ArrowLine;
import com.storyboard.graphx.StoryNode;
import com.storyboard.utils.Vector2;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class NodeLinking implements Command{


    private final Circle startPort;
    private final Circle endPort;
    private final StoryNode parentNode;

    private ArrowLine arrowLine;
    private final Vector2 center = new Vector2();


    public NodeLinking(Circle startPort, Circle endPort, StoryNode parentNode) {
        this.startPort = startPort;
        this.endPort = endPort;
        this.parentNode = parentNode;

        center.x.bind(Bindings.createDoubleBinding(() -> {
            Point2D scenePos = startPort.localToScene(startPort.getCenterX(), startPort.getCenterY());
            Point2D worldPos = parentNode.getEditor().getWorldPane().sceneToLocal(scenePos);
            return worldPos.getX();
        }, startPort.layoutXProperty(), parentNode.layoutXProperty()));

        center.y.bind(Bindings.createDoubleBinding(() -> {
            Point2D scenePos = startPort.localToScene(startPort.getCenterX(), startPort.getCenterY());
            Point2D worldPos = parentNode.getEditor().getWorldPane().sceneToLocal(scenePos);
            return worldPos.getY();
        }, startPort.layoutYProperty(), parentNode.layoutYProperty()));



    }

    @Override
    public void onHover(MouseEvent e) {
        if(!startPort.getStyleClass().contains("hover"))
            startPort.getStyleClass().add("hover");
    }

    @Override
    public void onExit(MouseEvent e) {
        startPort.getStyleClass().remove("hover");
    }

    @Override
    public void onReleased(MouseEvent e) {
        startPort.getStyleClass().remove("pressed");
        endPort.getStyleClass().remove("hover");
        if(arrowLine != null){
        parentNode.getEditor().removeArrow(arrowLine);
        arrowLine = null;
        }
    }

    @Override
    public void onPress(MouseEvent e) {
        startPort.getStyleClass().remove("hover");
        startPort.getStyleClass().add("pressed");


        if(arrowLine == null)
            arrowLine = new ArrowLine(20, center, parentNode.getEditor().getCamera().getMousePixelPos(e));

        arrowLine.setMouseTransparent(true);
        parentNode.getEditor().drawArrowLines(arrowLine);
    }

    @Override
    public void onDragged(MouseEvent e) {
        arrowLine.bindEndpoint(parentNode.getEditor().getCamera().getMousePixelPos(e));
        System.out.println("Dragging");
    }

    @Override
    public void onDragEntered(MouseDragEvent e) {
        endPort.getStyleClass().add("hover");
        System.out.println("port hovering");
    }

    @Override
    public void onDragExited(MouseDragEvent e) {
        endPort.getStyleClass().remove("hover");
    }
}
