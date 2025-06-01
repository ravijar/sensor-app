package com.littlebits.sensorapp.model;

public enum ActivityLabel {
    BIKING("biking"),
    DOWNSTAIRS("downstairs"),
    JOGGING("jogging"),
    SITTING("sitting"),
    UPSTAIRS("upstairs"),
    STANDING("standing"),
    WALKING("walking");

    private final String label;

    ActivityLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ActivityLabel fromIndex(int index) {
        return ActivityLabel.values()[index];
    }
}
