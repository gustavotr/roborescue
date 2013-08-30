package roborescue;


import jason.RoborescueEnv;
import jason.asSyntax.Structure;

import java.rmi.RemoteException;

import robocode.rescue.RobotAction;
import robocode.rescue.RobotInfo;
import robocode.rescue.interfaces.RMIRobotInterface;

public class TimeBTeamEnv extends RoborescueEnv {

    private final int numRobos = 5;
    private RobotInfo[] robos;

    // Para inicializacoes necessarias
    @Override
    public void setup() {
        robos = new RobotInfo[numRobos];
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
            
            if (xRefem < robos[robo].getX() && yRefem > robos[robo].getY()) {
                teamRef[robo].turnRight(1.0);
            } else if (xRefem < robos[robo].getX() && yRefem < robos[robo].getY()) {
                teamRef[robo].turnRight(-1.0);
            }
            
            if (robos[robo].getX() - xRefem > 100) {
                double distance = 40.0;
                if (robos[robo].getHeading() < 180.0) {
                    distance *= -1;
                }
                teamRef[robo].setAhead(distance);
            } else {
                if (robos[robo].getHeading() > 180.0) {
                    teamRef[robo].turnLeft(180.0);
                } else {
                    teamRef[robo].setAhead(40.0);
                }
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
        TimeBTeamEnv team = new TimeBTeamEnv();
        team.init(new String[]{"TimeB", "localhost"});
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
