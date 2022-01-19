package bot.database;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Database extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    private static final HashMap<String, Integer> board = new HashMap<>();
    private static final HashMap<String, String> nameCache = new HashMap<>();
    private static final String reputationClusterName = "ReputationTracker";
    private static final Bson filter = Filters.eq("_id", reputationClusterName);
    private static MongoClient client;
    private static MongoDatabase db;
    private static MongoCollection<Document> reputationCollection;

    public static void setupDatabase(String DatabaseURI) {
        client = MongoClients.create(DatabaseURI);
        db = client.getDatabase("BooleanCube");

        if (!db.listCollectionNames().into(new ArrayList<>()).contains(reputationClusterName)) {
            db.createCollection(reputationClusterName);
            LOGGER.info("Created {} collection", reputationClusterName);
        }

        reputationCollection = db.getCollection(reputationClusterName);

        if (reputationCollection.countDocuments(filter) == 0) {
            reputationCollection.insertOne(new Document("_id", reputationClusterName));
            LOGGER.info("Created {} in {} collection", reputationClusterName, reputationClusterName);
        }

        LOGGER.info("Connected to MongoDB");
    }

    public static void addReputation(Member m) {
        String id = m.getId();
        Integer reps = getReputation(m) + 1;
        executor.execute(() -> reputationCollection.findOneAndUpdate(filter, Updates.set(id, 1)));
        board.put(id, reps);
        LOGGER.info("Updated reputation of {} to {}", m.getUser().getAsTag(), reps);
    }

    public static int getReputation(Member m) {
        return board.computeIfAbsent(m.getId(), id -> 0);
    }

    public static ReputationsResult getMemberReputationsWithUser(User user) {
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(board.entrySet());
        entries.sort((a, b) -> (b.getValue()).compareTo(a.getValue()));

        int rank = 1;
        int lastValue = entries.get(0).getValue();
        List<ReputationsResult.BMember> fullList = new ArrayList<>();

        ReputationsResult.BMember member = new ReputationsResult.BMember(-1, user.getAsTag(), 0);

        for (Map.Entry<String, Integer> e : entries) {
            Integer value = e.getValue();
            if (value != lastValue) rank++;

            ReputationsResult.BMember m = new ReputationsResult.BMember(rank, nameCache.getOrDefault(e.getKey(), "Unknown#0000"), value);

            if (e.getKey().equals(user.getId())) member = m;

            fullList.add(m);
        }

        return new ReputationsResult(member, IntStream.range(0, fullList.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i / 10))
                .values()
                .stream()
                .map(indices -> indices.stream().map(fullList::get).collect(Collectors.toList()))
                .collect(Collectors.toList()));
    }

    public static int getReputationRank(Member m) {
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(board.entrySet());
        entries.sort((a, b) -> (b.getValue()).compareTo(a.getValue()));

        int rank = 1;
        int lastValue = entries.get(0).getValue();

        for (Map.Entry<String, Integer> e : entries) {
            Integer value = e.getValue();
            if (value != lastValue) rank++;

            if (e.getKey().equals(m.getId())) break;
        }

        return rank;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        //cache the database in a hashmap which is then updated frequently in a separate thread
        //this was done for optimization, because retrieving data from a cloud based database is going to take forever
        try (MongoCursor<Document> cursor = reputationCollection.find(filter).iterator()) {
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
}
