package me.yungweezy.skyblock.misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.bukkit.configuration.file.FileConfiguration;

import me.yungweezy.skyblock.main.Main;

public class SkyBlockMySQL {

	public static Connection con;
	public static Statement state;
	public static ResultSet rs;
	private static String pass;
	private static String user;
	private static String database;
	public static String table = "SkyBlockData2";
	
	public static boolean attemptConnect(){
		FileConfiguration config = Main.pl.getConfig();
		pass = config.getString("mysql.password");
		user = config.getString("mysql.username");
		database = config.getString("mysql.database");
		
		try {
			Properties cProps = new Properties();
			cProps.put("user", user);
			cProps.put("password", pass);
			cProps.put("autoReconnect", "true");
			cProps.put("maxReconnects", "4");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database, cProps);
			
			// jdbc:mysql://host:port/database , username , password
			// jdbc:mysql://host:port/database , java.util.Properties
			
			state = con.createStatement();
			
			testDatabase();
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	public static Connection getConnection(){
		if (con != null){
			return con;
		}
		
		try {
			Properties cProps = new Properties();
			cProps.put("user", user);
			cProps.put("password", pass);
			cProps.put("autoReconnect", "true");
			cProps.put("maxReconnects", "4");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database, cProps);
		} catch(Exception e) {
			System.out.println("[SBMysql] Something went wrong connecting!");
			System.out.println(e);
		}
		
		return con;
	}
	
	public static void testDatabase(){
		boolean exists = false;
		String quer = "SELECT * FROM " + table;
		try {
			rs = state.executeQuery(quer);
			if (rs.next()){
				System.out.println("[SBMysql] table exists");
				exists = true;
			} else {
				System.out.println("[SBMysql] table doesnt exist, creating it now!");
				exists = false;
			}
		} catch (Exception e){
			System.out.println("[SBMysql] Small error of table not existing, the following is fine!");
			System.out.println(e);
		}
		
		if (exists == false){
			String up = "CREATE TABLE " + table + " (ID VARCHAR(255), OWNER VARCHAR(255), NAME VARCHAR(255), CENTER VARCHAR(255), MEMBERS VARCHAR(5000), TYPE VARCHAR(255), LOGO VARCHAR(255), SUBLOGO INT NOT NULL DEFAULT '0', LEVEL DOUBLE NOT NULL DEFAULT '0.0', LOCKED INT NOT NULL DEFAULT '0', EXPLOSIONS INT NOT NULL DEFAULT '1', PVP INT NOT NULL DEFAULT '0', MOBSPAWNING INT NOT NULL DEFAULT '1', MAXMEMBERS INT NOT NULL DEFAULT '2', WARP VARCHAR(255), HOME VARCHAR(255), PRIMARY KEY (ID))";
			try {
				state.executeUpdate(up);
			} catch (Exception e) {
				System.out.println("[SBMysql] Couldnt create table, disabling plugin");
				System.out.println(e);
				
				Main.pl.getPluginLoader().disablePlugin(Main.pl);
				return;
			}
			
			System.out.println("[SBMysql] Created table, adding dummy info");
			
			String addDummy = "insert into " + table + " (ID, OWNER, NAME, CENTER, MEMBERS, TYPE, LOGO, SUBLOGO, LEVEL, LOCKED, EXPLOSIONS, PVP, MOBSPAWNING, MAXMEMBERS, WARP, HOME) VALUES ('DummySkyBlockID', 'Dummy|UUID|OF|OWNER', 'dummyIsland', '-1Q-1', 'fake|Id|One!Fake|id|two', 'FakeType', 'WOOD', '0', '0', '0', '1', '1', '1', '69', 'NULL', 'NULL')";
			try {
				PreparedStatement dummyState = getConnection().prepareStatement(addDummy);
				dummyState.executeUpdate();
				System.out.println("[SBMysql] Dummy added succesfully!");
			} catch (SQLException e1) {
				System.out.println("[SBMysql] Error while adding dummy");
				e1.printStackTrace();
			}
		}
	}
}
