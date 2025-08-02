package com.maxhit.calculators;

import com.google.common.collect.ImmutableSet;
import com.maxhit.Spellbook;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.stats.Stat;
import com.maxhit.stats.Stats;
import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;

import java.util.Set;

public class MagicMaxHitCalculator extends MaxHitCalculator{


    private static final Set<Integer> GOD_SPELLS = ImmutableSet.of(19, 20, 52);

    protected MagicMaxHitCalculator(Client client, ItemManager itemManager) {
        super(client, itemManager, Stats.MAGIC);
    }

    private boolean tomeOfFireEquipped() {
        return equippedItemIds.get("shield") == ItemID.TOME_OF_FIRE;
    }

    //Magic equipment damage bonus, includes void bonus
    //returns % damage increase
    public double magicBonus() {
        double magicEquipmentBonus = 0;

        int[] ids = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();
        for (int x : ids) {
            if (x > 512) {
                int id = x - 512;
                final ItemStats stats = itemManager.getItemStats(id);
                final ItemEquipmentStats currentEquipment = stats.getEquipment();
                magicEquipmentBonus += currentEquipment.getMdmg();

            }
        }
        if (eliteVoidSet.isWearingEliteVoid(CombatStyle.MAGE)) {
            magicEquipmentBonus += .025;
        }
        if (EquipmentFunctions.getWeaponName(client).contains("Smoke battle") && config.spellChoice().getSpellbook() == Spellbook.STANDARD) {
            magicEquipmentBonus += 0.1;
        }
        return 1 + magicEquipmentBonus;
    }

    @Override
    protected void getEffectiveStrength() {
        return;
    }

    @Override
    protected void getStrengthBonus() {
        return;
    }

    @Override
    protected void CalculateMaxHit() {
        maxHit = 0;
    }
}
