package me.yungweezy.skyblock.managers;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkyBlock {

	private Location center;
	private ArrayList<UUID> members = new ArrayList<UUID>();
	private String name = "";
	private String ID;
	private UUID owner;
	private SkyBlockType type;
	private Material logo;
	private int sublogo;
	private Boolean lock = false;
	public Boolean PvP = false;
	public Boolean mobSpawning = true;
	public Boolean explosions = true;
	private Location home;
	private Location warp = null;
	private int maxMembers = 2;
	double level = 0;
	String specialJoin;
	
	public String getSpecialJoin(){
		if (specialJoin == null){
			return "";
		}
		
		return specialJoin;
	}
	
	public void setSpecialJoin(String msg){
		specialJoin = msg;
	}
	
	public int getMaxMembers(){
		return maxMembers;
	}
	
	public void setMaxMembers(int amount){
		maxMembers = amount;
	}
	
	public void setWarp(Location newLoc){
		warp = newLoc;
	}
	
	public Location getWarp(){
		return warp;
	}
	
	public Double getLevel(){
		return level;
	}
	
	public void addToLevel(double add){
		level = level + add;
	}
	
	public void removeFromLevel(double remove){
		level = level - remove;
	}
	
	public Location getHome(){
		if (home == null){
			return center;
		}
		
		return home;
	}
	
	public void setHome(Location loc){
		home = loc;
	}
	
	public boolean doExplode(){
		return explosions;
	}
	
	public void setExplosions(Boolean explode){
		explosions = explode;
	}
	
	public boolean spawnMobs(){
		return mobSpawning;
	}
	
	public void setMobSpawning(Boolean spawning){
		mobSpawning = spawning;
	}
	
	public boolean pvpEnabled(){
		return PvP;
	}
	
	public void setPvp(Boolean pvp){
		PvP = pvp;
	}
	
	public boolean isLocked(){
		return lock;
	}
	
	public void setLock(Boolean l){
		lock = l;
	}
	
	public void setSublogo(int sub){
		sublogo = sub;
	}
	
	public int getSublogo(){
		return sublogo;
	}
	
	public Material getLogo(){
		return logo;
	}
	
	public void setLogo(Material mat){
		logo = mat;
	}
	
	public SkyBlockType getType(){
		return type;
	}
	
	public void setType(SkyBlockType tupe){
		type = tupe;
	}
	
	public void setOwner(Player player){
		owner = player.getUniqueId();
	}
	
	public void setOwner(UUID u){
		owner = u;
	}
	
	public UUID getOwner(){
		return owner;
	}
	
	public void setID(String id){
		ID = id;
	}
	
	public String getID(){
		return ID;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String newname){
		name = newname;
	}
	
	public void removeMember(UUID u){
		members.remove(u);
	}
	
	public void removeMember(Player player){
		members.remove(player.getUniqueId());
	}
	
	public void setAllMembers(ArrayList<UUID> uuids){
		members = uuids;
	}
	
	public int totalMembers(){
		return members.size();
	}
	
	public ArrayList<UUID> getAllMembers(){
		return members;
	}
	
	public boolean isMember(Player player){
		return members.contains(player.getUniqueId());
	}
	
	public boolean isMember(UUID u){
		return members.contains(u);
	}
	
	public void addMember(UUID u){
		members.add(u);
	}
	
	public void addMember(Player player){
		members.add(player.getUniqueId());
	}
	
	public Location getCenter(){
		return center;
	}
	
	public void setCenter(Location loc){
		center = loc;
	}
}
