import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.color.*;

public class Jeux extends MouseAdapter
{
	public JLabel[][] grille;
	public int hauteur=3;
	public int largeur=3;
	int piece=0;
	int nbEssaie=50;
	
	JMenuItem nouvo ;
    JMenuItem quitter ;
    JMenuItem prop ;
    
    JLabel essaie;
	
	
	
	public Jeux()
	{
	  JFrame fenetre=new JFrame("Taquin V1.0");
	  fenetre.setIconImage(new ImageIcon("./images/icone.gif").getImage());
	  
	  Container contentPane = fenetre.getContentPane();
	  
	  JPanel panel=new JPanel();
      panel.setLayout(new GridLayout(hauteur,largeur,4,4)); 
      
      essaie=new JLabel("                          -=[NB essaie : 50]=-");
      
	  grille= new JLabel[hauteur][largeur];
	  
	  JMenu MenuJ = new JMenu("Jeux");
      nouvo = new JMenuItem("Nouveau");
      nouvo.addActionListener(new Nouveau());
      MenuJ.add(nouvo);
      quitter = new JMenuItem("Quitter");
      quitter.addActionListener(new Quitter());
      MenuJ.add(quitter);
      
      JMenu MenuAP = new JMenu("?");
      prop= new JMenuItem("A propos");
      prop.addActionListener(new Propos());
      MenuAP.add(prop);
      
 
      JMenuBar menuBar = new JMenuBar();
      menuBar.add(MenuJ);
      menuBar.add(MenuAP);
      
      fenetre.setJMenuBar(menuBar);
      
	  int indice=1;
	  
	  for(int i =0;i<hauteur;i++)
	  {
	   for(int j=0;j<largeur;j++)
	   {
	   	 grille[i][j]=new JLabel(getImage());
	   	 grille[i][j].addMouseListener(this);
	   	 panel.add(grille[i][j]);indice++;
	   }
      }
      
      contentPane.add(panel,BorderLayout.CENTER);
      contentPane.add(essaie,BorderLayout.SOUTH);
      fenetre.pack(); 
      fenetre.show();
      fenetre.setResizable(false);   	 
	  	
	}
	
	public ImageIcon getImage()
	{
		int indice=(int)(Math.random()*(hauteur*largeur))+1;
		
		if(existe(indice)==true || indice==0)
		{
		 while(existe(indice)==true)
		 {indice=(int)(Math.random()*9)+1;}
	    }
	    
	    
		piece=piece*10+indice;

		String renvoie = indice+""; 
		return new ImageIcon("./images/"+renvoie+".jpg");
    }
		
    
    
    public boolean existe(int indice)
    {   boolean b=false;
        int inter = piece;
    
    	while(inter>0)
    	{
    		if(inter%10==indice){b=true;}
    		inter=inter/10;
        }
 
        return b;
    }
    
    public String getVoisinVide(int i , int j)
    { 
        String resultat="null";
       
 
    
    	if(i!=0 && i!=hauteur-1 && j!=largeur-1 && j!=0)
    	{
    	 if(grille[i-1][j].getIcon().toString().equals("./images/1.jpg")){resultat="dessus";}
    	 if(grille[i+1][j].getIcon().toString().equals("./images/1.jpg")){resultat="sous";}
    	 if(grille[i][j-1].getIcon().toString().equals("./images/1.jpg")){resultat="gauche";}
    	 if(grille[i][j+1].getIcon().toString().equals("./images/1.jpg")){resultat="droite";}
        }
        
        if(i==0)
        {
        	if(j==0)
        	{
        	 if(grille[i+1][j].getIcon().toString().equals("./images/1.jpg")){resultat="sous";}
        	 if(grille[i][j+1].getIcon().toString().equals("./images/1.jpg")){resultat="droite";}
        	}
        	
        	if(j==largeur-1)
        	{
        	  if(grille[i][j-1].getIcon().toString().equals("./images/1.jpg")){resultat="gauche";}
        	  if(grille[i+1][j].getIcon().toString().equals("./images/1.jpg")){resultat="sous";}
            }
            
            if(j!=0 && j!=largeur-1)
            {
    	 	 if(grille[i+1][j].getIcon().toString().equals("./images/1.jpg")){resultat="sous";}
    	 	 if(grille[i][j-1].getIcon().toString().equals("./images/1.jpg")){resultat="gauche";}
    	 	 if(grille[i][j+1].getIcon().toString().equals("./images/1.jpg")){resultat="droite";}
    	 	}	
        }
        
        
        
        if(j==0)
        {
        	if(i==hauteur-1)
        	{
        	 if(grille[i-1][j].getIcon().toString().equals("./images/1.jpg")){resultat="dessus";}
    	 	 if(grille[i][j+1].getIcon().toString().equals("./images/1.jpg")){resultat="droite";}
    	    }
    	    
    	    if(i!=hauteur-1 && i!=0)
    	    {
    	     if(grille[i+1][j].getIcon().toString().equals("./images/1.jpg")){resultat="sous";}
    	 	 if(grille[i-1][j].getIcon().toString().equals("./images/1.jpg")){resultat="dessus";}
    	 	 if(grille[i][j+1].getIcon().toString().equals("./images/1.jpg")){resultat="droite";}
    	    }
        }
        
        
        if(i==hauteur-1)
        {
        	if(j==largeur-1)
        	{
        	 if(grille[i-1][j].getIcon().toString().equals("./images/1.jpg")){resultat="dessus";}
    	 	 if(grille[i][j-1].getIcon().toString().equals("./images/1.jpg")){resultat="gauche";}
    	    }
    	    
    	    if(j!=largeur-1 && j!=0)
    	    {
    	     if(grille[i-1][j].getIcon().toString().equals("./images/1.jpg")){resultat="dessus";}
    	 	 if(grille[i][j-1].getIcon().toString().equals("./images/1.jpg")){resultat="gauche";}
    	 	 if(grille[i][j+1].getIcon().toString().equals("./images/1.jpg")){resultat="droite";}
    	    }
        }
        
        if(j==largeur-1)
        {
        	if(i!=hauteur-1 && i!=0)
        	{
        	 if(grille[i-1][j].getIcon().toString().equals("./images/1.jpg")){resultat="dessus";}
    	 	 if(grille[i+1][j].getIcon().toString().equals("./images/1.jpg")){resultat="sous";}
    	 	 if(grille[i][j-1].getIcon().toString().equals("./images/1.jpg")){resultat="gauche";}
    	    }
        }
        
        return resultat;
    }
    
