package com.storyboard.graphx;


import com.storyboard.utils.Vector2;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class DialogueEntry extends VBox {

    private String character;
    private String dialogue;
    private DialogueNode parent;
    private ArrowLine arrow;

    public String getDialogue() {
        dialogue = dialogueField.getText();
        return dialogue;
    }

    public String getCharacter() {
        character = characterField.getText();
        return character;
    }



    @FXML private FontIcon deleteButton;
    @FXML private TextField characterField;
    @FXML private TextArea dialogueField;
    @FXML private Circle startPort;
    @FXML private Circle endPort;

    public DialogueEntry(DialogueNode dialogueNode){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/DialogueEntry.fxml"));

        loader.setRoot(this);
        loader.setController(this);


        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dialogue = dialogueField.getText();
        character = characterField.getText();
        parent = dialogueNode;


        deleteButton.setCursor(Cursor.HAND);
        startPort.setCursor(Cursor.HAND);

        setStartPort();

        deleteButton.setOnMousePressed(_ -> dialogueNode.removeEntry(this));
    }


    private void setStartPort(){

        Vector2 center = new Vector2();
        center.x.bind(Bindings.createDoubleBinding(() -> {
            Point2D scenePos = startPort.localToScene(startPort.getCenterX(), startPort.getCenterY());
            Point2D worldPos = parent.getEditor().getWorldPane().sceneToLocal(scenePos);
            return worldPos.getX();
        }, startPort.layoutXProperty(), parent.layoutXProperty()));

        center.y.bind(Bindings.createDoubleBinding(() -> {
            Point2D scenePos = startPort.localToScene(startPort.getCenterX(), startPort.getCenterY());
            Point2D worldPos = parent.getEditor().getWorldPane().sceneToLocal(scenePos);
            return worldPos.getY();
        }, startPort.layoutYProperty(), parent.layoutYProperty()));
        startPort.setOnDragDetected(_ -> startPort.startFullDrag());

        startPort.setOnMouseDragged(e -> {

            Vector2 mousePos = parent.getEditor().camera.getMousePixelPos(e);

            if(arrow == null) {
                arrow = new ArrowLine(20, center, mousePos);
                parent.getEditor().drawArrowLines(arrow);
            }

            arrow.bindEndpoint(mousePos);
            e.consume();
        });

        startPort.setOnMouseEntered(_ -> startPort.getStyleClass().add("hover"));
        startPort.setOnMouseExited(_ -> startPort.getStyleClass().remove("hover"));
        startPort.setOnMousePressed(e -> {
            startPort.getStyleClass().remove("hover");
            startPort.getStyleClass().add("pressed");

            System.out.println(center);
            System.out.println(parent.getEditor().camera.getMousePixelPos(e));

        });

        startPort.setOnMouseReleased(e -> {
            startPort.getStyleClass().remove("pressed");
            parent.getEditor().removeArrow(arrow);
            arrow = null;
        });
    }
}
