package com.specoverlay;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.image.BufferedImage;

public class SpecOverlayCounter extends Counter {
    SpecOverlayCounter(Plugin plugin, int count, BufferedImage image) {
        super(image, plugin, count);
    }
}
