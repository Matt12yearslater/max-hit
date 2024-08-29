package com.maxhit;

import net.runelite.api.Client;
import net.runelite.api.GameState;
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

public class MaxHitOverlay extends Overlay {

    private static final Color COMBAT_LEVEL_COLOUR = new Color(0xff981f);
    private final MaxHitPlugin plugin;
    private final MaxHitConfig config;
    private final Client client;
    private final TooltipManager tooltipManager;
    private final PanelComponent panelComponent = new PanelComponent();


    @Inject
    public MaxHitOverlay(MaxHitPlugin plugin, MaxHitConfig config, Client client, TooltipManager tooltipManager) {
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
    public Dimension render(Graphics2D graphics) {

        if(client.getGameState() != GameState.LOGGED_IN) {
            return null;
        }

        panelComponent.getChildren().clear();

        if (config.maxHit()) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Max Hit:")
                    .right(Double.toString(plugin.equipedWeaponMaxHit))
                    .build());
        }

        if (config.showSpec() && plugin.maxHitSpec(plugin.weaponName(), plugin.equipedWeaponMaxHit) != -1) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Max Special:")
                    .right(Double.toString(Math.floor(plugin.maxHitSpec(plugin.weaponName(), plugin.equipedWeaponMaxHit))))
                    .build());
        }

        if (config.showMagic()) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Magic Max Hit:")
                    .right(Double.toString(Math.floor(plugin.maxMagicHitBase())))
                    .build());
        }

        //If showNextMaxHit is selected and mouse is inside the overlay show the next max hit tooltip
        if (config.showNextMaxHit() && this.getBounds().contains(
                client.getMouseCanvasPosition().getX(),
                client.getMouseCanvasPosition().getY())) {
            tooltipManager.add(new Tooltip(getNextMaxHitTooltip()));
        }

        if (plugin.map == null) {
            return panelComponent.render(graphics);
        }

        for (Object weapon : plugin.map.keySet()) {
            String wep = weapon.toString();
            if (config.inventoryWeapons()) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(plugin.map.get(wep).name)
                        .right(Double.toString(Math.floor(plugin.map.get(wep).maxHitBase)))
                        .build());
            }
            if (plugin.map.get(wep).maxHitSpec > 0 && config.inventoryWeaponsSpecial()) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(plugin.map.get(wep).name + " Spec")
                        .right(Double.toString(Math.floor(plugin.map.get(wep).maxHitSpec)))
                        .build());
            }
            if (config.inventorySelectiveSpecial() &&
                    !config.inventoryWeapons() &&
                    !config.inventoryWeaponsSpecial()) {

                if (plugin.map.get(wep).maxHitSpec <= 0) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left(plugin.map.get(wep).name)
                            .right(Double.toString(Math.floor(plugin.map.get(wep).maxHitBase)))
                            .build());
                }
                else {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left(plugin.map.get(wep).name + " Spec")
                            .right(Double.toString(Math.floor(plugin.map.get(wep).maxHitSpec)))
                            .build());
                }
            }
        }

        return panelComponent.render(graphics);
    }

    private String getNextMaxHitTooltip() {
        NextMaxHit nextMaxHit = plugin.nextMaxHit();

        StringBuilder sb = new StringBuilder();
        sb.append(ColorUtil.wrapWithColorTag("Next max hit:", COMBAT_LEVEL_COLOUR));

        if (nextMaxHit.strengthLevels > 0) {
            sb.append("</br>").append(nextMaxHit.strengthLevels).append(" Strength levels");
        }
        else if (nextMaxHit.rangedLevels > 0) {
            sb.append("</br>").append(nextMaxHit.rangedLevels).append(" Ranged levels");
        }
        else if (nextMaxHit.magicLevels > 0) {
            sb.append("</br>").append(nextMaxHit.magicLevels).append(" Magic levels");
        }

        if (nextMaxHit.strengthBonus > 0) {
            sb.append("</br>").append(nextMaxHit.strengthBonus).append(" Strength bonus");
        }
        else if (nextMaxHit.rangedBonus > 0) {
            sb.append("</br>").append(nextMaxHit.rangedBonus).append(" Ranged bonus");
        }
        else if (nextMaxHit.magicBonus > 0) {
            sb.append("</br>").append(nextMaxHit.magicBonus).append(" % Magic damage");
        }

        if (nextMaxHit.prayerBoost > 0) {
            sb.append("</br>").append(nextMaxHit.prayerBoost).append(" % Prayer boost");
        }

        return sb.toString();
    }
}