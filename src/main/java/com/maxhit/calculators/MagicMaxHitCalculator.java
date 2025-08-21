package com.maxhit.calculators;

import com.google.common.collect.ImmutableSet;
import com.maxhit.MagicSpell;
import com.maxhit.PrayerType;
import com.maxhit.Spellbook;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.monsters.MonsterWeaknesses;
import com.maxhit.styles.AttackStyle;
import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class MagicMaxHitCalculator extends MaxHitCalculator
{
	private static final Set<Integer> GOD_SPELLS = ImmutableSet.of(19, 20, 52);
	private static final Set<Integer> BOLT_SPELLS = ImmutableSet.of(6, 7, 8, 9);
	private static final int TOA_NEXUS_REGION_ID = 14160;
	private static final int BABA_PUZZLE_ROOM_REGION_ID = 15186;
	private static final int BABA_ROOM_REGION_ID = 15188;
	private static final int KEPHRI_PUZZLE_ROOM_REGION_ID = 14162;
	private static final int KEPHRI_ROOM_REGION_ID = 14164;
	private static final int AKKHA_PUZZLE_ROOM_REGION_ID = 14674;
	private static final int AKKHA_ROOM_REGION_ID = 14676;
	private static final int ZEBAK_PUZZLE_ROOM_REGION_ID = 15698;
	private static final int ZEBAK_ROOM_REGION_ID = 15700;
	private static final int WARDENS_OBELISK_ROOM_REGION_ID = 15184;
	private static final int WARDENS_P3_ROOM_REGION_ID = 15696;
	private static final int TOA_LOOT_ROOM_REGION_ID = 14672;
	private static final int TOA_LOBBY_REGION_ID = 13454;
	private static final Set<Integer> TOA_ROOM_IDS = ImmutableSet.of(
		TOA_NEXUS_REGION_ID,
		BABA_PUZZLE_ROOM_REGION_ID,
		BABA_ROOM_REGION_ID,
		KEPHRI_PUZZLE_ROOM_REGION_ID,
		KEPHRI_ROOM_REGION_ID,
		AKKHA_PUZZLE_ROOM_REGION_ID,
		AKKHA_ROOM_REGION_ID,
		ZEBAK_PUZZLE_ROOM_REGION_ID,
		ZEBAK_ROOM_REGION_ID,
		WARDENS_OBELISK_ROOM_REGION_ID,
		WARDENS_P3_ROOM_REGION_ID,
		TOA_LOOT_ROOM_REGION_ID,
		TOA_LOBBY_REGION_ID
	);
	MagicSpell activeSpell = null;
	private double baseSpellDamage;
	private double baseDamageModifier;
	private double primaryMagicDamage;
	private double preHitRoll;
	private double magicBonus;
	private double shadowBonus;
	private double salveBonus;
	private double avariceBonus;
	private double smokeBattlestaffBonus;
	private double virtusBonus;
	private double prayerBonus;
	private double elementalWeakness;
	private double slayerBonus;
	private double sceptreBonus;
	private double accursedSceptreSpecialAttackBonus;
	private double tomeBonus;
	private double markOfDarknessBonus;
	private double ahrimsDamnedBonus;
	//Ignore castle wars bracelet for now

	protected MagicMaxHitCalculator(Client client, ItemManager itemManager, AttackStyle attackStyle)
	{
		super(client, itemManager, Skill.MAGIC, attackStyle);
		salveRegularBonus = 0.0;
		salveEnchantedBonus = 0.0;
		salveImbuedBonus = 0.15;
		salveEnchantedImbuedBonus = 0.2;
		reset();
	}

	protected void reset()
	{
		voidBonus = 0.0;
		baseSpellDamage = 0.0;
		baseDamageModifier = 0.0;
		primaryMagicDamage = 0.0;
		magicBonus = 0.0;
		shadowBonus = 1.0;
		salveBonus = 0.0;
		avariceBonus = 0.0;
		smokeBattlestaffBonus = 0.0;
		virtusBonus = 0.0;
		prayerBonus = 0.0;
		elementalWeakness = 0.0;
		slayerBonus = 0.0;
		sceptreBonus = 0.0;
		accursedSceptreSpecialAttackBonus = 0.0;
		tomeBonus = 0.0;
		markOfDarknessBonus = 0.0;
		ahrimsDamnedBonus = 0.0;
	}

	private boolean chaosGauntletsEquipped()
	{
		return EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.GLOVES, ItemID.GAUNTLETS_OF_CHAOS);
	}

	private boolean matchingGodCapeEquipped()
	{
		int activeSpellVarbit = client.getVarbitValue(VarbitID.AUTOCAST_SPELL);
		if (activeSpellVarbit == MagicSpell.FLAMES_OF_ZAMORAK.getVarbValue())
		{

			return EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.CAPE, ItemID.ZAMORAK_CAPE) ||
				EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.CAPE, ItemID.MA2_ZAMORAK_CAPE);
		}
		if (activeSpellVarbit == MagicSpell.SARADOMIN_STRIKE.getVarbValue())
		{
			return EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.CAPE, ItemID.SARADOMIN_CAPE) ||
				EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.CAPE, ItemID.MA2_SARADOMIN_CAPE);
		}
		if (activeSpellVarbit == MagicSpell.CLAWS_OF_GUTHIX.getVarbValue())
		{
			return EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.CAPE, ItemID.GUTHIX_CAPE) ||
				EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.CAPE, ItemID.MA2_GUTHIX_CAPE);
		}
		return false;
	}

	private void getSpellBaseMaxDamage()
	{
		// Tumeken's Shadow
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.TUMEKENS_SHADOW))
		{
			baseSpellDamage = Math.floor((getSkillLevel() / 3.0) + 1.0);
			return;
		}

		int activeSpellVarbit = client.getVarbitValue(VarbitID.AUTOCAST_SPELL);
		// Spellbook Spells
		for (MagicSpell spell : MagicSpell.values())
		{
			if (activeSpellVarbit != spell.getVarbValue())
			{
				continue;
			}
			activeSpell = spell;
			baseSpellDamage = activeSpell.getBaseMaxHit(client);
		}
	}

	private void getBaseDamageModifier()
	{
		getSpellBaseMaxDamage();
		int activeSpellVarbit = client.getVarbitValue(VarbitID.AUTOCAST_SPELL);
		baseDamageModifier = baseSpellDamage;

		// Check for Chaos Gauntlets with bolt spells
		if (BOLT_SPELLS.contains(activeSpellVarbit))
		{
			if (chaosGauntletsEquipped())
			{
				baseDamageModifier += 3;
				return;
			}
		}

		// Check for Charge with God spells and god cape
		if (!GOD_SPELLS.contains(activeSpellVarbit))
		{
			return;
		}
		if (!matchingGodCapeEquipped())
		{
			return;
		}
		if (client.getVarbitValue(VarPlayerID.MAGEARENA_CHARGE) == 0)
		{
			return;
		}
		baseDamageModifier += 10;
	}

	@Override
	protected void getStrengthBonus()
	{
		if (equippedItems == null)
		{
			return;
		}
		//get str bonus of worn equipment
		for (EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			// Have to convert enum to int i.e. use ordinal
			Item item = equippedItems.getItem(slot.getSlotIdx());
			if (item == null)
			{
				continue;
			}
			int id = item.getId();
			final ItemStats stats = itemManager.getItemStats(id);
			if (stats == null)
			{
				continue;
			}
			final ItemEquipmentStats itemStats = stats.getEquipment();
			magicBonus += itemStats.getMdmg();
		}
	}

	private void getShadowBonus()
	{
		// Check if Shadow equipped
		if (!EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.WEAPON).contains("Tumaken"))
		{
			return;
		}
		int region = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
		boolean toaInside = TOA_ROOM_IDS.contains(region);
		if (toaInside)
		{
			shadowBonus = 4.0;
		}
		else
		{
			shadowBonus = 3.0;
		}
	}

	private void getVoidBonus()
	{
		if (eliteVoidSetChecker.isWearingEliteVoid(CombatStyle.MAGE))
		{
			voidBonus = .05;
		}
	}

	private void getAvariceBonus()
	{
		if (!EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.AMULET).contains(("Avarice")))
		{
			return;
		}
		NPC npc = (NPC) opponent;
		if (npc == null)
		{
			return;
		}
		String npcName = npc.getName();
		if (npcName == null)
		{
			return;
		}
		if (!npc.getName().contains("Revenant"))
		{
			return;
		}
		if (client.getVarbitValue(VarPlayerID.REVENANT_REWARD_TIME_REMAINING) > 0)
		{
			avariceBonus = 0.35;
			return;
		}
		avariceBonus = 0.2;
	}

	private void getSmokeBattlestaffBonus()
	{
		if (!Spellbook.STANDARD.isActive(client))
		{
			return;
		}
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.SMOKE_BATTLESTAFF))
		{
			smokeBattlestaffBonus = 0.1;
		}
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.MYSTIC_SMOKE_BATTLESTAFF))
		{
			smokeBattlestaffBonus = 0.1;
		}
	}

	private void getVirtusBonus()
	{
		if (!Spellbook.ANCIENT.isActive(client))
		{
			virtusBonus = 0.0;
			return;
		}
		if (EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.HEAD).contains("Virtus"))
		{
			virtusBonus += 0.03;
		}
		if (EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.BODY).contains("Virtus"))
		{
			virtusBonus += 0.03;
		}
		if (EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.LEGS).contains("Virtus"))
		{
			virtusBonus += 0.03;
		}
	}

	@Override
	protected void getPrayerBonus()
	{
		if (PrayerType.MYSTIC_LORE.isActive(client))
		{
			prayerBonus = 0.01;
		}
		if (PrayerType.MYSTIC_MIGHT.isActive(client))
		{
			prayerBonus = 0.02;
		}
		if (PrayerType.MYSTIC_VIGOUR.isActive(client))
		{
			prayerBonus = 0.03;
		}
		if (PrayerType.AUGURY.isActive(client))
		{
			prayerBonus = 0.04;
		}
	}

	private void getElementalWeakness()
	{
		if (opponent == null)
		{
			return;
		}
		NPC npc = (NPC) opponent;
		String npcName = npc.getName();
		if (npcName == null)
		{
			return;
		}
		for (MonsterWeaknesses weakness : MonsterWeaknesses.values())
		{
			if (weakness.getId() != npc.getId())
			{
				continue;
			}
			if (!Objects.equals(activeSpell.getElement(), weakness.getElement()))
			{
				return;
			}
			elementalWeakness += weakness.getSeverity();
			return;
		}
	}

	@Override
	protected void getEffectiveStrength()
	{
	}

	private void getPrimaryMagicDamage()
	{
		getStrengthBonus(); // I.E. visible bonuses
		getShadowBonus();
		getVoidBonus();
		getSalveBonus();
		getAvariceBonus();
		getSmokeBattlestaffBonus();
		getVirtusBonus();
		getPrayerBonus();
		getElementalWeakness();
		double tempBonuses = (magicBonus - voidBonus) * shadowBonus;
		double totalBonus = voidBonus + salveBonus + avariceBonus + smokeBattlestaffBonus + virtusBonus + prayerBonus;
		double elementalWeaknessAddition = Math.floor(baseDamageModifier * elementalWeakness);
		primaryMagicDamage = Math.floor(baseDamageModifier * (1 + tempBonuses + totalBonus) + elementalWeaknessAddition);
	}

	private void getPreHitRoll()
	{

		//TODO: Add slayer, sceptre, and tome bonus
		double totalSlayerBonus = 1.0 + slayerBonus;
		double totalSceptreBonus = 1.0 + sceptreBonus;
		double totalAccursedBonus = 1.0 + accursedSceptreSpecialAttackBonus;
		double totalTomeBonus = 1.0 + tomeBonus;

		double firstFloorCalculation = Math.floor(primaryMagicDamage * totalSlayerBonus);
		double secondFloorCalculation = Math.floor(firstFloorCalculation * totalSceptreBonus);
		double thirdfFloorCalculation = Math.floor(secondFloorCalculation * totalAccursedBonus);
		preHitRoll = Math.floor(thirdfFloorCalculation * totalTomeBonus);
	}

	@Override
	public void CalculateMaxHit()
	{
		reset();
		// Base Damage Modifier
		getBaseDamageModifier();
		// Primary Magic Damage
		getPrimaryMagicDamage();
		// Pre Hit Roll == Hit Roll
		// Don't need to use the hit roll step on the wiki since we're just calculating max hit

		getPreHitRoll();
		double firstFloorCalculation = Math.floor(preHitRoll * (1 + markOfDarknessBonus));
		// Skip castle wars bonus
		// Final Post Hit Roll
		maxHit = Math.floor(firstFloorCalculation * (1 + ahrimsDamnedBonus));
	}

    @Override
    public void calculateNextMaxHitReqs() {
		//TODO: Implement
    }
}
