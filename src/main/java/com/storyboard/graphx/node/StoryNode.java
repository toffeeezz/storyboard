package com.storyboard.graphx.node;

import com.storyboard.graphx.Editor;
import com.storyboard.graphx.input.NodeDragging;
import com.storyboard.utils.Vector2;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for all node types in the editor.
 * Handles default mouse interaction (dragging, focus), keyboard input,
 * and position tracking in both pixel and world coordinate spaces.
 *
 * Subclasses define their own visual content and layout via FXML.
 */
public class StoryNode extends StackPane {

    private final Editor editor;

    /** Position in pixel space — top-left corner of the node relative to the worldPane. Bound to layoutX/Y. */
    protected Vector2 positionInPixel;

    /** Position in world space — center of the node relative to the canvas origin (0, 0). */
    protected Vector2 positionInWorld;

    /** The visual center offset of this node. Used to correctly place the node when spawning. */
    Vector2 origin;

    protected Vector2 center;

    private final List<StoryNode> children;

    public Editor getEditor()                { return editor; }
    public Vector2 getOrigin()               { return origin; }
    public List<StoryNode> getChildrenList() { return children; }

    /**
     * Returns positionInPixel negated so it can be used directly as a camera focus target.
     * The negation accounts for the inverted translation direction of the camera system.
     */
    public Vector2 getPositionInPixel() {
        return positionInPixel.multiplyBy(-1);
    }

    protected StoryNode(Editor editor) {
        this.editor   = editor;
        this.children = new ArrayList<>();
        setDefaults();
    }

    private void setDefaults() {
        setViewOrder(Editor.nodeViewOrder);

        setOnMousePressed(mouseEvent -> {
            if (editor.getCommandHandler().isActive()) return;

            // Start a node drag command and immediately pass the press event to it
            editor.getCommandHandler().start(new NodeDragging(this));
            editor.getCommandHandler().press(mouseEvent);

            requestFocus();
            mouseEvent.consume();
        });

        setOnMouseDragged(mouseEvent -> {
            editor.getCommandHandler().drag(mouseEvent);
            mouseEvent.consume();
        });

        setOnMouseReleased(editor.getCommandHandler()::release);
        setOnKeyPressed(this::onKeyPressed);

        // Toggle the focused style class and view order when focus enters or leaves this node or any of its children.
        // focusWithinProperty is used instead of focusedProperty so that clicking a child
        // (e.g. a TextField inside the node) does not trigger an unfocus on the node itself.
        focusWithinProperty().addListener((_, _, isFocused) -> {
            if (isFocused) {
                if (!getStyleClass().contains("focused"))
                    getStyleClass().add("focused");
                setViewOrder(Editor.nodeViewOrder - 1);
                editor.setSelectedNode(this);
            } else {
                getStyleClass().remove("focused");
                setViewOrder(Editor.nodeViewOrder);
            }
        });
    }

    /**
     * Syncs positionInPixel and positionInWorld with the node's current layout position.
     * Should be called any time the node is moved (relocated or dragged).
     *
     * positionInPixel is then bound to layoutX/Y so it stays up to date automatically
     * without needing to call this method again after every drag tick.
     */
    public void updatePosition() {
        Vector2 pixelPos = new Vector2(getLayoutX(), getLayoutY());

        positionInPixel = pixelPos;
        positionInWorld = new Vector2(
                pixelPos.getX() - editor.getPixelOrigin().getX(),
                editor.getPixelOrigin().getY() - pixelPos.getY()
        );

        positionInPixel.x.bind(layoutXProperty());
        positionInPixel.y.bind(layoutYProperty());
    }

    void onKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.DELETE)
            editor.removeNode(this);
    }
}