package me.imthelion.lib.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class BaseGUI implements Listener, Cloneable {
	
	private final Plugin plugin;
	private final Inventory inv;
	private final Map<Integer, ClickAction> actions = new HashMap<>();
	private final Map<InventoryAnimation, Integer> animations= new HashMap<>();
	private final List<Object> linked = new ArrayList<>();
	private boolean autoCancel = false;
	private boolean unregisterOnClose = false;
	
	public BaseGUI(Plugin plugin, int size, String name) {
		this.plugin = plugin;
		inv = Bukkit.createInventory(null, size, name);
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.animationHandler();
	}
	
	public BaseGUI(Plugin plugin, InventoryType type, String name) {
		this.plugin = plugin;
		inv = Bukkit.createInventory(null, type, name);
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.animationHandler();
	}
	
	public BaseGUI(Plugin plugin, InventoryType type) {
		this.plugin = plugin;
		inv = Bukkit.createInventory(null, type);
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.animationHandler();
	}
	
	public BaseGUI(Plugin plugin, int size) {
		this.plugin = plugin;
		inv = Bukkit.createInventory(null, size);
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.animationHandler();
	}

	public boolean isAutoCancelled() {
		return autoCancel;
	}

	public void setAutoCancelled(boolean autoCancel) {
		this.autoCancel = autoCancel;
	}
	
	public void setAction(int slot, ClickAction action) {
		actions.put(slot, action);
	}
	
	public void addAnimation(InventoryAnimation animation, int delay) {
		animations.put(animation, delay);
	}

	public Inventory getInventory() {
		return inv;
	}
	
	public void setItem(int slot, ItemStack item) {
		inv.setItem(slot, item);
	}
	
	public void openInventory(Player p) {
		p.openInventory(inv);
	}
	
	public List<Object> getLinkedObjects() {
		return this.linked;
	}
	
	public void addLinkedObject(Object o) {
		this.linked.add(o);
	}
	
	public BaseGUI clone() throws CloneNotSupportedException {
		return (BaseGUI) super.clone();
	}
	
	public void border(Material mat) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		
		for(int i = 0; i < 9; i++) {
			inv.setItem(i, item);
		}
		for(int i = inv.getSize()-10; i < inv.getSize(); i++) {
			inv.setItem(i, item);
		}
		
		for(int i = 0; i < inv.getSize()-9; i+=9) {
			inv.setItem(i, item);
		}
		
		for(int i = 8; i < inv.getSize()-9; i+=9) {
			inv.setItem(i, item);
		}
	}
	
	private void animationHandler() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
			
			int count = 1;
			
			@Override
			public void run() {
				if(!animations.isEmpty()) {
					for(InventoryAnimation animation : animations.keySet()) {
						if(count % animations.get(animation) == 0) {
							animate(animation);
						}
					}
				}
				count++;
			}
			
		}, 1, 1);
	}
	
	private void animate(InventoryAnimation animation) {
		animation.animate(this);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getClickedInventory() == null) return;
		if(!e.getClickedInventory().equals(inv))return;
		if(this.autoCancel) e.setCancelled(true);
		if(!actions.containsKey(e.getSlot())) return;
		actions.get(e.getSlot()).onClick(e);
		((Player)e.getWhoClicked()).updateInventory();
	}

	public boolean isUnregisterOnClose() {
		return unregisterOnClose;
	}

	public void setUnregisterOnClose(boolean unregisterOnClose) {
		this.unregisterOnClose = unregisterOnClose;
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onClose(InventoryCloseEvent e) {
		if(!this.unregisterOnClose) return;
		if(!e.getInventory().equals(this.inv)) return;
		if(!e.getViewers().isEmpty()) return;
		HandlerList.unregisterAll(this);
	}
	

}
