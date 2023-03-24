package bots;

import elf_kingdom.*;

public class Util 
{
	private Game game;
	private Predict predict;
	public static Location[] destination;
	public static GameObject[] attackTarget;
	public static GameObject[] target;
	public static boolean[] didRotate;
	public double mapSize;
	public Util(Game game,Predict p)
	{
		this.game=game;
		this.predict=p;
		mapSize=Math.sqrt(game.cols*game.cols+game.rows*game.rows);
		Util.attackTarget=new GameObject[game.getAllMyElves().length];
		Util.target=new GameObject[game.getAllMyElves().length];
		Util.destination=new Location[game.getAllMyElves().length];
		Util.didRotate=new boolean[game.getAllMyElves().length];
		for(int i=0 ; i<Util.didRotate.length ; i++)
		{
			Util.didRotate[i]=false;
		}
	}
	
	public Location[] getDestination()
	{
	    return Util.destination;
	}
	
	public GameObject[] getAttackTarget()
	{
	    return Util.attackTarget;
	}
	
	public ElfTask tryAttackOrMove(Elf elf, GameObject danger)
	{
		if(elf.inAttackRange(danger))
	    {
	        Util.attackTarget[elf.id]=danger;
	    	return ElfTask.attack;
	    }
	    else
	    {
	    	if(this.elfInDanger(elf.getLocation().towards(danger, elf.maxSpeed)) && elf.currentHealth<5)
	    	{
	    		if(this.getThreat(elf) instanceof IceTroll)
	    		{
	    			IceTroll it=(IceTroll)this.getThreat(elf);
	    			if(this.distracted(it, elf))
	    			{
	    				if(elf.inAttackRange(it))
	    				{
	    					Util.attackTarget[elf.id]=it;
	    					return ElfTask.attack;
	    				}
	    				else
	    				{
	    					if(elf.distance(it)<500)
	    					{
	    						Util.destination[elf.id]=it.getLocation().towards(elf, elf.attackRange);
	        					return ElfTask.walk;
	    					}
	    					else
	    					{
	    						return this.rotate(elf, it.getLocation());
	    					}
	    				}
	    			}
	    		}
	    		if(winOrRun(elf,danger)==ElfTask.donothing)
			    {
	    		      Util.destination[elf.id]=danger.getLocation().towards(elf,elf.attackRange);
	    		      return ElfTask.walk;
			    }
	    		return this.runOrRotate(elf, this.getThreat(elf));
	    	}
	    	else
	    	{
	    	    game.debug("came herfeeeeeeeeeeeee");
	    		if(this.elfInDanger(elf.getLocation().towards(danger,elf.maxSpeed)))
	    		{
	    		    if(this.isOkToAttackV2(elf))
	    		    {
	    		        if(elf.inAttackRange(danger))
        		        {
        		            Util.attackTarget[elf.id]=danger;
        		            return ElfTask.attack;
        		        }
        		        else
        		        {
        		            if(danger instanceof Elf)
        		        	{
        		        		Elf temp=(Elf)danger;
        		        		Util.destination[elf.id]=predict.getNext(1, temp.id).getLocation().towards(elf,elf.attackRange);
            		            return ElfTask.walk;
        		        	}
        		        	else
        		        	{
        		        		Util.destination[elf.id]=danger.getLocation().towards(elf,elf.attackRange);
            		            return ElfTask.walk;
        		        	}
        		        }
	    		    }
	    		    else
	    		    {
	    		        GameObject temp123=this.getThreat(elf);
	    		        if(temp123.inRange(game.getMyCastle(),1000))
	    		        {
	    		            if(elf.inAttackRange(temp123))
	    		            {
	    		                Util.attackTarget[elf.id]=temp123;
	    		                return ElfTask.attack;
	    		            }
	    		            else
	    		            {
	    		                Util.destination[elf.id]=temp123.getLocation();
	    		                return ElfTask.walk;
	    		            }
	    		        }
	    		        else
	    		        {
	    		            return this.runOrRotate(elf, temp123);
	    		        }
	    		    }
	    		}
	    		else
	    		{
	    			Util.destination[elf.id]=danger.getLocation().towards(elf,elf.maxSpeed);
	            	return ElfTask.walk;
	    		}
	    	}
	    }
	}

