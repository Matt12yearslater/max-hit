package com.maxhit.calculators;

import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

public class MaxHitCalculatorFactory
{
    private Client client;
    private ItemManager itemManager;

    public MaxHitCalculatorFactory(Client client, ItemManager itemManager)
    {
        this.client = client;
        this.itemManager = itemManager;
    }
    public MaxHitCalculator create(CombatStyle combatStyle)
    {
        if (combatStyle == CombatStyle.MELEE)
            return new MeleeMaxHitCalculator(this.client, this.itemManager);
        if (combatStyle == CombatStyle.RANGED)
            return new RangedMaxHitCalculator(this.client, this.itemManager);
        return  null;
    }
}
