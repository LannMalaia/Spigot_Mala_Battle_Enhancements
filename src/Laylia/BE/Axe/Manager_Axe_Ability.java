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
 * @apiNote �μ� �����Ƽ �Ŵ���
 * �μ� �����ؼ��� �갡 �� ������ �ִٰ� ���� ��
 */
public class Manager_Axe_Ability extends Manager_Ability_Base
{
	public static Manager_Axe_Ability Instance;
	
	// ��ų ������ Ű
	public static final String key_mastery_dmg = "axe.key_mastery_dmg";
	public static final String key_double_edge_per = "axe.key_double_edge_per";
	public static final String key_tiger_power_power = "axe.key_tiger_power_power";
	public static final String key_rage_buf_amp = "axe.key_rage_buf_amp";

	// Ÿ�̸Ӹ� ����ϴ� ��ų
	public static final String rage_cooldown = "axe.rage_cooldown";
	public static final String rage_ready = "axe.rage_ready";

	// ����
	public static final String perm_mastery = "battleEnhancements.mcmmo.axes.mastery";
	public static final String perm_double_edge = "battleEnhancements.mcmmo.axes.double_edge";
	public static final String perm_tiger_power = "battleEnhancements.mcmmo.axes.tiger_power";
	public static final String perm_rage = "battleEnhancements.mcmmo.axes.rage";

	public Manager_Axe_Ability()
	{
		Bukkit.getConsoleSender().sendMessage("���� �����Ƽ Ȱ��ȭ!");
		
		// �̱���
		Instance = this;
		
		// �̺�Ʈ ���
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
		// �۹̼� üũ
		boolean has_mastery = _player.hasPermission(perm_mastery);
		boolean has_double_edge = _player.hasPermission(perm_double_edge);
		boolean has_tiger_power = _player.hasPermission(perm_tiger_power);
		boolean has_rage = _player.hasPermission(perm_rage);
		
		// mcmmo �⺻ ��ġ ȹ��
		int skill_level = ExperienceAPI.getLevel(_player, "axes");
		int skill_exp = ExperienceAPI.getXP(_player, "axes");
		int skill_need_exp = ExperienceAPI.getXPToNextLevel(_player, "axes");

		// ��ų ��ġ ȹ��
		int mastery_dmg = 0, double_edge_per = 0, tiger_power_power = 0, rage_buf_amp = 0;
		mastery_dmg = (int)Get_Data(_player, key_mastery_dmg);
		double_edge_per = (int)Get_Data(_player, key_double_edge_per);
		tiger_power_power = (int)Get_Data(_player, key_tiger_power_power);
		rage_buf_amp = (int)Get_Data(_player, key_rage_buf_amp);
		
		String temp = "";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "Axes" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.GRAY + "����ġ�� ��� ��� : " + ChatColor.WHITE + "���� �����ϱ�" + "\n";
		temp += ChatColor.GRAY + "���� : " + skill_level + " " + ChatColor.DARK_AQUA + "����ġ : " + ChatColor.YELLOW + "(" + ChatColor.GOLD + skill_exp + "/" + skill_need_exp + ChatColor.YELLOW + ")" + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "ȿ��" + ChatColor.RED + "[]-----" + "\n";
		temp +=  ChatColor.DARK_AQUA + "���� �����͸� : " + ChatColor.GREEN + "���� ��ų, ���ݽ� �߰� ���� ����" + (has_mastery ? "" : ChatColor.GRAY  + "(�̽���)")  + "\n";
		temp +=  ChatColor.DARK_AQUA + "�����ΰ� : " + ChatColor.GREEN + "�ڽ��� ������� ���� ��ŭ ���� �����͸� ȿ�� ����" + (has_double_edge ? "" : ChatColor.GRAY  + "(�̽���)")  +  "\n";
		temp +=  ChatColor.DARK_AQUA + "ȣ���� ��� : " + ChatColor.GREEN + "�� ������ ����� �߰� ���� ����" + (has_tiger_power ? "" : ChatColor.GRAY  + "(�̽���)")  + "\n";
		temp +=  ChatColor.DARK_AQUA + "����ȭ(�ɷ�) : " + ChatColor.GREEN + "��, ��� ���� ȹ��" + (has_rage ? "" : ChatColor.GRAY  + "(�̽���)")  + "\n";
		temp +=  ChatColor.DARK_AQUA + " ���ı����� : " + ChatColor.GREEN + "��, ��� ������ ������ �� ���� ����ġ�� ���� ����" + (has_rage && has_tiger_power && skill_level >= 3000 ? "" : ChatColor.GRAY  + "(�̽���)")  + "\n";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "����" + ChatColor.RED + "[]-----" + "\n";
		temp +=  ChatColor.RED + "���� �����͸� : " + ChatColor.YELLOW + "������ ����� �� ��� ��ų, ���ݿ� " + mastery_dmg + " ��ŭ�� �߰� ����" + "\n";
		temp +=  ChatColor.RED + "�����ΰ� : " + ChatColor.YELLOW + "������� 25% ������ �� �����͸��� " + double_edge_per + "% ��ŭ�� �߰� ����" + "\n";
		temp +=  ChatColor.RED + "ȣ���� ��� : " + ChatColor.YELLOW + "�� ���� * " + tiger_power_power + " ��ŭ�� �߰� ����" + "\n";
		temp +=  ChatColor.RED + "����ȭ : " + ChatColor.YELLOW + "��Ÿ�� 30��, ��, ��� ���� " + rage_buf_amp + "�ܰ�" + "\n";
		temp +=  ChatColor.RED + " ���ı����� : " + ( has_rage && has_tiger_power && skill_level >= 3000 ? ChatColor.YELLOW + "óġ�� ������ 1�ܰ�, 6�� ����(�ִ� 4�ܰ�, 20��)"
												: ChatColor.GRAY + "ȣ���� ���, ����ȭ�� ���� ���� 3000 �̻��� �Ǹ� ����") + "\n";
		
		return temp;
	}