	public ElfTask runOrRotate(Elf elf,GameObject danger)
	{
		Location[] arr= {new Location(1,1),new Location(1,game.cols-1),new Location(game.rows-1,1),new Location(game.rows-1,game.cols-1)};
		for(Location i : arr)
		{
			if(elf.inRange(i, 75))
			{
				if(!this.isInvis(elf) && elf.canCastInvisibility() && MyBot.mana>=game.invisibilityCost)
				{
					MyBot.mana-=game.invisibilityCost;
					return ElfTask.castInv;
				}
			}
		}
		if(danger!=null)
		{
			/*if(danger.distance(elf)>=400)
			{
				this.rotate(elf, danger.getLocation());
				if(Util.destination[elf.id]!=null)
					return this.rotate(elf, danger.getLocation());
				else
					return this.runAway(elf,danger);
			}
			else
			{
				return this.runAway(elf,danger);
			}*/
			if(Util.target[elf.id]!=null)
			{
			    game.debug(elf+"     "+Util.target[elf.id]);
			    if(Util.target[elf.id].currentHealth==1 && elf.inAttackRange(Util.target[elf.id]))
			    {
			        Util.attackTarget[elf.id]=Util.target[elf.id];
			        return ElfTask.attack;
			    }
			}
		    if(elf.distance(danger)>game.iceTrollAttackRange+game.iceTrollMaxSpeed)
		    {
		        game.debug("reacheddddddddddddddddd    "+danger);
		        Location loc=elf.getLocation();
    			Location dangerLoc=danger.getLocation();
    			if(Util.target[elf.id]==null)
    				return ElfTask.donothing;
    			Location third=Util.target[elf.id].getLocation();
    			Location middle=new Location((loc.row+dangerLoc.row+third.row)/3,(loc.col+dangerLoc.col+third.col)/3);
    			if(elf.distance(middle)<=this.distanceToBorder(loc) && !elf.inRange(middle,300))
    			{
    				return this.runAndRotate(elf, danger);
    			} 
    			
		    }
		    /*else
		    {
		        game.debug("exiteddddddddddddddddddd");
		        return this.runAway(elf,danger);
		    }*/
		}
		game.debug("exiteddddddddddddddddddd    "+danger);
		if(Util.target[elf.id]!=null && this.isOkToAttackV2(elf))
		{
		    if(elf.inAttackRange(Util.target[elf.id]))
    		{
    		    Util.attackTarget[elf.id]=Util.target[elf.id];
    		    return ElfTask.attack;
    		}
		}
		return this.runAway(elf,danger);
	}
	
	public double distanceToBorder(Location loc)
	{
		int c=loc.col;
		int r=loc.row;
		Location[] arr= {new Location(1,c),new Location(game.rows-1,c),new Location(r,1),new Location(r,game.cols-1)};
		int dis=99999;
		for(int i=0 ; i<arr.length ; i++)
		{
			if(loc.distance(arr[i])<dis)
			{
				dis=loc.distance(arr[i]);
			}
		}
		return dis;
	}

	public boolean attackOrNot(Elf danger, Elf me)
	{
	    if(danger.currentHealth<me.currentHealth-1 && me.inAttackRange(danger))
	    {
	        attackTarget[me.id]=danger;
	        return true;
	    }
	    return false;
	}
	
	public ElfTask winOrRun(Elf elf, GameObject threat)
	{
		int threats=this.countDefendingIceTrolls(elf.getLocation(), game.getEnemyIceTrolls(), game.iceTrollAttackRange+game.iceTrollMaxSpeed+elf.maxSpeed);
		threats+=this.countElvesInRange(elf.getLocation(), game.getEnemyLivingElves(), elf.attackRange+elf.maxSpeed*2);
		int defenders=this.countDefendingIceTrolls(threat.getLocation(), game.getMyIceTrolls(), game.iceTrollAttackRange+game.iceTrollMaxSpeed+elf.maxSpeed);
		defenders+=this.countElvesInRange(threat.getLocation(), game.getMyLivingElves(), elf.attackRange+elf.maxSpeed*2);
		GameObject danger=this.getClosestElf(elf.getLocation(), game.getEnemyLivingElves());
		if(danger==null)
			danger=this.getClosestIceTroll(elf.getLocation(), game.getEnemyIceTrolls());
		if(danger==null)
			return ElfTask.donothing;
		if(threats>defenders)
		{
			return this.runOrRotate(elf, danger);
		}
		return ElfTask.donothing;
	}
	
