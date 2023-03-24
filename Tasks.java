package bots;
import elf_kingdom.*;
public class Tasks 
{
    private Game game;
	private Util util;
	private Predict predict;
	
	private static boolean noNeedForMore=false;
	private  int numberOfEnemiesCastle;
	private static int numberOfIceTrollsCastle;
	private boolean needToSave;
	
	private boolean gotTask=false;
	
	public Tasks(Game game,Predict p,Util u)
	{
		this.game=game;
		this.predict=p;
		util=u;
		this.needToSave=util.saveMana();
		Tasks.numberOfIceTrollsCastle=util.countDefendingIceTrolls(game.getMyCastle().getLocation(), game.getMyIceTrolls(),game.iceTrollMaxSpeed*5+game.castleSize+game.iceTrollAttackRange+game.lavaGiantAttackRange);
	}
	
	public PortalTask portalTask(Portal portal)
	{
		gotTask=false;
		PortalTask toRet=null;
		Castle myCastle=this.game.getMyCastle();
		Castle enemyCastle=this.game.getEnemyCastle();
		Elf closestEnemyElf=this.util.getClosestElf(portal.getLocation(), game.getEnemyLivingElves());
		Elf closestEnemyElfToMyCastle=util.getClosestElf(myCastle.getLocation(), game.getEnemyLivingElves());
		Elf defender=util.getClosestElf(enemyCastle.getLocation(), game.getEnemyLivingElves());
		Elf closestAllyElfToMyCity=util.getClosestElf(myCastle.getLocation(), game.getMyLivingElves());
		Elf closestAllyElf=util.getClosestElf(portal.getLocation(), game.getMyLivingElves());
		Portal closestToEnemyCity=util.getClosestPortal(enemyCastle.getLocation(), game.getMyPortals());
		Portal closestEnemyPortal=util.getClosestPortal(portal.getLocation(), game.getEnemyPortals());
		Portal closestEnemyPortalToMyCity=this.util.getClosestPortal(myCastle.getLocation(), game.getEnemyPortals());
		Portal clsoestAllyPortalToMyCity =util.getClosestPortal(myCastle.getLocation(), game.getMyPortals());
		boolean a=false;
		boolean b=false;
		Tornado closestEnemyTor=util.getClosestTornado(portal.getLocation(), game.getEnemyTornadoes());
		double mapSize=Math.sqrt(game.cols*game.cols + game.rows*game.rows);
		int countAllyIceDefendingElf=-999;
		int countEnemyIceAttackingElf=-999;
		int numberOfTornadoes=util.countDefendingIceTrolls(portal.getLocation(), game.getMyTornadoes(), portal.size+game.tornadoMaxSpeed*9+game.tornadoAttackRange);
		int countEnemyLavaAttackingCastle=0;
		boolean saveForTor=util.saveForTor();
		//if elf is threatening
		
		if(closestEnemyElf!=null) 
		{
			a=closestEnemyElf.inRange(portal, portal.size+1200);
		}
		
		if(closestEnemyTor!=null)
		{
			b=closestEnemyTor.inRange(portal, portal.size+game.tornadoMaxSpeed*9+game.tornadoAttackRange);
		}
		
		if(closestEnemyElfToMyCastle!=null)
		{
			if(closestEnemyElfToMyCastle.distance(myCastle)<=clsoestAllyPortalToMyCity.distance(myCastle) && closestEnemyElfToMyCastle.canBuildPortal())
			{
			    if(closestAllyElfToMyCity!=null)
			    {
			        if(portal.equals(clsoestAllyPortalToMyCity) && closestAllyElfToMyCity.distance(myCastle)-300>portal.distance(myCastle))
    				{
    					if(MyBot.mana>=game.tornadoCost && portal.canSummonTornado() && numberOfTornadoes<2 && util.canPortalSpawn(portal, game.tornadoSummoningDuration))
    					{
    					    game.debug("$$$$$$$$$$");
    					    MyBot.mana-=game.tornadoCost;
    						return this.summonTornado(portal);
    					}
    				}  
			    }
			}
		}
		
		
		if(closestEnemyPortal!=null)
		{
			if(closestEnemyPortal.inRange(portal, (game.tornadoMaxHealth/2)*game.tornadoMaxSpeed+game.portalSize))
			{
				if(MyBot.mana>=game.tornadoCost && portal.canSummonTornado() && numberOfTornadoes<2 && util.canPortalSpawn(portal, game.tornadoSummoningDuration))
				{
				    game.debug("@@@@@@@@@");
				    MyBot.mana-=game.tornadoCost;
					return this.summonTornado(portal);
				}
			}
		}
		
		if(b && MyBot.mana>=game.iceTrollCost && portal.canSummonIceTroll() && !saveForTor)
		{
			if(closestEnemyTor!=null)
			{
				toRet= this.summonIce(portal);
			}
		}
		
		if(closestEnemyPortal!=null)
		{
			if(closestEnemyPortal.inRange(portal,(game.tornadoMaxHealth/2)*game.tornadoMaxSpeed+game.portalSize))
			{
				if(MyBot.mana>=game.tornadoCost && portal.canSummonTornado() && numberOfTornadoes<2 && util.canPortalSpawn(portal, game.tornadoSummoningDuration))
				{
				    game.debug("!!!!!!!!!");
			        MyBot.mana-=game.tornadoCost;
					return this.summonTornado(portal);
				}
			}
		}
		
		if(closestEnemyPortalToMyCity!=null)
		{
			if(closestEnemyPortalToMyCity.inRange(myCastle, (int) (mapSize/3.5)) && portal.inRange(closestEnemyPortalToMyCity, 2000))
			{
				if(MyBot.mana>=game.tornadoCost && portal.canSummonTornado())
				{
					game.debug("#######");
			        MyBot.mana-=game.tornadoCost;
					return this.summonTornado(portal);
				}
			}
		}
		
		if(portal.distance(enemyCastle)<2000 && portal.equals(closestToEnemyCity))
		{
		    if(MyBot.mana>=game.lavaGiantCost && portal.canSummonLavaGiant())
		    {
		        game.debug("55555555555");
		        return  this.summonLava(portal);
		    }
		}
		
		
		//summon lava to waist enemy mana at the beginning of the game
		if(((enemyCastle.currentHealth<=5 && MyBot.mana>=40) || MyBot.mana>=400) && !needToSave && !saveForTor)
		{ 
			if(portal.equals(closestToEnemyCity))
			{
			    game.debug("11111111111111");
	            toRet= this.summonLava(portal);
			}
		}
		
		
		//summon ice to defend elf
		if(closestAllyElf!=null && !saveForTor)
		{
			countAllyIceDefendingElf=this.util.countDefendingIceTrolls(closestAllyElf.getLocation(), game.getMyIceTrolls(),500);
			countEnemyIceAttackingElf=this.util.countDefendingIceTrolls(closestAllyElf.getLocation(), game.getEnemyIceTrolls(),500);
			countEnemyIceAttackingElf+=this.util.countDefendingIceTrolls(closestAllyElf.getLocation(), game.getEnemyLivingElves(), 500);
			Portal closestPortalToElf=this.util.getClosestPortal(closestAllyElf.getLocation(), game.getMyPortals());
		    if(closestAllyElf.distance(portal)<1000 && portal.equals(closestPortalToElf))
		    {
		        if(util.elfInDanger(closestAllyElf.getLocation()) && !gotTask)
		        {
		        	if(countAllyIceDefendingElf<=countEnemyIceAttackingElf+1 && countAllyIceDefendingElf<2)
		        	{
		        		if(portal.equals(closestPortalToElf));
		        		{
		        			game.debug("BROWN 22222");
					        toRet= this.summonIce(portal);
		        		}
		        	}
		        	else
		        	{
		        	    if(!saveForTor)
		        	    {
		        	        game.debug("22222 BROWN");
				            toRet= this.summonLava(portal);
		        	    }
		        	}
		        }
		    }
		}
		/*if(a && game.turn==57)
		{
		    if(portal.id==1)
		    {
		        game.debug("BLUESPECIAL");
        	    toRet= this.summonIce(portal);    
		    }
		    else
		    {
		        return PortalTask.donothing;
		    }
		}*/
		
		//defend portal
		if(closestEnemyElf!=null)
		{
			countAllyIceDefendingElf=this.util.countDefendingIceTrolls(portal.getLocation(), game.getMyIceTrolls(),1700);
			countEnemyIceAttackingElf=this.util.countDefendingIceTrolls(portal.getLocation(), game.getEnemyIceTrolls(),1700);
			countEnemyIceAttackingElf+=this.util.countDefendingIceTrolls(portal.getLocation(), game.getEnemyLivingElves(), 1700);
		    Portal toUse=this.util.getClosestPortal(closestEnemyElf.getLocation(), game.getMyPortals());
		    if(a && !saveForTor)
			{
			    if(countAllyIceDefendingElf<countEnemyIceAttackingElf+1 && countAllyIceDefendingElf<2)
			    {
			        if(portal.equals(toUse) && !gotTask)
                    {
            		    game.debug("BLUE");
    		        	toRet= this.summonIce(portal);
            		} 
			    }
			    else
			    {
			        if(portal.equals(closestToEnemyCity) && !needToSave)
    			    {
    			        game.debug("22222 2222 BLUE");
    			        toRet=this.summonLava(portal);
    			    }
			    }
			}
		}
		
		//summon lava if our portal is closer than enemies
		if(closestEnemyPortalToMyCity!=null && closestToEnemyCity!=null && !saveForTor)
		{
			if(closestToEnemyCity.distance(enemyCastle)<closestEnemyPortalToMyCity.distance(myCastle) && !a && !needToSave && portal.inRange(enemyCastle,(int) (mapSize/2.5)))
			{
			    if(portal.equals(closestToEnemyCity))
			    {
			        game.debug("66666666666");
			        toRet= this.summonLava(portal);
			    }
			}
		}
		
		//defend castle
		if(this.util.castleInDanger(myCastle))
		{
			countAllyIceDefendingElf=this.util.countDefendingIceTrolls(myCastle.getLocation(), game.getMyIceTrolls(),1600);
			countEnemyIceAttackingElf=this.util.countDefendingIceTrolls(myCastle.getLocation(), game.getEnemyIceTrolls(),1600);
			countEnemyIceAttackingElf+=this.util.countDefendingIceTrolls(myCastle.getLocation(), game.getEnemyLivingElves(), 1600);
		    countEnemyLavaAttackingCastle=this.util.countDefendingIceTrolls(myCastle.getLocation(), game.getEnemyLavaGiants(), 1600);
		    Portal toUse=this.util.getClosestPortal(myCastle.getLocation(), game.getMyPortals());
		    if(a && countAllyIceDefendingElf<countEnemyIceAttackingElf && countAllyIceDefendingElf<2 && !saveForTor)
			{
		    	if(portal.equals(toUse) && !gotTask)
        		{
        			game.debug("RED CLOSEST");
		        	toRet= this.summonIce(portal);
        		}
			}
			if(countEnemyLavaAttackingCastle>2 && !saveForTor)
			{	
			    if(countAllyIceDefendingElf<2 && portal.equals(clsoestAllyPortalToMyCity) && !needToSave)
			    {
    			    game.debug("lava CLOSEST");
    		       	toRet= this.summonLava(portal);
			    }
			}
		}
		if(Tasks.numberOfIceTrollsCastle>=this.numberOfEnemiesCastle)
				Tasks.noNeedForMore=true;
			else
				Tasks.noNeedForMore=false;
		if(toRet!=null)
			return toRet;
		return PortalTask.donothing;
	}
	
