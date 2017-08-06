package com.plugin.ftb.battleroyale.clubs.data;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.plugin.ftb.battleroyale.BattleRoyale;
import com.plugin.ftb.battleroyale.clubs.ClubManager;
import com.plugin.ftb.battleroyale.clubs.support.ManyArrows;
import com.plugin.ftb.battleroyale.clubs.support.PSSystem;

import net.minecraft.server.v1_11_R1.PacketPlayOutEntityDestroy;

public class Test implements Listener{
	
	public static BattleRoyale plugin = BattleRoyale.plugin;
	
	private String data;
	private static final String name = "Test";
	
	@EventHandler
	public void EntityDamage(EntityDamageByEntityEvent e){
		Entity en = e.getDamager();
		
		if(en instanceof Player){
			
			Player p = (Player)en;
			if(ClubManager.getClub(p).equals(ClubManager.getClub(name))){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Projectile(PlayerInteractEvent e){
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.SNOW_BALL){
				if(ClubManager.hasClub(e.getPlayer())){
					if(ClubManager.getClub(e.getPlayer()).equals(ClubManager.getClub(name))){
						data = name;
					}
				}
			}
		}
		if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_HOE){
			if(ClubManager.hasClub(e.getPlayer())){
				if(ClubManager.getClub(e.getPlayer()).equals(ClubManager.getClub(name))){
					if("いかずちの杖".equals(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName())){
						Location l = PSSystem.getNearbyEntity(e.getPlayer().getLocation() ,10000, e.getPlayer()).getLocation();
						e.getPlayer().getWorld().strikeLightning(l);
					}
					if("てんばつの杖".equals(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName())){
						PSSystem.getNearbyEntity(e.getPlayer().getLocation() ,10000, e.getPlayer()).setVelocity(new Vector(0,2,0));
					}
					if("まどうしの杖".equals(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName())){
						PSSystem.getNearbyEntity(e.getPlayer().getLocation() ,10000, e.getPlayer()).setFireTicks(50000);
					}
					if("マグマの杖".equals(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName())){
						/*Location l = PSSystem.getNearbyEntity(e.getPlayer().getLocation() ,10000, e.getPlayer()).getLocation();
						e.getPlayer().getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 10F, false, true);*/
					}
					if("ギガデイン".equals(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName())){
						List<LivingEntity> list = PSSystem.getNearbyEntityAll(e.getPlayer().getLocation() ,10000, e.getPlayer());
						for(Entity entity : list){
							Location l = entity.getLocation();
							e.getPlayer().getWorld().strikeLightningEffect(l);
							LivingEntity le = (LivingEntity)entity;
							le.damage(le.getHealth());
						}
					}
					if("イオナズン".equals(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName())){
						/*List<Entity> list = PSSystem.getNearbyEntityAll(e.getPlayer().getLocation() ,10000, e.getPlayer());
						for(Entity entity : list){
							Location l = entity.getLocation();
							e.getPlayer().getWorld().createExplosion(l,10F);
						}*/
					}
					if("test".equals(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName())){
						for(World w : Bukkit.getWorlds()){
							for(Entity entity :  w.getEntities()){
								if(entity instanceof Item){
									entity.remove();
								}
								else if(entity instanceof Projectile){
									entity.remove();
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void ProjectileLunch(ProjectileLaunchEvent e){
		if(e.getEntityType() == EntityType.SNOWBALL){
			if(name.equals(data)){
				PSSystem.ChangingSpeed(e.getEntity(), 2);
				data = null;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void Block(BlockBreakEvent e){
		Block b = e.getBlock();
		
		if(b.getType() == Material.DOUBLE_PLANT){
			if(b.getData() == 0 ||b.getData() == 1||b.getData() == 2||b.getData() == 3||b.getData() == 4||b.getData() == 5){
				Location l = b.getLocation().add(0,1,0);
				l.getBlock().setType(Material.DOUBLE_PLANT);
				l.getBlock().setData((byte) 10);
			}
		}
	}
	
	@EventHandler
	public void Arrow(EntityShootBowEvent e){
		
		if(e.getEntity() instanceof Player){
			Player p = (Player)e.getEntity();
			if(ClubManager.hasClub(p)){
				if(ClubManager.getClub(p) == ClubManager.getClub(name)){
					if("モーゼの十戒".equals(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName())){
						Projectile pro = (Projectile)e.getProjectile();
						PSSystem.ChangingSpeed(pro, 4);
						
						new BukkitRunnable() {
				            @Override
				            public void run() {
				            	Vector v = pro.getVelocity();
								Location l = pro.getLocation();
								Random r = new Random();
								
								pro.remove();
								
								for (int i = 1; i <= 81 * 9; i++){
									
									Location loc = new Location(l.getWorld(),l.getX() + r.nextDouble() + r.nextInt(2) ,l.getY() + r.nextDouble() + r.nextInt(2),l.getZ() + r.nextDouble()+ r.nextInt(2));
									
									Projectile ar = pro.getWorld().spawnArrow(loc, v, 0.6F, 20);
									PSSystem.ChangingSpeed(ar, 16);
									ar.setGravity(false);
									ar.setCustomName("モーゼ");
									
									new BukkitRunnable() {
							            @Override
							            public void run() {
							            	ar.remove();
							            }
							        }.runTaskLater(plugin, 20 * 60);
								}
				            }
				        }.runTaskLater(plugin, 1);
					}
					else if("メリーさんのひつじ".equals(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName())){
						Projectile pro = (Projectile)e.getProjectile();
						Vector v = pro.getVelocity();
						Location l = pro.getLocation();
						pro.remove();
						
						new ManyArrows(v, l, p,plugin);
						
					}
					else{
						Projectile pro = (Projectile)e.getProjectile();
						PSSystem.ChangingSpeed(pro,5);
						pro.setGravity(false);
						
						new BukkitRunnable() {
				            @Override
				            public void run() {
				            	Vector v = pro.getVelocity();
								Location l = pro.getLocation();
								Random r = new Random();
								
								pro.remove();
								
								for (int i = 1; i <= 81 * 9 * 2; i++){
									
									Location loc = new Location(l.getWorld(),l.getX() + r.nextDouble() + r.nextInt(2) ,l.getY() + r.nextDouble() + r.nextInt(2),l.getZ() + r.nextDouble()+ r.nextInt(2));
									
									//Vector vec = new Vector(r.nextDouble() - r.nextDouble(),-1,r.nextDouble() - r.nextDouble());
									
									Projectile ar = pro.getWorld().spawnArrow(loc, v, 0.6F, 20);
									PSSystem.ChangingSpeed(ar, 16);
									
									for(Player p : Bukkit.getServer().getOnlinePlayers()) {
									    PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ar.getEntityId());
									    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
									}
									
									ar.setGravity(false);
									
									new BukkitRunnable() {
							            @Override
							            public void run() {
							            	ar.remove();
							            }
							        }.runTaskLater(plugin, 20 * 60);
								}
				            }
				        }.runTaskLater(plugin, 1);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void hit(ProjectileHitEvent e){
		if(e.getEntity().getType() == EntityType.ARROW){
			Projectile p = e.getEntity();
			if("モーゼ".equals(p.getCustomName())){
				Location l = p.getLocation();
				Cow c = (Cow)l.getWorld().spawnEntity(l, EntityType.COW);
				c.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,50000,4));
				p.remove();
			}
		}
		e.getEntity().remove();
	}
	
	@EventHandler
	public void damage(EntityDamageByEntityEvent e){
		if("モーゼ".equals(e.getDamager().getCustomName())){
			e.setCancelled(true);
		}
		if(e.getCause() == DamageCause.PROJECTILE){
			if(e.getEntityType() == EntityType.COW && !e.isCancelled()){
				Cow c = (Cow)e.getEntity();
				if(c.hasPotionEffect(PotionEffectType.INVISIBILITY)){
					c.removePotionEffect(PotionEffectType.INVISIBILITY);
				}
			}
		}
	}
	
	@EventHandler
	public void damage2(EntityDamageEvent e){
		if(e.getCause() == DamageCause.FALL){
			if(e.getEntityType() == EntityType.COW){
				e.setCancelled(true);
			}
		}
	}
}

