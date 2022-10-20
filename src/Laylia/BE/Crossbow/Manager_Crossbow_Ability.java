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
 * @apiNote ���ü� �����Ƽ �Ŵ���
 * �ü� �����ؼ��� �갡 �� ������ �ִٰ� ���� ��
 */
public class Manager_Crossbow_Ability extends Manager_Ability_Base
{
	public static Manager_Crossbow_Ability Instance;
	
	// ��ų ������ Ű
	public static final String key_mastery_range_dmg = "crossbow.mastery_range_dmg";
	public static final String key_mastery_melee_dmg = "crossbow.mastery_melee_dmg";
	public static final String key_counting_shot_count = "crossbow.counting_shot_count";
	public static final String key_counting_shot_dmg = "crossbow.counting_shot_dmg";

	// Ÿ�̸Ӹ� ����ϴ� ��ų
	public static final String backstep_cooldown = "crossbow.backstep_cooldown";
	public static final String backstep_ready = "crossbow.backstep_ready";

	// ����
	public static final String perm_mastery = "battleEnhancements.mcmmo.crossbow.mastery";
	public static final String perm_counting_shot = "battleEnhancements.mcmmo.crossbow.counting_shot";
	public static final String perm_backstep = "battleEnhancements.mcmmo.crossbow.back_step";

	public Manager_Crossbow_Ability()
	{
		Bukkit.getConsoleSender().sendMessage("���ü� �����Ƽ Ȱ��ȭ!");
		
		// �̱���
		Instance = this;
		
		// �̺�Ʈ ���
		Bukkit.getPluginManager().registerEvents(new Crossbow_Event(),  Main.mother);
		
		// ��Ÿ
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
		// �۹̼� üũ
		boolean has_mastery = _player.hasPermission(perm_mastery);
		boolean has_countingshot = _player.hasPermission(perm_counting_shot);
		boolean has_backstep = _player.hasPermission(perm_backstep);
		
		// mcmmo �⺻ ��ġ ȹ��
		int skill_level = ExperienceAPI.getLevel(_player, PrimarySkillType.ARCHERY);
		int skill_exp = ExperienceAPI.getXP(_player, "archery");
		int skill_need_exp = ExperienceAPI.getXPToNextLevel(_player, "archery");

		// ��ų ��ġ ȹ��
		int mastery_range_dmg = 0, mastery_melee_dmg = 0, counting_shot_count = 0,
				counting_shot_dmg = 0;
		
		mastery_range_dmg = (int)Get_Data(_player, key_mastery_range_dmg);
		mastery_melee_dmg = (int)Get_Data(_player, key_mastery_melee_dmg);
		counting_shot_count = (int)Get_Data(_player, key_counting_shot_count);
		counting_shot_dmg = (int)Get_Data(_player, key_counting_shot_dmg);

		// ���� ����
		String temp = "";

		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "Archery - Crossbow" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.GRAY + "����ġ�� ��� ��� : " + ChatColor.WHITE + "���� �����ϱ�" + "\n";
		temp += ChatColor.GRAY + "���� : " + skill_level + " " + ChatColor.DARK_AQUA + "����ġ : " + ChatColor.YELLOW + "(" + ChatColor.GOLD + skill_exp + "/" + skill_need_exp + ChatColor.YELLOW + ")" + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "ȿ��" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.DARK_AQUA + "���� �����͸� : " + ChatColor.GREEN + "���� ��ų, ����, ���� ���ݽ� �߰� ���� ����" + (has_mastery ? "" : ChatColor.GRAY  + "(�̽���)")  + "\n";
		temp += ChatColor.DARK_AQUA + "ī���� : " + ChatColor.GREEN + "ȭ���� ���� ������ ������ �� ���� Ƚ������ �߰� ����\n"
				+ "         ī���� ���� - 500���� �̻�� �ӵ� ���� �� ���� ȹ��" + (has_countingshot ? "" : ChatColor.GRAY  + "(�̽���)")  +  "\n";
		temp += ChatColor.DARK_AQUA + "���� ȸ��(�ɷ�) : " + ChatColor.GREEN + "������ ����� �� �ڷ� �������� ���" + (has_backstep ? "" : ChatColor.GRAY  + "(�̽���)")  + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "����" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.RED + "���� �����͸� : " + ChatColor.YELLOW + "�������� ��ݽ� " + mastery_range_dmg + ", ���� ���ݽ� " + mastery_melee_dmg + " ��ŭ�� �߰� ����" + "\n";
		temp += ChatColor.RED + "ī���� : " + ChatColor.YELLOW + "�� " + counting_shot_count + "��° ��ݸ��� " + counting_shot_dmg + "�� �߰� ����" + "\n";
		temp += ChatColor.RED + "���� ȸ�� : " + ChatColor.YELLOW + "��Ÿ�� 7��" + "\n";
	
		return temp;
	}

