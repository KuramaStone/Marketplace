package com.github.kuramastone.marketplace.database;

import com.github.kuramastone.marketplace.Marketplace;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoService {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoService(String connectionString, String dbName) {
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(dbName);
    }

    /**
     * Checks if the connection to the MongoDB server is established.
     *
     * @return True if the connection is active
     */
    public boolean connectOrThrow() throws MongoCommandException {
        try {
            // Perform a ping to check the connection
            database.runCommand(new Document("ping", 1));
            return true; // Connection is active
        }
        catch (MongoCommandException e) {
            Marketplace.logger.severe("A database error occured.");
            throw e;
        }
        catch (Exception e) {
            Marketplace.logger.severe("An unexpected error occured.");
            throw e;
        }
    }

    /**
     * Close the MongoDB client connection
     */
    public void close() {
        mongoClient.close();
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
