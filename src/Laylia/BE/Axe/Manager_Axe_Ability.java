package Laylia.BE.Axe;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

import Laylia.BE.Base.Manager_Ability_Base;
import Laylia.BE.Base.TIMER_TYPE;
import Laylia.BE.Base.Timer_Data;
import Laylia.BE.Main.Main;
import Laylia.BE.Main.Manager_Timer;

/**
 * @author jimja
 * @version 2020. 5. 6.
 * @apiNote 부술 어빌리티 매니저
 * 부술 관련해서는 얘가 다 가지고 있다고 봐도 됨
 */
public class Manager_Axe_Ability extends Manager_Ability_Base
{
	public static Manager_Axe_Ability Instance;
	
	// 스킬 데이터 키
	public static final String key_mastery_dmg = "axe.key_mastery_dmg";
	public static final String key_double_edge_per = "axe.key_double_edge_per";
	public static final String key_tiger_power_power = "axe.key_tiger_power_power";
	public static final String key_rage_buf_amp = "axe.key_rage_buf_amp";

	// 타이머를 사용하는 스킬
	public static final String rage_cooldown = "axe.rage_cooldown";
	public static final String rage_ready = "axe.rage_ready";

	// 권한
	public static final String perm_mastery = "battleEnhancements.mcmmo.axes.mastery";
	public static final String perm_double_edge = "battleEnhancements.mcmmo.axes.double_edge";
	public static final String perm_tiger_power = "battleEnhancements.mcmmo.axes.tiger_power";
	public static final String perm_rage = "battleEnhancements.mcmmo.axes.rage";

	public Manager_Axe_Ability()
	{
		Bukkit.getConsoleSender().sendMessage("도끼 어빌리티 활성화!");
		
		// 싱글턴
		Instance = this;
		
		// 이벤트 등록
		Bukkit.getPluginManager().registerEvents(new Axe_Event(),  Main.mother);
	}

	@Override
	public double Get_Data(Player _player, String _key)
	{
		int axe_level = mcMMO.getDatabaseManager().loadPlayerProfile(_player.getUniqueId()).getSkillLevel(PrimarySkillType.AXES);
		switch (_key)
		{
		case key_mastery_dmg:
			return Math.min(25, axe_level / 120);
		case key_double_edge_per:
			return Math.min(120, 60 + axe_level / 70);
		case key_tiger_power_power:
			return Math.min(12, 1 + axe_level / 400);
		case key_rage_buf_amp:
			return Math.min(4, 1 + axe_level / 1000);
		}
		return 0;
	}
	
