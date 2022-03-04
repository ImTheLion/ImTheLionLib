package me.imthelion.lib.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

@FunctionalInterface
public interface ClickAction {
	
	public void onClick(InventoryClickEvent e);
	
	public class OpenGUI implements ClickAction {
		
		private final Inventory inv;
		
		public OpenGUI(Inventory toOpen) {
			this.inv = toOpen;
		}

		@Override
		public void onClick(InventoryClickEvent e) {
			e.getWhoClicked().openInventory(inv);
		}
		
	}
	
	public class CloseGUI implements ClickAction {

		@Override
		public void onClick(InventoryClickEvent e) {
			e.getWhoClicked().closeInventory();
		}
		
	}
	
	

}
