package com.storyboard.graphx;

import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;

public class DialogueCard extends StoryNode {

    DialogueCard(){
        setPrefSize(300, 100);
        setBackground(Background.fill(Color.BLACK));
        setBorder(Border.stroke(Color.WHITE));

    }
}