	@Override
	protected String Make_Description(Player _player)
	{
		// 퍼미션 체크
		boolean has_mastery = _player.hasPermission(perm_mastery);
		boolean has_double_edge = _player.hasPermission(perm_double_edge);
		boolean has_tiger_power = _player.hasPermission(perm_tiger_power);
		boolean has_rage = _player.hasPermission(perm_rage);
		
		// mcmmo 기본 수치 획득
		int skill_level = ExperienceAPI.getLevel(_player, "axes");
		int skill_exp = ExperienceAPI.getXP(_player, "axes");
		int skill_need_exp = ExperienceAPI.getXPToNextLevel(_player, "axes");

		// 스킬 수치 획득
		int mastery_dmg = 0, double_edge_per = 0, tiger_power_power = 0, rage_buf_amp = 0;
		mastery_dmg = (int)Get_Data(_player, key_mastery_dmg);
		double_edge_per = (int)Get_Data(_player, key_double_edge_per);
		tiger_power_power = (int)Get_Data(_player, key_tiger_power_power);
		rage_buf_amp = (int)Get_Data(_player, key_rage_buf_amp);
		
		String temp = "";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "Axes" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.GRAY + "경험치를 얻는 방법 : " + ChatColor.WHITE + "몬스터 공격하기" + "\n";
		temp += ChatColor.GRAY + "레벨 : " + skill_level + " " + ChatColor.DARK_AQUA + "경험치 : " + ChatColor.YELLOW + "(" + ChatColor.GOLD + skill_exp + "/" + skill_need_exp + ChatColor.YELLOW + ")" + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "효과" + ChatColor.RED + "[]-----" + "\n";
		temp +=  ChatColor.DARK_AQUA + "도끼 마스터리 : " + ChatColor.GREEN + "도끼 스킬, 공격시 추가 피해 적용" + (has_mastery ? "" : ChatColor.GRAY  + "(미습득)")  + "\n";
		temp +=  ChatColor.DARK_AQUA + "다중인격 : " + ChatColor.GREEN + "자신의 생명력이 낮은 만큼 도끼 마스터리 효과 증가" + (has_double_edge ? "" : ChatColor.GRAY  + "(미습득)")  +  "\n";
		temp +=  ChatColor.DARK_AQUA + "호랑이 기운 : " + ChatColor.GREEN + "힘 버프에 비례해 추가 피해 적용" + (has_tiger_power ? "" : ChatColor.GRAY  + "(미습득)")  + "\n";
		temp +=  ChatColor.DARK_AQUA + "광폭화(능력) : " + ChatColor.GREEN + "힘, 재생 버프 획득" + (has_rage ? "" : ChatColor.GRAY  + "(미습득)")  + "\n";
		temp +=  ChatColor.DARK_AQUA + " └파괴전차 : " + ChatColor.GREEN + "힘, 재생 버프를 가졌을 때 적을 물리치면 버프 갱신" + (has_rage && has_tiger_power && skill_level >= 3000 ? "" : ChatColor.GRAY  + "(미습득)")  + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "스탯" + ChatColor.RED + "[]-----" + "\n";
		temp +=  ChatColor.RED + "도끼 마스터리 : " + ChatColor.YELLOW + "도끼를 들었을 때 모든 스킬, 공격에 " + mastery_dmg + " 만큼의 추가 피해" + "\n";
		temp +=  ChatColor.RED + "다중인격 : " + ChatColor.YELLOW + "생명력이 25% 남았을 때 마스터리의 " + double_edge_per + "% 만큼의 추가 피해" + "\n";
		temp +=  ChatColor.RED + "호랑이 기운 : " + ChatColor.YELLOW + "힘 버프 * " + tiger_power_power + " 만큼의 추가 피해" + "\n";
		temp +=  ChatColor.RED + "광폭화 : " + ChatColor.YELLOW + "쿨타임 30초, 힘, 재생 버프 " + rage_buf_amp + "단계" + "\n";
		temp +=  ChatColor.RED + " └파괴전차 : " + ( has_rage && has_tiger_power && skill_level >= 3000 ? ChatColor.YELLOW + "처치할 때마다 1단계, 6초 연장(최대 4단계, 20초)"
												: ChatColor.GRAY + "호랑이 기운, 광폭화를 배우고 레벨 3000 이상이 되면 습득") + "\n";
		
		return temp;
	}

	// 이게 도끼여?
	public static boolean Is_Axe(Material _material)
	{
		switch(_material)
		{
		case WOODEN_AXE:
		case STONE_AXE:
		case IRON_AXE:
		case GOLDEN_AXE:
		case DIAMOND_AXE:
		case NETHERITE_AXE:
			return true;
		default:
			break;
		}
		return false;
	}
	
