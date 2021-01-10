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

public class PlayerMySQL {

	public static Connection con;
	public static Statement state;
	public static ResultSet rs;
	private static String pass;
	private static String user;
	private static String database;
	
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
	
	public static boolean attemptConnect(String username, String password){
		//pass = password;
		//user = username;
		return false;
	}
	
	public static void testDatabase(){
		boolean exists = false;
		String quer = "SELECT * FROM PlayerData";
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
			String up = "CREATE TABLE PlayerData (uuid VARCHAR(255), ID1 VARCHAR(255), ID2 VARCHAR(255), ID3 VARCHAR(255), ID4 VARCHAR(255), IN1 VARCHAR(255), IN2 VARCHAR(255), IN3 VARCHAR(255), IN4 VARCHAR(255), P1 VARCHAR(255), P2 VARCHAR(255), P3 VARCHAR(255), P4 VARCHAR(255), MAIN VARCHAR(255), PRIMARY KEY (uuid))";
			try {
				state.executeUpdate(up);
			} catch (Exception e) {
				System.out.println("[SBMysql] Couldnt create table, disabling plugin");
				System.out.println(e);
				
				Main.pl.getPluginLoader().disablePlugin(Main.pl);
				return;
			}
			
			System.out.println("[SBMysql] Created table, adding dummy info");
			
			String addDummy = "insert into PlayerData (uuid, ID1, ID2, ID3, ID4, IN1, IN2, IN3, IN4, P1, P2, P3, P4, MAIN) VALUES ('DummyUUID', 'Dummy|UU|ID|OF|ISLAND', 'NULL', 'NULL', 'NULL', 'NULL', 'NULL', 'NULL', 'NULL', 'NULL', 'NULL', 'NULL', 'NULL', 'Dummy|UU|ID|OF|ISLAND')";
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