	public PortalTask summonTornado(Portal p)
	{
		MyBot.mana-=game.tornadoCost;
		return PortalTask.summonTornado;
	}
	public PortalTask summonLava(Portal portal)
	{
		if(MyBot.mana>=game.lavaGiantCost && portal.canSummonLavaGiant() && util.canPortalSpawn(portal, game.lavaGiantSummoningDuration))
		{
			MyBot.mana-=game.lavaGiantCost;
			return PortalTask.summonlava;
		}
		return PortalTask.donothing; //IF DO NOTHING
	}
	
	public PortalTask summonIce(Portal portal)
	{
		if(MyBot.mana>=game.iceTrollCost && portal.canSummonIceTroll() && util.canPortalSpawn(portal, game.iceTrollSummoningDuration))
		{
			MyBot.mana-=game.iceTrollCost;
			gotTask=true;
			return PortalTask.summonice;
		}
		return PortalTask.donothing;
	}
	public ElfTask SnD(Elf elf)
	{
	    game.debug("RUSH B");
		Portal closestEnemyPortalToElf=this.util.getClosestPortal(elf.getLocation(), game.getEnemyPortals());
		Elf closestElf=this.util.getClosestElf(elf.getLocation(), game.getEnemyLivingElves());
		Castle myCastle=game.getMyCastle();
		Castle enemyCastle=game.getEnemyCastle();
		Portal closestEnemyPortalToMyCity=this.util.getClosestPortal(myCastle.getLocation(), game.getEnemyPortals());
		ManaFountain closestEnemyFountain=util.getClosestMana(elf.getLocation(), game.getEnemyManaFountains());
		double mapSize=Math.sqrt(game.cols*game.cols + game.rows*game.rows);
		if(util.attackPriority(elf)!=ElfTask.donothing && (Util.attackTarget[elf.id] instanceof Portal || Util.attackTarget[elf.id] instanceof Tornado))
		{
		    return ElfTask.attack;
		}
		if(closestEnemyFountain!=null)
		{
			return this.util.tryAttackOrMove(elf, closestEnemyFountain);
		}
		if(game.getEnemyPortals().length!=0)
		{
			if(closestEnemyPortalToMyCity.distance(myCastle)<mapSize/2)
			{
				return this.util.tryAttackOrMove(elf, closestEnemyPortalToMyCity);
			}
			else
			{
				return this.util.tryAttackOrMove(elf, closestEnemyPortalToElf);
			}
		}
		else
		{
			if(closestElf!=null)
			{
				return this.util.tryAttackOrMove(elf, closestElf);
			}
			else
			{
				return this.util.tryAttackOrMove(elf, enemyCastle);
			}
		}
	}
	
