package com.maxhit.sets;

import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import static net.runelite.api.ItemID.*;

public class EliteVoidSet extends VoidSet {
    public EliteVoidSet(Client client) {
        super(client);
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

    public boolean isWearingEliteVoid(CombatStyle combatStyle) {
        return super.isWearingVoid(combatStyle);
    }
}
