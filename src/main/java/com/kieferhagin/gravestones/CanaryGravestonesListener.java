package com.kieferhagin.gravestones;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.PlayerDeathHook;
import net.canarymod.plugin.PluginListener;

public class CanaryGravestonesListener implements PluginListener {
	@HookHandler
	public void onPlayerDeath(PlayerDeathHook hook){
		Player player = hook.getPlayer();
		
		Gravestone gravestone = new Gravestone(player, player.getLocation());
		gravestone.buryPlayer(hook.getDeathMessage1());
	}
}
