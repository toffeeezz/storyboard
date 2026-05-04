package com.storyboard.graphx.input;

import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

import java.util.logging.Logger;

public class CommandHandler {
    private Command activeCommand = null;
    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());

    public boolean isActive(){ return activeCommand != null;}

    public void start(Command command){
        if(activeCommand != null) {
            logger.warning("Command already active: " + activeCommand.getClass().getSimpleName()
                            + "\nUnable to activate: " + command.getClass().getSimpleName());
            return;
        }

        activeCommand = command;
    }

    public void press(MouseEvent e){
        if(activeCommand == null) return;
        activeCommand.onPress(e);
    }

    public void release(MouseEvent e){
        if(activeCommand == null) return;
        activeCommand.onReleased(e);
        activeCommand = null;
    }

    public void drag(MouseEvent e){
        if(activeCommand == null) return;
        activeCommand.onDragged(e);
    }

    public void hover(MouseEvent e){
        if(activeCommand == null) return;
        activeCommand.onHover(e);

    }

    public void enter(MouseEvent e){
        if (!isActive()) return;
        activeCommand.onEnter(e);
    }

    public void exit(MouseEvent e){
        if (!isActive()) return;
        activeCommand.onExit(e);
    }

    public void dragEnter(MouseDragEvent e){
        if (!isActive()) return;
        activeCommand.onDragEntered(e);
    }

    public void dragExit(MouseDragEvent e){
        if (!isActive()) return;
        activeCommand.onDragExited(e);
    }

    public void end(){
        if(!isActive()) return;
        activeCommand.end();
        activeCommand = null;
    }
}
