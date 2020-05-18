import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
* class qui gère le temp
*/
public class Horloge extends JLabel implements Runnable {

    Thread thr;
    boolean demarrer=false;
    private int temps;

    /**
    * Constructeur par defaults
    */
    public Horloge () {
    	    setFont(Parametres.fontTemps);
            setText("Temps");
    }

    /**
    * Fonction d en cours du temps
    */
    public void run()
    {  try
	 {
	   Thread thisThread = Thread.currentThread();
	   while(thr == thisThread){
	   	temps++;

                   setText("Temps "+(temps/3600)+" : "+((temps/60)%3600)+" : "+(temps%60)+" ");
	    	Thread.sleep(1000);
	    }
	}
       catch (InterruptedException e){}
    }

    /**
    * Acceseur du temps
    * @return le temps ecoulés
    */
    public int getTime(){
    return temps;
    }
    
    /**
    * Initialise la police avec son parametre
    */
    public void iniPolice(){
    setFont(Parametres.fontTemps);
    }
    /**
    * Fonction qui demarre le temps
    */
    public void execute () {
    	if(demarrer==false){
    	thr=new Thread(this);
	thr.start();
	demarrer=true;
	}
    }

    /**
    * Fonction qui ferme le temp
    */
    public void kill(){
    	temps=0;
    	demarrer=false;
    	thr=null;
    	setText("Temps");
    }

}