	/*public ElfTask checkLoc(Elf elf)
	{
		if(this.nextLocationInDanger(Util.destination[elf.id]))
		{
			if(!this.isInvis(elf) && elf.canCastInvisibility() && MyBot.mana>=game.invisibilityCost)
			{
				MyBot.mana-=game.invisibilityCost;
				return ElfTask.castInv;
			}
		}
		return ElfTask.walk;
	}*/

	public ElfTask runAway(Elf elf,GameObject danger)
	{
		Location move2=elf.getLocation().towards(danger, -elf.maxSpeed);
		if(elf.distance(danger)<=400)
		{
			if(move2.inMap())
			{
				Util.destination[elf.id]=move2;
				return ElfTask.walk;
				//return this.checkLoc(elf);
			}
			else
			{
				return runAwayEndOfMap(elf,danger);
			}
		}
		ElfTask e=this.attackPriority(elf);
		if(e!=ElfTask.donothing)
		{
			return e;
		}
		if(this.elfInDanger(danger.getLocation().towards(elf, elf.maxSpeed*2)) && elf.distance(danger)<=700)
		{
			if(move2.inMap())
			{
				Util.destination[elf.id]=move2;
				return ElfTask.walk;
				//return this.checkLoc(elf);
			}
			else
			{
				return runAwayEndOfMap(elf,danger);
			}
		}
		else
		{
			Util.destination[elf.id]=danger.getLocation().towards(elf, elf.attackRange);
			return ElfTask.walk;
		}
	}
	
	public ElfTask runAwayEndOfMap(Elf elf,GameObject danger)
	{
		Location elfLoc=elf.getLocation();
		Location dangerLoc=danger.getLocation();
		int rowDif=elfLoc.row-dangerLoc.row;
		int colDif=elfLoc.col-dangerLoc.col;
		Location dest=game.getMyCastle().getLocation();
		Location corner=this.closestCorner(elfLoc);
		if(rowDif>0 && colDif>0)
		{
			dest=new Location(game.rows-1,game.cols-1);
		}
		if(rowDif>0 && colDif<0)
		{
			dest=new Location(game.rows-1,0+1);
		}
		if(rowDif<0 && colDif<0)
		{
			dest=new Location(0+1,0+1);
		}
		if(rowDif<0 && colDif>0)
		{
			dest=new Location(0+1,game.cols-1);
		}
		Util.destination[elf.id]=dest;
		return ElfTask.walk;
	}
	
	public boolean isInvis(Elf elf)
	{
		Spell[] arr=elf.currentSpells;
		for(int i=0 ; i<arr.length ; i++)
		{
			if(arr[i] instanceof Invisibility)
				return true;
		}
		return false;
	}
	
	public ElfTask runAndRotate(Elf elf,GameObject danger)
	{
		Location loc=elf.getLocation();
		Location dangerLoc=danger.getLocation();
		if(Util.target[elf.id]==null)
			return ElfTask.donothing;
		Location third=Util.target[elf.id].getLocation();
		Location middle=new Location((loc.row+dangerLoc.row+third.row)/3,(loc.col+dangerLoc.col+third.col)/3);
		return this.rotate(elf, middle);
	}
	
	public GameObject getTarget(Elf elf)
	{
		Location loc=elf.getLocation();
		Portal enemyClosest=this.getClosestPortal(loc, game.getEnemyPortals());
		ManaFountain enemyFountain=this.getClosestMana(loc, game.getEnemyManaFountains());
		if(enemyClosest==null && enemyFountain==null)
		{
			return game.getEnemyCastle();
		}
		if(enemyClosest==null)
		{
			return enemyFountain;
		}
		if(enemyFountain==null)
		{
			return enemyClosest;
		}
		if(enemyClosest.distance(elf)<enemyFountain.distance(elf))
		{
			return enemyFountain;
		}
		return enemyClosest;
	}
	
	public boolean isFast(Elf elf)
	{
		Spell[] arr=elf.currentSpells;
		for(int i=0 ; i<arr.length ; i++)
		{
			if(arr[i] instanceof SpeedUp)
				return true;
		}
		return false;
	}
	
	public boolean clearPath(Elf elf, GameObject target) 
	{
		for(IceTroll i : game.getEnemyIceTrolls()) 
		{ 
			if(this.onVector(elf.getLocation(), target.getLocation(), i.getLocation(), i.attackRange)) 
				return false; 
		}
		return true; 
	}
	
