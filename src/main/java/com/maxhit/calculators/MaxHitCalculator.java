package com.maxhit.calculators;

import com.maxhit.NextMaxHitReqs;
import com.maxhit.sets.EquipmentSet;
import com.maxhit.styles.AttackStyle;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.game.ItemManager;

public abstract class MaxHitCalculator
{
    protected final Client client;
    protected final ItemManager itemManager;
	protected ItemContainer equippedItems;
	@Getter
    protected final Skill skill;
	@Setter
	protected AttackStyle attackStyle;
	protected BonusCalculator bonusCalculator;
    protected double effectiveStrength;
    protected double strengthBonus;
    protected double prayerBonus;
    protected double styleBonus;
	protected double salveBonus;
    protected double voidBonus;

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
		EquipmentSet.setEquippedItems(equippedItems);
		bonusCalculator = new BonusCalculator(client, skill);
        maxHit = 0.0;
        voidBonus = 1.0;
		salveBonus = 0.0;
    }

	public void setEquippedItems(ItemContainer equippedItems)
	{
		this.equippedItems = equippedItems;
		EquipmentSet.setEquippedItems(equippedItems);
	}

	protected void reset()
	{
		styleBonus = 0.0;
		voidBonus = 1.0;
		prayerBonus = 1.0;
	}

	protected abstract void getStyleBonus();

	protected void getVoidBonus()
	{
		voidBonus = bonusCalculator.getVoidBonus();
		if (skill == Skill.MAGIC)
			voidBonus -= 1.0;
	}

	protected void getPrayerBonus()
	{
		prayerBonus = bonusCalculator.getPrayerBonus();
	}

	protected void getEffectiveStrength()
	{
		getStyleBonus();
		getPrayerBonus();
		getVoidBonus();
		// getValue() gets boosted value
		effectiveStrength = Math.floor(Math.floor((getSkillLevel() * prayerBonus) + styleBonus + 8) * voidBonus);
	}

    protected void getStrengthBonus()
	{
		strengthBonus = StrengthBonusCalculator.getStrengthBonus(equippedItems, itemManager, skill);
	}

    public abstract void calculateMaxHit();

	protected int getSkillLevel()
	{
		return client.getBoostedSkillLevel(this.skill);
	}

	protected void getSalveBonus()
	{
		salveBonus = bonusCalculator.getSalveBonus(equippedItems, opponent);
	}

	protected abstract void calculateNextMaxHitRequirements();
}
