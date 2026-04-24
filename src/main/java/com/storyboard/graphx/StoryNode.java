package com.storyboard.graphx;

import javafx.scene.layout.StackPane;

public class StoryNode extends StackPane {

    protected Vector2 position;
    protected StoryNode parentNode;

    final Vector2 delta = new Vector2();

    protected StoryNode(){
        setViewOrder(-10);
        setOnMousePressed(mouseEvent -> {
            delta.x = getLayoutX() - mouseEvent.getSceneX();
            delta.y = getLayoutY() - mouseEvent.getSceneY();

            mouseEvent.consume();
        });
        setOnMouseDragged(mouseEvent -> {
            Vector2 moveDir = new Vector2(mouseEvent.getSceneX() + delta.x, mouseEvent.getSceneY() + delta.y);
            setPosition(moveDir);

            mouseEvent.consume();
        });
    }

    protected void setParentNode(StoryNode node){parentNode = node;}
    protected StoryNode getParentNode(){return parentNode;}

    protected void setPosition(Vector2 position){
        this.position = position;
        setLayoutX(position.x);
        setLayoutY(position.y);
    }

    protected Vector2 getPosition(){

        return new Vector2(position.x - (this.getWidth() / 2),
                                    position.y - (this.getHeight() / 2));
    }

}