	public ElfTask attackPriority(Elf elf)
	{
		if(this.isOkToAttackV2(elf)==false)
			return ElfTask.donothing;
	    try
	    {
	    	for(Elf el : game.getEnemyLivingElves())
	        {
				if(elf.inAttackRange(el))
				{
					Util.attackTarget[elf.id]=el;
					return ElfTask.attack;
				}
	        }
	        for(ManaFountain f:game.getEnemyManaFountains())
	    	{
	    		if(elf.inAttackRange(f))
	            {
	               Util.attackTarget[elf.id]=f;
	               return ElfTask.attack;
	            } 
	    	}
	    	for(Portal p : game.getEnemyPortals())
	    	{
	    		if(elf.inAttackRange(p))
	            {
	               Util.attackTarget[elf.id]=p;
	               return ElfTask.attack;
	            } 
	    	} 
	    	for(Tornado t : game.getEnemyTornadoes())
	    	{
	    	    if(elf.inAttackRange(t))
	    	    {
	    	        Util.attackTarget[elf.id]=t;
	    	        return ElfTask.attack;
	    	    }
	    	}
	    	if(elf.inAttackRange(game.getEnemyCastle()) && game.getEnemyManaFountains().length==0)
	      	{	
	    		Util.attackTarget[elf.id]=game.getEnemyCastle();
	    	  	return ElfTask.attack;
	      	} 
	    	return ElfTask.donothing;
	    }
	    catch(Exception e)
	    {
	        game.debug(e);
	        return ElfTask.donothing;
	    }
	}

	public boolean onVector(Location start,Location end,Location test,int range)
	{
		for(int i=0 ; i<start.distance(end) ; i+=200)
		{
			if(test.inRange(start.towards(end, i), range))
				return true;
		}
		return false;
	}
	
	public boolean shouldPlacePortal(Elf elf)
	{
		Location elfLoc=elf.getLocation();
		for(Portal p: game.getMyPortals())
		{
			if(this.onVector(elfLoc, game.getEnemyCastle().getLocation(), p.getLocation(), p.size+100))
				return false;
		}
		return true;
	}

	//rotates elf around an object
	public ElfTask rotate(Elf elf, Location center)
	{
		Location elfLoc=elf.getLocation();
		double distance=elf.distance(center);
		double rowDif=(center.row-elfLoc.row);
		double colDif=(center.col-elfLoc.col);
		double angle=0;
		if(colDif>0 && rowDif<0)
		{
		    angle=270+Math.abs(Math.toDegrees(Math.atan(colDif/rowDif)));
		}
		if(colDif<0 && rowDif<0)
		{
		    angle=180+(Math.toDegrees(Math.atan(rowDif/colDif)));
		}
		if(colDif<0 && rowDif>0)
		{
			angle=90+Math.abs(Math.toDegrees(Math.atan(colDif/rowDif)));
		}
		if(colDif>=0 && rowDif>=0)
		{
			angle=Math.toDegrees(Math.atan(rowDif/colDif));
		}
		Util.destination[elf.id]=(bestLocation(elf,(int)distance,angle,center)); 
		return ElfTask.walk;
	}

	//find the safest location for the elf to go to
	public Location bestLocation(Elf elf,int dis,double angle,Location center)
	{
		Location toRet=null;
		Util.didRotate[elf.id]=true;
		double max=9999999;
		double newAngle=angle-20;
		double newRow=Math.sin(Math.toRadians(newAngle))*dis;
		double newCol=Math.cos(Math.toRadians(newAngle))*dis;
		Location temp=new Location((int)newRow,(int)newCol);
		Location temp1=center.subtract(temp);
		Location temp2=elf.getLocation().towards(temp1, elf.maxSpeed);
		if(MyBot.rotateDirection[elf.id]==-999)
		{
		    IceTroll troll=getClosestIceTroll(temp2,game.getEnemyIceTrolls());
		    GameObject target=Util.target[elf.id];
		    if(target!=null)
		    {
		        double distanceToEnemyIceTroll=temp2.distance(target);
    			if(distanceToEnemyIceTroll<max)
    			{
    				max=distanceToEnemyIceTroll;
    				toRet=temp2;
    				MyBot.rotateDirection[elf.id]=1;
    			} 
		    }
			
			newAngle=angle+20;
			newRow=Math.sin(Math.toRadians(newAngle))*dis;
			newCol=Math.cos(Math.toRadians(newAngle))*dis;
			temp=new Location((int)newRow,(int)newCol);
			temp1=center.subtract(temp);
			temp2=elf.getLocation().towards(temp1, elf.maxSpeed);
			if(target!=null)
		    {
		        double distanceToEnemyIceTroll=temp2.distance(target);
    			if(distanceToEnemyIceTroll<max)
    			{
    				max=distanceToEnemyIceTroll;
    				toRet=temp2;
    				MyBot.rotateDirection[elf.id]=0;
    			} 
		    }
			
		}
		else
		{
			if(MyBot.rotateDirection[elf.id]==1)
				angle=-20;
			else
				angle=20;
			newRow=Math.sin(Math.toRadians(newAngle))*dis;
			newCol=Math.cos(Math.toRadians(newAngle))*dis;
			temp=new Location((int)newRow,(int)newCol);
			temp1=center.subtract(temp);
			temp2=elf.getLocation().towards(temp1, elf.maxSpeed);
			//1= rotate clockwise 
			IceTroll troll=getClosestIceTroll(temp2,game.getEnemyIceTrolls());
			GameObject target=Util.target[elf.id];
			if(target!=null)
		    {
		        double distanceToEnemyIceTroll=temp2.distance(target);
    			if(distanceToEnemyIceTroll<max)
    			{
    				max=distanceToEnemyIceTroll;
    				toRet=temp2;
    				MyBot.rotateDirection[elf.id]=1;
    			} 
		    }   
			
		}
		return toRet;
	}
	
