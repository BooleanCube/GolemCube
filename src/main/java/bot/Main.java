package bot;

import bot.listeners.LinkControl;
import bot.listeners.MassMentionControl;
import bot.listeners.SpamControl;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA AutoModerator = JDABuilder.createDefault(Secrets.TOKEN)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.watching("over this cube village! | g!help"))
                .addEventListeners(new Listener())
                .addEventListeners(new SpamControl())
                .addEventListeners(new MassMentionControl())
                .addEventListeners(new LinkControl())
                .build().awaitReady();
    }
}
