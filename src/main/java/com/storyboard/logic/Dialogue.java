package com.storyboard.logic;

public final class Dialogue {
    private String speaker;
    private String text;


    public String getSpeaker() {
        return speaker;
    }

    public String getText() {
        return text;
    }


    public Dialogue(String speaker, String text){
        this.speaker = speaker;
        this.text = text;
    }
}
