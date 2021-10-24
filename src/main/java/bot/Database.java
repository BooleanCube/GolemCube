package bot;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Database {

    static MongoClient client = MongoClients.create(Secrets.DatabaseURI);
    static MongoDatabase db = client.getDatabase("BooleanCube");
    static MongoCollection<Document> reputationCollection = db.getCollection("ReputationTracker");

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

    public static int getReputationPoints(Member m) {
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

    public static int getReputationRank(Member m) {

        return -1;
    }

}
