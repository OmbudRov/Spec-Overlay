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
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.inject.Inject;
import java.util.Objects;

@Slf4j
@PluginDescriptor(name = "Spec Overlay")
public class SpecOverlayPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SpecOverlayConfig specOverlayConfig;
    @Inject
    private ItemManager itemManager;
    private SpecOverlayCounter specOverlayCounter;
    private SpecOverlayOverlay specOverlayOverlay;
	private static final int SoulReaperAxe = 28338;
	private static final int SunlightSpear = 30369;
    static final String ConfigGroupKey = "SpecOverlay";


    @Inject
    SpriteManager spriteManager;

    @Override
    protected void startUp() throws Exception {
        specOverlayCounter = new SpecOverlayCounter(this, client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10, null);
        specOverlayOverlay = new SpecOverlayOverlay(this, specOverlayConfig, client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10);
    }

    @Override
    protected void shutDown() throws Exception {
        if (specOverlayConfig.info() == SpecOverlayConfig.OverlayType.INFOBOX) {
            infoBoxManager.removeInfoBox(specOverlayCounter);
        } else if (specOverlayConfig.info() == SpecOverlayConfig.OverlayType.OVERLAY) {
            overlayManager.remove(specOverlayOverlay);
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        if (itemContainerChanged.getItemContainer() != client.getItemContainer(InventoryID.EQUIPMENT)) {
            return;
        }
        if (Objects.requireNonNull(client.getItemContainer(InventoryID.EQUIPMENT)).contains(SoulReaperAxe) || Objects.requireNonNull(client.getItemContainer(InventoryID.EQUIPMENT)).contains(SunlightSpear)) {
            if (specOverlayConfig.info() == SpecOverlayConfig.OverlayType.INFOBOX) {
                specOverlayCounter.setImage(spriteManager.getSprite(SpriteID.MINIMAP_ORB_SPECIAL_ICON, 0));
                if (!infoBoxManager.getInfoBoxes().contains(specOverlayCounter)) { //I genuinely cant figure out any other way to do this, the infobox keeps replicating itself when hopping worlds
                    infoBoxManager.addInfoBox(specOverlayCounter);
                }
            } else if (specOverlayConfig.info() == SpecOverlayConfig.OverlayType.OVERLAY) {
                overlayManager.add(specOverlayOverlay);
            }
        } else {
            if (specOverlayConfig.info() == SpecOverlayConfig.OverlayType.INFOBOX) {
                infoBoxManager.removeInfoBox(specOverlayCounter);
            } else if (specOverlayConfig.info() == SpecOverlayConfig.OverlayType.OVERLAY) {
                overlayManager.remove(specOverlayOverlay);
            }
        }
        if (specOverlayConfig.info() == SpecOverlayConfig.OverlayType.ALWAYS) {
            specOverlayCounter.setImage(spriteManager.getSprite(SpriteID.MINIMAP_ORB_SPECIAL_ICON, 0));
            if (!infoBoxManager.getInfoBoxes().contains(specOverlayCounter)) { //I genuinely cant figure out any other way to do this, the infobox keeps replicating itself when hopping worlds
                infoBoxManager.addInfoBox(specOverlayCounter);
            }
        }

    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        updateInfoBox();
        updateOverlay();
    }

    private void updateInfoBox() {
        specOverlayCounter.setCount(client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10);
    }

    private void updateOverlay() {
        specOverlayOverlay.Spec = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals(ConfigGroupKey) && (Objects.requireNonNull(client.getItemContainer(InventoryID.EQUIPMENT)).contains(SoulReaperAxe) ||Objects.requireNonNull(client.getItemContainer(InventoryID.EQUIPMENT)).contains(SunlightSpear))) {
            if (event.getOldValue().equals("OVERLAY")) {
                overlayManager.remove(specOverlayOverlay);
                infoBoxManager.addInfoBox(specOverlayCounter);
            } else if (event.getOldValue().equals("INFOBOX")) {
                overlayManager.add(specOverlayOverlay);
                infoBoxManager.removeInfoBox(specOverlayCounter);
            }
        }
    }

    @Provides
    SpecOverlayConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SpecOverlayConfig.class);
    }
}
