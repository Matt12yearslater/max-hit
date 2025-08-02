package com.maxhit.calculators;

import com.maxhit.stats.Stat;
import com.maxhit.stats.Stats;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

public class RangedMaxHitCalculator extends  MaxHitCalculator
{

    protected RangedMaxHitCalculator(Client client, ItemManager itemManager) {
        super(client, itemManager, Stats.RANGED);
    }

    @Override
    protected void CalculateMaxHit() {
        maxHit = 0;
    }
}
