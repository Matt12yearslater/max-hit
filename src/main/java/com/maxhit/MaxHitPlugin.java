package com.maxhit;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.http.api.item.ItemStats;
import net.runelite.http.api.item.ItemEquipmentStats;
import static com.maxhit.AttackStyle.CASTING;
import static com.maxhit.AttackStyle.DEFENSIVE_CASTING;
import static com.maxhit.AttackStyle.OTHER;

import javax.inject.Inject;


@Slf4j
@PluginDescriptor(
		name = "Max Hit",
		description = "Displays current max hit",
		enabledByDefault = false,
		tags = {"max, hit, spec, pvp, magic, spell, combat"}
)


public class MaxHitPlugin extends Plugin {

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private MaxHitOverlay myOverlay;

	@Inject
	private MaxHitConfig config;

	@Provides
	MaxHitConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(MaxHitConfig.class);
	}

	@Override
	public void startUp() throws Exception { overlayManager.add(myOverlay); }

	@Override
	public void shutDown() throws Exception { overlayManager.remove(myOverlay); }

	//Info for all styles:
	//Item set bonus, combat type, prayer bonus, weapon, attack style

	private int attackStyleVarbit = -1;
	private int equippedWeaponTypeVarbit = -1;
	private int castingModeVarbit = -1;
	private AttackStyle attackStyle;

	// Item sets
	// Still need to add Inquisitor and crystal
	public String itemSet() {
		final ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment != null) {
			Item[] items = equipment.getItems();
			if (items != null) {
				// IDs of relevant equipment slots
				int bootsID = items[EquipmentInventorySlot.HEAD.getSlotIdx()].getId();
				int bodyID = items[EquipmentInventorySlot.BODY.getSlotIdx()].getId();
				int legsID = items[EquipmentInventorySlot.LEGS.getSlotIdx()].getId();
				int helmID = items[EquipmentInventorySlot.HEAD.getSlotIdx()].getId();
				int amuletID = items[EquipmentInventorySlot.AMULET.getSlotIdx()].getId();
				int glovesID = items[EquipmentInventorySlot.GLOVES.getSlotIdx()].getId();
				int weaponID = items[EquipmentInventorySlot.WEAPON.getSlotIdx()].getId();

				//Item sets
				//Normal void
				if (client.getItemDefinition(glovesID).getName().equalsIgnoreCase("Void knight gloves")) {
					if (client.getItemDefinition(bodyID).getName().equalsIgnoreCase("Void knight top") &&
							client.getItemDefinition(legsID).getName().equalsIgnoreCase("Void knight robe")) {
						if (client.getItemDefinition(helmID).getName().equalsIgnoreCase("Void ranger helm")) {
							return "Void range";
						}
						if (client.getItemDefinition(helmID).getName().equalsIgnoreCase("Void melee helm")) {
							return "Void melee";
						}
					}
					//Elite void
					if (client.getItemDefinition(bodyID).getName().equalsIgnoreCase("Elite void top") &&
							client.getItemDefinition(legsID).getName().equalsIgnoreCase("Elite void robe")) {
						if (client.getItemDefinition(helmID).getName().equalsIgnoreCase("Void ranger helm")) {
							return "Elite void range";
						}
						if (client.getItemDefinition(helmID).getName().equalsIgnoreCase("Void melee helm")) {
							return "Void melee";
						}
						if (client.getItemDefinition(helmID).getName().equalsIgnoreCase("Void mage helm")) {
							return "Elite void mage";
						}
					}
				}
				//Dharok
				if (client.getItemDefinition(helmID).getName().contains("harok") &&
						client.getItemDefinition(bodyID).getName().contains("harok") &&
						client.getItemDefinition(legsID).getName().contains("harok") &&
						client.getItemDefinition(weaponID).getName().contains("harok")) {
					return "Dharok";
				}
				//Obsidian
				if (client.getItemDefinition(weaponID).getName().contains("xil") || client.getItemDefinition(weaponID).getName().contains("ket")) {
					if (client.getItemDefinition(helmID).getName().contains("Obsidian") &&
							client.getItemDefinition(bodyID).getName().contains("Obsidian") &&
							client.getItemDefinition(legsID).getName().contains("Obsidian")) {
						if (client.getItemDefinition(amuletID).getName().contains("serker")) {
							return "Max obsidian";
						}
						return "Obsidian armor";
					}
					if (client.getItemDefinition(amuletID).getName().contains("serker")) {
						return "Obsidian weapon and berserker necklace";
					}
				}
			}
		}
		return "No item set";
	}

	public double setBonus() {
		String set = itemSet();

		//Melee sets
		if (combatType().equals("Melee")) {
			if (set.equals("Void melee")) {
				return 1.1;
			}
			if (set.equals("Dharok")) {
				double baseHitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
				double currentHitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
				return 1 + (((baseHitpoints - currentHitpoints)/100) * baseHitpoints/100);
			}
			if (set.equals("Obsidian armor")) {
				return 1.1;
			}
			if (set.equals("Obsidian weapon and berserker necklace")) {
				return 1.2;
			}
			if (set.equals("Max obsidian")) {
				return 1.1 * 1.2;
			}
		}
		//Ranged sets
		if (combatType().equals("Ranged")) {
			if (set.equals("Void range")) {
				return 1.1;
			}
			if (set.equals("Elite void range")) {
				return 1.125;
			}
		}
		//Magic sets applied directly in max hit calculation
		return 1;
	}


	//Combat type of equipped weapon (Melee, ranged, magic, other)
	public String combatType() {
		String attackStyle = attackStyle();

		if (attackStyle.equalsIgnoreCase("Aggressive") || attackStyle.equalsIgnoreCase("Accurate") ||
				attackStyle.equalsIgnoreCase("Defensive") || attackStyle.equalsIgnoreCase("Controlled")) {
			return "Melee";
		}
		if (attackStyle.contains("ang")) {
			return "Ranged";
		}
		if (attackStyle.contains("asting")) {
			return "Magic";
		}
		return "Other";
	}


	//Prayer Bonus
	public double prayerMultiplier() {

		//Melee prayers
		if (combatType().equals("Melee")) {
			if (client.isPrayerActive(Prayer.BURST_OF_STRENGTH)) { return 1.05; }
			if (client.isPrayerActive(Prayer.SUPERHUMAN_STRENGTH)) { return 1.10; }
			if (client.isPrayerActive(Prayer.ULTIMATE_STRENGTH)) { return 1.15; }
			if (client.isPrayerActive(Prayer.CHIVALRY)) { return 1.18; }
			if (client.isPrayerActive(Prayer.PIETY)) { return 1.23; }
		}
		//Ranged prayers
		if (combatType().equals("Ranged")) {
			if (client.isPrayerActive(Prayer.SHARP_EYE)) { return 1.05; }
			if (client.isPrayerActive(Prayer.HAWK_EYE)) { return 1.1; }
			if (client.isPrayerActive(Prayer.EAGLE_EYE)) { return 1.15; }
			if (client.isPrayerActive(Prayer.RIGOUR)) { return 1.23; }
		}

		return 1;
	}


	//Weapon name
	public String weaponName() {

		final ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
		if (container != null) {
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
		return "No weapon or items";
	}


	//Attack style (Aggressive, Defensive, etc.. Doesn't differentiate
	//between accurate and rapid for ranged, so use isRangingAccurate() for that
	private String attackStyle() {
		attackStyleVarbit = client.getVar(VarPlayer.ATTACK_STYLE);
		equippedWeaponTypeVarbit = client.getVar(Varbits.EQUIPPED_WEAPON_TYPE);
		castingModeVarbit = client.getVar(Varbits.DEFENSIVE_CASTING_MODE);

		AttackStyle[] attackStyles = WeaponType.getWeaponType(equippedWeaponTypeVarbit).getAttackStyles();
		if (attackStyleVarbit < attackStyles.length)
		{
			attackStyle = attackStyles[attackStyleVarbit];
			if (attackStyle == null) { attackStyle = OTHER; }
			else if ((attackStyle == CASTING) && (castingModeVarbit == 1))
			{
				attackStyle = DEFENSIVE_CASTING;
			}
		}
		return attackStyle.getName();
	}

	public int styleBonus() {
		attackStyleVarbit = client.getVar(VarPlayer.ATTACK_STYLE);
		equippedWeaponTypeVarbit = client.getVar(Varbits.EQUIPPED_WEAPON_TYPE);
		castingModeVarbit = client.getVar(Varbits.DEFENSIVE_CASTING_MODE);

		WeaponType equippedWeaponType = WeaponType.getWeaponType(equippedWeaponTypeVarbit);

		String attackStyle =  attackStyle();

		if (attackStyle.equalsIgnoreCase("Aggressive")) { return 3; }
		if (attackStyle.equalsIgnoreCase("Controlled")) { return 1; }
		if (isRangingAccurate()) { return 3; }
		else { return 0; }
	}




	//MAGIC FACTORS -- An option in config menu, not based on equipped weapon
	//Get all factors that specifically affect magic max hit:
	//Boosted magic level (for trident), spell, equipment bonus

	//Visible magic level
	public int magicLevel() { return client.getBoostedSkillLevel(Skill.MAGIC); }

	public int spellDamage() {

		MagicSpell spell = config.spellChoice();
		if (spell.element == Element.GOD && config.applyCharge()) {
			return 30;
		}
		return spell.damage;
	}

	public String shieldName() {
		final ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
		if (container != null) {
			Item[] items = container.getItems();
			if (items.length >= EquipmentInventorySlot.SHIELD.getSlotIdx()) {
				final Item shield = items[EquipmentInventorySlot.SHIELD.getSlotIdx()];
				if (shield.getId() > 512) {
					int shieldID = shield.getId();
					return client.getItemDefinition(shieldID).getName();
				}
			}
		}
		return "no shield";
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
			else { ; }
		}
		if (itemSet().equals("Elite void mage")) {
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
	public int rangedLevel() { return client.getBoostedSkillLevel(Skill.RANGED); }

	//Using accurate ranging style, gives +3 boost to style bonus
	public boolean isRangingAccurate() {

		//if 0 for ranged weapons, style is accurate
		int styleNum = client.getVar(VarPlayer.ATTACK_STYLE);

		String attackStyle = attackStyle();

		return (attackStyle.contains("ang") && styleNum == 0);
	}

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

	//Ranged equipment bonus
	public int rangedStrengthBonus() {
		int rangedStrBonus = 0;

		int[] ids = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();
		for (int x : ids) {
			if (x > 512) {
				int id = x - 512;
				final ItemStats stats = itemManager.getItemStats(id, false);
				final ItemEquipmentStats currentEquipment = stats.getEquipment();
				rangedStrBonus += currentEquipment.getRstr();
			}
			else { ; }
		}

		//check if weapon takes priority over ammo slot
		ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
		if (container != null) {
			Item[] items = container.getItems();
			if (items.length >= EquipmentInventorySlot.WEAPON.getSlotIdx()) {
				final Item weapon = items[EquipmentInventorySlot.WEAPON.getSlotIdx()];
				if (weapon != null) {
					final ItemComposition weaponComp = itemManager.getItemComposition(weapon.getId());

					if (!weaponComp.isStackable() && !client.getItemDefinition(weapon.getId()).getName().contains("pipe")
							&& !client.getItemDefinition(weapon.getId()).getName().contains("ystal")) {
						if (items.length == 14) {
							final Item ammo = items[EquipmentInventorySlot.AMMO.getSlotIdx()];
							int ammoID = ammo.getId();
							if (ammoID != -1) {
								//return client.getItemDefinition(ammoID).getName();
								final ItemStats ammoStats = itemManager.getItemStats(ammoID, false);
								final ItemEquipmentStats ammoEquipment = ammoStats.getEquipment();
								rangedStrBonus += ammoEquipment.getRstr();
							}
						}
					}
				}
			}
		}
		return rangedStrBonus;
	}




	//MELEE FACTORS -- Get all factors that specifically affect melee max hit:
	//Boosted strength level, equipment bonus, attack style bonus from all styles section

	//Visible strength Level
	public int strengthLevel() { return client.getBoostedSkillLevel(Skill.STRENGTH); }

	//Equipment bonus
	public int ringBonus() {
		//get ring bonus
		final ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
		if (container != null) {
			Item[] items = container.getItems();
			if (items.length >= EquipmentInventorySlot.RING.getSlotIdx()) {
				final Item ring = items[EquipmentInventorySlot.RING.getSlotIdx()];
				if (ring.getId() > 512) {
					int ringID = ring.getId();
					//return client.getItemDefinition(ringID).getName();
					final ItemStats ringStats = itemManager.getItemStats(ringID, false);
					final ItemEquipmentStats ringEquipment = ringStats.getEquipment();
					return ringEquipment.getStr();
				}
			}
		}
		return 0;
	}

	public int strengthBonus() {
		int strBonus = 0;

		strBonus += ringBonus();

		//get str bonus of worn equipment except ring
		int[] ids = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();
		for (int x : ids) {
			if (x > 512) {
				int id = x - 512;
				final ItemStats stats = itemManager.getItemStats(id, false);
				final ItemEquipmentStats currentEquipment = stats.getEquipment();
				strBonus += currentEquipment.getStr();

			}
			else { ; }
		}
		return strBonus;
	}




	//MAX HIT CALCULATIONS -- including special attack
	//Does not account for special cases such as
	//slayer, twisted bow, wilderness weapons, etc.

	//Max hit base for melee and ranged, as well as tridents
	//Displayed by "Max Hit:" overlay

	public double maxHitBase() {
		int style = styleBonus();
		double pray = prayerMultiplier();
		double setBonus = setBonus();
		int equipment = 0;
		int level = 0;

		if (combatType().equals("Melee")) {
			level = strengthLevel();
			equipment = strengthBonus();
		}
		else if (combatType().equals("Ranged")) {
			level = rangedLevel();
			equipment = rangedStrengthBonus();
		}
		//Special case: Trident
		else if (weaponName().contains("rident")) {
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
		}

		//Actual calculation for base damage (base damage refers to hit without special attack
		double effectiveStrengthLevel = Math.floor((Math.floor(level * pray) + style + 8));

		//Void set effects enter into calculation in effective strength level
		if (itemSet().contains("oid")) {
			effectiveStrengthLevel = Math.floor(effectiveStrengthLevel * setBonus);
		}

		double baseMax = Math.floor((effectiveStrengthLevel * (equipment + 64) / 640) + 0.5);

		//Non-void set effects enter into calculation after base damage calculation
		if (!itemSet().contains("oid")) { return baseMax * setBonus; }

		return baseMax;
	}


	//Max hit calculation for magic
	//Displayed by "Max Magic Hit:" overlay
	public double maxMagicHitBase() {
		if (config.showMagic()) {
			int spellDamage = spellDamage();
			double magicEquipment = magicBonus();

			double base = spellDamage * magicEquipment;
			if (config.spellChoice().element == Element.FIRE && shieldName().contains("ome of fire")) {
				base = Math.floor(base) * 1.5;
			}
			return base;
		}
		return 0;
	}

	/**still need to add mage spec (nightmare staff). need to find documentation on formula**/
	//Returns -1 if special attack does not affect max hit
	//Displayed by "Max Special:" overlay
	public double maxHitSpec() {

		if (weaponName().contains("rossbo") && ammoName().contains("(e)")) {
			if (ammoName().contains("iamond")) {
				return Math.floor(maxHitBase()) * 1.15;
			}
			if (ammoName().contains("nyx")) {
				return Math.floor(maxHitBase()) * 1.2;
			}
			if (ammoName().contains("ragonstone")) {
				return Math.floor(maxHitBase()) + Math.floor(rangedLevel() * .2);
			}
			if (ammoName().contains("pal")) {
				return Math.floor(maxHitBase()) + Math.floor(rangedLevel() * .1);
			}
			if (ammoName().contains("earl")) {
				return Math.floor(maxHitBase()) + Math.floor(rangedLevel() * .05);
			}
		}

		if (weaponName().equalsIgnoreCase("Saradomin sword")) {
			return 16 + (Math.floor(maxHitBase()) * specialAttackDamageMultiplier(weaponName()));
		}
		if (weaponName().equalsIgnoreCase("Granite hammer")) {
			return Math.floor(maxHitBase()) + 5;
		}
		if (specialAttackDamageMultiplier(weaponName()) != 1) {
			if (weaponName().contains("dagger") || weaponName().equalsIgnoreCase("Dark bow")) {
				return Math.floor(Math.floor(maxHitBase()) * specialAttackDamageMultiplier(weaponName())) * 2;
			}
			return Math.floor(maxHitBase()) * specialAttackDamageMultiplier(weaponName());
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
}
