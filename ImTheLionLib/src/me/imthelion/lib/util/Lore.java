package me.imthelion.lib.util;

import java.util.ArrayList;
import java.util.List;

public class Lore {
	
	private List<String> lore;
	
	public Lore() {
		this(new ArrayList<String>());
	}
	
	public Lore(List<String> lore) {
		this.lore = lore;
	}
	
	public Lore addLine(String line) {
		lore.add(line);
		return this;
	}
	
	public Lore setLine(int index, String line) {
		if(lore.size()>=index) return this;
		lore.set(index, line);
		return this;
	}
	
	public boolean removeLine(String string) {
		try {
			lore.remove(string);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public boolean removeLine(int index) {
		try {
			lore.remove(index);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public Lore clone() {
		return new Lore(lore);
	}
	
	public List<String> getLore() {
		return new ArrayList<String>(lore);
	}
	

}
