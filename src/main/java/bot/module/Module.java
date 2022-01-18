package bot.module;

public enum Module {
    SPAM_CONTROL(1, "Spam Control", "spam", "spamcontrol"),
    MASS_MENTION_CONTROL(2, "Mass Mention Control", "massmention", "massmentioncontrol"),
    LINK_CONTROL(3, "Link Control", "link", "linkcontrol"),
    SUGGESTION_LISTENER(4, "Suggestion Listener", "suggestion", "suggestions", "suggestionlistener", "suggestionslistener");

    private final int id;
    private final String name;
    private final String[] aliases;

    Module(int id, String name, String... aliases) {
        this.id = id;
        this.name = name;
        this.aliases = aliases;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }
}
