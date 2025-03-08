package com.specoverlay;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("SpecOverlay")
public interface SpecOverlayConfig extends Config {
    enum OverlayType {
        INFOBOX("InfoBox"), OVERLAY("Overlay"),ALWAYS("Always");
        private final String name;

        OverlayType(String string) {
            this.name = string;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    @ConfigItem(
            keyName = "OverlayType",
            name = "Overlay Type",
            description = "Choose how to display the current amount of special attack"
    )
    default OverlayType info() {
        return OverlayType.INFOBOX;
    }
}