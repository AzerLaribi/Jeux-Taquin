import java.awt.*;

/**
* class qui gere les parametres des differents niveau
*/
public abstract class Parametres{

public static Color couleurPiece=Color.white;
public static Color couleurVide=Color.pink;
public static Color couleurSelection=Color.green;
public static Color couleurMenu=Color.white;
public static Color couleurBackButton=Color.black;
public static Color couleurFrontButton=Color.white;


public static boolean visibleButton=true;

public static Font fontMenu=new Font("Times New Roman",Font.BOLD+Font.ITALIC,20);
public static Font fontTemps=new Font("GungsuhChe",Font.PLAIN,12);
public static Font fontCase=new Font("Times New Roman",Font.BOLD+Font.ITALIC,20);
public static Font fontButton=new Font("Times New Roman",Font.BOLD+Font.ITALIC,20);


public static int random =0;
public static int NbLigne=0;
public static int NbColonne=0;
public static int UndoMax=0;

public static final int debutant_random=20;
public static final int debutant_NbLigne=2;
public static final int debutant_NbColonne=2;
public static final int debutant_UndoMax=1000;

public static final int intermediaire_random=50;
public static final int intermediaire_NbLigne=4;
public static final int intermediaire_NbColonne=4;
public static final int intermediaire_UndoMax=20;

public static final int expert_random=100;
public static final int expert_NbLigne=6;
public static final int expert_NbColonne=6;
public static final int expert_UndoMax=10;

public static int perso_random=20;
public static int perso_NbLigne=2;
public static int perso_NbColonne=2;
public static int perso_UndoMax=1000;

public static String nomJoueur="";
}
