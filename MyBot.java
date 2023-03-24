package bots;
import elf_kingdom.*;
import java.util.*;

public class MyBot implements SkillzBot {
    /**
     * Makes the bot run a single turn.
     *
     * @param game - the current game state.
     */
     public Util util;
     public Tasks Tasks;
     public Execute execute;
     public TasksPriority TP;
     public Predict predict;
     public int something=0;
     public static Elf[] myElfs;
     public static int mana;
     public static int deathCounter=0;
     public static List<Location> newBuildings;
     public static Location[] newBuildingLoc;
     public static int[] turnsChasing;
     public static int[] rotateDirection;
    @Override
    public void doTurn(Game game) {
    	newBuildingLoc=new Location[game.getAllMyElves().length];
        mana=game.getMyself().mana;
        //MyBot.newBuildings.clear();
        if(game.turn==1)
        {
            myElfs=game.getMyLivingElves();
            MyBot.turnsChasing=new int[game.getAllMyElves().length];
            for(int i=0 ; i<MyBot.turnsChasing.length ; i++)
            {
                MyBot.turnsChasing[i]=0;
            }
            predict=new Predict(game);
            predict.fillFirst();
            MyBot.rotateDirection=new int[game.getAllMyElves().length];
            for(int i=0 ; i<MyBot.rotateDirection.length ; i++)
			{
            	MyBot.rotateDirection[i]=-999;
			}
        }
        if(game.turn==2)
        {
        	predict.fillSecond();
        }
        if(game.turn>2)
        {
        	predict.fillMain();
        }
        util=new Util(game,predict);
    	Tasks=new Tasks(game,predict,util);
    	TP=new TasksPriority(game,util,Tasks);
    	TP.handleElfs();
        TP.handlePortals();
        execute=new Execute(game,TP.getElfAssigns(),TP.getPortalAssigns());
        execute.doIt();
    }
}
    