	// �̰� ������?
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
	
	// ���� ��ų �غ� (����Ʈ ��Ŭ)
	public static void Axe_Active_Ready(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		Material handitem = player.getInventory().getItemInMainHand().getType();
		
		// ���� üũ
		if(!(player.hasPermission(perm_rage) || player.hasPermission("*")))
			return;
		
		// ������ ��� �ִ���, ��ũ�� ��������, �������� �ƴ����� Ȯ��
		if(!Is_Axe(handitem) || !player.isSneaking() || player.isFlying() || !player.isOnGround())
			return;
				
		// ��Ÿ�ӿ� �ش�Ǵ� ��� (��Ÿ���̶�� �ڵ����� �÷��̾�� ����)
		if(mt.Is_Timer_Available(player, rage_cooldown, true))
			return;
		
		// ��ų �غ������� ���� ���
		if(!mt.Is_Timer_Available(player, rage_ready, false))
		{
			// �غ�
			mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_READY, player, "����ȭ", rage_ready, 20 * 3));
			return;
		}
		else
		{
			Axe_Active_Use(player, false);
		}
	}

	// ���� ��ų ���
	public static void Axe_Active_Use(Player player, boolean _is_jump)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		
		// ���� üũ
		if(!(player.hasPermission(perm_rage) || player.hasPermission("*")))
			return;
		
		// Ȱ�� ��� �ִ���, �������� �ƴ����� Ȯ��
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if(!Is_Axe(handitem) || player.isFlying())
			return;
				
		// �غ������� ���� ��츦 üũ
		if(!mt.Is_Timer_Available(player, rage_ready, false))
			return;

		/*
		 * ������ ��ų ��� ����
		 */

		// ���� ���� ���
		int buf_amp = 0;
		buf_amp = (int) Manager_Axe_Ability.Instance.Get_Data(player, key_rage_buf_amp);
		
		// ��Ÿ��
		mt.Remove_Timer(player, rage_ready); // �غ� Ÿ�̸� ����
		mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_COOLDOWN, player, "����ȭ", rage_cooldown, 20 * 20));  // ��Ÿ�� ����
		player.sendMessage(ChatColor.RED + "[ ����ȭ �ߵ� ]");
		
		player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().clone().add(0,1,0), 150, 0d, 0d, 0d, 0.2d);

		// ����!
		if(_is_jump)
		{
			player.setMetadata("BE_Mcmmo_Axe_Skill_Stomp", new FixedMetadataValue(Main.mother, true));
			player.setVelocity(player.getEyeLocation().getDirection().clone().add(new Vector(0d, 1.5d, 0d)));
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 2f, 2f);
		}
		
		// ���� �ο�
		if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			player.sendMessage(ChatColor.GRAY + "[ �� ������ ���� ���� ȿ���� ��ҵ˴ϴ�. ]");
		else if(player.hasPotionEffect(PotionEffectType.WEAKNESS))
			player.sendMessage(ChatColor.GRAY + "[ ��ȭ ������ ���� ���� ȿ���� ���� �� �����ϴ�! ]");
		else
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, (_is_jump ? 0 : 1)));

		if(player.hasPotionEffect(PotionEffectType.REGENERATION))
			player.sendMessage(ChatColor.GRAY + "[ ��� ������ ���� ���� ȿ���� ��ҵ˴ϴ�. ]");
		else if(player.hasPotionEffect(PotionEffectType.POISON))
			player.sendMessage(ChatColor.GRAY + "[ �� ������ ���� ���� ȿ���� ���� �� �����ϴ�! ]");
		else if(player.hasPotionEffect(PotionEffectType.WITHER))
			player.sendMessage(ChatColor.GRAY + "[ ���� ������ ���� ���� ȿ���� ���� �� �����ϴ�! ]");
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












