package com.plugin.ftb.battleroyale.clubs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.plugin.ftb.battleroyale.BattleRoyale;

public class ClubManager extends BattleRoyale{
	
	public static BattleRoyale plugin = BattleRoyale.plugin;
	private static List<Club> clublist = new ArrayList<>();
	
	public static final String filename = "plugins/battleroyale/clubsystem.ini";
	
	/**
	 * システムに部活を登録します。<p>
	 * 登録されていない場合、エラーになるのため最初に登録してください。
	 */
	public static void addClub(Club club){
		clublist.add(club);
	}
	
	/**
	 * システムに部活を登録します。<p>
	 * 登録されていない場合、エラーになるのため最初に登録してください。<p>
	 * また、表示されるべき名前も同時に設定します。
	 */
	public static void addClub(Club club, String displayname){
		clublist.add(club);
		club.setDisplayName(displayname);
	}
	
	/**
	 * プレイヤーに部活を設定します。
	 */
	@SuppressWarnings("deprecation")
	public static void setClub(Player player ,Club club){
		if(ClubManager.getClubList().contains(club)){
			 Properties conf = new Properties();
			 try {
				 conf.load(new FileInputStream(filename));
			 } catch (IOException e) {}
			try {
				conf.setProperty(player.getName(), club.getName());
				conf.save(new FileOutputStream(filename), "");
			} catch (NumberFormatException nfex) {}catch(ArrayIndexOutOfBoundsException a){} catch (FileNotFoundException e) {}
		}
		else{
			Bukkit.broadcastMessage(ChatColor.RED + "部活 <" + club.getName() + "> は登録されていません。");
		}
	}
	
	/**
	 * プレイヤーにランダムで部活を設定します。<p>
	 * log = true の時、対象に部活を付与した旨のメッセージを送信します。
	 * @param player
	 */
	public static void setClubRandom(Player player, boolean log) {
		List<Club> clublist = ClubManager.getClubList();
		Collections.shuffle(clublist);
		Club club = clublist.get(0);
		if(club == ClubManager.getClub("Dummy")) {
			ClubManager.setClubRandom(player, log);
		}
		else {
			ClubManager.setClub(player, club);
			if(log) {
				player.sendMessage("あなたに部活" + ChatColor.AQUA + " <" + club.getDisplayName() + "> " + ChatColor.RESET + "を付与しました。");
			}
		}
	}
	
	/**
	 * プレイヤーの部活を取得します。<p>
	 * @return プレイヤーの所属する部活(Club)
	 */
	@SuppressWarnings("deprecation")
	public static Club getClub(Player player){
		
		 Properties conf = new Properties();
		 try {
			 conf.load(new FileInputStream(filename));
		 } catch (IOException e) {}
		try {
			String club = conf.getProperty(player.getName());
			if("".equals(club)){
				return ClubManager.getClub("Dummy");
			}
			conf.save(new FileOutputStream(filename), "");
			return ClubManager.getClub(club);
		} catch (NumberFormatException nfex) {}catch(ArrayIndexOutOfBoundsException a){} catch (FileNotFoundException e) {}
		return null;
	}
	
	/**
	 * プレイヤーが部活に所属しているか判定します。
	 * @return プレイヤーが部活に所属しているか(true or false)
	 */
	public static boolean hasClub(Player player){
		
		if(ClubManager.getClub(player) != ClubManager.getClub("Dummy")){
			return true;
		}
		return false;
	}
	
	/**
	 * すべてのプレイヤーから部活を消去します。
	 */
	@SuppressWarnings("deprecation")
	public static void removeClubs(){
		for(Player p: Bukkit.getOnlinePlayers()){
			 Properties conf = new Properties();
			 try {
				 conf.load(new FileInputStream(filename));
			 } catch (IOException e) {}
			try {
				conf.setProperty(p.getName(), "");
				conf.save(new FileOutputStream(filename), "");
			} catch (NumberFormatException nfex) {}catch(ArrayIndexOutOfBoundsException a){} catch (FileNotFoundException e) {}
		}
	}
	
	/**
	 * 対象から部活を消去します。
	 */
	@SuppressWarnings("deprecation")
	public static void removeClub(Player player){
		 Properties conf = new Properties();
		 try {
			 conf.load(new FileInputStream(filename));
		 } catch (IOException e) {}
		try {
			conf.setProperty(player.getName(), "");
			conf.save(new FileOutputStream(filename), "");
		} catch (NumberFormatException nfex) {}catch(ArrayIndexOutOfBoundsException a){} catch (FileNotFoundException e) {}
	}
	
	/**
	 * 対象をシステムから抹消します。
	 * @param club
	 */
	public static void removeClub(Club club){
		if(ClubManager.getClubList().contains(club)){
			ClubManager.getClubList().remove(club);
		}
	}
	
	/**
	 * 登録されている部活一覧を取得します。
	 * @return 部活リスト(List Club)
	 */
	public static List<Club> getClubList(){
		return clublist;
	}
	
	/**
	 * システム名から部活を取得します。<p>
	 * 登録されていない場合、Nullが返ります。
	 * @return Club
	 */
	public static Club getClub(String clubname){
		
		for(Club club : ClubManager.getClubList()){
			if(clubname.equals(club.getName())){
				return club;
			}
		}
		Bukkit.broadcastMessage(ChatColor.RED + "部活 <" + clubname + "> は存在しません");
		return  null;
	}
	
	/**
	 * システム名が対象の部活が存在するか判定します。
	 * @param clubname
	 * @return 対象が存在するか(true or false)
	 */
	public static boolean isClub(String clubname){
		
		if(clubname == null){
			return false;
		}
		
		for(Club club : ClubManager.getClubList()){
			if(clubname.equals(club.getName())){
				return true;
			}
		}
		return  false;
	}
}




























