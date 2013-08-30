package roborescue;


import jason.RoborescueEnv;
import jason.asSyntax.Structure;

import java.awt.geom.Point2D;
import java.rmi.RemoteException;

import robocode.rescue.RobotInfo;
import robocode.rescue.interfaces.RMIRobotInterface;

public class TimeATeamEnv extends RoborescueEnv {

    private final int numRobos = 5;
    private RobotInfo[] robos;
    
    //Para inicializacoes necessarias
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
        	
        	Point2D from = new Point2D.Double(robos[robo].getX(), robos[robo].getY());
			double toX = 0;
			double toY = 0;	
            
			
			String str = robos[robo].getName();
			str = str.substring(15,16);
			int num = Integer.parseInt(str);					
			switch(num){
				case 1:
					if(teamRef[0].isFollowing() == 1){
						toX = xRefem;
						toY = yRefem;
					}
					else{						
						toX = 2500;
						toY = 1400;
					}												
					break;
				/*case 2:
					toX = 1700;
					toY = enemys[2].getY();
					break;
				case 3:
					toX = 1700;
					toY = enemys[3].getY();
					break;
				case 4:
					if(id == 1.0){ //Robo4 ao ataque	
						if(getReferenciaServidor().isRefemFollowing(myTeam)){
							refemStatus = 1;
							perc = Literal.parseLiteral("status(jogo, "+ataque()+", "+defesa()+", 1)"); 
							addPercept(perc);
						}
						toX = xRefem;
						toY = yRefem;						
					}
					if(id == 1.1){ //Robo4 bloqueado						
						toX = xRefem;
						toY = yRefem;
					}
					if(id == 2){						
						toX = 2500;
						toY = 100;
					}			
					break;*/
			}		
					
			Point2D to = new Point2D.Double(toX,toY);						
			goTo(from, to, robos[robo], teamRef);	 
			
			
			
			/*
            if (xRefem > robos[robo].getX() && yRefem > robos[robo].getY()) {
                teamRef[robo].setTurnRight(0);
            } else if (xRefem > robos[robo].getX() && yRefem < robos[robo].getY()) {
                teamRef[robo].setTurnRight(0);
            }
            
            if (xRefem - robos[robo].getX() > 130) {
                double distance = 100.0;
                if (robos[robo].getHeading() > 180.0) {
                    distance *= -1;
                }
                teamRef[robo].setAhead(distance);
            } else {
                if (robos[robo].getHeading() < 180.0) {
                    teamRef[robo].turnLeft(180.0);
                } else {
                    teamRef[robo].setAhead(100.0);
                }
            }*/
            
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
        TimeATeamEnv team = new TimeATeamEnv();
        team.init(new String[]{"TimeA", "localhost"});
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
    
    
    //funçoes para locomoção
    private double absoluteBearing(Point2D source, Point2D target) {
        return Math.toDegrees(Math.atan2(target.getX() - source.getX(), target.getY() - source.getY()));
   }

   private double normalRelativeAngle(double angle) {
       double relativeAngle = angle % 360;
       if (relativeAngle <= -180)
           return 180 + (relativeAngle % 180);
       else if (relativeAngle > 180)
           return -180 + (relativeAngle % 180);
       else
           return relativeAngle;
   }
	
   //retorna um ponto onde a coordenada x é o angulo e o y é a distancia a ser percorrida
	private Point2D goTo(Point2D from, Point2D to, RobotInfo robo, RMIRobotInterface[] teamRef) {
       double distance = from.distance(to);
       double angle = normalRelativeAngle(absoluteBearing(from, to) - robo.getHeading());
       if (Math.abs(angle) > 90.0) {
           distance *= -1.0;
           if (angle > 0.0) {
               angle -= 180.0;
           }
           else {
               angle += 180.0;
           }
       }	
       return new Point2D.Double(angle,distance);       
   }
    
}