	public boolean attackNotPlace(Elf elf)
	{
		GameObject target=Util.target[elf.id];
		if(target==null)
			return true;
		if(target instanceof Elf)
		{
			Elf e=(Elf)target;
			Castle myCastle=game.getMyCastle();
			if(this.isFast(e) && elf.distance(myCastle)>predict.getNext(1, e.id).distance(myCastle) && elf.inRange(e, e.maxSpeed*8))
			{
				return false;
			}
		}
		return true;
	}

    public Location iceTrollNextMove(IceTroll ice)
    {
        Location toRet=ice.getLocation();
        int distance=999999;
        for(Creature c:game.getMyCreatures())
        {
            if(c.distance(ice)<distance)
            {
                distance=c.distance(ice);
                toRet=ice.getLocation().towards(c,ice.maxSpeed);
            }
            if(c.inRange(ice,ice.attackRange))
            {
                toRet=ice.getLocation();
            }
        }
        return toRet;
    }
    
    public boolean nextLocationInDanger(Location loc)
    {
        if(loc!=null)
        {
            for(Elf e : game.getEnemyLivingElves())
        	{
        		if(e.inAttackRange(loc))
        		{
        			return true;
        		}
        	}
        	for(IceTroll t : game.getEnemyIceTrolls())
        	{
        		if(t.inRange(loc, t.attackRange))
        		{
        			return true;
        		}
        	} 
        }
    	return false;
    }

	public boolean isOkToAttack(Elf elf)
	{
		int threats=this.countDefendingIceTrolls(elf.getLocation(), game.getEnemyIceTrolls(), game.iceTrollAttackRange);
		if(threats==0)
			return true;
		if(threats==1)
		{
			IceTroll threat=this.getClosestIceTroll(elf.getLocation(), game.getEnemyIceTrolls());
			if(threat!=null)
			{
				if(this.distracted(threat, elf))
					return true;
				else
				{
					return false;
				}
			}
		}
		int defenders=this.countDefendingIceTrolls(elf.getLocation(), game.getMyIceTrolls(), game.iceTrollAttackRange);
		return (threats<=defenders);
	}
	
	public boolean isOkToAttackV2(Elf elf)
	{
		int enemyhp=0,myhp=0;
		for(IceTroll i : game.getEnemyIceTrolls())
		{
			if(i.currentHealth>1 && i.inRange(elf, 500))
			{
				enemyhp+=i.currentHealth;
			}
		}
		for(IceTroll i : game.getMyIceTrolls())
		{
			if(i.currentHealth>1 && i.inRange(elf, 500))
			{
				myhp+=i.currentHealth;
			}
		}
		for(Elf e: game.getEnemyLivingElves())
		{
			if(e.inRange(elf, 500))
			{
				enemyhp+=e.currentHealth;
			}
		}
		for(Elf e: game.getMyLivingElves())
		{
			if(e.inRange(elf, 500))
			{
				myhp+=e.currentHealth;
			}
		}
		return myhp>=enemyhp;
	}
	
	public boolean elfEngage(Elf elf)
	{
		int myElves=this.countElvesInRange(elf.getLocation(), game.getMyLivingElves(), elf.attackRange);
		int enemyElves=this.countElvesInRange(elf.getLocation(), game.getEnemyLivingElves(), elf.attackRange);
		return myElves>=enemyElves;
	}
	
