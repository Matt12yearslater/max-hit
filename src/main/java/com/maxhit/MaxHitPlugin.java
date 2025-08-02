package com.maxhit;


import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import com.maxhit.calculators.MaxHitCalculator;
import com.maxhit.calculators.MaxHitCalculatorFactory;
import com.maxhit.sets.EliteVoidSet;
import com.maxhit.stats.Stats;
import com.maxhit.styles.AttackStyle;
import com.maxhit.styles.StyleFactory;
import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemStats;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Set;
import java.util.HashMap;

@Slf4j
@PluginDescriptor(
		name = "Max Hit",
		description = "Displays current max hit",
		enabledByDefault = true,
		tags = {"max, hit, spec, pvp, magic, spell, combat"}
)


public class MaxHitPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private ItemManager itemManager;
	@Inject
	private MaxHitOverlay myOverlay;
	@Inject
	private MaxHitConfig config;

	@Provides
	MaxHitConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MaxHitConfig.class);
	}

	CombatStyle combatStyle;
	StyleFactory styleFactory;
	public Item[] inventoryItems;
	public ItemContainer equippedItems;
	public MaxHitCalculator maxHitCalculator;
	private final MaxHitCalculatorFactory maxHitCalculatorFactory = new MaxHitCalculatorFactory(client, itemManager);
	public static int booleanToInt(boolean value) { return value ? 1 : 0;}
  	private final HashMap<String, Integer> equippedItemIds = new HashMap<String, Integer>();
	private final HashMap<String, InventoryWeapon> inventoryWeaponsHashMap = new HashMap<>();
	public HashMap<String, InventoryWeapon> map;
	private AttackStyle attackStyle;

	@Override
	public void startUp() throws Exception {
		overlayManager.add(myOverlay);

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(this::start);
		}
	}


	private void start() {

		int currentAttackStyleVarbit = client.getVarpValue(VarPlayerID.COM_MODE);
		int currentEquippedWeaponTypeVarbit = client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY);
		int currentCastingModeVarbit = client.getVarbitValue(VarbitID.AUTOCAST_DEFMODE);
		styleFactory = new StyleFactory(client);
		attackStyle = styleFactory.getAttackStyle(client, currentEquippedWeaponTypeVarbit, currentAttackStyleVarbit,
				currentCastingModeVarbit);
		combatStyle = styleFactory.getCombatType(attackStyle);
		maxHitCalculator = maxHitCalculatorFactory.create(combatStyle);
	}

	@Override
	public void shutDown() throws Exception {
		overlayManager.remove(myOverlay);
		equippedItems = null;
		inventoryItems = null;
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event) {
		final ItemContainer itemContainer = event.getItemContainer();
		//If equipment is changed, recalculate
		if (event.getContainerId() == InventoryID.WORN) {
			maxHitCalculator.setEquippedItems(itemContainer);
		}
	}

	//Update on stat change
	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		Skill[] skills = {
				Skill.STRENGTH, Skill.RANGED, Skill.MAGIC, Skill.HITPOINTS
		};
		for (Skill skill: skills)
		{
			if(event.getSkill() != skill) {
				continue;
			}
			maxHitCalculator.statUpdated();
			return;
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		// COM_MODE = Attack Style
		// COMBAT_WEAPON_CATEGORY = Weapon Style
		if (event.getVarpId() == VarPlayerID.COM_MODE
				|| event.getVarbitId() == VarbitID.COMBAT_WEAPON_CATEGORY
				|| event.getVarbitId() == VarbitID.AUTOCAST_DEFMODE)
		{
			final int currentAttackStyleVarbit = client.getVarpValue(VarPlayerID.COM_MODE);
			final int currentEquippedWeaponTypeVarbit = client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY);
			final int currentCastingModeVarbit = client.getVarbitValue(VarbitID.AUTOCAST_DEFMODE);

			attackStyle = styleFactory.getAttackStyle(client, currentEquippedWeaponTypeVarbit, currentAttackStyleVarbit,
					currentCastingModeVarbit);

		}
	}


}