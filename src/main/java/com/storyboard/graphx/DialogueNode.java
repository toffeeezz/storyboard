package com.storyboard.graphx;

import com.storyboard.utils.Vector2;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;

import java.io.IOException;


public class DialogueNode extends StoryNode {

    private final double defaultW = 300;
    private final double defaultH = 200;

   @FXML private TextField textField;
   @FXML private AnchorPane topBar;

    public DialogueNode(){
       setDefault();
    }

    public DialogueNode(StoryNode parent){
        setParentNode(parent);
        parent.addChildren(this);
        setDefault();
    }




    private void setDefault(){
        setPrefSize(defaultW, defaultH);
        setBackground(Background.fill(Color.BLACK));
        setBorder(Border.stroke(Color.WHITE));
        getStyleClass().add("dialogue-card");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/DialogueNode.fxml"));

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(textField.getText());

        origin = new Vector2(this.getPrefWidth() / 2, this.getPrefHeight() / 2);
    }

    private void onKeyPressed(KeyEvent event){

        if(event.getCode() == KeyCode.ENTER && event.isShiftDown()){

            //Check if children node exists before proceeding
            if(children.isEmpty())
                return;


        }else if(event.getCode() == KeyCode.ENTER && event.isControlDown()){
            if(parentNode == null)
                return;

        }

        event.consume();
    }
}
