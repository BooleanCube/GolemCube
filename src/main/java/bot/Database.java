package bot;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Database extends ListenerAdapter {
    static Logger LOGGER = LoggerFactory.getLogger(Database.class);

    static MongoClient client;
    static MongoDatabase db;
    static MongoCollection<Document> reputationCollection;

    static HashMap<String, Integer> board = new HashMap<>();

    static String reputationClusterName = "ReputationTracker";

    public static void setupDatabase() {
        client = MongoClients.create(Secrets.DatabaseURI);
        db = client.getDatabase("BooleanCube");

        if (!db.listCollectionNames().into(new ArrayList<>()).contains(reputationClusterName)) {
            db.createCollection(reputationClusterName);
            LOGGER.info("Created {} collection", reputationClusterName);
        }

        reputationCollection = db.getCollection(reputationClusterName);

        if (reputationCollection.countDocuments(Filters.eq("_id", reputationClusterName)) == 0) {
            reputationCollection.insertOne(new Document().append("_id", reputationClusterName));
            LOGGER.info("Created {} in {} collection", reputationClusterName, reputationClusterName);
        }
    }

    public static void addReputation(Member m) {
        String id = m.getId();
        int reps = board.computeIfAbsent(id, a -> 0) + 1;

        reputationCollection.findOneAndUpdate(Filters.eq("_id", reputationClusterName),
                Updates.set(id, reps));
        board.put(id, reps);

        LOGGER.info("Updated reputation of {} to {}", m.getUser().getAsTag(), reps);
    }

    public static int getReputation(Member m) {
        return board.computeIfAbsent(m.getId(), id -> 0);
    }

    public static String getReputationLB(Guild g, Member m) {
        // TODO:
        /*
        ArrayList<Integer> points = new ArrayList<>();

        List<Member> members = g.getMembers();
        for (Member s : members) {
            if (s.getUser().isBot()) continue;
            int sPoints = Database.getReputation(s);
            points.add(sPoints);
        }
        Collections.sort(points);
        Collections.reverse(points);
        StringBuilder lb = new StringBuilder();
        outer:
        for (int i = 0; i < 10; i++) {
            if (i > points.size()) break;
            while (board.get(points.get(i)).size() > 0) {
                lb.append("**#").append(i + 1).append(":** ").append(board.get(points.get(i)).remove(0).getUser().getAsTag()).append(" (").append(points.get(i)).append(" points)").append("\n");
                if (lb.toString().split("\\r?\\n").length >= 10) break outer;
            }
        }
        lb.append("\n").append("**#").append(Database.getReputationRank(g, m)).append(": ").append(m.getUser().getAsTag()).append("**");
        return lb.toString();
        */
        return "";
    }

    public static int getReputationRank(Guild g, Member m) {
        // TODO:

        ArrayList<Integer> points = new ArrayList<>();
        List<Member> members = g.getMembers();
        for (Member s : members) {
            if (s.getUser().isBot()) continue;
            points.add(Database.getReputation(s));
        }
        return points.indexOf(Database.getReputation(m)) + 1;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        //cache the database in a hashmap which is then updated frequently in a separate thread
        //this was done for optimization, because retrieving data from a cloud based database is going to take forever
        try (MongoCursor<Document> cursor =
                     reputationCollection.find(Filters.eq("_id", reputationClusterName)).iterator()) {
            Document reputationTracker = cursor.next();

            for (String id : reputationTracker.keySet()) {
                board.put(id, reputationTracker.getInteger(id));
            }
        }
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
    }

}
