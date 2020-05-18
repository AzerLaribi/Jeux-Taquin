import java.beans.*;
import java.io.*;

import java.awt.*;
import javax.swing.*;


/**
* Gere les entree sortie xml
*/
public class Fonction{


/**
* renvoie la valeur d un string contenant une suite de 0 et de 1 en entier
* @param  converti String a convertir
* @return renvoie la representation du string en entier
*/
    public static String Temps(String Converti){
	int temps=Integer.parseInt(Converti);
        Converti=(""+temps/3600+" h "+((temps/60)%3600)+" min "+(temps%60)+"s");
        return Converti;
    }



    /**
    * encode en xml un objet dans un fichier
    * @param Objet objet a encoder
    * @param fichier fichier de destination
    */
    public static void XmlEncodeur(Object Objet,String fichier){
    try{
    XMLEncoder encode = new XMLEncoder(  //on ouvre le fichier
                          new BufferedOutputStream(
                              new FileOutputStream(fichier)));
       //on ecrit dans le fichier
       encode.writeObject(Objet);
       encode.flush();
       //on ferme le fichier
       encode.close();
       }catch (Exception e) { System.out.println("porbleme avec le fichier: "+fichier);e.printStackTrace(System.err);}
       }


    /**
    * decode en xml un objet dans un fichier
    * @param fichier ou l objet est encode
    * @throws FileNotFoundExecption renvoie une erreur si le fichier n existe pas
    * @return Objet objet decodé
    */
    public static Object XmlDecodeur(String fichier) throws FileNotFoundException{
    Object Objet; //objet dans lequel on stocke l objet decodé
    XMLDecoder decodeur = new XMLDecoder(      //on ouvre le fichier
                          new BufferedInputStream(
                              new FileInputStream(fichier)));
       Objet = decodeur.readObject();  //on recupere l objet
       decodeur.close();  //on ferme le fichier
       return Objet;
       }


    /**
    *  Initialise le fichier des polices
    * @param fichier nom du fichier ou initialiser les polices
    */
    public static void IniPolice(String fichier){
    Font TabFont[]=new Font[4];

    //on stocke les parametres des polices dans un tableau
    TabFont[0]=Parametres.fontMenu;
    TabFont[1]=Parametres.fontTemps;
    TabFont[2]=Parametres.fontCase;
    TabFont[3]=Parametres.fontButton;

    //on ecrite dans le fichier
    XmlEncodeur(TabFont,fichier);
    }


    /**
    * initialisation des polices a partir d un tableau de police
    * @param TabFont tableau de police
    */
    public static void loadPolice(Font TabFont[]){
    //initialisation des polices
    Parametres.fontMenu=TabFont[0];
    Parametres.fontTemps=TabFont[1];
    Parametres.fontCase=TabFont[2];
    Parametres.fontButton=TabFont[3];
    }


    /**
    * Charge les polices a partir d un fichier
    * @param FPolice fichier xml contenant les polices
    */
    public static void chargerPolice(String FPolice){
    //on initialise un tableau de font
    Font TabPolice[]=null;
     //initialisation des polices enregistrées
        try{
        try{
        TabPolice=(Font [])Fonction.XmlDecodeur(FPolice);
        } catch(FileNotFoundException e){        //si le fichier n as pas ete  cree
                                        	System.out.println("creation du fichier de Parametres");
                                        	// on l initialise
                                                Fonction.IniPolice(FPolice);
                                                //on charge le tableau de font
                                                TabPolice=(Font [])Fonction.XmlDecodeur(FPolice);}


        if(TabPolice==null){//si le fichier ne contient pas l information voulu
                //on l initialise
                Fonction.IniPolice(FPolice);
                //on charge le tableau de font
                TabPolice=(Font [])Fonction.XmlDecodeur(FPolice);}
        //on attrape encore l'erreur pour tout probleme concerant le fichier
        }catch (Exception e) { System.out.println("porbleme avec le fichier: "+FPolice);e.printStackTrace(System.err);}

    //on charge les polices
    loadPolice(TabPolice);

    }

