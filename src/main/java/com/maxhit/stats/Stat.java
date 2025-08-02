package com.maxhit.stats;

import lombok.Getter;
import net.runelite.api.Client;

@Getter
public abstract class Stat
{
    private final String name;

    Stat(String name)
    {
        this.name = name;
    }

    /**
     * Get the current stat value including any boosts or damage.
     */
    public abstract int getValue(Client client);

    /**
     * Get the base stat maximum. (ie. the bottom half of the stat fraction)
     */
    public abstract int getMaximum(Client client);
}
