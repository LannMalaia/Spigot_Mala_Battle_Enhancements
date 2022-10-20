package Laylia.BE.Crossbow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

import Laylia.BE.Main.Main;
import io.lumine.mythic.lib.damage.DamageType;
import laylia_core.main.Damage;

public class Crossbow_Mastery
{
	// ���Ÿ� ���� �߻�
	public static void Arrow_Add_Damage_Meta(Player _player, Projectile _projectile, float _force)
	{
		int mastery_damage = 0;
		_projectile.setMetadata("be.crossbow.arrow", new FixedMetadataValue(Main.mother, mastery_damage));
		
		// �����͸� ȿ��
		mastery_damage += _force * (int)Manager_Crossbow_Ability.Instance.Get_Data(_player, Manager_Crossbow_Ability.key_mastery_range_dmg);
		_projectile.setMetadata("be.crossbow.mastery_dmg", new FixedMetadataValue(Main.mother, mastery_damage));
		
	}

	// ���� ���ݽ�
	public static double Set_Melee_Damage(Player player, EntityDamageByEntityEvent event)
	{
		// ���� �� �� �¼�?
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if (!Manager_Crossbow_Ability.Is_Crossbow(handitem))
			return event.getDamage();
		
		// ���� �ּ�?
		if(!(player.hasPermission(Manager_Crossbow_Ability.perm_mastery) || player.hasPermission("*")))
			return event.getDamage();

		// ���ط��� ���սô�
		double damage = event.getDamage();

		// ���� �����͸� ������ �߰�
		damage += (int) Manager_Crossbow_Ability.Instance.Get_Data(player, Manager_Crossbow_Ability.key_mastery_melee_dmg);

		return damage;	
	}
	
	// ���Ÿ� ���� ��Ʈ
	public static void When_Hit_Arrow_On_Entity(Player player, EntityDamageByEntityEvent event)
	{
		Arrow arrow = (Arrow)event.getDamager();
		if(arrow.getShooter() instanceof Player && event.getEntity() instanceof LivingEntity)
		{
			double damage = event.getDamage();

			// �������� �� ȭ���� �ƴϸ� ��ŵ
			if(!arrow.hasMetadata("be.crossbow.arrow"))
				return;
			
			if(arrow.hasMetadata("be.crossbow.mastery_dmg"))
				arrow.setKnockbackStrength(arrow.getKnockbackStrength() / 2);

			// �����͸� ȿ��
			if(arrow.hasMetadata("be.crossbow.mastery_dmg"))
				damage += arrow.getMetadata("be.crossbow.mastery_dmg").get(0).asInt();
			
			// ī���� ȿ��
			// ���ϸ鼭 �ڵ����� ī���� ������ ���ϰ�, ����� �ٸ��� �ʱ�ȭ�ȴ�
			if (Crossbow_Counting_Manager.Instance.Add_Player_Counting(player, (LivingEntity)event.getEntity()))
			{
				// ������ �� á�� ��쿡�� �˾Ƽ� ���ŵ�
				Bukkit.getScheduler().runTask(Main.mother, () ->
				{
					double cDamage = Manager_Crossbow_Ability.Instance.Get_Data(player, Manager_Crossbow_Ability.key_counting_shot_dmg);
					Damage.Attack(player, (LivingEntity)event.getEntity(), cDamage, DamageType.WEAPON, DamageType.PROJECTILE);
					event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 1.5f);
					event.getEntity().getWorld().spawnParticle(Particle.FIREWORKS_SPARK,
							event.getEntity().getLocation().add(0d, event.getEntity().getHeight() * 0.5d , 0d), 20, 0.0D, 0.0D, 0.0D, 1.0D);					
				
					if(ExperienceAPI.getLevel(player, PrimarySkillType.ARCHERY) >= 500)
					{
						// ����
						if(player.hasPotionEffect(PotionEffectType.SPEED))
							player.sendMessage(ChatColor.GRAY + "[ �ӵ� ���� ������ ���� ���� ȿ���� ��ҵ˴ϴ�. ]");
						else if(player.hasPotionEffect(PotionEffectType.SLOW))
							player.sendMessage(ChatColor.GRAY + "[ �ӵ� ���� ������ ���� ���� ȿ���� ���� �� �����ϴ�! ]");
						else
							player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1));
					}
				});
				// event.getEntity().getWorld().spawnParticle(Particle.FLASH,
				//		event.getEntity().getLocation().add(0d, event.getEntity().getHeight() * 0.5d , 0d), 1, 0.0D, 0.0D, 0.0D, 0.0D);
				
			}
			
			event.setDamage(damage);
		}
	}
}
