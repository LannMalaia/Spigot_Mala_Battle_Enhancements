package Laylia.BE.Main;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;

public class MMOlib_Event implements Listener
{
	public static HashMap<LivingEntity, Set<DamageType>> AttackType_Cache = new HashMap<LivingEntity, Set<DamageType>>();
	
	public MMOlib_Event()
	{
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.mother, new MMOlib_Attack_Cache_Remover(), 100, 400); // 20초에 한 번
	}
	
	@EventHandler
	public void mala_PlayerDamage(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			// Player player = (Player)event.getEntity();
			// double minus_value = 0.02 * MMOlib_EnchantPower.Get_Protect(player);
			// event.setDamage(event.getDamage() * (1.0 - minus_value));
		}
	}
	
	@EventHandler
	public void mala_PlayerAttack(PlayerAttackEvent event)
	{
		AttackType_Cache.put(event.getEntity(), event.getDamage().collectTypes());
		
		if (event.getDamage().hasType(DamageType.WEAPON))
		{
			// 날카
			event.getDamage().multiplicativeModifier(
					1.0 + 0.03 * MMOlib_EnchantPower.Get_Sharpness(event.getPlayer()),
					DamageType.WEAPON);
			
			// 강타
			if (MythicLib.plugin.getVersion().getWrapper().isUndead(event.getEntity()))
				event.getDamage().multiplicativeModifier(
						1.0 + 0.03 * MMOlib_EnchantPower.Get_Smite(event.getPlayer()),
						DamageType.WEAPON);
			// 살충
			switch(event.getEntity().getType())
			{
			case SPIDER:
			case CAVE_SPIDER:
			case BEE:
			case SILVERFISH:
			case ENDERMITE:
				event.getDamage().multiplicativeModifier(
						1.0 + 0.04 * MMOlib_EnchantPower.Get_BaneofArthropods(event.getPlayer()),
						DamageType.WEAPON);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * @author jimja
	 * @version 2020. 5. 13.
	 * @apiNote 마법이나 스킬 공격이 아닌 일반 공격인 경우
	 * @param _entity 비교할 대상
	 * @return 마법이나 스킬이 아닐 때 true
	 */
	public static boolean Attack_Is_NormalAttack(LivingEntity _entity)
	{
		Set<DamageType> set = AttackType_Cache.get(_entity);
		if(set == null)
			return true;

		if(set.contains(DamageType.MAGIC))
			return false;
		if(set.contains(DamageType.SKILL))
			return false;
		
		return true;
	}
	public static boolean Attack_Is_NormalAttack(AttackMetadata _attack)
	{
		if(_attack.getDamage().hasType(DamageType.MAGIC)) 
			return false;
		if(_attack.getDamage().hasType(DamageType.SKILL))
			return false;
		
		return true;
	}
	
	
	public static boolean Attack_Is(DamageType _type, LivingEntity _entity)
	{
		Set<DamageType> set = AttackType_Cache.get(_entity);
		if(set == null)
			return false;
		 
		if(set.contains(_type))
			return true;
		
		return false;
	}
}

class MMOlib_Attack_Cache_Remover implements Runnable
{
	public void run()
	{
		MMOlib_Event.AttackType_Cache.clear();
	}
}