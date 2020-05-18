import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import java.awt.GraphicsEnvironment;

/**
 * Taquin avec Undo/Redo simple
 * @author Capellier Sebastien, Zorgati Maher
 * @version 1.0
 */
public class Taquin {
    // Architecture Modèle/Vue/Contrôleur
    // Il n'y a pas de contrôleur car celui-ci est réparti entre
    // les actions de la vue.
    public Model plateau;   //Model du taquin
    private View view ;     //vue du taquin
    private String FScore="Score.xml";   //fichier d'enregistrement des scores
    private String FPartie="Partie.xml";     //fichier d'enregistrement d'une partie
    private String FParametres="Parametres.xml"; //fichier d'enregistrement des parametres
    private String FPolice="Police.xml";    //fichier d'enregistrement des polices utilisées
    private String TabScore[][]; //notre tableau de scores
    private GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();  //recuperation de l environnement graphique
    public int Score;   //score  d'une partie
    public int NbClick; //nombre de click effectué dans une partie
    private JAvancee avancee;  //menu des options
    Horloge horloge;   //JLabel du temps


    /**
    * Constructeur du Taquin
    */
    public Taquin() {

        //initialisation du tableau des scores
        TabScore=Fonction.loadScore(FScore);
        //initialisation des parametres
        Fonction.loadParametres(FParametres);
        //initialisation des polices
        Fonction.chargerPolice(FPolice);
        //initialisation de la vue
        view=new View();
        // Mise en place
	view.pack();
	//placement de la fenetre
        view.setLoc();
        // Mise en place
	view.update();
	// Affichage de la vue
	view.setVisible(true);

        }


    /**
    * Fonction de lancement du Taquin
    * @param args argument sur la ligne de commande non-prise en compte dans ce programme
    */
    public static void main(String [] args)
    {
	// Création
	new Taquin();
    }


    /**
    * Vue du Taquin
    */
    class View extends JFrame {
	final String imagedir = "images/";    //dossier contenat les images
	private UndoManager undoManager = new UndoManager(); //gestionnaire des undo/redo
	private Piece damier[][]; //matrice du model
	private JScore jscore;    //Tableau des scores


        // Gestionnaire de modifications;
	JComponent arena = new JPanel();
	Container contentPane;

	//Buttons elementaires
	JButton leftButton ;
	JButton rightButton;
	JButton upButton;
	JButton downButton;
	JButton undobutton;
	JButton redobutton;
        JRadioButtonMenuItem deb;
	JRadioButtonMenuItem inter;
	JRadioButtonMenuItem exp;
	JRadioButtonMenuItem perso;



        //Icones
	ImageIcon leftIcon;
	ImageIcon rightIcon;
	ImageIcon upIcon;
	ImageIcon downIcon;
	ImageIcon undoIcon;
	ImageIcon redoIcon;
	ImageIcon bordIcon;

	// Actions
	MoveAction leftAction;	// Déplacement du mobile vers la gauche
	MoveAction rightAction;	// Déplacement du mobile vers la droite
	MoveAction upAction;	// Déplacement du mobile vers le haut
	MoveAction downAction;	// Déplacement du mobile vers le bas
	UndoAction undoAction;	// Annulation du dernier déplacement
	RedoAction redoAction;	// refaire un déplacement annulé
        ExitAction exitAction;   //fermer le jeu
        ExitAction savexitAction; //fermer et sauvegarder le jeu
        NewAction newAction;     //nouveau jeu
        NewAction chargerAction; //chargement du jeu
        AvanceeAction avanceeAction; //menu des options
	NiveauAction debutant;      //initialisation du niveau debutant
	NiveauAction intermediaire; //initialisation du niveau intermediaire
	NiveauAction expert;       //initialisation du niveau expert
	PersoAction personnel;     //initialisation du niveau personnel
	ScoreAction scoreaction;   //Lancement de la fenetre du score
        
        // Les quatre déplacements
	Move leftMove;
	Move rightMove;
	Move upMove ;
	Move downMove;

        //Menu
	JMenuBar menuBar;

        /**
        * Constructeur de la vue
        */
        View() {

            super("Taquin avec Undo/Redo multiple"); // Constructeur avec titre

	    avancee=new JAvancee();       //initialisation de la fenetre des options
            contentPane = getContentPane();  //recuperation de l environnement
	    addWindowListener(new WindowAdapter() {    //mise en place d'un ecouteur en case de fermeture
			public void windowClosing(WindowEvent e){
                                exitAction.actionPerformed(null);
			}
			public void windowClosed(WindowEvent e){
			}
	    });
	    //chargement des images
            this.loadImage();
            // chargement des deplacements
	    this.loadDeplacement();
	    //chargement des actions
	    this.loadAction();
	    //chargement des boutons
	    this.loadButton();
	    //chargement du menu
	    this.loadMenu();
	    //initialisation au niveau debutant
            debutant.actionPerformed(null);
            //mise en place
            pack();
        }

