package bots;
import elf_kingdom.*;
public class Execute 
{
	private AssignElf[] elfs;
	private AssignPortal[] portals;
	private Game game;
	
	public Execute(Game game, AssignElf[] a, AssignPortal[] b)
	{
		this.game=game;
		this.elfs=a;
		this.portals=b;
	}
	
	public void doIt()
	{
	    try
		{
			for(int i=0 ; i<portals.length ; i++)
			{
				Portal p=portals[i].getPoratl();
				switch(portals[i].getTask())
				{
				case summonice: p.summonIceTroll(); game.debug(p+" SUMMONED ICE"); break;
				case summonlava: p.summonLavaGiant(); game.debug(p+" SUMMONED LAVA");  break;
				case summonTornado: p.summonTornado(); game.debug(p+" SUMMONED ELECTRA"); break;
				default: /* */ break;
				}
			}
			
		}
		catch(Exception e)
		{
			game.debug(e.toString());
			game.debug(e.getStackTrace());
		}	
		try
		{	
			for(int i=0 ; i<elfs.length ; i++)
			{
			    Elf elf=elfs[i].getElf();
			    if(elf!=null)
			    {
			    	if(!Util.didRotate[elf.id])
			    	{
			    		MyBot.rotateDirection[elf.id]=-999;
			    	}
			        switch(elfs[i].getTask())
    			    {
    			        case attack: 
    			            elf.attack(Util.attackTarget[elf.id]);
    			            game.debug("ELF:  "+elf +"  ATTACKED: "+Util.attackTarget[elf.id]);
    			            break;
    		           case walk: 
    		               elf.moveTo(Util.destination[elf.id]);
    		               game.debug(elf+" WALK TO "+Util.destination[elf.id]);
    		               break;
    	               case buildPortal:
    	                   elf.buildPortal();
    	                   game.debug(elf+" BUILDING PORTAL");
    	                   break;
    	               case buildMana:
    	            	   elf.buildManaFountain();
    	            	   game.debug(elf+"  BUILDING MANA");
    	            	   break;
    	               case castInv: 
    	            	   elf.castInvisibility();
    	            	   game.debug(elf+ "CASTED INVISABILITY");
    	            	   break;
    	               case castSpeed:
    	            	   elf.castSpeedUp();
    	            	   game.debug(elf+"SPEEDY GONZALES");
					default:
						break;
    			    }  
			    }
			}
		}
		catch(Exception e1)
		{
			game.debug(e1.toString());
			game.debug(e1.getStackTrace());
		}
	}
	
}
