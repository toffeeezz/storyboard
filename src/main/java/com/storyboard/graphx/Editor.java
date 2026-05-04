package com.storyboard.graphx;

import com.storyboard.constants.Settings;
import com.storyboard.graphx.input.CommandHandler;
import com.storyboard.graphx.input.CameraPanning;
import com.storyboard.graphx.node.DialogueNode;
import com.storyboard.graphx.node.StoryNode;
import com.storyboard.graphx.node.comp.ArrowLine;
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

/**
 * The main editor pane that hosts the infinite canvas, camera system, and all story nodes.
 * It manages input routing via {@link CommandHandler} and acts as the central hub
 * for node and arrow management.
 *
 * The editor uses a 10000x10000 worldPane as its canvas. The camera works by applying
 * {@link Translate} and {@link Scale} transforms to the worldPane — moving the camera
 * means moving the world in the opposite direction.
 */
public class Editor extends Pane {

    public static final int nodeViewOrder  = -5;
    public static final int arrowViewOrder = -15;
    public static final int lineViewOrder  = -9;

    /** The infinite scrollable canvas that holds all story nodes and arrows. */
    private final Pane worldPane = new Pane();

    /**
     * The origin point in pixel space — corresponds to world coordinate (0, 0).
     * Nodes are positioned relative to this point using {@link #addNode}.
     */
    private final Vector2 pixelOrigin = new Vector2();

    /**
     * Routes all mouse interactions to the currently active {@link com.storyboard.graphx.input.Command}.
     * Only one command can be active at a time.
     */
    private final CommandHandler commandHandler;

    private final List<StoryNode> dialogueNodes = new ArrayList<>();
    private final ObjectProperty<StoryNode> selectedNode = new SimpleObjectProperty<>();
    private final ObjectProperty<ArrowLine> selectedArrow = new SimpleObjectProperty<>();

    public final Camera camera;

    public Pane getWorldPane()                               { return worldPane; }
    public Vector2 getPixelOrigin()                          { return pixelOrigin; }
    public CommandHandler getCommandHandler()                { return commandHandler; }
    public Camera getCamera()                                { return camera; }
    public List<StoryNode> getNodes()                        { return dialogueNodes; }
    public ObjectProperty<StoryNode> selectedNodeProperty()  { return selectedNode; }
    public ObjectProperty<ArrowLine> selectedArrowProperty() { return selectedArrow; }
    public StoryNode getSelectedNode()                       { return selectedNode.get(); }
    public ArrowLine getSelectedArrow(ArrowLine arrow)       { return selectedArrow.get(); }
    public void setSelectedNode(StoryNode node)              { selectedNode.set(node); }
    public void setSelectedArrow(ArrowLine arrow)            { selectedArrow.set(arrow); }

    public Editor() {
        setPrefSize(Settings.windowWidth, Settings.windowHeight);
        getStyleClass().add("editor");
        worldPane.setPrefSize(10000, 10000);
        commandHandler = new CommandHandler();

        pixelOrigin.setVector(worldPane.getPrefWidth() / 2, worldPane.getPrefHeight() / 2);
        camera = new Camera();

        worldPane.getTransforms().addAll(camera.translate, camera.scale);

        Circle circle = new Circle(pixelOrigin.getX(), pixelOrigin.getY(), 12);
        circle.setFill(Color.RED);
        worldPane.getChildren().add(circle);

        // Deselect the active node when the editor canvas itself gains focus
        focusedProperty().addListener(_ -> selectedNode.set(null));

        DialogueNode card  = new DialogueNode(this);
        DialogueNode card2 = new DialogueNode(this);
        DialogueNode card3 = new DialogueNode(this);

        addNode(card,  new Vector2(0,  200));
        addNode(card2, new Vector2(0, -200));
        addNode(card3, new Vector2(0, -400));

        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);
        setOnScroll(this::onScroll);

