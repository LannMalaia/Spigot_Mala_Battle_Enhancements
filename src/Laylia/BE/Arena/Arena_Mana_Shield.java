package Laylia.BE.Arena;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import Laylia.BE.Main.Main;
import laylia_core.main.TitleManager;

public class Arena_Mana_Shield
{
	public static void Mana_Shield(Player player)
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
						if(lores.get(count).matches(".*" + "마나 실드" + ".*"))
						{
							if(player.getMetadata("Mana_Shield_Cooldown").size() != 0)
							{
								ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "마나 실드 쿨타임입니다.");
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

										if(player.hasPotionEffect(PotionEffectType.ABSORPTION))
										{
											if(player.getPotionEffect(PotionEffectType.ABSORPTION).getAmplifier() < 4)
												player.removePotionEffect(PotionEffectType.ABSORPTION);
										}
										p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 4));
									}
								}
								
								for(int i = 0; i < 10; i++)
								{
									Location loc = player.getLocation();
									loc.add(0, 0.8d * i, 0);
									Bukkit.getScheduler().runTaskLater(Main.mother, new Mana_Shield_Particle(loc, radius), 1 * i);
								}
	
								player.setMetadata("Mana_Shield_Cooldown", new FixedMetadataValue(Main.mother, 1));
								
								TitleManager.sendTitle(player, 0, 60, 0, "", "§6§l마나 실드");
	
								Bukkit.getScheduler().runTaskLater(Main.mother, new Mana_Shield_Cooldown(player), 1200);
							}
						}
					}
				}
			}
		}
	}

}

class Mana_Shield_Particle implements Runnable
{
	Location loc;
	double r;
	
	public Mana_Shield_Particle(Location _loc, double radius)
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
			temp.getWorld().spawnParticle(Particle.REDSTONE, temp, 1, 0.9d, 0.9d, 0.9d, 0d);
			
			//ParticleEffect..sendColor(Bukkit.getOnlinePlayers(), temp, Color.SILVER);
		}
	}
}

class Mana_Shield_Cooldown implements Runnable
{
	Player player;
	
	public Mana_Shield_Cooldown(Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Mana_Shield_Cooldown", Main.mother);
	}
}
