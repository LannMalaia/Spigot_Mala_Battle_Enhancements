package Laylia.BE.Sword;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

import Laylia.BE.Base.Manager_Ability_Base;
import Laylia.BE.Base.TIMER_TYPE;
import Laylia.BE.Base.Timer_Data;
import Laylia.BE.Main.Main;
import Laylia.BE.Main.Manager_Timer;
import io.lumine.mythic.lib.damage.DamageType;
import laylia_core.main.Damage;


/**
 * @author jimja
 * @version 2020. 5. 5.
 * @apiNote �˼� �����Ƽ �Ŵ���
 * �˼� �����ؼ��� �갡 �� ������ �ִٰ� ���� ��
 */
public class Manager_Sword_Ability extends Manager_Ability_Base
{
	public static Manager_Sword_Ability Instance;
	
	// ��ų ������ Ű
	public static final String key_mastery_dmg = "swords.mastery_dmg";
	public static final String key_counter_dmg = "swords.counter_dmg";
	public static final String key_counter_cool = "swords.counter_cool";
	public static final String key_critical_per = "swords.critical_per";
	public static final String key_critical_dmgper = "swords.critical_dmgper";
	public static final String key_swordwave_dmg = "swords.swordwave_dmg";
	public static final String key_swordwave_count = "swords.swordwave_count";

	// Ÿ�̸Ӹ� ����ϴ� ��ų
	public static final String counter_cooldown = "swords.counter_cooldown";
	public static final String swordwave_cooldown = "swords.swordwave_cooldown";
	public static final String swordwave_ready = "swords.swordwave_ready";

	// ����
	public static final String perm_mastery = "battleEnhancements.mcmmo.swords.mastery";
	public static final String perm_counter = "battleEnhancements.mcmmo.swords.counter";
	public static final String perm_swordwave = "battleEnhancements.mcmmo.swords.swordwave";
	public static final String perm_critical = "battleEnhancements.mcmmo.swords.crit";
	
	public Manager_Sword_Ability()
	{
		Bukkit.getConsoleSender().sendMessage("�� �����Ƽ Ȱ��ȭ!");
		
		// �̱���
		Instance = this;
		
		// �̺�Ʈ ���
		Bukkit.getPluginManager().registerEvents(new Sword_Event(),  Main.mother);
	}
	
	@Override
	protected String Make_Description(Player _player)
	{
		// �۹̼� üũ
		boolean has_mastery = _player.hasPermission(perm_mastery);
		boolean has_counter = _player.hasPermission(perm_counter);
		boolean has_critical = _player.hasPermission(perm_critical);
		boolean has_swordwave = _player.hasPermission(perm_swordwave);
		
		// mcmmo �⺻ ��ġ ȹ��
		int skill_level = ExperienceAPI.getLevel(_player, "swords");
		int skill_exp = ExperienceAPI.getXP(_player, "swords");
		int skill_need_exp = ExperienceAPI.getXPToNextLevel(_player, "swords");

		// ��ų ��ġ ȹ��
		int mastery_dmg = 0, counter_dmg = 0, counter_cool = 0, critical_per = 0, critical_dmgper = 0, swordwave_dmg = 0;
		mastery_dmg = (int)Get_Data(_player, key_mastery_dmg);
		counter_dmg = (int)Get_Data(_player, key_counter_dmg);
		counter_cool = (int)Get_Data(_player, key_counter_cool);
		critical_per = (int)Get_Data(_player, key_critical_per);
		critical_dmgper = (int)Get_Data(_player, key_critical_dmgper);
		swordwave_dmg = (int)Get_Data(_player, key_swordwave_dmg);
		
		// ���� ����
		String temp = "";
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "Swords" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.GRAY + "����ġ�� ��� ��� : " + ChatColor.WHITE + "���� �����ϱ�" + "\n";
		temp += ChatColor.GRAY + "���� : " + skill_level + " " + ChatColor.DARK_AQUA + "����ġ : " + ChatColor.YELLOW + "(" + ChatColor.GOLD + skill_exp + "/" + skill_need_exp + ChatColor.YELLOW + ")" + "\n";

		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "ȿ��" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.DARK_AQUA + "�� �����͸� : " + ChatColor.GREEN + "�� ��ų, ���ݽ� �߰� ���� ����" + (has_mastery ? "" : ChatColor.GRAY + "(�̽���)")  + "\n";
		temp += ChatColor.DARK_AQUA + "�ݰ� : " + ChatColor.GREEN + "��ũ�� ���¿��� ���ظ� �޾��� �� �ݰ�" + (has_counter ? "" : ChatColor.GRAY + "(�̽���)")  +  "\n";
		temp += ChatColor.DARK_AQUA + "���� ���� : " + ChatColor.GREEN + "���� Ȯ���� �ִ� ���ط� ����"
									+ "\n           ������ �������� �� �ݰ��� ��Ÿ���� 3�� ����" + (has_critical ? "" : ChatColor.GRAY + "(�̽���)")  + "\n";
		temp += ChatColor.DARK_AQUA + "�������(�ɷ�) : " + ChatColor.GREEN + "���� ������ ���� ����"
									+ "\n                  ���ƿ��� ȭ���� �η��߸�" + (has_swordwave ? "" : ChatColor.GRAY + "(�̽���)")  + "\n";
		
		temp += ChatColor.RED + "-----[]" + ChatColor.GREEN + "����" + ChatColor.RED + "[]-----" + "\n";
		temp += ChatColor.RED + "�� �����͸� : " + ChatColor.YELLOW + "���� ����� �� ��� ��ų, ���ݿ� " + mastery_dmg + " ��ŭ�� �߰� ����" + "\n";
		temp += ChatColor.RED + "�ݰ� : " + ChatColor.YELLOW + "�ݰݽ� " + counter_dmg + " ��ŭ�� ����, ��Ÿ�� " + counter_cool + " ��" + "\n";
		temp += ChatColor.RED + "���� ���� : " + ChatColor.YELLOW + "������ �������� �� " + critical_per + "% Ȯ���� " + critical_dmgper + "%�� ����" + "\n";
		temp += ChatColor.RED + "������� : " + ChatColor.YELLOW + "�� �����͸� ȿ�� + " + swordwave_dmg + " ����";

		return temp;
	}

