package Apps;

import Main.Controller;
import Main.MainController;

public class CommonResourcePaths {
    public final static String CONTROLS_DEFAULT_CSS = "/Controls/ControlsDefault.css";
    public final static String MAIN_DEFAULT_CSS  = "/Main/MainDefault.css";
    public final static String MAIN_HAPPY_THEME = "/Main/MainHappy.css";
    public final static String HEADER_HAPPY_THEME = "/Header/HeaderHappy.css";
    public final static String CONTROLS_HAPPY_THEME = "/Controls/ControlsHappy.css";
    public final static String MAIN_DARK_THEME = "/Main/MainDark.css";
    public final static String HEADER_DARK_THEME = "/Header/HeaderDark.css";
    public final static String CONTROLS_DARK_THEME = "/Controls/ControlsDark.css";
    public final static String SOUND_ON_IMG = "/Resources/sound.png";
    public final static String SOUND_OFF_IMG = "/Resources/nosound.png";
    public final static String RICK_IMG = "/Resources/rick.png";
    public final static String SKULL_IMG = "/Resources/skull.png";
    public final static String PHARRELL_IMG = "/Resources/pharrell.png";
    public final static String HAPPY_MUSIC = "/Resources/happy.mp3";
    public final static String DARK_MUSIC = "/Resources/bangarang.mp3";
    public final static String RICK_MUSIC = "/Resources/roll.mp3";


    public static String getBodyStylePath(Controller bodyController, MainController.eStyle style)
    {
        if(bodyController == null)
            return "";
        String path = "";
        String fullName = bodyController.getClass().getSimpleName();
        String name = fullName.substring(0, fullName.length() - "Controller".length());
        if(style == MainController.eStyle.Happy)
        {
            path = String.format("/Body/%s/%sHappy.css", name, name);
        }
        else if(style == MainController.eStyle.Dark)
        {
            path = String.format("/Body/%s/%sDark.css", name, name);
        }

        return path;
    }

}
