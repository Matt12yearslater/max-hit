package com.maxhit;

import com.maxhit.calculators.MaxHitCalculator;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class MaxHitOverlay extends Overlay
{
	private static final Color COMBAT_LEVEL_COLOUR = new Color(0xff981f);
	private final MaxHitPlugin plugin;
	private final MaxHitConfig config;
	private final Client client;
	private final TooltipManager tooltipManager;
	private final PanelComponent panelComponent = new PanelComponent();


	@Inject
	public MaxHitOverlay(MaxHitPlugin plugin, MaxHitConfig config, Client client, TooltipManager tooltipManager)
	{
		super(plugin);
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;  //set plugin field to plugin object given as input
		this.config = config;
		this.client = client;
		this.tooltipManager = tooltipManager;
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Max hit overlay"));
	}

	//render method
	@Override
	public Dimension render(Graphics2D graphics)
	{

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return null;
		}

		final MaxHitCalculator maxHitCalculator = plugin.getMaxHitCalculator();
		if (maxHitCalculator == null)
		{
			return null;
		}

		double maxHit = maxHitCalculator.getMaxHit();

		panelComponent.getChildren().clear();

		if (config.maxHit())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Max Hit:")
				.right(String.valueOf(maxHit))
				.build());
		}

		if (config.showSpec() && plugin.isWieldingSpecialAttackWeapon())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Max Special:")
				.right(Double.toString(Math.floor(plugin.getSpecialAttackCalculator().getSpecialMaxHit(maxHit))))
				.build());
		}

		//If showNextMaxHit is selected and mouse is inside the overlay show the next max hit tooltip
		if (config.showNextMaxHit() && this.getBounds().contains(
			client.getMouseCanvasPosition().getX(),
			client.getMouseCanvasPosition().getY()))
		{
			tooltipManager.add(new Tooltip(getNextMaxHitTooltip()));
		}

		return panelComponent.render(graphics);
	}

	private String getNextMaxHitTooltip()
	{
		NextMaxHitReqs nextMaxHitReqs = plugin.getMaxHitCalculator().getNextMaxHitReqs();
		StringBuilder sb = new StringBuilder();
		sb.append(ColorUtil.wrapWithColorTag("Next max hit:", COMBAT_LEVEL_COLOUR));
		if (plugin.getMaxHitCalculator().getSkill() == Skill.MAGIC)
		{
			sb.append("<br> UNSUPPORTED");
			return sb.toString();
		}
		String levelReqString = String.format("<br> %.0f %s Levels", nextMaxHitReqs.getNextRequiredLevel(), nextMaxHitReqs.getSkill());
		String bonusReqString = String.format("<br> %.0f %s bonus", nextMaxHitReqs.getNextRequiredStrength(), nextMaxHitReqs.getSkill());
		String prayerBonusReqString = String.format("<br> %.0f%% PRAYER bonus", nextMaxHitReqs.getNextRequiredPrayer());


		sb.append(levelReqString);
		sb.append(bonusReqString);
		sb.append(prayerBonusReqString);
		return sb.toString();
	}
}