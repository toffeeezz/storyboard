package com.storyboard.graphx;

import com.storyboard.constants.Settings;
import com.storyboard.graphx.input.CommandHandler;
import com.storyboard.graphx.input.CameraPanning;
import com.storyboard.utils.Vector2;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.List;

public class Editor extends Pane {

    public static final int nodeViewOrder = -5;
    public static final int arrowViewOrder = -15;
    public static final int lineViewOrder = -9;

    public Pane getWorldPane() {
        return worldPane;
    }

    private final Pane worldPane = new Pane();

    public Vector2 getPixelOrigin() {
        return pixelOrigin;
    }

    private final Vector2 pixelOrigin = new Vector2();
    public final Camera camera;

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    private final CommandHandler commandHandler;
    private final CameraPanning panningCommand;

    private final List<StoryNode> dialogueNodes = new ArrayList<>();
    private final ObjectProperty<StoryNode> selectedNode = new SimpleObjectProperty<>();
    private final ObjectProperty<ArrowLine> selectedArrow = new SimpleObjectProperty<>();

    public List<StoryNode> getNodes(){return dialogueNodes;}
    public Camera getCamera() {
        return camera;
    }

    public void setSelectedArrow(ArrowLine arrow){
        selectedArrow.set(arrow);
    }

    public ArrowLine getSelectedArrow(ArrowLine arrow){
        return selectedArrow.get();
    }

    public ObjectProperty<ArrowLine> selectedArrowProperty() {return  selectedArrow;}

    public ObjectProperty<StoryNode> selectedNodeProperty() {return selectedNode;}

    public StoryNode getSelectedNode() {
        return selectedNode.get();
    }

    public void setSelectedNode(StoryNode node) {
        selectedNode.set(node);
    }

    public Editor(){
        setPrefSize(Settings.windowWidth, Settings.windowHeight);
        getStyleClass().add("editor");
        worldPane.setPrefSize(10000, 10000);
        commandHandler = new CommandHandler();

        //Centralize everything
        pixelOrigin.setVector(worldPane.getPrefWidth() / 2, worldPane.getPrefHeight() / 2);
        camera = new Camera();

        worldPane.getTransforms().addAll(camera.translate, camera.scale);
        panningCommand = new CameraPanning(camera);

        //Origin Center
        Circle circle = new Circle(pixelOrigin.getX(), pixelOrigin.getY(), 12);
        circle.setFill(Color.RED);

        worldPane.getChildren().add(circle);
        focusedProperty().addListener(_ ->  selectedNode.set(null));

        DialogueNode card = new DialogueNode(this);
        DialogueNode card2 = new DialogueNode(this, card);
        DialogueNode card3 = new DialogueNode(this, card2);

        addNode(card, new Vector2(0, 200));
        addNode(card2, new Vector2(0, -200));
        addNode(card3, new Vector2(0, -400));

        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);
        setOnKeyPressed(this::onKeyPressed);
        setOnScroll(this::onScroll);

