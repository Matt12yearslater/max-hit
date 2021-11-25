package com.maxhit;
import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

public class MaxHitIndicator extends InfoBox {
    private final MaxHitPlugin plugin;
    private final MaxHitConfig config;

    MaxHitIndicator(BufferedImage img, MaxHitPlugin plugin, MaxHitConfig config)
    {
        super(img, plugin);
        this.plugin = plugin;
        this.config = config;
        setPriority(InfoBoxPriority.LOW);
    }

    @Override
    public String getText()
    {
        return Integer.toString((int) plugin.maxHitBase());
    }

    @Override
    public Color getTextColor()
    {
        return Color.WHITE;
    }

    @Override
    public boolean render()
    {
        return config.displayAsInfoBox();
    }
}