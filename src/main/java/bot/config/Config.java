package bot.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

public interface Config {

    @Order(1)
    @ConfKey("token")
    @ConfDefault.DefaultString("TmV2ZXIgZ29ubmEgZ2l2ZSB5b3U.gdXAKTmV2ZXIgZ29ubmEgbGV0IHlvdSBkb3du")
    @ConfComments("The Token Of the bot. Default String is encoded to Base64.")
    String token();

    @Order(2)
    @ConfKey("database-uri")
    @ConfDefault.DefaultString("mongodb+srv://BooleanCube:sussy@cube.91qk1.mongodb.net/mySecondDatabase")
    @ConfComments("The mongo db atlas url")
    String databaseURI();

    @Order(3)
    @ConfKey("prefix")
    @ConfDefault.DefaultString("g!")
    String prefix();

    @Order(3)
    @ConfKey("owner-id")
    @ConfDefault.DefaultString("525126007330570259")
    @ConfComments("The owner of this bot.")
    String ownerID();

    @Order(4)
    @ConfKey("main-server-id")
    @ConfDefault.DefaultString("740316079523627128")
    @ConfComments("The server which the bot will handle.")
    String mainServerID();

    @ConfKey("suggestion-channel-id")
    @ConfDefault.DefaultString("901832454904614953")
    @ConfComments("The suggestion channel id. If there is none leave it to -1.")
    String suggestionChannelID();

    @ConfComments("These are the blacklist for spam control")

    @Order(6)
    @ConfKey("channel-spam-blacklist")
    @ConfDefault.DefaultStrings({"768793442632990721", "741785877944074251"})
    List<String> channelSpamBlacklist();

    @Order(7)
    @ConfKey("role-spam-blacklist")
    @ConfDefault.DefaultStrings("773337238952083477")
    List<String> roleSpamBlacklist();

    @Order(8)
    @ConfKey("member-spam-blacklist")
    @ConfDefault.DefaultStrings("No one can Spam 😈")
    List<String> memberSpamBlacklist();

}
