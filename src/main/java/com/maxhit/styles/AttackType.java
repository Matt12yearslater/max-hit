package com.maxhit.styles;

// Copied from https://github.com/cwjoshuak/rl-attack-types/blob/master/src/main/java/com/cwjoshuak/AttackType.java

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttackType
{
	CRUSH("Crush"),
	SLASH("Slash"),
	STAB("Stab"),
	RANGED("Ranged"),
	MAGIC("Magic"),
	NONE("None");

	private final String name;
}
