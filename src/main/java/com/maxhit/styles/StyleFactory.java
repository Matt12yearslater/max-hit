package com.maxhit.styles;

import net.runelite.api.Client;
import net.runelite.api.EnumID;
import net.runelite.api.ParamID;
import net.runelite.api.StructComposition;

import static com.maxhit.styles.AttackStyle.OTHER;

public class StyleFactory {

    private final Client client;
    private CombatStyle combatStyle;
    private AttackStyle attackStyle;

    public StyleFactory(Client client)
    {
        this.client = client;
    }

    private AttackStyle[] getWeaponTypeStyles(int weaponType)
    {
        int BLUE_MOON_SPEAR = 22;
        int KERIS_PARTISAN = 30;
        // from script4525
        int weaponStyleEnum = client.getEnum(EnumID.WEAPON_STYLES).getIntValue(weaponType);
        if (weaponStyleEnum == -1)
        {
            // Blue moon spear

            if (weaponType == BLUE_MOON_SPEAR)
            {
                return new AttackStyle[]{
                        AttackStyle.ACCURATE,
                        AttackStyle.AGGRESSIVE,
                        null,
                        AttackStyle.DEFENSIVE,
                        AttackStyle.CASTING,
                        AttackStyle.DEFENSIVE_CASTING
                };
            }

            // Partisan
            if (weaponType == KERIS_PARTISAN)
            {
                return new AttackStyle[]{
                        AttackStyle.ACCURATE,
                        AttackStyle.AGGRESSIVE,
                        AttackStyle.AGGRESSIVE,
                        AttackStyle.DEFENSIVE
                };
            }
            return new AttackStyle[0];
        }
        int[] weaponStyleStructs = client.getEnum(weaponStyleEnum).getIntVals();

        AttackStyle[] styles = new AttackStyle[weaponStyleStructs.length];
        int i = 0;
        for (int style : weaponStyleStructs)
        {
            StructComposition attackStyleStruct = client.getStructComposition(style);
            String attackStyleName = attackStyleStruct.getStringValue(ParamID.ATTACK_STYLE_NAME);

            AttackStyle attackStyle = AttackStyle.valueOf(attackStyleName.toUpperCase());
            if (attackStyle == AttackStyle.OTHER)
            {
                // "Other" is used for no style
                ++i;
                continue;
            }

            // "Defensive" is used for Defensive and also Defensive casting
            if (i == 5 && attackStyle == AttackStyle.DEFENSIVE)
            {
                attackStyle = AttackStyle.DEFENSIVE_CASTING;
            }

            styles[i++] = attackStyle;
        }
        return styles;
    }

    public AttackStyle getAttackStyle(Client client, int equippedWeaponType, int attackStyleIndex, int castingMode)
    {
        AttackStyle[] attackStyles = getWeaponTypeStyles(equippedWeaponType);
        AttackStyle attackStyle = null;
        if (attackStyleIndex < attackStyles.length)
        {
            // from script4525
            // Even though the client has 5 attack styles for Staffs, only attack styles 0-4 are used, with an additional
            // casting mode set for defensive casting
            if (attackStyleIndex == 4)
            {
                attackStyleIndex += castingMode;
            }

            attackStyle = attackStyles[attackStyleIndex];
            if (attackStyle == null)
            {
                attackStyle = OTHER;
            }
        }

        return attackStyle;
    }

    //Combat type of equipped weapon (Melee, ranged, magic, other)
    public CombatStyle getCombatType(AttackStyle attackStyle) {
        if (attackStyle == AttackStyle.ACCURATE || attackStyle == AttackStyle.AGGRESSIVE ||
                attackStyle == AttackStyle.CONTROLLED || attackStyle == AttackStyle.DEFENSIVE) return CombatStyle.MELEE;
        if (attackStyle.getName().contains("ang")) return CombatStyle.RANGED;
        if (attackStyle.getName().contains("Casting")) return CombatStyle.MAGE;
        return null;
    }
}
