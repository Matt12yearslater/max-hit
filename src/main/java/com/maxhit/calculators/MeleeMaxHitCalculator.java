package com.maxhit.calculators;

import com.maxhit.sets.DharokSet;
import com.maxhit.sets.ObsidianSet;
import com.maxhit.stats.Stats;
import com.maxhit.styles.CombatStyle;
import com.maxhit.utils.NextMaxHit;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;

public class MeleeMaxHitCalculator extends MaxHitCalculator
{

    private final DharokSet dharokSetChecker;
    private final ObsidianSet obsidianSetChecker;

    protected MeleeMaxHitCalculator(Client client, ItemManager itemManager) {
        super(client, itemManager, Stats.STRENGTH);
        dharokSetChecker = new DharokSet();
        obsidianSetChecker = new ObsidianSet();
    }

    @Override
    protected void getEffectiveStrength()
    {
        effectiveStrength = Math.floor(Math.floor((stat.getValue(client) * prayerBonus) + styleBonus + 8) * voidModifier);
    }

    @Override
    protected void getStrengthBonus() {
        int bonus = 0;
        //get str bonus of worn equipment
        for (EquipmentInventorySlot slot : itemIdSlots) {
            Item item = equippedItems.getItem(slot.ordinal());
            if (item == null)
                continue;
            int id = item.getId();
            final ItemStats stats = itemManager.getItemStats(id);
            if (stats == null) {
                continue;
            }
            final ItemEquipmentStats currentEquipment = stats.getEquipment();
            bonus += currentEquipment.getStr();
        }
        strengthBonus = bonus;
    }

    private double getBaseDamage()
    {
        double bonusMultiplier = (strengthBonus + 64.0) / 640.0;
        return 0.5 + (effectiveStrength * bonusMultiplier);
    }

    private double setBonus() {
        //Melee sets
        if (dharokSetChecker.isWearingSet()) {
            double baseHitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
            double currentHitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
            return 1 + (((baseHitpoints - currentHitpoints)/100) * baseHitpoints/100);
        }

        if (obsidianSetChecker.isWearingMaxSet()) {
            return 1.32;
        }
        if (obsidianSetChecker.isWearingWeaponAndNecklace()) {
            return 1.2;
        }
        if (obsidianSetChecker.isWearingSet()) {
            return 1.1;
        }
        //Magic sets applied directly in max hit calculation
        return 1;
    }

    @Override
    public void CalculateMaxHit()
    {
        maxHit =  (int) (getBaseDamage() * setBonus());
    }

    @Override
    public void getNextMaxHit() {
        // Looking for
        double baseMax = maxHit + 1;
        final double requiredStrengthBonus = Math.ceil(((getBaseDamage() - 0.5) * 640 / effectiveStrength) - 64);
        final double strengthBonusDiff = requiredStrengthBonus - strengthBonus;
        // Remove set bonus
        baseMax /= setBonus();

        double reverseEffectiveStrengthLevel = Math.ceil((baseMax - 0.5) * 640 / (strengthBonus + 64) - 8 - style);
    }
}