    /**
    * Charge les parametres a partir d un fichier
    * @param FParametres fichier xml contenant les parametress
    */
    public static void loadParametres(String FParametres){
    String [][]TabParametres=null;
     //initialisation des parametres enregistrés
        try{
        try{
        TabParametres=(String [][])Fonction.XmlDecodeur(FParametres);
        } catch(FileNotFoundException e){        //si le fichier n as pas ete  cree
                                        	System.out.println("creation du fichier de Parametres");
                                                //on l initialise
                                                Fonction.saveParametres(FParametres);
                                                //on charge le tableau de parametres
                                                TabParametres=(String [][])Fonction.XmlDecodeur(FParametres);}


        if(TabParametres==null){//si le fichier ne contient pas l information voulu
                //on l initialise
                Fonction.saveParametres(FParametres);
                //on charge le tableau de parametres
                TabParametres=(String [][])Fonction.XmlDecodeur(FParametres);}
        //on attrape encore l'erreur pour tout probleme concerant le fichier
        }catch (Exception e) { System.out.println("porbleme avec le fichier: "+FParametres);e.printStackTrace(System.err);}
        //on charge les parametres
        chargerParametres(TabParametres);

    }

    /**
    * initialisation des parametres a partir d un tableau de paramatres
    * @param TabParametres tableau de parametres
    */
    public static void chargerParametres(String [][]TabParametres){
    String TabCouleur[]=TabParametres[0]; //tableau des couleurs
    String TabPerso[]=TabParametres[1];   //tableau des parametres du taquin

        //initialisation des couleurs
        Parametres.couleurPiece=new Color(Integer.parseInt((String)TabCouleur[0]));
        Parametres.couleurVide=new Color(Integer.parseInt((String)TabCouleur[1]));
        Parametres.couleurSelection=new Color(Integer.parseInt((String)TabCouleur[2]));
        Parametres.couleurMenu=new Color(Integer.parseInt((String)TabCouleur[3]));
        Parametres.couleurFrontButton=new Color(Integer.parseInt((String)TabCouleur[4]));
        Parametres.couleurBackButton=new Color(Integer.parseInt((String)TabCouleur[5]));

        //initialisation des parametres du taquin
        Parametres.perso_random=Integer.parseInt((String)TabPerso[0]);
        Parametres.perso_NbLigne=Integer.parseInt((String)TabPerso[1]);
        Parametres.perso_NbColonne=Integer.parseInt((String)TabPerso[2]);
        Parametres.perso_UndoMax=Integer.parseInt((String)TabPerso[3]);

    }


    /**
    * Sauvegarde les parametres dans un fichier
    * @param fichier fichier ou sauvegarder les parametres
    */
    public static void saveParametres(String fichier){
    String TabParametres[][]=new String[2][]; //tableau des parametres
    String TabCouleur[]=new String[6];     //tableau des couleurs
    String TabPerso[]=new String[4];       //tableau des parametres du taquin

    //on enregistre les parametres dans un tableau
    TabCouleur[0]=""+Parametres.couleurPiece.getRGB();
    TabCouleur[1]=""+Parametres.couleurVide.getRGB();
    TabCouleur[2]=""+Parametres.couleurSelection.getRGB();
    TabCouleur[3]=""+Parametres.couleurMenu.getRGB();
    TabCouleur[4]=""+Parametres.couleurFrontButton.getRGB();
    TabCouleur[5]=""+Parametres.couleurBackButton.getRGB();

    TabParametres[0]=TabCouleur;  // premier tableau represente les couleurs

    //on enregistre les parametres du taquin dans un tableau
    TabPerso[0]=""+Parametres.perso_random;
    TabPerso[1]=""+Parametres.perso_NbLigne;
    TabPerso[2]=""+Parametres.perso_NbColonne;
    TabPerso[3]=""+Parametres.perso_UndoMax;


    TabParametres[1]=TabPerso;     //second tableau representant les parametres du model

    //on procede a l enregistrement en xml dans le fichier
    XmlEncodeur(TabParametres,fichier);
    }

