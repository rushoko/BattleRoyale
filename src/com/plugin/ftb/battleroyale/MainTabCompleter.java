package com.plugin.ftb.battleroyale;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.plugin.ftb.battleroyale.clubs.Club;
import com.plugin.ftb.battleroyale.clubs.ClubManager;

public class MainTabCompleter implements TabCompleter{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tab = new ArrayList<>();
		if (sender instanceof Player && args.length == 1) {
			if(label.equalsIgnoreCase("battleroyale")){
				tab.add("stageL");
				tab.add("stageR");
				tab.add("setLobbypoint");
				tab.add("setStartpoint");
				tab.add("setDeathpoint");
				tab.add("setMap");
				tab.add("setTimer");
				tab.add("setNATimer");
				tab.add("setChest");
				tab.add("comChest");
				tab.add("setMap");
				tab.add("club");
			}
		}
		if (sender instanceof Player && args.length == 2) {
			if(label.equalsIgnoreCase("battleroyale") && args[0].equals("club")){
				
				tab.add("list");
				tab.add("get");
				tab.add("remove");
				tab.add("set");
				
			}
		}
		if (sender instanceof Player && args.length == 3) {
			if(label.equalsIgnoreCase("battleroyale") && args[0].equals("club")){
				if(args[1].equals("set") || args[1].equals("remove") || args[1].equals("get") || args[1].equals("debugger")){
					for(Player p : Bukkit.getOnlinePlayers()){
						tab.add(p.getName());
					}
				}
				if(args[1].equals("get")){
					tab.add("all");
				}
			}
			
		}
		if (sender instanceof Player && args.length == 4) {
			if(label.equalsIgnoreCase("battleroyale") && args[0].equals("club")){
				if(args[1].equals("set")){
					for(Club c : ClubManager.getClubList()){
						tab.add(c.getName());
					}
				}
			}
		}

		return tab;
	}
}
