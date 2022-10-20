package Laylia.BE.Main;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import Laylia.BE.Base.Timer_Data;


/**
 * @author jimja
 * @version 2020. 5. 5.
 * @apiNote ��Ÿ�� ������
 */
public class Manager_Timer implements Runnable
{
	// ���
	public static final long update_tick = 20;
	
	// �̱���
	private static Manager_Timer Instance; // �̱��� ������Ʈ
	public static Manager_Timer Get_Instance()
	{
		if(Instance == null)
			Instance = new Manager_Timer();
		
		return Instance;
	}
	private Manager_Timer()
	{
		m_Timer_List = new ArrayList<Timer_Data>();
	}
	
	// ����
	ArrayList<Timer_Data> m_Timer_List; // �÷��̾���� ��Ÿ�� ����Ʈ
	
	// �� �ð� ȣ��
	@Override
	public void run()
	{
		for(int i = 0; i < m_Timer_List.size(); i++)
		{
			Timer_Data data = m_Timer_List.get(i);
			if(data.Update_Timer(update_tick))
			{
				m_Timer_List.remove(data);
				i--;
			}
		}
	}
	
	// �޼ҵ�
	/**
	 * @apiNote �ش� Ÿ�̸� ��ü�� ��� �ִ����� Ȯ���Ѵ�
	 * @param _player �÷��̾� ��ü
	 * @param _key ��ų Ű ����
	 * @param _alarm ��Ÿ�� ���̶�� �̸� �˷��ش�
	 * @return ��Ÿ�� ���̶�� true, �ƴ϶�� false
	 */
	public boolean Is_Timer_Available(Player _player, String _key, boolean _alarm)
	{
		for(Timer_Data data : m_Timer_List)
		{
			// ����Ʈ ���� �ִ� �����Ͱ� �Ķ���Ϳ� ���� ��� true ��ȯ
			if(data.is_Similar(_player, _key))
			{
				if(_alarm)
					data.Send_Playing_Message_To_Player();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @apiNote Ÿ�̸� ����
	 * @param _data
	 */
	public void Set_Timer(Timer_Data _data)
	{
		for(Timer_Data data : m_Timer_List)
		{
			// ����Ʈ ���� �̹� �����Ͱ� �ִ� ��쿡�� ��Ÿ�Ӹ� ����
			if(data.is_Similar(_data))
			{
				data.Set_Timer(_data.Get_Timer());
				return;
			}
		}
		m_Timer_List.add(_data);
	}
	
	/**
	 * @apiNote Ÿ�̸� ����
	 * @param _data
	 */
	public void Remove_Timer(Player _player, String _key)
	{
		for(int i = 0; i < m_Timer_List.size(); i++)
		{
			Timer_Data data = m_Timer_List.get(i);
			if(data.is_Similar(_player, _key))
			{
				m_Timer_List.remove(data);
				return;
			}
		}
	}
	
	/**
	 * @apiNote Ÿ�̸� ����
	 * @param _tick 1�� = 20ƽ, ������� �Ƿ� Ÿ�̸Ӱ� �����Ѵ�
	 */
	public void Sub_Timer(Player _player, String _key, long _tick)
	{
		for(int i = 0; i < m_Timer_List.size(); i++)
		{
			Timer_Data data = m_Timer_List.get(i);
			if(data.is_Similar(_player, _key))
			{
				if(data.Update_Timer(_tick))
				{
					m_Timer_List.remove(data);
					return;
				}
			}
		}
	}
}
