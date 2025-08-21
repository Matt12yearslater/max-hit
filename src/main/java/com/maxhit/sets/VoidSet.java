package com.maxhit.sets;

import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;

public class VoidSet extends EquipmentSet {

    public VoidSet(Client client) {
        this.client = client;
        this.bodies = new int[]{
                ItemID.PEST_VOID_KNIGHT_TOP,
        };
        this.legs = new int[]{
                ItemID.PEST_VOID_KNIGHT_ROBES,
        };
        this.gloves = new int[]{
                ItemID.PEST_VOID_KNIGHT_GLOVES,
        };
    }

    public boolean isWearingVoid(CombatStyle combatStyle) {
        final int[] meleeHeads = {
                ItemID.GAME_PEST_MELEE_HELM,
        };
        final int[] rangedHeads = {
                ItemID.GAME_PEST_ARCHER_HELM,
        };
        final int[] mageHeads = {
                ItemID.GAME_PEST_MAGE_HELM,
        };
        if (combatStyle == CombatStyle.MELEE) {
            this.heads = meleeHeads;
        } else if (combatStyle == CombatStyle.RANGED) {
            this.heads = rangedHeads;
        } else {
            this.heads = mageHeads;
        }
        return isWearingSet();
    }
}
