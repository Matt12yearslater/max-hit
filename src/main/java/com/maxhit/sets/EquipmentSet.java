package com.maxhit.sets;

//Used for determining if the user is wearing a set

import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;

import javax.inject.Inject;

public class EquipmentSet {
    @Inject
    private Client client;
    protected int[] heads = null;
    protected int[] bodies = null;
    protected int[] legs = null;
    protected int[] gloves = null;
    protected int[] weapons = null;

    protected boolean hasItem(int[] items, EquipmentInventorySlot slot) {
        try {
            Item[] equippedItems = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
            if (items == null) {
                return true;
            }
            for (int item : items) {
                if (equippedItems[slot.getSlotIdx()].getId() == item) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean hasHead() {
        return hasItem(heads, EquipmentInventorySlot.HEAD);
    }
    private boolean hasBody() {
        return hasItem(bodies, EquipmentInventorySlot.BODY);
    }
    private boolean hasLegs() {
        return hasItem(legs, EquipmentInventorySlot.LEGS);
    }
    private boolean hasGloves() {
        return hasItem(gloves, EquipmentInventorySlot.GLOVES);
    }
    protected boolean hasWeapon() {
        return hasItem(weapons, EquipmentInventorySlot.WEAPON);
    }

    public boolean isWearingSet() {
        return hasHead() && hasBody() && hasLegs() && hasGloves() && hasWeapon();
    }

}
