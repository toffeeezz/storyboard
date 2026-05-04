package com.storyboard.graphx.input;

import com.storyboard.graphx.node.comp.ArrowLine;
import com.storyboard.graphx.node.StoryNode;
import com.storyboard.graphx.node.Port;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

public class NodeLinking implements Command{


    private final Port startPort;
    private final Port currentEndPort;

    private final StoryNode parentNode;

    private boolean isLinking = false;

    private ArrowLine arrowLine;


    public NodeLinking(Port startPort, Port currentEndPort, StoryNode parentNode) {
        this.startPort = startPort;
        this.currentEndPort = currentEndPort;

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
        if(arrowLine != null && !isLinking){
            startPort.getStyleClass().remove("active");
            parentNode.getEditor().removeArrow(arrowLine);
            arrowLine = null;
        }
        isLinking = false;

        e.consume();
    }

    @Override
    public void onPress(MouseEvent e) {
        startPort.getStyleClass().remove("hover");

        if(!startPort.getStyleClass().contains("active"))
            startPort.getStyleClass().add("active");

        if(startPort.getEntry() != null && startPort.getEntry().getArrowLine() != null) {
            parentNode.getEditor().removeArrow(startPort.getEntry().getArrowLine());
            startPort.getEntry().setArrowLine(null);
            startPort.getEntry().getConnectedPort().linkedPropertyProperty().set(false);
            startPort.getEntry().getConnectedPort().getStyleClass().remove("hover");
            startPort.getEntry().setConnectedPort(null);
            startPort.getEntry().setNextEntry(null);
        }

        arrowLine = new ArrowLine(20, startPort.getCenterPos(), parentNode.getEditor().getCamera().getMousePixelPos(e));

        arrowLine.setMouseTransparent(true);
        parentNode.getEditor().drawArrowLines(arrowLine);
        e.consume();
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
    }

    @Override
    public void onDropExited(MouseDragEvent e, Object target) {
        if(!(target instanceof Port endPort)) return;

        isLinking = false;
        endPort.getStyleClass().remove("hover");
    }

    @Override
    public void onDropReleased(MouseDragEvent e, Object target) {

        if(!(target instanceof Port endPort)) {
            onDropExited(e, target);
            return;
        }

        if(currentEndPort.equals(endPort)){
            onDropExited(e, endPort);
            return;
        }

        System.out.println(isLinking);
        arrowLine.bindEndpoint(endPort.getCenterPos());
        endPort.linkedPropertyProperty().set(true);
        startPort.getEntry().setNextEntry(endPort.getEntry());
        startPort.getEntry().setArrowLine(arrowLine);
        startPort.getEntry().setConnectedPort(endPort);
    }
}
