package core;

import java.io.Serializable;
  
/** 
 * Représente un "noeud" du parcours de l'algotithme A*. Le noeud se caractèrise 
 * par : g , sa distance au noeud de départ h , sa distance au noeau d'arrivée f 
 * , la somme de g+h le noeud parent du noeud courant un index unique, par 
 * exemple la position sur une carte 
 * 
 * @author Moloch 
 * @since 1.0 
 * @version 1.0 
 * @param <T> 
 *            l'index du noeud, par exemple une position 
 */  
public class Node<T> implements Comparable<Node<T>>, Serializable {  
  
    /** 
     * UID 
     */  
    private static final long serialVersionUID = 464891324986135L;  
  
    private final double g;  
    private final double f;  
    private final Node<T> parent;  
    private final T index;  
  
    /** 
     * Construit un nouveau noeud 
     * 
     * @param parent 
     *            le noeud parent de celui-ci (peut etre null) 
     * @param index 
     *            l'index du noeud courant 
     * @param g 
     *            le cout du trajet entre l'origine et ce noeud 
     * @param f 
     *            le cout total du trajet jusqu'a à la destination (cout reel 
     *            origine-noeud + cout theorique noeud-destination) 
     */  
    public Node(final Node<T> parent, final T index, final double g, final double f) {  
        this.parent = parent;  
        this.index = index;  
        this.g = g;  
        this.f = f;  
    }  
  
    /** 
     * Représente le cout réel entre ce noeud et le point de départ Le cout peut 
     * recouvrir la notion de distance, mais aussi de difficulté d'accès 
     * 
     * @return le cout pour arrivée jusqu'au noeud courant 
     */  
    public double getG() {  
        return g;  
    }  
  
    /** 
     * Représente le cout total du trajet en passant par ce noeud. Ce cout total 
     * est la somme de g + h C'est a dire que le cout total est en fait la somme 
     * Du cout réel entre le point de départ et le point courant (gestion de la 
     * difficulté) et du cout théorique pour arriver jusqu'au point d'arrivé 
     * (distance) 
     * 
     * @return le cout total du trajet 
     */  
    public double getF() {  
        return f;  
    }  
  
    /** 
     * Renvoit le noeud parent de ce noeud, permettant ainsi de remonter 
     * jusqu'au point de départ. Si le noeud courant est le noeud de départ, 
     * doit renvoyer null. 
     * 
     * @return le noeud parent 
     */  
    public Node<T> getParent() {  
        return parent;  
    }  
  
    /** 
     * Renvoit l'index du noeud courant dans l'ensemble des noeuds 
     * 
     * @return l'index du noeud courant 
     */  
    public T getIndex() {  
        return index;  
    }  
  
    /** 
     * La comparaison entre 2 noeuds doit se faire entre valeur de F 
     * 
     * @param node 
     *            le noeud avec lequelle on compare 
     * @return this.f - node.f 
     */  
    @Override  
    public int compareTo(final Node<T> node) {  
        if (node == this) {  
            return 0;  
        }  
        final int result = (int) (this.getF() - node.getF());  
        if (result == 0) {  
            return -1;  
        }  
        return result;  
    }  
  
    @SuppressWarnings("unchecked")  
    @Override  
    public boolean equals(final Object obj) {  
        if (!(obj instanceof Node<?>)) {  
            return false;  
        }  
        final Node<T> other = (Node<T>) obj;  
        if (other.getF() == getF() && other.getG() == getG() && (parent == null || parent.equals(other.getParent()))) {  
            return true;  
        }  
        return false;  
    }  
  
    @Override  
    public int hashCode() {  
        return (int) g + (int) f + ((parent != null) ? parent.hashCode() : 0);  
    }  
} 