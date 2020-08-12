package com.maxhit;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class MaxHitOverlay extends Overlay {

    private MaxHitPlugin plugin;
    private MaxHitConfig config;

    private PanelComponent panelComponent = new PanelComponent();


    @Inject
    public MaxHitOverlay(MaxHitPlugin plugin, MaxHitConfig config) {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;  //set plugin field to plugin object given as input
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Max hit overlay"));
    }

    //render method
    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        if (config.maxHit()) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Max Hit:")
                    .right(Double.toString(Math.floor(plugin.maxHitBase())))
                    .build());
        }

        if (config.showSpec() && plugin.maxHitSpec() != -1) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Max Special:")
                    .right(Double.toString(Math.floor(plugin.maxHitSpec())))
                    .build());
        }

        if (config.showMagic()) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Magic Max Hit:")
                    .right(Double.toString(Math.floor(plugin.maxMagicHitBase())))
                    .build());
        }

        return panelComponent.render(graphics);
    }
}
