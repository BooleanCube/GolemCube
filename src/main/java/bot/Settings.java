package bot;

public class Settings {

    //all settings enabled by defualt
    private static boolean spamControl = true;
    private static boolean massMentionControl = true;
    private static boolean linkControl = true;
    private static boolean suggestionListener = true;

    public static void toggle(SettingType type) {
        if(type == SettingType.SpamControl) spamControl = !spamControl;
        if(type == SettingType.MassMentionControl) massMentionControl = !massMentionControl;
        if(type == SettingType.LinkControl) linkControl = !linkControl;
        if(type == SettingType.SuggestionListener) suggestionListener = !suggestionListener;
    }

    public static boolean isEnabled(SettingType type) {
        if(type == SettingType.SpamControl) return spamControl;
        if(type == SettingType.MassMentionControl) return massMentionControl;
        if(type == SettingType.LinkControl) return linkControl;
        if(type == SettingType.SuggestionListener) return suggestionListener;
        return false;
    }

    public static SettingType stringToType(String typeName) {
        if(typeName.equalsIgnoreCase("spamcontrol") ||
                typeName.equalsIgnoreCase("spam") ||
                typeName.equalsIgnoreCase("spam control")) return SettingType.SpamControl;
        if(typeName.equalsIgnoreCase("massmentioncontrol") ||
                typeName.equalsIgnoreCase("mass mention control") ||
                typeName.equalsIgnoreCase("mass mention") ||
                typeName.equalsIgnoreCase("massmention")) return SettingType.MassMentionControl;
        if(typeName.equalsIgnoreCase("linkcontrol") ||
                typeName.equalsIgnoreCase("link control") ||
                typeName.equalsIgnoreCase("link")) return SettingType.LinkControl;
        if(typeName.equalsIgnoreCase("suggestionlistener") ||
                typeName.equalsIgnoreCase("suggestion listener") ||
                typeName.equalsIgnoreCase("suggestion")) return SettingType.SuggestionListener;
        return null;
    }

}