        /**
        * Fonction de chargement du plateau de jeu
        * @param charger indique si le jeu vient d'etre charger
        */
        void loadArena(boolean charger){
            //on arrete l'horloge
            horloge.kill();
            //on vide l'undo manager
            undoManager.discardAllEdits();
            NbClick=0;
            Score=0;
            //on rend invisible l'arene
            arena.setVisible(false);
            //pour mieux la supprimer
            remove(arena);
            
            //n rend visible ou non les boutons
            addremoveButton(Parametres.visibleButton);
            //on gere le cas si la partie a ete charger
            if(charger){
                        try{    //on initialise un score mauvais
                                NbClick=200000;
                                //initialisation  de la matrice de jeu avec le fichier de sauvegarde des parties
                                int arene[][]=(int [][])Fonction.XmlDecodeur(FPartie);
                                //initialisation du model grace a la precedente matrice
                                plateau=new Model(arene);}
                                //gestion du cas ou aucune partie n'a été sauvegardée
                        catch(FileNotFoundException e){charger=false;
                                                       JOptionPane.showMessageDialog(
                                                       view,
                                                       "Aucune partie sauvegarder",
                                                       "probleme fichier sauvegarde",
                                                       JOptionPane.ERROR_MESSAGE
                                                       );}

            }
            else{       //initialisation  de la matrice de jeu avec les parametres requis
                	plateau=new Model(Parametres.NbLigne,Parametres.NbColonne);
	                //on melange le Taquin
                        plateau.RandomArea(Parametres.random);}
            //on fixe la limite de l'undo
            undoManager.setLimit(Parametres.UndoMax);

            //creation d'une nouvelle arene
            arena = new JPanel();

            //on  initialise le gestionnaire de placement et son bord
            arena.setLayout(new GridLayout(plateau.getNbLigne(),plateau.getNbColonne(),1,1));
	    arena.setBorder(BorderFactory.createMatteBorder(15,15,15,15,bordIcon));


            //initialisation de la matrice du model
            damier=new Piece[plateau.getNbLigne()][plateau.getNbColonne()];

            for(int i=0;i<plateau.getNbLigne();i++){
		for(int j=0;j<plateau.getNbColonne();j++){
                    damier[i][j]=new Piece(""+plateau.getCase(i,j),i,j,Parametres.couleurPiece);
                    damier[i][j].setFont(Parametres.fontCase);
                    //on rajoute chacun des elements a l'arene
                    arena.add(damier[i][j]);
		}
	    }
            //on initialise la place vide
            damier[plateau.getPosX()][plateau.getPosY()].setText("");
	    damier[plateau.getPosX()][plateau.getPosY()].setCouleur(Parametres.couleurVide);


	    // Vue mise à jour lorsque l'arène change de taille
	    arena.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent event) {
	                               update();
		    }});
	    contentPane.add(arena, BorderLayout.CENTER);
	}


        /**
        * Fonction de chargement du plateau de jeu
        * @param arene, permet de charger une arene avec un model donné
        */
        void loadArena(int arene[][]){

             //on rend invisible l'arene
            arena.setVisible(false);
            //pour mieux la supprimer
            remove(arena);

            //on rend visible ou non les boutons
            addremoveButton(Parametres.visibleButton);
            //initialisation du model
            plateau=new Model(arene);



            //creation d'une nouvelle arene
            arena = new JPanel();
            
            //initialisation de la matrice du model
            damier=new Piece[plateau.getNbLigne()][plateau.getNbColonne()];

            //on  initialise le gestionnaire de placement et son bord
            arena.setLayout(new GridLayout(plateau.getNbLigne(),plateau.getNbColonne(),1,1));
	    arena.setBorder(BorderFactory.createMatteBorder(15,15,15,15,bordIcon));

            for(int i=0;i<plateau.getNbLigne();i++){
		for(int j=0;j<plateau.getNbColonne();j++){
                    damier[i][j]=new Piece(""+plateau.getCase(i,j),i,j,Parametres.couleurPiece);
                    damier[i][j].setFont(Parametres.fontCase);
                    arena.add(damier[i][j]);
		}
	    }
            //on initialise la place vide
            damier[plateau.getPosX()][plateau.getPosY()].setText("");
	    damier[plateau.getPosX()][plateau.getPosY()].setCouleur(Parametres.couleurVide);




	    // Vue mise à jour lorsque l'arène change de taille
	    arena.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent event) {
	                               update();
		    }});
	    contentPane.add(arena, BorderLayout.CENTER);

            pack();
	}


        /**
        * Association et placement des boutons avec leurs actions
        */
        void loadButton(){
        // Boutons
	    JPanel barretat= new JPanel(); //barre d "etat" contenant le bouton down et l horloge
	    //Association des boutons avec leurs actions
            leftButton = new JButton(leftAction);
	    rightButton = new JButton(rightAction);
	    upButton = new JButton(upAction);
	    downButton = new JButton(downAction);
            undobutton=new JButton(undoAction);
	    redobutton=new JButton(redoAction);

            //lancement du design des boutons (couleurs,texte...)
            loadDesignButton();

            //placement des boutons
            contentPane.add(upButton, BorderLayout.NORTH);  //le up boutton au nord
	    contentPane.add(leftButton, BorderLayout.WEST); //le left a l ouest
	    contentPane.add(rightButton, BorderLayout.EAST); // le right a l est

	    barretat.setLayout(new BorderLayout());
	    barretat.add(downButton, BorderLayout.NORTH);
	    barretat.add(horloge=new Horloge(), BorderLayout.EAST);

	    contentPane.add(barretat, BorderLayout.SOUTH);    // la barre au sud

	    //Association des raccourcis claviers
            leftButton.setMnemonic(KeyEvent.VK_RIGHT);
            rightButton.setMnemonic(KeyEvent.VK_LEFT);
            upButton.setMnemonic(KeyEvent.VK_DOWN);
            downButton.setMnemonic(KeyEvent.VK_UP);
	}


     /**
     * Mise en forme des Boutons
     */
     void loadDesignButton(){
     //undo Bouton
     undobutton.setBackground(Color.blue);

     //bouton gauche
     leftButton.setBackground(Parametres.couleurBackButton);
     leftButton.setForeground(Parametres.couleurFrontButton);
     leftButton.setFont(Parametres.fontButton);

     //bouton droit
     rightButton.setBackground(Parametres.couleurBackButton);
     rightButton.setForeground(Parametres.couleurFrontButton);
     rightButton.setFont(Parametres.fontButton);

     //bouton haut
     upButton.setBackground(Parametres.couleurBackButton);
     upButton.setForeground(Parametres.couleurFrontButton);
     upButton.setFont(Parametres.fontButton);

     //bouton bas
     downButton.setBackground(Parametres.couleurBackButton);
     downButton.setForeground(Parametres.couleurFrontButton);
     downButton.setFont(Parametres.fontButton);
     }

     /**
     * mise en place ou enlevement des boutons
     */
      void addremoveButton(boolean param){
      
      leftButton.setVisible(param);
      rightButton.setVisible(param);
      upButton.setVisible(param);
      downButton.setVisible(param);
      
      }

      /**
      * Mise en place du menu
      */
      void loadMenu(){
          //variable pertant d iniatialiser ou de sauvegarder les radio button d un menu
          boolean bdeb=true;
          boolean binter=false;
          boolean bexp=false;
          boolean bperso=false;
          // Barre de menus
	  menuBar = new JMenuBar();

          //placement de l undo redo
          JPanel placement=new JPanel();

          //si on recharge le menu on initialise les radiobuton
          if(deb!=null){
                bdeb=deb.isSelected();
                binter=inter.isSelected();
                bexp=exp.isSelected();
                bperso=perso.isSelected();}

          //bouton radio permettant de selectionner le niveau
          deb=new JRadioButtonMenuItem(debutant);
	  deb.setSelected(bdeb);
          inter=new JRadioButtonMenuItem(intermediaire);
          inter.setSelected(binter);
	  exp=new JRadioButtonMenuItem(expert);
          exp.setSelected(bexp);
          perso=new JRadioButtonMenuItem(personnel);
          perso.setSelected(bperso);
	  //Menu de la barre des Options
          JMenu Options = new JMenu("Options");

          Options.add(deb);
          Options.add(inter);
          Options.add(exp);
          Options.add(perso);
          Options.addSeparator();
          Options.add(new JMenuItem(avanceeAction));
	  ButtonGroup niv= new ButtonGroup();
	  niv.add(deb);
	  niv.add(inter);
	  niv.add(exp);
	  niv.add(perso);

	  // Menu de la barre de Jeu
          JMenu Jeu = new JMenu("Jeu");
          //Mise en place du raccourci clavier associé
          Jeu.setMnemonic(KeyEvent.VK_J);

          Jeu.add(new JMenuItem(newAction));
          Jeu.add(new JMenuItem(chargerAction));
	  Jeu.add(Options);
	  Jeu.add(new JMenuItem(exitAction));
          Jeu.add(new JMenuItem(savexitAction));

          //Menu de la barre informative
          JMenu Info = new JMenu("?");
          //Mise en place du raccourci clavier associé
          Info.setMnemonic(KeyEvent.VK_F1);

          Info.add(new JMenuItem(scoreaction));

           //Menu de la barre de mouvement
           JMenu Move=new JMenu("Move");
           Move.add(new JMenuItem(leftAction));
           Move.add(new JMenuItem(rightAction));
           Move.add(new JMenuItem(upAction));
           Move.add(new JMenuItem(downAction));
           Move.setVisible(false);

          // Boutons Undo et Redo dans la barre de menu
          undobutton.setMargin(new Insets(0,0,0,0) );
          placement.add(undobutton);

          redobutton.setMargin(new Insets(0,0,0,0) );
          placement.add(redobutton);

          //Mise en place des couleurs du menu
          placement.setBackground(Parametres.couleurMenu);
          Jeu.setBackground(Parametres.couleurMenu);
          Info.setBackground(Parametres.couleurMenu);

          //Mise en place des fonts
          Jeu.setFont(Parametres.fontMenu);
          Info.setFont(Parametres.fontMenu);

          //on ajoute tout au menu
          menuBar.add(Jeu);
          menuBar.add(Info);
          menuBar.add(Move);
          menuBar.add(placement);

          //on met en place la barre de menu
          setJMenuBar(menuBar);
          }

	  /**
	  * Fonction de chargement des images
	  */
          void loadImage(){
	    // Icônes pour les boutons
	    leftIcon  = getIcon(imagedir + "left.png");
	    rightIcon = getIcon(imagedir + "right.gif");
	    upIcon    = getIcon(imagedir + "up.gif");
	    downIcon  = getIcon(imagedir + "down.gif");
	    undoIcon  = getIcon(imagedir + "undo.gif");
	    redoIcon  = getIcon(imagedir + "nextperso.gif");
	    bordIcon  = getIcon(imagedir + "fond.jpg");
        }

        /**
        * Gestion de la mise en place des actions
        */
        void loadAction(){
	    // Actions

            leftAction = new MoveAction(leftMove, "Element de Gauche", rightIcon, KeyEvent.VK_RIGHT);
	    rightAction = new MoveAction(rightMove, "Element de Droite", leftIcon, KeyEvent.VK_LEFT);
	    upAction = new MoveAction(upMove, "Element du Haut", downIcon, KeyEvent.VK_DOWN);
	    downAction = new MoveAction(downMove, "Element du bas", upIcon, KeyEvent.VK_UP);
	    undoAction = new UndoAction("", undoIcon, KeyEvent.VK_BACK_SPACE);
	    redoAction = new RedoAction("", redoIcon, KeyEvent.VK_ENTER);
            exitAction = new ExitAction("Quit",null,KeyEvent.VK_Q,false);
            savexitAction = new ExitAction("Quit and Save",null,KeyEvent.VK_E,true);
            newAction= new NewAction("Nouvelle partie",null,KeyEvent.VK_N,false);
	    chargerAction= new NewAction("Charger partie",null,KeyEvent.VK_C,true);
            avanceeAction= new AvanceeAction("Avancee",null,KeyEvent.VK_A);
      	    debutant=new NiveauAction("Debutant",null,Parametres.debutant_random,Parametres.debutant_NbLigne,Parametres.debutant_NbColonne,Parametres.debutant_UndoMax);
      	    intermediaire=new NiveauAction("Intermediare",null,Parametres.intermediaire_random,Parametres.intermediaire_NbLigne,Parametres.intermediaire_NbColonne,Parametres.intermediaire_UndoMax);
      	    expert=new NiveauAction("Expert",null,Parametres.expert_random,Parametres.expert_NbLigne,Parametres.expert_NbColonne,Parametres.expert_UndoMax);
      	    personnel=new PersoAction("Perso",null);
            scoreaction=new ScoreAction ("Score",null,KeyEvent.VK_S);

      }

       /**
       * Mise en place de la valeur des deplacements
       */
	void loadDeplacement(){
	    // Les quatre déplacements
	    leftMove = new Move(0 , -1);
	    rightMove = new Move(0 , 1);
	    upMove = new Move(-1 , 0);
	    downMove = new Move(1 , 0);}

        /**
        * Fonction de mise a jour de la vue
        */
	void update() {
       // Mise à jour de l'activation des actions
            leftAction.updateEnabled();
	    rightAction.updateEnabled();
	    upAction.updateEnabled();
	    downAction.updateEnabled();
	    undoAction.updateEnabled();
	    redoAction.updateEnabled();
	//on verifie la condition de victoire
            if(plateau.Victoire()){loadVictoire();}
	}

        /**
        * Lancement de la fenetre de victoire
        */
	void loadVictoire(){
	//on recupere le temps
        int temps=horloge.getTime();
        //on arrete l horloge
        horloge.kill();
        //on iniitialise le score
	int score=Parametres.UndoMax-(Parametres.NbColonne*Parametres.NbLigne)+NbClick+(temps/(Parametres.NbColonne*Parametres.NbLigne));
        //on lance la fenetre de victoire permettant de recuperer le nom du joueur
        String nom = (String)JOptionPane.showInputDialog(
                    view,
                    "C'est Gagne *!*!*!*!*!*:\n"+
                    "Points: "+score+"\n"
                    + "\"Mets ton joli nom\"",
                    "*-*-*-*-*Victoire V*-*-*-*-*-",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    Parametres.nomJoueur);
        //on ajoute le nom au tableau des scores si necessaire
        if(nom!=null && Fonction.HighScore(TabScore,score,nom,temps)){TabScore=Fonction.ajoutElt(TabScore,score,nom,temps); }
        //on affiche le tableau des scores
        jscore=new JScore(TabScore);
        //on relance une partie
        newAction.actionPerformed(null);
        }

	// Méthode permettant de récupérer une image à partir du fichier
	ImageIcon getIcon(String file) {
	    // La façon normale est de faire "new ImageIcon(file);"
	    // mais cette méthode ne marche pas lorsque les fichiers
	    // d'images sont inclus dans un fichier jar.
	    return new ImageIcon(Taquin.class.getResource(file));
	}

	/**
	* echange de la valeur d'une case avec la position vide
	* @param dx abscisse de la case a echanger
	* @param dy ordonnee de la case a echanger
	*/
        void echange(int dx, int dy){
	    //au premier deplacement l horloge va s activer
            horloge.execute();
	    //on recupere la valeur de la case
            String tmp=damier[plateau.getPosX()][plateau.getPosY()].getText();
	    //on procede au changement
            damier[plateau.getPosX()][plateau.getPosY()].setText("");
	    damier[plateau.getPosX()][plateau.getPosY()].setCouleur(Parametres.couleurVide);
	    damier[plateau.getPosX()+dx][plateau.getPosY()+dy].setText(tmp);
	    damier[plateau.getPosX()+dx][plateau.getPosY()+dy].setCouleur(Parametres.couleurPiece);
	}

        /**
        * Positionnement de la vue au milieu de l ecran
        */
        void setLoc(){
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
        }

	/**
        * Action de déplacement
         */
	class MoveAction extends AbstractAction {
	Move move;		// Déplacement
	    /**
            * Construction
	    * @param move mouvement associé
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    * @param key key acceleratrice associé a l action
            */
            public MoveAction(Move move, String text,ImageIcon icon, int key) {
        	super(text,icon);
                setForeground(Parametres.couleurFrontButton);
                // Met la touche comme un accélérateur sans modificateur
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, 0));
		this.move = move;
	    }
	    /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
            public void actionPerformed(ActionEvent e) {
		// Enregistrement de la modification auprès du gestionnaire
		undoManager.addEdit(new MoveEdit(move));
		// Action déléguée au déplacement
		NbClick++;
		move.doit();
		// Mise à jour de la vue
		view.update();
	    }
	/**
        * Mise à jour de l'activation
	*/
            void updateEnabled() {
		// L'activation est déterminée par le déplacement
		setEnabled(move.isDoable());
	}
     }

	/**
        * Action de Selection Niveau
        */
	class NiveauAction extends AbstractAction {
	    // Construction
	    int random;
	    int NbColonne;
	    int NbLigne;
	    int undoMax;

	    /**
	    * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    * @param random nombre de deplacement aléatoire
	    * @param NbColonne nombre de colonne du taquin
	    * @param NbLigne nombre de ligne du taquin
	    * @param undoMax maximun d undo possible
	    */
            public NiveauAction(String text,ImageIcon icon,int random,int NbColonne,int NbLigne,int undoMax) {
		super(text,icon);
		//on initialise les parametres de l action
                this.random=random;
		this.NbColonne=NbColonne;
		this.NbLigne=NbLigne;
		this.undoMax=undoMax;
	    }
	    /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
            public void actionPerformed(ActionEvent e) {
            //on initialise les parametres courant avec les parametres de l action
            Parametres.random=this.random;
            Parametres.NbColonne=this.NbColonne;
            Parametres.NbLigne=this.NbLigne;
            Parametres.UndoMax=this.undoMax;
            //on relance l affichage de l'arene de jeu
            loadArena(false);
            pack();
            }
	 }

	/**
        *Action de Selection Niveau
        */
	class PersoAction extends AbstractAction {

	    /**
	    * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    * @param key key acceleratrice associé a l action
            */
            public PersoAction(String text,ImageIcon icon) {
		super(text,icon);
	    }

            /**
            * petite fonction de d initialisation des parametres
            */
            public void loadParam(){
            Parametres.random=Parametres.perso_random;
            Parametres.NbColonne=Parametres.perso_NbColonne;
            Parametres.NbLigne=Parametres.perso_NbLigne;
            Parametres.UndoMax=Parametres.perso_UndoMax;
            }

            /**
            * Réalisation de l'action
            * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {
            //on initialise les parametres personnel
            loadParam();
            //on relance l affichage de l'arene de jeu
            loadArena(false);
            pack();
            }

         }

	/**
        *Action de quitter
        */
	class ExitAction extends AbstractAction {
        boolean quit; // boolean indiquand si c une action de quitter ou de quitter sauvegarder

            /**
            * Construction
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    * @param key key acceleratrice associé a l action
            * @param quit boolean indiquand si c une action de quitter ou de quitter sauvegarder, true pour quitter sauvegarder
            */
            public ExitAction(String text,ImageIcon icon, int key,boolean quit) {
		super(text,icon);
		this.quit=quit;
		// Met la touche comme un accélérateur sans modificateur
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, 0));
	    }

            /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
            public void actionPerformed(ActionEvent e) {
            //cas si on quit et sauvegarde
            if(quit){
                //on sauvegarde les parametres
                Fonction.saveParametres(FParametres);
                //on sauvegarde la police
                Fonction.IniPolice(FPolice);
                //sauvegarder ces parametres dans leur fichiers respectifs
                Fonction.XmlEncodeur(plateau.getArea(),FPartie);
                Fonction.XmlEncodeur(TabScore,FScore);
		//on quit le programme
                System.exit(0);}
             //cas ou l on quit

             //Tableeau des reponses
             Object[] options = {"Oui",
                                 "Oui,\n" +"mais sauvegarder quand meme",
                                 "Non,\n je sais meme plus pourquoi j ai cliquer"};

             //lancement d'une fenetre à choix multiple
             int reponse = JOptionPane.showOptionDialog(null,
                    "Quelque chose de plus interressant a faire?",
                    "C'est votre dernier mot?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            //on gere la reponse
            if(reponse==1){Fonction.XmlEncodeur(plateau.getArea(),FPartie);}  //sauvegarde de la partie
            if(reponse==0 || reponse==1){
                //sauvegarde du score
                Fonction.XmlEncodeur(TabScore,FScore);
                //sauvegarde des parametres
		Fonction.saveParametres(FParametres);
		//sauvegarde des polices
                Fonction.IniPolice(FPolice);
                //on quit le programme
                System.exit(0);}
	    }

          }



        /**
        * Action de rejouer
        */
	class NewAction extends AbstractAction {
        boolean charger;

            /**
            * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    * @param key key acceleratrice associé a l action
            * @param charger boolean indiquand si c une action de nouvelle partie ou de chargement de partie, true pour quitter sauvegarder
            */
            public NewAction(String text,ImageIcon icon, int key,boolean charger) {
		super(text,icon);
		// Met la touche comme un accélérateur sans modificateur
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, 0));
                this.charger=charger;
	    }

            /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {
            if(perso.isSelected()){personnel.actionPerformed(null);}
            loadArena(charger);
            view.update();
            pack();
	    }

        }

        /** 
        * Action de lancer le menu des options 
        */
	class AvanceeAction extends AbstractAction {
	    /**
	    * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    * @param key key acceleratrice associé a l action
            */
	    public AvanceeAction(String text,ImageIcon icon, int key) {
		super(text,icon);
		// Met la touche comme un accélérateur sans modificateur
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, 0));
	    }
	    /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {
	    avancee.setVisible(true);
            update();
	    }

        }

        /**
        * Action de lancer la fenetre du score 
        */
	class ScoreAction extends AbstractAction {
	    /**
            * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    * @param key key acceleratrice associé a l action
	    */
            public ScoreAction(String text,ImageIcon icon, int key) {
		super(text,icon);
		// Met la touche comme un accélérateur sans modificateur
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, 0));
	    }
	    /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {
	    jscore=new JScore(TabScore);
            update();
	    }

        }


	/** 
        * Action d'annuler un déplacement 
        */
	class UndoAction extends AbstractAction {
	    /**
            * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    * @param key key acceleratrice associé a l action
	    */
	    public UndoAction(String text, ImageIcon icon, int key) {
		super(text, icon);
		// Met la touche comme un accélérateur sans modificateur
	    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, 0));
	    }
	    /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {
		NbClick--;
                // Action déléguée à la modification choisie par le gestionnaire
		// de modifications
		undoManager.undo();
	    // Mise à jour de la vue
		view.update();
	    }
	    /**
            * Mise à jour de l'activation
            */
	    void updateEnabled() {
		// L'activation est déterminée par le gestionnaire de modifications
		setEnabled(undoManager.canUndo());
	}
	}

	/** 
        * Action de refaire un déplacement annulé 
        */
	class RedoAction extends AbstractAction {
	    /**
            * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    * @param key key acceleratrice associé a l action
	    */
	    public RedoAction(String text, ImageIcon icon, int key) {
		super(text, icon);
		// Met la touche comme un accélérateur sans modificateur
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, 0));
	    }
	    /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {
	    NbClick++;
            // Action déléguée à la modification choisie par le gestionnaire
	    // de modifications

		undoManager.redo();
		// Mise à jour de la vue
		view.update();
	    }
	     /**
            * Mise à jour de l'activation
            */
            void updateEnabled() {
	    // L'activation est déterminée par le gestionnaire de modifications
	    setEnabled(undoManager.canRedo());
	}
	}
	

        /** 
        * Moification du deplacement, c est une undo action
        */
	class MoveEdit extends AbstractUndoableEdit {
	    Move move;		// Déplacement
	    /** 
            * Construction
            *@param move mouvement a enregistre
            */
	    MoveEdit(Move move) {
		this.move = move;
	    }
	    /**
            * Annuler la modification
            */
	    public void undo() {
		super.undo();	// Indispensable
		move.undo();
	    }
	    /**
            * Refaire la modification
	    */
            public void redo() {
		super.redo();	// Indispensable
		move.doit();
	    }
	}

	/** 
        * Class Déplacement
        */
	class Move {
	    int dx;			// Déplacement horizontal
	    int dy;			// Déplacement vertical
	    /**
            * Constructeur
            * @param dx deplacement horizontal
            * @param dy deplacement vertical
	    */
            Move(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	    }
	    /**
            * Teste si le déplacement est autorisé par le modèle
            */
            boolean isDoable() {
		return plateau.isMoveDoable(dx, dy);
	    }
	    /**
            * Faire le déplacement
	    */
            void doit() {
		plateau.move(dx, dy);
		echange(-dx,-dy);
	}
	    /**
            * Faire le déplacement inverse
	    */
            void undo() {
		plateau.move(-dx, -dy);
		//echange la valeur de deux cases
                echange(dx,dy);
	}


    }

        /**
        * Class piece gerant les cases du taquin
        */
    	class Piece extends JButton implements MouseListener{
	    int x;   //coordonnée horizontale
	    int y;   //coordonnée verticale
	    int dx;  //distance par rapport a la piece vide a l horizontal
	    int dy;  //distance par rapport a la piece vide a la vertical
	    Color couleur; //couleur de la piece


            /**
            * Contructeur
            * @param txt valeur de la piece
            * @param dx coordonnée horizontale
            * @param dy coordonnée verticale
            */
            Piece(String txt, int dx, int dy,Color couleur){
		//Mise en place du texte
                setText(txt);
                //Mise en place du fond
		setBackground(couleur);
                //coordonnée horizontale
		this.x=dx;
                //coordonnée verticale
		this.y=dy;
                //couleur de la piece
		this.couleur=couleur;
		//mise en place d un ecouteur dessus
		addMouseListener(this);
	    }


            /**
            * Mise en place de la couleur
            * @param couleur
            */
            void setCouleur(Color couleur){
		this.couleur=couleur;
		setBackground(couleur);}

	    /**
            * Recuperation de la couleur d une piece
            */
            Color getCouleur(){
		return this.couleur;}

            /**
            * Action de la souris qui clique sur la piece
            * @param e recupere l evenement
            */
	    public void mouseClicked(MouseEvent e) {
		//recuperation de la distance entre la piece vide et la piece
                dx=this.x-plateau.getPosX();
		dy=this.y-plateau.getPosY();
		//on teste si la piece ce situe a coter de la piece vide
                if((Math.abs(dx)+Math.abs(dy))==1){
		    //on bouge la piece en consequence
                    plateau.move(dx,dy);
		    //on interverti les propriétés de la piece vide avec la piece
                    echange(-dx,-dy);
                    //on enregistre le mouvement au pres du gestionnaire
		    undoManager.addEdit(new MoveEdit(new Move(dx,dy)));
		    //mise a jour de la vue
                    view.update();
		}
	    }

            /**
            * Action de la souris reste cliqué sur la piece
            * @param e recupere l evenement
            */
            public void mousePressed(MouseEvent e){
		dx=this.x-plateau.getPosX();
		dy=this.y-plateau.getPosY();
	 	NbClick++;
	    }

            /**
            * Action de la souris qui est relache
            * @param e recupere l evenement
            */
	    public void mouseReleased(MouseEvent e){
		//on teste si la piece ce situe a coter de la piece vide
                if((Math.abs(dx)+Math.abs(dy))==1){
		    //on bouge la piece en consequence
                    plateau.move(dx,dy);
                    //on interverti les propriétés de la piece vide avec la piece
		    echange(-dx,-dy);
		    //on enregistre le mouvement au pres du gestionnaire
		    undoManager.addEdit(new MoveEdit(new Move(dx,dy)));
		    //mise a jour de la vue
		    view.update();
		}
	    }

	    /**
            * Action de la souris qui sort de la piece
            * @param e recupere l evenement
            */
            public void mouseExited(MouseEvent e){
	        //on remet sa couleur initiale
        	setBackground(this.couleur);
	    }

            /**
            * Action de la souris entre dans la piece
            * @param e recupere l evenement
            */
	    public void mouseEntered(MouseEvent e){
		//on affiche la couleur de selection
                setBackground(Parametres.couleurSelection);
            }
	}
    }


