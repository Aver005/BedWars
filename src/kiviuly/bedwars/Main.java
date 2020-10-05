package kiviuly.bedwars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import net.minecraft.server.v1_12_R1.ItemArmor;

public class Main extends JavaPlugin
{
	public static Main main;
	public FileConfiguration config = null;
	
	private HashMap<String, Arena> arenasByName = new HashMap<>();
	private HashMap<String, Shop> shopsByName = new HashMap<>();
	private HashMap<String, Object> arenasByPlayer = new HashMap<>(); 
	private HashMap<UUID, Shop> shopsByPlayers = new HashMap<>(); 
	
	private ArrayList<String> aliases = new ArrayList<>();
	private ArrayList<String> types = new ArrayList<>();
	
	@Override
	public void onEnable() 
	{
		aliases.add("bw"); aliases.add("bws");
		types.add("NORMAL"); types.add("HARD");
		main = this;
		
		getServer().getPluginManager().registerEvents(new Events(main), this);
		getCommand("bedwars").setExecutor(new Commands(main));
		getCommand("bedwars").setAliases(aliases);
		config = this.getConfig();
		
		reloadCFG();
	}
	
	@Override
	public void onDisable() {saveCFG();}
	
	public FileConfiguration getConfig() {return config;}
	public static Main getMain() {return main;}
	public ArrayList<String> getTypes() {return types;}
	public HashMap<String, Arena> getArenasByName() {return arenasByName;}
	public void setArenasByName(HashMap<String, Arena> arenasByName) {this.arenasByName = arenasByName;}
	public void addArena(String name, Arena arena) {this.arenasByName.put(name, arena);}
	public void addShop(String name, Shop shop) {this.shopsByName.put(name, shop);}

	public void removeArena(String name) {this.arenasByName.remove(name);}
	public Boolean isArena(String name) {return arenasByName.containsKey(name);}
	public Boolean isShop(String name) {return shopsByName.containsKey(name);}

	public Arena getArena(String name) {return arenasByName.getOrDefault(name,null);}
	public Shop getShop(String shopName) {return shopsByName.getOrDefault(shopName,null);}

	public boolean isPlayerInGame(Player p) {return (boolean) arenasByPlayer.getOrDefault(p.getUniqueId()+"-IsPlaying", false);}
	public boolean isPlayerWaiting(Player p) {return (boolean) arenasByPlayer.getOrDefault(p.getUniqueId()+"-IsWaiting", false);}
	
	public Arena getPlayerArena(Player p) 
	{
		String arenaName = (String) arenasByPlayer.getOrDefault(p.getUniqueId()+"-Arena", "");
		if (arenaName.isEmpty()) {return null;}
		return arenasByName.getOrDefault(arenaName,null);
	}
	
