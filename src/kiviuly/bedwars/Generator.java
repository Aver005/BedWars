package kiviuly.bedwars;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Generator
{
	private String ID = "";
	
	private String headerText = "&7Железный генератор";
	private String footerText = "&3Появление через # сек.";
	
	private Arena arena = null;
	private Team team = null;
	
	private int period = 20;
	private int count = 1;
	private int lvl = 1;
	
	private Location dropLocation = null;
	private Location blockLocation = null;
	private Location headerLocation = null;
	private Location footerLocation = null;
	
	private ArmorStand headBlock = null;
	private ArmorStand headerBlock = null;
	private ArmorStand footerBlock = null;
	
	private int nextSpawnTime = 0;
	
	private ItemStack dropItem = new ItemStack(Material.IRON_INGOT);
	private ItemStack blockItem = new ItemStack(Material.IRON_BLOCK);
	
	private boolean isTeam = false;
	private boolean isEnabled = true;
	private boolean rotating = true;
	private boolean showBlock = false;
	private boolean showHeader = false;
	private boolean showFooter = false;
	
	public Generator(String ID, ItemStack drop, int period, int count)
	{
		setID(ID);
		setDropItem(drop);
		setPeriod(period);
		setCount(count);
	}
	
	public boolean Create()
	{
		if (!isEnabled) {return false;}
		if (blockLocation == null) {return false;}
		if (headerLocation == null) {return false;}
		if (footerLocation == null) {return false;}
		
		headBlock = (ArmorStand) blockLocation.getWorld().spawnEntity(blockLocation, EntityType.ARMOR_STAND);
		headBlock.setMarker(true);
		headBlock.setVisible(false);
		headBlock.setGravity(false);
		headBlock.setCustomNameVisible(false);
		headBlock.setSilent(true);
		headBlock.setAI(false);
		headBlock.setInvulnerable(true);
		headBlock.setHelmet(blockItem);
		
		headerBlock = (ArmorStand) headerLocation.getWorld().spawnEntity(headerLocation, EntityType.ARMOR_STAND);
		headerBlock.setVisible(false);
		headerBlock.setGravity(false);
		headerBlock.setCustomNameVisible(true);
		headerBlock.setSilent(true);
		headerBlock.setAI(false);
		headerBlock.setInvulnerable(true);
		headerBlock.setCustomName(headerText);
		
		footerBlock = (ArmorStand) footerLocation.getWorld().spawnEntity(footerLocation, EntityType.ARMOR_STAND);
		footerBlock.setVisible(false);
		footerBlock.setGravity(false);
		footerBlock.setCustomNameVisible(true);
		footerBlock.setSilent(true);
		footerBlock.setAI(false);
		footerBlock.setInvulnerable(true);
		footerBlock.setCustomName(footerText);
		parseFooter(0);
		return true;
	}
	
	public void tick(int ticks)
	{
		Spawn(ticks);
		Rotate();
	}
	
	public void Rotate()
	{
		if (!isEnabled) {return;}
		
		Location l = blockLocation;
		World w = l.getWorld();

		double x = l.getX();
		double y = l.getY();
		double z = l.getZ();
		float pitch = l.getPitch();
		float yaw = l.clone().getYaw() + 5.0F;
		Location location = new Location(w, x, y, z, yaw, pitch);
		headBlock.teleport(location);
	}
	
	public boolean Spawn(int ticks)
	{
		if (!isEnabled) {return false;}
		if (ticks % period != 0) {return false;}
		ForceSpawn(ticks);
		return true;
	}
	
	public void ForceSpawn(int ticks)
	{
		dropLocation.getWorld().dropItemNaturally(dropLocation, dropItem);
		nextSpawnTime = ticks + period;
		parseFooter(ticks);
	}
	
	public void parseFooter(int ticks)
	{
		footerText = footerText.replace("#", nextSpawnTime-ticks+"");
		footerBlock.setCustomName(footerText);
	}
	
	public String getID()
	{
		return ID;
	}
	public void setID(String iD)
	{
		ID = iD;
	}
	public String getHeaderText()
	{
		return headerText;
	}
	public void setHeaderText(String headerText)
	{
		this.headerText = headerText;
	}
	public String getFooterText()
	{
		return footerText;
	}
	public void setFooterText(String footerText)
	{
		this.footerText = footerText;
	}
	public Arena getArena()
	{
		return arena;
	}
	public void setArena(Arena arena)
	{
		this.arena = arena;
	}
	public Team getTeam()
	{
		return team;
	}
	public void setTeam(Team team)
	{
		this.team = team;
	}
	public int getPeriod()
	{
		return period;
	}
	public void setPeriod(int period)
	{
		this.period = period;
	}
	public int getCount()
	{
		return count;
	}
	public void setCount(int count)
	{
		this.count = count;
	}
	public int getLvl()
	{
		return lvl;
	}
	public void setLvl(int lvl)
	{
		this.lvl = lvl;
	}
	public Location getDropLocation()
	{
		return dropLocation;
	}
	public void setDropLocation(Location dropLocation)
	{
		this.dropLocation = dropLocation;
	}
	public Location getBlockLocation()
	{
		return blockLocation;
	}
	public void setBlockLocation(Location blockLocation)
	{
		this.blockLocation = blockLocation;
	}
	public Location getHeaderLocation()
	{
		return headerLocation;
	}
	public void setHeaderLocation(Location headerLocation)
	{
		this.headerLocation = headerLocation;
	}
	public Location getFooterLocation()
	{
		return footerLocation;
	}
	public void setFooterLocation(Location footerLocation)
	{
		this.footerLocation = footerLocation;
	}
	public int getNextSpawnTime()
	{
		return nextSpawnTime;
	}
	public void setNextSpawnTime(int nextSpawnTime)
	{
		this.nextSpawnTime = nextSpawnTime;
	}
	public ItemStack getDropItem()
	{
		return dropItem;
	}
	public void setDropItem(ItemStack dropItem)
	{
		this.dropItem = dropItem;
	}
	public ItemStack getBlockItem()
	{
		return blockItem;
	}
	public void setBlockItem(ItemStack blockItem)
	{
		this.blockItem = blockItem;
	}
	public boolean isTeam()
	{
		return isTeam;
	}
	public void setTeam(boolean isTeam)
	{
		this.isTeam = isTeam;
	}
	public boolean isShowBlock()
	{
		return showBlock;
	}
	public void setShowBlock(boolean showBlock)
	{
		this.showBlock = showBlock;
	}
	public boolean isShowHeader()
	{
		return showHeader;
	}
	public void setShowHeader(boolean showHeader)
	{
		this.showHeader = showHeader;
	}
	public boolean isShowFooter()
	{
		return showFooter;
	}
	public void setShowFooter(boolean showFooter)
	{
		this.showFooter = showFooter;
	}
	public boolean isRotating()
	{
		return rotating;
	}
	public void setRotating(boolean rotating)
	{
		this.rotating = rotating;
	}
	public boolean isEnabled()
	{
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled)
	{
		this.isEnabled = isEnabled;
	}

	public ArmorStand getHeadBlock()
	{
		return headBlock;
	}

	public void setHeadBlock(ArmorStand headBlock)
	{
		this.headBlock = headBlock;
	}

	public ArmorStand getHeaderBlock()
	{
		return headerBlock;
	}

	public void setHeaderBlock(ArmorStand headerBlock)
	{
		this.headerBlock = headerBlock;
	}

	public ArmorStand getFooterBlock()
	{
		return footerBlock;
	}

	public void setFooterBlock(ArmorStand footerBlock)
	{
		this.footerBlock = footerBlock;
	}
}
