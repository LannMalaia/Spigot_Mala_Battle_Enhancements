package Laylia.BE.Base;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author jimja
 * @version 2020. 5. 5.
 * @apiNote ��Ÿ�� ������
 */
public class Timer_Data
{
	private TIMER_TYPE m_Type; // Ÿ�̸� Ÿ��
	private Player m_Player; // �÷��̾�
	private String m_Skill_Name; // ��ų��
	private String m_Skill_Key; // ��ų��
	private long m_Remained_Timer; // �ܿ� ��Ÿ��
	
	/**
	 * @apiNote ��Ÿ�� ���� ����ü
	 * @param _player �÷��̾� ������Ʈ
	 * @param _name ��ų �̸�
	 * @param _key ��ų Ű ����
	 * @param _Timer_ticks ��Ÿ�� ƽ (1�� = 20ƽ)
	 */
	public Timer_Data(TIMER_TYPE _type, Player _player, String _name, String _key, long _Timer_ticks)
	{
		m_Type = _type;
		m_Player = _player;
		m_Skill_Name = new String(_name);
		m_Skill_Key = new String(_key);
		m_Remained_Timer = _Timer_ticks;
		
		Send_Start_Message_To_Player();
	}

	// ��Ÿ�� �޼ҵ�
	public void Set_Timer(long _time)
	{
		m_Remained_Timer = _time;
	}
	public long Get_Timer()
	{
		return m_Remained_Timer;
	}
	/**
	 * @apiNote ��Ÿ���� ������Ʈ�Ѵ�
	 * @param _time ������Ʈ �� ƽ (1�� = 20ƽ)
	 * @return �Ҹ�Ǿ�� �ϴ� ��� true�� ��ȯ
	 */
	public boolean Update_Timer(long _time)
	{
		m_Remained_Timer -= _time;
		if(m_Remained_Timer <= 0)
		{
			// ��Ÿ���� �� �� ���
			Send_End_Message_To_Player();
			return true;
		}
		return false;
	}

	// �޽��� �޼ҵ�
	/**
	 * @apiNote ������ ��쿡 �޽����� ������
	 */
	public void Send_Playing_Message_To_Player()
	{
		switch(m_Type)
		{
		case SKILL_COOLDOWN:
			m_Player.sendMessage(ChatColor.RED + m_Skill_Name + "��(��) ���� ����� �� �����ϴ�."
					+ ChatColor.YELLOW + " (���� �ð�: " + (m_Remained_Timer / 20) + "��)");
			break;
		}
	}
	/**
	 * @apiNote Ÿ�̸Ӱ� ���۵��� �� �޽����� ������
	 */
	public void Send_Start_Message_To_Player()
	{
		switch(m_Type)
		{
		case SKILL_READY:
			m_Player.sendMessage(ChatColor.AQUA + "[ " + m_Skill_Name + " �غ� ]");
			break;
		case SKILL_USE:
			m_Player.sendMessage(ChatColor.RED + "[ " + m_Skill_Name + " �ߵ� ]");
			break;
		}
	}
	/**
	 * @apiNote Ÿ�̸Ӱ� ������ �� �޽����� ������
	 */
	public void Send_End_Message_To_Player()
	{
		switch(m_Type)
		{
		case SKILL_READY:
			m_Player.sendMessage(ChatColor.GRAY + "[ " + m_Skill_Name + " �غ� ������ ]");
			break;
		case SKILL_USE:
			m_Player.sendMessage(ChatColor.GRAY + "[ " + m_Skill_Name + " ���� ]");
			break;
		case SKILL_COOLDOWN:
			m_Player.sendMessage(ChatColor.YELLOW + m_Skill_Name + "��(��) ���� ��� �����մϴ�.");
			break;
		}
	}
	
	// �� �޼ҵ�
	/**
	 * @param _data ���� ���
	 * @return ���� ���ٸ� true, �ƴϸ� false
	 */
	public boolean is_Similar(Timer_Data _data)
	{
		return m_Player == _data.m_Player && m_Skill_Key.equals(_data.m_Skill_Key);
	}
	/**
	 * @param _player ���� �÷��̾�
	 * @param _key ���� ��ų Ű ����
	 * @return ���� ���ٸ� true, �ƴϸ� false
	 */
	public boolean is_Similar(Player _player, String _key)
	{
		return m_Player == _player && m_Skill_Key.equals(_key);
	}
}