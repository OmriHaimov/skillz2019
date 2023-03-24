package bots;
import elf_kingdom.*;
public class AssignElf 
{
	private Elf elf;
	private ElfTask task;
	private GameObject attacking;
	private Location go2;
	
	public AssignElf(Elf e,ElfTask a)
	{
		this.elf=e;
		this.task=a;
	}
	
	public AssignElf(Elf e)
	{
		this.elf=e;
	}
	
	public ElfTask getTask()
	{
		return this.task;
	}
	
	public Elf getElf()
	{
		return this.elf;
	}
	
	public void setTask(ElfTask a)
	{
		this.task=a;
		
	}
	public GameObject getAttacking()
	{
	    return this.attacking;
	}
	
	public void setAttacking(GameObject a)
	{
	    this.attacking=a;
	}
	
	public Location getDest()
	{
	    return this.go2;
	}
	
	public void setDest(Location a)
	{
	    this.go2=a;
	}
}
