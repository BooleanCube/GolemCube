package bot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Tools {

    public static HashMap<Member, List<Warning>> memberToWarns = new HashMap<>();

    public static void wrongUsage(MessageChannel tc, Command c) {
        tc.sendMessage("Wrong Command Usage!\n" + c.getHelp()).queue();
    }

    public static Member getEffectiveMember(Guild g, String s) {
        Member m;
        try {
            long id = Long.parseLong(s.replaceAll("[<@!>]", ""));
            m = g.getMemberById(id);
        } catch (Exception e) {
            try {
                m = g.getMemberByTag(s);
            } catch (Exception e2) {
                try {
                    m = g.getMembersByEffectiveName(s, true).get(0);
                } catch (Exception e3) {
                    try {
                        m = g.getMembersByName(s, true).get(0);
                    } catch (Exception e4) {
                        return null;
                    }
                }
            }
        }
        return m;
    }

    public static void muteMember(Member m, Guild g, String reason) {
        if (!memberToWarns.containsKey(m)) {
            ArrayList<Warning> warning = new ArrayList<>();
            warning.add(new Warning(reason, System.currentTimeMillis()));
            memberToWarns.put(m, warning);
        } else memberToWarns.get(m).add(new Warning(reason, System.currentTimeMillis()));
    }

    // TODO:
    public static void unmuteMember(Member m, Guild g) {
        Role r = g.getRoleById(741287382757933206L);

        if (m.getRoles().stream().noneMatch(it -> it.getIdLong() == r.getIdLong())) {
            return;
        }

        g.removeRoleFromMember(m, r).queue();
    }

    public static String secondsToTime(long timeseconds) {
        StringBuilder builder = new StringBuilder();
        int years = (int) (timeseconds / (60 * 60 * 24 * 365));
        if (years > 0) {
            builder.append("**").append(years).append("** years, ");
            timeseconds = timeseconds % (60 * 60 * 24 * 365);
        }
        int weeks = (int) (timeseconds / (60 * 60 * 24 * 365));
        if (weeks > 0) {
            builder.append("**").append(weeks).append("** weeks, ");
            timeseconds = timeseconds % (60 * 60 * 24 * 7);
        }
        int days = (int) (timeseconds / (60 * 60 * 24));
        if (days > 0) {
            builder.append("**").append(days).append("** days, ");
            timeseconds = timeseconds % (60 * 60 * 24);
        }
        int hours = (int) (timeseconds / (60 * 60));
        if (hours > 0) {
            builder.append("**").append(hours).append("** hours, ");
            timeseconds = timeseconds % (60 * 60);
        }
        int minutes = (int) (timeseconds / (60));
        if (minutes > 0) {
            builder.append("**").append(minutes).append("** minutes, ");
            timeseconds = timeseconds % (60);
        }
        if (timeseconds > 0)
            builder.append("**").append(timeseconds).append("** seconds");
        String str = builder.toString();
        if (str.endsWith(", "))
            str = str.substring(0, str.length() - 2);
        if (str.equals(""))
            str = "**No time**";
        return str;
    }

    public static int boolToBinary(boolean b) {
        return b ? 1 : 0;
    }

}
