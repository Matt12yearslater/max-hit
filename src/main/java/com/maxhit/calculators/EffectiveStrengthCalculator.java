package com.maxhit.calculators;

import com.maxhit.sets.EliteVoidSet;
import com.maxhit.sets.VoidSet;
import com.maxhit.stats.Stat;
import net.runelite.api.Client;


public abstract class EffectiveStrengthCalculator {

    protected Client client;
    protected Stat stat;
    protected double prayerBonus;
    protected int styleBonus;
    protected double voidModifier;
    protected VoidSet voidSet;
    protected EliteVoidSet eliteVoidSet;

    public EffectiveStrengthCalculator(Client client)
    {
        this.client = client;
        voidSet = new VoidSet();
        eliteVoidSet = new EliteVoidSet();
    }

    protected abstract void UpdateParameters();

    public double getEffectiveStrength()
    {
        return Math.floor(Math.floor((stat.getValue(client) * prayerBonus) + styleBonus + 8) * voidModifier);
    }
}