/**
* class permettant la gestion de la fenetres des options avancees
*/
class JAvancee extends JFrame {
        //Mise en place d un system d'onglet
        JTabbedPane tabbedPane = new JTabbedPane();

        //on met creer tout les panel necessaires
        //panel lié a l option de complexité
        JPanel Prandom=new JPanel();
        //panel lié a l option du nombre de colonne
        JPanel Pcolonne=new JPanel();
        //panel lié a l option du nombre de ligne
        JPanel Pligne=new JPanel();
        //panel lié a l option sur le nombre d undo max
        JPanel Pundo=new JPanel();
        //panel lié a l option sur la visibilité des boutons
        JPanel Pboutons=new JPanel();
        //panel lié au couleur du model
        JPanel Pmodel=new JPanel();
        //panel lié au couleur du menu
        JPanel Pmenu=new JPanel();
        //panel contant les options de liés au taquin(colonne, ligne, undo, complexité)
        JPanel Ptext=new JPanel();

        //couleur de fond des options
        Color background=Color.white;

        //actions sur les choix de couleur
        Puit piecesAction=new Puit("Couleur des pieces",null,Parametres.couleurPiece);     //couleur des pieces
	Puit videAction=new Puit("Couleur de la piece vide",null,Parametres.couleurVide);  //couleur de la piece vide
	Puit menuAction=new Puit("Couleur du menu",null,Parametres.couleurMenu);          //couleur du menu
	Puit selectionAction=new Puit("Couleur de la selection",null,Parametres.couleurSelection);   //couleur de la selection
        Puit BackButtonAction=new Puit("Couleur des boutons",null,Parametres.couleurBackButton);     // couleur des boutons
        Puit FrontButtonAction=new Puit("Couleur du texte",null,Parametres.couleurFrontButton);      //couleur du texte des boutons


