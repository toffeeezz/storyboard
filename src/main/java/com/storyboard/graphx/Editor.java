package com.storyboard.graphx;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Editor extends Pane {

    private final Pane spacePane = new Pane();
    Vector<Vector2> deltas = new Vector<>();

    protected Editor(){
        setBackground(Background.fill(Color.BLACK));
        spacePane.setBackground(Background.fill(Color.BLACK));
        spacePane.prefWidthProperty().bind(widthProperty());
        spacePane.prefHeightProperty().bind(heightProperty());

        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);

        setOnScroll(this::onMouseZoom);

        setOnMouseReleased(_ -> {
            deltas.clear();
        });

        setOnMousePressed(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.SECONDARY){
                QuickTool tool = new QuickTool();
                tool.setPosition(new Vector2(mouseEvent.getSceneX(), mouseEvent.getSceneY()));

                spacePane.getChildren().add(tool);

                System.out.println("Menu!");
            }
        });

        DialogueCard card = new DialogueCard();
        DialogueCard card2 = new DialogueCard();
        spacePane.getChildren().addAll(card2, card);
        card.relocate(100, 100);
        card.setParentNode(card2);

        getChildren().add(spacePane);

        drawLines();
    }

    private void drawLines(){

        List<Line> lines = new ArrayList<>();
        for(Node node : spacePane.getChildren()){
            if(!(node instanceof StoryNode card))
                continue;

            if(card.getParentNode() == null)
                continue;

            DialogueCard parent = (DialogueCard) card.getParentNode();

            Line line = new Line();

            line.startXProperty().bind(parent.layoutXProperty().add(parent.widthProperty().divide(2)));
            line.startYProperty().bind(parent.layoutYProperty().add(parent.heightProperty().divide(2)));

            line.endXProperty().bind(card.layoutXProperty().add(card.widthProperty().divide(2)));
            line.endYProperty().bind(card.layoutYProperty().add(card.heightProperty().divide(2)));

            line.setStroke(Color.WHITE);
            line.setStrokeWidth(10);
            line.setViewOrder(-9);
            lines.add(line);
        }

        spacePane.getChildren().addAll(lines);
    }

    private void onMousePressed(MouseEvent mouseEvent){
        if(mouseEvent.getButton() != MouseButton.PRIMARY)
            return;
        for(int i = 0; i < spacePane.getChildren().size(); i++) {

            Node node = spacePane.getChildren().get(i);
            if (!(node instanceof StoryNode card))
                continue;

            Vector2 delta = new Vector2();

            delta.x = card.getLayoutX() - mouseEvent.getSceneX();
            delta.y = card.getLayoutY() - mouseEvent.getSceneY();

            deltas.add(delta);
        }
    }

    private void onMouseDragged(MouseEvent mouseEvent) {

        for (int i = 0; i < spacePane.getChildren().size(); i++) {
            Node node = spacePane.getChildren().get(i);
            if (!(node instanceof StoryNode storyNode))
                continue;

            Vector2 delta = deltas.get(i);

            storyNode.setLayoutX(mouseEvent.getSceneX() + delta.x);
            storyNode.setLayoutY(mouseEvent.getSceneY() + delta.y);
        }

        System.out.println("Moving world space");
    }

    private void onMouseZoom(ScrollEvent scrollEvent){
        double zoomFactor = 0.1;

        if(scrollEvent.getDeltaY() < 0 && spacePane.getScaleX() > 0.3){
            spacePane.setScaleX(spacePane.getScaleX() - zoomFactor);
            spacePane.setScaleY(spacePane.getScaleY() - zoomFactor);
        }

        if(scrollEvent.getDeltaY() > 0 && spacePane.getScaleX() < 1.5){
            spacePane.setScaleX(spacePane.getScaleX() + zoomFactor);
            spacePane.setScaleY(spacePane.getScaleY() + zoomFactor);
        }
    }

    private StoryNode[] getAllNodes(){
        List<StoryNode> nodes = new ArrayList<>();

        for(Node node : getChildren()){
            if(!(node instanceof StoryNode))
                continue;

            nodes.add((StoryNode) node);
        }
        StoryNode[] nodeArray = new StoryNode[nodes.size()];
        for(int i = 0; i < nodes.size(); i++){
            nodeArray[i] = nodes.get(i);
        }
        return nodeArray;
    }

    private Line[] getAllLines(){
        List<Line> lines = new ArrayList<>();

        for(Node node : getChildren()){
            if(!(node instanceof Line))
                continue;

            lines.add((Line) node);
        }
        Line[] lineArray = new Line[lines.size()];
        for(int i = 0; i < lines.size(); i++){
            lineArray[i] = lines.get(i);
        }
        return lineArray;
    }
}

