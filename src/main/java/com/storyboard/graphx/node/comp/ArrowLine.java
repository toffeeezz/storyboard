package com.storyboard.graphx.node.comp;

import com.storyboard.graphx.Editor;
import com.storyboard.utils.Vector2;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class ArrowLine {


    public final SimpleDoubleProperty angle;
    public final Vector2 endPoint;
    public final Vector2 startPoint;
    public final Vector2 dist;

    public final List<Shape> shapes = new ArrayList<>();


    public ArrowLine(double arrowSize, Vector2 start, Vector2 end) {


        angle = new SimpleDoubleProperty();
        endPoint = new Vector2();
        startPoint = new Vector2();
        dist = new Vector2();

        dist.x.addListener(_ -> updateAngle());
        dist.y.addListener(_ -> updateAngle());

        startPoint.x.bind(start.x);
        startPoint.y.bind(start.y);

        bindEndpoint(end);

        Line line = new Line();
        line.startXProperty().bind(startPoint.x);
        line.startYProperty().bind(startPoint.y);
        line.setStrokeWidth(5);
        line.getStyleClass().add("arrow");
        line.setViewOrder(Editor.lineViewOrder);
        line.endYProperty().bind(endPoint.y);
        line.endXProperty().bind(endPoint.x);

        //Tip
        // Left
        // Right
        Polygon head = new Polygon(
                0, 0,        //Tip
                -arrowSize, -arrowSize / 2,    // Left
                -arrowSize, arrowSize / 2      // Right
        );
        head.setViewOrder(Editor.arrowViewOrder);
        head.layoutXProperty().bind(startPoint.x.add(endPoint.x).divide(2).add(arrowSize / 2));
        head.layoutYProperty().bind(startPoint.y.add(endPoint.y).divide(2));
        head.getStyleClass().add("arrow");
        head.rotateProperty().bind(angle);

        shapes.add(line);
        shapes.add(head);
    }

    private void updateAngle(){
        double dx = dist.getX();
        double dy = dist.getY();
        double radians = Math.atan2(dy, dx);
        angle.set(Math.toDegrees(radians));

    }

    public void setMouseTransparent(boolean value){
        for(Shape shape : shapes)
            shape.setMouseTransparent(value);
    }

    public void bindEndpoint(Vector2 point){
        if(endPoint.x.isBound() && endPoint.y.isBound()){
            endPoint.x.unbind();
            endPoint.y.unbind();
        }
        endPoint.x.bind(point.x);
        endPoint.y.bind(point.y);

        if(dist.x.isBound() && dist.y.isBound()){
            dist.x.unbind();
            dist.y.unbind();
        }
        dist.x.bind(endPoint.x.subtract(startPoint.x));
        dist.y.bind(endPoint.y.subtract(startPoint.y));
    }
}
