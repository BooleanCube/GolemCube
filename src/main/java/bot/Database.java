//simply overcomplicated, might as well use the regular
//optimizing a cloud based database using data structures is useless..

package bot;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Database extends ListenerAdapter {

    static MongoClient client = MongoClients.create(Secrets.DatabaseURI);
    static MongoDatabase db = client.getDatabase("BooleanCube");
    static MongoCollection<Document> reputationCollection = db.getCollection("ReputationTracker");
    static HashMap<Integer, ArrayList<Member>> board = new HashMap<>();

    public static void addReputation(Member m) {
        MongoCursor<Document> cursor = reputationCollection.find(Filters.eq("_id", "ReputationTracker")).iterator();
        Document reputationTracker = null;
        try {
            reputationTracker = cursor.next();
            int reps = reputationTracker.getInteger(m.getId()) + 1;
            reputationCollection.findOneAndUpdate(Filters.eq("_id", "ReputationTracker"), Updates.set(m.getId(), reps));
        }
        catch(NullPointerException npe) {
            reputationCollection.findOneAndUpdate(Filters.eq("_id", "ReputationTracker"), Updates.set(m.getId(), 1));
        }
        finally { cursor.close(); }
    }

    public static int getDBPoints(Member m) {
        MongoCursor<Document> cursor = reputationCollection.find(Filters.eq("_id", "ReputationTracker")).iterator();
        Document reputationTracker = null;
        try {
            reputationTracker = cursor.next();
            return reputationTracker.getInteger(m.getId());
        }
        catch(NullPointerException npe) {
            return 0;
        }
        finally { cursor.close(); }
    }

    public static String getReputationLB(Guild g, Member m) {
        ArrayList<Integer> points = new ArrayList<>();

        List<Member> members = g.getMembers();
        for(Member s : members) {
            if(s.getUser().isBot()) continue;
            int sPoints = Database.getReputationPoints(s);
            points.add(sPoints);
        }
        Collections.sort(points);
        Collections.reverse(points);
        StringBuilder lb = new StringBuilder();
        outer:
        for(int i=0; i<10; i++) {
            if(i>points.size()) break;
            while(board.get(points.get(i)).size() > 0) {
                lb.append("**#").append(i + 1).append(":** ").append(board.get(points.get(i)).remove(0).getUser().getAsTag()).append(" (").append(points.get(i)).append(" points)").append("\n");
                if(lb.toString().split("\\r?\\n").length >= 10) break outer;
            }
        }
        lb.append("\n").append("**#").append(Database.getReputationRank(g, m)).append(": ").append(m.getUser().getAsTag()).append("**");
        return lb.toString();
    }

    public static int getReputationRank(Guild g, Member m) {
        ArrayList<Integer> points = new ArrayList<>();
        List<Member> members = g.getMembers();
        for(Member s : members) {
            if(s.getUser().isBot()) continue;
            points.add(Database.getReputationPoints(s));
        }
        return points.indexOf(Database.getReputationPoints(m))+1;
    }

    public static int getReputationPoints(Member m) {
        for(int k : board.keySet()) if(board.get(k).contains(m)) return k;
        return -1;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        //store the entire database in a hashmap, which will be updated regularly and we won't be updating the database that often
        //this was done for optimization, because retrieving data from a cloud based database is going to take forever
        List<Member> members = Objects.requireNonNull(event.getJDA().getGuildById("740316079523627128")).getMembers();
        for(Member s : members) {
            if(s.getUser().isBot()) continue;
            int points = Database.getDBPoints(s);
            if(board.containsKey(points)) board.get(points).add(s);
            else {
                ArrayList<Member> a = new ArrayList<>(); a.add(s);
                board.put(points, a);
            }
        }
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        //update the database as the bot goes offline

    }
}
