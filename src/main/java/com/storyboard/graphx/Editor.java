package com.storyboard.graphx;

import com.storyboard.constants.Settings;
import com.storyboard.utils.Vector2;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.List;

public class Editor extends Pane {

    protected static Pane worldPane = new Pane();

    public static Vector2 pixelOrigin;
    public static Vector2 screenCenter;


    public static Vector2 mousePos;
    public static Vector2 cameraPos;

    public static Vector2 translateOrigin;

    private Vector2 initClickPos;
    private Vector2 finalClickPos;

    protected static double zoom;

    private final Translate camTranslate;
    private final Scale camScale;

    private final List<ArrowLine> arrowLines = new ArrayList<>();

    protected Editor(){
        setBackground(Background.fill(Color.BLACK));
        setPrefSize(Settings.windowWidth, Settings.windowHeight);
        worldPane.setBackground(Background.fill(Color.BLACK));
        worldPane.setPrefSize(10000, 10000);

        //Centralize everything
        pixelOrigin = new Vector2(worldPane.getPrefWidth() / 2, worldPane.getPrefHeight() / 2);
        screenCenter = new Vector2(getPrefWidth() / 2, getPrefHeight() / 2);
        translateOrigin = new Vector2((this.getPrefWidth() / 2) - (worldPane.getPrefWidth() / 2), (this.getPrefHeight() / 2) - (worldPane.getPrefHeight() / 2));

        cameraPos = translateOrigin;
        camTranslate = new Translate(translateOrigin.x.get(), translateOrigin.y.get());
        camScale = new Scale(1, 1);
        camScale.setPivotX(screenCenter.x.get());
        camScale.setPivotY(screenCenter.y.get());
        zoom = camScale.getX();

        worldPane.getTransforms().addAll(camTranslate, camScale);

        //Origin Center
        Circle circle = new Circle(pixelOrigin.x.get(), pixelOrigin.y.get(), 12);
        circle.setFill(Color.RED);

        worldPane.getChildren().add(circle);

        DialogueCard card = new DialogueCard();
        DialogueCard card2 = new DialogueCard(card);
        DialogueCard card3 = new DialogueCard(card2);
        DialogueCard card4 = new DialogueCard(card2);

        addNode(card, new Vector2(0, 200));
        addNode(card2, new Vector2(0, -200));
        addNode(card3, new Vector2(100, -200));
        addNode(card4, new Vector2(-100, -200));
        card.setParentNode(card2);

        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);
        setOnScroll(this::onScroll);

        getChildren().addAll(worldPane);
    }

    private void onMousePressed(MouseEvent event) {
        //Record the initial click position
        initClickPos = Vector2.subtract(screenCenter, new Vector2(event.getSceneX(), event.getSceneY()));

        event.consume();
    }

    private void onMouseDragged(MouseEvent event){
        //Get the distance between initial click position and current position
        mousePos = Vector2.subtract(screenCenter, new Vector2(event.getSceneX(), event.getSceneY()));
        Vector2 moveDir = new Vector2(initClickPos.x.get() - mousePos.x.get(),  mousePos.y.get() - initClickPos.y.get());
        Vector2 translateDir = new Vector2(moveDir.x.get() + cameraPos.x.get(),  cameraPos.y.get() - moveDir.y.get());

        //Apply the difference
        camTranslate.setY(translateDir.y.get());
        camTranslate.setX(translateDir.x.get());
        finalClickPos = translateDir;

        event.consume();
    }

    private void onMouseReleased(MouseEvent event){
        if (finalClickPos != null) cameraPos = finalClickPos;
    }

    private void onScroll(ScrollEvent event){
        double zoomFactor = 1.1;
        double delta = event.getDeltaY();

        double oldScale = worldPane.getScaleX();
        double newScale = (delta > 0) ? oldScale * zoomFactor : oldScale / zoomFactor;

        if (newScale < 0.3 || newScale > 5.0) return;

        double mouseX = event.getX();
        double mouseY = event.getY();

        // Adjust the translation to adjust for the scale change
        double f = (newScale / oldScale) - 1;
        double dx = (mouseX - (worldPane.getBoundsInLocal().getWidth() / 2 + worldPane.getTranslateX())) * f;
        double dy = (mouseY - (worldPane.getBoundsInLocal().getHeight() / 2 + worldPane.getTranslateY())) * f;

        worldPane.setScaleX(newScale);
        worldPane.setScaleY(newScale);

        worldPane.setTranslateX(worldPane.getTranslateX() - dx);
        worldPane.setTranslateY(worldPane.getTranslateY() - dy);

        event.consume();
    }

    protected void addNode(StoryNode node, Vector2 pos){
        worldPane.getChildren().add(node);

        //Relocate the node to 'pos' after adding
        Vector2 spawn = new Vector2((pixelOrigin.x.get() + pos.x.get()) - node.origin.x.get(), (pixelOrigin.y.get() - pos.y.get()) - node.origin.y.get());
        node.relocate(spawn.x.get(), spawn.y.get());
        node.updatePosition();
        System.out.println(node.positionInPixel);
        StoryNode parent = node.getParentNode();
        if(parent == null)
            return;

        ArrowLine arrow = new ArrowLine(20, parent, node);
        arrowLines.add(arrow);
        worldPane.getChildren().addAll(arrow.shapes);
    }
}

