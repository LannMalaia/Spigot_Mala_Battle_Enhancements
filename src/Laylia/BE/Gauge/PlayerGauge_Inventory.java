package Laylia.BE.Gauge;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerGauge_Inventory
{
	
	public static void Call_Inv(Player _player)
	{
		PlayerGauge pg = PG_Manager.Instance.Get_Gauge(_player);
		Inventory inv = Bukkit.createInventory(null, 9, "��1��l������ ������");

		inv.setItem(3, HP_Toggle(pg.HP));
		inv.setItem(4, Mana_Toggle(pg.Mana));
		inv.setItem(5, Stamina_Toggle(pg.Stamina));

		inv.setItem(8, Simple_Toggle(pg.SIMPLE));
		
		_player.openInventory(inv);
	}
	
	public static void When_Inv_Clicked(Player _player, ItemStack _item)
	{
		PlayerGauge pg = PG_Manager.Instance.Get_Gauge(_player);
		if (_item == null)
			return;
		if (_item.isSimilar(HP_Toggle(true)))
			pg.HP = false;
		else if (_item.isSimilar(HP_Toggle(false)))
			pg.HP = true;
		if (_item.isSimilar(Mana_Toggle(true)))
			pg.Mana = false;
		else if (_item.isSimilar(Mana_Toggle(false)))
			pg.Mana = true;
		if (_item.isSimilar(Stamina_Toggle(true)))
			pg.Stamina = false;
		else if (_item.isSimilar(Stamina_Toggle(false)))
			pg.Stamina = true;
		if (_item.isSimilar(Simple_Toggle(true)))
			pg.SIMPLE = false;
		else if (_item.isSimilar(Simple_Toggle(false)))
			pg.SIMPLE = true;
		pg.Save_PG();
		pg.Update_Gauge();
		Call_Inv(_player);
	}

	public static ItemStack HP_Toggle(boolean _is_on)
	{
		ItemStack item = new ItemStack(Material.REDSTONE);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lores = new ArrayList<String>();
		meta.setDisplayName("��f��l����� - " + (_is_on ? "��b��l����" : "��8��l����"));
		lores.add("��7����� �������� �Ѱų� ���ϴ�.");
		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack Mana_Toggle(boolean _is_on)
	{
		ItemStack item = new ItemStack(Material.SOUL_CAMPFIRE);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lores = new ArrayList<String>();
		meta.setDisplayName("��f��l���� - " + (_is_on ? "��b��l����" : "��8��l����"));
		lores.add("��7���� �������� �Ѱų� ���ϴ�.");
		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack Stamina_Toggle(boolean _is_on)
	{
		ItemStack item = new ItemStack(Material.YELLOW_DYE);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lores = new ArrayList<String>();
		meta.setDisplayName("��e��l���¹̳� - " + (_is_on ? "��b��l����" : "��8��l����"));
		lores.add("��7���¹̳� �������� �Ѱų� ���ϴ�.");
		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack Simple_Toggle(boolean _is_on)
	{
		ItemStack item = new ItemStack(Material.SNOWBALL);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lores = new ArrayList<String>();
		meta.setDisplayName(_is_on ? "��e��l���� ���" : "��e��l������ ���");
		if (_is_on)
		{
			lores.add("��7Ŭ���Ͽ� ������ ���� ��ȯ�� �� �ֽ��ϴ�.");
		}
		else
		{
			lores.add("��7Ŭ���Ͽ� ���� ���� ��ȯ�� �� �ֽ��ϴ�.");
		}
		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}

}
