package com.specoverlay;

import com.google.inject.Provides;
import java.awt.Color;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.SpriteID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ColorUtil;

@Slf4j
@Singleton
@PluginDescriptor(
	name = "Spec Overlay",
	description = "Overlays special attack energy when wielding soulreaper axe or sunlight spear.",
	tags = {"spec", "special", "attack", "soulreaper", "axe", "sunlight", "spear"}
)
public class SpecOverlayPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private SpecOverlayConfig config;
	@Inject
	private SpriteManager spriteManager;
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private SpecOverlayPanel overlay;

	@Nullable
	private Counter infobox;

	private boolean initialized;

	@Getter(AccessLevel.PACKAGE)
	private boolean renderOverlay;
	private boolean renderInfobox;

	@Getter(AccessLevel.PACKAGE)
	private int energy = -1;

	@Provides
	SpecOverlayConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(SpecOverlayConfig.class);
	}

	@Override
	protected void startUp()
	{
		clientThread.invoke(() -> {
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				initialize();
			}
		});
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		infoBoxManager.removeInfoBox(infobox);

		infobox = null;

		initialized = false;
		renderOverlay = false;
		renderInfobox = false;

		energy = -1;
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged e)
	{
		switch (e.getGameState())
		{
			case LOGGED_IN:
				initialize();
				break;
			case HOPPING:
			case LOGIN_SCREEN:
				shutDown();
				break;
		}
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged e)
	{
		if (e.getGroup().equals(SpecOverlayConfig.CONFIG_GROUP))
		{
			clientThread.invoke(() -> {
				if (client.getGameState() == GameState.LOGGED_IN)
				{
					setRender();
				}
			});
		}
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged e)
	{
		if (e.getContainerId() == net.runelite.api.gameval.InventoryID.WORN)
		{
			setRender();
		}
	}

	@Subscribe
	public void onVarbitChanged(final VarbitChanged e)
	{
		if (e.getVarpId() == VarPlayerID.SA_ENERGY)
		{
			setEnergy();
		}
	}

	private void initialize()
	{
		if (initialized)
		{
			return;
		}

		setEnergy();
		setRender();

		infobox = new Counter(spriteManager.getSprite(SpriteID.MINIMAP_ORB_SPECIAL_ICON, 0), this, 0)
		{
			@Override
			public boolean render()
			{
				return renderInfobox;
			}

			@Override
			public String getText()
			{
				return Integer.toString(energy);
			}

			@Override
			public Color getTextColor()
			{
				return energy < 50 ?
					ColorUtil.colorLerp(Color.RED, Color.YELLOW, energy / (double) 50) :
					ColorUtil.colorLerp(Color.YELLOW, Color.GREEN, (energy - 50) / (double) 50);
			}
		};

		infoBoxManager.addInfoBox(infobox);
		overlayManager.add(overlay);

		initialized = true;
	}

	private void setEnergy()
	{
		energy = client.getVarpValue(VarPlayerID.SA_ENERGY) / 10;
	}

	private void setRender()
	{
		switch (config.overlayType())
		{
			case INFOBOX:
				renderOverlay = false;
				renderInfobox = isWeaponEquipped();
				break;
			case OVERLAY:
				renderInfobox = false;
				renderOverlay = isWeaponEquipped();
				break;
		}
	}

	private boolean isWeaponEquipped()
	{
		final var worn = client.getItemContainer(InventoryID.WORN);
		return worn != null && (worn.contains(ItemID.SOULREAPER) || worn.contains(ItemID.WEAPON_OF_SOL));
	}
}
