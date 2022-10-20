package Laylia.BE.Archery;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
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
import net.Indyuce.mmocore.MMOCore;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author jimja
 * @version 2020. 5. 5.
 * @apiNote 궁술 어빌리티 매니저
 * 궁술 관련해서는 얘가 다 가지고 있다고 봐도 됨
 */
public class Manager_Archery_Ability extends Manager_Ability_Base
{
	public static Manager_Archery_Ability Instance;
	
	// 스킬 데이터 키
	public static final String key_mastery_range_dmg = "archery.mastery_range_dmg";
	public static final String key_mastery_melee_dmg = "archery.mastery_melee_dmg";
	public static final String key_concentration_count = "archery.concentration_count";
	public static final String key_support_shot_dmg = "archery.key_support_shot_dmg";
	public static final String key_backstep_speed_buf = "archery.key_backstep_speed_buf";
	public static final String key_backstep_shot_count = "archery.key_backstep_shot_count";

	// 타이머를 사용하는 스킬
	public static final String backstep_cooldown = "archery.backstep_cooldown";
	public static final String backstep_ready = "archery.backstep_ready";

	// 권한
	public static final String perm_mastery = "battleEnhancements.mcmmo.archery.mastery";
	public static final String perm_concentration = "battleEnhancements.mcmmo.archery.concentration";
	public static final String perm_support_shot = "battleEnhancements.mcmmo.archery.support_shot";
	public static final String perm_backstep = "battleEnhancements.mcmmo.archery.back_step";

