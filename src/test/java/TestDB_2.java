import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sohan on 8/1/2015.
 */
public class TestDB_2 {
    public static void main(String... args) {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase test_db = mongoClient.getDatabase("test_db");
        MongoCollection<Document> region = test_db.getCollection("region");
        region.insertMany(Arrays.asList(
                new Document()
                        .append("name", "dhaka")
                        .append("phone", Arrays.asList(1, 2, 3, new Document("ph", "as")))
                        .append("pic", new byte[]{1, 2, 3, 4}),
                new Document()
                        .append("name", "dhaka")
                        .append("phone", Arrays.asList(new byte[]{1, 2, 3, 4}, 1, 2, 3, new Document("ph", "as")))
                        .append("pic", new byte[]{1, 2, 3, 4})
        ));

        mongoClient.close();
    }
}
