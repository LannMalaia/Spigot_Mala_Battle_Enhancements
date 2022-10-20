package Laylia.BE.Axe;

import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.api.ExperienceAPI;

public class Axe_Mastery
{
	// ���� ���ݷ� �Ҵ�
	public static double Set_Damage(Player player, EntityDamageByEntityEvent event)
	{
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Manager_Axe_Ability.Is_Axe(handitem))
			return event.getDamage();
		
		double damage = event.getDamage();
		double mastery_dmg = 0.0;
		double tiger_power_dmg = 0.0;
		double double_edge_per = 0.0;
		
		mastery_dmg = Manager_Axe_Ability.Instance.Get_Data(player, Manager_Axe_Ability.key_mastery_dmg);
		tiger_power_dmg = Manager_Axe_Ability.Instance.Get_Data(player, Manager_Axe_Ability.key_tiger_power_power);
		double_edge_per = Manager_Axe_Ability.Instance.Get_Data(player, Manager_Axe_Ability.key_double_edge_per);
		
		// �����͸�
		if((player.hasPermission(Manager_Axe_Ability.perm_mastery) || player.hasPermission("*")))
			damage += mastery_dmg;

		// ȣ���� ���
		if((player.hasPermission(Manager_Axe_Ability.perm_tiger_power) || player.hasPermission("*")))
		{
			PotionEffect effect = player.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
			if(effect != null)
				damage += (effect.getAmplifier() + 1) * tiger_power_dmg;
		}

		// �����ΰ�
		if((player.hasPermission(Manager_Axe_Ability.perm_double_edge) || player.hasPermission("*")))
		{
			double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(); // �ִ� ����
			double min = player.getHealth(); // ���� ����
			double health_percent = (1.0 - Math.max(0.25, min / max)) * (1.0 / 0.75); // ������� 100%�� �� 0, 25%�� �� 1�� �Ǵ� ���� ��ġ
			
			double percentage = health_percent * double_edge_per * 0.01d;
			damage += mastery_dmg * percentage;
			// Bukkit.broadcastMessage(percentage + " // " + min + " // " + max + " // " + damage);
		}
		
		//player.sendMessage(ChatColor.RED + "�� �������� "+ damage);
		return damage;
	}
	// ���� ���ݷ� �Ҵ�
	public static double Get_Damage(Player player)
	{
		// ��ų ������ �Ŵ��� �̸��� �ʹ� ��ϱ� ����ȭ
		
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Manager_Axe_Ability.Is_Axe(handitem))
			return 0.0;
		
		double damage = 0.0;
		double mastery_dmg = 0.0;
		double tiger_power_dmg = 0.0;
		double double_edge_per = 0.0;
		
		mastery_dmg = Manager_Axe_Ability.Instance.Get_Data(player, Manager_Axe_Ability.key_mastery_dmg);
		tiger_power_dmg = Manager_Axe_Ability.Instance.Get_Data(player, Manager_Axe_Ability.key_tiger_power_power);
		double_edge_per = Manager_Axe_Ability.Instance.Get_Data(player, Manager_Axe_Ability.key_double_edge_per);
		
		// �����͸�
		if((player.hasPermission(Manager_Axe_Ability.perm_mastery) || player.hasPermission("*")))
			damage += mastery_dmg;

		// ȣ���� ���
		if((player.hasPermission(Manager_Axe_Ability.perm_tiger_power) || player.hasPermission("*")))
		{
			PotionEffect effect = player.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
			if(effect != null)
				damage += (effect.getAmplifier() + 1) * tiger_power_dmg;
		}
		
		// �����ΰ�
		if((player.hasPermission(Manager_Axe_Ability.perm_double_edge) || player.hasPermission("*")))
		{
			double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(); // �ִ� ����
			double min = player.getHealth(); // ���� ����
			double health_percent = (1.0 - Math.max(0.25, min / max)) * (1.0 / 0.75); // ������� 100%�� �� 0, 25%�� �� 1�� �Ǵ� ���� ��ġ
			
			double percentage = health_percent * double_edge_per * 0.01d;
			damage += mastery_dmg * percentage;
			// Bukkit.broadcastMessage(percentage + " // " + min + " // " + max + " // " + damage);
		}
		
		//player.sendMessage(ChatColor.RED + "�� �������� "+ damage);
		return damage;
	}

	public static void Add_Axe_Buf(Player player)
	{
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Manager_Axe_Ability.Is_Axe(handitem))
			return;
		
		int level = ExperienceAPI.getLevel(player, "axes");
		if((player.hasPermission(Manager_Axe_Ability.perm_tiger_power) && player.hasPermission(Manager_Axe_Ability.perm_rage) && level >= 3000) || player.hasPermission("*"));
		else
			return;
		
		PotionEffect power, regen;
		power = player.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
		regen = player.getPotionEffect(PotionEffectType.REGENERATION);
		
		if(power != null)
		{
			if (power.getAmplifier() < 3)
			{
				power = new PotionEffect(power.getType(),
						Math.min(20 * 20, power.getAmplifier() == 2 ? 20 * 20 : power.getDuration() + 20 * 6),
						Math.min(3, power.getAmplifier() + 1));
				player.addPotionEffect(power);
			}
		}
		if(regen != null)
		{
			if (regen.getAmplifier() < 3)
			{
				regen = new PotionEffect(regen.getType(),
						Math.min(20 * 20, regen.getAmplifier() == 2 ? 20 * 20 : regen.getDuration() + 20 * 6),
						Math.min(3, regen.getAmplifier() + 1));
				player.addPotionEffect(regen);
			}
		}
	}
}
