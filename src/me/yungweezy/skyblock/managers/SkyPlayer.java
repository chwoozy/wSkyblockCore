package me.yungweezy.skyblock.managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

import me.yungweezy.skyblock.main.Main;

public class SkyPlayer {

	private String main = null;
	
	public String I1 = null;
	public String I2 = null;
	public String I3 = null;
	public String I4 = null;
	public String I5 = null;
	public String I6 = null;
	public String I7 = null;
	public String I8 = null;
	
	public ArrayList<Location> b = new ArrayList<Location>();
	
	private HashMap<Integer, String> invites = new HashMap<Integer, String>();
	
	private HashMap<String, String> removedPerms = new HashMap<String, String>();
	
	public void xSetPermString(String ID, String s){
		removedPerms.put(ID, s);
	}
	
	public boolean hasRemovedPerm(String ID, String perm){
		String removed = getAllRemovedPerms(ID);
		if (removed.contains(perm)){
			return true; // so if player CANT DO WHAT PERM DESCRIBES
		} else {
			return false;
		}
	}
	
	public String getAllRemovedPerms(String ID){
		if (removedPerms.isEmpty() || removedPerms.get(ID) == null){
			return "";
		}
		
		return removedPerms.get(ID);
	}
	
	public void addPerm(String ID, String perm){
		if (removedPerms.isEmpty() || removedPerms.get(ID) == null){
			return;
		}
		
		String old = removedPerms.get(ID);
		if (!old.contains(perm)){
			return;
		}
		
		if (old.equals(perm + ";")){
			removedPerms.put(ID, null);
			return;
		}
		
		old = old.replace(perm + ";", "");
		removedPerms.put(ID, old);
	}
	
	public void removePerm(String ID, String perm){
		if (removedPerms.isEmpty() || removedPerms.get(ID) == null){
			removedPerms.put(ID, perm + ";");
		} else {
			String old = removedPerms.get(ID);
			if (old.contains(perm)){
				return;
			}
			
			String news = old + perm + ";";
			removedPerms.put(ID, news);
		}
	}
	
	public String getMain(){
		return main;
	}
	
	public void setMain(String m){
		main = m;
	}
	
	public String getID(int i){
		switch (i){
		case 1: 
			return I1;
		case 2: 
			return I2;
		case 3: 
			return I3;
		case 4: 
			return I4;
		case 5: 
			return I5;
		case 6: 
			return I6;
		case 7: 
			return I7;
		case 8: 
			return I8;
		}
		
		return null;
	}
	
