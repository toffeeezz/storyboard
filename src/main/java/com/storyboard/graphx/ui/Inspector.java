package com.storyboard.graphx.ui;

import com.storyboard.graphx.node.StoryNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.function.Consumer;

public class Inspector extends VBox {

    @FXML private Label parentLabel;
    @FXML private Label nameLabel;

    private Consumer<StoryNode> onParentLabelPressed;

    protected Inspector(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storyboard/graphx/ui/Inspector.fxml"));

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void showProperties(StoryNode node, boolean toggle){
        if(toggle){
            String name = node.getId();

            node.idProperty().addListener(_ -> {
                String newName = node.getId();
                nameLabel.setText(newName);
            });
            nameLabel.setText(name);
        }else{
            nameLabel.setText("");
        }
    }

    protected void setOnParentLabelPressed(Consumer<StoryNode> callback){
        this.onParentLabelPressed = callback;
    }


}
