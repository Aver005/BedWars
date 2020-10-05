package kiviuly.bedwars;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Team
{
	private String ID = "";
	private String name = "";
	private String color = "&f";
	
	private int minPlayers = 1;
	private int maxPlayers = 1;
	
	private int ironSpawnPeriod = 20;
	private int goldSpawnPeriod = 20;
	private int emeraldSpawnPeriod = 20;
	
	private int ironSpawnCount = 1;
	private int goldSpawnCount = 1;
	private int emeraldSpawnCount = 1;
	
	private boolean dropIron = true;
	private boolean dropGold = true;
	private boolean dropEmerald = false;
	
	private boolean enchantedArmor = false; // Прокачка "Улучшить броню"
	private boolean enchantedGuns = false; // Прокачка "Улучшить оружие"
	private boolean enchantedTools = false; // Прокачка "Улучшить инструменты"
	private boolean healingIsland = false; // Прокачка "Родной остров"
	
	private boolean hasBed = false;
	private boolean canRespawn = false;
	
	private Location bedHeadLocation = null;
	private Location bedFeedLocation = null;
	private Location spawnLocation = null;
	private Location ironGeneratorLocation = null;
	private Location goldGeneratorLocation = null;
	private Location emeraldGeneratorLocation = null;
	
	private ArrayList<Player> livePlayers = new ArrayList<>();
	private ArrayList<Player> deadPlayers = new ArrayList<>();
	
	private Chat chat = null;
	
	private ArrayList<Generator> generators = new ArrayList<>();
	
	public Team(String ID, int maxPlayers)
	{
		setID(ID);
		setMaxPlayers(maxPlayers);
		setName(ID);
	}
	
	public void addPlayer(Player p)
	{
		if (livePlayers.contains(p)) {return;}
		livePlayers.add(p);
	}
	
	public void addDeadPlayer(Player p)
	{
		if (!livePlayers.contains(p)) {return;}
		if (deadPlayers.contains(p)) {return;}
		deadPlayers.add(p);
	}
	
	
	public String getID()
	{
		return ID;
	}
	public void setID(String iD)
	{
		ID = iD;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getColor()
	{
		return color;
	}
	public void setColor(String color)
	{
		this.color = color;
	}
	public int getMinPlayers()
	{
		return minPlayers;
	}
	public void setMinPlayers(int minPlayers)
	{
		this.minPlayers = minPlayers;
	}
	public int getMaxPlayers()
	{
		return maxPlayers;
	}
	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}
	public int getIronSpawnPeriod()
	{
		return ironSpawnPeriod;
	}
	public void setIronSpawnPeriod(int ironSpawnPeriod)
	{
		this.ironSpawnPeriod = ironSpawnPeriod;
	}
	public int getGoldSpawnPeriod()
	{
		return goldSpawnPeriod;
	}
	public void setGoldSpawnPeriod(int goldSpawnPeriod)
	{
		this.goldSpawnPeriod = goldSpawnPeriod;
	}
	public int getIronSpawnCount()
	{
		return ironSpawnCount;
	}
	public void setIronSpawnCount(int ironSpawnCount)
	{
		this.ironSpawnCount = ironSpawnCount;
	}
	public int getGoldSpawnCount()
	{
		return goldSpawnCount;
	}
	public void setGoldSpawnCount(int goldSpawnCount)
	{
		this.goldSpawnCount = goldSpawnCount;
	}
	public boolean isDropIron()
	{
		return dropIron;
	}
	public void setDropIron(boolean dropIron)
	{
		this.dropIron = dropIron;
	}
	public boolean isDropGold()
	{
		return dropGold;
	}
	public void setDropGold(boolean dropGold)
	{
		this.dropGold = dropGold;
	}
	public boolean isDropEmerald()
	{
		return dropEmerald;
	}
	public void setDropEmerald(boolean dropEmerald)
	{
		this.dropEmerald = dropEmerald;
	}
	public boolean isEnchantedArmor()
	{
		return enchantedArmor;
	}
	public void setEnchantedArmor(boolean enchantedArmor)
	{
		this.enchantedArmor = enchantedArmor;
	}
	public boolean isEnchantedGuns()
	{
		return enchantedGuns;
	}
	public void setEnchantedGuns(boolean enchantedGuns)
	{
		this.enchantedGuns = enchantedGuns;
	}
	public boolean isEnchantedTools()
	{
		return enchantedTools;
	}
	public void setEnchantedTools(boolean enchantedTools)
	{
		this.enchantedTools = enchantedTools;
	}
	public boolean isHealingIsland()
	{
		return healingIsland;
	}
	public void setHealingIsland(boolean healingIsland)
	{
		this.healingIsland = healingIsland;
	}
	public boolean isHasBed()
	{
		return hasBed;
	}
	public void setHasBed(boolean hasBed)
	{
		this.hasBed = hasBed;
	}
	public boolean isCanRespawn()
	{
		return canRespawn;
	}
	public void setCanRespawn(boolean canRespawn)
	{
		this.canRespawn = canRespawn;
	}
	public Location getIronGeneratorLocation()
	{
		return ironGeneratorLocation;
	}
	public void setIronGeneratorLocation(Location ironGeneratorLocation)
	{
		this.ironGeneratorLocation = ironGeneratorLocation;
	}
	public Location getSpawnLocation()
	{
		return spawnLocation;
	}
	public void setSpawnLocation(Location spawnLocation)
	{
		this.spawnLocation = spawnLocation;
	}
	public Location getGoldGeneratorLocation()
	{
		return goldGeneratorLocation;
	}
	public void setGoldGeneratorLocation(Location goldGeneratorLocation)
	{
		this.goldGeneratorLocation = goldGeneratorLocation;
	}
	public ArrayList<Player> getLivePlayers()
	{
		return livePlayers;
	}
	public void setLivePlayers(ArrayList<Player> livePlayers)
	{
		this.livePlayers = livePlayers;
	}
	public ArrayList<Player> getDeadPlayers()
	{
		return deadPlayers;
	}
	public void setDeadPlayers(ArrayList<Player> deadPlayers)
	{
		this.deadPlayers = deadPlayers;
	}
	public void setChat(Chat chat)
	{
		this.chat = chat;
	}
	public Chat getChat()
	{
		return chat;
	}

	public Location getBedHeadLocation()
	{
		return bedHeadLocation;
	}

	public void setBedHeadLocation(Location bedHeadLocation)
	{
		this.bedHeadLocation = bedHeadLocation;
	}

	public Location getBedFeedLocation()
	{
		return bedFeedLocation;
	}

	public void setBedFeedLocation(Location bedFeedLocation)
	{
		this.bedFeedLocation = bedFeedLocation;
	}

	public boolean hasIronGenerator()
	{
		return ironGeneratorLocation != null;
	}

	public Location getEmeraldGeneratorLocation()
	{
		return emeraldGeneratorLocation;
	}

	public void setEmeraldGeneratorLocation(Location emeraldGeneratorLocation)
	{
		this.emeraldGeneratorLocation = emeraldGeneratorLocation;
	}

	public int getEmeraldSpawnCount()
	{
		return emeraldSpawnCount;
	}

	public void setEmeraldSpawnCount(int emeraldSpawnCount)
	{
		this.emeraldSpawnCount = emeraldSpawnCount;
	}

	public int getEmeraldSpawnPeriod()
	{
		return emeraldSpawnPeriod;
	}

	public void setEmeraldSpawnPeriod(int emeraldSpawnPeriod)
	{
		this.emeraldSpawnPeriod = emeraldSpawnPeriod;
	}

	public void addGenerator(Generator g)
	{
		generators.add(g);
	}
	
	public ArrayList<Generator> getGenerators()
	{
		return generators;
	}
}