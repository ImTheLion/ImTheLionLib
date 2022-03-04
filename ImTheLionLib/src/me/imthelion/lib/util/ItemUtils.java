package me.imthelion.lib.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class ItemUtils {
	
	public static Material randomSurvivalMaterial() {
		Material temp;
		do {
			temp = randomMaterial();
		} while (!isSurvivalObtainable(temp));
		return temp;
	}
	
	public static Material randomMaterial() {
		Random r = new Random();
		int i = r.nextInt(Material.values().length);
		int j = 0;
		for(Material mat : Material.values()) {
			if(j != i)j++;
			else return mat;
		}
		return null;
	}
	
	public static boolean isSurvivalObtainable(Material mat) {
		if(mat == null || mat == Material.AIR)return false;
		if(mat.isLegacy())return false;
		if(!mat.isItem()) return false;
		if(mat.toString().contains("COMMAND")) return false;
		if(mat == Material.STRUCTURE_BLOCK || mat == Material.STRUCTURE_VOID) return false;
		if(mat == Material.JIGSAW || mat == Material.BEDROCK || mat == Material.BARRIER || mat == Material.LIGHT) return false;
		if(mat == Material.END_GATEWAY || mat == Material.END_PORTAL || mat == Material.END_PORTAL_FRAME||mat == Material.CHORUS_PLANT)return false;
		if(mat.toString().contains("SPAWN")) return false;
		if(mat == Material.DEBUG_STICK || mat == Material.DRAGON_EGG)return false;
		return true;
	}
	
	public static void removeMaterial(Player p, Material m, Integer amount) {
		int removed = 0;
		for(ItemStack itemStack:p.getInventory().getContents()) {
            if (itemStack != null) {
                if (itemStack.getType().equals(m)) {
                    if (itemStack.getAmount() == amount-removed) {
                        itemStack.setAmount(0);
                        return;
                    }
                    else if (itemStack.getAmount() > amount-removed) {
                        itemStack.setAmount((itemStack.getAmount() - (amount-removed)));
                        return;
                }
                    else {
                    	removed = removed +itemStack.getAmount();
                    	itemStack.setAmount(0);
                    }
                }
            }
        }
    }
	
	public static boolean hasAtLeast(Player p, Material m, Integer amount) {
        AtomicReference<Integer> has = new AtomicReference<>(0);

        Arrays.stream(p.getInventory().getContents()).forEach(itemStack -> {
            if (itemStack != null && itemStack.getType() != null && itemStack.getType().equals(m))
                has.updateAndGet(v -> v + itemStack.getAmount());
        });

        if (has.get() >= amount)
            return true;

        return false;
    }
	
	public static ItemStack smeltItem(ItemStack item) {
        ItemStack result = null;
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
           Recipe recipe = iter.next();
           if (!(recipe instanceof FurnaceRecipe)) continue;
           if (((FurnaceRecipe) recipe).getInput().getType() != item.getType()) continue;
           result = recipe.getResult();
           break;
        }
        if(result == null)return item;
        result.setAmount(item.getAmount());
		return result;
	}
	
	public static ItemStack getItem(Material mat, int amount, String name, Map<Enchantment, Integer> ench, Lore lore, boolean unbreakable, boolean hideFlags, Integer data) {
		return 	getItem(mat, amount, name, ench, lore.getLore(), unbreakable, hideFlags, data);

	}
	
	public static ItemStack getItem(Material mat, int amount, String name, Map<Enchantment, Integer> ench, List<String> lore, boolean unbreakable, boolean hideFlags, Integer data) {
		ItemStack item;
		if(data != null) item = new ItemStack(mat, amount, data.shortValue()); else item = new ItemStack(mat, amount);
		item = addEnchants(item, ench);
		ItemMeta meta = item.getItemMeta();
		if(name != null)meta.setDisplayName(name);
		if(lore != null) meta.setLore(lore);
		if(hideFlags) {
			for(ItemFlag flag : ItemFlag.values()) {
				meta.addItemFlags(flag);
			}
		}
		meta.setUnbreakable(unbreakable);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItem(Material mat, int amount, String name) {
		ItemStack item;
		item = new ItemStack(mat, amount);
		ItemMeta meta = item.getItemMeta();
		if(name != null)meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getLeatherItem(final Material mat, final int amount, final String name, final Map<Enchantment, Integer> ench,
			final String[] lore, final boolean unbreakable, final boolean hideFlags, final Color color) {
		ItemStack item;
		item = new ItemStack(mat, amount);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		if(name != null)meta.setDisplayName(name);
		if(lore != null) meta.setLore(Arrays.asList(lore));
		if(hideFlags) {
			for(ItemFlag flag : ItemFlag.values()) {
				meta.addItemFlags(flag);
			}
		}
		meta.setUnbreakable(unbreakable);
		meta.setColor(color);
		item.setItemMeta(meta);
		item = addEnchants(item, ench);
		return item;
	}
	
	public static ItemStack addEnchants(ItemStack item, Map<Enchantment, Integer> ench) {
		if(ench == null)return item;
		for (Map.Entry<Enchantment, Integer> entry : ench.entrySet()) {
			item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
		}
		return item;
	}
	
	public static ItemStack modifyItem(ItemStack item, String name, Map<Enchantment, Integer> ench, String[] lore, boolean unbreakable, boolean hideFlags) {
		item = addEnchants(item, ench);
		ItemMeta meta = item.getItemMeta();
		if(name != null)meta.setDisplayName(name);
		if(lore != null) meta.setLore(Arrays.asList(lore));
		if(hideFlags) {
			for(ItemFlag flag : ItemFlag.values()) {
				meta.addItemFlags(flag);
			}
		}
		meta.setUnbreakable(unbreakable);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createSkull(String id) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		if(id.isEmpty())return item;
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		GameProfile skin = new GameProfile(UUID.randomUUID(), null);
		skin.getProperties().put("textures", new Property("textures", id));
		
		try {
			Field f = meta.getClass().getDeclaredField("profile");
			f.setAccessible(true);
			f.set(meta, skin);
		} catch(Exception e) {
			e.printStackTrace();
		}
		item.setItemMeta(meta);
		
		return item;
	}
	
	@Deprecated
	public static ItemStack createPlayerSkull(String name) {
		return createPlayerSkull(Bukkit.getOfflinePlayer(name));
	}
	
	public static ItemStack createPlayerSkull(UUID id) {
		return createPlayerSkull(Bukkit.getOfflinePlayer(id));
	}
	
	public static ItemStack createPlayerSkull(OfflinePlayer owner) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(owner);
		meta.setDisplayName("§e" + owner.getName());
		item.setItemMeta(meta);
		return item;
	}
}