	public Manager_Archery_Ability()
	{
		Bukkit.getConsoleSender().sendMessage("궁술 어빌리티 활성화!");
		
		// 싱글턴
		Instance = this;
		
		// 이벤트 등록
		Bukkit.getPluginManager().registerEvents(new Archery_Event(),  Main.mother);
		
		// 기타
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.mother, new Archery_Concentrate_Manager(), 100, 5);
		
	}

	@Override
	public double Get_Data(Player _player, String _key)
	{
		int archery_level = mcMMO.getDatabaseManager().loadPlayerProfile(_player.getUniqueId()).getSkillLevel(PrimarySkillType.ARCHERY);
		switch (_key)
		{
		case key_mastery_range_dmg:
			return Math.min(30, archery_level / 200);
		case key_mastery_melee_dmg:
			return Math.min(10, archery_level / 300);
		case key_concentration_count:
			return 1 + Math.min(5, archery_level / 1000);
		case key_support_shot_dmg:
			return 4 + archery_level / 250;
		case key_backstep_speed_buf:
			return Math.min(4, 1 + archery_level / 2000);
		case key_backstep_shot_count:
			return Math.min(10, 3 + archery_level / 500);
		}
		return 0;
	}

	@Override
	protected String Make_Description(Player _player)
	{
		// 퍼미션 체크
		boolean has_mastery = _player.hasPermission(perm_mastery);
		boolean has_concentration = _player.hasPermission(perm_concentration);
		boolean has_supportshot = _player.hasPermission(perm_support_shot);
		boolean has_backstep = _player.hasPermission(perm_backstep);
		
		// mcmmo 기본 수치 획득
		int skill_level = ExperienceAPI.getLevel(_player, "archery");
		int skill_exp = ExperienceAPI.getXP(_player, "archery");
		int skill_need_exp = ExperienceAPI.getXPToNextLevel(_player, "archery");

		// 스킬 수치 획득
		int mastery_range_dmg = 0, mastery_melee_dmg = 0, concent_count = 0,
				support_dmg = 0, backstep_speed_buf = 0, backstep_shot_count = 0;
		
		mastery_range_dmg = (int)Get_Data(_player, key_mastery_range_dmg);
		mastery_melee_dmg = (int)Get_Data(_player, key_mastery_melee_dmg);
		concent_count = (int)Get_Data(_player, key_concentration_count);
		support_dmg = (int)Get_Data(_player, key_support_shot_dmg);
		backstep_speed_buf = (int)Get_Data(_player, key_backstep_speed_buf);
		backstep_shot_count = (int)Get_Data(_player, key_backstep_shot_count);

		// 설명 시작
		String temp = "";

		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "Archery - Bow" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.GRAY + "경험치를 얻는 방법 : " + ChatColor.WHITE + "몬스터 공격하기" + "\n";
		temp += ChatColor.GRAY + "레벨 : " + skill_level + " " + ChatColor.DARK_AQUA + "경험치 : " + ChatColor.YELLOW + "(" + ChatColor.GOLD + skill_exp + "/" + skill_need_exp + ChatColor.YELLOW + ")" + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "효과" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.DARK_AQUA + "활 마스터리 : " + ChatColor.GREEN + "활 스킬, 공격, 근접 공격시 추가 피해 적용" + (has_mastery ? "" : ChatColor.GRAY  + "(미습득)")  + "\n";
		temp += ChatColor.DARK_AQUA + "집중 : " + ChatColor.GREEN + "활로 오래 조준할수록 좀 더 강한 화살을 발사" + (has_concentration ? "" : ChatColor.GRAY  + "(미습득)")  +  "\n";
		temp += ChatColor.DARK_AQUA + "은밀 사격 : " + ChatColor.GREEN + "활로 쏜 화살이 자신을 노리지 않은 적에게 추가 피해를 줌" + (has_supportshot ? "" : ChatColor.GRAY  + "(미습득)") + "\n";
		temp += ChatColor.DARK_AQUA + "후퇴 사격(능력) : " + ChatColor.GREEN + "활을 들었을 때 뒤로 점프하며 사격, 7초간 이동 속도 증가" + (has_backstep ? "" : ChatColor.GRAY  + "(미습득)")  + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "스탯" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.RED + "활 마스터리 : " + ChatColor.YELLOW + "활로 사격시 " + mastery_range_dmg + ", 근접 공격시 " + mastery_melee_dmg + " 만큼의 추가 피해" + "\n";
		temp += ChatColor.RED + "집중 : " + ChatColor.YELLOW + "집중도 - " + concent_count + "\n";
		temp += ChatColor.RED + "은밀 사격 : " + ChatColor.YELLOW + "적에게 " + support_dmg + "의 추가 피해" + "\n";
		temp += ChatColor.RED + "후퇴 사격 : " + ChatColor.YELLOW + "쿨타임 15초, 발사하는 화살 " + backstep_shot_count + "발, 속도 증가 버프 " + backstep_speed_buf + "단계" + "\n";
	
		return temp;
	}

	// 이게 활이여?
	public static boolean Is_Bow(Material _material)
	{
		switch(_material)
		{
		case BOW:
			return true;
		default:
			break;
		}
		return false;
	}
	
	// 액티브 스킬 준비 (쉬프트 좌클)
	public void Archery_Active_Ready(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		Material handitem = player.getInventory().getItemInMainHand().getType();
		
		// 권한 체크
		if(!(player.hasPermission(perm_backstep) || player.hasPermission("*")))
			return;
		
		// 활을 들고 있는지, 웅크린 상태인지, 비행중은 아닌지를 확인
		if(!Is_Bow(handitem) || !player.isSneaking() || player.isFlying() || !player.isOnGround())
			return;
				
		// 쿨타임에 해당되는 경우 (쿨타임이라면 자동으로 플레이어에게 고지)
		if(mt.Is_Timer_Available(player, backstep_cooldown, true))
			return;
		
		// 스킬 준비중이지 않은 경우
		if(!mt.Is_Timer_Available(player, backstep_ready, false))
		{
			// 준비
			mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_READY, player, "후퇴 사격", backstep_ready, 20 * 3));
			return;
		}
	}

	// 액티브 스킬 사용 (점프)
	public void Archery_Active_Use(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		
		// 권한 체크
		if(!(player.hasPermission(perm_backstep) || player.hasPermission("*")))
			return;
		
		// 활을 들고 있는지, 비행중은 아닌지를 확인
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Is_Bow(handitem) || player.isFlying())
			return;
				
		// 준비중이지 않은 경우를 체크
		if(!mt.Is_Timer_Available(player, backstep_ready, false))
			return;
		
		/*
		 * 진정한 스킬 사용 시작
		 */
		
		// 화살 수와 속도 증가치 취득
		int arrow_count = 0;
		int buf_speed_amp = 0;
		arrow_count = (int) Get_Data(player, key_backstep_shot_count);
		buf_speed_amp = (int) Get_Data(player, key_backstep_speed_buf);

		for(int i = 0; i < arrow_count; i++)
		{
			Bukkit.getScheduler().runTaskLater(Main.mother,
					new Archery_BackStep_Skill(player, arrow_count), 6 + i * (arrow_count > 5 ? 1 : 2));
		}
		
		// 점프!
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
		Vector jump = player.getLocation().getDirection().normalize();
		jump.setX(jump.getX() * -1.0d);
		jump.setZ(jump.getZ() * -1.0d);
		jump.setY(0.6d);
		jump.multiply(1.3);
		player.setVelocity(jump);

		// 쿨타임
		mt.Remove_Timer(player, backstep_ready); // 준비 타이머 제거
		mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_COOLDOWN, player, "후퇴 사격", backstep_cooldown, 20 * 20));  // 쿨타임 설정
		player.sendMessage(ChatColor.RED + "[ 후퇴 사격 발동 ]");
		
		// 버프
		if(player.hasPotionEffect(PotionEffectType.SPEED))
			player.sendMessage(ChatColor.GRAY + "[ 속도 증가 버프로 인해 버프 효과가 취소됩니다. ]");
		else if(player.hasPotionEffect(PotionEffectType.SLOW))
			player.sendMessage(ChatColor.GRAY + "[ 속도 감소 버프로 인해 버프 효과를 받을 수 없습니다! ]");
		else
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, buf_speed_amp - 1));
	}

	// 집중 시작
	public void Archery_Concentrate_Ready(Player player)
	{		
		// 권한 체크
		if(!(player.hasPermission(perm_concentration) || player.hasPermission("*")))
			return;
		
		// 활을 들고 있는지, 비행중은 아닌지를 확인
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Is_Bow(handitem) || player.isFlying())
			return;
		
		// 조준 완료로 치부하고 집중 리스트에 추가
		Archery_Concentrate_Manager.Instance.Remove_Player_Concentrate(player);
		Archery_Concentrate_Manager.Instance.Add_Player_Concentrate(player);
	}
}

