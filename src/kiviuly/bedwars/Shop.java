package kiviuly.bedwars;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("unused")
public class Shop 
{
	private Shop shop = this;
	private String ConfigPath = "";
	private Main main = Main.getMain();
	private HashMap<UUID, String> playersCategory = new HashMap<>();
	
	// Основные переменные (сохраняются в конфиге)
	private String ID = "";
	private String title = "";
	
	private UUID creator = null;
	
	private ArrayList<String> arenas = new ArrayList<>();
	
	private HashMap<String, Object> categories = new HashMap<>();
	
	private HashMap<String, Integer> progress = new HashMap<>();
	
	public Shop(String name, UUID creator) 
	{
		this.setID(name);
		this.setTitle(name);
		this.setCreator(creator);
	}
	
	public void Open(Player p, String category) 
	{
		FileConfiguration c = getConfig();
		if (c == null) {return;}
		if (!c.isSet("Pages."+category+".Title")) {category = "MainPage";}
		String invTitle = ChatColor.translateAlternateColorCodes('&', c.getString("Pages."+category+".Title"));;
		int slots = c.getInt("Pages."+category+".Slots", 27);
		if (slots % 9 != 0 || slots == 0) {return;}
		
		Inventory inv = Bukkit.createInventory(null, slots, invTitle);
		
		for(int i = 0; i < slots; i++)
		{
			String s = "Pages."+category+".Item-"+i;
			if (!c.isSet(s)) {continue;}
			ItemStack is = null;
			if (c.isSet(s+".ICON-ID")) 
			{
				String name = c.getString(s+".ICON-NAME", c.getString(s+".ITEM-NAME", c.getString(s+".ITEM.display-name", "§cНазвание иконки не задано")));
				name = ChatColor.translateAlternateColorCodes('&', name);
				is = new ItemBuilder(
					Material.getMaterial(c.getInt(s+".ICON-ID")))
					.damage((short) c.getInt(s+".ICON-DATA", 0))
					.amount(c.getInt(s+".ICON-AMOUNT", 1))
					.displayname(name)
					.lore(c.getStringList(s+".ICON-LORE"))
					.build();
			}
			if (c.isSet(s+".ICON")) {is = c.getItemStack(s+".ICON", is);}
			if (c.getString(s+".TYPE", "NONE").equals("ITEM"))
			{
				String price = getItemSlotPrice(i, category);
				int iron = Integer.parseInt(price.split("I")[0]); price = price.split("I")[1];
				int gold = Integer.parseInt(price.split("G")[0]); price = price.split("G")[1];
				int diamond = Integer.parseInt(price.split("D")[0]); price = price.split("D")[1];
				int emerald = Integer.parseInt(price.split("E")[0]); price = "";
				ItemMeta meta = is.getItemMeta();
				List<String> lore = new ArrayList<>();
				if (meta.hasLore()) {lore = meta.getLore();}
				lore.add("");
				lore.add("§f§lЦена:");
				if (iron > 0) 
				{
					String text = "Железа";
					if ((iron+"").endsWith("1")) {text = "Железо";}
					lore.add("§f§l" + iron + " §7" + text);
				}
				if (gold > 0)
				{
					String text = "Золота";
					if ((gold+"").endsWith("1")) {text = "Золото";}
					lore.add("§f§l" + gold + " §6" + text);
				}
				if (diamond > 0)
				{
					String text = "Алмаза";
					if ((diamond+"").endsWith("1")) {text = "Алмаз";}
					lore.add("§f§l" + diamond + " §b" + text);
				}
				if (emerald > 0)
				{
					String text = "Изумрудов";
					if ((emerald+"").endsWith("1")) {text = "Изумруд";}
					lore.add("§f§l" + emerald + " §a" + text);
				}
				meta.setLore(lore);
				is.setItemMeta(meta);
			}
			if (c.getString(s+".TYPE", "NONE").equals("UPGRADE"))
			{
				String upgrade = c.getString(s+".UPGRADE");
				int lvl = progress.getOrDefault(p.getUniqueId()+"-"+upgrade+"-LVL", 0);
				int price = getUpgradeSlotPrice(i, category, lvl);
				int iconID = c.getInt(s+".UPGRADE-LVL"+lvl+"-ICON-ID", is.getTypeId());
				ItemMeta meta = is.getItemMeta();
				List<String> lore = new ArrayList<>();
				is = new ItemStack(iconID);
				if (meta.hasLore()) {lore = meta.getLore();}
				lore.add("");
				lore.add("§f§lЦена:");
				String text = "Алмаза";
				if ((price+"").endsWith("1")) {text = "Алмаз";}
				lore.add("§f§l" + price + " §b" + text);
				meta.setLore(lore);
				is.setItemMeta(meta);
			}
			
			inv.setItem(i, is);
		}
		
		main.getShopsByPlayers().put(p.getUniqueId(), shop);
		playersCategory.put(p.getUniqueId(), category);
		p.openInventory(inv);
	}