        getChildren().addAll(worldPane);
    }

    private void onMousePressed(MouseEvent event) {
        if (commandHandler.isActive()) return;

        if (event.getButton() == MouseButton.PRIMARY)
            commandHandler.start(new CameraPanning(camera));

        commandHandler.press(event);
        requestFocus();
        event.consume();
    }

    private void onMouseDragged(MouseEvent event) {
        commandHandler.drag(event);
        event.consume();
    }

    private void onMouseReleased(MouseEvent event) {
        commandHandler.release(event);
        event.consume();
    }

    private void onScroll(ScrollEvent event) {
        camera.zoom(event.getDeltaY(), new Vector2(event.getX(), event.getY()));
        event.consume();
    }

    /**
     * Registers a port circle as a valid drop target for node linking.
     * Forwards drag enter, exit, and release events to the active command
     * so {@link com.storyboard.graphx.input.NodeLinking} can react to them.
     * Called once per port when a {@link com.storyboard.graphx.node.comp.DialogueEntry} is created.
     *
     * @param endPort the port circle to register as a connection target
     */
    public void registerLinkPort(Circle endPort) {
        endPort.setOnMouseDragEntered(e -> commandHandler.dropEnter(e, endPort));
        endPort.setOnMouseDragExited(e -> commandHandler.dropExited(e, endPort));
        endPort.setOnMouseDragReleased(e -> commandHandler.dropReleased(e, endPort));
    }

    /**
     * Adds a node to the world canvas and positions it relative to the pixel origin.
     * pos uses world coordinates where (0, 0) is the canvas center.
     * The node's own origin offset is subtracted so it appears centered at the target position.
     *
     * @param node the story node to add
     * @param pos  world-space position to spawn the node at
     */
    public void addNode(StoryNode node, Vector2 pos) {
        worldPane.getChildren().add(node);

        if (node instanceof DialogueNode dialogueNode)
            dialogueNodes.add(dialogueNode);

        Vector2 spawn = new Vector2(
                (pixelOrigin.getX() + pos.getX()) - node.getOrigin().getX(),
                (pixelOrigin.getY() - pos.getY()) - node.getOrigin().getY()
        );
        node.relocate(spawn.getX(), spawn.getY());
        node.updatePosition();
    }

    public void drawArrowLines(ArrowLine line) {
        worldPane.getChildren().addAll(line.shapes);
    }

    public void removeNode(StoryNode node) {
        worldPane.getChildren().remove(node);
        if (node instanceof DialogueNode dialogueNode)
            dialogueNodes.remove(dialogueNode);
    }

    public void removeArrow(ArrowLine arrow) {
        worldPane.getChildren().removeAll(arrow.shapes);
    }

    // -------------------------------------------------------------------------
    // Camera
    // -------------------------------------------------------------------------

    /**
     * Responsible for all camera behavior: panning, zooming, and converting
     * mouse coordinates between screen space and world space.
     *
     * Both the {@link Translate} and {@link Scale} transforms are added directly to
     * worldPane's transform list. This avoids the snapping issue caused by mixing
     * setTranslateX/Y with getTransforms().
     */
    public class Camera {

        public Vector2 position;
        public final Vector2 screenCenter;
        public final Scale scale;
        public double zoom;
        private final Translate translate;

        Camera() {
            this.screenCenter = new Vector2(getPrefWidth() / 2, getPrefHeight() / 2);

            // Initial translation centers the worldPane so that
            // pixelOrigin (5000, 5000) aligns with the screen center.
            Vector2 translateOrigin = new Vector2(
                    (getPrefWidth()  / 2) - (worldPane.getPrefWidth()  / 2),
                    (getPrefHeight() / 2) - (worldPane.getPrefHeight() / 2)
            );

            position  = translateOrigin;
            translate = new Translate(translateOrigin.getX(), translateOrigin.getY());
            scale     = new Scale(1, 1);
            scale.setPivotX(screenCenter.getX());
            scale.setPivotY(screenCenter.getY());
            zoom = scale.getX();
        }

        /** Moves the camera by updating the Translate transform directly. */
        public void drag(Vector2 dir) {
            translate.setX(dir.getX());
            translate.setY(dir.getY());
        }

        /**
         * Focuses the camera on a pixel position by computing the translation
         * needed to bring that point to the screen center.
         */
        public void focus(Vector2 dir) {
            Vector2 pos = Vector2.add(dir, screenCenter);
            translate.setX(pos.getX());
            translate.setY(pos.getY());
            camera.position = pos;
        }

        /**
         * Scales the worldPane around the mouse cursor to produce a zoom-to-cursor effect.
         * After scaling, the translation is adjusted so the point under the cursor stays fixed.
         *
         * @param delta    scroll delta — positive zooms in, negative zooms out
         * @param mousePos screen-local position of the mouse cursor
         */
        public void zoom(double delta, Vector2 mousePos) {
            double zoomFactor = 1.1;
            double oldScale   = worldPane.getScaleX();
            double newScale   = (delta > 0) ? oldScale * zoomFactor : oldScale / zoomFactor;

            if (newScale < 0.3 || newScale > 5.0) return;

            double mouseX = mousePos.getX();
            double mouseY = mousePos.getY();

            // f is the fractional scale change. dx/dy corrects the translation so the
            // point under the cursor stays visually fixed after the scale is applied.
            double f  = (newScale / oldScale) - 1;
            double dx = (mouseX - (worldPane.getBoundsInLocal().getWidth()  / 2 + worldPane.getTranslateX())) * f;
            double dy = (mouseY - (worldPane.getBoundsInLocal().getHeight() / 2 + worldPane.getTranslateY())) * f;

            worldPane.setScaleX(newScale);
            worldPane.setScaleY(newScale);
            worldPane.setTranslateX(worldPane.getTranslateX() - dx);
            worldPane.setTranslateY(worldPane.getTranslateY() - dy);
        }

        /** Returns the mouse position relative to the screen center. Used for panning calculations. */
        public Vector2 getMouseScreenPos(InputEvent event) {
            if (event instanceof MouseEvent mouseEvent)
                return Vector2.subtract(camera.screenCenter, new Vector2(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
            if (event instanceof ScrollEvent scrollEvent)
                return Vector2.subtract(camera.screenCenter, new Vector2(scrollEvent.getSceneX(), scrollEvent.getSceneY()));
            else
                throw new RuntimeException("Invalid Input Event Passed");
        }

        /**
         * Returns the mouse position in world coordinates where (0, 0) is the canvas center.
         * Y is inverted so positive Y points upward, matching a standard coordinate system.
         */
        public Vector2 getMouseWorldPos(InputEvent event) {
            if (event instanceof MouseEvent mouseEvent) {
                Point2D worldPos = worldPane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                return new Vector2(
                        worldPos.getX() - getPixelOrigin().getX(),
                        -(worldPos.getY() - getPixelOrigin().getY())
                );
            }
            if (event instanceof ScrollEvent scrollEvent)
                return new Vector2(scrollEvent.getX(), scrollEvent.getY());
            else
                throw new RuntimeException("Invalid Input Event Passed");
        }

        /**
         * Returns the mouse position in raw pixel coordinates relative to the worldPane.
         * Unlike {@link #getMouseWorldPos}, the Y axis is not inverted here.
         */
        public Vector2 getMousePixelPos(InputEvent event) {
            if (event instanceof MouseEvent mouseEvent) {
                Point2D worldPos = worldPane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                return new Vector2(worldPos.getX(), worldPos.getY());
            }
            if (event instanceof ScrollEvent scrollEvent)
                return new Vector2(scrollEvent.getX(), scrollEvent.getY());
            else
                throw new RuntimeException("Invalid Input Event Passed");
        }
    }
}