        //bouton associé au actions sur les choix de couleur
        JButton pieces = new JButton(piecesAction);    //bouton de la couleur des pieces
	JButton vide = new JButton(videAction);        //bouton de la couleur de la piece vide
	JButton menu = new JButton(menuAction);        //bouton de couleur du menu
	JButton selection = new JButton(selectionAction);     //bouton de couleur de la selection
	JButton couleurBackButton= new JButton(BackButtonAction);    //bouton de couleur des boutons
        JButton couleurFrontButton= new JButton(FrontButtonAction);   //bouton de  couleur du texte des boutons

        //permet de saisir les enregistrement des options numeric
        JTextField trandom = new JTextField(""+Parametres.perso_random);  //champ de la complexité
	JTextField tcolonne = new JTextField(""+Parametres.perso_NbColonne);  //champ du nombre de colonne
	JTextField tligne = new JTextField(""+Parametres.perso_NbLigne);      //champ du nombre de ligne
	JTextField tundo = new JTextField(""+Parametres.perso_UndoMax);       //champ du nombre d undo max


        //permet de selectionné des font par le nom, type et taille
        MaFontList Fmenu;    //selecteur de font du menu
        MaFontList Ftemps;   //selecteur de font du temps
        MaFontList FCase;    //selecteur de font des pieces
        MaFontList FButton;  //selecteur de font des boutons

