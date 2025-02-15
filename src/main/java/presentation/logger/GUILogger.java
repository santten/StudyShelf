package presentation.logger;

public class GUILogger {
    static boolean showWarnings = false;
    static boolean showInfo = false;

    public static void setWarVisibility(Boolean warningVisibility){
        showWarnings = warningVisibility;
    }

    public static void setInfoVisibility(Boolean infoVisibility){
        showInfo = infoVisibility;
    }

    public static void warn(String warning){
        if (showWarnings) {
            System.out.println("\033[33m[GUILogger WARNING] " + warning + "\033[0m");
        }
    }

    public static void info(String info){
        if (showInfo) {
            System.out.println("\033[36m[GUILogger INFO] " + info + "\033[0m");
        }
    }
}