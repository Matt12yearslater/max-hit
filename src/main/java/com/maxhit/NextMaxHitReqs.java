package com.maxhit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Skill;

@Getter
@Setter
@AllArgsConstructor
public class NextMaxHitReqs
{
	private Skill skill;
	private double nextRequiredLevel;
	private double nextRequiredStrength;
	private double nextRequiredPrayer;
}
