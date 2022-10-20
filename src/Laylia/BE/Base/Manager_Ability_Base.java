package Laylia.BE.Base;

import org.bukkit.entity.Player;

/**
 * @author jimja
 * @version 2020. 5. 5.
 * @apiNote ���  �����Ƽ���� �ٺ��� �Ǵ� Ŭ����
 * �̴�δ� ��� �Ұ���
 */
public abstract class Manager_Ability_Base
{	
	/**
	 * @author jimja
	 * @version 2020. 5. 5.
	 * @apiNote �ش� �����Ƽ�� ���� ������ �÷��̾�� ����
	 */
	public void Get_Desc(Player _player)
	{
		_player.sendMessage(Make_Description(_player));
	};

	/**
	 * @author jimja
	 * @version 2020. 5. 5.
	 * @apiNote �ش� �����Ƽ�� ���� ������ ����
	 */
	protected abstract String Make_Description(Player _player);
	
	public abstract double Get_Data(Player _player, String _key);
}