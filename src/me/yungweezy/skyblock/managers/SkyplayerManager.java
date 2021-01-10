package me.yungweezy.skyblock.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.misc.PlayerMySQL;

public class SkyplayerManager implements Listener {

	public static HashMap<UUID, SkyPlayer> SkyPlayers = new HashMap<UUID, SkyPlayer>();
	public static HashMap<UUID, ArrayList<Location>> CanBuildAt = new HashMap<UUID, ArrayList<Location>>();
	
	public static void loadForPlayer(OfflinePlayer player) {
		System.out.println("[SBMysql] Loading playerdata from MySQL database! " + System.currentTimeMillis());
		
		if (Main.debugMode){
			if (player.isOnline()){
				Player pl = (Player) player;
				pl.sendMessage("[Debug] Your data is being loaded!");
			}
		}
		
		if (!player.hasPlayedBefore()){
			loadEmptyForPlayer(player);
			return;
		}
		
		boolean exists = false;
		String quer = "SELECT * FROM PlayerData where uuid=" + "'" + player.getUniqueId().toString().replace("-", "|") + "'"; 
		
		Connection con = PlayerMySQL.getConnection();
		Statement state = null;
		
		try {
			state = con.createStatement();
		} catch (SQLException e1) {
			System.out.println("[SBMysql] Couldnt create statement! (E 472)");
			e1.printStackTrace();
			exists = false;
		}
		
		ResultSet rs = null;
		
		try {
			rs = state.executeQuery(quer);
			if (rs.next()){
				exists = true;
			} else {
				System.out.println("[SBMysql] Player doesnt exist in table! Loading empty user!");
				exists = false;
			}
		} catch (Exception e){
			System.out.println(e);
			return;
		}
		
		if (!exists){
			loadEmptyForPlayer(player);
			return;
		}
		
		SkyPlayer p = new SkyPlayer();
		
		int maxMembers = SBUtils.getMaxMemberPerm(player);
		
		try {
			String ID1 = rs.getString("ID1").replace("|", "-");
			String ID2 = rs.getString("ID2").replace("|", "-");
			String ID3 = rs.getString("ID3").replace("|", "-");
			String ID4 = rs.getString("ID4").replace("|", "-");
			
			p = doOwnIslands(p, 1, ID1, maxMembers);
			p = doOwnIslands(p, 2, ID2, maxMembers);
			p = doOwnIslands(p, 3, ID3, maxMembers);
			p = doOwnIslands(p, 4, ID4, maxMembers);
			
			String P1 = rs.getString("P1").replace("|", "-");
			String P2 = rs.getString("P2").replace("|", "-");
			String P3 = rs.getString("P3").replace("|", "-");
			String P4 = rs.getString("P4").replace("|", "-");
			
			p = doPartyIslands(p, 1, P1);
			p = doPartyIslands(p, 2, P2);
			p = doPartyIslands(p, 3, P3);
			p = doPartyIslands(p, 4, P4);
			
			String IN1 = rs.getString("IN1");
			String IN2 = rs.getString("IN2");
			String IN3 = rs.getString("IN3");
			String IN4 = rs.getString("IN4");
			
			if (Main.debugMode){
				System.out.println("[Debug] Received IN4 from DB: " + IN4);
			}
			
			p = doPartyInvites(p, 1, IN1);
			p = doPartyInvites(p, 2, IN2);
			p = doPartyInvites(p, 3, IN3);
			p = doPartyInvites(p, 4, IN4);
			
			if (Main.debugMode){
				System.out.println("[Debug] Invite 4 before setting main: " + p.getInvitation(4));
			}
			
			String main = rs.getString("MAIN");
			if (!main.equals("NULL")){
				p.setMain(main.replace("|", "-"));
			}
			
			if (Main.debugMode){
				System.out.println("[Debug] Invite 4 after setting main: " + p.getInvitation(4));
			}
		} catch (SQLException e) {
			System.out.println("[SBMysql] Error while loading from mysql, giving empty new user!");
			e.printStackTrace();
			loadEmptyForPlayer(player);
			return;
		}
		
		CanBuildAt.put(player.getUniqueId(), p.b);
		SkyPlayers.put(player.getUniqueId(), p);
		System.out.println("[SBMysql] Completely loaded from MySQL! (" + player.getName() + ") " + System.currentTimeMillis());
		
		if (Main.debugMode){
			if (player.isOnline()){
				Player pl = (Player) player;
				pl.sendMessage("[Debug] Finished loading from MySQL");
				pl.sendMessage("[Debug] ID (5): " + p.getID(5));
				pl.sendMessage("[Debug] ID (6): " + p.getID(6));
				pl.sendMessage("[Debug] ID (7): " + p.getID(7));
				pl.sendMessage("[Debug] ID (8): " + p.getID(8));
				
				String debugInvs = "";
				for (int i = 1; i <= 8; i = i + 1){
					debugInvs = debugInvs + "| I: " + i + " " + p.getInvitation(i) + " ";
				}
				pl.sendMessage("[Debug] Inv1|8: " + debugInvs);
			}
		}
	}

	public static void loadEmptyForPlayer(OfflinePlayer player){
		SkyPlayer p = new SkyPlayer();
		
		ArrayList<Location> b = new ArrayList<Location>();
		
		p.b = b;
		CanBuildAt.put(player.getUniqueId(), b);
		SkyPlayers.put(player.getUniqueId(), p);
	}
	
	public static SkyPlayer doPartyInvites(SkyPlayer sp, int nr, String rawID){
		if (Main.debugMode){
			System.out.println("doPartyInvites with rawID " + rawID + " and NR " + nr);
		}
		
		if (rawID.equals("NULL")){
			return sp;
		}
		
		String id = rawID.replace("|", "-");
		sp.addInvitation(id, nr);
		return sp;
	}
	
