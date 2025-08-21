package com.maxhit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarbitID;

@AllArgsConstructor
@Getter
public enum Spellbook {
        STANDARD(0),
        ANCIENT(1),
        LUNAR(2),
        ARCEUUS(3);

        private final int spellbookId;

        public boolean isActive(Client client)
        {
            return client.getVarbitValue(VarbitID.SPELLBOOK) == spellbookId;
        }
}
