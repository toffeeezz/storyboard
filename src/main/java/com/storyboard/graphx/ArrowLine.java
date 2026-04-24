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

    public final List<Node> shapes = new ArrayList<>();

    protected final StoryNode parent;
    protected final StoryNode node;


    public ArrowLine(double arrowSize, StoryNode parent, StoryNode node){
        this.parent = parent;
        this.node = node;



        //Create the line and bind each point from the parent to the child
        Line line = new Line();
        line.startXProperty().bind(parent.layoutXProperty().add(parent.widthProperty().divide(2)));
        line.startYProperty().bind(parent.layoutYProperty().add(parent.heightProperty().divide(2)));
        line.endXProperty().bind(node.layoutXProperty().add(node.widthProperty().divide(2)));
        line.endYProperty().bind(node.layoutYProperty().add(node.heightProperty().divide(2)));
        line.setStrokeWidth(5);
        line.setStroke(Color.WHITE);
        line.setViewOrder(-11);


        Polygon head = new Polygon(
                0, 0,        //Tip
                -arrowSize, -arrowSize / 2,    // Left
                -arrowSize, arrowSize / 2      // Right
        );

        head.layoutXProperty().bind(line.endXProperty().add(arrowSize / 2));
        head.layoutYProperty().bind(line.endYProperty());
        head.setFill(Color.WHITE);
        head.setViewOrder(-11);

        //Create the hypotenuse line
        Vector2 hypPoints = new Vector2();
        hypPoints.x.bind(line.endXProperty().subtract(line.startXProperty()));
        hypPoints.y.bind(line.endYProperty().subtract(line.startYProperty()));
        hypLen = Bindings.createDoubleBinding(() -> {
            double dx = hypPoints.x.get();
            double dy = hypPoints.y.get();
            return Math.sqrt(dx * dx + dy * dy);
        }, hypPoints.x, hypPoints.y);


        angle = Bindings.createDoubleBinding(() -> {
            double dx = hypPoints.x.get();
            double dy = hypPoints.y.get();

            double radians = Math.atan2(dy, dx);
            return Math.toDegrees(radians);
        }, hypPoints.x, hypPoints.y);

        hypLen.addListener(_ -> System.out.println("Len: " + hypLen.get()));

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