class Archery_BackStep_Skill implements Runnable
{
	Player player;
	int arrow_count;
	double random_range = 0.2;
	
	public Archery_BackStep_Skill(Player p, int _count)
	{
		player = p;
		arrow_count = _count;
		random_range = 0.35 + arrow_count * 0.015;
	}
	
	public void run()
	{
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 1f);
		Arrow arrow = player.launchProjectile(Arrow.class);
		arrow.setCritical(true);
		arrow.setPickupStatus(PickupStatus.DISALLOWED);
		arrow.setMetadata("arrow_no_time", new FixedMetadataValue(Main.mother, true));
		arrow.setMetadata("arrow_remove", new FixedMetadataValue(Main.mother, true));
		arrow.setMetadata("be.archery.arrow", new FixedMetadataValue(Main.mother, true));
		int dam = 10;
		dam = (int)Manager_Archery_Ability.Instance.Get_Data(player, Manager_Archery_Ability.key_mastery_range_dmg);
		
		arrow.setMetadata("be.archery.mastery_dmg", new FixedMetadataValue(Main.mother, dam));

	}
}

/**
 * @author jimja
 * @version 2020. 5. 5.
 * @apiNote 집중 스택 관리자
 */
class Archery_Concentrate_Manager implements Runnable
{	
	public static Archery_Concentrate_Manager Instance;
	
	private HashMap<Player, Integer> player_list;
	Manager_Archery_Ability maa;
	
	int counter = 0;
	
	public Archery_Concentrate_Manager()
	{
		Instance = this;
		player_list = new HashMap<Player, Integer>();
		maa = Manager_Archery_Ability.Instance;
	}
	
	public void Add_Player_Concentrate(Player player)
	{
		if (player_list.containsKey(player))
		{
			Integer concent_value = player_list.get(player);
			int concent_max = (int)maa.Get_Data(player, Manager_Archery_Ability.key_concentration_count);
			int updated_value = Math.min(concent_max, concent_value + 1);
			
			if(concent_value == updated_value)
				return;
			
			if(updated_value > 0)
			{
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7§l[ 집중 " + updated_value + " : " + concent_max + " ]"));
				player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 2f);
			}
			
			if(updated_value == concent_max)
				player.sendMessage(ChatColor.YELLOW + "[ 집중 최대치 ]");

			player_list.put(player, updated_value);
		}
		else
		{
			player_list.put(player, -2);
		}
	}
	
	public void Remove_Player_Concentrate(Player player)
	{
		player_list.remove(player);
	}
	
	public int Get_Player_Concentrate(Player player)
	{
		int value = 0;
		if(player_list.containsKey(player))
			value = player_list.get(player).intValue();
		return value;
	}
	
	public void run()
	{
		counter = (counter + 1) % 4;
		
		// 리스트에 등록된 유저들 집중을 잘 하고 있나 체크
		Iterator<Player> players = player_list.keySet().iterator();
		while (players.hasNext())
		{
			Player player = players.next();

			// 활을 들고 있는지, 비행중은 아닌지를 확인
			Material handitem = player.getInventory().getItemInMainHand().getType();
			if(!Manager_Archery_Ability.Is_Bow(handitem) || player.isFlying() || !player.isHandRaised())
				return;
			
			// 집중을 잘 했으면 단계 상승
			if (player.hasMetadata("malammo.skill.rapid_fire"))
				Add_Player_Concentrate(player);
			else if (counter == 0)
				Add_Player_Concentrate(player);
		}		
	}
}
