package com.maxhit.MaxHitTest;

import com.maxhit.calculators.MagicMaxHitCalculator;
import com.maxhit.calculators.MeleeMaxHitCalculator;
import com.maxhit.calculators.RangedMaxHitCalculator;
import com.maxhit.styles.AttackStyle;
import com.maxhit.styles.CombatStyle;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.gameval.ItemID;

@Getter
@RequiredArgsConstructor
public enum PlayerSetup
{
	BASE(
		createEquipment(Map.of()),
		CombatStyle.MELEE, AttackStyle.ACCURATE, MeleeMaxHitCalculator.class,
		0.0f, 1.0, 11.0),
	MELEE_VOID(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.GAME_PEST_MELEE_HELM, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.PEST_VOID_KNIGHT_TOP, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.PEST_VOID_KNIGHT_ROBES, 1),
			EquipmentInventorySlot.GLOVES.getSlotIdx(), new Item(ItemID.PEST_VOID_KNIGHT_GLOVES, 1)
		)),
		CombatStyle.MELEE, AttackStyle.ACCURATE, MeleeMaxHitCalculator.class,
		0.0f, 1.0, 12.0),
	INQUISITORS(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.INQUISITORS_HELM, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.INQUISITORS_MACE, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.INQUISITORS_BODY, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.INQUISITORS_SKIRT, 1)
		)),
		CombatStyle.MELEE, AttackStyle.ACCURATE, MeleeMaxHitCalculator.class,
		159.0f, 3.0, 39.0),
	FANG(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.OSMUMTENS_FANG, 1)
		)),
		CombatStyle.MELEE, AttackStyle.ACCURATE, MeleeMaxHitCalculator.class,
		181.0f, 3.0, 35.0),
	MAX_DHAROK(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.BARROWS_DHAROK_HEAD, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.BARROWS_DHAROK_WEAPON, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.BARROWS_DHAROK_BODY, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.BARROWS_DHAROK_LEGS, 1)
		)),
		CombatStyle.MELEE, AttackStyle.ACCURATE, MeleeMaxHitCalculator.class,
		157.0f, 3.0, 72.0),
	BOWFA_AND_CRYSTAL(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.CRYSTAL_HELMET, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.BOW_OF_FAERDHINEN, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.CRYSTAL_CHESTPLATE, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.CRYSTAL_PLATELEGS, 1)
		)),
		CombatStyle.RANGED, AttackStyle.ACCURATE, RangedMaxHitCalculator.class,
		121.0f, 3.0, 36.0),
	VIRTUS_MAGE(
	createEquipment(Map.of(
		EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.VIRTUS_MASK, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.STAFF_OF_ZAROS, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.VIRTUS_TOP, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.VIRTUS_LEGS, 1)
		)),
	CombatStyle.MAGE, AttackStyle.CASTING, MagicMaxHitCalculator.class,
	0.33f, 39.0, 39.0
	),
	WARPED_SCEPTRE(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.WARPED_SCEPTRE, 1)
		)),
		CombatStyle.MAGE, AttackStyle.ACCURATE, MagicMaxHitCalculator.class,
		0.27f, 2.0, 30.0
	),
	BONE_STAFF(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.RAT_BONE_STAFF, 1)
		)),
		CombatStyle.MAGE, AttackStyle.ACCURATE, MagicMaxHitCalculator.class,
		0.27f, 6.0, 48.0
	),
	SANGUINE_STAFF(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.SANGUINESTI_STAFF_OR, 1)
		)),
		CombatStyle.MAGE, AttackStyle.ACCURATE, MagicMaxHitCalculator.class,
		0.27f, 6.0, 40.0
	),
	SHADOW(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.TUMEKENS_SHADOW, 1)
		)),
		CombatStyle.MAGE, AttackStyle.ACCURATE, MagicMaxHitCalculator.class,
		0.81f, 2.0, 68.0
	);


	private static Item[] createEquipment(Map<Integer, Item> itemMap) {
		final Item[] equipment = new Item[14];
		itemMap.forEach((slot, item) -> {
			if (slot >= 0 && slot < equipment.length) {
				equipment[slot] = item;
			}
		});
		return equipment;
	}

	private final Item[] equippedItems;
	private final CombatStyle combatStyle;
	private final AttackStyle attackStyle;
	private final Class maxHitCalculatorClass;
	private final float strengthBonus;
	private final double standardMaxHit;
	private final double maxedMaxHit;
}
