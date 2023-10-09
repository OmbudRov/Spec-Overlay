package com.specoverlay;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import java.awt.*;

public class SpecOverlayOverlay extends OverlayPanel {
    private final SpecOverlayPlugin plugin;
    private final SpecOverlayConfig config;
    public int Spec;

    SpecOverlayOverlay(SpecOverlayPlugin plugin, SpecOverlayConfig config, int Spec) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.Spec = Spec;
        setPosition(OverlayPosition.TOP_RIGHT);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        panelComponent.getChildren().clear();
        panelComponent.getChildren().add(LineComponent.builder().left("Spec:").right(String.valueOf(Spec)).build());
        return super.render(graphics2D);
    }
}