	@Override
	public double Get_Data(Player _player, String _key)
	{
		int sword_level = mcMMO.getDatabaseManager().loadPlayerProfile(_player.getUniqueId()).getSkillLevel(PrimarySkillType.SWORDS);
		switch (_key)
		{
		case key_mastery_dmg:
			return Math.min(20, sword_level / 200);
		case key_counter_dmg:
			return Math.min(60, sword_level / 100);
		case key_counter_cool:
			return Math.max(12, 60 - sword_level / 100);
		case key_critical_per:
			return 20;
		case key_critical_dmgper:
			return 150;
		case key_swordwave_dmg:
			return Math.min(20, sword_level / 200) + Math.min(80, 30 + sword_level / 200);
		case key_swordwave_count:
			return Math.min(8, 3 + sword_level / 400);
		}
		return 0;
	}
	
	// �̰� ���̿�?
	public static boolean Is_Sword(Material _material)
	{
		switch(_material)
		{
		case DIAMOND_SWORD:
		case WOODEN_SWORD:
		case STONE_SWORD:
		case GOLDEN_SWORD:
		case IRON_SWORD:
		case NETHERITE_SWORD:
			return true;
		default:
			break;
		}
		return false;
	}
	
	/**
	 * @param ��Ƽ�� ��ų ���
	 */
	public void Sword_Active_Use(Player player)
	{
		Manager_Timer mt = Manager_Timer.Get_Instance();
		Material handitem = player.getInventory().getItemInMainHand().getType();
		
		// ���� üũ
		if(!(player.hasPermission(perm_swordwave) || player.hasPermission("*")))
			return;
		
		// ���� ��� �ִ���, ��ũ�� ��������, ������������ Ȯ��
		if(!Is_Sword(handitem) || !player.isSneaking() || player.isFlying())
			return;
				
		// ��Ÿ�ӿ� �ش�Ǵ� ��� (��Ÿ���̶�� �ڵ����� �÷��̾�� ����)
		if(mt.Is_Timer_Available(player, swordwave_cooldown, true))
			return;
		
		/*
		// ��ų �غ������� ���� ���
		if(!mt.Is_Timer_Available(player, swordwave_ready, false))
		{
			// �غ�
			mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_READY, player, "�������", swordwave_ready, 20 * 2));
			return;
		}
		*/
		
		/*
		 * ������ ��ų ��� ����
		 */

		// ��Ÿ��
		mt.Remove_Timer(player, swordwave_ready); // �غ� Ÿ�̸� ����
		mt.Set_Timer(new Timer_Data(TIMER_TYPE.SKILL_COOLDOWN, player, "�������", swordwave_cooldown, 20 * 25));  // ��Ÿ�� ����
		
		// ��ų ���
		int wave_count = (int)Get_Data(player, key_swordwave_count);
		player.sendMessage(ChatColor.RED + "[ ������� �ߵ� ]");
		Bukkit.getScheduler().runTask(Main.mother, new Sword_Wave_Skill(player, wave_count));
	}

}

