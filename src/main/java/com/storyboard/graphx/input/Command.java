package com.storyboard.graphx.input;

import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

public interface Command {
    default void onEnter(MouseEvent e){}
    default void onHover(MouseEvent e){}
    default void onExit(MouseEvent e){}
    default void onPress(MouseEvent e){}
    default void onDragged(MouseEvent e){}
    default void onReleased(MouseEvent e){}
    default void onDragOver(MouseDragEvent e){}
    default void onDragEntered(MouseDragEvent e){}

    default void onDragExited(MouseDragEvent e){}
    default void onDragReleased(MouseDragEvent e){}
    default void onDropEntered(MouseDragEvent e, Object target){}
    default void onDropExited(MouseDragEvent e, Object target){}
    default void onDropReleased(MouseDragEvent e, Object target){}
    default void end(){}
}