	public boolean portalInDanger()
	{
		for(Portal p : game.getMyPortals())
		{
			Elf closest=this.getClosestElf(p.getLocation(), game.getEnemyLivingElves());
			if(closest==null)
			    return false;
			if(p.inRange(closest, p.size+1300))
				return true;
		}
		return false;
	}
	
	//returns closest ice troll to a given location out of inserted array of ice trolls
	public IceTroll getClosestIceTroll(Location loc,IceTroll[] index)
    {
        int min=55555555;
        IceTroll toret=null;
        for(IceTroll it : index)
        {
            if(loc.distance(it)<min)
            {
                min=(loc.distance(it));
                toret=it;
            }
        }
        return toret;
    }
	
	//returns the closest enemy elf to a given location, if the elf is to far or dead, returns closest portal
	public GameObject getClosest(Location location,GameObject[] index)
    {
        int min=5555555;
        GameObject toret=null;
        for (GameObject o : index)
        {
            if(o.distance(location)<min)
            {
                min=o.distance(location);
                toret=o;
            }
        }
        return toret;
    }
	
	public Tornado getClosestTornado(Location loc, Tornado[] index)
	{
		int min=5555555;
		Tornado toret=null;
        for (Tornado o : index)
        {
            if(o.distance(loc)<min)
            {
                min=o.distance(loc);
                toret=o;
            }
        }
        return toret;
	}
	
	//returns the closest portal to a given location, shows the closest portal out of array of inserted portals
	public Portal getClosestPortal(Location location,Portal[] index)
    {
        int min=10000000;
        Portal toret=null;
        for(Portal p : index)
        {
            if(location.distance(p)<min)
            {
                min=location.distance(p);
                toret=p;
            }
        }
        return toret;
    }
	
	public Elf getClosestElf(Location loc,Elf[] index)
	{
		int min=Integer.MAX_VALUE;
		Elf toRet=null;
		for(Elf a: index)
		{
			if(a.distance(loc)<min && a.distance(loc)!=0)
			{
				min=a.distance(loc);
				toRet=a;
			}
		}
		return toRet;
	}
	
	public ManaFountain getClosestMana(Location loc,ManaFountain[] index)
	{
		int min=Integer.MAX_VALUE-20;
		ManaFountain toRet=null;
		for(ManaFountain a: index)
		{
			if(a.distance(loc)<min+10 && a.distance(loc)!=0)
			{
				min=a.distance(loc);
				toRet=a;
			}
		}
		return toRet;
	}

	//returns number of enemies in specific radius
	public int countEnemiesAttackingCastle(Location castle, Creature[] b,Elf[] index)
	{
		int a=0;
		for(Creature c : b)
		{
			if(c.currentHealth>1 && c.inRange(castle, c.attackRange+c.maxSpeed*4))
			{
				a++;
			}
		}
		for (Elf c : index)
		{
		    if(c.currentHealth>0 && c.inRange(castle, c.attackRange+c.maxSpeed*4))
			{
				a++;
			}
		}
		return a;
	}
	
	//returns number of ice trolls in defence
	public int countDefendingIceTrolls(Location castle, GameObject[] a, int range)
	{
		int b=0;
		for(GameObject c : a)
		{
			if(c.inRange(castle, range) && c.currentHealth>=3)
			{
				b++;
			}
		}
		return b;
	}
	
	public int countElvesInRange(Location center,Elf[] index,int radius)
	{
		int b=0;
		for(Elf c : index)
		{
			if(c.distance(center)<=radius)
			{
				b++;
			}
		}
		return b;
	}

    public Elf getClosestElfLowLife(Elf elf,Elf[] index)
    {
        int lowHp=9999;
        Elf toRet=null;
        for(int i=0 ; i<index.length ; i++)
        {
            if(elf.inAttackRange(index[i]) && index[i].currentHealth<lowHp)
            {
                lowHp=index[i].currentHealth;
                toRet=index[i];
            }
        }
        return toRet;
    }
	