	// �̰� �����̿�?
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
	
	// ��Ƽ�� ��ų �غ� (����Ʈ ��Ŭ)
	public void Crossbow_Active_Ready(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		Material handitem = player.getInventory().getItemInMainHand().getType();
		
		// ���� üũ
		if(!(player.hasPermission(perm_backstep) || player.hasPermission("*")))
			return;
		
		// ������ ��� �ִ���, ��ũ�� ��������, �������� �ƴ����� Ȯ��
		if(!Is_Crossbow(handitem) || !player.isSneaking() || player.isFlying() || !player.isOnGround())
			return;
				
		// ��Ÿ�ӿ� �ش�Ǵ� ��� (��Ÿ���̶�� �ڵ����� �÷��̾�� ����)
		if(mt.Is_Timer_Available(player, backstep_cooldown, true))
			return;
		
		// ��ų �غ������� ���� ���
		if(!mt.Is_Timer_Available(player, backstep_ready, false))
		{
			// �غ�
			mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_READY, player, "���� ȸ��", backstep_ready, 20 * 3));
			return;
		}
	}

	// ��Ƽ�� ��ų ��� (����)
	public void Crossbow_Active_Use(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		
		// ���� üũ
		if(!(player.hasPermission(perm_backstep) || player.hasPermission("*")))
			return;
		
		// ������ ��� �ִ���, �������� �ƴ����� Ȯ��
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Is_Crossbow(handitem) || player.isFlying())
			return;
				
		// �غ������� ���� ��츦 üũ
		if(!mt.Is_Timer_Available(player, backstep_ready, false))
			return;
		
		/*
		 * ������ ��ų ��� ����
		 */
		Bukkit.getScheduler().runTaskLater(Main.mother,
				new Crossbow_BackStep_Skill(player), 4);

		// ����!
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
		Vector jump = player.getLocation().getDirection().normalize();
		jump.setX(jump.getX() * -1.0d);
		jump.setZ(jump.getZ() * -1.0d);
		jump.setY(0.1d);
		jump.multiply(2d);
		player.setVelocity(jump);

		// ��Ÿ��
		mt.Remove_Timer(player, backstep_ready); // �غ� Ÿ�̸� ����
		mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_COOLDOWN, player, "���� ȸ��", backstep_cooldown, 20 * 7));  // ��Ÿ�� ����
		player.sendMessage(ChatColor.RED + "[ ���� ȸ�� �ߵ� ]");
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
	public int m_Count = 0; // ��� Ƚ��
	public int m_Remained_Tick = 0; // ���� ���ݱ��� ���� ƽ
	
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
		// ����Ʈ�� ��ϵ� ������ �ð� üũ
		Iterator<Player> players = player_list.keySet().iterator();
		while(players.hasNext())
		{
			Player player = players.next();

			// ������ ��� �ִ���, �������� �ƴ����� Ȯ��
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
	 * @apiNote ���� ���ݽ� ī���� ������ �÷��ش�
	 * 
	 * @param _player ������
	 * @param _entity ������(?)
	 * @return �� N��° ���ݿ� �ش��ϴ� ��� true�� ��ȯ
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




