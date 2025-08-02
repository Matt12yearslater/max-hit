package com.maxhit.calculators;

import com.maxhit.styles.CombatStyle;
import net.runelite.api.Skill;

public class NextMaxHitCalculator {

    public int strengthLevels;
    public int rangedLevels;
    public int magicLevels;
    public int strengthBonus;
    public int rangedBonus;
    public int magicBonus;
    public int prayerBoost;

    public NextMaxHitCalculator()
    {

    }

    public static NextMaxHit nextMaxHit(String weaponName, CombatStyle combatStyle) {
        NextMaxHit nextMaxHit = new NextMaxHit();
        if (combatStyle == CombatStyle.MELEE || combatStyle == CombatStyle.RANGED || weaponName.contains("rident")) {
            nextMaxHit = nextMaxHitBase();
        }
        else if (combatStyle == CombatStyle.MAGE) {
            nextMaxHit = nextMagicMaxHitBase();
        }
        return nextMaxHit;
    }

    public static NextMaxHit nextMaxHitBase(String weaponName, CombatStyle combatStyle) {
        double baseMax = maxHitBase(weaponName, combatStyle) + 1;

        switch(combatStyle) {
            case TRIDENT:
                final double magicLevel = getPlayerMagicLevel();
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

        // Remove non-void set effects
        if (!(voidSetChecker.isWearingVoid() || eliteVoidSetChecker.isWearingEliteVoid())) {
            baseMax /= setBonus;
        }



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

        double base = spellDamage() + 1;
        if (config.spellChoice().getDisplayName().startsWith("Fire") && tomeOfFireEquipped()) {
            base = Math.ceil(base / 1.5);
        }
        final double equipmentNew = base / spellDamage();
        final double equipmentDiff = Math.ceil((equipmentNew - magicBonus()) * 100);

        nextMaxHit.magicBonus = (int) equipmentDiff;

        return nextMaxHit;
    }
}
