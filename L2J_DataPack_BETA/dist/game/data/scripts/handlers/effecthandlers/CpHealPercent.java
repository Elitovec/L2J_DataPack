/*
 * Copyright (C) 2004-2014 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Cp Heal Percent effect implementation.
 * @author UnAfraid
 */
public final class CpHealPercent extends AbstractEffect
{
	public CpHealPercent(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.CPHEAL_PERCENT;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		L2Character target = info.getEffected();
		if ((target == null) || target.isDead() || target.isDoor())
		{
			return;
		}
		
		double amount = 0;
		double power = getValue();
		boolean full = (power == 100.0);
		
		amount = full ? target.getMaxCp() : (target.getMaxCp() * power) / 100.0;
		// Prevents overheal and negative amount
		amount = Math.max(Math.min(amount, target.getMaxRecoverableCp() - target.getCurrentCp()), 0);
		if (amount != 0)
		{
			target.setCurrentCp(amount + target.getCurrentCp());
		}
		
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED);
		sm.addInt((int) amount);
		target.sendPacket(sm);
	}
}
