package com.specoverlay;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

@Singleton
class SpecOverlayPanel extends OverlayPanel
{
	private final SpecOverlayPlugin plugin;
	private final LineComponent lineComponent;

	@Inject
	SpecOverlayPanel(final SpecOverlayPlugin plugin)
	{
		super(plugin);
		this.plugin = plugin;
		lineComponent = LineComponent.builder().left("Spec:").right(Integer.toString(plugin.getEnergy())).build();
		panelComponent.getChildren().add(lineComponent);
		setClearChildren(false);
		setPosition(OverlayPosition.DYNAMIC);
		setMovable(true);
		setSnappable(true);
		setPriority(Overlay.PRIORITY_HIGH);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		if (plugin.isRenderOverlay())
		{
			lineComponent.setRight(Integer.toString(plugin.getEnergy()));
			return super.render(graphics2D);
		}

		return null;
	}
}
