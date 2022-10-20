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
	// 원거리 공격 발사
	public static void Arrow_Add_Damage_Meta(Player _player, Projectile _projectile, float _force)
	{
		int mastery_damage = 0;
		_projectile.setMetadata("be.crossbow.arrow", new FixedMetadataValue(Main.mother, mastery_damage));
		
		// 마스터리 효과
		mastery_damage += _force * (int)Manager_Crossbow_Ability.Instance.Get_Data(_player, Manager_Crossbow_Ability.key_mastery_range_dmg);
		_projectile.setMetadata("be.crossbow.mastery_dmg", new FixedMetadataValue(Main.mother, mastery_damage));
		
	}

	// 근접 공격시
	public static double Set_Melee_Damage(Player player, EntityDamageByEntityEvent event)
	{
		// 석궁 든 거 맞소?
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if (!Manager_Crossbow_Ability.Is_Crossbow(handitem))
			return event.getDamage();
		
		// 권한 있소?
		if(!(player.hasPermission(Manager_Crossbow_Ability.perm_mastery) || player.hasPermission("*")))
			return event.getDamage();

		// 피해량을 정합시다
		double damage = event.getDamage();

		// 석궁 마스터리 데미지 추가
		damage += (int) Manager_Crossbow_Ability.Instance.Get_Data(player, Manager_Crossbow_Ability.key_mastery_melee_dmg);

		return damage;	
	}
	
	// 원거리 공격 히트
	public static void When_Hit_Arrow_On_Entity(Player player, EntityDamageByEntityEvent event)
	{
		Arrow arrow = (Arrow)event.getDamager();
		if(arrow.getShooter() instanceof Player && event.getEntity() instanceof LivingEntity)
		{
			double damage = event.getDamage();

			// 석궁으로 쏜 화살이 아니면 스킵
			if(!arrow.hasMetadata("be.crossbow.arrow"))
				return;
			
			if(arrow.hasMetadata("be.crossbow.mastery_dmg"))
				arrow.setKnockbackStrength(arrow.getKnockbackStrength() / 2);

			// 마스터리 효과
			if(arrow.hasMetadata("be.crossbow.mastery_dmg"))
				damage += arrow.getMetadata("be.crossbow.mastery_dmg").get(0).asInt();
			
			// 카운팅 효과
			// 비교하면서 자동으로 카운팅 스택을 더하고, 대상이 다르면 초기화된다
			if (Crossbow_Counting_Manager.Instance.Add_Player_Counting(player, (LivingEntity)event.getEntity()))
			{
				// 스택이 꽉 찼을 경우에는 알아서 제거됨
				Bukkit.getScheduler().runTask(Main.mother, () ->
				{
					double cDamage = Manager_Crossbow_Ability.Instance.Get_Data(player, Manager_Crossbow_Ability.key_counting_shot_dmg);
					Damage.Attack(player, (LivingEntity)event.getEntity(), cDamage, DamageType.WEAPON, DamageType.PROJECTILE);
					event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 1.5f);
					event.getEntity().getWorld().spawnParticle(Particle.FIREWORKS_SPARK,
							event.getEntity().getLocation().add(0d, event.getEntity().getHeight() * 0.5d , 0d), 20, 0.0D, 0.0D, 0.0D, 1.0D);					
				
					if(ExperienceAPI.getLevel(player, PrimarySkillType.ARCHERY) >= 500)
					{
						// 버프
						if(player.hasPotionEffect(PotionEffectType.SPEED))
							player.sendMessage(ChatColor.GRAY + "[ 속도 증가 버프로 인해 버프 효과가 취소됩니다. ]");
						else if(player.hasPotionEffect(PotionEffectType.SLOW))
							player.sendMessage(ChatColor.GRAY + "[ 속도 감소 버프로 인해 버프 효과를 받을 수 없습니다! ]");
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
