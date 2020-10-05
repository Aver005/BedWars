package kiviuly.bedwars;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Events implements Listener
{
	private Main main;
	public Events(Main main) {this.main = main;}
	public void SM(String msg, Player p) {p.sendMessage(msg);}
	
	@EventHandler
	public void onPlayerUseCommand(PlayerCommandPreprocessEvent e)
	{
		Player p = e.getPlayer();
		
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (!e.getWhoClicked().getType().equals(EntityType.PLAYER)) {return;}
		Player p = (Player) e.getWhoClicked();
		//if (!main.isPlayerInGame(p)) {return;}
		
		String title = e.getView().getTitle();
		
		if (title.equals("§c§lBed Wars"))
		{
			ItemStack is = e.getCurrentItem();
			e.setCancelled(true);
			if (is == null) {return;}
			if (is.getType().equals(Material.STAINED_GLASS_PANE)) {return;}
			
			ItemMeta meta = is.getItemMeta();
			if (is.getType().equals(Material.WOOL))
			{
				if (is.getData().getData() == 5)
				{
					Arena arena = main.getArenaByDisplayName(meta.getDisplayName());
					if (arena == null) {p.sendMessage("§cОшибка. Данная арена не найдена."); main.OpenGameMenu(p); return;}
					arena.addPlayerToLobby(p);
				}
			}
			return;
		}
		
		Shop shop = main.getShopsByPlayers().getOrDefault(p.getUniqueId(), null);
		if (shop == null) {return;}
		String category = shop.getPlayersCategory().getOrDefault(p.getUniqueId(), "");
		if (category.isEmpty()) {return;}
		String catTitle = shop.getTitleFromCategory(category);
		
		if (catTitle.equals(title)) 
		{
			int slot = e.getSlot();
			String itemType = shop.getSlotType(slot, category);
			e.setCancelled(true);
			
			if (itemType.equals("NONE")) {return;}
			
			if (itemType.equals("CATEGORY"))
			{
				String newCategory = shop.getSlotCategory(slot, category);
				shop.Open(p, newCategory);
				return;
			}
			
			int myIron = main.getAmountOfMaterial(p, Material.IRON_INGOT);
			int myGold = main.getAmountOfMaterial(p, Material.GOLD_INGOT);
			int myDiamond = main.getAmountOfMaterial(p, Material.DIAMOND);
			int myEmerald = main.getAmountOfMaterial(p, Material.EMERALD);
			
			if (itemType.equals("ITEM"))
			{
				String price = shop.getItemSlotPrice(slot, category);
				int iron = Integer.parseInt(price.split("I")[0]); price = price.split("I")[1];
				int gold = Integer.parseInt(price.split("G")[0]); price = price.split("G")[1];
				int diamond = Integer.parseInt(price.split("D")[0]); price = price.split("D")[1];
				int emerald = Integer.parseInt(price.split("E")[0]);
				
				if (iron > myIron || gold > myGold || diamond > myDiamond || emerald > myEmerald) 
				{
					p.sendMessage("§cУ вас не хватает ресурсов.");
					return;
				}
				
				main.takeMaterial(Material.IRON_INGOT, iron, p);
				main.takeMaterial(Material.GOLD_INGOT, gold, p);
				main.takeMaterial(Material.DIAMOND, diamond, p);
				main.takeMaterial(Material.EMERALD, emerald, p);
				p.sendMessage("§2Покупка совершена. (§7-"+iron+" §6-"+gold+" §b-"+diamond+" §a-"+emerald+"§2)");
				ItemStack is = shop.getSlotItem(slot, category);
				p.getInventory().addItem(is);
				return;
			}
			
			if (itemType.equals("UPGRADE"))
			{
				String upgrade = shop.getSlotUpgrade(slot, category);
				if (upgrade.isEmpty()) {return;}
				Arena arena = main.getPlayerArena(p);
				if (arena == null) {return;}
				
				int lvl = shop.getPlayerProgress().getOrDefault(p.getUniqueId()+"-"+upgrade+"-LVL", 0);
				int price = shop.getUpgradeSlotPrice(slot, category, lvl);
				Team team = arena.getPlayerTeam(p); lvl++;
				
				if (price > myDiamond) 
				{
					p.sendMessage("§cУ вас не хватает ресурсов.");
					return;
				}
				
				if (upgrade.equals("Resources"))
				{
					if (team.)
				}
			}
			
			return;
		}
	}
	
	@EventHandler
	public void onFoodLevelChanged(FoodLevelChangeEvent e)
	{
		if (!e.getEntity().getType().equals(EntityType.PLAYER)) {return;}
		Player p = (Player) e.getEntity();
		
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			e.setCancelled(true);
			p.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e)
	{
		Player p = e.getPlayer();
		
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			arena.getInGameEntities().add(e.getItemDrop());
			return;
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			if (arena.hasBed(p)) {arena.addDeadPlayer(p, DamageCause.CUSTOM, null); return;}
			arena.addPlayerToSpectators(p);
			return;
		}
		
		for(Player pl : Bukkit.getOnlinePlayers())
		{
			if (pl == null) {continue;}
			if (pl.equals(p)) {continue;}
			
			if (main.isPlayerInGame(pl)) {p.hidePlayer(main, p); pl.hidePlayer(main, p);}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		Location t = e.getTo();
		Location f = e.getFrom();
		if (t.getBlockX() == f.getBlockX() && t.getBlockZ() == f.getBlockZ()) {return;}
		
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			Team team = arena.getPlayerTeam(p);
			if (team == null) {return;}
			if (!team.isHealingIsland()) {return;}
			int lvl = 0;
			Location l = team.getSpawnLocation();
			if (l == null) {return;}
			if (l.distance(t) > 8) 
			{
				if (p.hasPotionEffect(PotionEffectType.HEAL)) {p.removePotionEffect(PotionEffectType.HEAL);}
				return;
			}
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, Integer.MAX_VALUE, lvl));
			return;
		}
	}
	
	@EventHandler
	public void onPlayerClickEntity(PlayerInteractEntityEvent e)
	{
		Player p = e.getPlayer();
		Entity ent = e.getRightClicked();
		
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			if (!ent.getType().equals(EntityType.VILLAGER)) {return;}
			e.setCancelled(true);
			Shop shop = arena.getShopByLocation(ent.getLocation().clone().add(-0.5, -1, -0.5));
			if (shop == null) {return;}
			shop.Open(p, "MainPage");
			return;
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{
		Player p = e.getPlayer();
		String msg = e.getMessage();
		UUID id = p.getUniqueId();
		
		Arena arena = main.getPlayerArena(p);
		if (arena == null) {return;}
		
		if (arena.isPlayerInLobby(id))
		{
			e.setCancelled(true);
			Chat chat = arena.getLobbyChat();
			chat.SendMessage(msg, p);
		}
		
		if (arena.isPlayerInGame(id))
		{
			e.setCancelled(true);
			
			Chat chat = arena.getGameChat();
			if (chat.hasStartedSymbol() && msg.startsWith(chat.getMessageStartedSymbol()))
			{
				chat.SendMessage(msg, p);
				return;
			}
			
			chat = arena.getPlayerTeamChat(p);
			if (chat != null) 
			{
				chat.SendMessage(msg, p);
				return;
			}
		}
		
		if (arena.isPlayerSpectating(id))
		{
			e.setCancelled(true);
			Chat chat = arena.getSpectatorChat();
			chat.SendMessage(msg, p);
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e)
	{
		if (!e.getEntity().getType().equals(EntityType.PLAYER)) {return;}
		
		Player p = (Player) e.getEntity();
		if (main.isPlayerWaiting(p)) {e.setDamage(0); return;}
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			
			if (e.getDamage() >= p.getHealth() && !e.getCause().equals(DamageCause.ENTITY_ATTACK))
			{
				e.setDamage(0);
				arena.addDeadPlayer(p, e.getCause(), null);
			}
			return;
		}
	}
	
	@EventHandler
	public void onPlayerDamagePlayer(EntityDamageByEntityEvent e)
	{
		if (!e.getEntity().getType().equals(EntityType.PLAYER)) {return;}
		if (!e.getDamager().getType().equals(EntityType.PLAYER)) {return;}
		
		Player p = (Player) e.getEntity();
		Player d = (Player) e.getDamager();
		
		if (main.isPlayerWaiting(p)) {e.setDamage(0); return;}
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			arena = main.getPlayerArena(d);
			if (!arena.equals(main.getPlayerArena(p))) {return;}
			
			if (arena.getPlayerTeam(p).equals(arena.getPlayerTeam(d))) {e.setCancelled(true); return;}
				
			double doubleStat = (double) arena.getPlayersStats().getOrDefault(d.getName()+"-AllDamage", 0.0) + e.getDamage();
			arena.getPlayersStats().put(d.getName()+"-AllDamage", doubleStat);
			doubleStat = (double) arena.getArenaStats().getOrDefault("AllPlayersDamage", 0.0) + e.getDamage();
			arena.getArenaStats().put("AllPlayersDamage", doubleStat);
			
			if (e.getDamage() >= p.getHealth())
			{
				e.setDamage(0);
				arena.addDeadPlayer(p, DamageCause.ENTITY_ATTACK, d);
			}
			return;
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					p.spigot().respawn();
					arena.addDeadPlayer(p, DamageCause.CUSTOM, null);
				}
				
			}.runTaskLater(main, 10L);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		Block b = e.getBlock();
		
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			
			if (b.getType().equals(Material.BED_BLOCK))
			{
				Team team = arena.getTeamByBedLocation(b.getLocation());
				if (team == null) {e.setCancelled(true); return;}
				if (team.equals(arena.getPlayerTeam(p))) {e.setCancelled(true); return;}
				
				e.setDropItems(false);
				arena.PlayerBreakEnemyBed(p, team);
				return;
			}
			
			if (!arena.getPlacedBlocks().containsKey(b.getLocation())) {e.setCancelled(true); return;}
			String itemName = arena.getPlacedBlocks().get(b.getLocation());
			arena.getPlacedBlocks().remove(b.getLocation());
			if (itemName.isEmpty()) {return;}
			e.setDropItems(false);
			ItemStack is = new ItemBuilder(b.getType()).displayname(itemName).build();
			b.getWorld().dropItemNaturally(b.getLocation(), is);
			return;
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		Block b = e.getBlock();
		Material type = b.getType();
		
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			if (!arena.getAllowedBlocks().contains(type.name())) {e.setCancelled(true); return;}
			
			for(Team team : arena.getTeamList())
			{
				Location l = team.getSpawnLocation();
				if (l.distance(b.getLocation()) < 3) {e.setCancelled(true); return;}
			}
			
			ArrayList<String> allGenLocations = (ArrayList<String>) arena.getDiamondLocations().clone();
			allGenLocations.addAll(arena.getEmeraldLocations());
			for(String s : allGenLocations)
			{
				if (s.isEmpty()) {continue;}
				Location l = (Location) main.StringToLocation(s);
				if (l == null) {continue;}
				if (l.distance(b.getLocation()) < 4) {e.setCancelled(true); return;}
			}
			
			String itemName = "";
			ItemStack item = e.getItemInHand();
			if (item != null) 
			{
				if (item.hasItemMeta())
				{
					ItemMeta meta = item.getItemMeta();
					if (meta.hasDisplayName())
					{itemName = meta.getDisplayName();}
				}
			}
			arena.getPlacedBlocks().put(b.getLocation(), itemName);
			return;
		}
		
		ItemStack item = e.getItemInHand();
		if (item == null) {return;}
		if (!item.hasItemMeta()) {return;}
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName() || !meta.hasLore()) {return;}
		String dName = meta.getDisplayName();
		List<String> lore = meta.getLore();
		
		if (!dName.equals("§aОтметчик")) {return;}
		String arenaName = lore.get(4).replace("§fАрена: §e", "");
		String teamName = lore.get(5).replace("§fКоманда: §e", "");
		if (!main.isArena(arenaName)) {return;}
		Arena arena = main.getArena(arenaName);
		if (arena == null) {return;}
		Boolean isTeam = arena.isTeamExist(teamName);
		Location l = b.getLocation();
		e.setCancelled(true);
		
		Team team = arena.getTeamByID(teamName);
		if (team == null) {return;}
		
		if (type.equals(Material.BEACON) && isTeam) 
		{SM("§2Спавн добавлен на арену §b"+arenaName+"§2 команды §e"+teamName+"§2.",p);team.setSpawnLocation(l);}
		if (type.equals(Material.DIAMOND_BLOCK)) 
		{SM("§2Алмазный генератор добавлен на арену §b"+arenaName+"§2.",p); arena.addDiamondGenerator(l);}
		if (type.equals(Material.EMERALD_BLOCK) && !isTeam) 
		{SM("§2Изумрудный генератор добавлен на арену §b"+arenaName+"§2.",p); arena.addEmeraldGenerator(l);}
		if (type.equals(Material.EMERALD_BLOCK) && isTeam) 
		{SM("§2Изумрудный генератор добавлен на арену §b"+arenaName+"§2 команде §e"+teamName+"§2.",p); }
		if (type.equals(Material.IRON_BLOCK) && isTeam) 
		{SM("§2Железный генератор добавлен на арену §b"+arenaName+"§2 команде §e"+teamName+"§2.",p); }
		if (type.equals(Material.GOLD_BLOCK) && isTeam) 
		{SM("§2Золотой генератор добавлен на арену §b"+arenaName+"§2 команде §e"+teamName+"§2.",p); }
		if (type.equals(Material.BED_BLOCK) && isTeam) 
		{
			e.setCancelled(false); 
			SM("§2Золотой генератор добавлен на арену §b"+arenaName+"§2 команде §e"+teamName+"§2.",p); 
			Location loc = Main.getNearBlock(l, Material.BED_BLOCK);
			team.setBedHeadLocation(l);
			team.setBedFeedLocation(loc);
		}
		SM("§eX: "+l.getBlockX()+" Y: "+l.getBlockY()+" Z: "+l.getBlockZ()+" World: "+l.getWorld().getName(),p);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		
		if (main.isPlayerInGame(p)) 
		{
			Arena arena = main.getPlayerArena(p);
			if (arena == null) {return;}
			if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {return;}
			Block b = e.getClickedBlock();
			if (!b.getType().equals(Material.CHEST)) {return;}
			Chest chest = (Chest) b.getState();
			arena.getOpennedChests().add(chest);
			return;
		}
		
		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {return;}
		ItemStack item = e.getItem();
		if (item == null) {return;}
		if (!item.hasItemMeta()) {return;}
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName() || !meta.hasLore()) {return;}
		String dName = meta.getDisplayName();
		List<String> lore = meta.getLore();
		
		if (!dName.equals("§aОтметчик")) {return;}
		String arenaName = lore.get(4).replace("§fАрена: §e", "");
		if (!main.isArena(arenaName)) {return;}
		Arena arena = main.getArena(arenaName);
		if (arena == null) {return;}
		String shopName = lore.get(6).replace("§fТорговец: §e", "");
		if (!main.isShop(shopName)) {return;}
		Shop shop = main.getShop(shopName);
		if (shop == null) {return;}
		Block b = e.getClickedBlock();
		if (b == null) {return;}
		Location l = b.getLocation();
		e.setCancelled(true);
		arena.addShop(l, shop);
		SM("§2Точка появления торговца §e"+shop.getID()+" §2добавлен на арену §b"+arena.getID()+"§2.",p);
		SM("§eX: "+l.getBlockX()+" Y: "+l.getBlockY()+" Z: "+l.getBlockZ()+" World: "+l.getWorld().getName(),p);
	}
}
