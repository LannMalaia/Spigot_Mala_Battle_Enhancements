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
	 * @apiNote �˼� ���ط� ����
	 * @param player
	 * @param event
	 * @return
	 */
	public static double Set_Damage(Player player, EntityDamageByEntityEvent event)
	{		
		// ���� �� �� �� �¼�?
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Manager_Sword_Ability.Is_Sword(handitem))
			return event.getDamage();
		
		// ���� �ּ�?
		if(!(player.hasPermission(Manager_Sword_Ability.perm_mastery) || player.hasPermission("*")))
			return event.getDamage();
		
		// ���ط��� ���սô�
		double damage = event.getDamage();
		
		// ��ų ������ �Ŵ��� �̸��� �ʹ� ��ϱ� ����ȭ
		Manager_Sword_Ability msa = Manager_Sword_Ability.Instance;

		// �⺻ ���ط��� �ҵ� �����͸� ���ط��� ���Ұ� �̰� �⺻������
		damage += msa.Get_Data(player, Manager_Sword_Ability.key_mastery_dmg);

		// ���� ���� ���� �ִ� �� ���ô�?
		if(player.hasPermission(Manager_Sword_Ability.perm_critical) || player.hasPermission("*"))
		{
			// ���� �ִٸ� Ȯ���� ����
			int counter_percent = (int)msa.Get_Data(player, Manager_Sword_Ability.key_critical_per);
			if(Math.random() * 100 <= counter_percent)
			{
				// ���� ���ݿ� ������ ���
				// ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "[ ���� ������ �����ߴ�! ]");
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
				player.getWorld().spawnParticle(Particle.FLASH, event.getEntity().getLocation().clone().add(0, 1, 0),
						2, 0d, 0d, 0d, 0d);
				// ���ط� ����� ����
				damage *= msa.Get_Data(player, Manager_Sword_Ability.key_critical_dmgper) / 100.0d;

				// �ݰ� ��Ž 3�� ����
				Manager_Timer.Get_Instance().Sub_Timer(player, Manager_Sword_Ability.key_counter_cool, 20 * 3);
			}
		}
		
		return damage;
	}
	public static double Get_Damage(Player player, LivingEntity target)
	{		
		// ���� �� �� �� �¼�?
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Manager_Sword_Ability.Is_Sword(handitem))
			return 0;
		
		// ���� �ּ�?
		if(!(player.hasPermission(Manager_Sword_Ability.perm_mastery) || player.hasPermission("*")))
			return 0;
		
		// ���ط��� ���սô�
		double damage = 0;
		
		// ��ų ������ �Ŵ��� �̸��� �ʹ� ��ϱ� ����ȭ
		Manager_Sword_Ability msa = Manager_Sword_Ability.Instance;

		// �⺻ ���ط��� �ҵ� �����͸� ���ط��� ���Ұ� �̰� �⺻������
		damage += msa.Get_Data(player, Manager_Sword_Ability.key_mastery_dmg);

		// ���� ���� ���� �ִ� �� ���ô�?
		if(player.hasPermission(Manager_Sword_Ability.perm_critical) || player.hasPermission("*"))
		{
			// ���� �ִٸ� Ȯ���� ����
			int counter_percent = (int)msa.Get_Data(player, Manager_Sword_Ability.key_critical_per);
			if(Math.random() * 100 <= counter_percent)
			{
				// ���� ���ݿ� ������ ���
				// player.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "[ ���� ������ �����ߴ�! ]");
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2f);
				player.getWorld().spawnParticle(Particle.FLASH, target.getLocation().clone().add(0, 1, 0),
						2, 0d, 0d, 0d, 0d);
				// ���ط� ����� ����
				damage *= msa.Get_Data(player, Manager_Sword_Ability.key_critical_dmgper) / 100.0d;

				// �ݰ� ��Ž 3�� ����
				Manager_Timer.Get_Instance().Sub_Timer(player, Manager_Sword_Ability.key_counter_cool, 20 * 3);
			}
		}
		
		return damage;
	}

	/**
	 * @apiNote �ݰ� ���
	 * @param event
	 */
	public static void Counter(EntityDamageByEntityEvent event)
	{
		Player player = (Player)event.getEntity();
		
		// ���� �������� ���� �� �¼�?
		if(event.getCause() != DamageCause.ENTITY_ATTACK)
			return;
		
		// ���� �� ����� ��ũ�ȼ�?
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Manager_Sword_Ability.Is_Sword(handitem) || !player.isSneaking())
			return;
		
		// ���� �ּ�?
		if(!(player.hasPermission(Manager_Sword_Ability.perm_counter) || player.hasPermission("*")))
			return;
		
		// ��Ÿ���̿�?
		if(Manager_Timer.Get_Instance().Is_Timer_Available(player, Manager_Sword_Ability.counter_cooldown, true))
			return;
		
		// ��ä�� ���� �������� ���� ���ֺ��� �ִ��� Ȯ��!
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
		
		// �����ڰ� �÷��̾��� ��� �ݰݴ��ߴٴ� �޽����� �����ش�
		if(damager instanceof Player)
			damager.sendMessage("" + ChatColor.DARK_RED + ChatColor.BOLD + "[ �ݰݴ��ߴ�! ]");
		
		// �ݰ��ڿ��� �ݰ��߾�~ �����ְ�
		player.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "[ �ݰ��ߴ�! ]");
		loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0d, 0d, 0d, 0d);
		loc.getWorld().spawnParticle(Particle.FLASH, loc, 1, 0d, 0d, 0d, 0d);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f, 1);
		
		// ��Ÿ�� ����
		Manager_Timer.Get_Instance().Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_COOLDOWN, player, "�ݰ�",
				Manager_Sword_Ability.counter_cooldown,
				(long)Manager_Sword_Ability.Instance.Get_Data(player, Manager_Sword_Ability.key_counter_cool) * 20));
		
		// �����ڿ��� ���� �ֱ�
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
								if(lores.get(count).contains("[��ų��ȭ] �������"))
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
								if(lores.get(count).contains("[��ų��ȭ] �ݰ�"))
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
