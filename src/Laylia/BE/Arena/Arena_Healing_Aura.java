package Laylia.BE.Arena;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;

import Laylia.BE.Main.Main;

public class Arena_Healing_Aura
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
						if(lores.get(count).matches(".*" + "힐링 오오라" + ".*"))
						{
							// 오오라 실행
							if(player.getMetadata("arena_healing_aura").size() == 0)
							{
								player.setMetadata("arena_healing_aura", new FixedMetadataValue(Main.mother, true));
								Bukkit.getScheduler().runTaskLater(Main.mother, new Healing_Aura(player), 10);
							}
						}
					}
				}
			}
		}
	}

}

class Healing_Aura implements Runnable
{
	Player player;
	public Healing_Aura(Player p)
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
						if(lores.get(count).matches(".*" + "힐링 오오라" + ".*"))
						{
							// 오오라 발동
							double radius = 6;
							for(double theta = 0; theta < Math.PI * 2d; theta += 0.05)
							{
								Location loc = player.getLocation();
								loc.add(radius * Math.cos(theta), 1, radius * Math.sin(theta));
								player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 1, 1.0d, 0d, 0d, 0d);
								
							}
							
							// 회복 적용
							Collection<Entity> temp = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius);
							for(Entity temp2 : temp)
							{
								if(temp2 instanceof HumanEntity)
								{
									Player p = (Player)temp2;
									p.setHealth(Math.min(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), p.getHealth() + 2));
								}
								if(temp2 instanceof Zombie)
								{
									((Zombie) temp2).damage(2);
								}
								if(temp2 instanceof Skeleton)
								{
									((Skeleton) temp2).damage(2);
								}
							}
							
							Bukkit.getScheduler().runTaskLater(Main.mother, new Healing_Aura(player), 15);
							return;
						}
					}
				}
			}
		}
		player.removeMetadata("arena_healing_aura", Main.mother);
	}
}
