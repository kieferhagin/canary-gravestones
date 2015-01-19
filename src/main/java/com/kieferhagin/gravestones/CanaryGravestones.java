package com.kieferhagin.gravestones;

import net.canarymod.Canary;
import net.canarymod.plugin.Plugin;

public class CanaryGravestones extends Plugin {
	
	private CanaryGravestonesListener listener = new CanaryGravestonesListener();

	@Override
	public boolean enable() {
		Canary.hooks().registerListener(listener, this);
		return true;
	}

	@Override
	public void disable() {
		
	}

}