        //bouton radio lié au choix de la visibilité des boutons
        JRadioButton vrai=new JRadioButton(new CheckAction("oui",null));  //choix bouton visible
	JRadioButton faux=new JRadioButton(new CheckAction("non",null));  //choix bouton invisible

        /**
        * Constructeur
        */
        public JAvancee(){
	//association d un titre au menu
        setTitle("Modification des parametres");
	//initialisation des panels d options lié au taquin
        iniPanel();
        //chargement des sous panels
        loadParametre();
        //chargement des couleurs des options de couleur
	loadCouleur();
	//chargement des polices
	loadPolice();
	//chargement du placement
	loadAvancee();
	//positionnement de la fenetre
        setLoc();
        //mise en place
        pack();
	}

        /**
        * Positionnement de la fenetre
        */
        void setLoc(){
        //on recupere la taille de l ecran
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        //on positionne le taquin au milieu
        setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
        }

        /**
        * Initialisation des panels d options lié au taquin
        */
	public void iniPanel(){
        //Titre de l'option
        JLabel random= new JLabel("Complexite :");
        //ajout du titre
        Prandom.add(random);
        //ajout de la zone de saisie
        Prandom.add(trandom);
	Prandom.setBackground(background);

        //Titre de l'option
        JLabel colonne= new JLabel("Nombre de Colonnes :");
        //ajout du titre
        Pcolonne.add(colonne);
        //ajout de la zone de saisie
        Pcolonne.add(tcolonne);
        Pcolonne.setBackground(background);

        //Titre de l'option
        JLabel ligne= new JLabel("Nombre de lignes :");
        //ajout du titre
        Pligne.add(ligne);
        //ajout de la zone de saisie
        Pligne.add(tligne);
        Pligne.setBackground(background);

        //Titre de l'option
        JLabel undo= new JLabel("Nombre de Undo :");
        //ajout du titre
        Pundo.add(undo);
        //ajout de la zone de saisie
        Pundo.add(tundo);
        Pundo.setBackground(background);

        //Mise en place d un groupe de bouton
        ButtonGroup rbg=new ButtonGroup();
        //Titre de l option
        JLabel boutons=new JLabel("Boutons visibles? :");
        //panel de placement des boutons
        JPanel bg=new JPanel();

        //mise en place des couleurs de fond
        vrai.setBackground(background);
        faux.setBackground(background);
        bg.setBackground(background);

        //ajout des boutons vrai faux au groupe
        rbg.add(vrai);
        rbg.add(faux);
        //ajout des boutons dans le panel
        bg.add(vrai);
        bg.add(faux);
        //initialisation des boutons radio
        vrai.setSelected(Parametres.visibleButton);
	faux.setSelected(!Parametres.visibleButton);
        //ajout du titre au panel final de choix de visibilité
        Pboutons.add(boutons);
        //ajout des boutons
        Pboutons.add(bg);
        //selection du fond
        Pboutons.setBackground(background);
        }

