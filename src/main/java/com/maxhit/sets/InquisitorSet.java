package com.maxhit.sets;

import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.equipment.InquisitorPieces;
import com.maxhit.styles.AttackType;
import com.maxhit.styles.StyleFactory;
import java.util.Arrays;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;

public class InquisitorSet
{

	public static double getMultiplier(Client client, ItemContainer equippedItems)
	{
		double bonus = 0.0;
		AttackType attackType = StyleFactory.getAttackType(client);
		if (attackType != AttackType.CRUSH)
			return bonus;
		long piecesEquipped = Arrays.stream(InquisitorPieces.values())
			.filter(piece -> piece.isEquipped(equippedItems))
			.count();

		boolean maceEquipped = EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.INQUISITORS_MACE);
		if (maceEquipped)
			bonus += piecesEquipped * 0.025;
		else
			bonus += piecesEquipped * 0.005;
		if (piecesEquipped == 3 && !maceEquipped)
		{
			bonus += 0.01;
		}
		return bonus;
	}
}
