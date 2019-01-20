/*
 * Copyright (C) 2004-2019 L2J DataPack
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

package events.ZakenCurse;

import java.util.Calendar;
import java.util.Set;

import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.gameserver.util.Broadcast;
import com.l2jserver.util.Rnd;

/**
 * Traces of Evil (268)
 * @author xban1x
 */
public final class ZakenCurse extends Quest
{
	
	private static final String qn = "ZakenCurse";
	// event properties
	
	private static final int[] EVENT_DATE =
	{
		07,
		01
	}; // {DD, MM}
	
	private static final int EVENT_NPC = 32365;
	private static final int EVENT_DAY_TO_STAY = 3; // how many days after event date Event_Npc will be respawn (for giving reward)
	private static final int CUPID_BOW = 9141;
	private static final int GOLDEN_APIGA = 9143;
	
	private static final int[] SKILLS =
	{
		3260,
		3262
	};
	private static final int[] CHANCE =
	{
		45,
		5,
		2
	}; // {HugePigs, SuperHugePig, GoldenPig}
	private static final int[] REWARD =
	{
		1,
		7,
		10
	}; // {HugePigs, SuperHugePig, GoldenPig}
	private static final int[] Pigs =
	{
		13031,
		13032,
		13033,
		13034,
		13035
	};
	// Misc
	private static final int MIN_LVL = 20;
	
	private static final int[][] EM =
	{
		{
			10693,
			17345,
			-4590,
			45486
		},
		{
			115095,
			-178309,
			-917,
			10631
		},
		{
			47566,
			51138,
			-3001,
			33264
		},
		{
			-45272,
			-112396,
			-245,
			652
		},
		{
			-84542,
			244682,
			-3735,
			53988
		},
		{
			147855,
			26629,
			-2210,
			19523
		},
		{
			16249,
			142870,
			-2711,
			12022
		},
		{
			17832,
			170509,
			-3531,
			48408
		},
		{
			83057,
			149281,
			-3474,
			31176
		},
		{
			-80858,
			149456,
			-3070,
			16948
		},
		{
			-12147,
			122760,
			-3102,
			34490
		},
		{
			110954,
			218935,
			-3548,
			0
		},
		{
			117158,
			75807,
			-2735,
			25189
		},
		{
			82494,
			53151,
			-1501,
			946
		},
		{
			43556,
			-47626,
			-802,
			42552
		},
		{
			147388,
			-55436,
			-2738,
			62376
		},
		{
			87775,
			-143216,
			-1298,
			27295
		}
	};
	
