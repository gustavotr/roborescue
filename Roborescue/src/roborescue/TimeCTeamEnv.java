package roborescue;


import jason.RoborescueEnv;
import jason.asSyntax.Structure;

import java.rmi.RemoteException;

import robocode.rescue.RobotInfo;
import robocode.rescue.interfaces.RMIRobotInterface;


public class TimeCTeamEnv extends RoborescueEnv {
    
    private final int numRobos = 5;
    private RobotInfo[] robos;
    
    double radius = 200;
    double angle = 180;
    double distance = radius * angle * Math.PI / 180;
    boolean [] direction;
    
    //Para inicializacoes necessarias
    @Override
    public void setup() {
        robos = new RobotInfo[numRobos];
        direction = new boolean[numRobos];
    }

    @Override
    public boolean executeAction(String ag, Structure action) {

        try {
            mainLoop();
            Thread.sleep(20);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return true;

    }
    
    public void mainLoop() throws RemoteException {
        
        robos = getServerRef().getMyTeamInfo(myTeam);
        
        RobotInfo refem = robos[0];
        double xRefem = refem.getX();
        double yRefem = refem.getY();
        RMIRobotInterface[] teamRef = getTeamRef();

        for (int robo = 1; robo < numRobos; robo++) {
          
            double newMaxTurnRate = angle / (distance / robos[robo].getVelocity());
            teamRef[robo].setMaxTurnRate(newMaxTurnRate);
            
            if(teamRef[robo].getDistanceRemaining() == 0.0) {
                if(!direction[robo]) {
                    teamRef[robo].setTurnRight(angle);
                    teamRef[robo].setAhead(distance);
                } else {
                    teamRef[robo].setTurnRight(-angle);
                    teamRef[robo].setAhead(-distance);
                }
                direction[robo] = !direction[robo];
            }
            
            teamRef[robo].execute();

        }

    }
    
    @Override
    public void end() {
        try {
          super.getEnvironmentInfraTier().getRuntimeServices().stopMAS();
        } catch (Exception ex) {
          ex.printStackTrace();
          System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        TimeCTeamEnv team = new TimeCTeamEnv();
        team.init(new String[]{"TimeC", "localhost"});
        team.setup();
        while(true) {
          try {
            team.mainLoop();
            Thread.sleep(20);
          } catch (RemoteException ex) {
            ex.printStackTrace();
          } catch (InterruptedException ex) {
            ex.printStackTrace();
          }
        }
    }
    
    
}