	public boolean elfInDanger(Location elf)
	{
	    try 
	    {
	        if(elf==null)
			    return false;
    		if(this.game.getEnemyIceTrolls().length==0 && this.game.getEnemyLivingElves().length==0)
    			return false;
			GameObject it=getClosestIceTroll(elf.getLocation(),game.getEnemyIceTrolls());
			if(it!=null)
			{
			   if(it.distance(elf)<=((game.iceTrollMaxSpeed+game.elfMaxSpeed+game.iceTrollAttackRange)*2))
    			return true; 
			}
    		Elf a=getClosestElf(elf.getLocation(),this.game.getEnemyLivingElves());
    		if(a!=null)
    		{
    			if(a.distance(elf)<=a.maxSpeed*6+a.attackRange*2)
    				return true;
    		}
    		return false;
	    } 
	    catch(Exception e) 
	    {
	        this.game.debug(e+"             fdsasasdfsdfs");
	        return false;
	    } 
	}
	
	public GameObject getThreat(Elf elf)
	{
	    GameObject ice=getClosestIceTroll(elf.getLocation(),game.getEnemyIceTrolls());
	    Elf dangerElf=getClosestElf(elf.getLocation(),this.game.getEnemyLivingElves());
	    if(ice==null)
	        return dangerElf;
	    if(dangerElf==null)
	        return ice;
	    if(elf.distance(dangerElf)<elf.distance(ice)*2)
	        return (GameObject)dangerElf;
	    return ice;     
	}
	
	public boolean distracted(GameObject attacker,Elf elf)
	{
		int a=Integer.MAX_VALUE;
		for(IceTroll c: game.getMyIceTrolls())
		{
			if(c.distance(attacker)<a && c.currentHealth>1)
			{
				a=c.distance(attacker);
			}
		}
		//
		return a<=elf.distance(attacker) || elf.distance(attacker)>=500;
	}
	
	public boolean canBuild(Location buildAt,int b)
	{
		boolean a=true;
		for(Building c : game.getAllBuildings())
		{
			if(buildAt.inRange(c, c.size+b))
				a=false;
		}
		for(Location loc : MyBot.newBuildingLoc)
		{
			if(loc!=null)
			{
				if(buildAt.inRange(loc, game.portalSize+b))
					a=false;
			}
		}
		return a;
	}
	
	public Location closestCorner(Location loc)
	{
		Location[] arr= {new Location(0,0),new Location(0,game.cols),new Location(game.rows,game.cols),new Location(game.rows,0)};
		int a=9999999,index=0;
		for(int i=0 ; i<arr.length ; i++)
		{
			if(loc.distance(arr[i])<a)
			{
				a=loc.distance(arr[i]);
				index=i;
			}
		}
		return arr[index];
	}
	public boolean anyElfInDanger()
	{
		for(Elf e:game.getMyLivingElves())
		{
			if(this.elfEngage(e))
				return true;
		}
		return false;
	}
	
	public boolean castleInDanger(Castle castle)
	{
		for(LavaGiant a: game.getEnemyLavaGiants())
		{
			if(a.inRange(castle, castle.size+a.attackRange+a.maxSpeed*4))
				return true;
		}
		for(Elf a: game.getEnemyLivingElves())
		{
			if(a.inRange(castle, castle.size+a.attackRange+a.maxSpeed*4))
				return true;
		}
		return false;
	}
	public boolean hasAttackPortal(int a)
	{
		if(a==1)
		{
			Castle enemyCastle=game.getEnemyCastle();
			Portal closestAllyPortalToEnemyCity=this.getClosestPortal(enemyCastle.getLocation(), game.getMyPortals());
			if(closestAllyPortalToEnemyCity!=null)
				return closestAllyPortalToEnemyCity.inRange(enemyCastle, (int)(mapSize/2.3));
			else
				return false;
		}
		else
		{
			Castle myCastle=game.getMyCastle();
			Portal closestAllyPortalToEnemyCity=this.getClosestPortal(myCastle.getLocation(), game.getEnemyPortals());
			if(closestAllyPortalToEnemyCity!=null)
				return closestAllyPortalToEnemyCity.inRange(myCastle, (int)(mapSize/2.3));
			else
				return false;
		}
	}
	
	public boolean liklyToLive(Elf elf, int turns)
	{
		int count=0;
		int dis=99999;
		for(IceTroll ice : game.getEnemyIceTrolls())
		{
			for(Creature c : game.getMyCreatures())
			{
				if(c.distance(ice)<dis)
					dis=c.distance(ice);
			}
			if(elf.distance(ice)<dis)
				count++;
			dis=999999;
		}
		int count2=0;
		for(Elf e : game.getEnemyLivingElves())
		{
			if(e.inAttackRange(elf))
			{
				count2++;
			}
		}
		return elf.currentHealth-turns*count>0 && elf.currentHealth-count2*turns>0;
	}
	
