package bot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class Tools {
    public static HashMap<String, List<Warning>> memberToWarns = new HashMap<>();

    public static void wrongUsage(MessageChannel tc, Command c) {
        tc.sendMessage("Wrong Command Usage!\n" + c.getHelp()).queue();
    }

    public static Member getEffectiveMember(Guild g, String s) {
        Member m = g.getMemberById(s.replaceAll("[<@!>]", ""));
        if (m == null && User.USER_TAG.matcher(s).matches()) {
            m = g.getMemberByTag(s);
        }
        if (m == null) {
            m = g.getMembersByEffectiveName(s, true).get(0);
        }
        if (m == null) {
            m = g.getMembersByName(s, true).get(0);
        }
        return m;
    }

    public static void muteMember(Member m, long minutes, String reason) {
        m.timeoutFor(minutes, TimeUnit.MINUTES).reason(reason).queue();
        getWarns(m.getId()).add(new Warning(reason, System.currentTimeMillis()));
    }

    public static void unmuteMember(Member m) {
        m.removeTimeout().queue();
    }

    public static String secondsToTime(long seconds) {
        StringJoiner joiner = new StringJoiner(", ").setEmptyValue("**No time**");
        int years = (int) (seconds / (60 * 60 * 24 * 365));
        if (years > 0) {
            joiner.add("**" + years + "** years");
            seconds %= 60 * 60 * 24 * 365;
        }
        int weeks = (int) (seconds / (60 * 60 * 24 * 365));
        if (weeks > 0) {
            joiner.add("**" + weeks + "** weeks");
            seconds %= 60 * 60 * 24 * 7;
        }
        int days = (int) (seconds / (60 * 60 * 24));
        if (days > 0) {
            joiner.add("**" + days + "** days");
            seconds %= 60 * 60 * 24;
        }
        int hours = (int) (seconds / (60 * 60));
        if (hours > 0) {
            joiner.add("**" + hours + "** hours");
            seconds %= 60 * 60;
        }
        int minutes = (int) (seconds / 60);
        if (minutes > 0) {
            joiner.add("**" + minutes + "** minutes");
            seconds %= 60;
        }
        if (seconds > 0) {
            joiner.add("**" + seconds + "** seconds");
        }
        return joiner.toString();
    }

    public static int boolToBinary(boolean b) {
        return b ? 1 : 0;
    }

    public static List<Warning> getWarns(String id) {
        return memberToWarns.computeIfAbsent(id, x -> new ArrayList<>());
    }
}
