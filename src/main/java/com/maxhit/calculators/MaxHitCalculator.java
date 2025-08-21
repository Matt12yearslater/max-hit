package com.maxhit.calculators;

import com.maxhit.NextMaxHitReqs;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.monsters.UndeadMonsters;
import com.maxhit.sets.EliteVoidSet;
import com.maxhit.sets.VoidSet;
import com.maxhit.styles.AttackStyle;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;

public abstract class MaxHitCalculator
{

    protected final Client client;
    protected final ItemManager itemManager;
	@Getter
    protected final Skill skill;
	@Setter
	protected AttackStyle attackStyle;
    protected double effectiveStrength;
    protected int strengthBonus;
    protected double prayerBonus;
    protected double styleBonus;
	//Default these values to melee and update for magic/ranged
	protected double salveRegularBonus = 0.1667;
	protected double salveEnchantedBonus = 0.2;
	protected double salveImbuedBonus = 0.1667;
	protected double salveEnchantedImbuedBonus = 0.2;
	protected double salveBonus;
    protected double voidBonus;
    protected VoidSet voidSetChecker;
    protected EliteVoidSet eliteVoidSetChecker;
	@Setter
    protected ItemContainer equippedItems;
	@Nullable
    public Actor opponent;
	@Getter
    protected double maxHit;
	@Getter
	protected NextMaxHitReqs nextMaxHitReqs;

	@Inject
    protected MaxHitCalculator (Client client, ItemManager itemManager, Skill skill, AttackStyle attackStyle)
    {
		this.client = client;
		this.itemManager = itemManager;
        this.skill = skill;
		this.attackStyle = attackStyle;
		this.equippedItems = client.getItemContainer(InventoryID.WORN);
        maxHit = 0.0;
        voidBonus = 1.0;
		salveBonus = 0.0;
        voidSetChecker = new VoidSet(client);
        eliteVoidSetChecker = new EliteVoidSet(client);
    }

	protected void reset()
	{
		styleBonus = 0.0;
		voidBonus = 1.0;
		prayerBonus = 1.0;
	}

	protected abstract void getPrayerBonus();

    protected abstract void getEffectiveStrength();

    protected abstract void getStrengthBonus();

    public abstract void CalculateMaxHit();

	protected int getSkillLevel()
	{
		return client.getBoostedSkillLevel(this.skill);
	}

	protected void getSalveBonus()
	{
		// Check if wearing a salve amulet
		if (!EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.AMULET).contains("Salve"))
		{
			salveBonus = 0.0;
			return;
		}
		// Check if opponent is undead
		NPC npc = (NPC) opponent;

		if (npc == null)
		{
			salveBonus = 0.0;
			return;
		}

		if (!UndeadMonsters.ID_LIST.contains(npc.getId()))
		{
			salveBonus = 0.0;
			return;
		}
		// Salve Amulet
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.AMULET, ItemID.CRYSTALSHARD_NECKLACE))
		{
			salveBonus = salveRegularBonus;
			return;
		}
		// Salve Amulet (e)
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.AMULET, ItemID.LOTR_CRYSTALSHARD_NECKLACE_UPGRADE))
		{
			salveBonus = salveEnchantedBonus;
			return;
		}
		// Salve(i)
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.AMULET, ItemID.NZONE_SALVE_AMULET))
		{
			salveBonus = salveImbuedBonus;
		}
		// Salve(ei)
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.AMULET, ItemID.NZONE_SALVE_AMULET_E))
		{
			salveBonus = salveEnchantedImbuedBonus;
		}
	}

	protected abstract void calculateNextMaxHitReqs();
}
