package com.plugin.ftb.battleroyale;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

class RunTP extends BukkitRunnable{

	public static BattleRoyale plugin = BattleRoyale.plugin;

	// チーム名
	public static final String TEAM_ALIVE_NAME = BattleRoyale.TEAM_ALIVE_NAME;
	public static final String TEAM_DEAD_NAME = BattleRoyale.TEAM_DEAD_NAME;

	//ロビーへテレポート用
	public static ArrayList<Integer> locB = new ArrayList<>();

	int locX = plugin.getConfig().getInt("SignValue.x");
	int locY = plugin.getConfig().getInt("SignValue.y");
	int locZ = plugin.getConfig().getInt("SignValue.z");

	@SuppressWarnings("deprecation")
	public void run(){
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		locB = (ArrayList<Integer>) plugin.getConfig().getIntegerList("Lobbypoint");
		///////loc.get()になってました。
		Location worB = new Location(Bukkit.getWorld("world"),locB.get(0),locB.get(1),locB.get(2));

		for(Player p : Bukkit.getOnlinePlayers()){
			if(board.getTeam(TEAM_ALIVE_NAME).hasPlayer(p)){

				p.getPlayer().teleport(worB);
				board.getTeam(TEAM_ALIVE_NAME).removePlayer(p);

			}else if(board.getTeam(TEAM_DEAD_NAME).hasPlayer(p)){

				p.getPlayer().teleport(worB);
				board.getTeam(TEAM_DEAD_NAME).removePlayer(p);

			}
		}

		Block b = Bukkit.getWorld("world").getBlockAt(locX, locY, locZ);

		if (b.getType()==Material.WALL_SIGN || b.getType()==Material.SIGN_POST) {

			Sign ee = (Sign) b.getState();

        	ee.setLine(1, ChatColor.BOLD + String.valueOf(plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(TEAM_ALIVE_NAME).getPlayers().size() + "/" + 50));

        	ee.update();

		}

		//ゲーム中に破壊されたブロックの復元
		for(int i=0;i<MainListener.bBLOCK.size();i++){
			MainListener.bBLOCK.get(i).setType(MainListener.bMAT.get(i));
			MainListener.bBLOCK.get(i).setData(MainListener.bDATA.get(i));
		}

		resetVar();

		this.cancel();
	}

	//毎試合の変数の初期化
	public void resetVar(){

		PlusThreadClass.count=0;
    	PlusThreadClass.countPast=0;
    	PlusDeathArea.beta=0;
    	StartCommand.start=0;
		PlusThreadClass.deathRan.clear();
		PlusThreadClass.deathRanCount.clear();
		PlusThreadClass.deathRanCountPast.clear();
		PlusDeathArea.plusDeathX.clear();
		PlusDeathArea.plusDeathZ.clear();
		CustomMap.pastLoc.clear();
		CustomMap.pastLocP.clear();
		MainListener.bBLOCK.clear();
		MainListener.bDATA.clear();
		MainListener.bMAT.clear();

		Bukkit.getScheduler().cancelAllTasks();

		return;
	}
}


public class MainListener implements Listener {

	public static BattleRoyale plugin = BattleRoyale.plugin;

	// チーム名
	public static final String TEAM_ALIVE_NAME = BattleRoyale.TEAM_ALIVE_NAME;
	public static final String TEAM_DEAD_NAME = BattleRoyale.TEAM_DEAD_NAME;

	// キル数カウント
	public static HashMap<Player, Integer> killCount = BattleRoyale.killCount;
	// ポイントカウント
	public static HashMap<Player, Integer> pointCount = BattleRoyale.pointCount;

	//リスポーン地点へテレポート用
	public static ArrayList<Integer> loc = new ArrayList<>();

	//ダメージ無効かの判定用
	public static boolean Attack = false;

	/*
	 * ゲーム中に破壊されたブロックの値保存用のリスト
	 * Javaでの構造体の書き方を理解できたら構造体に変更しますm(_ _)m
	 */
	public static ArrayList<Block> bBLOCK = new ArrayList<>();
	public static ArrayList<Byte> bDATA = new ArrayList<>();
	public static ArrayList<Material> bMAT = new ArrayList<>();

