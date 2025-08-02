package com.maxhit.equipment;

import com.maxhit.styles.AttackStyle;
import net.runelite.api.*;
import net.runelite.api.gameval.InventoryID;

public class EquipmentFunctions {


    public static String getAmmoName(ItemContainer equipment, Client client) {
        final Item ammo = equipment.getItem(EquipmentInventorySlot.AMMO.getSlotIdx());
        if (ammo == null) return  null;
        int ammoID = ammo.getId();
        if (ammoID != -1) {
            return client.getItemDefinition(ammoID).getName();
        }
        return null;
    }

    public static String getWeaponName(Client client) {

        final ItemContainer container = client.getItemContainer(InventoryID.WORN);
        if (container == null) return null;
        Item[] items = container.getItems();
        if (items.length >= EquipmentInventorySlot.WEAPON.getSlotIdx()) {
            final Item weapon = items[EquipmentInventorySlot.WEAPON.getSlotIdx()];
            if (weapon.getId() > 512) {
                int weaponID = weapon.getId();
                return client.getItemDefinition(weaponID).getName();
            }
        }
        return null;
    }
}
