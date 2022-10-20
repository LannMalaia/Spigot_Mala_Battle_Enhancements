package Laylia.BE.Crossbow;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
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
 * @apiNote 석궁술 어빌리티 매니저
 * 궁술 관련해서는 얘가 다 가지고 있다고 봐도 됨
 */
public class Manager_Crossbow_Ability extends Manager_Ability_Base
{
	public static Manager_Crossbow_Ability Instance;
	
	// 스킬 데이터 키
	public static final String key_mastery_range_dmg = "crossbow.mastery_range_dmg";
	public static final String key_mastery_melee_dmg = "crossbow.mastery_melee_dmg";
	public static final String key_counting_shot_count = "crossbow.counting_shot_count";
	public static final String key_counting_shot_dmg = "crossbow.counting_shot_dmg";

	// 타이머를 사용하는 스킬
	public static final String backstep_cooldown = "crossbow.backstep_cooldown";
	public static final String backstep_ready = "crossbow.backstep_ready";

	// 권한
	public static final String perm_mastery = "battleEnhancements.mcmmo.crossbow.mastery";
	public static final String perm_counting_shot = "battleEnhancements.mcmmo.crossbow.counting_shot";
	public static final String perm_backstep = "battleEnhancements.mcmmo.crossbow.back_step";

	public Manager_Crossbow_Ability()
	{
		Bukkit.getConsoleSender().sendMessage("석궁술 어빌리티 활성화!");
		
		// 싱글턴
		Instance = this;
		
		// 이벤트 등록
		Bukkit.getPluginManager().registerEvents(new Crossbow_Event(),  Main.mother);
		
		// 기타
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.mother, new Crossbow_Counting_Manager(), 100, 30);
	}

	@Override
	public double Get_Data(Player _player, String _key)
	{
		int archery_level = mcMMO.getDatabaseManager().loadPlayerProfile(_player.getUniqueId()).getSkillLevel(PrimarySkillType.ARCHERY);
		switch (_key)
		{
		case key_mastery_range_dmg:
			return Math.min(15, archery_level / 250);
		case key_mastery_melee_dmg:
			return Math.min(15, archery_level / 300);
		case key_counting_shot_count:
			return Math.max(3, 5 - archery_level / 1500);
		case key_counting_shot_dmg:
			return Math.min(100, archery_level / 100);
		}
		return 0;
	}
	
	@Override
	protected String Make_Description(Player _player)
	{
		// 퍼미션 체크
		boolean has_mastery = _player.hasPermission(perm_mastery);
		boolean has_countingshot = _player.hasPermission(perm_counting_shot);
		boolean has_backstep = _player.hasPermission(perm_backstep);
		
		// mcmmo 기본 수치 획득
		int skill_level = ExperienceAPI.getLevel(_player, PrimarySkillType.ARCHERY);
		int skill_exp = ExperienceAPI.getXP(_player, "archery");
		int skill_need_exp = ExperienceAPI.getXPToNextLevel(_player, "archery");

		// 스킬 수치 획득
		int mastery_range_dmg = 0, mastery_melee_dmg = 0, counting_shot_count = 0,
				counting_shot_dmg = 0;
		
		mastery_range_dmg = (int)Get_Data(_player, key_mastery_range_dmg);
		mastery_melee_dmg = (int)Get_Data(_player, key_mastery_melee_dmg);
		counting_shot_count = (int)Get_Data(_player, key_counting_shot_count);
		counting_shot_dmg = (int)Get_Data(_player, key_counting_shot_dmg);

		// 설명 시작
		String temp = "";

		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "Archery - Crossbow" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.GRAY + "경험치를 얻는 방법 : " + ChatColor.WHITE + "몬스터 공격하기" + "\n";
		temp += ChatColor.GRAY + "레벨 : " + skill_level + " " + ChatColor.DARK_AQUA + "경험치 : " + ChatColor.YELLOW + "(" + ChatColor.GOLD + skill_exp + "/" + skill_need_exp + ChatColor.YELLOW + ")" + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "효과" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.DARK_AQUA + "석궁 마스터리 : " + ChatColor.GREEN + "석궁 스킬, 공격, 근접 공격시 추가 피해 적용" + (has_mastery ? "" : ChatColor.GRAY  + "(미습득)")  + "\n";
		temp += ChatColor.DARK_AQUA + "카운팅 : " + ChatColor.GREEN + "화살을 같은 적에게 맞췄을 때 일정 횟수마다 추가 피해\n"
				+ "         카운팅 서지 - 500레벨 이상시 속도 증가 Ⅱ 버프 획득" + (has_countingshot ? "" : ChatColor.GRAY  + "(미습득)")  +  "\n";
		temp += ChatColor.DARK_AQUA + "전술 회피(능력) : " + ChatColor.GREEN + "석궁을 들었을 때 뒤로 물러나며 사격" + (has_backstep ? "" : ChatColor.GRAY  + "(미습득)")  + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "스탯" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.RED + "석궁 마스터리 : " + ChatColor.YELLOW + "석궁으로 사격시 " + mastery_range_dmg + ", 근접 공격시 " + mastery_melee_dmg + " 만큼의 추가 피해" + "\n";
		temp += ChatColor.RED + "카운팅 : " + ChatColor.YELLOW + "매 " + counting_shot_count + "번째 사격마다 " + counting_shot_dmg + "의 추가 피해" + "\n";
		temp += ChatColor.RED + "전술 회피 : " + ChatColor.YELLOW + "쿨타임 7초" + "\n";
	
		return temp;
	}

	// 이게 석궁이여?
	public static boolean Is_Crossbow(Material _material)
	{
		switch(_material)
		{
		case CROSSBOW:
			return true;
		default:
			break;
		}
		return false;
	}
	
	// 액티브 스킬 준비 (쉬프트 좌클)
	public void Crossbow_Active_Ready(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		Material handitem = player.getInventory().getItemInMainHand().getType();
		
		// 권한 체크
		if(!(player.hasPermission(perm_backstep) || player.hasPermission("*")))
			return;
		
		// 석궁을 들고 있는지, 웅크린 상태인지, 비행중은 아닌지를 확인
		if(!Is_Crossbow(handitem) || !player.isSneaking() || player.isFlying() || !player.isOnGround())
			return;
				
		// 쿨타임에 해당되는 경우 (쿨타임이라면 자동으로 플레이어에게 고지)
		if(mt.Is_Timer_Available(player, backstep_cooldown, true))
			return;
		
		// 스킬 준비중이지 않은 경우
		if(!mt.Is_Timer_Available(player, backstep_ready, false))
		{
			// 준비
			mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_READY, player, "전술 회피", backstep_ready, 20 * 3));
			return;
		}
	}

	// 액티브 스킬 사용 (점프)
	public void Crossbow_Active_Use(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		
		// 권한 체크
		if(!(player.hasPermission(perm_backstep) || player.hasPermission("*")))
			return;
		
		// 석궁을 들고 있는지, 비행중은 아닌지를 확인
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Is_Crossbow(handitem) || player.isFlying())
			return;
				
		// 준비중이지 않은 경우를 체크
		if(!mt.Is_Timer_Available(player, backstep_ready, false))
			return;
		
		/*
		 * 진정한 스킬 사용 시작
		 */
		Bukkit.getScheduler().runTaskLater(Main.mother,
				new Crossbow_BackStep_Skill(player), 4);

		// 점프!
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
		Vector jump = player.getLocation().getDirection().normalize();
		jump.setX(jump.getX() * -1.0d);
		jump.setZ(jump.getZ() * -1.0d);
		jump.setY(0.1d);
		jump.multiply(2d);
		player.setVelocity(jump);

		// 쿨타임
		mt.Remove_Timer(player, backstep_ready); // 준비 타이머 제거
		mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_COOLDOWN, player, "전술 회피", backstep_cooldown, 20 * 7));  // 쿨타임 설정
		player.sendMessage(ChatColor.RED + "[ 전술 회피 발동 ]");
	}
}

