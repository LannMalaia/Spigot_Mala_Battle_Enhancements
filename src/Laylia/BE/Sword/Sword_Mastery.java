package Laylia.BE.Sword;

import java.util.ArrayList;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Laylia.BE.Base.TIMER_TYPE;
import Laylia.BE.Base.Timer_Data;
import Laylia.BE.Main.Manager_Timer;
import io.lumine.mythic.lib.damage.DamageType;
import laylia_core.main.Damage;

public class Sword_Mastery
{
	/**
	 * @apiNote 검술 피해량 설정
	 * @param player
	 * @param event
	 * @return
	 */
	public static double Set_Damage(Player player, EntityDamageByEntityEvent event)
	{		
		// 무기 검 든 거 맞소?
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Manager_Sword_Ability.Is_Sword(handitem))
			return event.getDamage();
		
		// 권한 있소?
		if(!(player.hasPermission(Manager_Sword_Ability.perm_mastery) || player.hasPermission("*")))
			return event.getDamage();
		
		// 피해량을 정합시다
		double damage = event.getDamage();
		
		// 스킬 데이터 매니저 이름이 너무 기니까 변수화
		Manager_Sword_Ability msa = Manager_Sword_Ability.Instance;

		// 기본 피해량에 소드 마스터리 피해량을 더할게 이건 기본사항임
		damage += msa.Get_Data(player, Manager_Sword_Ability.key_mastery_dmg);

		// 약점 공격 권한 있는 지 봅시다?
		if(player.hasPermission(Manager_Sword_Ability.perm_critical) || player.hasPermission("*"))
		{
			// 권한 있다면 확률을 얻자
			int counter_percent = (int)msa.Get_Data(player, Manager_Sword_Ability.key_critical_per);
			if(Math.random() * 100 <= counter_percent)
			{
				// 약점 공격에 성공한 경우
				// ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "[ 적의 약점을 공격했다! ]");
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
				player.getWorld().spawnParticle(Particle.FLASH, event.getEntity().getLocation().clone().add(0, 1, 0),
						2, 0d, 0d, 0d, 0d);
				// 피해량 요로케 조절
				damage *= msa.Get_Data(player, Manager_Sword_Ability.key_critical_dmgper) / 100.0d;

				// 반격 쿨탐 3초 감소
				Manager_Timer.Get_Instance().Sub_Timer(player, Manager_Sword_Ability.key_counter_cool, 20 * 3);
			}
		}
		
