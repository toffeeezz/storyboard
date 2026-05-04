package com.storyboard.graphx.input;

import com.storyboard.graphx.Editor;
import com.storyboard.utils.Vector2;
import javafx.scene.input.MouseEvent;

public class CameraPanning implements Command{

    private Vector2 initClickPos;
    private Vector2 finalClickPos;
    private final Editor.Camera camera;

    public CameraPanning(Editor.Camera camera){
        this.camera = camera;
    }

    @Override
    public void onPress(MouseEvent e) {
        initClickPos = camera.getMouseScreenPos(e);
    }

    @Override
    public void onDragged(MouseEvent e) {
        //Get the distance between initial click position and current position
        Vector2 mousePos = camera.getMouseScreenPos(e);
        Vector2 moveDir = new Vector2(initClickPos.getX() - mousePos.getX(),  mousePos.getY() - initClickPos.getY());
        Vector2 translateDir = new Vector2(moveDir.getX() + camera.position.getX(),  camera.position.getY() - moveDir.getY());

        //Apply the difference
        camera.drag(translateDir);
        finalClickPos = translateDir;
    }

    @Override
    public void onReleased(MouseEvent e) {
        if (finalClickPos != null) camera.position = finalClickPos;

    }
}
