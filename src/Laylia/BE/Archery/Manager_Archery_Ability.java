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
 * @apiNote �ü� �����Ƽ �Ŵ���
 * �ü� �����ؼ��� �갡 �� ������ �ִٰ� ���� ��
 */
public class Manager_Archery_Ability extends Manager_Ability_Base
{
	public static Manager_Archery_Ability Instance;
	
	// ��ų ������ Ű
	public static final String key_mastery_range_dmg = "archery.mastery_range_dmg";
	public static final String key_mastery_melee_dmg = "archery.mastery_melee_dmg";
	public static final String key_concentration_count = "archery.concentration_count";
	public static final String key_support_shot_dmg = "archery.key_support_shot_dmg";
	public static final String key_backstep_speed_buf = "archery.key_backstep_speed_buf";
	public static final String key_backstep_shot_count = "archery.key_backstep_shot_count";

	// Ÿ�̸Ӹ� ����ϴ� ��ų
	public static final String backstep_cooldown = "archery.backstep_cooldown";
	public static final String backstep_ready = "archery.backstep_ready";

	// ����
	public static final String perm_mastery = "battleEnhancements.mcmmo.archery.mastery";
	public static final String perm_concentration = "battleEnhancements.mcmmo.archery.concentration";
	public static final String perm_support_shot = "battleEnhancements.mcmmo.archery.support_shot";
	public static final String perm_backstep = "battleEnhancements.mcmmo.archery.back_step";

	public Manager_Archery_Ability()
	{
		Bukkit.getConsoleSender().sendMessage("�ü� �����Ƽ Ȱ��ȭ!");
		
		// �̱���
		Instance = this;
		
		// �̺�Ʈ ���
		Bukkit.getPluginManager().registerEvents(new Archery_Event(),  Main.mother);
		
		// ��Ÿ
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
		// �۹̼� üũ
		boolean has_mastery = _player.hasPermission(perm_mastery);
		boolean has_concentration = _player.hasPermission(perm_concentration);
		boolean has_supportshot = _player.hasPermission(perm_support_shot);
		boolean has_backstep = _player.hasPermission(perm_backstep);
		
		// mcmmo �⺻ ��ġ ȹ��
		int skill_level = ExperienceAPI.getLevel(_player, "archery");
		int skill_exp = ExperienceAPI.getXP(_player, "archery");
		int skill_need_exp = ExperienceAPI.getXPToNextLevel(_player, "archery");

		// ��ų ��ġ ȹ��
		int mastery_range_dmg = 0, mastery_melee_dmg = 0, concent_count = 0,
				support_dmg = 0, backstep_speed_buf = 0, backstep_shot_count = 0;
		
		mastery_range_dmg = (int)Get_Data(_player, key_mastery_range_dmg);
		mastery_melee_dmg = (int)Get_Data(_player, key_mastery_melee_dmg);
		concent_count = (int)Get_Data(_player, key_concentration_count);
		support_dmg = (int)Get_Data(_player, key_support_shot_dmg);
		backstep_speed_buf = (int)Get_Data(_player, key_backstep_speed_buf);
		backstep_shot_count = (int)Get_Data(_player, key_backstep_shot_count);

		// ���� ����
		String temp = "";

		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "Archery - Bow" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.GRAY + "����ġ�� ��� ��� : " + ChatColor.WHITE + "���� �����ϱ�" + "\n";
		temp += ChatColor.GRAY + "���� : " + skill_level + " " + ChatColor.DARK_AQUA + "����ġ : " + ChatColor.YELLOW + "(" + ChatColor.GOLD + skill_exp + "/" + skill_need_exp + ChatColor.YELLOW + ")" + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "ȿ��" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.DARK_AQUA + "Ȱ �����͸� : " + ChatColor.GREEN + "Ȱ ��ų, ����, ���� ���ݽ� �߰� ���� ����" + (has_mastery ? "" : ChatColor.GRAY  + "(�̽���)")  + "\n";
		temp += ChatColor.DARK_AQUA + "���� : " + ChatColor.GREEN + "Ȱ�� ���� �����Ҽ��� �� �� ���� ȭ���� �߻�" + (has_concentration ? "" : ChatColor.GRAY  + "(�̽���)")  +  "\n";
		temp += ChatColor.DARK_AQUA + "���� ��� : " + ChatColor.GREEN + "Ȱ�� �� ȭ���� �ڽ��� �븮�� ���� ������ �߰� ���ظ� ��" + (has_supportshot ? "" : ChatColor.GRAY  + "(�̽���)") + "\n";
		temp += ChatColor.DARK_AQUA + "���� ���(�ɷ�) : " + ChatColor.GREEN + "Ȱ�� ����� �� �ڷ� �����ϸ� ���, 7�ʰ� �̵� �ӵ� ����" + (has_backstep ? "" : ChatColor.GRAY  + "(�̽���)")  + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "����" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.RED + "Ȱ �����͸� : " + ChatColor.YELLOW + "Ȱ�� ��ݽ� " + mastery_range_dmg + ", ���� ���ݽ� " + mastery_melee_dmg + " ��ŭ�� �߰� ����" + "\n";
		temp += ChatColor.RED + "���� : " + ChatColor.YELLOW + "���ߵ� - " + concent_count + "\n";
		temp += ChatColor.RED + "���� ��� : " + ChatColor.YELLOW + "������ " + support_dmg + "�� �߰� ����" + "\n";
		temp += ChatColor.RED + "���� ��� : " + ChatColor.YELLOW + "��Ÿ�� 15��, �߻��ϴ� ȭ�� " + backstep_shot_count + "��, �ӵ� ���� ���� " + backstep_speed_buf + "�ܰ�" + "\n";
	
		return temp;
	}

