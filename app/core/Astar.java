package core;


import java.util.ArrayList;
import java.util.Collection;  
import java.util.HashMap;  
import java.util.LinkedList;  
import java.util.List;  
import java.util.Map;  
import java.util.PriorityQueue;  
import java.util.Queue;  
  
/** 

 */  
public class Astar<T> {  
  
    private final SuccessorComputer<T> successorComputer;  
    private final NodeFactory<T> nodeFactory;
  
    private final Queue<Node<T>> paths = new PriorityQueue<Node<T>>();  
    private final Map<T, Double> distancesMinimum = new HashMap<T, Double>();  
  
    /** 
     * Instancie un nouveau calculateur de chemin par algorithme A* 
     *
     * @param successorComputer
     *            le fournisseur de l'algorithme calculant les successeurs 
     * @param nodeFactory 
     *            le créateur du noeud 
     */  
    public Astar(final SuccessorComputer<T> successorComputer, final NodeFactory<T> nodeFactory) {  
        if (nodeFactory == null || successorComputer == null) {  
            throw new IllegalArgumentException("Les 2 paramètres de A* doivent etre non null");  
        }  
        this.successorComputer = successorComputer;  
        this.nodeFactory = nodeFactory;  
    }  
  
    /** 
     * Rempli les paths possibles autour du noeud spécifié 
     * 
     * @param node 
     *            le noeud autour duquel on teste les voies 
     * @param goal 
     *            la destination finale 
     */  
    private void expand(final Node<T> node, final T goal) {  
        final T p = node.getIndex();  
        final Double minimum = distancesMinimum.get(p);  
  
        final double nodeF = node.getF();  
        if (minimum == null || minimum > nodeF) {  
            distancesMinimum.put(p, nodeF);  
  
            final Collection<T> successorsCollection = successorComputer.computeSuccessor(node);  
            for (final T t : successorsCollection) {  
                final Node<T> newNode = nodeFactory.instanciateNode(node, t, goal);  
                if (newNode != null) {  
                    paths.add(newNode);  
                }  
            }  
  
        }  
    }  
  
    /** 
     * Renvoit la liste des noeuds représentant le parcours optimisé par A* 
     * 
     * @param start 
     *            le point de départ 
     * @param goal 
     *            le point d'arrivée espéré 
     * @return la liste des index dans l'ordre représentant le trajet entre les 
     *         2 points 
     */  
    public List<T> compute(final T start, final T goal) {  
        final Node<T> root = nodeFactory.instanciateNode(null, start, goal);  
        expand(root, goal);  
        while (true) {  
            final Node<T> p = paths.poll();  
            if (p == null) {  
                return new ArrayList<T>();  
            }  
  
            final T last = p.getIndex();  
  
            if (last.equals(goal)) {  
                final LinkedList<T> result = new LinkedList<T>();  
                for (Node<T> i = p; i != null; i = i.getParent()) {  
                    result.addFirst(i.getIndex());  
                }  
                return result;  
            }  
            expand(p, goal);  
        }  
    }  
}  