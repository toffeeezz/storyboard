package com.storyboard.graphx.node;

import com.storyboard.utils.Vector2;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class Port extends Circle {

    private final SimpleBooleanProperty linkedProperty = new SimpleBooleanProperty();

    public boolean isLinkedProperty() {
        return linkedProperty.get();
    }

    public SimpleBooleanProperty linkedPropertyProperty() {
        return linkedProperty;
    }

    public StoryNode getStoryNode() {
        return storyNode;
    }

    public void setStoryNode(StoryNode storyNode) {
        this.storyNode = storyNode;
    }

    private StoryNode storyNode;

    public Port() {
        super();
    }
    
    public Vector2 getCenterPos(){
        Vector2 endCenter = new Vector2();

        endCenter.x.bind(Bindings.createDoubleBinding(() -> {
            Point2D scenePos = localToScene(getCenterX(), getCenterY());
            Point2D worldPos = storyNode.getEditor().getWorldPane().sceneToLocal(scenePos);
            return worldPos.getX();
        }, layoutXProperty(), storyNode.layoutXProperty()));

        endCenter.y.bind(Bindings.createDoubleBinding(() -> {
            Point2D scenePos = localToScene(getCenterX(), getCenterY());
            Point2D worldPos = storyNode.getEditor().getWorldPane().sceneToLocal(scenePos);
            return worldPos.getY();
        }, layoutYProperty(), storyNode.layoutYProperty()));

        linkedProperty.addListener((_, _, isLinked) -> {
            if(isLinked)
                getStyleClass().add("active");
            else
                getStyleClass().remove("active");
        });

        return endCenter;
    }
}
