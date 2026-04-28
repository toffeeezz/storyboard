package com.storyboard.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Vector2 {
    public DoubleProperty x = new SimpleDoubleProperty(), y = new SimpleDoubleProperty();

    public Vector2(double x, double y){
        this.x.set(x);
        this.y.set(y);
    }

    public Vector2(){

    }

    public void setVector(double x, double y){
        this.x.set(x);
        this.y.set(y);
    }

    public static Vector2 subtract(Vector2 a, Vector2 b){
        return new Vector2(a.x.get() - b.x.get(), a.y.get() - b.y.get());
    }

    public static Vector2 add(Vector2 a, Vector2 b){
        return new Vector2(a.x.get() + b.x.get(), a.y.get() + b.y.get());
    }

    public Vector2 divideBy(double b){
        return new Vector2(x.get() /b, y.get() / b);
    }

    @Override
    public String toString() {
        return "Vector2(x " + x.get() + ", " + y.get() + " )";
    }
}
