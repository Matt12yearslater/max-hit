package com.maxhit.sets;

import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;

public class DharokSet extends EquipmentSet {
    public DharokSet(Client client) {
        this.client = client;
        this.heads = new int[] {
                ItemID.BARROWS_DHAROK_HEAD,
                ItemID.BARROWS_DHAROK_HEAD_100,
                ItemID.BARROWS_DHAROK_HEAD_75,
                ItemID.BARROWS_DHAROK_HEAD_50,
                ItemID.BARROWS_DHAROK_HEAD_25,
        };
        this.bodies = new int[] {
                ItemID.BARROWS_DHAROK_BODY,
                ItemID.BARROWS_DHAROK_BODY_100,
                ItemID.BARROWS_DHAROK_BODY_75,
                ItemID.BARROWS_DHAROK_BODY_50,
                ItemID.BARROWS_DHAROK_BODY_25,
        };
        this.legs = new int[] {
                ItemID.BARROWS_DHAROK_LEGS,
                ItemID.BARROWS_DHAROK_LEGS_100,
                ItemID.BARROWS_DHAROK_LEGS_75,
                ItemID.BARROWS_DHAROK_LEGS_50,
                ItemID.BARROWS_DHAROK_LEGS_25,
        };
        this.weapons = new int[] {
                 ItemID.BARROWS_DHAROK_WEAPON,
                 ItemID.BARROWS_DHAROK_WEAPON_100,
                 ItemID.BARROWS_DHAROK_WEAPON_75,
                 ItemID.BARROWS_DHAROK_WEAPON_50,
                 ItemID.BARROWS_DHAROK_WEAPON_25,
        };
    }
}