        /**
        * Mise en place des sous panel
        */
        public void loadParametre(){
        //choix du fond
        Ptext.setBackground(background);
        //ajout des options du taquin dans le panel requis
        Ptext.add(Pundo);
        Ptext.add(Prandom);
        Ptext.add(Pcolonne);
        Ptext.add(Pligne);

        //ajout des options de couleur des pieces dans le panel requis
        Pmodel.add(pieces);
        Pmodel.add(vide);
        Pmodel.add(selection);
        Pmodel.setBackground(background);

        //ajout des options de couleur de menu dans le panel requis
	Pmenu.add(menu);
	Pmenu.add(couleurBackButton);
	Pmenu.add(couleurFrontButton);
	Pmenu.setBackground(background);
        }


        /**
        * Creation de choix des differentes polices et de leur parametres
        */
        public void loadPolice(){
        Ftemps=new MaFontList("Police du temps: ",Parametres.fontTemps);  //choix de la police du temps
        FButton=new MaFontList("Police des boutons: ",Parametres.fontCase); //choix de la police des boutons
        FCase=new MaFontList("Police des cases: ",Parametres.fontButton);  //choix de la police des cases
        Fmenu=new MaFontList("Police du menu: ",Parametres.fontMenu);   //choix de la police du menu

        }

	/**
	* Initialisation des boutons concernant le choix des couleurs
	*/
        public void loadCouleur(){

        pieces.setBackground(Parametres.couleurPiece); //couleur du bouton "couleur de piece"
        vide.setBackground(Parametres.couleurVide);  //couleur du bouton  "couleur de la piece vide"
        menu.setBackground(Parametres.couleurMenu);  //couleur du bouton  "couleur de piece"
        selection.setBackground(Parametres.couleurSelection);   //couleur du bouton  "couleur de piece"
        couleurBackButton.setBackground(Parametres.couleurBackButton);  //couleur du bouton  "couleur de piece"
        couleurFrontButton.setBackground(Parametres.couleurFrontButton);  //couleur du bouton  "couleur de piece"

        }

