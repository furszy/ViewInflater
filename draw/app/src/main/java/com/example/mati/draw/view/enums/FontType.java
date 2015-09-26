package com.example.mati.draw.view.enums;

/**
 * Font enum type
 *
 * @author Francisco Vasquez
 * @version 1.0
 */
public enum FontType {

    CAVIAR_DREAMS("fonts/CaviarDreams.ttf"),
    CAVIAR_DREAMS_BOLD("fonts/Caviar Dreams Bold.ttf"),
    CAVIAR_DREAMS_ITALIC("fonts/CaviarDreams_Italic.ttf"),
    CAVIAR_DREAMS_ITALIC_BOLD("fonts/CaviarDreams_BoldItalic.ttf");

    private String path;

    FontType(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
