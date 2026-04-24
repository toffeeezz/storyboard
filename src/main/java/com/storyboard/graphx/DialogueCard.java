package com.storyboard.graphx;

import com.storyboard.utils.Vector2;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;

public class DialogueCard extends StoryNode {


    DialogueCard(){
        setPrefSize(300, 100);
        setBackground(Background.fill(Color.BLACK));
        setBorder(Border.stroke(Color.WHITE));

        origin = new Vector2(this.getPrefWidth() / 2, this.getPrefHeight() / 2);
    }

    DialogueCard(StoryNode parent){
        setPrefSize(300, 100);
        setBackground(Background.fill(Color.BLACK));
        setBorder(Border.stroke(Color.WHITE));
        setParentNode(parent);
        parent.addChildren(this);

        origin = new Vector2(this.getPrefWidth() / 2, this.getPrefHeight() / 2);
    }
}
