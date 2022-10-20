package Laylia.BE.Arena;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import Laylia.BE.Main.Main;
import laylia_core.main.TitleManager;

public class Arena_Roar
{
	public static void Roar(Player player)
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
						if(lores.get(count).matches(".*" + "포효" + ".*"))
						{
							if(player.getMetadata("Roar_Cooldown").size() != 0)
							{
								ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "포효 쿨타임입니다.");
							}
							else
							{
								// 오오라 발동
								double radius = 20;
								
								// 회복 적용
								Collection<Entity> temp = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius);
								for(Entity temp2 : temp)
								{
									if(temp2 instanceof Creature)
									{
										((Creature)temp2).setTarget(player);
										player.getWorld().spawnParticle(Particle.REDSTONE, temp2.getLocation().clone().add(0, 3, 0), 1, 0.1d, 0.1d, 0.1d, 0d);
										player.getWorld().spawnParticle(Particle.REDSTONE, temp2.getLocation().clone().add(0, 6, 0), 1, 0.1d, 1d, 0.1d, 0d);
										//ParticleEffect..send(Bukkit.getOnlinePlayers(), , 0.1, 0.1, 0.1, 0, 100);
										//ParticleEffect.REDSTONE.send(Bukkit.getOnlinePlayers(), temp2.getLocation().clone().add(0, 6, 0), 0.1, 1, 0.1, 0, 200);
									}
								}

								for(int i = 0; i < 10; i++)
								{
									Location loc = player.getLocation();
									loc.add(0, 0.8d * i, 0);
									Bukkit.getScheduler().runTaskLater(Main.mother, new Roar_Particle(loc, radius), 1 * i);
								}
								player.setMetadata("Roar_Cooldown", new FixedMetadataValue(Main.mother, 1));
								
								TitleManager.sendTitle(player, 0, 60, 0, "", "§4§l포효");

								if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
								{
									if(player.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() < 2)
										player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
								}
								player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 2));
								
								Bukkit.getScheduler().runTaskLater(Main.mother, new Roar_Cooldown(player), 800);
							}
						}
					}
				}
			}
		}
	}

}

class Roar_Particle implements Runnable
{
	Location loc;
	double r;
	
	public Roar_Particle(Location _loc, double radius)
	{
		loc = _loc;
		r = radius;
	}
	
	public void run()
	{
		for(double theta = 0; theta < Math.PI * 2d; theta += 0.02)
		{
			Location temp = loc.clone();
			temp.add(r * Math.cos(theta), 0, r * Math.sin(theta));
			temp.getWorld().spawnParticle(Particle.REDSTONE, temp, 1, 1d, 0.5d, 0.1d, 0d);
			//ParticleEffect.REDSTONE.sendColor(Bukkit.getOnlinePlayers(), temp, Color.ORANGE);
		}
	}
}

class Roar_Cooldown implements Runnable
{
	Player player;
	
	public Roar_Cooldown(Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Roar_Cooldown", Main.mother);
	}
}