class Crossbow_BackStep_Skill implements Runnable
{
	Player player;
	
	public Crossbow_BackStep_Skill(Player p)
	{
		player = p;
	}
	
	public void run()
	{	
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 1f);
		Arrow arrow = player.launchProjectile(Arrow.class);
		arrow.setVelocity(player.getLocation().getDirection().multiply(2d));
		arrow.setCritical(true);
		arrow.setMetadata("arrow_remove", new FixedMetadataValue(Main.mother, true));
	}
}


class Counting_Data
{
	public static final int Refresh_Tick = 120;
	
	public LivingEntity m_Target_Entity;
	public int m_Count = 0; // 사격 횟수
	public int m_Remained_Tick = 0; // 다음 공격까지 남은 틱
	
	public Counting_Data(LivingEntity _entity, int _count, int _time)
	{
		m_Target_Entity = _entity;
		m_Count = _count;
		m_Remained_Tick = _time;
	}

	public boolean Update_Time(int _time)
	{
		m_Remained_Tick -= _time;
		
		return m_Remained_Tick <= 0;
	}

	public int Update_Count(LivingEntity _entity, int _count)
	{
		if(m_Target_Entity == _entity)
			m_Count += _count;
		else
		{
			m_Target_Entity = _entity;
			m_Count = 1;
		}
		m_Remained_Tick = Refresh_Tick;
		
		return m_Count;
	}
}

class Crossbow_Counting_Manager implements Runnable
{
	public static final long interval = 10;
	public static Crossbow_Counting_Manager Instance;
	
	private HashMap<Player, Counting_Data> player_list;
	
	public Crossbow_Counting_Manager()
	{
		Instance = this;
		player_list = new HashMap<Player, Counting_Data>();
	}
	
	public void run()
	{
		// 리스트에 등록된 유저들 시간 체크
		Iterator<Player> players = player_list.keySet().iterator();
		while(players.hasNext())
		{
			Player player = players.next();

			// 석궁을 들고 있는지, 비행중은 아닌지를 확인
			Material handitem = player.getInventory().getItemInMainHand().getType();
			if(!Manager_Crossbow_Ability.Is_Crossbow(handitem) || player.isFlying())
			{
				Remove_Player_Counting(player);
				continue;
			}

			if(player_list.get(player).Update_Time((int)interval))
				Remove_Player_Counting(player);
		}
	}

	/**
	 * @apiNote 석궁 공격시 카운팅 스택을 올려준다
	 * 
	 * @param _player 공격자
	 * @param _entity 피해자(?)
	 * @return 매 N번째 공격에 해당하는 경우 true를 반환
 	 */
	public boolean Add_Player_Counting(Player _player, LivingEntity _entity)
	{
		Manager_Crossbow_Ability mca = Manager_Crossbow_Ability.Instance;
		if(player_list.containsKey(_player))
		{
			Counting_Data counting_value = player_list.get(_player);
			
			int count_max = (int)mca.Get_Data(_player, Manager_Crossbow_Ability.key_counting_shot_count);
			if(counting_value.Update_Count(_entity, 1) >= count_max)
			{
				Remove_Player_Counting(_player);
				return true;
			}
			
			player_list.put(_player, counting_value);
		}
		else
		{
			player_list.put(_player, new Counting_Data(_entity, 1, Counting_Data.Refresh_Tick));
		}
		return false;
	}
	
	public void Remove_Player_Counting(Player player)
	{
		player_list.remove(player);
	}
}