        /**
        *  Mise en place des onglets
        */
        public void loadAvancee(){
        //creation de l action de quitter
        ExitAction exitAction=new ExitAction("Quit",null);
        //creation de l action de sauvegarde
        SaveAction sauvegarderAction=new SaveAction("Sauvegarder",null);


        //mise en place des boutons quit et save sur chaqu un des onglets
        JButton sauvegarder=new JButton(sauvegarderAction);
        JButton exit=new JButton(exitAction);
        JButton sauve=new JButton(sauvegarderAction);
        JButton quit=new JButton(exitAction);

        //onglet des options de base
        JPanel base=new JPanel();
        //onglet des options avancee
        JPanel avancee=new JPanel();

        //mise en place de la couleur
        tabbedPane.setBackground(background);
        sauvegarder.setBackground(background);
        exit.setBackground(background);
        sauve.setBackground(background);
        quit.setBackground(background);

        //Mise en place des gestionnaires de placement
        base.setLayout(new GridLayout(0,1));    //placement vertical
        avancee.setLayout(new GridLayout(0,1));  //placement vertical

        //mise en place des options du taquin dans le premier onglet
        base.add(Ptext);
        base.add(Pboutons);
        base.add(Pmodel);
	base.add(Pmenu);
        base.add(Pmodel);
	base.add(Pmenu);
	base.add(Pboutons);
	base.add(sauvegarder);
        base.add(exit);

        //mise en place des options de police dans le second onglet
        avancee.add(Fmenu);
        avancee.add(Ftemps);
        avancee.add(FButton);
        avancee.add(FCase);
        avancee.add(sauve);
        avancee.add(quit);

        //mise en place des onglets dans le tableau d onglet
        tabbedPane.add("base",base);
        tabbedPane.add("Police",avancee);

        //Mise en place du tableau d onglet dans la frame
        getContentPane().add(tabbedPane);
        pack();

        }


        /**
        * Rend la fenetre visible ou non a l utilisateur
        * @param bool boolean indiquant si oui ou non on affiche la fenetre
        */
	public void setVisible(boolean bool){
	//on rend la fenetre du taquin non accesssible ou non
        view.setEnabled(!bool);
        //on fait apparaitre ou non la fenetre des options
        super.setVisible(bool);
        }


	/**
	* Class gerant l action des options de couleur
	*/
        class Puit extends AbstractAction {
	    Color choix;  //couleur de l action en cours

           /**
            * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    * @param cc couleur associé
	    */
	    public Puit(String text,ImageIcon icon,Color cc) {
		super(text, icon);
		this.choix=cc;
	    }
	    /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {
		Color tmp;
		//on lance le selectionneur de couleur
                JColorChooser puit=new JColorChooser(choix);
		//on recupere la valeur
                tmp=puit.showDialog(getContentPane(),"Chaleur",choix);
	        if(tmp!=null){choix=tmp;}
            }

	    /**
	    * Retourne la couleur de l'action
	    */
            public Color getColor(){
                    return choix;
	    }


	}

	/**
	* Class permettant de sauvegarder les options
	*/
        class SaveAction extends AbstractAction {

	    /**
            * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    */
	    public SaveAction(String text,ImageIcon icon) {
		super(text, icon);
	    }

            /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {
            //on recupere les valeurs des champs en les tranformant en entier
            int random=Parametres.perso_random;       //valeur de recuperation du champ complexite
            int NbLigne=Parametres.perso_NbLigne;     //valeur de recuperation du champ nombre de ligne
            int NbColonne=Parametres.perso_NbColonne;  //valeur de recuperation du champ nombre de colonne
            int undo=Parametres.perso_UndoMax;    //valeur de recuperation du champ undo limite

               //on essaye de recuperer les valeurs
               try{
                random=Integer.parseInt(trandom.getText());
                NbLigne=Integer.parseInt(tligne.getText());
                NbColonne=Integer.parseInt(tcolonne.getText());
                undo=Integer.parseInt(tundo.getText());
                //on recupere le cas ou l on ne peut transformer les chaines de caracteres en entier
                }catch(Exception execption){
                                           	JOptionPane.showMessageDialog(
                                                       avancee,
                                                       "Valeurs numeriques requises",
                                                       "probleme options",
                                                       JOptionPane.ERROR_MESSAGE
                                                       );}

                //on enregistre les couleurs dans leur parametres associés
                Parametres.couleurPiece=piecesAction.getColor();
		Parametres.couleurVide=videAction.getColor();
		Parametres.couleurSelection=selectionAction.getColor();
		Parametres.couleurMenu=menuAction.getColor();
                Parametres.couleurBackButton=BackButtonAction.getColor();
                Parametres.couleurFrontButton=FrontButtonAction.getColor();
                //on les met a jour les couleurs
                loadCouleur();

                //on enregistre l information sur la visibilité des boutons
                Parametres.visibleButton=vrai.isSelected();
                Parametres.fontMenu=Fmenu.haveFont();
                Parametres.fontTemps=Ftemps.haveFont();
                Parametres.fontCase=FCase.haveFont();
                Parametres.fontButton=FButton.haveFont();

                //si les parametres corresponde aux contraintes d integrité on enregistre les parametres du taquin
              if(random>1){Parametres.perso_random=random;}
              if(NbLigne>1){Parametres.perso_NbLigne=NbLigne;}
	      if(NbColonne>1){Parametres.perso_NbColonne=NbColonne;}
	      if(undo>=0){Parametres.perso_UndoMax=undo;}

	    }

     	}

	/**
        * Action de quitter
        */
	class ExitAction extends AbstractAction {

	    /**
            * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    */
	    public ExitAction(String text,ImageIcon icon) {
		super(text,icon);
	    }
	    /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {
             setVisible(false);
             view.loadArena(plateau.getArea());
             remove(view.menuBar);
             view.loadDesignButton();
             view.loadMenu();
             horloge.iniPolice();
            }

        }

