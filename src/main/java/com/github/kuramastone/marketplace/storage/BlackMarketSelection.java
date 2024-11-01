package com.github.kuramastone.marketplace.storage;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BlackMarketSelection {

    /**
     * Randomly adds items to a new MarketplaceStorage using the seed and item.
     * Uses the ItemEntry's hashcode to modify the seed to determine if that entry should be added.
     *
     * @param seed Random seed to use
     * @param discount Discount to apply in new MarketplaceStorage
     * @param storage Source of the items
     * @return
     */
    public static Set<ItemEntry> createSelectionFor(long seed, double chanceToAdd, MarketplaceStorage storage) {
        Random random = new Random(seed);
        Set<ItemEntry> itemEntries = new HashSet<>();

        for(ItemEntry ie : storage.getItemEntries()) {
            long moddedSeed = seed + ie.hashCode() * 415215L;
            random.setSeed(moddedSeed);
            double rnd = random.nextDouble();
            if(rnd < chanceToAdd) {
                itemEntries.add(ie);
            }
        }

        return itemEntries;
    }

}
