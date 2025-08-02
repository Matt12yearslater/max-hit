package com.maxhit;

import com.maxhit.styles.CombatStyle;
import net.runelite.client.plugins.Plugin;

public class InventoryWeapon extends Plugin {
    String name;
    CombatStyle weaponCombatStyle;
    boolean isTwoHanded = false;
    int ID = -1;
    int strBonus = 0;
    double maxHitBase;
    double maxHitSpec;
}