	public Inventory fillFreeSlots(Inventory inv, short data, String type)
	{
		for(int i = 0; i < inv.getSize(); i++) 
		{
			if (inv.getItem(i) != null) {continue;}
			
			if (type.equals("WALLS") && (i > 9 && i < inv.getSize()-10 && i % 9 != 0 && (i+1) % 9 != 0)) {continue;}
			ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, data);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(" ");
			item.setItemMeta(meta);
			inv.setItem(i,item);
		}
		return inv;
	}
	
	public void reloadCFG()
	{
		File temp = getDataFolder();
		if (!temp.exists()) {temp.mkdir();}
		temp = new File(getDataFolder() + File.separator + "config.yml");
		if (!temp.exists()) {try {temp.createNewFile();} catch (IOException e) {e.printStackTrace();}}
		
		config = YamlConfiguration.loadConfiguration(temp);
		
		temp = new File(this.getDataFolder() + File.separator + "Arenas");
		if (!temp.exists()) {temp.mkdir();}
		
		for(File f : temp.listFiles())
		{
			if (!f.getName().endsWith(".yml")) {continue;}
			Arena arena = new Arena(f.getName().replace(".yml", ""), f.getName().replace(".yml", ""), 0, null);
			Boolean isLoaded = loadArena(arena, f);
			if (isLoaded) {arenasByName.put(f.getName().replace(".yml", ""), arena);}
		}
		
		temp = new File(this.getDataFolder() + File.separator + "Shops");
		if (!temp.exists()) {temp.mkdir();}
		
		for(File f : temp.listFiles())
		{
			if (!f.getName().endsWith(".yml")) {continue;}
			Shop shop = new Shop("", null);
			Boolean isLoaded = loadShop(shop, f);
			if (isLoaded) {shopsByName.put(f.getName().replace(".yml", ""), shop);}
		}
	}
	
	public void saveCFG()
	{
		File temp = new File(getDataFolder() + File.separator + "config.yml");
		if (!temp.exists()) {try {temp.createNewFile();} catch (IOException e) {e.printStackTrace();}}
		try {config.save(temp);} catch (IOException e) {e.printStackTrace();}
		getLogger().info("Config Saved!");
		
		if (!arenasByName.isEmpty())
		{
			temp = new File(getDataFolder() + File.separator + "Arenas");
			if (!temp.exists()) {temp.mkdir();}
			for(String s : arenasByName.keySet())
			{
				File f = new File(getDataFolder() + File.separator + "Arenas" + File.separator + s + ".yml");
				Arena arena = arenasByName.get(s);
				Boolean isSaved = saveArena(arena, f);
				if (isSaved) {getLogger().info("Arena '"+s+"' saved.");}
			}
			
			getLogger().info("All ARENAS Saved!");
			
			temp = new File(getDataFolder() + File.separator + "Shops");
			if (!temp.exists()) {temp.mkdir();}
			for(String s : shopsByName.keySet())
			{
				File f = new File(getDataFolder() + File.separator + "Shops" + File.separator + s + ".yml");
				Shop shop = shopsByName.get(s);
				Boolean isSaved = saveShop(shop, f);
				if (isSaved) {getLogger().info("Shop '"+s+"' saved.");}
			}
			
			getLogger().info("All SHOPS Saved!");
		}
	}

	public HashMap<String, Object> getArenasByPlayer() {return arenasByPlayer;}
	public void setArenasByPlayer(HashMap<String, Object> arenasByPlayer) {this.arenasByPlayer = arenasByPlayer;}
	public HashMap<UUID, Shop> getShopsByPlayers() {return shopsByPlayers;}
	public void setShopsByPlayers(HashMap<UUID, Shop> shopsByPlayers) {this.shopsByPlayers = shopsByPlayers;}

	public void OpenGameMenu(Player p) 
	{
		Inventory inv = Bukkit.createInventory(null, 54, "§c§lBed Wars");
		inv = fillFreeSlots(inv, (short)2, "WALLS");
		for(String s : arenasByName.keySet())
		{
			Arena arena = arenasByName.get(s);
			if (!arena.isEnabled()) {continue;}
			if (arena.isStarted()) {continue;}
			ItemStack is = new ItemBuilder(Material.WOOL)
				.damage((short)5).lore("")
				.lore("§fИгроков: §2"+arena.getPlayersInLobby().size()+" / "+arena.getMaxPlayers())
				.lore("§fОписание:")
				.lore("§e"+arena.getDescription())
				.lore("").lore("§2§lНажмите, §2чтобы войти")
				.displayname(arena.getName()).build();
			
			inv.addItem(is);
		}
		p.openInventory(inv);
	}

	public void savePlayerData(Player p)
	{
		File temp = new File(getDataFolder() + File.separator + "Players");
		if (!temp.exists()) {temp.mkdir();}
		
		temp = new File(getDataFolder() + File.separator + "Players" + File.separator + p.getUniqueId() + ".data");
		HashMap<String, Object> data = new HashMap<>();
		
		data.put("Location", p.getLocation());
		data.put("DisplayName", p.getDisplayName());
		data.put("InventoryContents", p.getInventory().getContents());
		data.put("ArmorContents", p.getInventory().getArmorContents());
		data.put("EnderChest", p.getEnderChest().getContents());
		data.put("HP", p.getHealth());
		data.put("FOOD", p.getFoodLevel());
		data.put("isFlying", p.isFlying());
		data.put("GameMode", p.getGameMode());
		data.put("PotionEffects", p.getActivePotionEffects());
		data.put("LEVEL", p.getLevel());
		data.put("EXP", p.getExp());
		data.put("WalkSpeed", p.getWalkSpeed());
		data.put("FlySpeed", p.getFlySpeed());
		data.put("AllowFlight", p.getAllowFlight());
		
		try 
		{
			ObjectOutputStream ois = new BukkitObjectOutputStream(new FileOutputStream(temp));
			ois.writeObject(data); ois.flush(); ois.close();
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	public void loadPlayerData(Player p)
	{
		File temp = new File(getDataFolder() + File.separator + "Players");
		
		temp = new File(getDataFolder() + File.separator + "Players" + File.separator + p.getUniqueId() + ".data");
		if (temp.exists())
		{
			try 
			{
				ObjectInputStream ois = new BukkitObjectInputStream(new FileInputStream(temp));
				HashMap<String, Object> data = (HashMap<String, Object>) ois.readObject();
				ois.close();
				
				p.setDisplayName((String) data.getOrDefault("DisplayName", p.getDisplayName()));
				p.getInventory().setContents((ItemStack[]) data.getOrDefault("InventoryContents", p.getInventory().getContents()));
				p.getInventory().setContents((ItemStack[]) data.getOrDefault("EnderChest", p.getEnderChest().getContents()));
				p.getInventory().setArmorContents((ItemStack[]) data.getOrDefault("ArmorContents", p.getInventory().getArmorContents()));
				p.setHealth((double) data.getOrDefault("HP", p.getHealth()));
				p.setFoodLevel((int) data.getOrDefault("FOOD", p.getFoodLevel()));
				p.setGameMode((GameMode) data.getOrDefault("GameMode", p.getGameMode()));
				p.addPotionEffects((Collection<PotionEffect>) data.getOrDefault("PotionEffects", p.getActivePotionEffects()));
				p.setLevel((int) data.getOrDefault("LEVEL", p.getLevel()));
				p.setExp((float) data.getOrDefault("EXP", p.getExp()));
				p.setWalkSpeed((float) data.getOrDefault("WalkSpeed", p.getWalkSpeed()));
				p.setFlySpeed((float) data.getOrDefault("FlySpeed", p.getFlySpeed()));
				p.teleport((Location) data.getOrDefault("Location", p.getBedSpawnLocation()));
				p.setAllowFlight((boolean) data.getOrDefault("AllowFlight", p.getAllowFlight()));
				if (p.getAllowFlight()) {p.setFlying((boolean) data.getOrDefault("isFlying", p.isFlying()));}
			} 
			catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
		}
	}
	
	public void clearPlayer(Player p)
	{
		p.setDisplayName(p.getName());
		p.getInventory().setArmorContents(null);
		p.getEnderChest().clear();
		p.getInventory().clear();
		p.setMaxHealth(20);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setGameMode(GameMode.ADVENTURE);
		p.setLevel(60);
		p.setExp(0);
		p.setFlying(false);
		p.setWalkSpeed(0.2F);
		p.setFlySpeed(0.2F);
		for(PotionEffect pi : p.getActivePotionEffects()) {p.removePotionEffect(pi.getType());}
	}
	
	public static String randomString(int targetStringLength) 
	{
	    int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    Random random = new Random();
	 
	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	 
	    return generatedString;
	}
	
	public int randomInt(int min, int max) 
	{
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public boolean isInt(String s)
	{
	    try {Integer.parseInt(s); return true;} 
	    catch (NumberFormatException ex) {return false;}
	}

	public Arena getArenaByDisplayName(String dName) 
	{
		for(String s : arenasByName.keySet())
		{
			Arena arena = arenasByName.get(s);
			if (arena == null) {continue;}
			if (arena.getName().equals(dName)) {return arena;}
		}
		
		return null;
	}

	public HashMap<String, Shop> getShopsByName() {
		return shopsByName;
	}

	public void setShopsByName(HashMap<String, Shop> shopsByName) {
		this.shopsByName = shopsByName;
	}

	public boolean saveArena(Arena arena, File file) 
	{
		try 
		{
			FileConfiguration c = YamlConfiguration.loadConfiguration(file);
			c.set("ID", arena.getID());
			c.set("Name", arena.getName());
			c.set("Description", arena.getDescription());
			c.set("Creator", arena.getCreator().toString());
			c.set("CreatedDate", arena.getCreatedDate());
			c.set("LastMatchDate", arena.getLastMatchDate());
			c.set("MinPlayers", arena.getMinPlayers());
			c.set("MaxPlayers", arena.getMaxPlayers());
			c.set("isEnabled", arena.isEnabled());
			c.set("SpectatorSpawn", arena.getSpectatorsSpawn());
			c.set("LobbyLocation", arena.getLobbyLocation());
			c.set("DiamondLocations", arena.getDiamondLocations());
			c.set("EmeraldLocations", arena.getEmeraldLocations());
			c.set("StartItems", arena.getStartItems());
			c.set("Shops", arena.getShops());
			c.set("DiamondGeneratorUpdateTime", arena.getDiamondGeneratorUpdateTime());
			c.set("EmeraldGeneratorUpdateTime", arena.getEmeraldGeneratorUpdateTime());
			c.set("BedsDestroyingTime", arena.getBedsDestroyingTime());
			c.set("MatchEndTime", arena.getMatchEndTime());
			c.set("AllowedBlocks", arena.getAllowedBlocks());
			c.set("StartedMessage", arena.getStartedMessage());
			c.set("TeamsCount", arena.getTeamList().size());
			
			for(int i = 0; i < arena.getTeamList().size(); i++)
			{
				Team team = arena.getTeamList().get(i);
				if (team == null) {continue;}
				String str = "Teams.Team-"+i;
				c.set(str+".ID", team.getID());
				c.set(str+".Name", team.getName());
				c.set(str+".Color", team.getColor());
				c.set(str+".MinPlayers", team.getMinPlayers());
				c.set(str+".MaxPlayers", team.getMaxPlayers());
				c.set(str+".IronGenerator", team.getIronGeneratorLocation());
				c.set(str+".IronSpawnPeriod", team.getIronSpawnPeriod());
				c.set(str+".IronSpawnCount", team.getIronSpawnCount());
				c.set(str+".GoldGenerator", team.getGoldGeneratorLocation());
				c.set(str+".GoldSpawnPeriod", team.getGoldSpawnPeriod());
				c.set(str+".GoldSpawnCount", team.getGoldSpawnCount());
				c.set(str+".EmeraldGenerator", team.getEmeraldGeneratorLocation());			
				c.set(str+".EmeraldSpawnPeriod", team.getEmeraldSpawnPeriod());
				c.set(str+".EmeraldSpawnCount", team.getEmeraldSpawnCount());
				c.set(str+".Spawn", team.getSpawnLocation());			
			}
			
			c.save(file);
			return true;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean loadArena(Arena arena, File file) 
	{
		FileConfiguration c = YamlConfiguration.loadConfiguration(file);
		arena.setID(c.getString("ID", arena.getID()));
		arena.setName(c.getString("Name", arena.getName()));
		arena.setDescription(c.getString("Description", arena.getDescription()));
		arena.setCreator(UUID.fromString(c.getString("Creator", "")));
		arena.setCreatedDate((Date) c.get("CreatedDate", arena.getCreatedDate()));
		arena.setLastMatchDate((Date) c.get("LastMatchDate", arena.getLastMatchDate()));
		arena.setMinPlayers(c.getInt("MinPlayers", arena.getMinPlayers()));
		arena.setMaxPlayers(c.getInt("MaxPlayers", arena.getMaxPlayers()));
		arena.setEnabled(c.getBoolean("isEnabled", arena.isEnabled()));
		arena.setSpectatorsSpawn((Location) c.get("SpectatorSpawn", arena.getSpectatorsSpawn()));
		arena.setLobbyLocation((Location) c.get("LobbyLocation", arena.getLobbyLocation()));
		arena.setDiamondLocations((ArrayList<String>) c.get("DiamondLocations", arena.getDiamondLocations()));
		arena.setEmeraldLocations((ArrayList<String>) c.get("EmeraldLocations", arena.getEmeraldLocations()));
		arena.setStartItems((ArrayList<ItemStack>) c.get("StartItems", arena.getStartItems()));
		arena.setShops((HashMap<String, Object>) c.getConfigurationSection("Shops").getValues(false));
		arena.setAllowedBlocks((ArrayList<String>) c.get("AllowedBlocks", arena.getAllowedBlocks()));
		arena.setDiamondGeneratorUpdateTime(c.getInt("DiamondGeneratorUpdateTime", arena.getDiamondGeneratorUpdateTime()));
		arena.setEmeraldGeneratorUpdateTime(c.getInt("EmeraldGeneratorUpdateTime", arena.getEmeraldGeneratorUpdateTime()));
		arena.setBedsDestroyingTime(c.getInt("BedsDestroyingTime", arena.getBedsDestroyingTime()));
		arena.setMatchEndTime(c.getInt("MatchEndTime", arena.getMatchEndTime()));
		arena.setStartedMessage(c.getStringList("StartedMessage"));
		int teamsCount = c.getInt("TeamsCount", 0);
		
		for(int i = 0; i < teamsCount; i++)
		{
			String str = "Teams.Team-"+i;
			
			String ID = c.getString(str+".ID", "Error");
			String color = c.getString(str+".ID", "Error");
			int maxPl = c.getInt(str+".MaxPlayers", 0);
			
			Team team = new Team(ID, maxPl);
			team.setName(c.getString(str+".Name"));
			team.setColor(color);
			team.setMinPlayers(c.getInt(str+".MinPlayers"));
			team.setIronGeneratorLocation((Location) c.get(str+".IronGenerator"));
			team.setIronSpawnPeriod(c.getInt(str+".IronSpawnPeriod"));
			team.setIronSpawnCount(c.getInt(str+".IronSpawnCount"));
			team.setGoldGeneratorLocation((Location) c.get(str+".GoldGenerator"));
			team.setGoldSpawnPeriod(c.getInt(str+".GoldSpawnPeriod"));
			team.setGoldSpawnCount(c.getInt(str+".GoldSpawnCount"));
			team.setEmeraldGeneratorLocation((Location) c.get(str+".EmeraldGenerator"));
			team.setEmeraldSpawnPeriod(c.getInt(str+".EmeraldSpawnPeriod"));
			team.setEmeraldSpawnCount(c.getInt(str+".EmeraldSpawnCount"));
			team.setSpawnLocation((Location) c.get(str+".Spawn"));
			arena.addTeam(ID, maxPl, color);
		}
		
		return true;
	}

	public boolean saveShop(Shop shop, File file) 
	{
		try 
		{
			FileConfiguration c = YamlConfiguration.loadConfiguration(file);
			c.set("ID", shop.getID());
			c.set("Title", shop.getTitle());
			c.set("Creator", shop.getCreator().toString());
			//c.set("Pages", shop.getCategories());
			c.save(file);
			return true;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean loadShop(Shop shop, File file) 
	{
		FileConfiguration c = YamlConfiguration.loadConfiguration(file);
		shop.setID(c.getString("ID", shop.getID()));
		shop.setTitle(c.getString("Title", shop.getTitle()));
		//shop.setCategories((HashMap<String, Object>) c.getConfigurationSection("Pages").getValues(true));
		shop.setCreator(UUID.fromString(c.getString("Creator", "")));
		shop.setConfigPath(file.getPath());
		return true;
	}

	public String LocationToString(Location l) {return l.getWorld().getName()+"/"+l.getBlockX()+"/"+l.getBlockY()+"/"+l.getBlockZ();}
	public Location StringToLocation(String l) {return new Location(Bukkit.getWorld(l.split("/")[0]), Integer.parseInt(l.split("/")[1]), Integer.parseInt(l.split("/")[2]), Integer.parseInt(l.split("/")[3]));}
	
	public int getAmountOfMaterial(Player p, Material mat)
	{
		int amount = 0;
		Inventory inv = p.getInventory();
		for(ItemStack is : inv.getContents())
		{
			if (is == null) {continue;}
			if (is.getType().equals(mat))
			{
				amount += is.getAmount();
			}
		}
		return amount;
	}
	
	public void takeMaterial(Material mat, int amount, Player p)
	{
		Inventory inv = p.getInventory();
		for(ItemStack is : inv.getContents())
		{
			if (is == null) {continue;}
			if (is.getType().equals(mat))
			{
				if (is.getAmount() >= amount) 
				{
					is.setAmount(is.getAmount()-amount);
					amount = 0;
					return;
				}
				else
				{
					amount -= is.getAmount();
					is.setAmount(0);
				}
			}
		}
	}
	
	public static boolean isArmor(ItemStack item) { return (CraftItemStack.asNMSCopy(item).getItem() instanceof ItemArmor);}

	public Color getColorFromChatColor(String chatColor) 
	{
		if (chatColor.endsWith("0")) {return Color.BLACK;}
		if (chatColor.endsWith("1")) {return Color.BLUE;}
		if (chatColor.endsWith("2")) {return Color.GREEN;}
		if (chatColor.endsWith("3")) {return Color.TEAL;}
		if (chatColor.endsWith("4")) {return Color.RED;}
		if (chatColor.endsWith("5")) {return Color.PURPLE;}
		if (chatColor.endsWith("6")) {return Color.ORANGE;}
		if (chatColor.endsWith("7")) {return Color.SILVER;}
		if (chatColor.endsWith("8")) {return Color.GRAY;}
		if (chatColor.endsWith("9")) {return Color.BLUE;}
		if (chatColor.endsWith("a")) {return Color.LIME;}
		if (chatColor.endsWith("b")) {return Color.AQUA;}
		if (chatColor.endsWith("c")) {return Color.RED;}
		if (chatColor.endsWith("d")) {return Color.FUCHSIA;}
		if (chatColor.endsWith("e")) {return Color.YELLOW;}
		return Color.WHITE;
	}
	
	public DyeColor getDyeColorFromChatColor(String chatColor)
	{
		if (chatColor.endsWith("0")) {return DyeColor.BLACK;}
		if (chatColor.endsWith("1")) {return DyeColor.BLUE;}
		if (chatColor.endsWith("2")) {return DyeColor.GREEN;}
		if (chatColor.endsWith("3")) {return DyeColor.CYAN;}
		if (chatColor.endsWith("4")) {return DyeColor.RED;}
		if (chatColor.endsWith("5")) {return DyeColor.PURPLE;}
		if (chatColor.endsWith("6")) {return DyeColor.ORANGE;}
		if (chatColor.endsWith("7")) {return DyeColor.SILVER;}
		if (chatColor.endsWith("8")) {return DyeColor.GRAY;}
		if (chatColor.endsWith("9")) {return DyeColor.BLUE;}
		if (chatColor.endsWith("a")) {return DyeColor.LIME;}
		if (chatColor.endsWith("b")) {return DyeColor.LIGHT_BLUE;}
		if (chatColor.endsWith("c")) {return DyeColor.RED;}
		if (chatColor.endsWith("d")) {return DyeColor.PINK;}
		if (chatColor.endsWith("e")) {return DyeColor.YELLOW;}
		return DyeColor.WHITE;
	}

	public static Location getNearBlock(Location loc, Material mat)
	{
        Location test = loc.clone();
        if(test.getWorld().getBlockAt(test.clone().add(1,0,0)).getType().equals(mat)) return test.clone().add(1,0,0);
        if(test.getWorld().getBlockAt(test.clone().add(-1,0,0)).getType().equals(mat)) return test.clone().add(-1,0,0);
        if(test.getWorld().getBlockAt(test.clone().add(0,0,1)).getType().equals(mat)) return test.clone().add(0,0,1);
        if(test.getWorld().getBlockAt(test.clone().add(0,0,-1)).getType().equals(mat)) return test.clone().add(0,0,-1);
        return null;
    }
}
