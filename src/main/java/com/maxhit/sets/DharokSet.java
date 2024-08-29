package com.maxhit.sets;

import static net.runelite.api.ItemID.*;

public class DharokSet extends EquipmentSet {
    public DharokSet() {
        this.heads = new int[] {
                DHAROKS_HELM,
                DHAROKS_HELM_100,
                DHAROKS_HELM_75,
                DHAROKS_HELM_50,
                DHAROKS_HELM_25,
        };
        this.bodies = new int[] {
                DHAROKS_PLATEBODY,
                DHAROKS_PLATEBODY_100,
                DHAROKS_PLATEBODY_75,
                DHAROKS_PLATEBODY_50,
                DHAROKS_PLATEBODY_25,
        };
        this.legs = new int[] {
                DHAROKS_PLATELEGS,
                DHAROKS_PLATELEGS_100,
                DHAROKS_PLATELEGS_75,
                DHAROKS_PLATELEGS_50,
                DHAROKS_PLATELEGS_25,
        };
        this.weapons = new int[] {
                DHAROKS_GREATAXE,
                DHAROKS_GREATAXE_100,
                DHAROKS_GREATAXE_75,
                DHAROKS_GREATAXE_50,
                DHAROKS_GREATAXE_25,
        };
    }
}