        getChildren().addAll(worldPane);
    }

    private void onMousePressed(MouseEvent event) {
        //Record the initial click position
        if(commandHandler.isActive()) return;

        if(event.getButton() == MouseButton.PRIMARY)
            commandHandler.start(panningCommand);

        commandHandler.press(event);
        requestFocus();
        event.consume();
    }

    private void onKeyPressed(KeyEvent event){

    }

    private void onMouseDragged(MouseEvent event){

        commandHandler.drag(event);
        event.consume();
    }

    private void onMouseReleased(MouseEvent event){
        commandHandler.release(event);
        commandHandler.end();
        event.consume();
    }

    private void onScroll(ScrollEvent event){
        double delta = event.getDeltaY();
        camera.zoom(delta, new Vector2(event.getX(), event.getY()));
        event.consume();
    }

    public void addNode(StoryNode node, Vector2 pos){
        worldPane.getChildren().add(node);

        if(node instanceof DialogueNode dialogueNode)
            dialogueNodes.add(dialogueNode);

        //Relocate the node to 'pos' after adding
        Vector2 spawn = new Vector2((pixelOrigin.getX() + pos.getX()) - node.origin.getX(), (pixelOrigin.getY() - pos.getY()) - node.origin.getY());
        node.relocate(spawn.getX(), spawn.getY());
        node.updatePosition();

    }
    public void drawArrowLines(ArrowLine line){
        worldPane.getChildren().addAll(line.shapes);
    }

    protected void removeNode(StoryNode node){

        worldPane.getChildren().remove(node);
        if(node instanceof DialogueNode dialogueNode)
            dialogueNodes.remove(dialogueNode);
    }

    public void removeArrow(ArrowLine arrow){
        worldPane.getChildren().removeAll(arrow.shapes);
    }




    public class Camera{


        public Vector2 position;

        public final Vector2 screenCenter;
        public final Scale scale;

        public double zoom;

        private final Translate translate;

        Camera() {
            this.screenCenter = new Vector2(getPrefWidth() / 2, getPrefHeight() / 2);

            Vector2 translateOrigin = new Vector2((getPrefWidth() / 2) - (worldPane.getPrefWidth() / 2), (getPrefHeight() / 2) - (worldPane.getPrefHeight() / 2));
            position = translateOrigin;

            translate = new Translate(translateOrigin.getX(), translateOrigin.getY());
            scale = new Scale(1, 1);
            scale.setPivotX(screenCenter.getX());
            scale.setPivotY(screenCenter.getY());
            zoom = scale.getX();
        }

        public void drag(Vector2 dir){
            translate.setY(dir.getY());
            translate.setX(dir.getX());

        }

        public void focus(Vector2 dir){
            Vector2 pos = Vector2.add(dir, screenCenter);
            translate.setY(pos.getY());
            translate.setX(pos.getX());
            camera.position = pos;

        }

        public void zoom(double delta, Vector2 mousePos){
            double zoomFactor = 1.1;

            //New scale becomes positive or negative based on scroll wheel direction
            double oldScale = worldPane.getScaleX();
            double newScale = (delta > 0) ? oldScale * zoomFactor : oldScale / zoomFactor;

            if (newScale < 0.3 || newScale > 5.0) return;

            double mouseX = mousePos.getX();
            double mouseY = mousePos.getY();



            // Adjust the translation to adjust for the scale change
            double f = (newScale / oldScale) - 1;
            double dx = (mouseX - (worldPane.getBoundsInLocal().getWidth() / 2 + worldPane.getTranslateX())) * f;
            double dy = (mouseY - (worldPane.getBoundsInLocal().getHeight() / 2 + worldPane.getTranslateY())) * f;

            worldPane.setScaleX(newScale);
            worldPane.setScaleY(newScale);

            worldPane.setTranslateX(worldPane.getTranslateX() - dx);
            worldPane.setTranslateY(worldPane.getTranslateY() - dy);
        }

        //Returns the position of the mouse relative to the screen
        public Vector2 getMouseScreenPos(InputEvent event){
            if(event instanceof MouseEvent mouseEvent)
                return Vector2.subtract(camera.screenCenter, new Vector2(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
            if(event instanceof ScrollEvent scrollEvent)
                return Vector2.subtract(camera.screenCenter, new Vector2(scrollEvent.getSceneX(), scrollEvent.getSceneY()));
            else{
                throw new RuntimeException("Invalid Input Event Passed");
            }
        }

        //Returns the position of the mouse relative to the offset of the editor pane
        public Vector2 getMouseWorldPos(InputEvent event){
            if(event instanceof MouseEvent mouseEvent) {
                Point2D worldPos = worldPane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                return new Vector2(worldPos.getX() - getPixelOrigin().getX(), -(worldPos.getY() - getPixelOrigin().getY()));
            }
            if(event instanceof ScrollEvent scrollEvent)
                return new Vector2(scrollEvent.getX(), scrollEvent.getY());
            else{
                throw new RuntimeException("Invalid Input Event Passed");
            }
        }

        //Returns the position of the mouse relative to the offset of the editor pane in pixel units
        public Vector2 getMousePixelPos(InputEvent event){
            if(event instanceof MouseEvent mouseEvent) {
                Point2D worldPos = worldPane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                return new Vector2(worldPos.getX(), worldPos.getY());
            }
            if(event instanceof ScrollEvent scrollEvent)
                return new Vector2(scrollEvent.getX(), scrollEvent.getY());
            else{
                throw new RuntimeException("Invalid Input Event Passed");
            }
        }
    }
}

