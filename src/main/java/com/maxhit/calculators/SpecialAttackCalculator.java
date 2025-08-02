package com.maxhit.calculators;

import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.equipment.SpecialAttackWeapons;
import com.maxhit.stats.Stats;
import net.runelite.api.Client;

public class SpecialAttackCalculator
{
    private Client client;

    public SpecialAttackCalculator(Client client)
    {
        this.client = client;
    }

    public double maxHitSpec(int weaponId, double maxHitBase) {
        String weaponName = client.getItemDefinition(weaponId).getName();
        double damageMultiplier = SpecialAttackWeapons.WeaponMap.get(weaponId);
        String ammoName = EquipmentFunctions.getAmmoName(equippedItems, client);
        if (weaponName.contains("rossbo") && ammoName.contains("(e)")) {
            int playerRangedLevel = Stats.RANGED.getValue(client);
            if (ammoName.contains("Diamond")) return Math.floor(maxHitBase) * 1.15;
            if (ammoName.contains("Onyx")) return Math.floor(maxHitBase) * 1.2;
            if (ammoName.contains("Dragonstone")) return Math.floor(maxHitBase) + Math.floor(playerRangedLevel * .2);
            if (ammoName.contains("Opal")) return Math.floor(maxHitBase) + Math.floor(playerRangedLevel * .1);
            if (ammoName.contains("Pearl")) return Math.floor(maxHitBase) + Math.floor(playerRangedLevel * .05);
        }

        if (weaponName.equalsIgnoreCase("Saradomin sword")) {
            return 16 + (Math.floor(maxHitBase) * SpecialAttackWeapons.WeaponMap.get(weaponId));
        }
        if (weaponName.equalsIgnoreCase("Granite hammer")) {
            return Math.floor(maxHitBase) + 5;
        }
        if (damageMultiplier != -1) {
            if (weaponName.contains("dagger") || weaponName.equalsIgnoreCase("Dark bow")) {
                return Math.floor(Math.floor(maxHitBase) * damageMultiplier) * 2;
            }
            return Math.floor(maxHitBase) * damageMultiplier;
        }
        return -1;
    }
}