	// �̰� Ȱ�̿�?
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
	
	// ��Ƽ�� ��ų �غ� (����Ʈ ��Ŭ)
	public void Archery_Active_Ready(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		Material handitem = player.getInventory().getItemInMainHand().getType();
		
		// ���� üũ
		if(!(player.hasPermission(perm_backstep) || player.hasPermission("*")))
			return;
		
		// Ȱ�� ��� �ִ���, ��ũ�� ��������, �������� �ƴ����� Ȯ��
		if(!Is_Bow(handitem) || !player.isSneaking() || player.isFlying() || !player.isOnGround())
			return;
				
		// ��Ÿ�ӿ� �ش�Ǵ� ��� (��Ÿ���̶�� �ڵ����� �÷��̾�� ����)
		if(mt.Is_Timer_Available(player, backstep_cooldown, true))
			return;
		
		// ��ų �غ������� ���� ���
		if(!mt.Is_Timer_Available(player, backstep_ready, false))
		{
			// �غ�
			mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_READY, player, "���� ���", backstep_ready, 20 * 3));
			return;
		}
	}

	// ��Ƽ�� ��ų ��� (����)
	public void Archery_Active_Use(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		
		// ���� üũ
		if(!(player.hasPermission(perm_backstep) || player.hasPermission("*")))
			return;
		
		// Ȱ�� ��� �ִ���, �������� �ƴ����� Ȯ��
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Is_Bow(handitem) || player.isFlying())
			return;
				
		// �غ������� ���� ��츦 üũ
		if(!mt.Is_Timer_Available(player, backstep_ready, false))
			return;
		
		/*
		 * ������ ��ų ��� ����
		 */
		
		// ȭ�� ���� �ӵ� ����ġ ���
		int arrow_count = 0;
		int buf_speed_amp = 0;
		arrow_count = (int) Get_Data(player, key_backstep_shot_count);
		buf_speed_amp = (int) Get_Data(player, key_backstep_speed_buf);

		for(int i = 0; i < arrow_count; i++)
		{
			Bukkit.getScheduler().runTaskLater(Main.mother,
					new Archery_BackStep_Skill(player, arrow_count), 6 + i * (arrow_count > 5 ? 1 : 2));
		}
		
		// ����!
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
		Vector jump = player.getLocation().getDirection().normalize();
		jump.setX(jump.getX() * -1.0d);
		jump.setZ(jump.getZ() * -1.0d);
		jump.setY(0.6d);
		jump.multiply(1.3);
		player.setVelocity(jump);

		// ��Ÿ��
		mt.Remove_Timer(player, backstep_ready); // �غ� Ÿ�̸� ����
		mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_COOLDOWN, player, "���� ���", backstep_cooldown, 20 * 20));  // ��Ÿ�� ����
		player.sendMessage(ChatColor.RED + "[ ���� ��� �ߵ� ]");
		
		// ����
		if(player.hasPotionEffect(PotionEffectType.SPEED))
			player.sendMessage(ChatColor.GRAY + "[ �ӵ� ���� ������ ���� ���� ȿ���� ��ҵ˴ϴ�. ]");
		else if(player.hasPotionEffect(PotionEffectType.SLOW))
			player.sendMessage(ChatColor.GRAY + "[ �ӵ� ���� ������ ���� ���� ȿ���� ���� �� �����ϴ�! ]");
		else
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, buf_speed_amp - 1));
	}

	// ���� ����
	public void Archery_Concentrate_Ready(Player player)
	{		
		// ���� üũ
		if(!(player.hasPermission(perm_concentration) || player.hasPermission("*")))
			return;
		
		// Ȱ�� ��� �ִ���, �������� �ƴ����� Ȯ��
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Is_Bow(handitem) || player.isFlying())
			return;
		
		// ���� �Ϸ�� ġ���ϰ� ���� ����Ʈ�� �߰�
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
 * @apiNote ���� ���� ������
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
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("��7��l[ ���� " + updated_value + " : " + concent_max + " ]"));
				player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 2f);
			}
			
			if(updated_value == concent_max)
				player.sendMessage(ChatColor.YELLOW + "[ ���� �ִ�ġ ]");

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
		
		// ����Ʈ�� ��ϵ� ������ ������ �� �ϰ� �ֳ� üũ
		Iterator<Player> players = player_list.keySet().iterator();
		while (players.hasNext())
		{
			Player player = players.next();

			// Ȱ�� ��� �ִ���, �������� �ƴ����� Ȯ��
			Material handitem = player.getInventory().getItemInMainHand().getType();
			if(!Manager_Archery_Ability.Is_Bow(handitem) || player.isFlying() || !player.isHandRaised())
				return;
			
			// ������ �� ������ �ܰ� ���
			if (player.hasMetadata("malammo.skill.rapid_fire"))
				Add_Player_Concentrate(player);
			else if (counter == 0)
				Add_Player_Concentrate(player);
		}		
	}
}
