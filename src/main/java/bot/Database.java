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

import java.util.*;

public class Database extends ListenerAdapter {
    static MongoClient client;
    static MongoDatabase db;
    static MongoCollection<Document> reputationCollection;

    static HashMap<Integer, ArrayList<Member>> board = new HashMap<>();

    static String reputationClusterName = "ReputationTracker";

    public static void setupDatabase() {
        client = MongoClients.create(Secrets.DatabaseURI);
        db = client.getDatabase("BooleanCube");

        if (db.listCollectionNames().first() == null) {  // Only one collection so yea.
            db.createCollection(reputationClusterName);
            reputationCollection.insertOne(new Document().append("_id", reputationClusterName));
        }

        reputationCollection = db.getCollection(reputationClusterName);
    }

    public static void addReputation(Member m) {
        int reps = 1;
        try (MongoCursor<Document> cursor = reputationCollection.find(Filters.eq("_id", reputationClusterName)).iterator()) {
            Document reputationTracker = cursor.next();
            reps = reputationTracker.getInteger(m.getId()) + 1;
        } catch (NullPointerException ignored) {
        }

        reputationCollection.findOneAndUpdate(Filters.eq("_id", reputationClusterName), Updates.set(m.getId(), reps));
    }

    public static int getDBPoints(Member m) {
        try (MongoCursor<Document> cursor = reputationCollection.find().iterator()) {
            Document reputationTracker = cursor.next();
            return reputationTracker.getInteger(m.getId());
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public static String getReputationLB(Guild g, Member m) {
        ArrayList<Integer> points = new ArrayList<>();

        List<Member> members = g.getMembers();
        for (Member s : members) {
            if (s.getUser().isBot()) continue;
            int sPoints = Database.getReputationPoints(s);
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
    }

    public static int getReputationRank(Guild g, Member m) {
        ArrayList<Integer> points = new ArrayList<>();
        List<Member> members = g.getMembers();
        for (Member s : members) {
            if (s.getUser().isBot()) continue;
            points.add(Database.getReputationPoints(s));
        }
        return points.indexOf(Database.getReputationPoints(m)) + 1;
    }

    public static int getReputationPoints(Member m) {
        for (int k : board.keySet()) if (board.get(k).contains(m)) return k;
        return -1;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        //cache the database in a hashmap which is then updated frequently in a separate thread
        //this was done for optimization, because retrieving data from a cloud based database is going to take forever
        List<Member> members = Objects.requireNonNull(event.getJDA().getGuildById("740316079523627128")).getMembers();
        for (Member s : members) {
            if (s.getUser().isBot()) continue;
            int points = Database.getDBPoints(s);
            if (board.containsKey(points)) board.get(points).add(s);
            else {
                ArrayList<Member> a = new ArrayList<>();
                a.add(s);
                board.put(points, a);
            }
        }
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        //update the database as the bot goes offline

    }

}
