package Laylia.BE.LegacyEnchant;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.nossr50.api.ExperienceAPI;

import Laylia.BE.Main.Main;

public class Super_Counter_Enchant
{
	public static void Super_Counter_Judge(Player player)
	{
		List<String> lores = player.getInventory().getItemInMainHand().getItemMeta().getLore();
		if(lores != null)
		{
			for(int count = 0; count < lores.size(); count++)
			{
				if(lores.get(count).matches(".*" + "슈퍼 카운터" + ".*"))
				{
					if(player.getMetadata("Super_Counter_Attack").size() != 0 || player.getMetadata("Super_Counter_Attack_Cooldown").size() != 0)
					{
						// ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "재사용 대기시간입니다.");
						return;
					}
					if(lores.get(count).contains("III"))
					{
						Super_Counter_Action(player, 3);
					}
					else if(lores.get(count).contains("II"))
					{
						Super_Counter_Action(player, 2);
					}
					else if(lores.get(count).contains("I"))
					{
						Super_Counter_Action(player, 1);
					}
				}
			}
		}
	}
	
	public static void Super_Counter_Action(Player player, int power)
	{
		// 파워에 맞춰서 이것저것 변경
		switch(power)
		{
		case 1:
			player.setMetadata("Super_Counter_Attack", new FixedMetadataValue(Main.mother, 1));
			break;
		case 2:
			player.setMetadata("Super_Counter_Attack", new FixedMetadataValue(Main.mother, 2));
			break;
		case 3:
			player.setMetadata("Super_Counter_Attack", new FixedMetadataValue(Main.mother, 3));
			break;
		}
		Main.mother.getServer().getScheduler().runTaskLater(Main.mother,
				new Super_Counter_Release(player, power), 20 + power * 40);
	}

	public static void Passive_Counter_Judge(EntityDamageByEntityEvent event)
	{
		if(event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.CUSTOM)
		{
			Player player = (Player)event.getEntity();
			if(player.getInventory().getItemInMainHand().getType() != Material.AIR);
			{
				ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
				List<String> lores = meta.getLore();
				if(lores != null)
				{
					for(int count = 0; count < lores.size(); count++)
					{
						if(lores.get(count).matches(".*" + "반격:" + ".*"))
						{
							String str = lores.get(count).substring(9).replaceAll("[^0-9]", "");
							Double chance = Double.parseDouble(str);
							
							double player_counter_angle = player.getLocation().getYaw();
							double damager_angle = event.getDamager().getLocation().getYaw();
							double angle_range = 60;
							double angle_max = 0, angle_low = 0;
							boolean counter_ok = false;

							if(player_counter_angle < 0) player_counter_angle += 360;
							if(damager_angle < 0) damager_angle += 360;

							//player.sendMessage("내 각도 : " + player_counter_angle);
							//player.sendMessage("적 각도 : " + damager_angle);
							// max보다 낮고 low보다 높아야됨

							double calcul = player_counter_angle + 180 + angle_range;
							if(calcul > 360) angle_max = calcul - 360;
							else angle_max = calcul;
							calcul = player_counter_angle - 180 - angle_range;
							if(calcul < 0) angle_low = calcul + 360;
							else angle_low = calcul;
							
							//player.sendMessage(angle_max + "이하, " + angle_low + "이상");
							
							if(angle_max < angle_low)
							{
								if(damager_angle < angle_max || damager_angle > angle_low)
								{
									counter_ok = true;
								}
							}
							else
							{
								if(damager_angle < angle_max)
								{
									if(damager_angle > angle_low)
									{
										counter_ok = true;
									}
								}
							}
							
							if(counter_ok)
							{
								//player.sendMessage("일단 범위 안에 들었어요.");
								if(Math.random() * 100 <= chance) // 반격 실행
								{
									LivingEntity damager = (LivingEntity)event.getDamager();
									Location loc = player.getLocation();
									loc.add(0, 1, 0);
									if(damager instanceof Player)
									{
										Player dmg_man = (Player)damager;
										// ActionBarAPI.sendActionBar(dmg_man, "" + ChatColor.DARK_RED + ChatColor.BOLD + "반격당했습니다!");
									}
									// ActionBarAPI.sendActionBar(player, "" + ChatColor.GREEN + ChatColor.BOLD + "반격했습니다!");
									loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0d, 0d, 0d, 0d);
									//ParticleEffect.SWEEP_ATTACK.send(Bukkit.getOnlinePlayers(), loc, 0, 0, 0, 0, 1);
									player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f, 1);
									damager.damage(event.getDamage(), player);
									damager.setHealth(damager.getHealth() - 3);
									event.setCancelled(true);
								}
							}
						}
					}
				}
			}
		}
	}

	public static void Active_Counter_Judge(EntityDamageByEntityEvent event)
	{
		if(event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.CUSTOM)
		{
			Player player = (Player)event.getEntity();
			LivingEntity damager = (LivingEntity)event.getDamager();
			if(player.getMetadata("Super_Counter_Attack").size() != 0)
			{
				if(damager.getMetadata("Super_Counter_Attack").size() == 0)
				{
					Location loc = player.getLocation();
					loc.add(0, 1, 0);
					if(damager instanceof Player)
					{
						Player dmg_man = (Player)damager;
						// ActionBarAPI.sendActionBar(dmg_man, "" + ChatColor.DARK_RED + ChatColor.BOLD + "반격당했습니다!");
					}
					// ActionBarAPI.sendActionBar(player, "" + ChatColor.GREEN + ChatColor.BOLD + "반격했습니다!");
					loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0d, 0d, 0d, 0d);
					//ParticleEffect.SWEEP_ATTACK.send(Bukkit.getOnlinePlayers(), loc, 0, 0, 0, 0, 1);
					player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f, 1);
					int sword_level = ExperienceAPI.getLevel(player, "swords");
					int damage = sword_level / 100;
					int level = player.getMetadata("Super_Counter_Attack").get(0).asInt();
					damager.damage(4 * level + Math.min(12, damage), player);
					damager.setHealth(damager.getHealth() - 3);
					event.setCancelled(true);
				}
			}
		}
	}

}

class Super_Counter_Release implements Runnable
{
	Player p;
	int power;
	
	public Super_Counter_Release(Player player, int _power)
	{
		p = player;
		power = _power;
	}
	
	public void run()
	{
		if(p.getMetadata("Super_Counter_Attack").size() != 0)
		{
			p.removeMetadata("Super_Counter_Attack", Main.mother);
			// 쿨타임 바 세팅
			Main.mother.getServer().getScheduler().runTaskLater(Main.mother,
					new Super_Counter_Cooldown(p), 200 + power * 200);
		}
	}
}

class Super_Counter_Cooldown implements Runnable
{
	Player p;
	
	public Super_Counter_Cooldown(Player player)
	{
		p = player;
	}
	
	public void run()
	{
		p.removeMetadata("Super_Counter_Attack_Cooldown", Main.mother);
	}
}















