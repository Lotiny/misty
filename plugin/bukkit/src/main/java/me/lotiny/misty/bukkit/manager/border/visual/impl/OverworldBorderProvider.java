package me.lotiny.misty.bukkit.manager.border.visual.impl;

import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.bukkit.manager.border.visual.BorderProvider;

public class OverworldBorderProvider implements BorderProvider {

    @Autowired
    private static GameManager gameManager;

    @Override
    public int getMinX() {
        return -gameManager.getGame().getSetting().getBorderSize();
    }

    @Override
    public int getMaxX() {
        return gameManager.getGame().getSetting().getBorderSize();
    }

    @Override
    public int getMinZ() {
        return -gameManager.getGame().getSetting().getBorderSize();
    }

    @Override
    public int getMaxZ() {
        return gameManager.getGame().getSetting().getBorderSize();
    }
}