	public void removeInvitation(String ID){
		if (invites == null){
			invites = new HashMap<Integer, String>();
		}
		
		for (int i = 1; i <= 4; i = i + 1){
			if (invites.get(i) != null){
				if (invites.get(i).equals(ID)){
					invites.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean hasInvites(){
		if (invites == null){
			invites  = new HashMap<Integer, String>();
		}
		
		if (invites.isEmpty()){
			return false;
		}
		
		return true;
	}
	
	public String getInvitation(int i){
		if (invites == null){
			invites  = new HashMap<Integer, String>();
		}
		
		return invites.get(i);
	}
	
	public boolean addInvitation(String ID){
		if (invites == null){
			invites  = new HashMap<Integer, String>();
		}
		
		if (invites.containsValue(ID)){
			if (me.yungweezy.skyblock.main.Main.debugMode){
				System.out.println("[Debug] Couldnt invite player, already has an invite of this ID (1)");
			}
			return false;
		}
		
		for (int i = 4; i <= 8; i = i + 1){
			if (getID(i) != null){
				if (getID(i).equals(ID)){
					if (me.yungweezy.skyblock.main.Main.debugMode){
						System.out.println("[Debug] Couldnt invite player, already has an invite of this ID (2)");
					}
					
					return false;
				}
			}
		}
		
		for (int i = 1; i <= 4; i = i + 1){
			if (getID(i + 4) == null){
				if (invites.get(i) == null){
					if (me.yungweezy.skyblock.main.Main.debugMode){
						System.out.println("[Debug] Invited, as nr " + i);
					}
					
					invites.put(i, ID);
					return true;
				}
			}
		}
		
		if (me.yungweezy.skyblock.main.Main.debugMode){
			System.out.println("[Debug] Couldnt invite player, end of block");
		}
		
		return false;
	}
	
	public boolean addInvitation(String ID, int nr){
		if (invites == null){
			invites  = new HashMap<Integer, String>();
		}
		
		if (invites.containsValue(ID)){
			if (me.yungweezy.skyblock.main.Main.debugMode){
				System.out.println("[Debug] couldnt hard add inv " + ID + ", already an invite with the same ID");
			}
			return false;
		}
		
		
		if (getID(nr + 4) != null){
			if (getID(nr + 4).equals(ID)){
				if (me.yungweezy.skyblock.main.Main.debugMode){
					System.out.println("[Debug] couldnt hard add inv " + ID + ", already an island at the same NR & ID");
				}
				return false;
			}
		}
		
		if (me.yungweezy.skyblock.main.Main.debugMode){
			System.out.println("[Debug] Before final block " + nr);
		}

		if (getID(nr + 4) == null){
			if (invites.get(nr) == null){
				invites.put(nr, ID);
				if (me.yungweezy.skyblock.main.Main.debugMode){
					System.out.println("[Debug] Put it in invites " + nr);
				}
				return true;
			}
		}

		return false;
	}
	
	@Deprecated
	public SkyBlock getp1(){
		return Main.blockManager.skyBlocksByID.get(I5);
	}
	
	@Deprecated
	public SkyBlock getp2(){
		return Main.blockManager.skyBlocksByID.get(I6);
	}
	
	@Deprecated
	public SkyBlock getp3(){
		return Main.blockManager.skyBlocksByID.get(I7);
	}
	
	@Deprecated
	public SkyBlock getp4(){
		return Main.blockManager.skyBlocksByID.get(I8);
	}
	
	@Deprecated
	public void setp1(SkyBlock block){
		if (block == null){
			//p1 = null;
			I5 = null;
			return;
		}
		
		//p1 = block;
		I5 = block.getID();
	}
	
	@Deprecated
	public void setp2(SkyBlock block){
		if (block == null){
			//p2 = null;
			I6 = null;
			return;
		}
		
		//p2 = block;
		I6 = block.getID();
	}
	
	@Deprecated
	public void setp3(SkyBlock block){
		if (block == null){
			//p3 = null;
			I7 = null;
			return;
		}
		
		//p3 = block;
		I7 = block.getID();
	}
	
	@Deprecated
	public void setp4(SkyBlock block){
		if (block == null){
			//p4 = null;
			I8 = null;
			return;
		}
		
		//p4 = block;
		I8 = block.getID();
	}
	
	@Deprecated
	public SkyBlock getFirst(){
		return Main.blockManager.skyBlocksByID.get(I1);
	}
	
	@Deprecated
	public SkyBlock getSecond(){
		return Main.blockManager.skyBlocksByID.get(I2);
	}
	
	@Deprecated
	public SkyBlock getThird(){
		return Main.blockManager.skyBlocksByID.get(I3);
	}
	
	@Deprecated
	public SkyBlock getFourth(){
		return Main.blockManager.skyBlocksByID.get(I4);
	}
	
	@Deprecated
	public void setFirst(SkyBlock block){
		if (block == null){
			//first = null;
			I1 = null;
			return;
		}
		
		//first = block;
		I1 = block.getID();
	}
	
	@Deprecated
	public void setSecond(SkyBlock block){
		if (block == null){
			//second = null;
			I2 = null;
			return;
		}
		
		//second = block;
		I2 = block.getID();
	}
	
	@Deprecated
	public void setThird(SkyBlock block){
		if (block == null){
			//third = null;
			I3 = null;
			return;
		}
		
		//third = block;
		I3 = block.getID();
	}
	
	@Deprecated
	public void setFourth(SkyBlock block){
		if (block == null){
			//fourth = null;
			I4 = null;
			return;
		}
		
		//fourth = block;
		I4 = block.getID();
	}
}
