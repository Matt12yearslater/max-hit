package com.maxhit;

import java.util.HashMap;
import java.util.Map;

enum Spellbook {
    NORMAL,
    ANCIENT,
}

enum Element {
    AIR,
    WATER,
    EARTH,
    FIRE,
    OTHER,
    GOD,
    SMOKE,
    SHADOW,
    BLOOD,
    ICE
}

public enum MagicSpell {
    //Most common spells
    ICE_BARRAGE(Spellbook.ANCIENT, Element.ICE, 30),
    BLOOD_BARRAGE(Spellbook.ANCIENT, Element.BLOOD, 29),
    FIRE_SURGE(Spellbook.NORMAL, Element.FIRE, 24),
    FLAMES_OF_ZAMORAK(Spellbook.NORMAL, Element.GOD, 20),
    CLAWS_OF_GUTHIX(Spellbook.NORMAL, Element.GOD, 20),
    SARADOMIN_STRIKE(Spellbook.NORMAL, Element.GOD, 20),
    IBAN_BLAST(Spellbook.NORMAL, Element.OTHER, 25),
    //Normal spells
    EARTH_SURGE(Spellbook.NORMAL, Element.EARTH, 23),
    WATER_SURGE(Spellbook.NORMAL, Element.WATER, 22),
    WIND_SURGE(Spellbook.NORMAL, Element.AIR, 21),
    FIRE_WAVE(Spellbook.NORMAL, Element.FIRE, 20),
    EARTH_WAVE(Spellbook.NORMAL, Element.EARTH, 19),
    WATER_WAVE(Spellbook.NORMAL, Element.WATER, 18),
    WIND_WAVE(Spellbook.NORMAL, Element.AIR, 17),
    FIRE_BLAST(Spellbook.NORMAL, Element.FIRE, 16),
    EARTH_BLAST(Spellbook.NORMAL, Element.EARTH, 15),
    WATER_BLAST(Spellbook.NORMAL, Element.WATER, 14),
    WIND_BLAST(Spellbook.NORMAL, Element.AIR, 13),
    FIRE_BOLT(Spellbook.NORMAL, Element.FIRE, 12),
    EARTH_BOLT(Spellbook.NORMAL, Element.EARTH, 11),
    WATER_BOLT(Spellbook.NORMAL, Element.WATER, 10),
    WIND_BOLT(Spellbook.NORMAL, Element.AIR, 9),
    FIRE_STRIKE(Spellbook.NORMAL, Element.FIRE, 8),
    EARTH_STRIKE(Spellbook.NORMAL, Element.EARTH, 6),
    WATER_STRIKE(Spellbook.NORMAL, Element.WATER, 4),
    WIND_STRIKE(Spellbook.NORMAL, Element.AIR, 2),
    //Ancient spells
    SHADOW_BARRAGE(Spellbook.ANCIENT, Element.SHADOW, 28),
    SMOKE_BARRAGE(Spellbook.ANCIENT, Element.SMOKE, 27),
    ICE_BLITZ(Spellbook.ANCIENT, Element.ICE, 26),
    BLOOD_BLITZ(Spellbook.ANCIENT, Element.BLOOD, 25),
    SHADOW_BLITZ(Spellbook.ANCIENT, Element.SHADOW, 24),
    SMOKE_BLITZ(Spellbook.ANCIENT, Element.SMOKE, 23),
    ICE_BURST(Spellbook.ANCIENT, Element.ICE, 22),
    BLOOD_BURST(Spellbook.ANCIENT, Element.BLOOD, 21),
    SHADOW_BURST(Spellbook.ANCIENT, Element.SHADOW, 19),
    SMOKE_BURST(Spellbook.ANCIENT, Element.SMOKE, 18),
    ICE_RUSH(Spellbook.ANCIENT, Element.ICE, 17),
    BLOOD_RUSH(Spellbook.ANCIENT, Element.BLOOD, 16),
    SHADOW_RUSH(Spellbook.ANCIENT, Element.SHADOW, 15),
    SMOKE_RUSH(Spellbook.ANCIENT, Element.SMOKE, 14),
    ;

    private static final Map<Spellbook, MagicSpell> SPELLBOOK = new HashMap<>();
    private static final Map<Element, MagicSpell> ELEMENT = new HashMap<>();
    private static final Map<Integer, MagicSpell> DAMAGE = new HashMap<>();

    static {
        for (MagicSpell m : values()) {
            SPELLBOOK.put(m.spellbook, m);
            ELEMENT.put(m.element, m);
            DAMAGE.put(m.damage, m);
        }
    }

    public final Spellbook spellbook;
    public final Element element;
    public final int damage;

    private MagicSpell(Spellbook spellbook, Element element, int damage) {
        this.spellbook = spellbook;
        this.element = element;
        this.damage = damage;
    }
}