package com.maxhit.calculators;

import com.maxhit.styles.AttackStyle;
import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

public class MaxHitCalculatorFactory
{
    private final Client client;
    private final ItemManager itemManager;

    public MaxHitCalculatorFactory(Client client, ItemManager itemManager)
    {
        this.client = client;
        this.itemManager = itemManager;
    }
    public MaxHitCalculator create(CombatStyle combatStyle, AttackStyle attackStyle)
    {
        if (combatStyle == CombatStyle.MELEE)
            return new MeleeMaxHitCalculator(this.client, this.itemManager, attackStyle);
        if (combatStyle == CombatStyle.RANGED)
            return new RangedMaxHitCalculator(this.client, this.itemManager, attackStyle);
        if (combatStyle == CombatStyle.MAGE)
            return new MagicMaxHitCalculator(this.client, this.itemManager, attackStyle);
        return  null;
    }
}