        /**
        * Action de quitter
        */
	class CheckAction extends AbstractAction {
	    /**
            * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    */
	    public CheckAction(String text,ImageIcon icon) {
		super(text,icon);
            }
	    /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {

            }

        }

}
        /**
        * Affichage du score
        */
	public class JScore extends JFrame {
        int nbjoueur; //nombre de joueur
        ExitAction exitAction;  //action de quitter


        /**
        * Constructeur
        * @param TabScore representation du tableau des scores
        */
        public JScore(String TabScore[][]){
            //on recupere la taille du tableau
            nbjoueur=TabScore.length;
            //on place le titre
	    super.setTitle("Score");
	    //on iniitialise l action de quitter
            exitAction=new ExitAction("Quit",null);
            //on met en place les boutons
            loadButton();
     	    //on met en place l affichage du score
            loadTable();
            //on place la fenetre
            setLoc();
            //mise en forme
            pack();
            //on rend la fenetre visible
       	    setVisible(true);
       	}

        /**
        * Rend la fenetre visible ou non a l utilisateur
        * @param bool boolean indiquant si oui ou non on affiche la fenetre
        */
         public void setVisible(boolean bool){
	 //on rend la fenetre du taquin non accesssible ou non
         view.setEnabled(!bool);
        //on fait apparaitre ou non la fenetre des options
	 super.setVisible(bool);
	}

        /**
        * Placement de la fenetre
        */
        void setLoc(){
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
        }

        /**
        * Mise en place des boutons
        */
        public void loadButton(){
        //mise en place du fond
        getContentPane().setBackground(Color.white);
        //mise en place d un gestionnaire de placement
        getContentPane().setLayout(new GridLayout(nbjoueur+2,3));
        //mise en place des "boutons", c plus un coté esthetique
        getContentPane().add(new JButton("Score"));
        getContentPane().add(new JButton("Nom joueur"));
	getContentPane().add(new JButton("temps"));
	}

       
        /**
        * Mise en place de la visualisation des scores
        */
        public void loadTable(){
        JLabel n1;
        JButton n2;


        for(int i=0;i<nbjoueur;i++){
            //initialisation d un label de  score
            n1=new JLabel(" " + TabScore[i][0]);
            //mise en place du bord
            n1.setBorder(BorderFactory.createLineBorder(Color.black));
            //ajout au conteneur
            getContentPane().add(n1);
            //initialisation d un label de nom de joueur
            n1=new JLabel(" " + TabScore[i][2]);
            //mise en place du bord
            n1.setBorder(BorderFactory.createLineBorder(Color.black));
            getContentPane().add(n1);
            //initialisation d un label de temps
            n1=new JLabel(" "+Fonction.Temps(TabScore[i][1]));
            //mise en place du bord
            n1.setBorder(BorderFactory.createLineBorder(Color.black));
            //ajout au conteneur
            getContentPane().add(n1);
        }
        //initialisation d un label vide
        n1=new JLabel("");
        //mise en place du bord
        n1.setBorder(BorderFactory.createLineBorder(Color.black));
        //ajout au conteneur
        getContentPane().add(n1);

        n2=new JButton(exitAction);
        //mise en place du bord
        n2.setBorder(BorderFactory.createLineBorder(Color.black));
        //ajout au conteneur
        getContentPane().add(n2);

        //initialisation d un label vide
        n1=new JLabel("");
        //mise en place du bord
        n1.setBorder(BorderFactory.createLineBorder(Color.black));
        //ajout au conteneur
        getContentPane().add(n1);
        }


         /**
         * Action de quitter
         */
	class ExitAction extends AbstractAction{

	    /**
            * Constructeur
	    * @param text teste associé a l action
	    * @param icon icone associé a l action
	    */
	    public ExitAction(String text,ImageIcon icon) {
		super(text,icon);
	    }

            /**
            * Réalisation de l'action
	    * @param e evenement enregistré lors de l action
            */
	    public void actionPerformed(ActionEvent e) {
            //on rend invisible la fenetre
            setVisible(false);
	    }

        }

  }

        /**
        * selectionneur de font
        */
        class MaFontList extends JPanel implements ActionListener{
        //boite de selection de la famille de la font
        JComboBox CfontMenuListe;
        //boite de selection de la taille de la font
        JComboBox TfontMenuListe;
        //boite de selection du type gras
        JCheckBox TygfontMenuListe;
        //boite de selection du type italique
        JCheckBox TyifontMenuListe;
        //boite d apercu de la font
        JLabel apercue;
        //label contenant le titre
        JLabel fontMenuListe;
        //couleur de fond
        Color background=Color.white;


        /**
        * Constructeur
        *@param nom titre du label
        *@param font font de base
        */
        MaFontList(String nom,Font font){
        //mise en place de la couleur de fond
        setBackground(background);

        //recuperation du nom de la font
        String name=font.getName();
        //recuperation de la taille de la font
        String taille=""+font.getSize();
        //recuperation du style de la font
        int type=font.getStyle();

        //creation d un tableau de taille possible
        String[] Taille=new String[36];
        for(int i=0;i<Taille.length;i++){
        Taille[i]=""+(i+6);}

        //mise en place et recuperation de la liste des fonts possibles
        CfontMenuListe=new JComboBox(ge.getAvailableFontFamilyNames());
        //mise en place du fond
        CfontMenuListe.setBackground(background);
        //on iniatialise le choix sur la font en cours
        CfontMenuListe.setSelectedItem(name);


        //mise en place et recuperation de la liste des tailles possibles
        TfontMenuListe=new JComboBox(Taille);
        //mise en place du fond
        TfontMenuListe.setBackground(background);
        //on peut rentrer une valeur sois meme
        TfontMenuListe.setEditable(true);
        //on iniatialise le choix sur la taille en cours
        TfontMenuListe.setSelectedItem(taille);

        //mise en place de la case a cocher gras
        TygfontMenuListe=new JCheckBox("Gras");
        //mise en place du fond
        TygfontMenuListe.setBackground(background);

        //mise en place de la case a cocher italic
        TyifontMenuListe=new JCheckBox("ITALIC");
        //mise en place du fond
        TyifontMenuListe.setBackground(background);

        //en fonction du type de la font charger on initialise les choix
        switch (type){
        case 0:
        TygfontMenuListe.setSelected(false);
        TyifontMenuListe.setSelected(false);
        break;

        case Font.BOLD:
        TygfontMenuListe.setSelected(true);
        TyifontMenuListe.setSelected(false);
        break;

        case Font.ITALIC:
        TygfontMenuListe.setSelected(false);
        TyifontMenuListe.setSelected(true);
        break;

        default:
        TygfontMenuListe.setSelected(true);
        TyifontMenuListe.setSelected(true);

        }


        //initialisation de l apercu
        apercue=new JLabel("abcABC 0123");
        //mise en place du fond
        apercue.setBackground(background);
        //mise en place de l appercu avec la font selectionnée
        apercue.setFont(haveFont());

        //initialisation du titre de la font
        fontMenuListe= new JLabel(nom);

        //on ajoute le titre
        this.add(fontMenuListe);
        //on ajoute la boite de la selection de font
        this.add(CfontMenuListe);
        //on ajoute la boite de la selection de taille
        this.add(TfontMenuListe);
        //on ajoute la boite de la selection du type
        this.add(TygfontMenuListe);
        this.add(TyifontMenuListe);
        //on ajoute l apercu
        this.add(apercue);
        //on initialise les ecouteurs dessus
        TfontMenuListe.addActionListener(this);
        TygfontMenuListe.addActionListener(this);
        TyifontMenuListe.addActionListener(this);
        CfontMenuListe.addActionListener(this);
        }


        /**
        * recuperation de la font choisis
        */
        public Font haveFont(){
        Font result; //font final lié a tout les autres parametres
        String nom=(String)CfontMenuListe.getSelectedItem();  //recuperation du nom de la font
        String Taille=(String)TfontMenuListe.getSelectedItem(); // recuperation de la taille
        int taille=Integer.parseInt(Taille);  //transformation de la valeur rentré
        int type=Font.PLAIN;  //initialisation du type de la font

        if(TygfontMenuListe.isSelected()){type+=Font.BOLD;}  //si gras est selectionner on l ajoute au type de la font
        if(TyifontMenuListe.isSelected()){type+=Font.ITALIC;} //si italic est selectionner on l ajoute au type de la font
        result=new Font(nom,type,taille);   //on recupere la font
        return result;    //on le retourne
        }

        /**
        * Réalisation de l'action
	* @param e evenement enregistré lors de l action
        */
        public void actionPerformed(ActionEvent e) {
        //quand un parametre est effectué on change l appercu de la font
        apercue.setFont(haveFont());
        //on met en forme
        avancee.pack();
        }

        }
}
