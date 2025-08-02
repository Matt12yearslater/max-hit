package com.maxhit.calculators;

import com.maxhit.stats.Stats;
import com.maxhit.styles.StyleFactory;
import com.maxhit.styles.CombatStyle;
import com.maxhit.utils.PrayerBonus;
import net.runelite.api.Client;

public class EffectiveRangedStrengthCalculator extends EffectiveStrengthCalculator
{
    public EffectiveRangedStrengthCalculator(Client client) {
        super(client);
        GetRequiredParameters();
    }

    @Override
    protected void GetRequiredParameters()
    {
        stat = Stats.RANGED;
        prayerBonus = PrayerBonus.getRangedPrayerBonus(client);
        styleBonus = StyleFactory.getAttackStyle(client);
        voidModifier = voidSet.isWearingVoid(CombatStyle.RANGED) ? 1.1 : 1.0;
    }
}