    /**
    * Permet de savoir si le score est un highScore
    * @param TabScore tableau des scores
    * @param score score du joueur
    * @param nom nom du joueur
    * @param temps temps du joueur
    * @return boolean indique si oui ou non c un highscore
    */
    public static boolean HighScore(String [][]TabScore,int score,String nom,int temps){
    return ((score<Integer.parseInt(TabScore[TabScore.length-1][0])) ||
                                              (score==Integer.parseInt(TabScore[TabScore.length-1][0])
                                                  && temps<Integer.parseInt(TabScore[TabScore.length-1][1])));
    }



    /**
    * Initialise un tableau de score dans un fichier xml
    * @param fichier fichier ou enregistrer le tableau de score
    */
    public static void IniScore(String fichier){
    String TabScore[][]=new String[5][3];   //tableau de score

    //on initalise le tableau de score
    for(int i=0;i<TabScore.length;i++){
    TabScore[i][0]=""+2000;  //nombre de points
    TabScore[i][1]=""+1000;  //temps
    TabScore[i][2]="";       //nom
    }
    //on encode le tableau de score dans un fichier
    XmlEncodeur(TabScore,fichier);
    }

    /**
    * Initialise un tableau de score a partir d un fichier
    * @param fichier fichier pour charger le tableau de score
    * @return TabScore tableau des scores
    */
    public static String[][] loadScore(String fichier){
    String [][]TabScore=null;  //on initialise un tableau de score
     try{
        try{
        TabScore=(String [][])Fonction.XmlDecodeur(fichier);   //on decode le fichier des scores
        } catch(FileNotFoundException e){       //si le fichier n as pas ete  cree
                                        	System.out.println("creation du fichier de score");
                                                Fonction.IniScore(fichier);  //on initialise le fichier des scores
                                                TabScore=(String [][])Fonction.XmlDecodeur(fichier);}

        if(TabScore==null){  //si le fichier ne contient pas l information voulu
                Fonction.IniScore(fichier);
                TabScore=(String [][])Fonction.XmlDecodeur(fichier);}
        //on attrape encore l'erreur pour tout probleme concerant le fichier
        }catch (Exception e) { System.out.println("porbleme avec le fichier: "+fichier);e.printStackTrace(System.err);}
      //on renvoie le tableau de score
      return TabScore;
      }

    /**
    * Ajoute une entree de score dans un tableau de score
    * @param TabScore tableau des scores
    * @param score score du joueur
    * @param nom nom du joueur
    * @param temps temps effectué par le joueur
    */

    public static String[][] ajoutElt(String [][]TabScore,int score,String nom,int temps){

        //on parcours le fichier de score
        for(int i=TabScore.length-1;i>=0;i--){
        //si le score ne fait pas parti des highscores on sort
        if((score>Integer.parseInt(TabScore[i][0])) ||
                                                    (score==Integer.parseInt(TabScore[i][0])
                                                      && temps>Integer.parseInt(TabScore[i][1]))){return TabScore;}
        //si il est meilleur que le dernier du tableau on le rentre
        if(i==(TabScore.length-1)){     TabScore[TabScore.length-1][0]=""+score;
                                        TabScore[TabScore.length-1][1]=""+temps;
                                        TabScore[TabScore.length-1][2]=nom;}
        else{   //on echange les cases tant que le score est meilleur (tri bulle)
                TabScore[i+1][0]=TabScore[i][0];
                TabScore[i+1][1]=TabScore[i][1];
                TabScore[i+1][2]=TabScore[i][2];
                TabScore[i][0]=""+score;
                TabScore[i][1]=""+temps;
                TabScore[i][2]=nom;}
        }
        //on retourne le tableau des scores
        return TabScore;
        }

}