	@SuppressWarnings("deprecation")
	@EventHandler
	public static void onBlockBreak(BlockBreakEvent e){
		Player _player = (Player)e.getPlayer();
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		/*
		 * ゲーム中にアイテムが入るチェストの登録
		 * setChestPlayerはチェストを編集する人のデータが入ったリスト
		 */
		if(_player.getInventory().getItemInHand().getType()==Material.BONE
				&& MainCommandExecutor.setChestPlayer.contains(_player)){

			MainConfig.subChestConfig(e.getBlock().getLocation(), _player);

		}

		/*
		 * ゲーム中に破壊されたブロックの情報を取得して保存
		 */
		//ゲーム中で、かつブロックを破壊した人がゲームに参加していて、生存者だった場合
		if(StartCommand.start==1 && board.getTeam(TEAM_ALIVE_NAME).hasPlayer(_player)){

			//破壊されたブロックがガラス、ガラス板、色付きガラス、色付きガラス板だった場合はその場所の値と壊されたブロックの種類、データ値を保存しておく。
			if(e.getBlock().getType()==Material.GLASS
					|| e.getBlock().getType()==Material.STAINED_GLASS
					|| e.getBlock().getType()==Material.STAINED_GLASS_PANE
					|| e.getBlock().getType()==Material.THIN_GLASS){

				//値保存
				bBLOCK.add(Bukkit.getWorld("world").getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()));
				bDATA.add(e.getBlock().getData());
				bMAT.add(e.getBlock().getType());

			}
			//破壊されたブロックが背の高い草花だった場合、setCancelledではデータ値が変わって違うものに置き換わるので、データ値の設定をする。
			else if(e.getBlock().getType() == Material.DOUBLE_PLANT){
				if(e.getBlock().getData() == 0 ||e.getBlock().getData() == 1||e.getBlock().getData() == 2
						||e.getBlock().getData() == 3||e.getBlock().getData() == 4||e.getBlock().getData() == 5){
					Location l = e.getBlock().getLocation().add(0,1,0);
					l.getBlock().setType(Material.DOUBLE_PLANT);
					l.getBlock().setData((byte) 10);
				}
			}
			//その他のブロックは破壊不可
			else{
				e.setCancelled(true);
			}
		}

