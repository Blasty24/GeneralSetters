package com.blasty.managers;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.event.Listener;

/* Class exits for future devlopment or adding of new features TBD */
public class ItemManager implements Listener {

    public static HashMap<Material, Integer> limitList = new HashMap<>();

    public void addItem(Material item, int limit) {
        limitList.put(item, limit);
    }

    public void removeItem(Material item) {
        limitList.remove(item);
    }
}
