package com.storyboard.graphx.input;

import com.storyboard.graphx.node.comp.ArrowLine;
import com.storyboard.graphx.node.StoryNode;
import com.storyboard.graphx.node.Port;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

public class NodeLinking implements Command{


    private final Port startPort;

    private final StoryNode parentNode;

    private boolean isLinking = false;

    private ArrowLine arrowLine;


    public NodeLinking(Port startPort, StoryNode parentNode) {
        this.startPort = startPort;

        this.parentNode = parentNode;

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

        if(arrowLine != null && !isLinking){

            parentNode.getEditor().removeArrow(arrowLine);
            arrowLine = null;
        }
        isLinking = false;
    }

    @Override
    public void onPress(MouseEvent e) {
        startPort.getStyleClass().remove("hover");
        startPort.getStyleClass().add("pressed");


        if(arrowLine == null)
            arrowLine = new ArrowLine(20, startPort.getCenterPos(), parentNode.getEditor().getCamera().getMousePixelPos(e));

        arrowLine.setMouseTransparent(true);
        parentNode.getEditor().drawArrowLines(arrowLine);
    }

    @Override
    public void onDragged(MouseEvent e) {
        if(arrowLine == null) return;
        arrowLine.bindEndpoint(parentNode.getEditor().getCamera().getMousePixelPos(e));

    }

    @Override
    public void onDropEntered(MouseDragEvent e, Object target) {
        if(!(target instanceof Port endPort)) return;

        endPort.getStyleClass().add("hover");
        isLinking = true;
        System.out.println("port hovering");
    }

    @Override
    public void onDropExited(MouseDragEvent e, Object target) {
        if(!(target instanceof Port endPort)) return;

        isLinking = false;
        endPort.getStyleClass().remove("hover");
    }

    @Override
    public void onDropReleased(MouseDragEvent e, Object target) {
        System.out.println("Connected");

        if(!(target instanceof Port endPort)) return;

        arrowLine.bindEndpoint(endPort.getCenterPos());
        isLinking = false;
        end();
    }
}
