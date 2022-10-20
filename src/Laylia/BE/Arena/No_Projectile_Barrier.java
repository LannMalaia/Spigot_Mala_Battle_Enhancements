package Laylia.BE.Arena;


import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import Laylia.BE.Main.Main;
import laylia_core.main.TitleManager;

public class No_Projectile_Barrier
{
	public static void Barrier_Action(Player player)
	{
		if(player.getInventory().getItemInMainHand().hasItemMeta())
		{
			if(player.getInventory().getItemInMainHand().getItemMeta().hasLore())
			{
				List<String> lores = player.getInventory().getItemInMainHand().getItemMeta().getLore();
				if(lores != null)
				{
					for(int count = 0; count < lores.size(); count++)
					{
						if(lores.get(count).matches(".*" + "오브젝트 배리어" + ".*"))
						{
							if(player.getMetadata("Object_Barrier_Cooldown").size() != 0)
							{
								ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "오브젝트 배리어 쿨타임입니다.");
							}
							else
							{
								player.setMetadata("Object_Barrier_Cooldown", new FixedMetadataValue(Main.mother, 1));
								
								TitleManager.sendTitle(player, 0, 60, 0, "", "§6§l오브젝트 배리어");

								Bukkit.getScheduler().runTaskLater(Main.mother, new Barrier_Cancel(player.getLocation(), player, 40), 0);
								Bukkit.getScheduler().runTaskLater(Main.mother, new Barrier_Cooldown(player), 1200);
							}
						}
					}
				}
			}
		}
	}
}

//원 캔슬 판정
class Barrier_Cancel implements Runnable
{
	Location loc;
	Player player;
	int time;
	
	public Barrier_Cancel(Location original_loc, Player _player, int _time)
	{
		loc = original_loc;
		player = _player;
		time = _time;
	}
	
	public void run()
	{
		if(player.getInventory().getItemInMainHand().hasItemMeta())
		{
			if(player.getInventory().getItemInMainHand().getItemMeta().hasLore())
			{
				List<String> lores = player.getInventory().getItemInMainHand().getItemMeta().getLore();
				if(lores != null)
				{
					for(int count = 0; count < lores.size(); count++)
					{
						if(lores.get(count).matches(".*" + "오브젝트 배리어" + ".*"))
						{
							double dist = loc.distance(player.getLocation());
							if(dist > 1d || time <= 0)
							{
								if(dist > 1d)
								{
									ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "정신이 흐트러져 배리어가 사라졌습니다.");
									return;
								}
								else if(time <= 0)
								{
									ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "지속 시간이 끝나 배리어가 사라졌습니다.");
									return;
								}
								// 취소
							}
							else
							{
								// 지속
								Bukkit.getScheduler().runTaskLater(Main.mother, new Barrier_Circle(0, time * 20 + 10, player.getLocation()), 0);
								Bukkit.getScheduler().runTaskLater(Main.mother, new Barrier_Circle(0, time * 20, player.getLocation()), 5);
								Bukkit.getScheduler().runTaskLater(Main.mother, new Barrier_Judgement(player.getLocation()), 2);
								Bukkit.getScheduler().runTaskLater(Main.mother, new Barrier_Judgement(player.getLocation()), 4);
								Bukkit.getScheduler().runTaskLater(Main.mother, new Barrier_Judgement(player.getLocation()), 6);
								Bukkit.getScheduler().runTaskLater(Main.mother, new Barrier_Judgement(player.getLocation()), 8);
								Bukkit.getScheduler().runTaskLater(Main.mother, new Barrier_Cancel(loc, player, time - 1), 10);
								return;
							}
						}
					}
				}
			}
		}
		ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "정신이 흐트러져 배리어가 사라졌습니다.");
	}

}


// 원의 회전
class Barrier_Circle implements Runnable
{
	double angle;
	double pitch_angle;
	Location loc;
	double radius = 7d;
	
	public Barrier_Circle(double _angle, double _pitch_angle, Location _loc)
	{
		angle = _angle;
		pitch_angle = _pitch_angle;
		loc = _loc;
	}
	
