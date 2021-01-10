package me.yungweezy.skyblock.misc;

import be.maximvdw.animatednames.api.PlaceholderAPI;
import be.maximvdw.animatednames.api.PlaceholderAPI.PlaceholderRequestEvent;
import be.maximvdw.animatednames.api.PlaceholderAPI.PlaceholderRequestEventHandler;
import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.managers.SkyBlock;
import me.yungweezy.skyblock.managers.SkyPlayer;
import me.yungweezy.skyblock.managers.SkyplayerManager;

public class AnimatedNamesHook {

	public static void initiate(){
		PlaceholderAPI.registerOfflinePlaceholder("ilvl", true,
				new PlaceholderRequestEventHandler() {

			@Override
			public String onPlaceholderRequest(PlaceholderRequestEvent event) {
				if (SkyplayerManager.SkyPlayers.get(event.getPlayer().getUniqueId()) == null){
					return "0";
				}

				SkyPlayer p = SkyplayerManager.SkyPlayers.get(event.getPlayer().getUniqueId());

				if (p.getMain() == null){
					return "0";
				}

				String mainid = p.getMain();
				SkyBlock main = Main.blockManager.skyBlocksByID.get(mainid);

				int lvl = (int) (main.getLevel() / IslandLevels.lvsperLevel);

				if (lvl < 0){
					lvl = 0;
				}

				return lvl + "";
			}
		});
	}

}
