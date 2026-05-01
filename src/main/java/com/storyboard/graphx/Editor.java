package com.storyboard.graphx;

import com.storyboard.constants.Settings;
import com.storyboard.logic.FileExporter;
import com.storyboard.utils.Vector2;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Editor extends Pane {

    public static final int nodeViewOrder = -9;
    public static final int arrowViewOrder = -15;
    public static final int lineViewOrder = -5;

    protected Pane worldPane = new Pane();

    public Vector2 getPixelOrigin() {
        return pixelOrigin;
    }

    private final Vector2 pixelOrigin = new Vector2();
    public final Camera camera;

    private Vector2 initClickPos;
    private Vector2 finalClickPos;


    private final List<DialogueNode> dialogueNodes = new ArrayList<>();
    private final ObjectProperty<StoryNode> selectedNode = new SimpleObjectProperty<>();


    public Camera getCamera() {
        return camera;
    }

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


        //Centralize everything
        pixelOrigin.setVector(worldPane.getPrefWidth() / 2, worldPane.getPrefHeight() / 2);
        camera = new Camera();

        worldPane.getTransforms().addAll(camera.translate, camera.scale);

        //Origin Center
        Circle circle = new Circle(pixelOrigin.x.get(), pixelOrigin.y.get(), 12);
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
        initClickPos = camera.getMouseScreenPos(event);
        requestFocus();
        event.consume();
    }

    private void onKeyPressed(KeyEvent event){
        if(event.getCode() == KeyCode.S && event.isControlDown()){
            try {
                new FileExporter().exportSBoard(dialogueNodes.stream().filter(n -> !n.isChild()).findFirst().orElse(null), "SaveData");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            event.consume();
        }
    }

    private void onMouseDragged(MouseEvent event){
        //Get the distance between initial click position and current position
        Vector2 mousePos = camera.getMouseScreenPos(event);
        Vector2 moveDir = new Vector2(initClickPos.x.get() - mousePos.x.get(),  mousePos.y.get() - initClickPos.y.get());
        Vector2 translateDir = new Vector2(moveDir.x.get() + camera.position.x.get(),  camera.position.y.get() - moveDir.y.get());

        //Apply the difference
        camera.drag(translateDir);
        finalClickPos = translateDir;

        event.consume();
    }

    private void onMouseReleased(MouseEvent event){
        if (finalClickPos != null) camera.position = finalClickPos;
    }

    private void onScroll(ScrollEvent event){
        double delta = event.getDeltaY();
        camera.zoom(delta, new Vector2(event.getX(), event.getY()));
        event.consume();
    }

    protected void addNode(StoryNode node, Vector2 pos){
        worldPane.getChildren().add(node);

        if(node instanceof DialogueNode dialogueNode)
            dialogueNodes.add(dialogueNode);

        //Relocate the node to 'pos' after adding
        Vector2 spawn = new Vector2((pixelOrigin.x.get() + pos.x.get()) - node.origin.x.get(), (pixelOrigin.y.get() - pos.y.get()) - node.origin.y.get());
        node.relocate(spawn.x.get(), spawn.y.get());
        node.updatePosition();
        StoryNode parent = node.getParentNode();
        if(parent == null)
            return;
        drawArrowLines(node.getArrowLine());

    }
    protected void drawArrowLines(ArrowLine line){
        worldPane.getChildren().addAll(line.shapes);
    }

    protected void removeNode(StoryNode node){

        worldPane.getChildren().remove(node);
        if(node instanceof DialogueNode dialogueNode)
            dialogueNodes.remove(dialogueNode);
    }

    protected void removeArrow(ArrowLine arrow){
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

            translate = new Translate(translateOrigin.x.get(), translateOrigin.y.get());
            scale = new Scale(1, 1);
            scale.setPivotX(screenCenter.x.get());
            scale.setPivotY(screenCenter.y.get());
            zoom = scale.getX();
        }

        public void drag(Vector2 dir){
            translate.setY(dir.y.get());
            translate.setX(dir.x.get());
            System.out.println(camera.position);
        }

        public void focus(Vector2 dir){
            Vector2 pos = Vector2.add(dir, screenCenter);
            translate.setY(pos.y.get());
            translate.setX(pos.x.get());
            camera.position = pos;
            System.out.println(camera.position);
        }

        public void zoom(double delta, Vector2 mousePos){
            double zoomFactor = 1.1;

            //New scale becomes positive or negative based on scroll wheel direction
            double oldScale = worldPane.getScaleX();
            double newScale = (delta > 0) ? oldScale * zoomFactor : oldScale / zoomFactor;

            if (newScale < 0.3 || newScale > 5.0) return;

            double mouseX = mousePos.x.get();
            double mouseY = mousePos.y.get();

            System.out.println("Mouse X: " + mouseX);

            // Adjust the translation to adjust for the scale change
            double f = (newScale / oldScale) - 1;
            double dx = (mouseX - (worldPane.getBoundsInLocal().getWidth() / 2 + worldPane.getTranslateX())) * f;
            double dy = (mouseY - (worldPane.getBoundsInLocal().getHeight() / 2 + worldPane.getTranslateY())) * f;

            worldPane.setScaleX(newScale);
            worldPane.setScaleY(newScale);

            worldPane.setTranslateX(worldPane.getTranslateX() - dx);
            worldPane.setTranslateY(worldPane.getTranslateY() - dy);
        }
        public Vector2 getMouseScreenPos(InputEvent event){
            if(event instanceof MouseEvent mouseEvent)
                return Vector2.subtract(camera.screenCenter, new Vector2(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
            if(event instanceof ScrollEvent scrollEvent)
                return Vector2.subtract(camera.screenCenter, new Vector2(scrollEvent.getSceneX(), scrollEvent.getSceneY()));
            else{
                throw new RuntimeException("Invalid Input Event Passed");
            }
        }
    }
}

