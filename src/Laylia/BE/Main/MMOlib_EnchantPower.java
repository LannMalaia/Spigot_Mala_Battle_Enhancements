package Laylia.BE.Main;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.Type;

public class MMOlib_EnchantPower
{
	public static int Get_Sharpness(Player _player)
	{
		if (_player.getInventory().getItemInMainHand() != null)
			return _player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL);
		return 0;
	}
	public static int Get_Smite(Player _player)
	{
		if (_player.getInventory().getItemInMainHand() != null)
			return _player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
		return 0;
	}
	public static int Get_BaneofArthropods(Player _player)
	{
		if (_player.getInventory().getItemInMainHand() != null)
			return _player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);
		return 0;
	}

	public static int Get_Protect(Player _player)
	{
		int result = 0;
		for (ItemStack item : _player.getInventory().getArmorContents())
		{
			if (item == null)
				continue;
			
			NBTItem nbt = NBTItem.get(item);
			
			if (nbt == null)
				continue;
			if (!nbt.hasType())
				continue;
			
			if (nbt.getType().matches(Type.ARMOR.getId()))
			{
				result += nbt.toItem().getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
			}
		}
		return result;
	}
}
