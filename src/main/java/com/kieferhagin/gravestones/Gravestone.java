package com.kieferhagin.gravestones;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.canarymod.Canary;
import net.canarymod.api.chat.ChatComponent;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.ChatComponentFactory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.PlayerInventory;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.Chest;
import net.canarymod.api.world.blocks.Sign;
import net.canarymod.api.world.position.Location;

public class Gravestone {
	private Location location;
	private Player player;
	
	private Chest chest;
	
	/**
	 * Create a new gravestone container
	 * @param player The player that the gravestone will contain
	 * @param location The location of the gravestone (the stone stair)
	 */
	public Gravestone(Player player, Location location){
		this.location = location;
		this.player = player;
	}
	
	/**
	 * Generate the gravestone in the world and bury the player's items
	 */
	public void buryPlayer(ChatComponent causeOfDeath){
		this.chest = this.generate(causeOfDeath);
		this.addPlayerItemsToChest();
	}
	
	private Chest generate(ChatComponent causeOfDeath){
		World world = this.location.getWorld();
		
		// Place the blocks in such a way where the double chest is parallel with the stone stair
		Location stoneStairLocation = new Location(this.location);
		Location[] chestLocations = {
				new Location(stoneStairLocation.getX(),
						stoneStairLocation.getY()-1, stoneStairLocation.getZ()),
				new Location(stoneStairLocation.getX(),
						stoneStairLocation.getY()-1, stoneStairLocation.getZ() - 1)
		};
		
		Location signLocation = new Location(stoneStairLocation.getX(),
				stoneStairLocation.getY(), stoneStairLocation.getZ() - 2);
		
		this.addSign(signLocation, causeOfDeath);
		
		world.setBlockAt(stoneStairLocation, BlockType.StoneBrickStair);
		
		for (Location chestLoc : chestLocations){
			world.setBlockAt(chestLoc, BlockType.Chest);
		}
		
		return (Chest)(world.getBlockAt(chestLocations[1]).getTileEntity());
	}
	
	private void addSign(Location signLocation, ChatComponent causeOfDeath){
		World world = signLocation.getWorld();
		world.setBlockAt(signLocation, BlockType.SignPost);
			 
		Sign sign = (Sign)(world.getBlockAt(signLocation).getTileEntity());
		
		ChatComponentFactory chatFactory = Canary.factory().getChatComponentFactory();
		
		ChatComponent nameComponent = chatFactory.compileChatComponent(this.player.getName());
		ChatComponent timeOfDeathComponent = chatFactory.compileChatComponent(this.getDateNowString());
		
		String fullDeathText = causeOfDeath.getFullText().replace(this.player.getName() + " ", "");
		ArrayList<ChatComponent> deathTextLines = new ArrayList<ChatComponent>();
		
		if (fullDeathText.length() > 10){
			deathTextLines.add(chatFactory.compileChatComponent(fullDeathText.substring(0, 9)));
			deathTextLines.add(chatFactory.compileChatComponent(fullDeathText.substring(9)));
		} else {
			deathTextLines.add(chatFactory.compileChatComponent(fullDeathText));
		}
		
		sign.setComponentOnLine(nameComponent, 0);
		sign.setComponentOnLine(timeOfDeathComponent, 1);
		sign.setComponentOnLine(deathTextLines.get(0), 2);
		
		if (deathTextLines.size() > 1){
			sign.setComponentOnLine(deathTextLines.get(1), 3);
		}
	}
	
	private String getDateNowString(){
		Date nowDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		
		return dateFormat.format(nowDate);
	}
	
	private void addPlayerItemsToChest(){
		PlayerInventory inventory = this.player.getInventory();
		
		// Get the players equipment details, and clear it out to keep it from dropping
		Item boots = inventory.getBootsSlot();
		Item chestplate = inventory.getChestplateSlot();
		Item legs = inventory.getLeggingsSlot();
		Item helmet = inventory.getHelmetSlot();
		
		inventory.setBootsSlot(null);
		inventory.setChestPlateSlot(null);
		inventory.setLeggingsSlot(null);
		inventory.setHelmetSlot(null);
		
		// Add all the equipped and inventory items to the chest
		Item[] equippedItems = {boots, chestplate, legs, helmet};
		Item[] inventoryItems = inventory.clearInventory();
		
		for (Item item : equippedItems){
			if (item != null){
				chest.addItem(item);
			}
		}
		
		for (Item item : inventoryItems){
			if (item != null){
				chest.addItem(item);
			}
		}
	}
}