		return damage;
	}
	public static double Get_Damage(Player player, LivingEntity target)
	{		
		// 무기 검 든 거 맞소?
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Manager_Sword_Ability.Is_Sword(handitem))
			return 0;
		
		// 권한 있소?
		if(!(player.hasPermission(Manager_Sword_Ability.perm_mastery) || player.hasPermission("*")))
			return 0;
		
		// 피해량을 정합시다
		double damage = 0;
		
		// 스킬 데이터 매니저 이름이 너무 기니까 변수화
		Manager_Sword_Ability msa = Manager_Sword_Ability.Instance;

		// 기본 피해량에 소드 마스터리 피해량을 더할게 이건 기본사항임
		damage += msa.Get_Data(player, Manager_Sword_Ability.key_mastery_dmg);

		// 약점 공격 권한 있는 지 봅시다?
		if(player.hasPermission(Manager_Sword_Ability.perm_critical) || player.hasPermission("*"))
		{
			// 권한 있다면 확률을 얻자
			int counter_percent = (int)msa.Get_Data(player, Manager_Sword_Ability.key_critical_per);
			if(Math.random() * 100 <= counter_percent)
			{
				// 약점 공격에 성공한 경우
				// player.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "[ 적의 약점을 공격했다! ]");
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2f);
				player.getWorld().spawnParticle(Particle.FLASH, target.getLocation().clone().add(0, 1, 0),
						2, 0d, 0d, 0d, 0d);
				// 피해량 요로케 조절
				damage *= msa.Get_Data(player, Manager_Sword_Ability.key_critical_dmgper) / 100.0d;

				// 반격 쿨탐 3초 감소
				Manager_Timer.Get_Instance().Sub_Timer(player, Manager_Sword_Ability.key_counter_cool, 20 * 3);
			}
		}
		
		return damage;
	}

	/**
	 * @apiNote 반격 기능
	 * @param event
	 */
	public static void Counter(EntityDamageByEntityEvent event)
	{
		Player player = (Player)event.getEntity();
		
		// 직접 공격으로 맞은 거 맞소?
		if(event.getCause() != DamageCause.ENTITY_ATTACK)
			return;
		
		// 무기 검 들었고 웅크렸소?
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Manager_Sword_Ability.Is_Sword(handitem) || !player.isSneaking())
			return;
		
		// 권한 있소?
		if(!(player.hasPermission(Manager_Sword_Ability.perm_counter) || player.hasPermission("*")))
			return;
		
		// 쿨타임이요?
		if(Manager_Timer.Get_Instance().Is_Timer_Available(player, Manager_Sword_Ability.counter_cooldown, true))
			return;
		
		// 부채꼴 넓이 판정으로 서로 마주보고 있는지 확인!
		double player_counter_angle = player.getLocation().getYaw();
		double damager_angle = event.getDamager().getLocation().getYaw();
		double angle_range = 60;
		double angle_max = 0, angle_low = 0;
		boolean counter_ok = false;

		if(player_counter_angle < 0)
			player_counter_angle += 360;
		if(damager_angle < 0)
			damager_angle += 360;

		double calcul = player_counter_angle + 180 + angle_range;
		angle_max = calcul > 360 ? calcul - 360 : calcul;
		calcul = player_counter_angle - 180 - angle_range;
		angle_low = calcul < 0 ? calcul + 360 : calcul;
		
		if(angle_max < angle_low)
			counter_ok = damager_angle < angle_max || damager_angle > angle_low;
		else
			counter_ok = damager_angle < angle_max && damager_angle > angle_low;
		
		if(!counter_ok)
			return;
		
		LivingEntity damager = (LivingEntity)event.getDamager();
		Location loc = player.getLocation();
		loc.add(0, 1, 0);
		
		// 공격자가 플레이어인 경우 반격당했다는 메시지를 보내준다
		if(damager instanceof Player)
			damager.sendMessage("" + ChatColor.DARK_RED + ChatColor.BOLD + "[ 반격당했다! ]");
		
		// 반격자에게 반격했어~ 보내주고
		player.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "[ 반격했다! ]");
		loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0d, 0d, 0d, 0d);
		loc.getWorld().spawnParticle(Particle.FLASH, loc, 1, 0d, 0d, 0d, 0d);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f, 1);
		
		// 쿨타임 설정
		Manager_Timer.Get_Instance().Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_COOLDOWN, player, "반격",
				Manager_Sword_Ability.counter_cooldown,
				(long)Manager_Sword_Ability.Instance.Get_Data(player, Manager_Sword_Ability.key_counter_cool) * 20));
		
		// 공격자에게 피해 주기
		double damage = Manager_Sword_Ability.Instance.Get_Data(player, Manager_Sword_Ability.key_counter_dmg);

		Damage.Attack(player, damager, damage, DamageType.SKILL, DamageType.PHYSICAL);
		loc.getWorld().spawnParticle(Particle.CRIT, damager.getLocation().clone().add(0, 1, 0), 20, 0.5d, 0.5d, 0.5d, 0d);
		event.setCancelled(true);
	}

	public static double Check_Sword_Wave_Enchant(Player player)
	{
		double addictive_damage = 0;
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.add(player.getInventory().getArmorContents()[0]);
		items.add(player.getInventory().getArmorContents()[1]);
		items.add(player.getInventory().getArmorContents()[2]);
		items.add(player.getInventory().getArmorContents()[3]);
		items.add(player.getInventory().getItemInMainHand());
		items.add(player.getInventory().getItemInOffHand());
		for(ItemStack item : items)
		{
			if(item != null)
			{
				if(item.hasItemMeta())
				{
					ItemMeta meta = item.getItemMeta();
					if(meta.hasLore())
					{
						List<String> lores = meta.getLore();
						if(lores != null)
						{
							for(int count = 0; count < lores.size(); count++)
							{
								if(lores.get(count).contains("[스킬강화] 베어가르기"))
								{
									if(lores.get(count).contains("III"))
									{
										addictive_damage += 6;
									}
									else if(lores.get(count).contains("II"))
									{
										addictive_damage += 4;
									}
									else if(lores.get(count).contains("I"))
									{
										addictive_damage += 2;
									}
								}
							}
						}
					}
				}
			}
		}
		return addictive_damage;
	}

	public static double Check_Sword_Counter_Enchant(Player player)
	{
		double addictive_chance = 0;
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.add(player.getInventory().getArmorContents()[0]);
		items.add(player.getInventory().getArmorContents()[1]);
		items.add(player.getInventory().getArmorContents()[2]);
		items.add(player.getInventory().getArmorContents()[3]);
		items.add(player.getInventory().getItemInMainHand());
		items.add(player.getInventory().getItemInOffHand());
		for(ItemStack item : items)
		{
			if(item != null)
			{
				if(item.hasItemMeta())
				{
					ItemMeta meta = item.getItemMeta();
					if(meta.hasLore())
					{
						List<String> lores = meta.getLore();
						if(lores != null)
						{
							for(int count = 0; count < lores.size(); count++)
							{
								if(lores.get(count).contains("[스킬강화] 반격"))
								{
									String str = lores.get(count).substring(4).replaceAll("[^0-9]", "");
									addictive_chance += Double.parseDouble(str);
								}
							}
						}
					}
				}
			}
		}
		
		return addictive_chance;
	}

}
