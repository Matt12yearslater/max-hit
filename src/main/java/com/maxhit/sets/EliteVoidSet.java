package com.maxhit.sets;

import static net.runelite.api.ItemID.*;

public class EliteVoidSet extends VoidSet {
    public EliteVoidSet() {
        this.bodies = new int[]{
                ELITE_VOID_TOP_LOR,
                ELITE_VOID_TOP_L,
                ELITE_VOID_TOP_OR,
                ELITE_VOID_TOP
        };
        this.legs = new int[]{
                ELITE_VOID_ROBE_LOR,
                ELITE_VOID_ROBE_L,
                ELITE_VOID_ROBE_OR,
                ELITE_VOID_ROBE
        };
    }

    public boolean isWearingEliteVoid() {
        return super.isWearingVoid();
    }
    public boolean isWearingEliteVoid(int style) {
        return super.isWearingVoid(style);
    }
}
