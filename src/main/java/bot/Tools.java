package bot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Tools {

    public static HashMap<Member, List<Warning>> memberToWarns = new HashMap<>();

    public static void wrongUsage(TextChannel tc, Command c) {
        tc.sendMessage("Wrong Command Usage!\n" + c.getHelp()).queue();
    }
    public static void muteMember(Member m, Guild g, int durationSeconds, String reason) {
        if(!memberToWarns.containsKey(m)) {
            ArrayList<Warning> warning = new ArrayList<>();
            warning.add(new Warning(reason, System.currentTimeMillis()));
            memberToWarns.put(m, new ArrayList<>());
        } else {
            memberToWarns.get(m).add(new Warning(reason, System.currentTimeMillis()));
        }
        Role r = g.getRoleById(741287382757933206L);
        assert r != null;
        if(durationSeconds > -1) {
            g.addRoleToMember(m, r).queue();
            g.removeRoleFromMember(m, r).queueAfter(durationSeconds, TimeUnit.SECONDS);
            for (TextChannel textChannel : g.getTextChannels()) {
                textChannel.putPermissionOverride(m).deny(Permission.MESSAGE_WRITE).queue();
                textChannel.putPermissionOverride(m).grant(Permission.MESSAGE_WRITE).queueAfter(durationSeconds, TimeUnit.SECONDS);
            }
        } else {
            g.addRoleToMember(m, r).queue();
            for (TextChannel textChannel : g.getTextChannels()) {
                textChannel.putPermissionOverride(m).deny(Permission.MESSAGE_WRITE).queue();
            }
        }
    }
}