	public static SkyPlayer doPartyIslands(SkyPlayer sp, int nr, String rawID){
		if (rawID.equals("NULL")){
			return sp;
		}
		
		String ID = rawID;
		String remperms = null;
		
		if (rawID.contains(",")){
			String[] split = rawID.split(",");
			remperms = split[1];
			ID = split[0];
		}
		
		if (nr == 1){
			sp.I5 = ID;
			//sp.setp1(Main.blockManager.skyBlocksByID.get(ID));
		} else if (nr == 2){
			sp.I6 = ID;
			//sp.setp2(Main.blockManager.skyBlocksByID.get(ID));
		} else if (nr == 3){
			sp.I7 = ID;
			//sp.setp3(Main.blockManager.skyBlocksByID.get(ID));
		} else if (nr == 4){
			sp.I8 = ID;
			//sp.setp4(Main.blockManager.skyBlocksByID.get(ID));
		}
		
		sp.b.add(Main.blockManager.skyBlocksByID.get(ID).getCenter());
		sp.xSetPermString(ID, remperms);
		
		return sp;
	}
	
	public static SkyPlayer doOwnIslands(SkyPlayer sp, int nr, String ID, int maxmembers){
		if (ID.equals("NULL")){
			return sp;
		}
		
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		block.setMaxMembers(maxmembers);
		
		sp.b.add(block.getCenter());
		
		if (nr == 1){
			sp.I1 = block.getID();
			//sp.setFirst(block);
		} else if (nr == 2){
			sp.I2 = block.getID();
			//sp.setSecond(block);
		} else if (nr == 3){
			sp.I3 = block.getID();
			//sp.setThird(block);
		} else if (nr == 4){
			sp.I4 = block.getID();
			//sp.setFourth(block);
		}
		
		return sp;
	}
	
	public static void saveForPlayer(OfflinePlayer player){
		System.out.println("[SBMysql] Starting to save data for " + player.getName() + " " + System.currentTimeMillis());
		
		if (Main.debugMode){
			if (player.isOnline()){
				Player pl = (Player) player;
				pl.sendMessage("[Debug] Your data is being saved to MySQL!");
			}
		}
		
		Connection con = PlayerMySQL.getConnection();

		String saveQuery = "insert into PlayerData (uuid, ID1, ID2, ID3, ID4, IN1, IN2, IN3, IN4, P1, P2, P3, P4, MAIN) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			PreparedStatement checkstate = con.prepareStatement("SELECT * FROM PlayerData where uuid=" + "'" + player.getUniqueId().toString().replace("-", "|") + "'");
			ResultSet result = checkstate.executeQuery();
			
			if (result.next()){
				saveQuery = saveQuery.replace("insert", "replace");
			}
		} catch (Exception e){
			System.out.println("[SBMysql] Something went wrong while checking if having to update or insert");
			e.printStackTrace();
			return;
		}
		
		//System.out.println("Query: " + saveQuery);
		SkyPlayer sp = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		
		try {
			PreparedStatement saveState = PlayerMySQL.getConnection().prepareStatement(saveQuery);
			saveState.setString(1, player.getUniqueId().toString().replace("-", "|"));
			
			// OWN ID's
			for (int id = 1; id <= 4; id = id + 1){
				if (sp.getID(id) != null){
					saveState.setString(id + 1, sp.getID(id).replace("-", "|"));
				} else {
					saveState.setString(id + 1, "NULL");
				}
			}
			
			// Invites
			for (int i = 1; i <= 4; i = i + 1){
				if (sp.getInvitation(i) != null){
					saveState.setString(4 + i + 1, sp.getInvitation(i).replace("-", "|"));
					if (Main.debugMode){
						System.out.println("[Debug] Set IN" + i + " (slot " + (4+i+1) + ") as" + sp.getInvitation(i));
					}
				} else {
					saveState.setString(4 + i + 1, "NULL");
					if (Main.debugMode){
						System.out.println("[Debug] Set IN" + i + " (slot " + (4+i+1) + ") as NULL");
					}
				}
			}
			
			// Parties
			for (int i = 5; i <= 8; i = i + 1){
				if (sp.getID(i) != null){
					saveState.setString(4 + i + 1, sp.getID(i).replace("-", "|"));
					
					if (!sp.getAllRemovedPerms(sp.getID(i)).equals("")){
						saveState.setString(4 + i + 1, sp.getID(i).replace("-", "|") + "," + sp.getAllRemovedPerms(sp.getID(i)));
					}
				} else {
					saveState.setString(4 + i + 1, "NULL");
				}
			}
			
			// Main
			if (sp.getMain() != null){
				saveState.setString(14, sp.getMain().replace("-", "|"));
			} else {
				if (sp.I1 != null){
					saveState.setString(14, sp.I1.replace("-", "|"));
				} else {
					saveState.setString(14, "NULL");
				}
			}
			
			saveState.executeUpdate();
			System.out.println("[SBMysql] Succesfully saved data for " + player.getName() + " " + System.currentTimeMillis());
			
			if (Main.debugMode){
				if (player.isOnline()){
					Player pl = (Player) player;
					pl.sendMessage("[Debug] Your data has been saved to MySQL!");
				}
			}
		} catch (SQLException e1) {
			System.out.println("[SBMysql] Error while saving data for " + player.getName());
			e1.printStackTrace();
			return;
		}
	}
	
	@EventHandler
	public void onLogout(PlayerQuitEvent event){
		saveForPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event){
		loadForPlayer(event.getPlayer());
	}
}