		//死者は全ブロックを破壊不可能にする。
		if (board.getTeam(TEAM_DEAD_NAME).hasPlayer(_player)) {
			e.setCancelled(true);
		}
	}

	/*
	 * 死亡者チャットを実装
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		String message = event.getMessage();
		Player player = event.getPlayer();
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		// デバッグ用。
		if (message.equalsIgnoreCase("showCounts")) {
			broadcast(killCount + "");
			broadcast(pointCount + "");
		}

		// DEADチームのみに送信
		if (board.getTeam(TEAM_DEAD_NAME).hasPlayer(player)) {
			event.setCancelled(true);
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (board.getTeam(TEAM_DEAD_NAME).hasPlayer(p)) {
					p.sendMessage(ChatColor.GRAY + "<" + player.getName() + "> " + message);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();
		loc = (ArrayList<Integer>) plugin.getConfig().getIntegerList("Deathpoint");
		Location wor = new Location(Bukkit.getWorld("world"),loc.get(0),loc.get(1),loc.get(2));

		if(board.getTeam(TEAM_DEAD_NAME).hasPlayer(e.getPlayer())||board.getTeam(TEAM_ALIVE_NAME).hasPlayer(e.getPlayer())){
			e.setRespawnLocation(wor);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {

		//////Location型の変数locを設定したのでgetBlockの前など少し変更しました。//////

		Player player = event.getEntity();
		Player killer = player.getKiller();
		Location loc = player.getLocation();
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		// 死亡後、DEADチームへ移行
		if (board.getTeam(TEAM_ALIVE_NAME).hasPlayer(player)) {
			board.getTeam(TEAM_ALIVE_NAME).removePlayer(player);
			board.getTeam(TEAM_DEAD_NAME).addPlayer(player);

			//チェストとプレイヤーの頭に置き換わる前に存在していたブロックの値の保存
			//チェスト
			bBLOCK.add(player.getLocation().getBlock());
			bDATA.add(player.getLocation().getBlock().getData());
			bMAT.add(player.getLocation().getBlock().getType());
			//頭
			loc.add(0, 1, 0);
			bBLOCK.add(loc.getBlock());
			bDATA.add(loc.getBlock().getData());
			bMAT.add(loc.getBlock().getType());
			loc.add(0, -1, 0);

			//死亡後、ドロップアイテムをチェストに保管
			Block block = loc.getBlock();
			block.setType(Material.CHEST);
			Chest chest = (Chest)block.getState();
			for(ItemStack itemStack : event.getDrops()){
				chest.getInventory().addItem(itemStack);
			}
			event.getDrops().clear();

			//プレイヤーの頭を置く
			block = loc.add(0, 1, 0).getBlock();
			block.setTypeIdAndData(Material.SKULL.getId(), (byte)1, true);
			Skull skull = (Skull)block.getState();
			skull.setSkullType(SkullType.PLAYER);
			skull.setOwningPlayer(player);
			skull.update();
		}

		if(killer != null){
			// キル数をカウント
			if (killCount.containsKey(killer)) {
				killCount.put(killer, killCount.get(killer) + 1);
				//スコアボードのキル数表示の変更
				new ScoreBoard().onBoard(killer);
			}
			//スコアボードにキル数を表示させるため、SignJoinクラスの61行目で参加するプレイヤーに0を与えました。
			//そのため、elseには行かなくなると思うのでコメント化しました。
			/*else {
				killCount.put(killer, 1);
			}*/

			//ポイントをカウント
			if (pointCount.containsKey(killer)) {
				pointCount.put(killer, pointCount.get(killer) + 1);
			} else {
				pointCount.put(killer, 1);
			}
		}

		// 最後の1人だった場合5ポイントを加算
		if (board.getTeam(TEAM_ALIVE_NAME).getPlayers().size() == 1) {
			if(killer != null){
				if (pointCount.containsKey(killer)) {
					pointCount.put(killer, pointCount.get(killer) + 5);
				} else {
					pointCount.put(killer, 1);
				}
			}

			/*
			 * 終了時統計を表示
			 */
			//0ポイントのプレイヤーはデータがないので追加する
			for(Player p : Bukkit.getServer().getOnlinePlayers()){
				if(!pointCount.containsKey(p)){
					pointCount.put(p, 0);
					broadcast(pointCount.size() + "");
				}
				if(!killCount.containsKey(p)){
					killCount.put(p, 0);
				}
			}
			ArrayList<Player> pointRank = MainUtils.scoreSort(pointCount);
			broadcast(ChatColor.DARK_AQUA + "------------終了------------");
			int same = 0;//同率のプレイヤー用
			for(int i=0; i<pointRank.size(); i++){
				if(i >= 5)
					break;

				//前の順位と同じポイントだった場合同じ順位にする
				if(i >= 1){
					if(pointCount.get(pointRank.get(i-1)) == pointCount.get(pointRank.get(i))){
						same -= 1;
					}else{
						same = 0;
					}
				}
				int rank = i+1 + same;

				ChatColor color = ChatColor.WHITE;
				if(rank == 1)
					color = ChatColor.GOLD;
				if(rank == 2)
					color = ChatColor.YELLOW;
				if(rank == 3)
					color = ChatColor.GREEN;

				broadcast(" " + color + String.valueOf(rank) + "位 : " + pointRank.get(i).getName());
				broadcast(" " + ChatColor.RED + pointCount.get(pointRank.get(i)) + ChatColor.GRAY + " point, " +
						ChatColor.RED + killCount.get(pointRank.get(i)) + ChatColor.GRAY + " kill");
			}
			broadcast(ChatColor.DARK_AQUA + "-----------------------------");

			/*
			 * ゲーム終了後、全員をロビーに戻す
			 * 看板の値をリセット
			 */

			for(Player p : Bukkit.getOnlinePlayers()){
				if(board.getTeam(TEAM_ALIVE_NAME).hasPlayer(p)){

					p.sendMessage(BattleRoyale.prefix+"10秒後にロビーへ戻ります");

				}else if(board.getTeam(TEAM_DEAD_NAME).hasPlayer(p)){

					p.sendMessage(BattleRoyale.prefix+"10秒後にロビーへ戻ります");

				}
			}

			RunTP rtp = new RunTP();
			rtp.runTaskTimer(plugin, 200, 100);

		}
	}

	/*
	 * DEADチームは一切のダメージを受けないようにする
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

			// DEADチームはダメージを受けないように
			if (board.getTeam(TEAM_DEAD_NAME).hasPlayer(player)) {
				event.setCancelled(true);
			}

			//ダメージ無効時間中はダメージを受けないようにする。
			if(!Attack&&board.getTeam(TEAM_ALIVE_NAME).hasPlayer(player)){
				event.setCancelled(true);
			}

		}
	}

	//畑が荒らし防止------
	@EventHandler(ignoreCancelled=true)
	public void onEntityInteractEvent(EntityInteractEvent event){
		if (event.getBlock().getType().equals(Material.SOIL)) {
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = (Player) event.getPlayer();
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		if (event.getAction().equals(Action.PHYSICAL) && event.hasBlock()
			&& event.getClickedBlock().getType().equals(Material.SOIL) && board.getTeam(TEAM_ALIVE_NAME).hasPlayer(player)) {
			event.setCancelled(true);
		}
	}
	//ここまで-------

	@SuppressWarnings("deprecation")
	//壁紙、手綱、額縁を破壊不可にする
	@EventHandler
	public void onBreak(HangingBreakByEntityEvent e){
		Player player = (Player) e.getRemover();
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		if(board.getTeam(TEAM_ALIVE_NAME).hasPlayer(player)){
			e.setCancelled(true);
		}
	}

	// ブロードキャスト
	public void broadcast(String message) {
		BattleRoyale.broadcast(message);
	}

}
