package kiviuly.bedwars;

import org.bukkit.OfflinePlayer;

public class User 
{
	private String ID = "";
	private OfflinePlayer offlinePlayer = null;
	
	private Arena arena = null;
	
	private boolean isPlaying = false;
	private boolean isWaiting = false;
	private boolean isSpectating = false;
	
	public User(String ID, OfflinePlayer offp)
	{
		this.ID = ID;
		this.offlinePlayer = offp;
	}
	
	public String getID() {return ID;}
	public void setID(String iD) {ID = iD;}
	public OfflinePlayer getOfflinePlayer() {return offlinePlayer;}
	public void setOfflinePlayer(OfflinePlayer offlinePlayer) {this.offlinePlayer = offlinePlayer;}
	public Arena getArena() {return arena;}
	public void setArena(Arena arena) {this.arena = arena;}
	public boolean isPlaying() {return isPlaying;}
	public void setPlaying(boolean isPlaying) {this.isPlaying = isPlaying;}
	public boolean isWaiting() {return isWaiting;}
	public void setWaiting(boolean isWaiting) {this.isWaiting = isWaiting;}
	public boolean isSpectating() {return isSpectating;}
	public void setSpectating(boolean isSpectating) {this.isSpectating = isSpectating;}
}
