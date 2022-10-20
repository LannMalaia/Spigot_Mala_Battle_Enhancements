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

public class Arena_Attack_Aura
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
						if(lores.get(count).matches(".*" + "파워 오오라" + ".*"))
						{
							// 오오라 실행
							if(player.getMetadata("arena_attack_aura").size() == 0)
							{
								//player.sendMessage("파워 오오라 발동");
								player.setMetadata("arena_attack_aura", new FixedMetadataValue(Main.mother, true));
								Bukkit.getScheduler().runTaskLater(Main.mother, new Attack_Aura(player), 10);
							}
						}
					}
				}
			}
		}
	}

}

class Attack_Aura implements Runnable
{
	Player player;
	public Attack_Aura(Player p)
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
						if(lores.get(count).matches(".*" + "파워 오오라" + ".*"))
						{
							// 오오라 발동
							//player.sendMessage("파워 오오라 발동");
							double radius = 6;
							for(double theta = 0; theta < Math.PI * 2d; theta += 0.05)
							{
								Location loc = player.getLocation();
								loc.add(radius * Math.cos(theta), 1, radius * Math.sin(theta));
								loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 1d, 0.4d, 0.4d, 0d);
								//ParticleEffect.REDSTONE.sendColor(Bukkit.getOnlinePlayers(), loc, Color.PURPLE);
							}
							
							// 회복 적용
							Collection<Entity> temp = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius);
							for(Entity temp2 : temp)
							{
								if(temp2 instanceof HumanEntity)
								{
									Player p = (Player)temp2;
									if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
									{
										if(player.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() < 1)
											player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
									}
									p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 1));
								}
							}
							
							Bukkit.getScheduler().runTaskLater(Main.mother, new Attack_Aura(player), 10);
							return;
						}
					}
				}
			}
		}
		player.removeMetadata("arena_attack_aura", Main.mother);
	}
}
