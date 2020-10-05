package kiviuly.bedwars;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Bed;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Arena
{
	private Arena arena = this;
	private Main main = Main.getMain();

	private String ID = "";
	private String name = "";
	private String description = "";
	
	private List<String> startMessage = new ArrayList<>();
	private List<String> endMessage = new ArrayList<>();

	private int minPlayers = 2;
	private int maxPlayers = 0;
	private int diamondGeneratorUpdateTime = 240;
	private int emeraldGeneratorUpdateTime = 540;
	private int bedsDestroyingTime = 900;
	private int matchEndTime = 1500;

	private Boolean isEnabled = false;
	private Boolean isStarted = false;
	private Boolean isEnding = false;

	private Location lobbyLocation = null;
	private Location spectatorsSpawn = null;

	private UUID creator = null;

	private Date lastMatchDate = null;
	private Date createdDate = new Date();

	private ArrayList<String> diamondLocations = new ArrayList<>();
	private ArrayList<String> emeraldLocations = new ArrayList<>();
	private ArrayList<ItemStack> startItems = new ArrayList<>();
	private ArrayList<String> allowedBlocks = new ArrayList<>();
	private HashMap<String, Object> shops = new HashMap<>();

	private int matchTime = 0;
	private HashMap<String, Integer> generatorsLastDrop = new HashMap<>();

	private ArrayList<UUID> playersInLobby = new ArrayList<>();
	private ArrayList<UUID> playersInMatch = new ArrayList<>();
	private ArrayList<UUID> playersInSpectators = new ArrayList<>();
	
	private ArrayList<Team> teamList = new ArrayList<>();
	private HashMap<Player, Team> playersTeam = new HashMap<>();
	
	private ArrayList<Location> inGameShopsLocations = new ArrayList<>();
	private ArrayList<Location> inGameBedsLocations = new ArrayList<>();
	private ArrayList<Entity> inGameEntities = new ArrayList<>();
	private HashMap<Location, String> inGamePlacedBlocks = new HashMap<>();
	private HashMap<Player, Integer> needToRespawn = new HashMap<>();
	
	private HashMap<String, Object> playersStats = new HashMap<>();
	private HashMap<String, Object> arenaStats = new HashMap<>();
	
	private HashMap<String, Object> upgradesProgress = new HashMap<>();
	private ArrayList<Chest> opennedChests = new ArrayList<>();

	private HashMap<String, ArmorStand> inGameDiamondGenerators = new HashMap<>();
	private HashMap<String, ArmorStand> inGameEmeraldGenerators = new HashMap<>();

	private BossBar bossbar = Bukkit.createBossBar("§e§lОжидание...", BarColor.WHITE, BarStyle.SOLID);
	
	private ArrayList<Chat> teamsChats = null;
	private Chat lobbyChat = null;
	private Chat gameChat = null;
	private Chat spectatorChat = null;
	
	private ItemStack ironItem = new ItemBuilder(Material.IRON_INGOT).displayname("§7Железо").build();
	private ItemStack goldItem = new ItemBuilder(Material.GOLD_INGOT).displayname("§6Золото").build();
	private ItemStack diamondItem = new ItemBuilder(Material.DIAMOND).displayname("§bАлмаз").build();
	private ItemStack emeraldItem = new ItemBuilder(Material.EMERALD).displayname("§aИзумруд").build();

	public Arena(String ID,String name,Integer minPLCount,UUID creator)
	{
		this.ID = ID;
		this.name = name;
		this.minPlayers = minPLCount;
		this.creator = creator;
		this.createdDate = new Date();

		this.startItems.add(new ItemBuilder(Material.LEATHER_HELMET, 1).displayname("§eВелосипедный шлем").build());
		this.startItems.add(new ItemBuilder(Material.WOOD_SWORD, 1).displayname("§eСтартовый кортик").build());

		this.allowedBlocks.add(Material.SANDSTONE.name());
		this.allowedBlocks.add(Material.WOOD.name());
		this.allowedBlocks.add(Material.WOOL.name());
		this.allowedBlocks.add(Material.GLASS.name());
		this.allowedBlocks.add(Material.OBSIDIAN.name());
		
		startMessage.add("§8╔════════════ §6Bed Wars §8════════════");
		startMessage.add("§8║");
		startMessage.add("§8║          §fСобирайте ресурсы");
		startMessage.add("§8║  §bПокупайте предметы, оружия, улучшения");
		startMessage.add("§8║         §2Защитите свою кровать");
		startMessage.add("§8║       §cУбивайте других игроков");
		startMessage.add("§8║             §6§lПобеждайте!");
		startMessage.add("§8║");
		startMessage.add("§8╚══════════════════════════════════════");
		
		endMessage.add("§8╔════════════ §6Bed Wars §8════════════");
		endMessage.add("§8║");
		endMessage.add("§8║          §f§lИгра закончена!");
		endMessage.add("§8║  §6Победитель §8― {WinnerTeam}");
		endMessage.add("§8║");
		endMessage.add("§8║           §f§lТоп убийств");
		endMessage.add("§8║  §7§l1. {BestKiller} §8― {BestKillerKills}");
		endMessage.add("§8║  §7§l2. {KillerIn2Place} §8― {KillerIn2PlaceKills}");
		endMessage.add("§8║  §7§l3. {KillerIn3Place} §8― {KillerIn3PlaceKills}");
		endMessage.add("§8║");
		endMessage.add("§8╚══════════════════════════════════════");
		
		spectatorChat = new Chat("SpectatorChat-"+ID);
		spectatorChat.setPrefix("§9[Наблюдателям] §e");
		spectatorChat.setSuffix("§8: §7");
		
		lobbyChat = new Chat("LobbyChat-"+ID);
		lobbyChat.setPrefix("§d[Ожидание...] §e");
		lobbyChat.setSuffix("§8: §7");
		lobbyChat.addPlayerPlaceHolder("%p%");
		lobbyChat.setJoinMessage("§7Игрок §b%p%§7 зашёл на арену §b"+getName());
		lobbyChat.setLeaveMessage("§7Игрок §b%p%§7 вышел");
		
		gameChat = new Chat("ArenaChat-"+ID);
		gameChat.setPrefix("§9[Крик] §e");
		gameChat.setSuffix("§8: §7");
		gameChat.addPlayerPlaceHolder("%p%");
		gameChat.addMessageInheritance(spectatorChat);
		gameChat.MessageStartedSymbol("!");
	}

	public boolean Start(int time)
	{
		if (!isEnabled || isStarted) {return false;}
		isEnding = false;

		new BukkitRunnable()
		{
			int secs = time;

			public void run()
			{
				if (secs == 0)
				{
					isStarted = true;
					
					for (int i = 0; i < diamondLocations.size(); i++)
					{
						String s = diamondLocations.get(i);
						Location l = main.StringToLocation(s);
						if (l == null) {continue;}
						inGameDiamondGenerators = SpawnGenerator(l, i, "§b§lАлмазный генератор", Material.DIAMOND_BLOCK, inGameDiamondGenerators);
					}
					for (int i = 0; i < emeraldLocations.size(); i++)
					{
						String s = emeraldLocations.get(i);
						Location l = main.StringToLocation(s);
						if (l == null) {continue;}
						inGameEmeraldGenerators = SpawnGenerator(l, i, "§a§lИзумрудный генератор",Material.EMERALD_BLOCK, inGameEmeraldGenerators);
					}

					for (String index : shops.keySet())
					{
						if (!index.startsWith("Location-")) {continue;}
						String l = index.replace("Location-", "");
						Location loc = main.StringToLocation(l);
						if (loc == null) {continue;}
						String shopID = (String) shops.get(index);
						if (!main.isShop(shopID)) {continue;}
						Shop shop = main.getShop(shopID);
						shop.ClearProgress();

						Villager trader = (Villager) loc.getWorld().spawnEntity(loc.add(0.5D, 1.0D, 0.5D),EntityType.VILLAGER);
						trader.setCustomNameVisible(true);
						trader.setAI(false);
						trader.setCanPickupItems(false);
						trader.setInvulnerable(true);
						trader.setCustomName(ChatColorParse(shop.getTitle()));
						inGameShopsLocations.add(loc);
					}

					for (UUID id : getPlayersInLobby())
					{
						Player p = Bukkit.getPlayer(id);
						if (p == null || !p.isOnline()) {continue;}
						
						Team team = getPlayerTeam(p);
						if (team == null) {continue;}
						Location loc = team.getSpawnLocation();
						if (loc == null) {continue;}
						
						String teamColor = team.getColor();
						Chat teamChat = team.getChat();
						if (teamChat == null)
						{
							teamChat = new Chat("Team"+team+"Chat"+ID);
							teamChat.setPrefix(ChatColorParse(teamColor)+"[Команде] §e");
							teamChat.setSuffix("§8: §7");
							teamChat.addPlayerPlaceHolder("%p%");
						}
						
						teamChat.addPlayer(p);
						team.setChat(teamChat);
						gameChat.addPlayer(p);
						lobbyChat.removePlayer(p);
						
						p.sendTitle("§f§lИгра началась!", "§cКроватные §eвоины - §lвперёд!", 10, 45, 40);
						for(int i = 0; i < startMessage.size(); i++)
						{
							String msg = startMessage.get(i);
							p.sendMessage(msg);
						}
						
						if (!team.isHasBed())
						{
							Block blockHead = team.getBedHeadLocation().getBlock();
				            Block blockFeed = team.getBedFeedLocation().getBlock();
				            BlockState headState = blockHead.getState();
				            BlockState feedState = blockFeed.getState();

				            headState.setType(Material.BED_BLOCK);
				            feedState.setType(Material.BED_BLOCK);
				            headState.setRawData((byte) 0x0);
				            feedState.setRawData((byte) 0x8);
				            feedState.update(true, false);
				            headState.update(true, false);

				            org.bukkit.material.Bed bedHead = (org.bukkit.material.Bed) headState.getData();
				            bedHead.setHeadOfBed(true);
				            bedHead.setFacingDirection(blockHead.getFace(blockFeed).getOppositeFace());

				            org.bukkit.material.Bed bedFeed = (org.bukkit.material.Bed) feedState.getData();
				            bedFeed.setHeadOfBed(false);
				            bedFeed.setFacingDirection(blockFeed.getFace(blockHead));

				            feedState.update(true, false);
				            headState.update(true, true);
				            
				            Bed b1 = (Bed) team.getBedHeadLocation().getBlock().getState();
				            Bed b2 = (Bed) team.getBedFeedLocation().getBlock().getState();
				            b1.setColor(main.getDyeColorFromChatColor(teamColor));
				            b2.setColor(main.getDyeColorFromChatColor(teamColor));
				            b1.update(); b2.update();
				            
				            Generator iron = new Generator(team.getID()+"-Iron", ironItem, team.getIronSpawnPeriod(), team.getIronSpawnCount());
				            iron.setShowBlock(false);
				            iron.setShowHeader(false);
				            iron.setShowFooter(false);
				            
				            Generator gold = new Generator(team.getID()+"-Gold", goldItem, team.getGoldSpawnPeriod(), team.getGoldSpawnCount());
				            gold.setShowBlock(false);
				            gold.setShowHeader(false);
				            gold.setShowFooter(false);
				            
				            Generator emerald = new Generator(team.getID()+"-Emerald", emeraldItem, team.getEmeraldSpawnPeriod(), team.getEmeraldSpawnCount());
				            emerald.setEnabled(false);
				            emerald.setShowBlock(false);
				            emerald.setShowHeader(false);
				            emerald.setShowFooter(false);
				            
				            team.addGenerator(iron);
				            team.addGenerator(gold);
				            team.addGenerator(emerald);
				            team.setHasBed(true);
						}
						
						Respawn(p);
					}

					playersInLobby.clear();
					inGamePlacedBlocks.clear();
					cancel();
				}

				for (UUID id : getPlayersInLobby())
				{
					Player p = Bukkit.getPlayer(id);
					if (p == null || !p.isOnline()) {continue;}
					p.setLevel(secs);
				}

				double progress = (1.0*secs)/(time*1.0);
				bossbar.setTitle("§eНачало через " + secs + "...");
				bossbar.setProgress(progress); secs--;
			}
		}.runTaskTimer(main, 20L, 20L);

		new BukkitRunnable()
		{
			int ticks = 0;

			public void run()
			{
				if (!isStarted) {cancel();}
				ticks++;

				for(Team team : teamList)
				{
					if (team == null) {continue;}
					
					if (matchTime == bedsDestroyingTime)
					{
						team.setHasBed(true);
						Location l = team.getBedHeadLocation();
						l.getBlock().setType(Material.AIR);
						l = team.getBedFeedLocation();
						l.getBlock().setType(Material.AIR);
					}
					
					if (team.getGenerators().size() == 0) {continue;}
					for(Generator g : team.getGenerators()) {g.tick(ticks);}
				}

				if (ticks % 20 == 0)
				{
					matchTime++;
					
					if (needToRespawn != null)
					{
						if (needToRespawn.size() > 0)
						{
							for(Player p : needToRespawn.keySet())
							{
								if (p == null) {continue;}
								int time = needToRespawn.get(p);
								if (time > matchTime) {continue;}
								p.sendTitle("§2Возрождены", "§eВремя мстить...", 20, 50, 20);
								Respawn(p);
								
								needToRespawn.remove(p);
							}
						}
					}

					if (matchTime == matchEndTime) {Stop(15); cancel();}

					String title = "", subtitle = "";
					Sound sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

					if (matchTime == diamondGeneratorUpdateTime)
					{
						title = "§b§lАлмазные генераторы";
						subtitle = "§f§lОбновлены до 2 уровня...";
					}

					if (matchTime == emeraldGeneratorUpdateTime)
					{
						title = "§a§lИзумрудные генераторы";
						subtitle = "§f§lОбновлены до 2 уровня...";
					}

					if (matchTime == bedsDestroyingTime)
					{
						title = "§c§lВсе кровати... Были уничтожены!";
						subtitle = "§e§lВторого шанса больше нет";
						sound = Sound.ENTITY_ENDERDRAGON_DEATH;
					}

					if (matchTime == diamondGeneratorUpdateTime || matchTime == emeraldGeneratorUpdateTime || matchTime == bedsDestroyingTime)
					{
						for (UUID id : getPlayersInMatch())
						{
							Player p = Bukkit.getPlayer(id);
							if (p == null) {continue;}
							p.sendTitle(title, subtitle, 20, 45, 25);
							p.playSound(p.getLocation(), sound, 1.0F, 1.0F);
						}
					}

					double progress = 1.0D;
					title = "§cОшибка. Игра сломана.";
					BarColor color = BarColor.RED;
					boolean update = false;

					if (matchTime <= diamondGeneratorUpdateTime)
					{
						progress = 1.0D - (matchTime * 1.0D / diamondGeneratorUpdateTime * 1.0D);
						title = "§f§lДо обновления §b§lАлмазного генератора";
						color = BarColor.BLUE;
						update = true;
					} 
					else if (matchTime <= emeraldGeneratorUpdateTime)
					{

						progress = 1.0D - ((matchTime-diamondGeneratorUpdateTime) * 1.0D) / ((emeraldGeneratorUpdateTime-diamondGeneratorUpdateTime) * 1.0D);
						title = "§f§lДо обновления §a§lИзумрудного генератора";
						color = BarColor.GREEN;
						update = true;
					} 
					else if (matchTime <= bedsDestroyingTime)
					{

						progress = 1.0D - ((matchTime-emeraldGeneratorUpdateTime) * 1.0D) / ((bedsDestroyingTime-emeraldGeneratorUpdateTime) * 1.0D);
						title = "§f§lДо уничтожения всех §c§lКРОВАТЕЙ";
						color = BarColor.RED;
						update = true;
					} 
					else if (matchTime <= matchEndTime)
					{

						progress = 1.0D - ((matchTime-bedsDestroyingTime) * 1.0D) / ((matchEndTime-bedsDestroyingTime) * 1.0D);
						title = "§f§lДо конца игры";
						color = BarColor.YELLOW;
						update = true;
					}

					if (update && !isEnding)
					{
						bossbar.setProgress(progress);
						bossbar.setTitle(title);
						bossbar.setColor(color);
					}

				}
			}
		}.runTaskTimer(main, (time + 1) * 20L, 1L);
		return true;
	}

	public String ChatColorParse(String str)
	{
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public boolean Stop(final int time)
	{
		if (!isEnabled || !isStarted) {return false;}
		isEnding = true;

		new BukkitRunnable()
		{
			int secs = time;

			public void run()
			{
				if (secs == time)
				{
					String winnerTeam = "";
					List<String> KillersByKills = new ArrayList<>();
				}
				
				if (secs == 0)
				{
					for (Location l : inGamePlacedBlocks.keySet())
					{
						if (l == null) {continue;}
						l.getBlock().setType(Material.AIR);
					}

					for (Entity item : inGameEntities)
					{
						if (item == null) {continue;}
						Chunk chunk = item.getLocation().getChunk();
						chunk.load();
						if (chunk.getEntities().length == 0) {continue;}
						for (Entity e : chunk.getEntities()) {if (e.getType().equals(EntityType.DROPPED_ITEM)) {e.remove();}}
						item.remove();
					}

					for (Location l : inGameShopsLocations)
					{
						if (l == null) {continue;}
						Chunk chunk = l.getChunk();
						chunk.load();
						if (chunk.getEntities().length == 0) {continue;}
						for (Entity e : chunk.getEntities()) {if (e.getType().equals(EntityType.VILLAGER)) {e.remove();}}
					}

					for (int i = 0; i < diamondLocations.size(); i++)
					{
						String s = diamondLocations.get(i);
						Location l = main.StringToLocation(s);
						if (l == null) {continue;}
						Chunk chunk = l.getChunk();
						chunk.load();
						if (chunk.getEntities().length == 0) {continue;}
						for (Entity e : chunk.getEntities()) {if (e.getType().equals(EntityType.ARMOR_STAND)){e.remove();}}
					}

					for (int i = 0; i < emeraldLocations.size(); i++)
					{
						String s = emeraldLocations.get(i);
						Location l = main.StringToLocation(s);
						if (l == null) {continue;}
						Chunk chunk = l.getChunk();
						chunk.load();
						if (chunk.getEntities().length == 0) {continue;}
						for (Entity e : chunk.getEntities())
						{
							if (e.getType().equals(EntityType.ARMOR_STAND))
							{
								e.remove();
							}
						}
					}
					
					if (opennedChests.size() > 0)
					{
						for (Chest chest : opennedChests)
						{
							if (chest == null) {continue;}
							chest.getInventory().clear();
						}
					}

					for (UUID id : getAllPlayers())
					{
						Player p = Bukkit.getPlayer(id);
						if (p == null || !p.isOnline()) {continue;}
						playersTeam.remove(p.getUniqueId() + "-Team");
						playersTeam.remove(p.getUniqueId() + "-TeamColor");
						playersTeam.remove(p.getUniqueId() + "-TeamSpawn");
						
						for(Player pl : Bukkit.getOnlinePlayers())
						{
							if (pl == null) {continue;}
							if (pl.equals(p)) {continue;}
							if (!pl.canSee(p)) {pl.showPlayer(main, p);}
							if (!p.canSee(pl)) {p.showPlayer(main, pl);}
						}
						
						main.clearPlayer(p);
						main.loadPlayerData(p);
						main.getArenasByPlayer().put(id + "-IsPlaying", false);
						main.getArenasByPlayer().put(id + "-IsWaiting", false);
						main.getArenasByPlayer().remove(id + "-Arena");
						bossbar.removePlayer(p);
					}
					
					matchTime = 0;
					isStarted = false;
					
					bossbar.removeAll();
					bossbar.setProgress(0D);
					bossbar.setTitle("§eОжидание игроков...");
					bossbar.setColor(BarColor.WHITE);
					
					opennedChests.clear();
					needToRespawn.clear();
					
					playersInLobby.clear();
					playersInMatch.clear();
					playersInSpectators.clear();
					playersTeam.clear();
					inGameDiamondGenerators.clear();
					inGameEmeraldGenerators.clear();
					inGameEntities.clear();
					inGamePlacedBlocks.clear();
					inGameShopsLocations.clear();
					inGameBedsLocations.clear();
					generatorsLastDrop.clear();
					cancel();
				}

				for (UUID id : getAllPlayers())
				{
					Player p = Bukkit.getPlayer(id);
					if (p == null || !p.isOnline()) {continue;}
					p.setLevel(secs);
				}

				bossbar.setTitle("§eВозвращение в лобби " + secs + "...");
				bossbar.setProgress(secs * 1.0D / time * 1.0D);
				secs--;
			}
		}.runTaskTimer(main, 20L, 20L);
		return true;
	}

	public void Respawn(Player p)
	{
		Team team = getPlayerTeam(p);
		if (team == null) {return;}
		UUID id = p.getUniqueId();
		main.clearPlayer(p);
		main.getArenasByPlayer().put(id + "-IsPlaying", true);
		main.getArenasByPlayer().put(id + "-IsWaiting", false);
		main.getArenasByPlayer().put(id + "-Arena", getID());
		p.setGameMode(GameMode.SURVIVAL);
		p.teleport(team.getSpawnLocation());
		if (!playersInMatch.contains(id)) {playersInMatch.add(id);}
		
		for (ItemStack is : startItems)
		{
			if (is == null) {continue;}
			is = is.clone();
			if (Main.isArmor(is))
			{
				if (is.getType().name().startsWith("LEATHER_"))
				{
					LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
					Color color = main.getColorFromChatColor(team.getColor());
					meta.setColor(color);
					is.setItemMeta(meta);
				}
				
				if (is.getType().name().endsWith("_HELMET")) {p.getInventory().setHelmet(is);}
				if (is.getType().name().endsWith("_CHESTPLATE")) {p.getInventory().setChestplate(is);}
				if (is.getType().name().endsWith("_LEGGINGS")) {p.getInventory().setLeggings(is);}
				if (is.getType().name().endsWith("_BOOTS")) {p.getInventory().setBoots(is);}
				continue;
			}
			
			p.getInventory().addItem(is);
		}
	}

	public Team getPlayerTeam(Player p)
	{
		return playersTeam.getOrDefault(p, null);
	}

	public void GeneratorRotate(HashMap<String, ArmorStand> map)
	{
		for (String index : map.keySet())
		{
			ArmorStand dg = map.get(index);
			if (dg == null) {continue;}
			if (index.startsWith("Block"))
			{
				Location l = dg.getLocation();
				World w = l.getWorld();

				double x = l.getX();
				double y = l.getY();
				double z = l.getZ();
				float pitch = l.getPitch();
				float yaw = l.clone().getYaw() + 5.0F;
				Location location = new Location(w, x, y, z, yaw, pitch);
				dg.teleport(location);
			}
		}
	}

	public void GeneratorSpawnItems(HashMap<String, ArmorStand> map, int spawnPeriod, int spawnCount, Material mat, String matName)
	{
		for (String index : map.keySet())
		{
			ArmorStand dg = map.get(index);
			if (dg == null) {continue;}
			if (index.startsWith("Block"))
			{
				Location l = dg.getLocation();
				World w = l.getWorld();
				int nextDtopTime = (Integer) generatorsLastDrop.getOrDefault(main.LocationToString(l), matchTime + spawnPeriod);

				if (matchTime == nextDtopTime && matchTime > 0)
				{
					ItemStack is = new ItemStack(mat, spawnCount);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName(matName);
					is.setItemMeta(im);
					Item item = w.dropItemNaturally(l, is);
					this.inGameEntities.add(item);
					nextDtopTime = matchTime + spawnPeriod;
				}

				dg = map.get(index.replace("Block", "FooterText"));
				int spawnTime = nextDtopTime - matchTime;
				if (spawnTime < 0) {spawnTime = 0;}
				dg.setCustomName("§3Появится через " + spawnTime + " сек.");
				generatorsLastDrop.put(main.LocationToString(l), nextDtopTime);
			}
		}
	}

	public HashMap<String, ArmorStand> SpawnGenerator(Location l, int i, String name, Material mat,
			HashMap<String, ArmorStand> map)
	{
		ArmorStand eg = (ArmorStand) l.getWorld().spawn(l.clone().add(0.5D, 1.5D, 0.5D), ArmorStand.class);
		eg.setMarker(true);
		eg.setVisible(false);
		eg.setGravity(false);
		eg.setCustomNameVisible(false);
		eg.setSilent(true);
		eg.setAI(false);
		eg.setInvulnerable(true);
		eg.setHelmet(new ItemStack(mat));
		map.put("Block-" + i, eg);

		eg = (ArmorStand) l.getWorld().spawn(l.clone().add(0.5D, 2.0D, 0.5D), ArmorStand.class);
		eg.setVisible(false);
		eg.setGravity(false);
		eg.setCustomNameVisible(true);
		eg.setSilent(true);
		eg.setAI(false);
		eg.setInvulnerable(true);
		eg.setCustomName(name);
		map.put("HeaderText-" + i, eg);

		eg = (ArmorStand) l.getWorld().spawn(l.clone().add(0.5D, 1.6D, 0.5D), ArmorStand.class);
		eg.setVisible(false);
		eg.setGravity(false);
		eg.setCustomNameVisible(true);
		eg.setSilent(true);
		eg.setAI(false);
		eg.setInvulnerable(true);
		eg.setCustomName("§3Появится через <> сек.");
		map.put("FooterText-" + i, eg);

		return map;
	}
	
	public Team getSmallerTeam()
	{
		Team team = teamList.get(0);
		int minPl = 0;
		
		for(Team t : teamList)
		{
			if (t.getLivePlayers().size() == t.getMaxPlayers()) {continue;}
			int plInTeam = team.getLivePlayers().size();
			if (plInTeam <= minPl) {team = t;}
		}
			
		return team;
	}

	public Integer addPlayerToLobby(Player p)
	{
		if (playersInLobby.size() == maxPlayers) return 5;
		if (p == null) return 1;
		if (!p.isOnline()) return 1;
		UUID id = p.getUniqueId();
		if (isStarted()) return 2;
		if (!isEnabled()) return 3;
		if (playersInLobby.contains(id)) return 4;
		
		Team team = getSmallerTeam();
		team.addPlayer(p);
		
		lobbyChat.addPlayer(p);
		playersInLobby.add(id);
		main.getArenasByPlayer().put(id + "-IsPlaying", false);
		main.getArenasByPlayer().put(id + "-IsWaiting", true);
		main.getArenasByPlayer().put(id + "-Arena", getID());
		main.savePlayerData(p);
		main.clearPlayer(p);
		
		for(Player pl : Bukkit.getOnlinePlayers())
		{
			if (pl == null) {continue;}
			if (pl.equals(p)) {continue;}
			
			if (!playersInLobby.contains(pl.getUniqueId())) 
			{
				p.hidePlayer(main, pl); 
				pl.hidePlayer(main, p);
				continue;
			}
			
			p.showPlayer(main, pl); 
			pl.showPlayer(main, p);
		}
		
		p.getInventory().setItem(8, new ItemBuilder(Material.BED).displayname("§cВыйти").lore("§fНажмите, чтобы выйти").build());
		p.teleport(this.lobbyLocation);
		
		bossbar.addPlayer(p);
		bossbar.setVisible(true);
		if (playersInLobby.size() == minPlayers) 
		{
			bossbar.setColor(BarColor.GREEN);
			bossbar.setProgress(1.0D); Start(15);
		}
		if (playersInLobby.size() > minPlayers) {bossbar.setProgress((playersInLobby.size() * 1.0)/ (maxPlayers * 1.0));}
		return 0;
	}

	public void addDeadPlayer(Player p, DamageCause reason, Entity ent)
	{
		Team team = getPlayerTeam(p);
		String colorName = ChatColorParse(team.getColor()+p.getName());
		String deathMsg = "§c[Смерть] "+colorName+"§e умер";
		
		switch(reason)
		{
			case CUSTOM: case SUICIDE: deathMsg = "§c[Смерть] "+colorName+"§e выпилился"; break;
			case BLOCK_EXPLOSION: deathMsg = "§c[Смерть] "+colorName+"§e съел ТНТ"; break;
			case CONTACT: deathMsg = "§c[Смерть] "+colorName+"§e неудачно спаял контакты"; break;
			case DRAGON_BREATH: deathMsg = "§c[Смерть] "+colorName+"§e задохнулся от пердежа"; break;
			case DROWNING: deathMsg = "§c[Смерть] "+colorName+"§e забыл как дышать"; break;
			case ENTITY_ATTACK:
				deathMsg = "§c[Смерть] " + colorName + "§e был изнасилован";
				
				if (ent instanceof Player)
				{
					String killerColorName = ChatColorParse(getPlayerTeam(((Player) ent)).getColor() + ent.getName());
					deathMsg = "§c[Смерть] " + colorName + "§e получил от " + killerColorName; 
					
					int iron = main.getAmountOfMaterial(p, Material.IRON_INGOT);
					int gold = main.getAmountOfMaterial(p, Material.GOLD_INGOT);
					int diamonds = main.getAmountOfMaterial(p, Material.DIAMOND);
					int emeralds = main.getAmountOfMaterial(p, Material.EMERALD);
					ItemStack is = null;
					
					if (iron + gold + diamonds + emeralds > 0) {ent.sendMessage("§a[Наследство] "+colorName+" §2оставил вам:");}
					if (iron > 0) {ent.sendMessage("§a[Наследство] §7Железо: §l+"+iron); is = ironItem.clone(); is.setAmount(iron); ((Player) ent).getInventory().addItem(is);}
					if (gold > 0) {ent.sendMessage("§a[Наследство] §6Золото: §l+"+gold); is = goldItem.clone(); is.setAmount(gold); ((Player) ent).getInventory().addItem(is);}
					if (diamonds > 0) {ent.sendMessage("§a[Наследство] §bАлмазы: §l+"+diamonds); is = diamondItem.clone(); is.setAmount(diamonds); ((Player) ent).getInventory().addItem(is);}
					if (emeralds > 0) {ent.sendMessage("§a[Наследство] §aИзумруды: §l+"+emeralds); is = emeraldItem.clone(); is.setAmount(emeralds); ((Player) ent).getInventory().addItem(is);}
				}
			break;
			case ENTITY_EXPLOSION: deathMsg = "§c[Смерть] "+colorName+"§e подорвался"; break;
			case FALL: deathMsg = "§c[Смерть] "+colorName+"§e поскользнулся"; break;
			case FALLING_BLOCK: deathMsg = "§c[Смерть] "+colorName+"§e словил кирпичём по бошке"; break;
			case FIRE: deathMsg = "§c[Смерть] "+colorName+"§e сгорел (зад)"; break;
			case FIRE_TICK: deathMsg = "§c[Смерть] "+colorName+"§e съел тако"; break;
			case FLY_INTO_WALL: deathMsg = "§c[Смерть] "+colorName+"§e летал в блоке (читер)"; break;
			case HOT_FLOOR: deathMsg = "§c[Смерть] "+colorName+"§e забыл, что пол - это лава"; break;
			case LAVA: deathMsg = "§c[Смерть] "+colorName+"§e поплавал в горячем говне"; break;
			case LIGHTNING: deathMsg = "§c[Смерть] "+colorName+"§e разгневал Тора"; break;
			case MAGIC: deathMsg = "§c[Смерть] "+colorName+"§e магически погиб"; break;
			case POISON: deathMsg = "§c[Смерть] "+colorName+"§e отравился шаурмой"; break;
			case PROJECTILE: deathMsg = "§c[Смерть] "+colorName+"§e превратился в ежа"; break;
			case STARVATION: deathMsg = "§c[Смерть] "+colorName+"§e устроил голодовку"; break;
			case SUFFOCATION: deathMsg = "§c[Смерть] "+colorName+"§e застрял в гробу"; break;
			case THORNS: deathMsg = "§c[Смерть] "+colorName+"§e попал под горячую руку"; break;
			case VOID: deathMsg = "§c[Смерть] "+colorName+"§e недопрыгнул"; break;
			case WITHER: deathMsg = "§c[Смерть] "+colorName+"§e ВТФ? Это как?"; break;
			default: deathMsg = "§c[Смерть] "+colorName+"§e умер"; break;
		}
		
		gameChat.SendSystemMessage(deathMsg);
		p.setGameMode(GameMode.SPECTATOR);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.getInventory().clear();
		p.teleport(spectatorsSpawn);
		
		if (team.isCanRespawn()) {needToRespawn.put(p, matchTime + 5); return;}
		
		p.sendTitle("§cЭто конец", "§eУ вас нет кровати...", 20, 40, 20);
		playersInMatch.remove(p.getUniqueId());
		playersInSpectators.add(p.getUniqueId());
		
		int teamsLeft = 1; Team lastTeam = null;
		for(UUID id : playersInMatch)
		{
			Player pl = Bukkit.getPlayer(id);
			if (pl == null) {continue;}
			if (lastTeam == null) {lastTeam = getPlayerTeam(pl); continue;}
			Team t = getPlayerTeam(pl);
			if (!t.equals(lastTeam)) {teamsLeft++;}
		}
		
		if (teamsLeft == 1) {Stop(15);}
	}

	public Integer addPlayerToSpectators(Player p)
	{
		if (p == null) return 1;
		if (!p.isOnline()) return 1;
		UUID id = p.getUniqueId();
		if (!isEnabled()) return 3;
		if (playersInSpectators.contains(id)) return 4;
		main.clearPlayer(p);
		p.teleport(spectatorsSpawn);
		playersInSpectators.add(id);
		if (playersInLobby.contains(id)) playersInLobby.remove(id);
		if (playersInMatch.contains(id)) playersInMatch.remove(id);
		return 0;
	}

	public Integer addPlayerToMatch(Player p)
	{
		if (p == null) return 1;
		if (!p.isOnline()) return 1;
		UUID id = p.getUniqueId();
		if (!isEnabled()) return 3;
		if (this.playersInMatch.contains(id)) return 4;
		main.clearPlayer(p);
		Team team = getPlayerTeam(p);
		if (team == null) return 5;
		Location loc = team.getSpawnLocation();
		if (loc == null) return 6;
		p.teleport(loc);
		playersInMatch.add(id);
		if (playersInLobby.contains(id)) playersInLobby.remove(id);
		if (playersInSpectators.contains(id)) playersInSpectators.remove(id);
		return 0;
	}

	public boolean addShop(Location l, Shop shop)
	{
		this.shops.put("Location-" + main.LocationToString(l), shop.getID());
		return true;
	}

	public boolean addDiamondGenerator(Location loc)
	{
		String l = main.LocationToString(loc);
		if (l.isEmpty())
			return false;
		if (this.diamondLocations.contains(l))
			return false;
		this.diamondLocations.add(l);
		return true;
	}

	public boolean addEmeraldGenerator(Location loc)
	{
		String l = main.LocationToString(loc);
		if (l.isEmpty())
			return false;
		if (this.emeraldLocations.contains(l))
			return false;
		this.emeraldLocations.add(l);
		return true;
	}

	public boolean isPlayerInLobby(UUID id)
	{
		return this.playersInLobby.contains(id);
	}

	public boolean isPlayerInMatch(UUID id)
	{
		return this.playersInMatch.contains(id);
	}

	public boolean isPlayerInSpectators(UUID id)
	{
		return this.playersInSpectators.contains(id);
	}
	
	public boolean isPlayerSpectating(UUID id)
	{
		return this.playersInSpectators.contains(id);
	}

	public boolean isPlayerInGame(UUID id)
	{
		return getAllPlayers().contains(id);
	}

	public String getID()
	{
		return this.ID;
	}

	public void setID(String iD)
	{
		this.ID = iD;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Integer getMinPlayers()
	{
		return this.minPlayers;
	}

	public void setMinPlayers(Integer minPlayers)
	{
		this.minPlayers = minPlayers;
	}

	public Integer getMaxPlayers()
	{
		return this.maxPlayers;
	}

	public void setMaxPlayers(Integer maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}

	public Boolean isEnabled()
	{
		return this.isEnabled;
	}

	public void setEnabled(Boolean isEnabled)
	{
		this.isEnabled = isEnabled;
	}

	public Boolean isStarted()
	{
		return this.isStarted;
	}

	public void setStarted(Boolean isStarted)
	{
		this.isStarted = isStarted;
	}

	public ArrayList<UUID> getPlayersInLobby()
	{
		return this.playersInLobby;
	}

	public void setPlayersInLobby(ArrayList<UUID> playersInLobby)
	{
		this.playersInLobby = playersInLobby;
	}

	public ArrayList<UUID> getPlayersInMatch()
	{
		return this.playersInMatch;
	}

	public void setPlayersInMatch(ArrayList<UUID> playersInMatch)
	{
		this.playersInMatch = playersInMatch;
	}

	public ArrayList<UUID> getPlayersInSpectators()
	{
		return this.playersInSpectators;
	}

	public void setPlayersInSpectators(ArrayList<UUID> playersInSpectators)
	{
		this.playersInSpectators = playersInSpectators;
	}

	public ArrayList<UUID> getAllPlayers()
	{
		ArrayList<UUID> allPlayers = (ArrayList<UUID>) getPlayersInLobby().clone();
		allPlayers.addAll(this.playersInMatch);
		allPlayers.addAll(this.playersInSpectators);
		return allPlayers;
	}

	public ArrayList<String> getDiamondLocations()
	{
		return this.diamondLocations;
	}

	public void setDiamondLocations(ArrayList<String> diamondLocations)
	{
		this.diamondLocations = diamondLocations;
	}

	public ArrayList<String> getEmeraldLocations()
	{
		return this.emeraldLocations;
	}

	public void setEmeraldLocations(ArrayList<String> emeraldLocations)
	{
		this.emeraldLocations = emeraldLocations;
	}

	public HashMap<String, Object> getShops()
	{
		return this.shops;
	}

	public void setShops(HashMap<String, Object> shops)
	{
		this.shops = shops;
	}

	public Arena getArena()
	{
		return this.arena;
	}

	public void setArena(Arena arena)
	{
		this.arena = arena;
	}

	public Location getSpectatorsSpawn()
	{
		return this.spectatorsSpawn;
	}

	public void setSpectatorsSpawn(Location spectatorsSpawn)
	{
		this.spectatorsSpawn = spectatorsSpawn;
	}

	public ArrayList<ItemStack> getStartItems()
	{
		return this.startItems;
	}

	public void setStartItems(ArrayList<ItemStack> startItems)
	{
		this.startItems = startItems;
	}

	public UUID getCreator()
	{
		return this.creator;
	}

	public void setCreator(UUID creator)
	{
		this.creator = creator;
	}

	public Date getLastMatchDate()
	{
		return this.lastMatchDate;
	}

	public void setLastMatchDate(Date lastMatchDate)
	{
		this.lastMatchDate = lastMatchDate;
	}

	public Date getCreatedDate()
	{
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

	public Location getLobbyLocation()
	{
		return this.lobbyLocation;
	}

	public void setLobbyLocation(Location l)
	{
		this.lobbyLocation = l;
	}

	public ArrayList<Location> getInGameShopsLocations()
	{
		return this.inGameShopsLocations;
	}

	public void setInGameShopsLocations(ArrayList<Location> inGameShopsLocations)
	{
		this.inGameShopsLocations = inGameShopsLocations;
	}

	public ArrayList<Location> getInGameBedsLocations()
	{
		return this.inGameBedsLocations;
	}

	public void setInGameBedsLocations(ArrayList<Location> inGameBedsLocations)
	{
		this.inGameBedsLocations = inGameBedsLocations;
	}

	public ArrayList<Entity> getInGameEntities()
	{
		return this.inGameEntities;
	}

	public void setInGameEntities(ArrayList<Entity> inGameEntities)
	{
		this.inGameEntities = inGameEntities;
	}

	public ArrayList<String> getAllowedBlocks()
	{
		return this.allowedBlocks;
	}

	public void setAllowedBlocks(ArrayList<String> allowedBlocks)
	{
		this.allowedBlocks = allowedBlocks;
	}

	public Integer getMatchTime()
	{
		return matchTime;
	}

	public void setMatchTime(Integer matchTime)
	{
		this.matchTime = matchTime;
	}

	public Shop getShopByLocation(Location l)
	{
		String loc = main.LocationToString(l);
		String shopID = (String) this.shops.getOrDefault("Location-" + loc, null);
		return main.getShop(shopID);
	}

	public HashMap<Location, String> getPlacedBlocks()
	{
		return this.inGamePlacedBlocks;
	}

	public int getDiamondGeneratorUpdateTime()
	{
		return this.diamondGeneratorUpdateTime;
	}

	public int getEmeraldGeneratorUpdateTime()
	{
		return this.emeraldGeneratorUpdateTime;
	}

	public int getBedsDestroyingTime()
	{
		return this.bedsDestroyingTime;
	}

	public int getMatchEndTime()
	{
		return this.matchEndTime;
	}

	public void setDiamondGeneratorUpdateTime(int i)
	{
		this.diamondGeneratorUpdateTime = i;
	}

	public void setEmeraldGeneratorUpdateTime(int i)
	{
		this.emeraldGeneratorUpdateTime = i;
	}

	public void setBedsDestroyingTime(int i)
	{
		this.bedsDestroyingTime = i;
	}

	public void setMatchEndTime(int i)
	{
		this.matchEndTime = i;
	}

	public ArrayList<Chat> getTeamsChats()
	{
		return teamsChats;
	}

	public void setTeamsChats(ArrayList<Chat> teamsChats)
	{
		this.teamsChats = teamsChats;
	}

	public Chat getLobbyChat()
	{
		return lobbyChat;
	}

	public void setLobbyChat(Chat lobbyChat)
	{
		this.lobbyChat = lobbyChat;
	}

	public Chat getGameChat()
	{
		return gameChat;
	}

	public void setGameChat(Chat gameChat)
	{
		this.gameChat = gameChat;
	}

	public Chat getSpectatorChat()
	{
		return spectatorChat;
	}

	public void setSpectatorChat(Chat spectatorsChat)
	{
		this.spectatorChat = spectatorsChat;
	}

	public HashMap<Player, Integer> getNeedToRespawn()
	{
		return needToRespawn;
	}

	public void setNeedToRespawn(HashMap<Player, Integer> needToRespawn)
	{
		this.needToRespawn = needToRespawn;
	}

	public HashMap<String, Object> getPlayersStats()
	{
		return playersStats;
	}

	public void setPlayersStats(HashMap<String, Object> playersStats)
	{
		this.playersStats = playersStats;
	}

	public HashMap<String, Object> getArenaStats()
	{
		return arenaStats;
	}

	public void setArenaStats(HashMap<String, Object> arenaStats)
	{
		this.arenaStats = arenaStats;
	}

	public Team getTeamByBedLocation(Location l)
	{
		for(Team team : teamList)
		{
			if (team == null) {continue;}
			if (team.getBedHeadLocation().equals(l) || team.getBedHeadLocation().equals(l))
			{return team;}
		}
		
		return null;
	}

	public void PlayerBreakEnemyBed(Player p, Team team)
	{
		if (team.equals(getPlayerTeam(p))) {return;}
		
		team.setHasBed(false);
		team.setCanRespawn(false);
		
		String msg = "&c&l[Уничтожение] &e&lКровать команда "+team.getColor()+team.getName()+" &e&lсломал игрок "+getPlayerTeam(p).getColor()+p.getName();
		gameChat.SendSound(Sound.ENTITY_ENDERDRAGON_HURT);
		gameChat.SendSystemMessage(msg);
	}

	public Boolean getIsEnding()
	{
		return isEnding;
	}

	public void setIsEnding(Boolean isEnding)
	{
		this.isEnding = isEnding;
	}

	public HashMap<String, Object> getUpgradesProgress()
	{
		return upgradesProgress;
	}

	public void setUpgradesProgress(HashMap<String, Object> upgradesProgress)
	{
		this.upgradesProgress = upgradesProgress;
	}

	public ArrayList<Chest> getOpennedChests()
	{
		return opennedChests;
	}

	public void setOpennedChests(ArrayList<Chest> opennedChests)
	{
		this.opennedChests = opennedChests;
	}

	public void setStartedMessage(List<String> list)
	{
		if (list == null) {return;}
		startMessage = list;
	}
	
	public List<String> getStartedMessage()
	{
		return startMessage;
	}

	public List<String> getEndMessage()
	{
		return endMessage;
	}

	public void setEndMessage(List<String> endMessage)
	{
		this.endMessage = endMessage;
	}

	public ArrayList<Team> getTeamList()
	{
		return teamList;
	}

	public void setTeamList(ArrayList<Team> teamList)
	{
		this.teamList = teamList;
	}

	public boolean isTeamExist(String name)
	{
		for(Team team : teamList)
		{
			if (team.getID().equals(name)) {return true;}
		}
		
		return false;
	}

	public int addTeam(String teamName, Integer maxPlCount, String color)
	{
		Team team = new Team(teamName, maxPlCount);
		team.setColor(color);
		teamList.add(team);
		return 0;
	}

	public Team getTeamByID(String teamName)
	{
		for(Team team : teamList)
		{
			if (team.getID().equals(name)) {return team;}
		}
		
		return null;
	}

	public boolean hasBed(Player p)
	{
		Team team = getPlayerTeam(p);
		if (team == null) {return false;}
		return team.isHasBed();
	}

	public Chat getPlayerTeamChat(Player p)
	{
		Team team = getPlayerTeam(p);
		if (team == null) {return null;}
		return team.getChat();
	}
}
