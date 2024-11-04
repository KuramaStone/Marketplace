package com.github.kuramastone.marketplace.database;

import com.github.kuramastone.marketplace.storage.ItemEntryData;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.Binary;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItemService {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public ItemService(MongoService mongoService) {
        mongoClient = mongoService.getMongoClient();
        database = mongoService.getDatabase();
    }

    /**
     * Downloads all available items from the MongoDB database.
     * This method retrieves all items that are marked as available, deserializes them,
     * and returns them as a Set of ItemEntryData.
     *
     * @return a Set of ItemEntryData representing available items
     */
    public synchronized Set<ItemEntryData> downloadAvailableItems() {
        MongoCollection<Document> collection = database.getCollection("items");
        Set<ItemEntryData> availableItems = new HashSet<>();

        for (Document document : collection.find()) {
            ItemEntryData itemEntryData = ItemService.deserializeItemEntryData(document);
            availableItems.add(itemEntryData);
        }

        return availableItems; // Return the set of available items
    }

    /**
     * Only detects items that are available.
     * Send a request to check if it is available and, using the same request, removes it from the database preventing race conditions
     *
     * @return True if item is available for sale
     */
    public synchronized boolean buyItem(UUID entryUUID) {
        MongoCollection<Document> collection = database.getCollection("items");

        Document item = collection.findOneAndDelete(
                Filters.and(Filters.eq("entry_uuid", entryUUID.toString()))
        );

        return item != null; // Returns true if the item was successfully purchased, false otherwise
    }


    /**
     * Uploads an item to the MongoDB database.
     * This method creates a document of the ItemEntryData and uploads it
     */
    public synchronized void uploadItem(ItemEntryData itemEntryData) {
        MongoCollection<Document> collection = database.getCollection("items");

        // Create a document from ItemEntryData
        Document doc = serializeItemEntryData(itemEntryData);

        collection.insertOne(doc);
    }

    /**
     * Removes entry from the database
     *
     * @return True if item was in database
     */
    public boolean removeItem(ItemEntryData itemEntry) {
        MongoCollection<Document> collection = database.getCollection("items");
        Document item = collection.findOneAndDelete(
                Filters.and(Filters.eq("entry_uuid", itemEntry.getEntryUUID().toString()))
        );

        return item != null;
    }

    public static Document serializeItemEntryData(ItemEntryData itemEntryData) {
        return new Document("entry_uuid", itemEntryData.getEntryUUID().toString())
                .append("seller_uuid", itemEntryData.getSellerUUID().toString())
                .append("itemstack", serializeItemStack(itemEntryData.getItemstack()))
                .append("originalPrice", itemEntryData.getOriginalPrice())
                .append("list_time", itemEntryData.getListTime());
    }

    public static ItemEntryData deserializeItemEntryData(Document document) {
        UUID entryUUID = UUID.fromString(document.getString("entry_uuid"));
        ItemStack itemStack = deserializeItemStack(document.get("itemstack", Document.class));
        double originalPrice = document.getDouble("originalPrice");
        UUID sellerUUID = UUID.fromString(document.getString("seller_uuid"));
        Long listTime = document.getLong("list_time");

        return new ItemEntryData(entryUUID, sellerUUID, itemStack, originalPrice, listTime);
    }

    public static Document serializeItemStack(ItemStack itemStack) {
        byte[] data = itemStack.serializeAsBytes(); // Assume this method exists
        return new Document("data", data);
    }

    public static ItemStack deserializeItemStack(Document document) {
        Binary binaryData = document.get("data", Binary.class); // Retrieve as Binary
        byte[] data = binaryData.getData(); // Extract byte array
        return ItemStack.deserializeBytes(data); // Use the byte array
    }

}