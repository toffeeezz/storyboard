package com.storyboard.graphx;

import com.storyboard.utils.Vector2;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
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


    private final Line line;
    private final Polygon head;



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

        line = new Line();
        line.startXProperty().bind(startPoint.x);
        line.startYProperty().bind(startPoint.y);
        line.setStrokeWidth(5);
        line.setStroke(Color.WHITE);
        line.setViewOrder(Editor.lineViewOrder);
        line.endYProperty().bind(endPoint.y);
        line.endXProperty().bind(endPoint.x);

        head = new Polygon(
                0, 0,        //Tip
                -arrowSize, -arrowSize / 2,    // Left
                -arrowSize, arrowSize / 2      // Right
        );
        head.setViewOrder(Editor.arrowViewOrder);
        head.setFill(Color.WHITE);
        head.layoutXProperty().bind(line.endXProperty());
        head.layoutYProperty().bind(line.endYProperty());
        head.rotateProperty().bind(angle);

        shapes.add(line);
        shapes.add(head);
    }

    private void updateAngle(){
        double dx = dist.getX();
        double dy = dist.getY();
        double radians = Math.atan2(dy, dx);
        angle.set(Math.toDegrees(radians));
        System.out.println(Math.toDegrees(radians));
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
