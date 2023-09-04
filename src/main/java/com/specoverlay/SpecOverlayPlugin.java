package com.specoverlay;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.SpriteID;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;

@Slf4j
@PluginDescriptor(name = "Spec Overlay")
public class SpecOverlayPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ItemManager itemManager;
    private SpecOverlayCounter specOverlayCounter;
    private static final int SoulReaperAxe = 28338;


    @Inject
    SpriteManager spriteManager;

    @Override
    protected void startUp() throws Exception {
        specOverlayCounter = new SpecOverlayCounter(this, client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10, null);
    }

    @Override
    protected void shutDown() throws Exception {
        infoBoxManager.removeInfoBox(specOverlayCounter);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        if (itemContainerChanged.getItemContainer() != client.getItemContainer(InventoryID.EQUIPMENT)) {
            return;
        }
        if (Objects.requireNonNull(client.getItemContainer(InventoryID.EQUIPMENT)).contains(SoulReaperAxe)) {
            specOverlayCounter.setImage(spriteManager.getSprite(SpriteID.MINIMAP_ORB_SPECIAL_ICON, 0));
            if (!infoBoxManager.getInfoBoxes().contains(specOverlayCounter)) { //I genuinely cant figure out any other way to do this, the infobox keeps replicating itself when hopping worlds
                infoBoxManager.addInfoBox(specOverlayCounter);
            }
        } else {
            infoBoxManager.removeInfoBox(specOverlayCounter);
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        updateInfoBox();
    }

    private void updateInfoBox() {
        specOverlayCounter.setCount(client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10);
    }

    @Provides
    SpecOverlayConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SpecOverlayConfig.class);
    }
}
