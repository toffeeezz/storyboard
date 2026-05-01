package com.storyboard.graphx;

import com.storyboard.utils.Vector2;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

public class ArrowLine {


    public final DoubleBinding angle;
    public final DoubleBinding hypLen;
    public final DoubleBinding endpointX;
    public final DoubleBinding endpointY;

    public final List<Node> shapes = new ArrayList<>();

    protected StoryNode child;
    protected StoryNode node;


    public ArrowLine(double arrowSize, StoryNode child, StoryNode node){
        this.child = child;
        this.node = node;

        DoubleBinding childX = child.layoutXProperty().add(child.widthProperty().divide(2));
        DoubleBinding childY = child.layoutYProperty().add(child.heightProperty().divide(2));
        DoubleBinding nodeX = node.layoutXProperty().add(node.widthProperty().divide(2));
        DoubleBinding nodeY = node.layoutYProperty().add(node.heightProperty().divide(2));

        //Create the line and bind each point from the child to the child
        Line line = new Line();
        line.startXProperty().bind(nodeX);
        line.startYProperty().bind(nodeY);
        line.endYProperty().bind(childY);
        line.setStrokeWidth(5);
        line.setStroke(Color.WHITE);
        line.setViewOrder(Editor.lineViewOrder);


        Polygon head = new Polygon(
                0, 0,        //Tip
                -arrowSize, -arrowSize / 2,    // Left
                -arrowSize, arrowSize / 2      // Right
        );

        head.layoutXProperty().bind(line.endXProperty().add(arrowSize / 2));
        head.layoutYProperty().bind(line.endYProperty());
        head.setFill(Color.WHITE);
        head.setViewOrder(Editor.arrowViewOrder);

        //Calculate the distance between the two points of the line
        Vector2 dist = new Vector2();
        dist.x.bind(childX.subtract(nodeX));
        dist.y.bind(childY.subtract(nodeY));
        hypLen = Bindings.createDoubleBinding(() -> {
            double dx = dist.getX();
            double dy = dist.getY();
            return Math.sqrt(dx * dx + dy * dy);
        }, dist.x, dist.y);

        //Calculate the angle of the line
        angle = Bindings.createDoubleBinding(() -> {
            double dx = dist.getX();
            double dy = dist.getY();

            double radians = Math.atan2(dy, dx);
            return Math.toDegrees(radians);
        }, dist.x, dist.y);

        //Calculate the X endpoint based on the angle of the arrow
        endpointX = Bindings.createDoubleBinding(() -> {
            double w;
            double degrees = Math.abs(angle.get());
            double totalW = child.getWidth();

            w = degrees * (totalW / 180);

            return child.getLayoutX() + w;

        }, child.widthProperty(), child.layoutXProperty(), angle);

        endpointY = Bindings.createDoubleBinding(() -> {
            double radians = Math.toRadians(angle.get());
            double totalH = child.getHeight();

            double h = (totalH / 2) - (Math.sin(radians) * (totalH / 2));

            return child.getLayoutY() + h;

        }, child.heightProperty(), child.layoutYProperty(), angle);

        line.endXProperty().bind(endpointX);
        line.endYProperty().bind(endpointY);


        angle.addListener(_ -> System.out.println("Angle: " + angle.get()));

        head.rotateProperty().bind(angle);

        shapes.add(line);
        shapes.add(head);

        addOnMousePressed(_ -> System.out.println("Clicked!!"));
    }

    private void addOnMousePressed(EventHandler<MouseEvent> event){
        for(Node node : shapes){
            node.setOnMousePressed(e -> {
                event.handle(e);
                e.consume();
            });

            node.setOnMouseDragged(Event::consume);
        }
    }

}