	public ElfTask elfTask(Elf elf)
	{
	    double mapSize=Math.sqrt(game.cols*game.cols + game.rows*game.rows);
		Portal closestEnemyPortalToElf=this.util.getClosestPortal(elf.getLocation(), game.getEnemyPortals());
		Elf closestElf=this.util.getClosestElf(elf.getLocation(), game.getEnemyLivingElves());
		IceTroll closestIceTroll=this.util.getClosestIceTroll(elf.getLocation(), game.getEnemyIceTrolls());
		Castle myCastle=game.getMyCastle();
		Castle enemyCastle=game.getEnemyCastle();
		Portal closestAllyPortalToEnemyCity=this.util.getClosestPortal(enemyCastle.getLocation(), game.getMyPortals());
		Portal closestAllyPortalToMyCity= this.util.getClosestPortal(myCastle.getLocation(),game.getMyPortals());
		Portal closestEnemyPortalToMyCity=this.util.getClosestPortal(myCastle.getLocation(), game.getEnemyPortals());
		Portal closestEnemyPortal=util.getClosestPortal(elf.getLocation(),game.getEnemyPortals());
		Portal closestAllyPortal=this.util.getClosestPortal(elf.getLocation(), game.getMyPortals());
		Portal closestEnemyPortalToEnemyCity=util.getClosestPortal(enemyCastle.getLocation(), game.getEnemyPortals());
		boolean atRisk=this.util.elfInDanger(elf.getLocation());
		boolean canBuildPortal=this.util.canBuild(elf.getLocation(),game.portalSize);
		boolean canBuildMana=this.util.canBuild(elf.getLocation(),game.manaFountainSize);
	    Elf cloestElfToMyCity=util.getClosestElf(myCastle.getLocation(), game.getEnemyLivingElves());
	    int defendingIceTrolls=this.util.countDefendingIceTrolls(elf.getLocation(), game.getMyIceTrolls(),300);
	    boolean hasAttackPortal=false;
	    ManaFountain closestEnemyMana=util.getClosestMana(elf.getLocation(), game.getEnemyManaFountains());
	    ManaFountain closestEnemyManaToEnemyCity=util.getClosestMana(enemyCastle.getLocation(), game.getEnemyManaFountains());
	    int mymana=game.getMyself().manaPerTurn;
	    boolean isInvis=util.isInvis(elf);
	    boolean isFast=util.isFast(elf);
	    GameObject target=enemyCastle;
	    boolean saveForTor=util.saveForTor();
	    Tornado closestEnemyTor=util.getClosestTornado(elf.getLocation(), game.getEnemyTornadoes());
	    
	    if(util.countElvesInRange(elf.getLocation(),game.getEnemyLivingElves(),elf.attackRange)>1)
	    {
	        if(util.getClosestElfLowLife(elf,game.getEnemyLivingElves())!=null)
	        {
	            closestElf=util.getClosestElfLowLife(elf,game.getEnemyLivingElves());
	        }
	    }
	    
	    if(closestAllyPortalToEnemyCity!=null)
	    {
	    	hasAttackPortal=closestAllyPortalToEnemyCity.inRange(enemyCastle, 2000);
	    }
	    
	    if(closestEnemyManaToEnemyCity==null && closestEnemyPortalToEnemyCity==null)
	    {
	    	if(hasAttackPortal)
	    	{
	    		if(closestElf!=null)
	    		{
	    			target=closestElf;
	    		}
	    		else
	    		{
	    			target=enemyCastle;
	    		}
	    	}
	    	else
	    	{
	    		target=enemyCastle;
	    	}
	    }
	    else
	    {
	    	if(closestEnemyPortalToEnemyCity==null)
	    	{
	    		target=closestEnemyManaToEnemyCity;
	    	}
	    	else
	    	{
	    		if(closestEnemyManaToEnemyCity==null)
	    		{
	    			target=closestEnemyPortalToEnemyCity;
	    		}
	    		else
	    		{
	    			if(closestEnemyPortalToEnemyCity.distance(enemyCastle)+500<closestEnemyManaToEnemyCity.distance(enemyCastle))
	    				target=closestEnemyPortalToEnemyCity;
	    			else
	    				target=closestEnemyManaToEnemyCity;
	    				
    				if(closestEnemyPortal!=null)
    				{
    				    if(elf.inAttackRange(closestEnemyPortal) && !isFast)
    				        target=closestEnemyPortal;
    				}
    				if(closestEnemyMana!=null)
    				{
    				    if(elf.inAttackRange(closestEnemyMana))
				            target=closestEnemyMana;
    				}
	    		}
	    	}
	    }
	    
	    if(closestElf!=null && !isFast && !(elf.inAttackRange(target) && target.currentHealth==1))
	    {
	    	if(closestElf.distance(enemyCastle)<=elf.distance(enemyCastle) && (!elf.inRange(target, elf.attackRange)) && util.onVector(elf.getLocation(), target.getLocation(), closestElf.getLocation(), elf.attackRange)
	    	|| closestElf.inRange(myCastle,(int) mapSize/3) && elf.inRange(closestElf,(int) mapSize/4))
	    	{
	    		target=closestElf;
	    	}
	    }
	    boolean enemyHasInvis=false;
	    int enemyIndex=0;
	    for(Elf enemyElf1 : game.getEnemyLivingElves())
	    {
	        if(util.isInvis(enemyElf1))
	        {
	            enemyHasInvis=true;
	            enemyIndex=enemyElf1.id;
	        }
	    }
	    if(enemyHasInvis && game.getEnemyLivingElves().length>enemyIndex)
	    {
	        Elf enemyElfInvis=game.getEnemyLivingElves()[enemyIndex];
	        if(predict.getNext(1,enemyIndex).distance(myCastle)<enemyElfInvis.distance(myCastle) &&
	            predict.getNext(1,enemyIndex).inRange(myCastle,2500))
	        {
	            Util.destination[elf.id]=predict.getNext(1,enemyIndex);
	            return ElfTask.walk;
	        }
	    }
	    Util.target[elf.id]=target;
	    /* * SPECIALS * */
	    /*if(myCastle.getLocation().equals(new Location(1600,5200)) && game.getAllEnemyElves().length==3)
	    {
	        if(game.turn>48 && game.turn<52 && elf.id==1)
	        {
	            if(elf.inAttackRange(closestElf))
	            {
	                return util.tryAttackOrMove(elf,closestElf);
	            }
	            else
	            {
	                Util.destination[elf.id]=new Location(2547,2715);
	            return ElfTask.walk;
	            }
	            
	        }
	    }*/
	    
	    if(game.lavaGiantMaxSpeed==0)
	    {
	        game.debug(elf.currentHealth);
	        game.debug(MyBot.mana+"     "+game.getMyself().mana);
	        if(MyBot.mana>=60 && closestIceTroll.inRange(elf,closestIceTroll.attackRange+1))
            {   
                return ElfTask.castInv;
            }
            else
            {
            	Util.destination[elf.id]=enemyCastle.getLocation();
            	return ElfTask.walk;
            }
	    }
	    
	    if(game.getEnemyLivingElves().length>10)
	    {
	        if(closestElf.inRange(elf,(int)elf.maxSpeed*2+closestElf.attackRange) && !isInvis)
	        {
	            MyBot.mana-=game.invisibilityCost;
	            return ElfTask.castInv;
	        }
	    }
	     
	    if(elf.maxSpeed<=50)
	    {
	          if(MyBot.mana>=60 && !atRisk)
	          {
	            return ElfTask.castSpeed;
	          }
	    }
	    
	    if(game.getEnemyIceTrolls().length>40)
	    {
	        if(!elf.inAttackRange(enemyCastle))
	        {
	            if(elf.canCastInvisibility() && MyBot.mana>=game.invisibilityCost && !isInvis)
    	        {
    	            MyBot.mana-=game.invisibilityCost;
    	            return ElfTask.castInv;
    	        }
    	        else
    	        {
    	            Util.destination[elf.id]=enemyCastle.getLocation();
    	            return ElfTask.walk;
    	        }
	        }
	        else
	        {
	            Util.attackTarget[elf.id]=enemyCastle;
	            return ElfTask.attack;
	        }
	    }
	    
	    if(isFast)
	    {
	    	if(elf.inRange(enemyCastle, (int) (mapSize/3)) && !atRisk)
	    	{
	    		if(elf.canBuildPortal() && MyBot.mana>=game.portalCost && !saveForTor && canBuildPortal)
	    	    {
	                game.debug(elf+"    BUILT PORTAL FAST");
	    	    	MyBot.newBuildingLoc[elf.id]=elf.getLocation();
	    	        MyBot.mana-=game.portalCost;
	    	        return ElfTask.buildPortal;
	    	    }
	    		else
	    		{
	    			if(elf.inAttackRange(target))
	    			{
	    			    game.debug("1");
	    			    Util.attackTarget[elf.id]=target;
	    			    return ElfTask.attack;
	    			}
	    			else
	    			{
	    			    game.debug("2");
	    			    Util.destination[elf.id]=target.getLocation();
	    			    return ElfTask.walk;
	    			}
	    		}
	    	}
	    	else
	    	{
	    		if(elf.inAttackRange(target))
    			{
    			    game.debug("3");
    			    Util.attackTarget[elf.id]=target;
    			    return ElfTask.attack;
    			}
    			else
    			{
    			    game.debug("4");
    			    Util.destination[elf.id]=target.getLocation();
    			    return ElfTask.walk;
    			}
	    	}
	    }
	    
	    if(isInvis)
	    {
	        Util.destination[elf.id]=enemyCastle.getLocation();
	        return ElfTask.walk;
	    }
	    
		if(elf.isBuilding)
			return ElfTask.donothing;
			
		if((mymana==0 || mymana<=10) && elf.canBuildManaFountain() && MyBot.mana>=game.manaFountainCost && canBuildMana)
		{
		    if(closestElf!=null)
		    {
		        game.debug(closestElf+"    "+closestElf.distance(elf)+"     "+(elf.maxSpeed*15)+closestElf.attackRange+game.manaFountainSize);
		        if(!closestElf.inRange(elf,(elf.maxSpeed*12)+closestElf.attackRange+game.manaFountainSize))
		        {
		            MyBot.mana-=game.manaFountainCost;
        			MyBot.newBuildingLoc[elf.id]=elf.getLocation();
        			return ElfTask.buildMana;
		        }
		    }
		    else
		    {
		        MyBot.mana-=game.manaFountainCost;
    			MyBot.newBuildingLoc[elf.id]=elf.getLocation();
    			return ElfTask.buildMana;
		    }
		}
		if(game.turn<10)
	    {
	        if(elf.canBuildPortal() && MyBot.mana>=131 && canBuildPortal)
	        {
	            if(closestAllyPortalToMyCity!=null)
	            {
	                if(closestAllyPortalToMyCity.distance(myCastle)<elf.distance(myCastle))
    	            {
    	                MyBot.newBuildingLoc[elf.id]=elf.getLocation();
        	            MyBot.mana-=game.portalCost;
        	            return ElfTask.buildPortal;
    	            } 
	            }
	        }
	    }
	    
	    /*boolean testF=false;
	    for(Elf e : game.getMyLivingElves())
	    {
	        if(util.isFast(e))
	            testF=true;
	    }
		 
		if(!testF && game.getEnemyManaFountains().length!=0 && elf.inRange(enemyCastle,(int) (mapSize/1.5)))
		{
		    if(!isFast && MyBot.mana>=game.speedUpCost && elf.canCastSpeedUp())
		    {
		        MyBot.mana-=game.speedUpCost;
		        return ElfTask.castSpeed;
		    }
		}*/
		 
		/*if(game.turn>3 && elf.canBuildPortal() && canBuildPortal && MyBot.mana>=game.portalCost)
		{
		    if(predict.willColide(elf,6))
		    {
		        game.debug(elf+"    BUILT PORTAL222222222");
    	    	MyBot.newBuildingLoc[elf.id]=elf.getLocation();
    	        MyBot.mana-=game.portalCost;
    	        return ElfTask.buildPortal;   
		    }
		}*/
		boolean test12=true;
		if(closestEnemyTor!=null)
		{
			if(closestEnemyTor.inRange(elf, (closestEnemyTor.currentHealth-closestEnemyTor.suffocationPerTurn)*closestEnemyTor.maxSpeed))
			{
				test12=false;
			}
		}
		 
		if(test12 && game.getMyPortals().length==0 && elf.canBuildPortal() && MyBot.mana>=game.portalCost && !saveForTor && canBuildPortal && !atRisk && util.canPlacePortal(elf) && util.shouldPlacePortal(elf) && !elf.inRange(myCastle,(int) (mapSize/4)))
	    {
            game.debug(elf+"    BUILT PORTAL11111");
	    	MyBot.newBuildingLoc[elf.id]=elf.getLocation();
	        MyBot.mana-=game.portalCost;
	        return ElfTask.buildPortal;
	    }
	    
	    if(test12 && elf.canBuildPortal() && MyBot.mana>=game.portalCost && !saveForTor && canBuildPortal && elf.distance(enemyCastle)<2500 && util.canPlacePortal(elf))
	    {
	        game.debug(elf+"    BUILT PORTAL");
	    	MyBot.newBuildingLoc[elf.id]=elf.getLocation();
	        MyBot.mana-=game.portalCost;
	        return ElfTask.buildPortal;
	    }
	    
	    /*if(target instanceof Elf)
	    {
	    	Elf current=(Elf)target;
	    	if(predict.getNext(1, current.id).distance(myCastle)<current.distance(myCastle) && 
	    			predict.getNext(1, current.id).distance(myCastle)<elf.distance(myCastle))
	    	{
    			if(elf.canCastSpeedUp() && MyBot.mana>=game.speedUpCost && !isFast)
    			{
    			    game.debug("SHREK");
    				MyBot.mana-=game.speedUpCost;
    				return ElfTask.castSpeed;
	    		}
	    	}
	    }*/
	    
	    game.debug(elf+"    "+target);
		if(atRisk)
		{
		    game.debug(elf+"    AT RISK");
		    if(this.util.attackPriority(elf)!=ElfTask.donothing)
		    {
		    	return ElfTask.attack;
		    }
		    if(test12 && util.attackNotPlace(elf) && elf.canBuildPortal() && MyBot.mana>=60 && canBuildPortal && elf.currentHealth>4 && util.liklyToLive(elf, game.portalBuildingDuration+1)/*&& !elf.inRange(target,elf.attackRange+1000)*/)
	        {
	            /*if(closestAllyPortal!=null)
	            {
	                if(!elf.inRange(closestAllyPortal,1200))
    	            {*/
    	                game.debug(elf+"    BUILT PORTAL");
        	        	MyBot.newBuildingLoc[elf.id]=elf.getLocation();
        	            MyBot.mana-=60;
        	            return ElfTask.buildPortal;  
    	          /*  }
	            }*/
	        }
			else
			{
			    game.debug(elf +"  CANT BUILD");
				if(closestIceTroll!=null && closestElf!=null)
				{
				    game.debug("CANT BE");
					if(closestIceTroll.inRange(elf, closestIceTroll.attackRange) && closestElf.inAttackRange(elf))
					{
						if(this.util.isOkToAttackV2(elf))
						{
						    if(elf.inAttackRange(target) && elf.inAttackRange(closestElf))
						    {
						        return util.tryAttackOrMove(elf, target);
						    }
						    else
						    {
						        if(elf.inAttackRange(closestElf))
						        {
						            return util.tryAttackOrMove(elf, closestElf);
						        }
						        else
						        {
						            return util.tryAttackOrMove(elf, target);
						        }
						    }
						}
						else
						{
							return this.util.runOrRotate(elf, closestIceTroll);
						}
					}
					else 
					{
						if(!closestIceTroll.inRange(elf, closestIceTroll.attackRange+200) && closestElf.inRange(elf,elf.attackRange+elf.maxSpeed*2))
						{
						    if(elf.currentHealth>=closestElf.currentHealth)
							{
								game.debug("FIRST CASE");
							    return this.util.tryAttackOrMove(elf, closestElf);
							}
						    else
						    {
						    	Portal closestAllyPortalToClosestEnemyElf=util.getClosestPortal(closestElf.getLocation(), game.getMyPortals());
						    	if(closestAllyPortalToClosestEnemyElf!=null)
						    	{
						    		if(closestElf.inAttackRange(closestAllyPortalToClosestEnemyElf))
						    		{
						    			game.debug("SEVENTH CASE");
									    return this.util.tryAttackOrMove(elf, closestElf);
						    		}
						    	}
						    	else
						    	{
						    		game.debug("SIX CASE ");
							        return util.runOrRotate(elf,closestElf);
						    	}
						    }
						}
						else
						{
							if(closestIceTroll.inRange(elf, closestIceTroll.attackRange+200))
							{
							    if(this.util.distracted(closestIceTroll, elf))
								{
									game.debug("FIFTH CASE");
									if(this.util.tryAttackOrMove(elf, target)!=ElfTask.donothing)
									    return this.util.tryAttackOrMove(elf, target);
							        else
							        {
							            Util.destination[elf.id]=target.getLocation().towards(elf,elf.attackRange);
							        }
								}
								else
								{
									game.debug("SECOND CASE");
									return this.util.runOrRotate(elf, closestIceTroll);
								}
							}
							else    
							{
								if(elf.inAttackRange(closestElf))
								{
									if(this.util.attackOrNot(closestElf, elf))
									{
									    game.debug("THIRD CASE");
										Util.attackTarget[elf.id]=closestElf;
										return ElfTask.attack;
									}
									else
									{
									    game.debug("FOURTH CASE");
										return this.util.runAway(elf, closestElf);
									}
								}
								else
								{
								    game.debug("HEREEEEEEEEEEEEEEE");
									if(!closestIceTroll.inRange(closestElf.getLocation().towards(elf, elf.attackRange), closestIceTroll.attackRange))
								    {
								    	Util.destination[elf.id]=target.getLocation().towards(elf, elf.attackRange);
										return ElfTask.walk;
								    }
								    else
								    {
								    	return this.util.runOrRotate(elf, closestElf);
								    }
								}
							}
						}
					}
				}
				else
				{
					if(closestIceTroll==null)
					{
					   /* if(this.util.isOkToAttackV2(elf))
					    {*/
					        if(elf.inAttackRange(target))
					        {
					            return util.tryAttackOrMove(elf, target);
					        }
					        if(closestElf.inAttackRange(elf))
    						{
    							return this.util.tryAttackOrMove(elf, closestElf);
    						}
    						else
    						{
    							if(util.attackNotPlace(elf) && elf.canBuildPortal() && MyBot.mana>=game.portalCost && canBuildPortal)
    					        {
    					        	MyBot.newBuildingLoc[elf.id]=elf.getLocation();
    					            MyBot.mana-=game.portalCost;
    					            return ElfTask.buildPortal;
    					        }
    							else
    							{
    								Util.destination[elf.id]=target.getLocation();
    								return ElfTask.walk;
    							}
    						}
					    /*}
					    else
					    {
					        return util.runOrRotate(elf,closestElf);
					    }*/
					}
					else
					{
					    game.debug("SHOULD BE HERE");
						if(this.util.distracted(closestIceTroll, elf))
						{
						    game.debug("DISTRACTED");
						    ElfTask distractedTemp=this.util.tryAttackOrMove(elf, closestIceTroll);
						    if(distractedTemp!=ElfTask.donothing)
					    	    return distractedTemp;
						    else
						    {
						        if(elf.inAttackRange(closestIceTroll))
						        {
						            Util.attackTarget[elf.id]=closestIceTroll;
						            return ElfTask.attack; 
						        }
						        else
						        {
						            Util.destination[elf.id]=elf.getLocation().towards(closestIceTroll, -elf.attackRange);
						    	    return ElfTask.walk;
						        }
						    }
						}
						else
						{
							return this.util.runOrRotate(elf, closestIceTroll);
						}
					}
				}
			}
		}
		
		
		/*if(game.getEnemyLivingElves().length==0)
		{
			if(closestEnemyPortalToElf!=null)
				return this.util.tryAttackOrMove(elf, target);
		}*/
		/*if(closestEnemyPortalToMyCity!=null )
		{
			if(closestEnemyPortalToMyCity.distance(myCastle)<mapSize/2)
			{
				return this.util.tryAttackOrMove(elf, closestEnemyPortalToMyCity);
			}
		}
		if(elf.canBuildPortal() && MyBot.mana>=60 && canBuild && elf.distance(enemyCastle)<mapSize/2)
        {
            game.debug(elf+"    BUILT PORTAL");
        	MyBot.newBuildingLoc[elf.id]=elf.getLocation();
            MyBot.mana-=60;
            return ElfTask.buildPortal;
        }*/
        boolean shouldCastSpeed=true;
        if(closestEnemyPortalToMyCity!=null && closestAllyPortalToMyCity!=null)
        {
            if(closestEnemyPortalToMyCity.inRange(myCastle,2000) && closestAllyPortalToMyCity.inRange(closestEnemyPortalToMyCity,2000))
            {
                shouldCastSpeed=false;
            }  
        }
        
        GameObject threat=util.getThreat(elf);
        if(threat!=null)
        {
        	if(!util.onVector(elf.getLocation(), target.getLocation(), threat.getLocation(), game.iceTrollAttackRange))
        	{
        		shouldCastSpeed=false;
        	}
        }
        
		int tempSize=0;
        if(target instanceof Portal)
        	tempSize=game.portalSize;
        if(target instanceof ManaFountain)
        	tempSize=game.manaFountainSize;
		/*if(!elf.inAttackRange(target) && shouldCastSpeed && elf.inRange(enemyCastle, (int) (mapSize/2.4)) && !elf.inRange(enemyCastle, (int) (mapSize/5)) && game.getEnemyLivingElves().length!=0)
		{
			if(elf.canCastSpeedUp() && MyBot.mana>=game.speedUpCost && !isFast && elf.inRange(target, tempSize+elf.attackRange+game.elfMaxSpeed*game.speedUpMultiplier*game.speedUpExpirationTurns))
			{
			    game.debug("CARS");
				MyBot.mana-=game.speedUpCost;
				return ElfTask.castSpeed;
			}
		}*/
		
		return this.SnD(elf);
	}
}