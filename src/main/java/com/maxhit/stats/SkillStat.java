package com.maxhit.stats;

import net.runelite.api.Client;
import net.runelite.api.Skill;

public class SkillStat extends Stat
{
    private final Skill skill;

    SkillStat(Skill skill)
    {
        super(skill.getName());
        this.skill = skill;
    }

    @Override
    public int getValue(Client client)
    {
        return client.getBoostedSkillLevel(this.skill);
    }

    @Override
    public int getMaximum(Client client)
    {
        return client.getRealSkillLevel(this.skill);
    }

}
