package com.maxhit.equipment;

import com.maxhit.MagicSpell;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;

@Getter
@AllArgsConstructor
public enum GodCape
{
	ZAMORAK(ItemID.ZAMORAK_CAPE, ItemID.MA2_ZAMORAK_CAPE, MagicSpell.FLAMES_OF_ZAMORAK.getVarbValue()),
	SARADOMIN(ItemID.SARADOMIN_CAPE, ItemID.MA2_SARADOMIN_CAPE, MagicSpell.SARADOMIN_STRIKE.getVarbValue()),
	GUTHIX(ItemID.GUTHIX_CAPE, ItemID.MA2_GUTHIX_CAPE, MagicSpell.CLAWS_OF_GUTHIX.getVarbValue())
	;

	private final int regularItemId;
	private final int imbuedItemId;
	private final int spellId;

	public boolean isEquipped(ItemContainer equippedItems)
	{
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.CAPE, regularItemId))
			return true;
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.CAPE, imbuedItemId))
			return true;
		return false;
	}
}
