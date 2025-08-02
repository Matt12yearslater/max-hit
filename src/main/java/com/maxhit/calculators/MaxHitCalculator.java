package com.maxhit.calculators;

import com.maxhit.sets.EliteVoidSet;
import com.maxhit.sets.VoidSet;
import com.maxhit.stats.Stat;
import com.maxhit.utils.NextMaxHit;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.client.game.ItemManager;

public abstract class MaxHitCalculator
{
    protected Client client;
    protected ItemManager itemManager;
    protected final Stat stat;
    protected double effectiveStrength;
    protected int strengthBonus;
    protected double prayerBonus;
    protected int styleBonus;
    protected double voidModifier;
    protected VoidSet voidSet;
    protected EliteVoidSet eliteVoidSet;
    protected ItemContainer equippedItems;
    public int maxHit;
    public NextMaxHit nextMaxHit;

    protected final EquipmentInventorySlot[] itemIdSlots = {
        EquipmentInventorySlot.HEAD,
        EquipmentInventorySlot.CAPE,
        EquipmentInventorySlot.AMULET,
        EquipmentInventorySlot.WEAPON,
        EquipmentInventorySlot.BODY,
        EquipmentInventorySlot.SHIELD,
        EquipmentInventorySlot.LEGS,
        EquipmentInventorySlot.GLOVES,
        EquipmentInventorySlot.BOOTS,
        EquipmentInventorySlot.RING,
        EquipmentInventorySlot.AMMO,
    };

    protected MaxHitCalculator (Client client, ItemManager itemManager, Stat stat)
    {
        this.client = client;
        this.itemManager = itemManager;
        this.stat = stat;
        nextMaxHit = new NextMaxHit();
        maxHit = 0;
    }


    protected abstract void getEffectiveStrength();

    protected abstract void getStrengthBonus();

    protected abstract void CalculateMaxHit();

    public abstract void getNextMaxHit();

    public void setEquippedItems(ItemContainer equippedItems) {
        this.equippedItems = equippedItems;
        CalculateMaxHit();
    }

    public void statUpdated()
    {
        stat.getValue(client);
        CalculateMaxHit();
    }
}
