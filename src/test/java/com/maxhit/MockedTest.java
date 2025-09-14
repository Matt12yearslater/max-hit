/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
 * Copyright (c) 2023, pajlads <https://github.com/pajlads>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.maxhit;

import com.google.inject.testing.fieldbinder.Bind;
import com.maxhit.calculators.StrengthBonusCalculator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemClient;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

/**
 * Based on <a href="https://github.com/pajlads/DinkPlugin/blob/d7c0d4d3f044c25bcff256efc5217955ec1c1494/src/test/java/dinkplugin/notifiers/MockedNotifierTest.java">Dink's MockedNotifierTest</a>
 */
@Slf4j
public abstract class MockedTest extends MockedTestBase
{
	@Bind
	protected Client client = mock(Client.class);

	@Bind
	protected ItemManager itemManager = mock(ItemManager.class);


	@Override
	@BeforeEach
	protected void setUp()
	{
		super.setUp();

		// Get region ID for Shadow Test
		var mockedPlayer = Mockito.mock(Player.class);
		when(client.getLocalPlayer()).thenReturn(mockedPlayer);
		when(mockedPlayer.getLocalLocation()).thenReturn(new LocalPoint(1, 1, 1));
		WorldView mockedWorldView = mock(WorldView.class);
		when(client.getTopLevelWorldView()).thenReturn(mockedWorldView);
		when(client.getWorldView(anyInt())).thenReturn(mockedWorldView);
		when(client.getRealSkillLevel(Skill.HITPOINTS)).thenReturn(99);
		when(client.getVarbitValue(VarbitID.AUTOCAST_SPELL)).thenReturn(MagicSpell.ICE_BARRAGE.getVarbValue());
	}
}
