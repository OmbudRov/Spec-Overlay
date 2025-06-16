package com.specoverlay;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(SpecOverlayConfig.CONFIG_GROUP)
public interface SpecOverlayConfig extends Config
{
	String CONFIG_GROUP = "SpecOverlay";

	enum OverlayType
	{
		INFOBOX,
		OVERLAY
	}

	@ConfigItem(
		keyName = "OverlayType",
		name = "Overlay Type",
		description = "Choose how to display the current amount of special attack"
	)
	default OverlayType overlayType()
	{
		return OverlayType.INFOBOX;
	}
}