	private static final int[][] MOB =
	{
		{
			12739,
			172819,
			-3415,
			53544
		},
		{
			13386,
			172680,
			-3452,
			63628
		},
		{
			13304,
			173135,
			-3449,
			53988
		},
		{
			13229,
			173748,
			-3463,
			19658
		},
		{
			14639,
			174127,
			-3486,
			65020
		},
		{
			14924,
			174953,
			-3602,
			7150
		},
		{
			15625,
			175086,
			-3662,
			59709
		},
		{
			16243,
			174928,
			-3644,
			60588
		},
		{
			16217,
			174024,
			-3691,
			47810
		},
		{
			16989,
			173868,
			-3626,
			64213
		},
		{
			16919,
			173152,
			-3598,
			43726
		},
		{
			16477,
			172906,
			-3565,
			35563
		},
		{
			15708,
			173583,
			-3576,
			25291
		},
		{
			17796,
			173812,
			-3645,
			63328
		},
		{
			18306,
			174341,
			-3680,
			8494
		},
		{
			18731,
			174004,
			-3639,
			55432
		},
		{
			19147,
			174213,
			-3598,
			64786
		},
		{
			19715,
			173364,
			-3578,
			55248
		},
		{
			20446,
			173433,
			-3577,
			5533
		},
		{
			20738,
			173844,
			-3577,
			59844
		},
		{
			20235,
			172255,
			-3582,
			45099
		},
		{
			21085,
			172082,
			-3577,
			63738
		},
		{
			21857,
			172346,
			-3567,
			2650
		},
		{
			21763,
			173214,
			-3577,
			16169
		},
		{
			21583,
			171240,
			-3547,
			59056
		},
		{
			20920,
			170514,
			-3556,
			41043
		},
		{
			20314,
			169681,
			-3532,
			49447
		},
		{
			19792,
			169950,
			-3560,
			26862
		},
		{
			19419,
			168903,
			-3480,
			48761
		},
		{
			18970,
			169172,
			-3483,
			29138
		},
		{
			18014,
			168372,
			-3524,
			38624
		},
		{
			18275,
			167273,
			-3486,
			50371
		},
		{
			17227,
			167052,
			-3519,
			34197
		},
		{
			16007,
			167490,
			-3546,
			24702
		},
		{
			14782,
			167310,
			-3643,
			36856
		},
		{
			14296,
			167874,
			-3636,
			16980
		},
		{
			14201,
			169242,
			-3616,
			19444
		},
		{
			14354,
			170546,
			-3558,
			13843
		},
		{
			15016,
			170893,
			-3485,
			3419
		},
		{
			15978,
			171333,
			-3555,
			11655
		},
		{
			21891,
			170635,
			-3516,
			49199
		},
		{
			21252,
			169570,
			-3443,
			50277
		},
		{
			18828,
			168262,
			-3463,
			34177
		},
		{
			17409,
			167654,
			-3460,
			33168
		},
		{
			15513,
			168057,
			-3512,
			21964
		},
		{
			12083,
			172092,
			-3514,
			29851
		},
		{
			12720,
			173616,
			-3463,
			10111
		},
		{
			14383,
			173431,
			-3459,
			53120
		},
		{
			14621,
			173096,
			-3485,
			57956
		},
		{
			14555,
			172248,
			-3479,
			48166
		},
		{
			15692,
			171932,
			-3564,
			62491
		},
		{
			17146,
			172578,
			-3546,
			4609
		},
		{
			17973,
			173254,
			-3580,
			4655
		},
		{
			19411,
			173905,
			-3604,
			2154
		},
		{
			20159,
			174128,
			-3577,
			54788
		},
		{
			23193,
			172600,
			-3472,
			554
		},
		{
			23850,
			172349,
			-3396,
			57840
		},
		{
			24495,
			171338,
			-3381,
			54967
		},
		{
			25261,
			170669,
			-3385,
			60908
		},
		{
			26423,
			170887,
			-3413,
			943
		},
		{
			26357,
			170026,
			-3348,
			26605
		},
		{
			25473,
			169179,
			-3330,
			40551
		},
		{
			26009,
			168673,
			-3295,
			64740
		},
		{
			26556,
			168599,
			-3253,
			2226
		},
		{
			27314,
			168350,
			-3220,
			47035
		},
		{
			27086,
			166750,
			-3365,
			46402
		},
		{
			27221,
			165848,
			-3447,
			59247
		},
		{
			26110,
			165447,
			-3437,
			34146
		},
		{
			26548,
			164573,
			-3496,
			54428
		},
		{
			23190,
			166013,
			-3339,
			23885
		},
		{
			21682,
			167176,
			-3370,
			24088
		},
		{
			20640,
			167878,
			-3390,
			27964
		},
		{
			20771,
			168524,
			-3395,
			13984
		},
		{
			19174,
			170072,
			-3558,
			21523
		},
		{
			18615,
			170401,
			-3507,
			54788
		},
		{
			21504,
			173649,
			-3577,
			2177
		},
		{
			21986,
			171263,
			-3567,
			55609
		},
		{
			23064,
			170718,
			-3419,
			19893
		},
		{
			23737,
			171751,
			-3408,
			9846
		},
		{
			24991,
			172189,
			-3388,
			7754
		},
		{
			25393,
			173308,
			-3428,
			15644
		},
		{
			27313,
			172418,
			-3388,
			56835
		},
		{
			28511,
			172474,
			-3476,
			14904
		},
		{
			28403,
			170634,
			-3390,
			44315
		},
		{
			27401,
			169594,
			-3271,
			43576
		},
		{
			26214,
			169225,
			-3256,
			38961
		},
		{
			29224,
			166327,
			-3548,
			56697
		},
		{
			28861,
			164369,
			-3632,
			38263
		},
		{
			15806,
			164244,
			-3644,
			48476
		},
		{
			15933,
			165069,
			-3556,
			15252
		},
		{
			15342,
			165490,
			-3557,
			26398
		},
		{
			14679,
			166316,
			-3611,
			16307
		},
		{
			15156,
			166694,
			-3551,
			14326
		},
		{
			13016,
			166792,
			-3713,
			3823
		},
		{
			11674,
			166816,
			-3719,
			39015
		},
		{
			11058,
			167733,
			-3696,
			19620
		},
		{
			9968,
			167180,
			-3604,
			43620
		},
		{
			9112,
			167076,
			-3641,
			29917
		},
		{
			9528,
			168170,
			-3545,
			12466
		},
		{
			9690,
			169159,
			-3535,
			60733
		},
		{
			9969,
			170049,
			-3538,
			8305
		},
		{
			11123,
			171612,
			-3605,
			9787
		},
		{
			10489,
			173775,
			-3660,
			23256
		},
		{
			9770,
			175077,
			-3662,
			19973
		},
		{
			10552,
			175728,
			-3623,
			8360
		},
		{
			11806,
			176327,
			-3583,
			6701
		},
		{
			13250,
			172087,
			-3477,
			36585
		},
		{
			14915,
			171874,
			-3504,
			3516
		},
		{
			16333,
			170029,
			-3558,
			64040
		},
		{
			17037,
			168803,
			-3544,
			54926
		},
		{
			16409,
			166252,
			-3494,
			45158
		},
		{
			14410,
			163938,
			-3702,
			41423
		},
		{
			13200,
			165174,
			-3696,
			19611
		},
		{
			12473,
			166578,
			-3675,
			21933
		},
		{
			13818,
			168499,
			-3660,
			14813
		},
		{
			18012,
			174804,
			-3673,
			1808
		},
		{
			23437,
			169290,
			-3436,
			64996
		},
		{
			25486,
			167606,
			-3227,
			23874
		},
		{
			22918,
			165340,
			-3340,
			37225
		},
		{
			20191,
			166388,
			-3405,
			31650
		},
		{
			19683,
			167733,
			-3395,
			15523
		}
	};
	