// �˼� �������  ȿ��
class Sword_Wave_Skill implements Runnable
{
	Player player;
	Vector skill_pos, dir, dir_2;
	int count;
	List<Entity> damaged_entities = new ArrayList<Entity>();
	List<Entity> entities;
	
	public Sword_Wave_Skill(Player p, int distance)
	{
		player = p;
		count = distance;
		skill_pos = player.getLocation().toVector().clone().add(new Vector(0, 1, 0));
		dir_2 = player.getLocation().getDirection().clone();
		dir_2.setY(0.0);
		dir = new Vector(Math.cos(Math.toRadians(player.getLocation().getYaw())), 3d * Math.cos(Math.random() * Math.PI ), Math.sin(Math.toRadians(player.getLocation().getYaw()))).normalize().clone();
		entities = player.getWorld().getEntities();
	}
	
	public void run()
	{
		double angle = 105d + Math.min(45, ExperienceAPI.getLevel(player, "swords") / 40);
		double power = 5.5;
		
		// �˱� �׸���
		Vector x1 = new Vector(-dir.getZ(), 0d, dir.getX()).normalize();
		Vector x2 = dir.clone().crossProduct(x1).normalize();

		player.getWorld().playSound(skill_pos.toLocation(player.getWorld()),
				count == 0 ? Sound.ENTITY_GENERIC_EXPLODE : Sound.ENTITY_BLAZE_SHOOT, 2f, 2f);
		
		// ��ƼŬ �׸���
		for(int i = 0; i < 60; i++)
		{
			double x = (power) * (Math.sin(Math.toRadians((180d - angle) / 2d) + (double)i / 60d * Math.toRadians(angle)));
			double z = (power) * (Math.cos(Math.toRadians((180d - angle) / 2d) + (double)i / 60d * Math.toRadians(angle)));
			Location temp2 = skill_pos.clone().add(x1.clone().multiply(x)).add(x2.clone().multiply(z)).add(new Vector(0.0, 1.0, 0.0)).toLocation(player.getWorld());
			temp2.setDirection(new Vector(0, 0, 0));
			player.getWorld().spawnParticle(count == 0 ? Particle.EXPLOSION_LARGE : Particle.CRIT, 
					temp2, 1, 0d, 0d, 0d, 0d);
		}
		
		// ���� ����
		for(int i = 0; i < entities.size(); i++)
		{
			Entity temp = entities.get(i);
			Location loc = temp.getLocation();
			loc.subtract(skill_pos);

			if(temp == player)
				continue;
			
			if(Math.sqrt(loc.getX() * loc.getX() + loc.getZ() * loc.getZ()) < (power + 3) && loc.getY() < 4 && loc.getY() > -3)
			{
				if(temp instanceof Arrow)
				{
					double vec = player.getLocation().getDirection().normalize().dot(loc.toVector().normalize());
					if(vec > Math.cos(Math.toRadians(angle / 2)))
					{
						Arrow temp2 = (Arrow)temp;
						if(!temp2.isOnGround())
						{
							player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, temp2.getLocation(), 1, 0d, 0d, 0d, 0d);
							temp2.remove();
						}
					}
				}
			}
			if(Math.sqrt(loc.getX() * loc.getX() + loc.getZ() * loc.getZ()) < (power) && loc.getY() < 3 && loc.getY() > -2)
			{
				if(!player.isFlying())
				{
					if(temp instanceof LivingEntity)
					{
						if(temp instanceof Animals)
						{
							continue;
						}
						double vec = dir_2.dot(loc.toVector().normalize());
						if(vec > Math.cos(Math.toRadians(angle / 2)))
						{
							int damage = 0;
							damage = (int)Manager_Sword_Ability.Instance.Get_Data(player, "swords.swordwave_dmg");
							
							LivingEntity temp2 = (LivingEntity)temp;
							if (!damaged_entities.contains(temp2))
							{
								Damage.Attack(player, temp2, damage, DamageType.WEAPON, DamageType.PHYSICAL);
								damaged_entities.add(temp2);
							}
							// EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(player, temp2, DamageCause.ENTITY_SWEEP_ATTACK, damage);
							// Bukkit.getPluginManager().callEvent(event);
							// PlayerAttackEvent attack_event = new PlayerAttackEvent(MMOData.get(player), event, result);
							// Bukkit.getPluginManager().callEvent(attack_event);
							
							// temp2.damage(damage, player);
							
						}
					}
				}
			}
		}
		
		// ��� ���� �ð�
		if(count > 0)
		{
			count--;
			skill_pos.add(dir_2.clone().multiply(power * 0.3));
			Bukkit.getScheduler().runTaskLater(Main.mother, this, 2L);
		}
	}
}