	public boolean canPortalSpawn(Portal portal,int index)
	{
		int torCount=this.countDefendingIceTrolls(portal.getLocation(), game.getEnemyTornadoes(), portal.size+game.tornadoAttackRange);
		int allyTor=this.countDefendingIceTrolls(portal.getLocation(), game.getMyTornadoes(), portal.size+game.tornadoAttackRange+game.tornadoMaxSpeed*5);
		return (portal.currentHealth-(torCount*index)>0 /*&& allyTor==0*/);
	}
	
	public boolean canPlacePortal(Elf elf)
	{
	    int ice=this.countDefendingIceTrolls(elf.getLocation(),game.getEnemyIceTrolls(),game.iceTrollAttackRange);
	    int count=0;
	    for(Elf e : game.getEnemyLivingElves())
	    {
	        if(e.inRange(elf,e.attackRange+e.maxSpeed))
	            count++;
	    }
	    
	    return (elf.currentHealth-((ice*game.iceTrollAttackMultiplier+count*game.elfAttackMultiplier)*game.portalBuildingDuration)>0);
	}
	
	public boolean saveForTor()
	{
		/*Castle myCastle = game.getMyCastle();
		Portal closestEnemyPortalToMe=this.getClosestPortal(myCastle.getLocation(), game.getEnemyPortals());
	    Portal closestAllyPortalToMe=this.getClosestPortal(myCastle.getLocation(), game.getMyPortals());
		if(closestEnemyPortalToMe!=null && closestAllyPortalToMe!=null)
    	{
    	    if(closestEnemyPortalToMe.inRange(myCastle, (int) (mapSize/2.5)) && closestEnemyPortalToMe.inRange(closestAllyPortalToMe, closestAllyPortalToMe.size*3))
	    	    return true;
    	}
		*/
		for(Portal me : game.getMyPortals())
		{
			for(Portal enemy : game.getEnemyPortals())
			{
				if(me.inRange(enemy, me.size*6) && this.countDefendingIceTrolls(me.getLocation(), game.getMyTornadoes(), me.size+game.tornadoAttackRange+game.tornadoMaxSpeed*5)==0)
		    	    return true;
			}
		}
		Castle myCastle = game.getMyCastle();
		Portal closestEnemyPortalToMe=this.getClosestPortal(myCastle.getLocation(), game.getEnemyPortals());
	    Portal closestAllyPortalToMe=this.getClosestPortal(myCastle.getLocation(), game.getMyPortals());
	    if(closestEnemyPortalToMe!=null)
	    {
	    	if(closestEnemyPortalToMe.inRange(myCastle, 2000) && MyBot.mana<=game.tornadoCost)
	    		return true;
	    }
		return false;
	}
	
	public boolean saveMana()
	{
	    Castle enemyCastle=game.getEnemyCastle();
	    Castle myCastle = game.getMyCastle();
	    Portal p=this.getClosestPortal(enemyCastle.getLocation(),game.getMyPortals());
	    Elf e=this.getClosestElf(enemyCastle.getLocation(),game.getMyLivingElves());
	    int countEnemyLavaAttackingCastle= countDefendingIceTrolls(myCastle.getLocation(), game.getEnemyLavaGiants(), 1600);
	    if(MyBot.mana>=110)
	        return false;
	    if(e==null)
	    {
	        return false;
	    }
	    if(this.castleInDanger(myCastle) && MyBot.mana<game.tornadoCost+game.lavaGiantCost+10)
	    	return true;
	    /*if(closestEnemyPortalToMe!=null && closestAllyPortalToMe!=null)
    	{
    	    if(closestEnemyPortalToMe.inRange(myCastle, (int) (mapSize/2.5)) && closestEnemyPortalToMe.inRange(closestAllyPortalToMe, p.size*3))
	    	    return false;
    	}*/
	    if((this.portalInDanger() || this.anyElfInDanger()) && MyBot.mana<game.iceTrollCost && !this.hasAttackPortal(1)&&countEnemyLavaAttackingCastle>2)
	    	return true;
	    if(p!=null)
	    {
	       if(e.distance(enemyCastle)<p.distance(enemyCastle) && e.distance(p)>=p.size)
	        {
	    	   if(this.elfInDanger(e.getLocation()) && this.countDefendingIceTrolls(e.getLocation(), game.getMyIceTrolls(), 500)<2)
	   	    		return false;	
	            return true;
	        }
	        else
	        {
	            return false;
	        }
	    }
	    return false;
	}
}