	public ZakenCurse(int questId, String name, String descr)
	{
		super(-1, qn, "events");
		System.out.println("Zaken's Curse: Event Zaken's Curse Init.");
		
		addStartNpc(EVENT_NPC);
		addTalkId(EVENT_NPC);
		
		unspawnNpc(EVENT_NPC); // prevent multiple spawn if server reboot
		unspawnNpc(Pigs[0]);
		if (!isEventPassed(0))
		{
			System.out.println("Zaken's Curse: Event Zaken's Curse Is Running.");
			System.out.println("Zaken's Curse: Event Manager Need To Be Spawn...");
			spawnNpc("Event Manager", EVENT_NPC, EM);
			System.out.println("Zaken's Curse: Event Mobs Need To Be Spawn...");
			spawnNpc("Event Mobs", Pigs[0], MOB);
			Broadcast.toAllOnlinePlayers("Event Zaken's Curse: Go talk to an Event Manager!");
			
			for (int i : Pigs)
			{
				addSkillSeeId(i);
				addSpellFinishedId(i);
			}
		}
		else
		{
			System.out.println("Zaken's Curse: Event Zaken's Curse Has Passed.");
			if (!isEventPassed(EVENT_DAY_TO_STAY))
			{
				System.out.println("Zaken's Curse: Event Manager Need To Be Spawn...");
				spawnNpc("Event Manager", EVENT_NPC, EM);
			}
		}
	}
	
