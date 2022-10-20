package Laylia.BE.Arena;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import Laylia.BE.Main.Main;

public class Arena_Speed_Aura
{
	public static void Aura_Judge(Player player, int slot)
	{
		if(player.getInventory().getItem(slot).hasItemMeta())
		{
			if(player.getInventory().getItem(slot).getItemMeta().hasLore())
			{
				List<String> lores = player.getInventory().getItem(slot).getItemMeta().getLore();
				if(lores != null)
				{
					for(int count = 0; count < lores.size(); count++)
					{
						if(lores.get(count).matches(".*" + "스피드 오오라" + ".*"))
						{
							// 오오라 실행
							if(player.getMetadata("arena_speed_aura").size() == 0)
							{
								player.setMetadata("arena_speed_aura", new FixedMetadataValue(Main.mother, true));
								Bukkit.getScheduler().runTaskLater(Main.mother, new Speed_Aura(player), 10);
							}
						}
					}
				}
			}
		}
	}

}

class Speed_Aura implements Runnable
{
	Player player;
	public Speed_Aura(Player p)
	{
		player = p;
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
						if(lores.get(count).matches(".*" + "스피드 오오라" + ".*"))
						{
							// 오오라 발동
							double radius = 13;
							for(double theta = 0; theta < Math.PI * 2d; theta += 0.02)
							{
								Location loc = player.getLocation();
								loc.add(radius * Math.cos(theta), 1, radius * Math.sin(theta));
								
								loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0d, 0.1d, 1d, 0d);
								
								//ParticleEffect.REDSTONE.sendColor(Bukkit.getOnlinePlayers(), loc, Color.AQUA);
							}
							
							// 회복 적용
							Collection<Entity> temp = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius);
							for(Entity temp2 : temp)
							{
								if(temp2 instanceof HumanEntity)
								{
									Player p = (Player)temp2;
									if(player.hasPotionEffect(PotionEffectType.SPEED))
									{
										if(player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() < 2)
											player.removePotionEffect(PotionEffectType.SPEED);
									}
									p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2));
								}
							}
							
							Bukkit.getScheduler().runTaskLater(Main.mother, new Speed_Aura(player), 10);
							return;
						}
					}
				}
			}
		}
		player.removeMetadata("arena_speed_aura", Main.mother);
	}
}
