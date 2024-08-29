package com.maxhit;


import com.google.inject.Provides;
import com.maxhit.sets.DharokSet;
import com.maxhit.sets.EliteVoidSet;
import com.maxhit.sets.ObsidianSet;
import com.maxhit.sets.VoidSet;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.http.api.item.ItemStats;
import net.runelite.http.api.item.ItemEquipmentStats;

import javax.inject.Inject;
import java.util.HashMap;

import static com.maxhit.AttackStyle.*;

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

	public static int booleanToInt(boolean value) { return value ? 1 : 0;}
	final int MELEE = 0;
	final int RANGED = 1;
	final int MAGE = 2;
	final int TRIDENT = 3;
	@Getter
	public ItemContainer equippedItems;
	@Getter
	public Item[] inventoryItems;
	private final VoidSet voidSetChecker = new VoidSet();
	private final EliteVoidSet eliteVoidSetChecker = new EliteVoidSet();
	private final DharokSet dharokSetChecker = new DharokSet();
	private final ObsidianSet obsidianSetChecker = new ObsidianSet();
	private final HashMap<String, Integer> equippedItemIds = new HashMap<String, Integer>();
	private final HashMap<String, InventoryWeapon> inventoryWeaponsHashMap = new HashMap<>();
	private final HashMap<String, EquipmentInventorySlot> slotNametoIdMap = new HashMap<String, EquipmentInventorySlot>() {{
		put("head", EquipmentInventorySlot.HEAD);
		put("cape", EquipmentInventorySlot.CAPE);
		put("amulet", EquipmentInventorySlot.AMULET);
		put("weapon", EquipmentInventorySlot.WEAPON);
		put("body", EquipmentInventorySlot.BODY);
		put("shield", EquipmentInventorySlot.SHIELD);
		put("legs", EquipmentInventorySlot.LEGS);
		put("gloves", EquipmentInventorySlot.GLOVES);
		put("boots", EquipmentInventorySlot.BOOTS);
		put("ring", EquipmentInventorySlot.RING);
		put("ammo", EquipmentInventorySlot.AMMO);
	}};
	public HashMap<String, InventoryWeapon> map;

	public double equipedWeaponMaxHit = 0.0;

    @Override
	public void startUp() throws Exception {
		overlayManager.add(myOverlay);

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(this::start);
		}

	}
	private void start() {
		calculateEquippedWeaponMaxHit();
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
		if (event.getContainerId() == InventoryID.EQUIPMENT.getId()) {
			calculateEquippedWeaponMaxHit();
			return;
		}
		if (event.getContainerId() != InventoryID.INVENTORY.getId()) {
			return;
		}
		inventoryItems = itemContainer.getItems();
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
			calculateEquippedWeaponMaxHit();
			return;
		}

	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarpId() == VarPlayer.ATTACK_STYLE
				|| event.getVarbitId() == Varbits.EQUIPPED_WEAPON_TYPE
				|| event.getVarbitId() == Varbits.DEFENSIVE_CASTING_MODE)
		{
			final int currentAttackStyleVarbit = client.getVarpValue(VarPlayer.ATTACK_STYLE);
			final int currentEquippedWeaponTypeVarbit = client.getVarbitValue(Varbits.EQUIPPED_WEAPON_TYPE);
			final int currentCastingModeVarbit = client.getVarbitValue(Varbits.DEFENSIVE_CASTING_MODE);

            updateAttackStyle(currentEquippedWeaponTypeVarbit, currentAttackStyleVarbit,
					currentCastingModeVarbit);
		}
	}

	public void getEquippedItems()
	{
		final ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment != null)
		{
			equippedItems = equipment;
			map = equipableItems();
		}

	}

	public void getEquippedItemsIds() {
		if (equippedItems == null)
			return;
		for (String name: slotNametoIdMap.keySet())
		{
			Item item = equippedItems.getItem(slotNametoIdMap.get(name).getSlotIdx());
			if (item == null)
			{
				equippedItemIds.put(name, -1);
				continue;
			}
			equippedItemIds.put(name, item.getId());
		}
	}

	//Info for all styles:
	//Item set bonus, combat type, prayer bonus, weapon, attack style
	private AttackStyle attackStyle;

	private double getEffectiveStrength(int strengthLevel, double prayerBonus, int styleBonus, int combatType) {
		return Math.floor((Math.floor(strengthLevel * prayerBonus) + styleBonus + 8) * voidBonus(combatType));
	}

	private double getBaseDamage(double effectiveStrength, int strengthBonus) {
		return Math.floor(0.5 + (effectiveStrength * (strengthBonus + 64) / 640));
	}
	private double getBonusDamage(int baseDamage, double specialBonus) {
		return Math.floor(baseDamage * specialBonus);
	}
	private int getWeaponType(String name) {
		if (name.contains("bow") ||
				name.contains("knif") ||
				name.contains("dart") ||
				name.contains("throw") ||
				name.contains("xil-ul") ||
				name.contains("chompa") ||
				name.contains("blowpipe") ||
				name.contains("ballista")) {
			return RANGED;
		}
		if (name.contains("rident")) {
			return TRIDENT;
		}
		return MELEE;
	}

	//Combat type of equipped weapon (Melee, ranged, magic, other)
	public int combatType() {

		if (attackStyle == ACCURATE || attackStyle == AGGRESSIVE ||
				attackStyle == CONTROLLED || attackStyle == DEFENSIVE) {
			return MELEE;
		}
		if (attackStyle.getName().contains("ang")) {
			return RANGED;
		}
		if (attackStyle.getName().contains("Casting")) {
			return MAGE;
		}
		return -1;
	}

	//Prayer Bonus
	public double getMeleePrayerBonus() {
		//Melee prayers
		if (client.isPrayerActive(Prayer.BURST_OF_STRENGTH)) {
			return 1.05;
		}
		if (client.isPrayerActive(Prayer.SUPERHUMAN_STRENGTH)) {
			return 1.10;
		}
		if (client.isPrayerActive(Prayer.ULTIMATE_STRENGTH)) {
			return 1.15;
		}
		if (client.isPrayerActive(Prayer.CHIVALRY)) {
			return 1.18;
		}
		if (client.isPrayerActive(Prayer.PIETY)) {
			return 1.23;
		}
		return 1.0;
	}

	public double getRangedPrayerBonus() {
		//Ranged prayers
		if (client.isPrayerActive(Prayer.SHARP_EYE)) { return 1.05; }
		if (client.isPrayerActive(Prayer.HAWK_EYE)) { return 1.1; }
		if (client.isPrayerActive(Prayer.EAGLE_EYE)) { return 1.15; }
		if (client.isPrayerActive(Prayer.RIGOUR)) { return 1.23; }
		return 1;
	}

	//Attack style (Aggressive, Defensive, etc..) Doesn't differentiate
	//between accurate and rapid for ranged, so use isRangingAccurate() for that

	private void updateAttackStyle(int equippedWeaponType, int attackStyleIndex, int castingMode)
	{
		AttackStyle[] attackStyles = WeaponType.getWeaponType(equippedWeaponType).getAttackStyles();
		if (attackStyleIndex < attackStyles.length)
		{
			attackStyle = attackStyles[attackStyleIndex];
			if (attackStyle == null)
			{
				attackStyle = OTHER;
			}
			else if ((attackStyle == CASTING) && (castingMode == 1))
			{
				attackStyle = DEFENSIVE_CASTING;
			}
		}
	}

	//Using accurate ranging style, gives +3 boost to style bonus
	public boolean isRangingAccurate() {

		//if 0 for ranged weapons, style is accurate
		int styleNum = client.getVarpValue(VarPlayer.ATTACK_STYLE);
		return (attackStyle.name().contains("ang") && styleNum == 0);
	}

	public int getStyleBonus() {

		if (attackStyle.name().equalsIgnoreCase("Aggressive")) { return 3; }
		if (attackStyle.name().equalsIgnoreCase("Controlled")) { return 1; }
		if (isRangingAccurate()) { return 3; }
		if (attackStyle.name().equalsIgnoreCase("Longrange")) { return 1; }
		return 0;
	}
	//MELEE FACTORS -- Get all factors that specifically affect melee max hit:
	//Boosted strength level, equipment bonus, attack style bonus from all styles section

	public int getEquipmentBonus(int style) {
		int bonus = 0;
		//get str bonus of worn equipment
		for (int id : equippedItemIds.values()) {
			final ItemStats stats = itemManager.getItemStats(id, false);
			if (stats == null) {
				continue;
			}
			final ItemEquipmentStats currentEquipment = stats.getEquipment();
			switch (style) {
				case MELEE:
					bonus += currentEquipment.getStr();
					break;
				case RANGED:
					bonus += currentEquipment.getRstr();
					break;
			}
		}
		return bonus;
	}

	public int getMeleeStrengthBonus() {
		return getEquipmentBonus(MELEE);
	}

	public int getRangedStrengthBonus() {
		if (itemManager.getItemComposition(equippedItemIds.get("weapon")).isStackable()) {
			return getEquipmentBonus(RANGED) - itemManager.getItemStats(equippedItemIds.get("ammo"), false).getEquipment().getRstr();
		}
		return getEquipmentBonus(RANGED);
	}


	public void calculateEquippedWeaponMaxHit() {
		getEquippedItems();
		getEquippedItemsIds();
		equipedWeaponMaxHit = maxHitBase(combatType());
	}

	private double getMaxMeleePrayerBonus(int prayerLevel, int defenseLevel) {
		if (prayerLevel < 13) {
			return 1.05; // Burst of Strength
		} else if (prayerLevel < 31) {
			return 1.1; // Superhuman Strength
		} else if (prayerLevel < 60) {
			return 1.15; // Ultimate Strength
		} else if (prayerLevel < 70 && defenseLevel >= 65) {
			return 1.18; // Chivalry
		} else if (prayerLevel >= 70 && defenseLevel >= 70) {
			return 1.23; // Piety
		}
		return 1;
	}

	private double getMaxRangedPrayerBonus(int prayerLevel, int defenseLevel) {
		if (prayerLevel < 8) {
			return 1.05; // Sharp Eye
		} else if (prayerLevel < 26) {
			return 1.1; // Hawk Eye
		} else if (prayerLevel < 74) {
			return 1.15; // Eagle Eye
		} else if (defenseLevel >= 70) {
			return 1.23; // Rigour
		}
		return 1;
	}

	public HashMap equipableItems() {
		inventoryWeaponsHashMap.clear();
		if (inventoryItems == null) {
			return null;
		}
		if (inventoryItems.length == 0) {
			return null;
		}

		if (equippedItemIds.size() == 0)
			return null;

		//MAX HIT FACTORS
		int style = 3; //have to assume, we can't actually get this value
		double prayerBonus = 1;
		int strengthBonus = 0;
		int strengthOrRangedLevel = 0;
		int defenseLevel = client.getRealSkillLevel(Skill.DEFENCE);
		int prayerLevel = client.getRealSkillLevel(Skill.PRAYER);
		double effectiveStrength = 0.0;
		int equippedWeaponStr = 0;
		int equippedWeaponRstr = 0;
		int equippedShieldStr = 0;
		int equippedShieldRstr = 0;
		int equippedAmmoRstr = 0;
		for (Item item : inventoryItems) {
			int ID = item.getId();
			if (ID == -1) {
				continue;
			}
			ItemStats itemStats = itemManager.getItemStats(ID, false);
			if (itemStats == null) {
				continue;
			}
			if (!itemStats.isEquipable()) {
				continue;
			}
			ItemEquipmentStats itemEquipmentStats = itemStats.getEquipment();
			if (itemEquipmentStats.getSlot() != 3) {
				continue;
			}

			boolean isWeaponEquipped = equippedItemIds.get("weapon") != -1;
			boolean isShieldEquipped = equippedItemIds.get("shield") != -1;
			boolean isAmmoEquipped = equippedItemIds.get("ammo") != -1;
			if (isWeaponEquipped) {
				equippedWeaponStr = itemManager.getItemStats(equippedItemIds.get("weapon"), false).getEquipment().getStr();
				equippedWeaponRstr = itemManager.getItemStats(equippedItemIds.get("weapon"), false).getEquipment().getRstr();
			}
			if (isShieldEquipped) {
				equippedShieldStr = itemManager.getItemStats(equippedItemIds.get("shield"), false).getEquipment().getStr();
				equippedShieldRstr = itemManager.getItemStats(equippedItemIds.get("shield"), false).getEquipment().getRstr();
			}
			if (isAmmoEquipped) {
				equippedAmmoRstr = itemManager.getItemStats(equippedItemIds.get("ammo"), false).getEquipment().getRstr();
			}
			String name = client.getItemDefinition(ID).getName();
			boolean twoHanded = itemEquipmentStats.isTwoHanded();
			int weaponType = getWeaponType(name);
			inventoryWeaponsHashMap.put(name, new InventoryWeapon());
			inventoryWeaponsHashMap.get(name).ID = ID;
			inventoryWeaponsHashMap.get(name).name = name;
			inventoryWeaponsHashMap.get(name).isTwoHanded = twoHanded;
			inventoryWeaponsHashMap.get(name).weaponType = weaponType;
			//Invent weapon is melee
			switch (weaponType) {
				case MELEE:
					inventoryWeaponsHashMap.get(name).strBonus = itemManager.getItemStats(ID, false).getEquipment().getStr();
					strengthOrRangedLevel = strengthLevel();
					strengthBonus += getMeleeStrengthBonus();
					prayerBonus = getMaxMeleePrayerBonus(prayerLevel, defenseLevel);
					//invent weapon is 2 handed, take away shield bonus
					if (twoHanded) {
						strengthBonus -= equippedShieldStr * booleanToInt(isShieldEquipped);
					}
					strengthBonus -= equippedWeaponStr * booleanToInt(isWeaponEquipped);
					strengthBonus += inventoryWeaponsHashMap.get(name).strBonus;
					break;
				//Invent weapon is ranged
				case RANGED:
					inventoryWeaponsHashMap.get(name).strBonus = itemManager.getItemStats(ID, false).getEquipment().getRstr();
					strengthOrRangedLevel = rangedLevel();
					strengthBonus += getRangedStrengthBonus();
					prayerBonus = getMaxRangedPrayerBonus(prayerLevel, defenseLevel);
					//If inventory weapon is stackable (throwable)
					if (itemManager.getItemComposition(ID).isStackable()) {
						strengthBonus -= equippedAmmoRstr * booleanToInt(isAmmoEquipped);
					}
					if(twoHanded) {
						strengthBonus -= equippedShieldRstr * booleanToInt(isShieldEquipped);
					}
					strengthBonus -= equippedWeaponRstr * booleanToInt(isWeaponEquipped);
					strengthBonus += inventoryWeaponsHashMap.get(name).strBonus;
					// get ranged prayer bonus
					prayerBonus = getMaxRangedPrayerBonus(prayerLevel, defenseLevel);
					inventoryWeaponsHashMap.get(name).strBonus = itemEquipmentStats.getRstr();
					break;

				//Special case: Trident
				case TRIDENT:
					double maxTrident;
					double magicLevel = magicLevel();
					if (name.contains("swamp")) {
						maxTrident = Math.floor(magicLevel / 3) - 2;
						inventoryWeaponsHashMap.get(name).maxHitBase = Math.floor(maxTrident * magicBonus());
						continue;
					}
					if (name.contains("seas")) {
						maxTrident = Math.floor(magicLevel / 3) - 5;
						inventoryWeaponsHashMap.get(name).maxHitBase = Math.floor(maxTrident * magicBonus());
						continue;
					}
					break;
			}

			// have to assume using the best prayer for level
			effectiveStrength = getEffectiveStrength(strengthOrRangedLevel, prayerBonus, style, weaponType);
			double baseMax = getBaseDamage(effectiveStrength, strengthBonus);
			inventoryWeaponsHashMap.get(name).maxHitBase = baseMax * setBonus(combatType());
			inventoryWeaponsHashMap.get(name).maxHitSpec = maxHitSpec(name, baseMax);
		}
		return inventoryWeaponsHashMap;
	}

	public double voidBonus(int combatType) {
		switch(combatType) {
			case MELEE:
				if (voidSetChecker.isWearingVoid(MELEE)) {
					return 1.1;
				}
				if (eliteVoidSetChecker.isWearingEliteVoid(MELEE)) {
					return 1.1;
				}
				break;
			case RANGED:
				//Ranged sets
				if (voidSetChecker.isWearingVoid((RANGED))) {
					return 1.1;
				}
				if (eliteVoidSetChecker.isWearingEliteVoid(RANGED)) {
					return 1.125;
				}
				break;
			case MAGE:
				if (voidSetChecker.isWearingVoid(MAGE)) {
					return 1.0;
				}
				if (eliteVoidSetChecker.isWearingEliteVoid(MAGE)) {
					return 1.025;
				}
				break;
			default:
				return 1;
		}
		return 1;
	}
	public double setBonus(int combatType) {
		//Melee sets
		if (combatType == MELEE) {
			if (dharokSetChecker.isWearingSet()) {
				double baseHitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
				double currentHitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
				return 1 + (((baseHitpoints - currentHitpoints)/100) * baseHitpoints/100);
			}

			if (obsidianSetChecker.isWearingMaxSet()) {
				return 1.32;
			}
			if (obsidianSetChecker.isWearingWeaponAndNecklace()) {
				return 1.2;
			}
			if (obsidianSetChecker.isWearingSet()) {
				return 1.1;
			}
		}
		//Magic sets applied directly in max hit calculation
		return 1;
	}
	//Weapon name
	public String weaponName() {

		final ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
		if (container == null) {
			return "No weapon or items";
		}
		Item[] items = container.getItems();
		if (items.length >= EquipmentInventorySlot.WEAPON.getSlotIdx()) {
			final Item weapon = items[EquipmentInventorySlot.WEAPON.getSlotIdx()];
			if (weapon.getId() > 512) {
				int weaponID = weapon.getId();
				return client.getItemDefinition(weaponID).getName();
			}
		}
		return "no weapon";
	}
	//MAGIC FACTORS -- An option in config menu, not based on equipped weapon
	//Get all factors that specifically affect magic max hit:
	//Boosted magic level (for trident), spell, equipment bonus

	//Visible levels
	public int strengthLevel() {return client.getBoostedSkillLevel(Skill.STRENGTH);}
	public int magicLevel() { return client.getBoostedSkillLevel(Skill.MAGIC); }
	public int rangedLevel() { return client.getBoostedSkillLevel(Skill.RANGED); }


	public int spellDamage() {
		MagicSpell spell = config.spellChoice();
		if (spell.element == Element.GOD && config.applyCharge()) {
			return 30;
		}
		return spell.damage;
	}
	public boolean tomeOfFireEquipped() {
		return equippedItemIds.get("shield") == ItemID.TOME_OF_FIRE;
	}

	//Magic equipment damage bonus, includes void bonus
	//returns % damage increase
	public double magicBonus() {
		double magicEquipmentBonus = 0;

		int[] ids = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();
		for (int x : ids) {
			if (x > 512) {
				int id = x - 512;
				final ItemStats stats = itemManager.getItemStats(id, false);
				final ItemEquipmentStats currentEquipment = stats.getEquipment();
				magicEquipmentBonus += currentEquipment.getMdmg();

			}
		}
		if (eliteVoidSetChecker.isWearingEliteVoid(MAGE)) {
			magicEquipmentBonus += 2.5;
		}
		if (weaponName().contains("moke battle") && config.spellChoice().spellbook == Spellbook.NORMAL) {
			magicEquipmentBonus += 10;
		}
		return 1 + (magicEquipmentBonus/100);
	}

	//RANGED FACTORS -- Get all factors that specifically affect ranged max hit:
	//Boosted ranged level, equipment bonus, attack style bonus from all styles section

	//Visible ranged level

	public String ammoName() {
		Item[] items = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
		if (items.length == 14) {
			final Item ammo = items[EquipmentInventorySlot.AMMO.getSlotIdx()];
			int ammoID = ammo.getId();
			if (ammoID != -1) {
				return client.getItemDefinition(ammoID).getName();
			}
		}
		return "None";
	}
	//MAX HIT CALCULATIONS -- including special attack
	//Does not account for special cases such as
	//slayer, twisted bow, wilderness weapons, etc.

	//Max hit base for melee and ranged, as well as tridents
	//Displayed by "Max Hit:" overlay

	public double maxHitBase(int combatType) {
		int style = getStyleBonus();
		double pray = 0.0;
		double setBonus = setBonus(combatType());
		int equipment = 0;
		int strengthOrRangedLevel = 0;
		switch(combatType) {
			case MELEE:
				strengthOrRangedLevel = strengthLevel();
				pray = getMeleePrayerBonus();
				equipment = getMeleeStrengthBonus();
				break;
			case RANGED:
				strengthOrRangedLevel = rangedLevel();
				pray = getRangedPrayerBonus();
				equipment = getRangedStrengthBonus();
				break;
			//Special case: Trident
			case TRIDENT:
				double maxTrident;
				double magicLevel = magicLevel();
				if (weaponName().contains("swamp")) {
					maxTrident = Math.floor(magicLevel / 3) - 2;
					return Math.floor(maxTrident * magicBonus());
				}
				if (weaponName().contains("seas")) {
					maxTrident = Math.floor(magicLevel / 3) - 5;
					return Math.floor(maxTrident * magicBonus());
				}
				break;
		}
		//Actual calculation for base damage (base damage refers to hit without special attack
		double effectiveStrengthLevel = getEffectiveStrength(strengthOrRangedLevel, pray, style, combatType());
		double baseMax = Math.floor((effectiveStrengthLevel * (equipment + 64) / 640) + 0.5);
		return Math.floor(baseMax * setBonus);
	}

	//Max hit calculation for magic
	//Displayed by "Max Magic Hit:" overlay
	public double maxMagicHitBase() {
		if (config.showMagic()) {
			int spellDamage = spellDamage();
			double magicEquipment = magicBonus();

			double base = spellDamage * magicEquipment;
			if (config.spellChoice().element == Element.FIRE && tomeOfFireEquipped()) {
				base = Math.floor(base) * 1.5;
			}
			return base;
		}
		return 0;
	}

	/**still need to add mage spec (nightmare staff). need to find documentation on formula**/
	//Returns -1 if special attack does not affect max hit
	//Displayed by "Max Special:" overlay
	public double maxHitSpec(String weaponName, double maxHitBase) {

		if (weaponName.contains("rossbo") && ammoName().contains("(e)")) {
			if (ammoName().contains("iamond")) {
				return Math.floor(maxHitBase) * 1.15;
			}
			if (ammoName().contains("nyx")) {
				return Math.floor(maxHitBase) * 1.2;
			}
			if (ammoName().contains("ragonstone")) {
				return Math.floor(maxHitBase) + Math.floor(rangedLevel() * .2);
			}
			if (ammoName().contains("pal")) {
				return Math.floor(maxHitBase) + Math.floor(rangedLevel() * .1);
			}
			if (ammoName().contains("earl")) {
				return Math.floor(maxHitBase) + Math.floor(rangedLevel() * .05);
			}
		}

		if (weaponName.equalsIgnoreCase("Saradomin sword")) {
			return 16 + (Math.floor(maxHitBase) * specialAttackDamageMultiplier(weaponName));
		}
		if (weaponName.equalsIgnoreCase("Granite hammer")) {
			return Math.floor(maxHitBase) + 5;
		}
		if (specialAttackDamageMultiplier(weaponName) != 1) {
			if (weaponName.contains("dagger") || weaponName.equalsIgnoreCase("Dark bow")) {
				return Math.floor(Math.floor(maxHitBase) * specialAttackDamageMultiplier(weaponName)) * 2;
			}
			return Math.floor(maxHitBase) * specialAttackDamageMultiplier(weaponName);
		}


		return -1;
	}

	public double specialAttackDamageMultiplier(String weaponName) {

		if (weaponName.contains("Abyssal dagger")) {
			return .85;
		}
		if (weaponName.equalsIgnoreCase("Dragon claws")) {
			return 1.98;
		}
		if (weaponName.contains("Dragon dagger")) {
			return 1.15;
		}
		if (weaponName.equalsIgnoreCase("Dragon halberd")) {
			return 1.1;
		}
		if (weaponName.equalsIgnoreCase("Dragon longsword")) {
			return 1.25;
		}
		if (weaponName.equalsIgnoreCase("Dragon mace")) {
			return 1.5;
		}
		if (weaponName.equalsIgnoreCase("Dragon sword")) {
			return 1.25;
		}
		if (weaponName.equalsIgnoreCase("Dragon warhammer")) {
			return 1.5;
		}
		if (weaponName.equalsIgnoreCase("Armadyl godsword")) {
			return 1.375;
		}
		if (weaponName.equalsIgnoreCase("Bandos godsword")) {
			return 1.21;
		}
		if (weaponName.equalsIgnoreCase("Saradomin godsword")) {
			return 1.1;
		}
		if (weaponName.equalsIgnoreCase("Saradomin sword")) {
			return 1.1; //also add + 16 for magic damage
		}
		if (weaponName.equalsIgnoreCase("Saradomin's blessed sword")) {
			return 1.25;
		}
		if (weaponName.equalsIgnoreCase("Zamorak Godsword")) {
			return 1.1;
		}
		if (weaponName.equalsIgnoreCase("Granite hammer")) {
			return 1; // add extra 5 damage
		}
		if (weaponName.equalsIgnoreCase("Barrelchest anchor")) {
			return 1.1;
		}
		if (weaponName.equalsIgnoreCase("Crystal halberd")) {
			return 1.1;
		}
		if (weaponName.contains("ballista")) {
			return 1.25;
		}
		if (weaponName.equalsIgnoreCase("Dragon crossbow")) {
			return 1.2;
		}
		if (weaponName.equalsIgnoreCase("Dark bow")) {
			if (ammoName().contains("Dragon arr")) {
				return 1.5;
			}
			return 1.3;
		}
		if (weaponName.equalsIgnoreCase("Toxic blowpipe")) {
			return 1.5;
		}
		return 1;
	}

	public NextMaxHit nextMaxHit() {
		NextMaxHit nextMaxHit = new NextMaxHit();
		if (combatType() == MELEE || combatType() == RANGED || weaponName().contains("rident")) {
			nextMaxHit = nextMaxHitBase();
		}
		else if (combatType() == MAGE) {
			nextMaxHit = nextMagicMaxHitBase();
		}
		return nextMaxHit;
	}

	private NextMaxHit nextMaxHitBase() {
		double baseMax = maxHitBase(combatType()) + 1;
		NextMaxHit reqs = new NextMaxHit();

		int style = getStyleBonus();
		double prayerBonus = 1.0;
		double setBonus = setBonus(combatType());
		int equipment = 0;
		int strengthOrRangedLevel = 0;

		switch(combatType()) {
			case MELEE:
				strengthOrRangedLevel = client.getRealSkillLevel(Skill.STRENGTH);
				equipment = getMeleeStrengthBonus();
				prayerBonus = getMeleePrayerBonus();
				break;
			case RANGED:
				strengthOrRangedLevel = rangedLevel();
				equipment = getRangedStrengthBonus();
				prayerBonus = getRangedPrayerBonus();
				break;
			case TRIDENT:
				final double magicLevel = magicLevel();
				double magicLevelNew = magicLevel;
				if (weaponName().contains("swamp")) {
					magicLevelNew = (Math.ceil(baseMax / magicBonus()) + 2) * 3;
				}
				if (weaponName().contains("seas")) {
					magicLevelNew = (Math.ceil(baseMax / magicBonus()) + 5) * 3;
				}

				final double magicDiff = magicLevelNew - magicLevel;

				reqs.magicLevels = (int) magicDiff;
				return reqs;
		}

		// Calculate effective strength level
		//double effectiveStrengthLevel = Math.floor((Math.floor(level * pray) + style + 8));
		double effectiveStrengthLevel = getEffectiveStrength(strengthOrRangedLevel, prayerBonus, style, combatType());

		// Remove non-void set effects
		if (!(voidSetChecker.isWearingVoid() || eliteVoidSetChecker.isWearingEliteVoid())) {
			baseMax /= setBonus;
		}

		final double equipmentNew = Math.ceil(((baseMax - 0.5) * 640 / effectiveStrengthLevel) - 64);
		final double equipmentDiff = equipmentNew - equipment;

		double reverseEffectiveStrengthLevel = Math.ceil((baseMax - 0.5) * 640 / (equipment + 64) - 8 - style);
		// Remove void set effects
		if (voidSetChecker.isWearingVoid() || eliteVoidSetChecker.isWearingEliteVoid()) {
			reverseEffectiveStrengthLevel /= setBonus;
		}

		final double levelNew = Math.ceil(reverseEffectiveStrengthLevel / prayerBonus);
		final double levelDiff = levelNew - strengthOrRangedLevel;

		final double prayerNew = reverseEffectiveStrengthLevel / strengthOrRangedLevel;
		final double prayerDiff = Math.ceil((prayerNew - prayerBonus) * 100);

		if (combatType() == MELEE) {
			reqs.strengthBonus = (int) equipmentDiff;
			reqs.strengthLevels = (int) levelDiff;
		}
		else if (combatType() == RANGED) {
			reqs.rangedBonus = (int) equipmentDiff;
			reqs.rangedLevels = (int) levelDiff;
		}
		reqs.prayerBoost = (int) prayerDiff;

		return reqs;
	}

	private NextMaxHit nextMagicMaxHitBase() {
		NextMaxHit nextMaxHit = new NextMaxHit();

		double base = maxMagicHitBase() + 1;
		if (config.spellChoice().element == Element.FIRE && tomeOfFireEquipped()) {
			base = Math.ceil(base / 1.5);
		}
		final double equipmentNew = base / spellDamage();
		final double equipmentDiff = Math.ceil((equipmentNew - magicBonus()) * 100);

		nextMaxHit.magicBonus = (int) equipmentDiff;

		return nextMaxHit;
	}
}