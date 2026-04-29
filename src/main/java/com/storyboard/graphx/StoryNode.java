package com.storyboard.graphx;

import com.storyboard.utils.Vector2;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class StoryNode extends StackPane {

    private final Editor editor;

    public ArrowLine getArrowLine() {
        return arrowLine;
    }

    private ArrowLine arrowLine;

    private StoryNode parentNode;


    protected Vector2 positionInWorld; //Already at the center of the node
    protected Vector2 positionInPixel;

    public List<StoryNode> getChildrenList() {
        return children;
    }

    private final List<StoryNode> children;
    protected Vector2 origin;

    Vector2 lastMousePos;

    protected StoryNode(Editor editor){
        this.editor = editor;
        this.parentNode = null;
        this.arrowLine = null;
        children = new ArrayList<>();
        setDefaults();
    }

    protected StoryNode(Editor editor, StoryNode parentNode){
        this.editor = editor;
        this.parentNode = parentNode;
        this.arrowLine = new ArrowLine(20, parentNode, this);
        parentNode.addChildren(this);
        children = new ArrayList<>();
        setDefaults();
    }

    private void setDefaults(){
        setViewOrder(Editor.nodeViewOrder);
        setOnMousePressed(mouseEvent -> {
            lastMousePos = new Vector2(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            requestFocus();
            mouseEvent.consume();
        });
        setOnMouseDragged(mouseEvent -> {
            double currScale = Editor.worldPane.getScaleX();

            Vector2 rawDelta = Vector2.subtract(new Vector2(mouseEvent.getSceneX(), mouseEvent.getSceneY()), lastMousePos);
            Vector2 scaledDelta = rawDelta.divideBy(currScale);

            Vector2 moveDir = Vector2.add(positionInPixel, scaledDelta);
            setLayoutX(moveDir.x.get());
            setLayoutY(moveDir.y.get());
            updatePosition();

            lastMousePos = new Vector2(mouseEvent.getSceneX(), mouseEvent.getSceneY());

            mouseEvent.consume();
        });
        setOnKeyPressed(this::onKeyPressed);


        focusWithinProperty().addListener((_, _, isFocused) -> {
            if(isFocused){
                if(!getStyleClass().contains("focused"))
                    getStyleClass().add("focused");
                setViewOrder(Editor.nodeViewOrder - 1);
            }else{
                getStyleClass().remove("focused");
                setViewOrder(Editor.nodeViewOrder);
            }
        });


    }

    protected void setParentNode(StoryNode node){
        if(node == null) {
            arrowLine = null;
            return;
        }
        parentNode = node;
        arrowLine = new ArrowLine(20, parentNode, this);
    }
    public StoryNode getParentNode(){return parentNode;}

    protected void addChildren(StoryNode node){children.add(node);}

    protected void updatePosition() {

        Vector2 pixelPos = new Vector2(getLayoutX(), getLayoutY());

        positionInPixel = pixelPos;
        positionInWorld = new Vector2(pixelPos.x.get() - editor.getPixelOrigin().x.get(), editor.getPixelOrigin().y.get() - pixelPos.y.get());

        positionInPixel.x.bind(layoutXProperty());
        positionInPixel.y.bind(layoutYProperty());
    }

    protected void onKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.DELETE) {
            editor.removeNode(this);



            if(!isChild() && isParent()){
                for (StoryNode node : children) {
                    node.setParentNode(null);
                    editor.removeArrow(node.arrowLine);
                }
            }else if(isChild() && isParent()){
                parentNode.children.remove(this);
                for(StoryNode node : children){
                    editor.removeArrow(node.arrowLine);
                    node.setParentNode(parentNode);
                    editor.drawArrowLines(node.getArrowLine());
                }
                parentNode.children.remove(this);
                editor.removeArrow(arrowLine);
            }else if(isChild() && !isParent()){
                parentNode.children.remove(this);
                editor.removeArrow(arrowLine);
            }
        }
        System.out.println("delete");
    }

    protected boolean isChild(){return parentNode != null;}
    protected boolean isParent(){return !children.isEmpty();}
}
