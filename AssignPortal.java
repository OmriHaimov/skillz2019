package bots;
import elf_kingdom.*;
public class AssignPortal 
{
	private Portal portal;
	private PortalTask task;
	
	public AssignPortal(Portal p)
	{
		this.portal=p;
	}
	
	public AssignPortal(Portal p,PortalTask t)
	{
		this.portal=p;
		this.task=t;
	}
	
	public Portal getPoratl()
	{
		return this.portal;
	}
	
	public PortalTask getTask()
	{
		return this.task;
	}
	
	public void setTask(PortalTask t)
	{
		this.task=t;
	}
}
