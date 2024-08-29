package com.maxhit.sets;

import net.runelite.api.EquipmentInventorySlot;

import static net.runelite.api.ItemID.*;

public class ObsidianSet extends EquipmentSet {

    private int[] amulets = {
            BERSERKER_NECKLACE,
            BERSERKER_NECKLACE_OR
    };
    // This does not include the amulet
    public ObsidianSet() {
        this.heads = new int[] {
                OBSIDIAN_HELMET
        };
        this.bodies = new int[]{
                OBSIDIAN_PLATEBODY
        };

        this.legs = new int[] {
                OBSIDIAN_PLATELEGS
        };
        this.weapons = new int[] {
                TOKTZXILEK,
                TOKTZXILAK,
                TZHAARKETEM,
                TZHAARKETOM,
                TZHAARKETOM_T,
                TOKTZMEJTAL
        };
    }

    private boolean hasAmulet() {
        return hasItem(amulets, EquipmentInventorySlot.AMULET);
    }

    public boolean isWearingWeaponAndNecklace() {
        return hasWeapon() && hasAmulet();
    }

    public boolean isWearingMaxSet() {
        return isWearingSet() && hasAmulet();
    }

}
