package roborescue;


import jason.RoborescueEnv;
import jason.asSyntax.Structure;

import java.awt.geom.Point2D;
import java.rmi.RemoteException;

import robocode.rescue.RobotInfo;
import robocode.rescue.interfaces.RMIRobotInterface;

public class TimeGTeamEnv extends RoborescueEnv {

    private final int numRobos = 5;
    private RobotInfo[] robos;
    private RobotInfo[] inimigos;
    double meucampo = 0;
    
    //Para inicializacoes necessarias
    @Override
    public void setup(){
        robos = new RobotInfo[numRobos];
        inimigos = new RobotInfo[numRobos];
        try {
			meucampo = getServerRef().getEnemyTeamInfo(myTeam)[0].getX();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
        inimigos = getServerRef().getEnemyTeamInfo(myTeam);
        
        RobotInfo refem = robos[0];
        double xRefem = refem.getX();
        double yRefem = refem.getY();
        RMIRobotInterface[] teamRef = getTeamRef();

        //loop passa verificando a situacao de cada robo
        for (int robo = 1; robo < numRobos; robo++) {        	
        	
        	Point2D from = new Point2D.Double(robos[robo].getX(), robos[robo].getY());
			double toX = 0;
			double toY = 0;	
            
			
			String str = robos[robo].getName();
			str = str.substring(8,9);
			int num = Integer.parseInt(str);
			//faz uma acao para cada robo (robo1, robo2, robo3, robo4)
			switch(num){
				case 1:
					if(teamRef[0].isFollowing() == 0){//caso nao esteja com o refem
						toX = xRefem;
						toY = yRefem;
					}
					else{//caso esteja com o refem						
						toX = meucampo;
						toY = from.getY();
					}												
					break;
				case 2:
					toX = 700;
					toY = inimigos[2].getY();
					break;
				case 3:
					toX = 700;
					toY = inimigos[3].getY();
					break;
				case 4:					
					toX = 700;
					toY = inimigos[4].getY();							
					break;
			}							
						          
			Point2D to = new Point2D.Double(toX,toY);			
			Point2D gt = goTo(from, to, robos[robo]);
			double angle = gt.getX();
			double distance = gt.getY();
						
			if(distance > 20){						//Robo se mexe somente se estiver numa distância maior que 20 
													//pixels do objetivo caso contrário fica parado    
				
				if (Math.abs(angle) > 90.0) {		   //Caso o robo tenha que virar mais de 90 graus 
													   //ele vai de ré para ser mais rápido
					teamRef[robo].setBack(distance);
					if (angle > 0.0) {
			               angle -= 180.0;
		            } else {
		            	angle += 180.0;
		           } //else
			    } else {
			    	teamRef[robo].setAhead(distance);
			    } //fim else
				
				teamRef[robo].turnRight(angle);			//passa para o robo o angulo que ele irá girar
			}
			
			teamRef[robo].execute();					//executa as ações passadas ao robo

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
        TimeGTeamEnv team = new TimeGTeamEnv();
        team.init(new String[]{"TimeG", "localhost"});
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
    
       
    private double absoluteBearing(Point2D source, Point2D target) {		//calcula o angulo ente dois pontos
        return Math.toDegrees(Math.atan2(target.getX() - source.getX(), target.getY() - source.getY()));
   }

   private double normalRelativeAngle(double angle) {			//retorna um angulo relativo sempre menor que o módulo de 180 graus
       double relativeAngle = angle % 360;
       if (relativeAngle <= -180)
           return 180 + (relativeAngle % 180);
       else if (relativeAngle > 180)
           return -180 + (relativeAngle % 180);
       else
           return relativeAngle;
   }
	
   	private Point2D goTo(Point2D from, Point2D to, RobotInfo robo) {			//retorna um ponto onde a coordenada x é o angulo e o y é 
   																				//a distancia a ser percorrida
       double distance = from.distance(to);
       double angle = normalRelativeAngle(absoluteBearing(from, to) - robo.getHeading());
       
       return new Point2D.Double(angle,distance);       
   }
    
}
