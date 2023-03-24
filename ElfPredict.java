package bots;
import elf_kingdom.*;
public class ElfPredict 
{
	private Game game;
	private Elf elf;
	
	private Location first;
	private Location second;
	private Location next=new Location(-100,-100);
	
	private int streak=0;
	
	public ElfPredict(Game game,Elf elf)
	{
		this.game=game;
		this.elf=elf;
	}
	
	public void fillFirst()
	{
		this.first=elf.getLocation();
	}
	
	public void fillSecond()
	{
		this.second=elf.getLocation();
		this.next=second.towards(first, -elf.maxSpeed);
	}
	
	public void mainFill()
	{
		boolean temp=false;
		Spell[] arr=elf.currentSpells;
		for(int i=0 ; i<arr.length ; i++)
		{
			if(arr[i] instanceof Invisibility)
				temp= true;
		}
		if(temp)
		{
		    if(second.towards(first, -elf.maxSpeed*5).inMap())
		    {
			    this.next=second.towards(first, -elf.maxSpeed*5);
		    }
		    else
		    {
		        this.next=second;
		    }
		}
		else
		{
			this.thirdFill();
		}
	}
	
	public void thirdFill()
	{
		if(elf.getLocation().inRange(next, 100))
		{
			this.streak++;
		}
		else
		{
			this.streak=0;
		}
		
		this.first=this.second;
		this.second=elf.getLocation();
		if(second.towards(first, -elf.maxSpeed*2).inMap())
	    {
		    this.next=second.towards(first, -elf.maxSpeed*2);
	    }
	    else
	    {
	        this.next=second;
	    }
	}
	
	public Location getFirst()
	{
		return this.first;
	}
	
	public Location getSecond()
	{
		return this.second;
	}
	
	public Location getNext()
	{
		if(elf.isAlive() && !next.equals(new Location(-100,-100)))
			return next;
		return elf.getLocation();
	}
	
	public Location getNextInTurns(int turns)
	{
		return this.second.towards(first, -elf.maxSpeed*(turns+1));
	}
}
