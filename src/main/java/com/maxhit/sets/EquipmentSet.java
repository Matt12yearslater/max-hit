package com.maxhit.sets;

//Used for determining if the user is wearing a set

import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.Item;
import net.runelite.api.gameval.InventoryID;
import javax.inject.Inject;

public class EquipmentSet {
    @Inject
    protected Client client;
    protected int[] heads = null;
    protected int[] bodies = null;
    protected int[] legs = null;
    protected int[] gloves = null;
    protected int[] weapons = null;

    protected boolean hasItem(int[] items, EquipmentInventorySlot slot) {
        ItemContainer container = client.getItemContainer(InventoryID.WORN);
        if (container == null)
            return false;
        Item[] equippedItems = container.getItems();

        for (int item : items) {
            if (equippedItems[slot.getSlotIdx()].getId() == item) {
                return true;
            }
        }
        return false;
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
