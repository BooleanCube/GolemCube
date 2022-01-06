package bot.commands;

import bot.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class Toggle implements Command {
    @Override
    public String getCommand() {
        return "toggle";
    }

    @Override
    public String getHelp() {
        return "Enables a setting in the automoderator discord bot!\nUsage: `" + Constants.PREFIX + getCommand() + " [feature]`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        if(!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) return;
        if(args.size()!=1) { Tools.wrongUsage(event.getTextChannel(), this); return; }
        SettingType type = Settings.stringToType(args.get(0));
        if(type == null) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setDescription("That is not a valid setting to toggle!")
                    .setColor(Color.red)
                    .build()
            ).queue();
            return;
        }
        Settings.toggle(type);
        String status = Settings.isEnabled(type) ? "on" : "off";
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setDescription(args.get(0) + " was toggled `" + status + "`")
                .build()
        ).queue();
    }
}
