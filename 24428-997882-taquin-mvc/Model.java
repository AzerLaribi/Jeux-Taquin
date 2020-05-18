/**
 * Modèle du labyrinthe
 * @author Capellier Sebastien
 * @version 1.0
 */

import java.util.*;

/** 
* class gerant le model du programme
*/
class Model {
public final int Vide=-1;

    // Le labyrinthe est consitué d'une grille de taille nx x ny
    // Les positions possibles dans la grille sont les couples (x,y)
    // avec x entre 0 et nx-1 et y entre 0 et ny-1 (inclus).
    int nx;			// Nombre de lignes de la grille
    int ny;			// Nombre de colonnes de la grille
    int posx;			// Position en abscisse du mobile
    int posy;			// Position en ordonnée du mobile
    int area[][];               // tableau de jeu

   /** 
   * Constructeur par défaut
   * @param extense Désigne une extension possible
   */
    Model() {
	this(5, 5);
    }

    /** 
   * Constructeur par défaut
   * @param nx Désigne le nombre de lignes
   * @param ny de Designe le nombre decolonnes
   */
    Model(int nx, int ny) {
	this.nx = nx > 0 ? nx : 2;
	this.ny = ny > 0 ? ny : 2;
    // Position de départ en bas à droite
	this.posx = nx-1;
 	this.posy = ny-1;
   // initialisation du tableau representant la grille du taquin
        this.area=new int[this.nx][this.ny];
        this.iniArea();
        area[this.posx][this.posy]=Vide;
    }

    Model(int [][]arene){
    this.nx=arene.length;
    this.ny=(arene[0]).length;
    // On cherche la Position de départ
    for(int i=0;i<this.nx;i++){
        for(int j=0;j<this.ny;j++){
    if(arene[i][j]==Vide){// Position de départ
                          this.posx = i;
                          this.posy = j;
                          break;}
        }
    }
    this.area=arene;
    }
    /** 
    * Initialisation de l arene de jeu
    */
    void iniArea(){
    for(int i=0;i<this.nx;i++){
        for(int j=0;j<this.ny;j++){
        area[i][j]=1+j+i*ny;
                }
        }
    }

    /** 
    * Melange de l arene de jeu
    */
    void RandomArea(int Complexite){
    Random rand=new Random();

    while(Victoire()){
    for(int i=0;i<Complexite;i++){
       switch(rand.nextInt(4)){
       case 0: this.move(0,1);break;
       case 1: this.move(0,-1);break;
       case 2: this.move(1,0);break;
       case 3: this.move(-1,0);break;}

    }
   }
    }

    /** 
    * Retourne le nombre de colonne du tableau
    */
    int getNbColonne() {
	return ny;
    }
    /** 
    * Retourne le nombre de ligne du tableau
    */
    int getNbLigne() {
	return nx;
    }

    /** 
    * Acceseur la position en abscisse de l emplacement vide
    *@return la position en abscisse de l emplacement vide
    */
    int getPosX() {
	return  posx;
    }
    /** 
    * Acceseur la position en ordonnée de l emplacement vide
    * @return la position en ordonnée de l emplacement vide
    */
    int getPosY() {
	return posy;
    }

    /** 
    * Retourne la valeur de l emplacement dans le tableau
    * @param x valeur en abscisse dans le tableau
    * @param y valeur en ordonnée dans le tableau
    * @return la valeur entier de la case
    */
    int getCase(int x,int y){
    	return area[x][y];}
    	
    /**
    * Retourne la valeur de l emplacement dans le tableau
    * @return le tableau d entier representant l arene
    */
    int [][] getArea(){
    return area;}




    /**
    * Teste si un déplacement du mobile est possible
    * @param dx valeur du deplacement en abscisse
    * @param dy valeur du deplacement en ordonnée
    * @return si le deplacement est posible
    */
    boolean isMoveDoable(int dx, int dy) {
	return (0 <= posx + dx) && (posx + dx < nx) &&
	       (0 <= posy + dy) && (posy + dy < ny);
    }
    /** 
    * Effectue un déplacement du mobile
    * @param dx valeur du deplacement en abscisse
    * @param dy valeur du deplacement en ordonnée
    */
    void move(int dx, int dy) {
    int posxundo=this.posx;
    int posyundo=this.posy;

        if (isMoveDoable(dx, dy)) {
            this.posx += dx;
	    this.posy += dy;
            area[posxundo][posyundo]=area[this.posx][this.posy];
            area[this.posx][this.posy]=Vide;
        }
    }

    /**
    * Fonction indiquant si les conditions de victoire sont remplies
    * @return si la victoire est possible
    */
     boolean Victoire(){
     if(this.posx!=(nx-1) || this.posy!=(ny-1)){return false;}
     int i,j;
     for(i=0;i<nx-1;i++){
     	for(j=0;j<ny;j++){
     	if(area[i][j]!=1+(j+i*ny)){return false;}
     	}}
     	for(j=0;j<(ny-1);j++){
     	if(area[i][j]!=1+(j+i*ny)){return false;}
	}
	return true;}

}
