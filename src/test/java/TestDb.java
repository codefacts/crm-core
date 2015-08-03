import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.io.IOException;

/**
 * Created by sohan on 8/1/2015.
 */
public class TestDb {
    public static void main() throws IOException {
        final Vertx vertx = Vertx.vertx();
        MongoClient mongoClient = MongoClient.createShared(vertx, new JsonObject().put("db_name", "test_db"));
        mongoClient.createCollection("region", r -> {

        });
        System.in.read();
    }
}
