package com.maxhit.stats;

import net.runelite.api.Skill;

public class Stats {
    public static final Stat ATTACK = new SkillStat(Skill.ATTACK);
    public static final Stat DEFENCE = new SkillStat(Skill.DEFENCE);
    public static final Stat STRENGTH = new SkillStat(Skill.STRENGTH);
    public static final Stat HITPOINTS = new SkillStat(Skill.HITPOINTS);
    public static final Stat RANGED = new SkillStat(Skill.RANGED);
    public static final Stat PRAYER = new SkillStat(Skill.PRAYER);
    public static final Stat MAGIC = new SkillStat(Skill.MAGIC);
}
