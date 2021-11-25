package com.maxhit;


import static com.maxhit.AttackStyle.CASTING;
import static com.maxhit.AttackStyle.DEFENSIVE_CASTING;
import static com.maxhit.AttackStyle.OTHER;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.item.ItemStats;
import net.runelite.http.api.item.ItemEquipmentStats;

import javax.inject.Inject;
import java.util.HashMap;



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
	private InfoBoxManager infoBoxManager;

	@Inject
	private MaxHitConfig config;

	@Provides
	MaxHitConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(MaxHitConfig.class);
	}

	@Override
	public void startUp() throws Exception {
		infoBoxManager.addInfoBox(new MaxHitIndicator(ImageUtil.loadImageResource(getClass(), "hitsplat.png"), this, config));
		overlayManager.add(myOverlay);
	}

	@Override
	public void shutDown() throws Exception {
		infoBoxManager.removeIf(t -> t instanceof MaxHitIndicator);
		overlayManager.remove(myOverlay);
	}

	//Info for all styles:
	//Item set bonus, combat type, prayer bonus, weapon, attack style

	private int attackStyleVarbit = -1;
	private int equippedWeaponTypeVarbit = -1;
	private int castingModeVarbit = -1;
	private AttackStyle attackStyle;

	public HashMap equippableItems() {
		HashMap<String, InventoryWeapons> inventoryWeaponsHashMap = new HashMap<>();
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		if (inventory != null) {
			Item[] inventItems = inventory.getItems();
			if (inventItems.length != 0) {
				for (Item item : inventItems) {
					Integer ID = item.getId();
					if (ID != -1) {
						ItemStats itemStats = itemManager.getItemStats(ID, false);
						if (itemStats != null) {
							if (itemStats.isEquipable()) {
								if (itemManager.getItemStats(ID, false).getEquipment().getSlot() == 3) {

									String name = client.getItemDefinition(ID).getName();
									inventoryWeaponsHashMap.put(name, new InventoryWeapons());
									inventoryWeaponsHashMap.get(name).ID = ID;
									inventoryWeaponsHashMap.get(name).name = name;

									boolean twoHanded = itemManager.getItemStats(ID, false).getEquipment().isTwoHanded();
									inventoryWeaponsHashMap.get(name).isTwoHanded = twoHanded;

									String weaponType = new String();

									if (name.contains("bow") ||
											name.contains("knif") ||
											name.contains("dart") ||
											name.contains("throw") ||
											name.contains("xil-ul") ||
											name.contains("chompa") ||
											name.contains("blowpipe") ||
											name.contains("ballista")) {
										weaponType = "Ranged";
									} else if (name.contains("rident")) {
										weaponType = "Trident";
									} else {
										weaponType = "Melee";
									}
									inventoryWeaponsHashMap.get(name).weaponType = weaponType;
									if (inventoryWeaponsHashMap.get(name).weaponType.equals("Melee")) {
										inventoryWeaponsHashMap.get(name).strBonus = itemManager.getItemStats(ID, false).getEquipment().getStr();
									} else if (inventoryWeaponsHashMap.get(name).weaponType.equals("Ranged")) {
										inventoryWeaponsHashMap.get(name).strBonus = itemManager.getItemStats(ID, false).getEquipment().getRstr();
									}


									//MAX HIT FACTORS

									int style = 3; //have to assume, we can't actually get this value
									double pray = 1;
									double setBonus = 1;
									int equipment = 0;
									int level = 0;
									int defense = client.getRealSkillLevel(Skill.DEFENCE);
									int prayerLevel = client.getRealSkillLevel(Skill.PRAYER);

									//ASSIGNMENT OF LEVEL, EQUIPMENT, AND PRAYER

									//Invent weapon is melee
									if (weaponType.equals("Melee")) {
										level = strengthLevel();
										equipment = strengthBonus();
										//invent weapon is 2 handed, have weapon and shield equipped
										if (twoHanded &&
												equippedShieldID() != -1 &&
												equippedWeaponID() != -1 && combatType().equals("Melee")) {
											equipment = equipment - itemManager.getItemStats(equippedWeaponID(), false).getEquipment().getStr()
													- itemManager.getItemStats(equippedShieldID(), false).getEquipment().getStr()
													+ inventoryWeaponsHashMap.get(name).strBonus;
										}
										//invent weapon is 2 handed, have only weapon equipped
										else if (inventoryWeaponsHashMap.get(name).isTwoHanded &&
												equippedWeaponID() != -1 &&
												combatType().equals("Melee")) {
											equipment = equipment - itemManager.getItemStats(equippedWeaponID(), false).getEquipment().getStr()
													+ inventoryWeaponsHashMap.get(name).strBonus;
										}
										//invent weapon is 2 handed, have only shield equipped
										else if (inventoryWeaponsHashMap.get(name).isTwoHanded && equippedShieldID() != -1) {
											equipment = equipment - itemManager.getItemStats(equippedShieldID(), false).getEquipment().getStr()
													+ inventoryWeaponsHashMap.get(name).strBonus;
										}
										//invent weapon is 1 handed, have weapon equipped
										else if (!inventoryWeaponsHashMap.get(name).isTwoHanded &&
												equippedWeaponID() != -1 && combatType().equals("Melee")) {
											equipment = equipment - itemManager.getItemStats(equippedWeaponID(), false).getEquipment().getStr()
													+ inventoryWeaponsHashMap.get(name).strBonus;
										}
										//no weapon or shield equipped
										else {
											equipment = equipment + inventoryWeaponsHashMap.get(name).strBonus;
										}

										// have to assume using best prayer for level
										if (prayerLevel > 1 && prayerLevel < 13) {
											pray = 1.05;
										} else if (prayerLevel >= 13 && prayerLevel < 31) {
											pray = 1.1;
										} else if (prayerLevel >= 31 && prayerLevel < 60) {
											pray = 1.15;
										} else if (prayerLevel >= 60 && prayerLevel < 70 && defense >= 65) {
											pray = 1.18;
										} else if (prayerLevel >= 70 && defense >= 70) {
											pray = 1.23;
										}
									}


									//Invent weapon is ranged
									//have to consider whether equipped weapon is stackable or ammo
									else if (weaponType.equals("Ranged")) {
										level = rangedLevel();

										//have ranged weapon equipped
										//takes care of case where invent weapon is stackable and invent weapon isnt stackable
										if (equippedWeaponID() != -1) {

											//if equipped weapon is stackable, takes care of if invent weapon is stackable,
											//isnt stackable and doesn't use ammo
											if (itemManager.getItemComposition(equippedWeaponID()).isStackable()) {
												equipment = rangedStrengthBonus()
														- itemManager.getItemStats(equippedWeaponID(), false).getEquipment().getRstr()
														+ inventoryWeaponsHashMap.get(name).strBonus;
												//invent weapon is not stackable and uses ammo
												if (ammoID() != -1 && !itemManager.getItemComposition(ID).isStackable() &&
														!itemManager.getItemComposition(ID).getName().contains("rystal bow") &&
														!itemManager.getItemComposition(ID).getName().contains("pipe")) {
													equipment += itemManager.getItemStats(ammoID(), false).getEquipment().getRstr();
												}
											}

											//if equipped weapon isnt stackable and uses ammo
											//takes care of case where invent weapon is not stackable
											else if (!itemManager.getItemComposition(equippedWeaponID()).isStackable() &&
													ammoID() != -1
													&& !itemManager.getItemComposition(equippedWeaponID()).getName().contains("rystal bow")
													&& !itemManager.getItemComposition(equippedWeaponID()).getName().contains("pipe")) {
												equipment = rangedStrengthBonus()
														- itemManager.getItemStats(equippedWeaponID(), false).getEquipment().getRstr()
														+ inventoryWeaponsHashMap.get(name).strBonus;
												//invent weapon is stackable or doesnt use ammo
												if (itemManager.getItemComposition(ID).isStackable() ||
														name.contains("rystal bow") || name.contains("pipe")) {
													equipment = equipment - itemManager.getItemStats(ammoID(), false).getEquipment().getRstr();
												}
											}

											//if equipped weapon doesnt use ammo and isnt stackable
											else if (itemManager.getItemComposition(equippedWeaponID()).getName().contains("rystal bow") ||
													itemManager.getItemComposition(equippedWeaponID()).getName().contains("pipe")) {
												equipment = rangedStrengthBonus()
														- itemManager.getItemStats(equippedWeaponID(), false).getEquipment().getRstr()
														+ itemManager.getItemStats(ID, false).getEquipment().getRstr();
												//invent weapon isnt stackable and uses ammo
												if (!itemManager.getItemComposition(ID).isStackable() && ammoID() != -1) {
													equipment += itemManager.getItemStats(ammoID(), false).getEquipment().getRstr();
												}
											}
										}

										else {
											equipment = rangedStrengthBonus() + inventoryWeaponsHashMap.get(name).strBonus;
											if (itemManager.getItemComposition(ID).isStackable() ||
													name.contains("rystal") || name.contains("pipe") & ammoID() != -1) {
												equipment = equipment
														- itemManager.getItemStats(ammoID(), false).getEquipment().getRstr();
											}
										}
										if (prayerLevel > 1 && prayerLevel < 8) {
											pray = 1.05;
										} else if (prayerLevel >= 8 && prayerLevel < 26) {
											pray = 1.1;
										} else if (prayerLevel >= 26 && prayerLevel < 74) {
											pray = 1.15;
										} else if (prayerLevel >= 74 && defense >= 70) {
											pray = 1.23;
										}

									}

									//Special case: Trident
									else if (weaponType.equals("Trident")) {
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
									}

									//Actual calculation for base damage (base damage refers to hit without special attack)
									double effectiveStrengthLevel = Math.floor((Math.floor(level * pray) + style + 8));


									//Void set effects enter into calculation in effective strength level
									if (itemSet(ID).contains("oid")) {
										effectiveStrengthLevel = Math.floor(effectiveStrengthLevel * setBonus(weaponType, itemSet(ID)));
									}

									double baseMax = Math.floor((effectiveStrengthLevel * (equipment + 64) / 640) + 0.5);

									//Non-void set effects enter into calculation after base damage calculation
									if (!itemSet(ID).contains("oid") && !itemSet(ID).equals("No item set")) {
										inventoryWeaponsHashMap.get(name).maxHitBase = baseMax * setBonus(weaponType, itemSet(ID));
										continue;
									}

									inventoryWeaponsHashMap.get(name).maxHitBase = baseMax;

									//Special attack
									inventoryWeaponsHashMap.get(name).maxHitSpec = maxHitSpec(name, baseMax);
								}
							}
						}
					}
				}
			}
		}
		return inventoryWeaponsHashMap;
	}



	// Item sets
	// Still need to add Inquisitor and crystal
	public String itemSet(int weaponID) {
		final ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment != null) {
			Item[] items = equipment.getItems();
			if (items != null && items.length >= 9) {
				// IDs of relevant equipment slots
				int bodyID = items[EquipmentInventorySlot.BODY.getSlotIdx()].getId();
				int legsID = items[EquipmentInventorySlot.LEGS.getSlotIdx()].getId();
				int helmID = items[EquipmentInventorySlot.HEAD.getSlotIdx()].getId();
				int amuletID = items[EquipmentInventorySlot.AMULET.getSlotIdx()].getId();
				int glovesID = items[EquipmentInventorySlot.GLOVES.getSlotIdx()].getId();

				//Item sets
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
						client.getItemDefinition(legsID).getName().contains("harok")) {
					if (client.getItemDefinition(weaponID).getName().contains("harok")) {
						return "Dharok";
					}

				}

			}
		}
		return "No item set";
	}

	public double setBonus(String combatType, String set) {
		//String set = itemSet(equippedWeaponID());

		//Melee sets
		if (combatType.equals("Melee")) {
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
		if (combatType.equals("Ranged")) {
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
	public int equippedWeaponID() {
		final ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
		if (container != null) {
			Item[] items = container.getItems();
			if (items.length >= EquipmentInventorySlot.WEAPON.getSlotIdx()) {
				final Item weapon = items[EquipmentInventorySlot.WEAPON.getSlotIdx()];
				if (weapon.getId() > 512) {
					return weapon.getId();
				}
			}
		}
		return -1;
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
		if (attackStyle.equalsIgnoreCase("Longrange")) { return 1; }
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

	/**Replace this with use of shieldID()**/
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

	public int equippedShieldID() {
		final ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
		if (container != null) {
			Item[] items = container.getItems();
			if (items.length >= 6) {
				final Item shield = items[EquipmentInventorySlot.SHIELD.getSlotIdx()];
				if (shield.getId() > 512) {
					return shield.getId();
				}
			}
		}
		return -1;
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
		if (itemSet(equippedWeaponID()).equals("Elite void mage")) {
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
	public int ammoID() {
		Item[] items = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
		if (items.length == 14) {
			final Item ammo = items[EquipmentInventorySlot.AMMO.getSlotIdx()];
			return ammo.getId();
		}
		return -1;
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
		double setBonus = setBonus(combatType(), itemSet(equippedWeaponID()));
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
		if (itemSet(equippedWeaponID()).contains("oid")) {
			effectiveStrengthLevel = Math.floor(effectiveStrengthLevel * setBonus);
		}

		double baseMax = Math.floor((effectiveStrengthLevel * (equipment + 64) / 640) + 0.5);

		//Non-void set effects enter into calculation after base damage calculation
		if (!itemSet(equippedWeaponID()).contains("oid")) { return baseMax * setBonus; }

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
		if (combatType().equals("Melee") || combatType().equals("Ranged") || weaponName().contains("rident")) {
			nextMaxHit = nextMaxHitBase();
		}
		else if (combatType().equals("Magic")) {
			nextMaxHit = nextMagicMaxHitBase();
		}
		return nextMaxHit;
	}

	private NextMaxHit nextMaxHitBase() {
		double baseMax = maxHitBase() + 1;
		NextMaxHit reqs = new NextMaxHit();

		int style = styleBonus();
		double pray = prayerMultiplier();
		double setBonus = setBonus(combatType(), itemSet(equippedWeaponID()));
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
		else if (weaponName().contains("rident")) {
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
		double effectiveStrengthLevel = Math.floor((Math.floor(level * pray) + style + 8));
		if (itemSet(equippedWeaponID()).contains("oid")) {
			effectiveStrengthLevel = Math.floor(effectiveStrengthLevel * setBonus);
		}

		// Remove non-void set effects
		if (!itemSet(equippedWeaponID()).contains("oid")) {
			baseMax /= setBonus;
		}

		final double equipmentNew = Math.ceil(((baseMax - 0.5) * 640 / effectiveStrengthLevel) - 64);
		final double equipmentDiff = equipmentNew - equipment;

		double reverseEffectiveStrengthLevel = Math.ceil((baseMax - 0.5) * 640 / (equipment + 64) - 8 - style);
		// Remove void set effects
		if (itemSet(equippedWeaponID()).contains("oid")) {
		    reverseEffectiveStrengthLevel /= setBonus;
        	}

		final double levelNew = Math.ceil(reverseEffectiveStrengthLevel / pray);
		final double levelDiff = levelNew - level;

		final double prayerNew = reverseEffectiveStrengthLevel / level;
		final double prayerDiff = Math.ceil((prayerNew - pray) * 100);

		if (combatType().equals("Melee")) {
			reqs.strengthBonus = (int) equipmentDiff;
			reqs.strengthLevels = (int) levelDiff;
		}
		else if (combatType().equals("Ranged")) {
			reqs.rangedBonus = (int) equipmentDiff;
			reqs.rangedLevels = (int) levelDiff;
		}
		reqs.prayerBoost = (int) prayerDiff;

		return reqs;
	}

	private NextMaxHit nextMagicMaxHitBase() {
		NextMaxHit nextMaxHit = new NextMaxHit();

		double base = maxMagicHitBase() + 1;
		if (config.spellChoice().element == Element.FIRE && shieldName().contains("ome of fire")) {
			base = Math.ceil(base / 1.5);
		}
		final double equipmentNew = base / spellDamage();
		final double equipmentDiff = Math.ceil((equipmentNew - magicBonus()) * 100);

		nextMaxHit.magicBonus = (int) equipmentDiff;

		return nextMaxHit;
	}
}

