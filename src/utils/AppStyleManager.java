package utils;

import javafx.scene.Scene;

public class AppStyleManager {

    private static boolean largeFont = false;
    private static boolean tahomaFont = false;
    private static boolean darkTheme = false;

    public static void toggleFontSize(Scene scene) {
        largeFont = !largeFont;
        applyStyles(scene);
    }

    public static void toggleFontFamily(Scene scene) {
        tahomaFont = !tahomaFont;
        applyStyles(scene);
    }

    public static void toggleTheme(Scene scene) {
        darkTheme = !darkTheme;
        applyStyles(scene);
    }

    public static void applyStyles(Scene scene) {
        scene.getRoot().getStyleClass().removeAll(
                "large-font",
                "tahoma-font",
                "dark-theme"
        );

        if (largeFont) {
            scene.getRoot().getStyleClass().add("large-font");
        }

        if (tahomaFont) {
            scene.getRoot().getStyleClass().add("tahoma-font");
        }

        if (darkTheme) {
            scene.getRoot().getStyleClass().add("dark-theme");
        }
    }
}