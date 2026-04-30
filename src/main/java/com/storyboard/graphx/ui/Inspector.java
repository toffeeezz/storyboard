package com.storyboard.graphx.ui;

import com.storyboard.graphx.StoryNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Inspector extends VBox {

    @FXML private Label parentLabel;
    @FXML private Label nameLabel;

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
            String parentName = node.getParentNode() != null ? node.getParentNode().getId() : "";

            nameLabel.setText(name);
            parentLabel.setText(parentName);
        }else{
            nameLabel.setText("");
            parentLabel.setText("");
        }
    }
}