	public void addSlotItem(String category, ItemStack is, String price)
	{
		FileConfiguration c = getConfig();
		if (c == null) {return;}
		int maxSlots = c.getInt("Pages."+category+".Slots", 27);
		for(int i = 0; i < maxSlots; i++)
		{
			if (c.isSet("Pages."+category+".Item-"+i+".TYPE")) {continue;}
			c.set("Pages."+category+".Item-"+i+".ITEM", is);
			c.set("Pages."+category+".Item-"+i+".ITEM-PRICE", price);
		}
	}

	public String getTitleFromCategory(String category) 
	{
		FileConfiguration c = getConfig();
		if (c == null) {return ChatColor.translateAlternateColorCodes('&', title);}
		String invTitle = ChatColor.translateAlternateColorCodes('&', c.getString("Pages."+category+".Title", title));
		return invTitle;
	}

	public String getSlotType(int slot, String category) 
	{
		FileConfiguration c = getConfig();
		if (c == null) {return "NONE";}
		return c.getString("Pages."+category+".Item-"+slot+".TYPE", "NONE");
	}
	
	public ItemStack getSlotItem(int slot, String category) 
	{
		FileConfiguration c = getConfig();
		if (c == null) {return null;}
		ItemStack is = null;
		if (c.isSet("Pages."+category+".Item-"+slot+".ITEM")) 
		{is = c.getItemStack("Pages."+category+".Item-"+slot+".ITEM", is);}
		else
		{
			int itemID = c.getInt("Pages."+category+".Item-"+slot+".ITEM-ID", 0);
			int itemAmount = c.getInt("Pages."+category+".Item-"+slot+".ITEM-AMOUNT", 1);
			short itemData = (short) c.getInt("Pages."+category+".Item-"+slot+".ITEM-DATA", 0);
			String itemName = c.getString("Pages."+category+".Item-"+slot+".ITEM-NAME", c.getString("Pages."+category+".Item-"+slot+".ICON-NAME", "§cОшибка"));
			itemName = ChatColor.translateAlternateColorCodes('&', itemName);
			List<String> itemLore = c.getStringList("Pages."+category+".Item-"+slot+".ITEM-LORE");
			if (itemLore == null) {itemLore = new ArrayList<>();}
			is = new ItemBuilder(Material.getMaterial(itemID), itemAmount)
				.damage(itemData)
				.displayname(itemName)
				.lore(itemLore)
				.build();
		}
		return is;
	}
	
	public String getItemSlotPrice(int slot, String category) 
	{
		FileConfiguration c = getConfig();
		if (c == null) {return "0I0G0D0E";}
		String price = c.getString("Pages."+category+".Item-"+slot+".ITEM-PRICE", "0I0G0D0E");
		price = price.replace(" ", "");
		return price;
	}
	
	public int getUpgradeSlotPrice(int slot, String category, int lvl) 
	{
		FileConfiguration c = getConfig();
		if (c == null) {return 0;}
		int price = c.getInt("Pages."+category+".Item-"+slot+".UPGRADE-LVL"+lvl+"-PRICE", 0);
		return price;
	}
	
	public String getSlotCategory(int slot, String category) 
	{
		FileConfiguration c = getConfig();
		if (c == null) {return "MainPage";}
		return c.getString("Pages."+category+".Item-"+slot+".CATEGORY", "MainPage");
	}
	
	public String getSlotUpgrade(int slot, String category)
	{
		FileConfiguration c = getConfig();
		if (c == null) {return "";}
		return c.getString("Pages."+category+".Item-"+slot+".UPGRADE", "");
	}

	public FileConfiguration getConfig()
	{
		File file = new File(main.getDataFolder()+File.separator+"Shops"+File.separator+ID+".yml");
		if (!file.exists()) {return null;}
		return YamlConfiguration.loadConfiguration(file);
	}

	public boolean addArena(Arena arena)
	{
		if (arena == null) {return false;}
		if (arenas.contains(arena.getID())) {return false;}
		return arenas.add(arena.getID());
	}
	
	public boolean hasArena(Arena arena)
	{
		if (arena == null) {return false;}
		return arenas.contains(arena.getID());
	}
	
	public boolean removeArena(Arena arena)
	{
		if (arena == null) {return false;}
		if (!arenas.contains(arena.getID())) {return false;}
		return arenas.remove(arena.getID());
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UUID getCreator() {
		return creator;
	}

	public void setCreator(UUID creator) {
		this.creator = creator;
	}

	public ArrayList<String> getArenas() {
		return arenas;
	}

	public void setArenas(ArrayList<String> arenas) {
		this.arenas = arenas;
	}

	public HashMap<String, Object> getCategories() {
		return categories;
	}

	public void setCategories(HashMap<String, Object> categories) {
		this.categories = categories;
	}
	
	public String getConfigPath() {
		return ConfigPath;
	}
	
	public void setConfigPath(String ConfigPath) {
		this.ConfigPath = ConfigPath;
	}
	
	public HashMap<UUID, String> getPlayersCategory() {
		return playersCategory;
	}
	
	public void setPlayersCategory(HashMap<UUID, String> map) {
		playersCategory = map;
	}
	
	public void ClearProgress()
	{
		progress.clear();
	}
	
	public HashMap<String, Integer> getPlayerProgress()
	{
		return progress;
	}
}
