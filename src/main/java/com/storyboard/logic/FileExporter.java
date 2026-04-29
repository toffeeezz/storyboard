package com.storyboard.logic;

import com.storyboard.graphx.DialogueNode;
import com.storyboard.graphx.StoryNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileExporter {

    private StringBuilder sb;
    private int dialogueIndex;
    private String export;

    private void formatToSBoard(DialogueNode node){

        List<Dialogue> dialogueList = node.getDialogues();
        for(Dialogue dialogue : dialogueList){
            export = FileTemplates.SB_DIALOGUE_TEMPLATE.replace("{speaker}", dialogue.getSpeaker())
                    .replace("{index}", String.valueOf(dialogueIndex))
                    .replace("{text}", dialogue.getText());
            sb.append(export);
            dialogueIndex++;
        }

        for(StoryNode child : node.getChildrenList()){
            if(child instanceof DialogueNode dialogueNode){
                formatToSBoard(dialogueNode);
            }
        }
    }

    public void exportSBoard(DialogueNode node, String filename) throws IOException {
        sb = new StringBuilder();
        dialogueIndex = 0;

        formatToSBoard(node);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename))){
            bw.write(sb.toString());
            System.out.println("File Saved!!");
        }
    }
}

final class FileTemplates{
    public static final String SB_DIALOGUE_TEMPLATE =
                    "[NODE]\n" +
                    "index={index}\n" +
                    "speaker={speaker}\n" +
                    "text={text}\n";
}