	// 도끼 스킬 준비 (쉬프트 좌클)
	public static void Axe_Active_Ready(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		Material handitem = player.getInventory().getItemInMainHand().getType();
		
		// 권한 체크
		if(!(player.hasPermission(perm_rage) || player.hasPermission("*")))
			return;
		
		// 도끼를 들고 있는지, 웅크린 상태인지, 비행중은 아닌지를 확인
		if(!Is_Axe(handitem) || !player.isSneaking() || player.isFlying() || !player.isOnGround())
			return;
				
		// 쿨타임에 해당되는 경우 (쿨타임이라면 자동으로 플레이어에게 고지)
		if(mt.Is_Timer_Available(player, rage_cooldown, true))
			return;
		
		// 스킬 준비중이지 않은 경우
		if(!mt.Is_Timer_Available(player, rage_ready, false))
		{
			// 준비
			mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_READY, player, "광폭화", rage_ready, 20 * 3));
			return;
		}
		else
		{
			Axe_Active_Use(player, false);
		}
	}

	// 도끼 스킬 사용
	public static void Axe_Active_Use(Player player, boolean _is_jump)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		
		// 권한 체크
		if(!(player.hasPermission(perm_rage) || player.hasPermission("*")))
			return;
		
		// 활을 들고 있는지, 비행중은 아닌지를 확인
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Is_Axe(handitem) || player.isFlying())
			return;
				
		// 준비중이지 않은 경우를 체크
		if(!mt.Is_Timer_Available(player, rage_ready, false))
			return;

		/*
		 * 진정한 스킬 사용 시작
		 */

		// 버프 수준 취득
		int buf_amp = 0;
		buf_amp = (int) Manager_Axe_Ability.Instance.Get_Data(player, key_rage_buf_amp);
		
		// 쿨타임
		mt.Remove_Timer(player, rage_ready); // 준비 타이머 제거
		mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_COOLDOWN, player, "광폭화", rage_cooldown, 20 * 20));  // 쿨타임 설정
		player.sendMessage(ChatColor.RED + "[ 광폭화 발동 ]");
		
		player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().clone().add(0,1,0), 150, 0d, 0d, 0d, 0.2d);

		// 점프!
		if(_is_jump)
		{
			player.setMetadata("BE_Mcmmo_Axe_Skill_Stomp", new FixedMetadataValue(Main.mother, true));
			player.setVelocity(player.getEyeLocation().getDirection().clone().add(new Vector(0d, 1.5d, 0d)));
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 2f, 2f);
		}
		
		// 버프 부여
		if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			player.sendMessage(ChatColor.GRAY + "[ 힘 버프로 인해 버프 효과가 취소됩니다. ]");
		else if(player.hasPotionEffect(PotionEffectType.WEAKNESS))
			player.sendMessage(ChatColor.GRAY + "[ 약화 버프로 인해 버프 효과를 받을 수 없습니다! ]");
		else
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, (_is_jump ? 0 : 1)));

		if(player.hasPotionEffect(PotionEffectType.REGENERATION))
			player.sendMessage(ChatColor.GRAY + "[ 재생 버프로 인해 버프 효과가 취소됩니다. ]");
		else if(player.hasPotionEffect(PotionEffectType.POISON))
			player.sendMessage(ChatColor.GRAY + "[ 독 버프로 인해 버프 효과를 받을 수 없습니다! ]");
		else if(player.hasPotionEffect(PotionEffectType.WITHER))
			player.sendMessage(ChatColor.GRAY + "[ 위더 버프로 인해 버프 효과를 받을 수 없습니다! ]");
		else
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, (_is_jump ? 0 : 1)));
	}

	public static void Axe_Stomp(Player player)
	{
		// double power = ExperienceAPI.getLevel(player, "axes") / 1000d;
		double radius = Math.min(10, ExperienceAPI.getLevel(player, "axes") / 300d);

		player.removeMetadata("BE_Mcmmo_Axe_Skill_Stomp", Main.mother);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);

		for(float i = 0f; i < Math.PI * 2f; i += Math.PI / 90f)
		{
			Location loc = player.getLocation().clone();
			loc.add(Math.sin(i) * radius, 0.2f, Math.cos(i) * radius);
			loc.getWorld().spawnParticle(Particle.LAVA, loc, 1, 0d, 0d, 0d, 0d);
		}
		
		for(Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius))
		{
			if(e instanceof Player) continue;
			if(e instanceof Creature)
			{
				if(e.isOnGround())
				{
					Creature d = (Creature)e;
					Vector vec = d.getLocation().clone().subtract(player.getLocation()).toVector();
					d.setVelocity(new Vector(vec.getX() * 0.1, 0.9, vec.getZ() * 0.1));
					d.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 1));
				}
			}
		}
		
	}

}












