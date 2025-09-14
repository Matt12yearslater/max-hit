  package com.maxhit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.gameval.VarbitID;

// Based off of https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/prayer/PrayerType.java
@AllArgsConstructor
@Getter
public enum PrayerType
{
    // Melee
    BURST_OF_STRENGTH(Prayer.BURST_OF_STRENGTH),
    SUPERHUMAN_STRENGTH(Prayer.SUPERHUMAN_STRENGTH),
    ULTIMATE_STRENGTH(Prayer.ULTIMATE_STRENGTH),
    CHIVALRY(Prayer.CHIVALRY),
    PIETY(Prayer.PIETY),

    //Ranged
    SHARP_EYE(Prayer.SHARP_EYE),
    HAWK_EYE(Prayer.HAWK_EYE),
    EAGLE_EYE(Prayer.EAGLE_EYE)
            {
                @Override
                public boolean isEnabled(Client client)
                {
                    return !DEADEYE.isEnabled(client);
                }
            },
    DEADEYE(Prayer.DEADEYE)
            {
                @Override
                public boolean isEnabled(Client client)
                {
                    boolean inLms = client.getVarbitValue(VarbitID.BR_INGAME) != 0;
                    boolean deadeye = client.getVarbitValue(VarbitID.PRAYER_DEADEYE_UNLOCKED) != 0;
                    return deadeye && !inLms;
                }
            },
    RIGOUR(Prayer.RIGOUR),

    //Magic
    MYSTIC_WILL(Prayer.MYSTIC_WILL),
    MYSTIC_LORE(Prayer.MYSTIC_LORE),
    MYSTIC_MIGHT(Prayer.MYSTIC_MIGHT)
            {
                @Override
                public boolean isEnabled(Client client)
                {
                    return !MYSTIC_VIGOUR.isEnabled(client);
                }
            },
    MYSTIC_VIGOUR(Prayer.MYSTIC_VIGOUR)
            {
                @Override
                public boolean isEnabled(Client client)
                {
                    boolean inLms = client.getVarbitValue(VarbitID.BR_INGAME) != 0;
                    boolean vigour = client.getVarbitValue(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED) != 0;
                    return vigour && !inLms;
                }
            },
    AUGURY(Prayer.AUGURY),

    // Leagues
    RP_ANCIENT_STRENGTH(Prayer.RP_ANCIENT_STRENGTH),
    RP_ANCIENT_SIGHT(Prayer.RP_ANCIENT_SIGHT),
    RP_TRINITAS(Prayer.RP_TRINITAS),
    RP_DECIMATE(Prayer.RP_DECIMATE),
    RP_ANNIHILATE(Prayer.RP_ANNIHILATE),
    RP_VAPORISE(Prayer.RP_VAPORISE),
    ;

    private final Prayer prayer;

    public boolean isEnabled(Client client)
    {
        return true;
    }

    public boolean isActive(Client client)
    {
        return client.isPrayerActive(prayer) && isEnabled(client);
    }
}
