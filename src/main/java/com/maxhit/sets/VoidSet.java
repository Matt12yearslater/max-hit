package com.maxhit.sets;

import static net.runelite.api.ItemID.*;

public class VoidSet extends EquipmentSet {

    public VoidSet() {
        this.bodies = new int[]{
                VOID_KNIGHT_TOP_LOR,
                VOID_KNIGHT_TOP_L,
                VOID_KNIGHT_TOP_OR,
                VOID_KNIGHT_TOP
        };
        this.legs = new int[]{
                VOID_KNIGHT_ROBE_LOR,
                VOID_KNIGHT_ROBE_L,
                VOID_KNIGHT_ROBE_OR,
                VOID_KNIGHT_ROBE
        };
        this.gloves = new int[]{
                VOID_MELEE_HELM_LOR,
                VOID_MELEE_HELM_L,
                VOID_MELEE_HELM_OR,
                VOID_MELEE_HELM
        };
    }

    public boolean isWearingVoid() {
        this.heads = new int [] {
                VOID_MELEE_HELM_LOR,
                VOID_MELEE_HELM_L,
                VOID_MELEE_HELM_OR,
                VOID_MELEE_HELM,
                VOID_RANGER_HELM_LOR,
                VOID_RANGER_HELM_L,
                VOID_RANGER_HELM_OR,
                VOID_RANGER_HELM,
                VOID_MAGE_HELM_LOR,
                VOID_MAGE_HELM_L,
                VOID_MAGE_HELM_OR,
                VOID_MAGE_HELM
        };
        return isWearingSet();
    }
    public boolean isWearingVoid(int style) {
        final int[] meleeHeads = {
                VOID_MELEE_HELM_LOR,
                VOID_MELEE_HELM_L,
                VOID_MELEE_HELM_OR,
                VOID_MELEE_HELM
        };
        final int[] rangedHeads = {
                VOID_RANGER_HELM_LOR,
                VOID_RANGER_HELM_L,
                VOID_RANGER_HELM_OR,
                VOID_RANGER_HELM
        };
        final int[] mageHeads = {
                VOID_MAGE_HELM_LOR,
                VOID_MAGE_HELM_L,
                VOID_MAGE_HELM_OR,
                VOID_MAGE_HELM
        };
        if (style == 0) {
            this.heads = meleeHeads;
        } else if (style == 1) {
            this.heads = rangedHeads;
        } else {
            this.heads = mageHeads;
        }
        return isWearingSet();
    }
}
