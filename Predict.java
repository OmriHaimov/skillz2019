package bots;
import java.util.*;
import elf_kingdom.*;
public class Predict 
{
	private Game game;
	
	
	private ElfPredict[] enemyPredict;
	private ElfPredict[] myPredict;
	//int turns to get to destination 
	
	public Predict(Game g)
	{
		this.game=g;
		
		this.enemyPredict=new ElfPredict[game.getAllEnemyElves().length];
		this.myPredict=new ElfPredict[game.getAllMyElves().length];
		
		for(int i=0 ; i<enemyPredict.length ; i++)
		{
			enemyPredict[i]=new ElfPredict(g,game.getAllEnemyElves()[i]);
		}
		
		for(int i=0 ; i<myPredict.length ; i++)
		{
			myPredict[i]=new ElfPredict(g,game.getAllMyElves()[i]);
		}
	}
	
	public void fillFirst()
	{
		for(int i=0 ; i<enemyPredict.length ; i++)
		{
			Elf current=game.getAllEnemyElves()[i];
			if(current.isAlive())
			{
				enemyPredict[i].fillFirst();
			}
		}
		
		for(int i=0 ; i<myPredict.length ; i++)
		{
			Elf current=game.getAllMyElves()[i];
			if(current.isAlive())
			{
				myPredict[i].fillFirst();
			}
		}
	}
	
	public void fillSecond()
	{
		for(int i=0 ; i<enemyPredict.length ; i++)
		{
			Elf current=game.getAllEnemyElves()[i];
			if(current.isAlive())
			{
				enemyPredict[i].fillSecond();
			}
		}
		
		for(int i=0 ; i<myPredict.length ; i++)
		{
			Elf current=game.getAllMyElves()[i];
			if(current.isAlive())
			{
				myPredict[i].fillSecond();
			}
		}
	}
	
	public void fillMain()
	{
		for(int i=0 ; i<enemyPredict.length ; i++)
		{
			Elf current=game.getAllEnemyElves()[i];
			if(current.isAlive())
			{
				enemyPredict[i].mainFill();
			}
		}
		
		for(int i=0 ; i<myPredict.length ; i++)
		{
			Elf current=game.getAllMyElves()[i];
			if(current.isAlive())
			{
				myPredict[i].mainFill();
			}
		}
	}
	
	public Location getNext(int player, int id)
	{
		switch (player)
		{
		case 0: return myPredict[id].getNext();
		case 1: return enemyPredict[id].getNext();
		}
		return new Location (3200,3200);
	}
	
	public boolean willColide(Elf elf)
	{
		for(Elf e:game.getAllEnemyElves())
		{
			if(e.isAlive())
			{
				for(int i=0 ; i<15 ; i++)
				{
					if(myPredict[elf.id].getNextInTurns(i).inRange(enemyPredict[e.id].getNextInTurns(i), elf.attackRange))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void pred()
	{
		
	}
	
	public boolean iceWillAttackElf(Elf elf)
	{
		int dis=9999999;
		Elf closestElf=null;
		int dis1=99999;
		for(int i=0 ; i<game.getEnemyIceTrolls().length ; i++)
		{
			for(Creature c :game.getMyCreatures())
			{
				if(c.distance(game.getEnemyIceTrolls()[i])<dis)
				{
					dis=c.distance(game.getEnemyIceTrolls()[i]);
				}
			}
			for(Elf e :game.getMyLivingElves())
			{
				if(e.distance(game.getMyLivingElves()[i])<dis)
				{
					dis1=e.distance(game.getMyLivingElves()[i]);
					closestElf=e;
				}
			}
			if(dis1<dis && closestElf!=null)
			{
				if(closestElf.equals(elf))
					return true;
			}
		}
		return false;
	}
	
	/*public Location getNextMove(String player,int index)
	{
		
	}*/
	/*public int turnesToFight()
	{
		
	}*/
	
	//activate if turn>1
	//trying to predict enemy elf movement (next turn)
	/*public Location likelyGoTo()
	{
		this.headTo=prevLoc.towards(nextLoc, elf.maxSpeed*2);
		for(int i=300 ; i<7000 ; i+=100)
		{
			for(GameObject a:game.getAllBuildings())
			{
				if(elf.inRange(a, elf.attackRange))
				{
					this.headTo=a.getLocation();
				}
			}
			if(this.headTo.equals(prevLoc.towards(nextLoc, elf.maxSpeed*2)))
			{
				for(Elf a:game.getMyLivingElves())
				{
					if(elf.inRange(a, elf.attackRange))
					{
						this.headTo=a.getLocation();
					}
				}
			}
		}
		return headTo;
	}*/
}
