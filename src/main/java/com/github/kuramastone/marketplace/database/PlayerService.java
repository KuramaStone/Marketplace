package com.github.kuramastone.marketplace.database;

import com.github.kuramastone.marketplace.player.PlayerProfile;
import com.github.kuramastone.marketplace.player.TransactionEntry;
import com.github.kuramastone.marketplace.storage.ItemEntryData;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.util.*;

public class PlayerService {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public PlayerService(MongoService mongoService) {
        mongoClient = mongoService.getMongoClient();
        database = mongoService.getDatabase();
    }

    /**
     * Loads a set of PlayerProfiles from the MongoDB database.
     * @return a Set of PlayerProfile objects.
     */
    public synchronized Set<PlayerProfile> downloadProfiles() {
        Set<PlayerProfile> profiles = new HashSet<>();
        MongoCollection<Document> collection = database.getCollection("players");

        // Retrieve all documents from the collection
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                PlayerProfile profile = deserializePlayerProfile(doc);
                profiles.add(profile);
            }
        }

        return profiles;
    }


    /**
     * Uploads an item to the MongoDB database.
     * This method creates a document of the ItemEntryData and uploads it
     */
    public synchronized void uploadProfile(PlayerProfile profile) {
        MongoCollection<Document> collection = database.getCollection("players");

        // Create a document from ItemEntryData
        Document doc = new Document("entry_uuid", profile.getUUID().toString())
                .append("transaction_history", serializeTransactionHistory(profile.getTransactionHistory()));

        collection.replaceOne(Filters.eq("entry_uuid", profile.getUUID().toString()), doc, new ReplaceOptions().upsert(true));
    }

    /**
     * Loads a specific PlayerProfile from the MongoDB database by UUID.
     */
    public synchronized PlayerProfile downloadProfileByUUID(UUID uuid) {
        MongoCollection<Document> collection = database.getCollection("players");

        // Query the collection for a document with the given UUID
        Document doc = collection.find(Filters.eq("entry_uuid", uuid.toString())).first();

        if (doc != null) {
            return deserializePlayerProfile(doc); // Deserialize the document into a PlayerProfile
        }

        return null; // Return null if no profile found
    }

    /**
     * Deserialize a PlayerProfile from a Document.
     * @param document the MongoDB document to deserialize.
     * @return a PlayerProfile object.
     */
    private PlayerProfile deserializePlayerProfile(Document document) {
        UUID entryUUID = UUID.fromString(document.getString("entry_uuid"));
        List<TransactionEntry> transactionHistory = deserializeTransactionHistory(document.get("transaction_history", Document.class));

        return new PlayerProfile(entryUUID, transactionHistory);
    }

    /**
     * Deserialize a list of TransactionEntry objects from a Document.
     * @param document the MongoDB document containing transaction history.
     * @return a List of TransactionEntry objects.
     */
    private List<TransactionEntry> deserializeTransactionHistory(Document document) {
        List<Document> entryDocs = document.getList("data", Document.class);
        List<TransactionEntry> transactionHistory = new ArrayList<>();

        for (Document entryDoc : entryDocs) {
            ItemEntryData itemData = ItemService.deserializeItemEntryData(entryDoc.get("item", Document.class));
            double listPrice = entryDoc.getDouble("listPrice");
            long timeSubmitted = entryDoc.getLong("timeSubmitted");
            UUID purchasedBy = entryDoc.get("purchasedBy", String.class) != null ? UUID.fromString(entryDoc.getString("purchasedBy")) : null;
            double purchasePrice = entryDoc.getDouble("purchasePrice");
            long timePurchased = entryDoc.getLong("timePurchased");

            TransactionEntry entry = new TransactionEntry(itemData, listPrice, timeSubmitted, purchasedBy, purchasePrice, timePurchased);
            transactionHistory.add(entry);
        }

        return transactionHistory;
    }

    private Document serializeTransactionHistory(List<TransactionEntry> transactionHistory) {
        List<Document> transactionDocuments = new ArrayList<>();

        for (TransactionEntry entry : transactionHistory) {
            Document entryDoc = new Document("item", ItemService.serializeItemEntryData(entry.getItemEntryData()))
                    .append("listPrice", entry.getListPrice())
                    .append("timeSubmitted", entry.getTimeSubmitted())
                    .append("purchasedBy", entry.getPurchasedBy() != null ? entry.getPurchasedBy().toString() : null)
                    .append("purchasePrice", entry.getPurchasePrice())
                    .append("timePurchased", entry.getTimePurchased());

            transactionDocuments.add(entryDoc);
        }

        return new Document("data", transactionDocuments);
    }

}