	public void run()
	{
		Location temp_loc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		Vector x1 = new Vector(0d, 0d, -1d).normalize();
		Vector x2 = new Vector(Math.sin(Math.toRadians(pitch_angle)), Math.cos(Math.toRadians(pitch_angle)), 0d).normalize().crossProduct(x1).normalize();
		
		for(int i = 0; i < 140; i++)
		{
			temp_loc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
			Location temp2 = temp_loc.add(x1.clone().multiply(radius * Math.sin((double)i / 140d * Math.PI * 2d))).add(x2.clone().multiply(radius * Math.cos((double)i / 140d * Math.PI * 2d)));
			temp2.getWorld().spawnParticle(Particle.REDSTONE, temp2, 1, 0.4d, 0.4d, 1d, 0d);
			//ParticleEffect.REDSTONE.sendColor(Bukkit.getOnlinePlayers(), temp2, Color.AQUA);
			
			//ParticleEffect.REDSTONE.send(Bukkit.getOnlinePlayers(), temp2, 0, 0, 0, 0, 1);
		}

		temp_loc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		x1 = new Vector(1d, 0d, 0d).normalize();
		x2 = new Vector(0d, Math.cos(Math.toRadians(pitch_angle)), Math.sin(Math.toRadians(pitch_angle))).normalize().crossProduct(x1).normalize();
		
		for(int i = 0; i < 140; i++)
		{
			temp_loc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
			Location temp2 =  temp_loc.add(x1.clone().multiply(radius * Math.sin((double)i / 140d * Math.PI * 2d))).add(x2.clone().multiply(radius * Math.cos((double)i / 140d * Math.PI * 2d)));
			temp2.getWorld().spawnParticle(Particle.REDSTONE, temp2, 1, 0.4d, 0.4d, 1d, 0d);
			//ParticleEffect.REDSTONE.sendColor(Bukkit.getOnlinePlayers(), temp2, Color.AQUA);
			
			//ParticleEffect.REDSTONE.send(Bukkit.getOnlinePlayers(), temp2, 0, 0, 0, 0, 1);
		}
		
		temp_loc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		x1 = new Vector(-Math.sin(Math.toRadians(pitch_angle)), 0d, Math.cos(Math.toRadians(pitch_angle))).normalize();
		x2 = new Vector(Math.cos(Math.toRadians(pitch_angle)), 0d, Math.sin(Math.toRadians(pitch_angle))).normalize().crossProduct(x1).normalize();
		
		for(int i = 0; i < 140; i++)
		{
			temp_loc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
			Location temp2 = temp_loc.add(x1.clone().multiply(radius * Math.sin((double)i / 140d * Math.PI * 2d))).add(x2.clone().multiply(radius * Math.cos((double)i / 140d * Math.PI * 2d)));
			temp2.getWorld().spawnParticle(Particle.REDSTONE, temp2, 1, 0.4d, 0.4d, 1d, 0d);
			//ParticleEffect.REDSTONE.sendColor(Bukkit.getOnlinePlayers(), temp2, Color.AQUA);
			//ParticleEffect.REDSTONE.sendColor(Bukkit.getOnlinePlayers(), temp2, Color.AQUA);
			
			//ParticleEffect.REDSTONE.send(Bukkit.getOnlinePlayers(), temp2, 0, 0, 0, 0, 1);
		}
	}
}

class Barrier_Judgement implements Runnable
{
	Location loc;
	double radius = 10d;
	
	public Barrier_Judgement(Location _loc)
	{
		loc = _loc;
	}
	
	public void run()
	{
		Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, radius, radius, radius);
		for(Entity temp : entities)
		{
			if(temp instanceof Projectile)
			{
				Projectile obj = (Projectile)temp;
				if(obj instanceof Arrow)
				{
					Arrow arrow = (Arrow)obj;
					if(arrow.getShooter() instanceof Player)
					{
						((Player)arrow.getShooter()).sendMessage("화살이 방어막에 가로막혔습니다.");
					}
				}
				temp.remove();
			}
		}
	}
}

class Barrier_Cooldown implements Runnable
{
	Player player;
	
	public Barrier_Cooldown(Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Object_Barrier_Cooldown", Main.mother);
	}
}















