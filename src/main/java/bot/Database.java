package bot;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Database extends ListenerAdapter {
    static Logger LOGGER = LoggerFactory.getLogger(Database.class);

    static MongoClient client;
    static MongoDatabase db;
    static MongoCollection<Document> reputationCollection;

    static HashMap<String, Integer> board = new HashMap<>();
    static HashMap<String, String> nameCache = new HashMap<>();

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

        new Thread(() ->
                reputationCollection.findOneAndUpdate(Filters.eq("_id", reputationClusterName),
                        Updates.set(id, reps))
        ).start();
        board.put(id, reps);

        LOGGER.info("Updated reputation of {} to {}", m.getUser().getAsTag(), reps);
    }

    public static int getReputation(Member m) {
        return board.computeIfAbsent(m.getId(), id -> 0);
    }

    public static List<List<BMember>> getMemberReputations() {
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(board.entrySet());
        entries.sort((a, b) -> (b.getValue()).compareTo(a.getValue()));

        int rank = 1;
        int lastValue = entries.get(0).getValue();
        List<BMember> fullList = new ArrayList<>();

        for (Map.Entry<String, Integer> e : entries) {
            Integer value = e.getValue();
            if (value != lastValue) rank++;

            BMember m = new BMember(rank, nameCache.getOrDefault(e.getKey(), "Unknown#0000"), value);
            fullList.add(m);
        }

        return IntStream.range(0, fullList.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i / 10))
                .values()
                .stream()
                .map(indices -> indices.stream().map(fullList::get).collect(Collectors.toList()))
                .collect(Collectors.toList());
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
                if (id.equals("_id")) continue;

                board.put(id, reputationTracker.getInteger(id));
            }
        }
        LOGGER.info("Board Cache Completed");

        event.getJDA().getUsers().forEach(user -> nameCache.put(user.getId(), user.getAsTag()));
        LOGGER.info("Name Cache Completed");
    }

    @Override
    public void onUserUpdateName(@NotNull UserUpdateNameEvent event) {
        updateName(event.getUser());
    }

    @Override
    public void onUserUpdateDiscriminator(@NotNull UserUpdateDiscriminatorEvent event) {
        updateName(event.getUser());
    }

    private void updateName(User user) {
        if (user.isBot()) return;
        nameCache.put(user.getId(), user.getAsTag());
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
    }

}