    public void changer(JLabel a , JLabel b)
    {
    	Icon img = a.getIcon();
    	a.setIcon(b.getIcon());
    	b.setIcon(img);
    }
    
    public void verifier()
    {
      if(grille[0][0].getIcon().toString().equals("./images/1.jpg") &&
         grille[0][1].getIcon().toString().equals("./images/2.jpg") &&
         grille[0][2].getIcon().toString().equals("./images/3.jpg") &&
         grille[1][0].getIcon().toString().equals("./images/4.jpg") &&
         grille[1][1].getIcon().toString().equals("./images/5.jpg") &&
         grille[1][2].getIcon().toString().equals("./images/6.jpg") &&
         grille[2][0].getIcon().toString().equals("./images/7.jpg") &&
         grille[2][1].getIcon().toString().equals("./images/8.jpg") &&
         grille[2][2].getIcon().toString().equals("./images/9.jpg") )
      {
      	JOptionPane fin = new JOptionPane();
        fin.showMessageDialog(null,"Vous avez gagné avec "+(50-nbEssaie)+" essais !!!","Bravo",JOptionPane.INFORMATION_MESSAGE);  	 
      }
    }   	
    
    		
    
    public void mouseClicked(MouseEvent e)
    {
      Object label = e.getSource();
      for(int i=0;i<hauteur;i++)
      {
        for(int j=0;j<largeur;j++)
        {
          if(grille[i][j]==label)
          {
          	if(nbEssaie!=0 && grille[i][j].getIcon().toString()!="./images/0.jpg")
          	{
          	   essaie.setText("                          -=[NB essaie : "+nbEssaie+"]=-");
          	
               if(getVoisinVide(i,j)=="dessus"){changer(grille[i][j] , grille[i-1][j]);nbEssaie--;}
               if(getVoisinVide(i,j)=="sous"){changer(grille[i][j] , grille[i+1][j]);nbEssaie--;}
               if(getVoisinVide(i,j)=="gauche"){changer(grille[i][j] , grille[i][j-1]);nbEssaie--;}
               if(getVoisinVide(i,j)=="droite"){changer(grille[i][j] , grille[i][j+1]);nbEssaie--;}
            }else
                {
                  JOptionPane fin = new JOptionPane();
                  fin.showMessageDialog(null,"Vous avez perdu !",":-(",JOptionPane.ERROR_MESSAGE);	
                }
          }
        }
      }verifier();
    }
    
    public class Quitter implements ActionListener {
	public void actionPerformed(ActionEvent e){
			System.gc();
			System.exit(0);	
		}
	}
	
	public class Nouveau implements ActionListener {
	public void actionPerformed(ActionEvent e){
			nbEssaie=50;
			piece=0;
			essaie.setText("                          -=[NB essaie : 50]=-");
			for(int i =0;i<hauteur;i++){
	         for(int j=0;j<largeur;j++){
	   			 grille[i][j].setIcon(getImage());
	         }
            }
			
		}
	}
	
	public class Propos implements ActionListener {
	public void actionPerformed(ActionEvent e){
		
		JFrame fen = new JFrame("A propos de Taquin ...");
		fen.setIconImage(new ImageIcon("./images/icone.gif").getImage());
		
		Dimension dim = (fen.getToolkit()).getScreenSize();
        Dimension dim2 = fen.getSize();
        fen.setLocation(( dim.width - dim2.width ) / 3,( dim.height - dim2.height ) / 4);
	  
	    Container contentPane = fen.getContentPane();
	    JLabel centre = new JLabel(new ImageIcon("./images/font.jpg"));
	    
	    contentPane.add(centre,BorderLayout.CENTER);
	     
	    fen.pack(); 
        fen.show();
        fen.setResizable(false);
      }
    }   
	

	
	public static void main(String [] args)
	{Jeux j = new Jeux();}
}	