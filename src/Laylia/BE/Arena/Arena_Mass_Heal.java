package Laylia.BE.Arena;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import Laylia.BE.Main.Main;
import laylia_core.main.TitleManager;

public class Arena_Mass_Heal
{
	public static void Mass_Heal(Player player)
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
						if(lores.get(count).matches(".*" + "매시브 힐" + ".*"))
						{
							if(player.getMetadata("Mass_Heal_Cooldown").size() != 0)
							{
								ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "매시브 힐 쿨타임입니다.");
							}
							else
							{
								// 오오라 발동
								double radius = 6;
								
								// 회복 적용
								Collection<Entity> temp = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius);
								for(Entity temp2 : temp)
								{
									if(temp2 instanceof HumanEntity)
									{
										Player p = (Player)temp2;
										p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
									}
									if(temp2 instanceof Zombie)
									{
										((Zombie) temp2).damage(10);
									}
									if(temp2 instanceof Skeleton)
									{
										((Skeleton) temp2).damage(10);
									}
								}
								
								for(int i = 0; i < 10; i++)
								{
									Location loc = player.getLocation();
									loc.add(0, 0.8d * i, 0);
									Bukkit.getScheduler().runTaskLater(Main.mother, new Mass_Heal_Particle(loc, radius), 1 * i);
								}
	
								player.setMetadata("Mass_Heal_Cooldown", new FixedMetadataValue(Main.mother, 1));
								
								TitleManager.sendTitle(player, 0, 60, 0, "", "§6§l매시브 힐");
	
								Bukkit.getScheduler().runTaskLater(Main.mother, new Mass_Heal_Cooldown(player), 800);
							}
						}
					}
				}
			}
		}
	}

}

class Mass_Heal_Particle implements Runnable
{
	Location loc;
	double r;
	
	public Mass_Heal_Particle(Location _loc, double radius)
	{
		loc = _loc;
		r = radius;
	}
	
	public void run()
	{
		for(double theta = 0; theta < Math.PI * 2d; theta += 0.03)
		{
			Location temp = loc.clone();
			temp.add(r * Math.cos(theta), 0, r * Math.sin(theta));
			temp.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, temp, 1, 0d, 0d, 0d, 0d);
			
		}
	}
}

class Mass_Heal_Cooldown implements Runnable
{
	Player player;
	
	public Mass_Heal_Cooldown(Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Mass_Heal_Cooldown", Main.mother);
	}
}