	private boolean isEventPassed(int val)
	{
		Calendar now = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, (EVENT_DATE[0] + val));
		c.set(Calendar.MONTH, (EVENT_DATE[1] - 1));
		long time = now.getTimeInMillis() - c.getTimeInMillis();
		return (time > 65000) || (time < -65000);
	}
	
	private void unspawnNpc(int npcId)
	{
		System.out.println("Zaken's Curse: Deleting npc(" + npcId + ") From Table Spawnlist.");
		Set<L2Spawn> t = SpawnTable.getInstance().getSpawns(npcId);
		if (t != null)
		{
			for (L2Spawn spawn : t)
			{
				System.out.println("Zaken's Curse: @ [" + spawn.getX() + "," + spawn.getY() + "," + spawn.getZ() + "," + spawn.getHeading() + "]");
				SpawnTable.getInstance().deleteSpawn(spawn, false);
				L2Npc npc = spawn.getLastSpawn();
				npc.deleteMe();
			}
		}
	}
	
	private void spawnNpc(String npcName, int npcId, int[][] list)
	{
		System.out.println("Zaken's Curse: Spawning " + npcName + "...");
		L2NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
		for (int[] data : list)
		{
			System.out.println("Zaken's Curse: @ [" + data[0] + "," + data[1] + "," + data[2] + "," + data[3] + "]");
			L2Spawn spawn;
			try
			{
				spawn = new L2Spawn(template);
				spawn.setX(data[0]);
				spawn.setY(data[1]);
				spawn.setZ(data[2]);
				spawn.setAmount(1);
				spawn.setHeading(data[3]);
				spawn.setRespawnDelay(60);
				SpawnTable.getInstance().addNewSpawn(spawn, false); // don't save spawn in DB
				spawn.init();
			}
			catch (Exception e)
			{
				_log.warning("Could not spawn Npc " + npcId);
				e.printStackTrace();
			}
		}
		System.out.println("Zaken's Curse: Done...");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmltext = null;
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "31857-03.htm":
			{
				st.startQuest();
				st.giveItems(CUPID_BOW, 1, 16);
				htmltext = event;
				break;
			}
			case "31857-06.htm":
			{
				if (st.getQuestItemsCount(GOLDEN_APIGA) <= 100)
				{
					htmltext = "31857-03.htm";
				}
				else
				{
					int i0 = Rnd.get(100);
					if (i0 < 1)
					{
						st.giveItems(46686, 1, 16);
					}
					else if (i0 < 2)
					{
						st.giveItems(54295, 2);
					}
					else if (i0 < 5)
					{
						st.giveItems(54296, 1);
					}
					else if (i0 < 34)
					{
						st.giveItems(54297, 10);
					}
					else if (i0 < 40)
					{
						st.giveItems(54046, 5);
					}
					else if (i0 < 45)
					{
						st.giveItems(54242, 1);
					}
					else if (i0 < 49)
					{
						st.giveItems(54038, 20);
					}
					else if (i0 < 52)
					{
						st.giveItems(54202, 2);
					}
					else if (i0 < 54)
					{
						st.giveItems(54201, 2);
					}
					else if (i0 < 55)
					{
						st.giveItems(54254, 30);
					}
					else if (i0 < 75)
					{
						st.giveItems(54284, 1);
					}
					else if (i0 < 90)
					{
						st.giveItems(54283, 1);
					}
					else
					{
						st.giveItems(22222, 1);
					}
					st.takeItems(GOLDEN_APIGA, 100);
					st.unset("cond");
					playSound(player, Sound.ITEMSOUND_QUEST_FINISH);
					st.exitQuest(true, true);
					htmltext = "31857-08.htm";
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance player, Skill skill, L2Object[] targets, boolean isPet)
	{
		return onSkillUse(npc, player, skill);
	}
	
	private String onSkillUse(L2Npc npc, L2PcInstance player, Skill skill)
	{
		// gather some values on local variables
		QuestState st = player.getQuestState(qn);
		int npcId = npc.getId();
		int skillId = skill.getId();
		// check if the npc and skills used are valid for this script. Exit if invalid.
		if (!com.l2jserver.gameserver.util.Util.contains(Pigs, npcId))
		{
			return null;
		}
		if (!com.l2jserver.gameserver.util.Util.contains(SKILLS, skillId))
		{
			return null;
		}
		if (isEventPassed(0))
		{
			for (int pigId : Pigs)
			{
				unspawnNpc(pigId);
			}
			Broadcast.toAllOnlinePlayers("Event Zaken's Curse has ended, get your reward to an Event Manager!");
			Broadcast.toAllOnlinePlayers(new ExShowScreenMessage("Event Zaken's Curse has ended, get your reward to an Event Manager", 10000));
			return null;
		}
		npc.onDecay();
		if (skillId == 3260)
		{
			int chance = Rnd.get(1, 100);
			// Heart Shot : can only spawn another Huge Pig
			if (npcId < 13033)
			{
				if (chance <= CHANCE[0])
				{
					// 13031 -> 13032 -> 13033
					addSpawn(npcId + 1, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 60000);
				}
				else
				{
					st.giveItems(GOLDEN_APIGA, REWARD[0]);
				}
				return null;
			}
			else if (npcId == 13034)
			{
				st.giveItems(GOLDEN_APIGA, REWARD[1]);
			}
			else if (npcId == 13035)
			{
				st.giveItems(GOLDEN_APIGA, REWARD[2]);
			}
			return null;
		}
		
		if (skillId == 3262)
		{
			int chance = Rnd.get(1, 100);
			// Double Heart Shot : can spawn Huge Pig, Super Huge Pig and Golden Pig
			if (npcId < 13033)
			{
				if (chance <= CHANCE[2])
				{
					addSpawn(13035, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 60000);
				}
				else if (chance <= CHANCE[1])
				{
					addSpawn(13034, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 60000);
				}
				else if (chance <= CHANCE[0])
				{
					if (npcId < 13033)
					{
						// 13031 -> 13032 -> 13033
						addSpawn(npcId + 1, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 60000);
					}
				}
				else
				{
					st.giveItems(GOLDEN_APIGA, REWARD[0]);
				}
			}
			else
			{
				if (npcId == 13034)
				{
					st.giveItems(GOLDEN_APIGA, REWARD[1]);
				}
				else if (npcId == 13035)
				{
					st.giveItems(GOLDEN_APIGA, REWARD[2]);
				}
				return null;
			}
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st != null)
		{
			switch (st.getState())
			{
				case State.CREATED:
				{
					htmltext = (player.getLevel() >= MIN_LVL) ? "31857-02.htm" : "31857-01.htm";
					break;
				}
				case State.STARTED:
				{
					switch (st.getCond())
					{
						case 1:
						{
							htmltext = (!st.hasQuestItems(GOLDEN_APIGA)) ? "31857-04.htm" : "31857-05.htm";
							break;
						}
						case 2:
						{
							if (st.getQuestItemsCount(GOLDEN_APIGA) >= 10000000)
							{
								st.giveAdena(2474, true);
								st.addExpAndSp(8738, 409);
								st.exitQuest(true, true);
								htmltext = "31857-06.html";
							}
							break;
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new ZakenCurse(-1, qn, "events");
	}
	
}