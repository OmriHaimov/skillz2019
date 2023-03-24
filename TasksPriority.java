package bots;
import java.util.*;
import elf_kingdom.*;
public class TasksPriority 
{
	private Game game;
	private Util util;
	private Tasks task;
	private Elf[] Aelf;
	private Portal[] Aportal;
	private AssignElf[] elfAssign;
	private AssignPortal[] portalAssign;
	
	public static boolean portalInDanger;
	
	public TasksPriority(Game g,Util u,Tasks t)
	{
		this.game=g;
		this.util=u;
		this.Aelf=g.getMyLivingElves();
		this.Aportal=g.getMyPortals();
		portalAssign=new AssignPortal[this.Aportal.length];
		elfAssign=new AssignElf[this.Aelf.length];
		this.task=t;
	}
	
	@SuppressWarnings("incomplete-switch")
	public void handleElfs()
	{
		for(int i=0 ; i<this.Aelf.length ; i++)
		{
			 this.elfAssign[i] =new AssignElf(Aelf[i]);
		}
		for(int i=0 ; i<this.Aelf.length ; i++)
		{
			this.elfAssign[i].setTask(task.elfTask(this.elfAssign[i].getElf()));
			switch(this.elfAssign[i].getTask())
			{
			    case walk: this.elfAssign[i].setDest(this.util.getDestination()[i]); break;
			    case attack: this.elfAssign[i].setAttacking(this.util.getAttackTarget()[i]); break;
			}
		}
	}
	
	public void handlePortals()
	{
	/*	try
		{*/
			for(int i=0  ; i<this.Aportal.length; i++)
			{
				 this.portalAssign[i] =new AssignPortal(Aportal[i]);
				 
			}
			for(int i=0  ; i<this.Aportal.length; i++)
			{
				this.portalAssign[i].setTask(task.portalTask(this.portalAssign[i].getPoratl()));
			}
	/*	}
		catch(Exception e)
		{
			game.debug(e);
			game.debug(e.getStackTrace());
		}*/
	}
	
	public AssignPortal[] getPortalAssigns()
	{
		return this.portalAssign;
	}
	
	public AssignElf[] getElfAssigns()
	{
		return this.elfAssign;
	}
}
