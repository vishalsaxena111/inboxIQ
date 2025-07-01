// EmailTrainingData.java
package com.inboxiq.ml;

public class EmailTrainingData {
    private String text;
    private int label;

    public EmailTrainingData(String text, int label) {
        this.text = text;
        this.label = label;
    }

    public String getText() { return text; }
    public int getLabel() { return label; }
}
