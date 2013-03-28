package core;

import com.ekino.animation.devoxx.RestPlayerAlgorithm;
import com.ekino.animation.devoxx.model.World;
import com.ekino.animation.devoxx.model.actions.*;
import com.ekino.animation.devoxx.model.army.Ship;
import com.ekino.animation.devoxx.model.base.Asteroid;
import com.ekino.animation.devoxx.model.base.DarkPlanet;
import com.ekino.animation.devoxx.model.base.Location;
import com.ekino.animation.devoxx.model.base.Square;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PlayerAlgorithmDefault implements RestPlayerAlgorithm {

	/**
	 * Compléter par votre pseudo
	 * 
	 * @return votre pseudo
	 */
	@Override
	public String ping() {
		return "NicoP";
	}

	/**
	 * {@inheritDoc RestPlayerAlgorithm}
	 */
	@Override
	public ActionList turn(World world) {
        int turnNumber = world.getTurnNumber();
        Location darkLocation = null;
        int[][] map = new int[world.getWidth()][world.getHeight()];
        Collection<Location> ennemies = Sets.newHashSet();
        for (Square square : world.getScanner()) {
            if (square instanceof Asteroid) {
                map[square.getLocation().getX()][square.getLocation().getY()] = 1;
            }
            if (square instanceof DarkPlanet) {
                darkLocation = square.getLocation();
            }
            if (square instanceof Ship) {
                ennemies.add(square.getLocation());
            }
        }
        if (darkLocation == null) {
            darkLocation = new Location(world.getWidth() / 2, world.getHeight() / 2);
        }
        map[darkLocation.getX()][darkLocation.getY()] = 2;
        Map<Location, Collection<Ship>> shipsByEnnemy = Maps.newHashMap();
        for (Location ennemy : ennemies) {
            Collection<Ship> shipsAround = foundShipsAround(world.getShips(), ennemy);
            if (!shipsAround.isEmpty()) {
                shipsByEnnemy.put(ennemy, shipsAround);
            }
        }
        List<Action> actions = Lists.newArrayList();
        Collection<Location> ennemiesTaken = Sets.newHashSet();
        Collection<Ship> alreadyAttacked = Sets.newHashSet();
        for (Map.Entry<Location, Collection<Ship>> entry : Maps.newHashMap(shipsByEnnemy).entrySet()) {

            int nb = entry.getValue().size();
            if (nb == 0) {
                shipsByEnnemy.remove(entry.getKey());
                continue;
            }
            if (nb == 1) {
                Ship ship = entry.getValue().iterator().next();
                if (alreadyAttacked.contains(ship)) {
                    continue;
                }
                ennemiesTaken.add(entry.getKey());
                actions.add(new AttackAction(ship, entry.getKey()));
                alreadyAttacked.add(ship);
                shipsByEnnemy.remove(entry.getKey());
                removeShipAttacking(shipsByEnnemy, entry, ship);
            }
        }

        for (Map.Entry<Location, Collection<Ship>> entry : Maps.newHashMap(shipsByEnnemy).entrySet()) {
            int nb = entry.getValue().size();
            if (nb == 0) {
                continue;
            }
            int nbAttack = 0;
            for (Ship ship : entry.getValue()) {
                if (alreadyAttacked.contains(ship)) {
                    continue;
                }
                actions.add(new AttackAction(ship, entry.getKey()));
                alreadyAttacked.add(ship);
                nbAttack++;
                if (nbAttack == 2) {
                    shipsByEnnemy.remove(entry.getKey());
                    ennemiesTaken.add(entry.getKey());
                    break;
                }
            }
        }
        /// moves
        for (Ship ship : world.getShips()) {
            List<Move> moves = findPath(ship, darkLocation, world, map);
            if (!moves.isEmpty()) {
                actions.add(new MoveAction(ship, moves)) ;
            }
        }

        return ActionList.valueOf(ImmutableList.copyOf(actions));
	}

    private List<Move> findPath(Ship ship, Location darkLocation, World world, int[][] map) {
         MySucessorComputer sc = new MySucessorComputer(world.getWidth(), world.getHeight());
        MyNodeFactory fac = new MyNodeFactory(map);
        Astar<Location> astart = new Astar<Location>(sc, fac);
        List<Location> compute = astart.compute(ship.getLocation(), darkLocation);
        Location start = ship.getLocation();
        int i = 0;
        List<Move> moves = Lists.newArrayList();
        for (Location location : compute) {
            if (location.getX() == start.getX()) {
                if (location.getY() > start.getY()) {
                    moves.add(new Move(Direction.SOUTH, 1));
                } else {
                    moves.add(new Move(Direction.NORTH, 1));
                }
            } else {
                if (location.getX() > start.getX()) {
                    moves.add(new Move(Direction.EAST, 1));
                } else {
                    moves.add(new Move(Direction.WEST, 1));
                }
            }
            i++;
            if (i >= 5) {
                break;
            }
        }
        return moves;
    }

    private void removeShipAttacking(Map<Location, Collection<Ship>> shipsByEnnemy,
                                     Map.Entry<Location, Collection<Ship>> entry, Ship ship) {
        for (Map.Entry<Location, Collection<Ship>> anotherEntry : Maps.newHashMap(shipsByEnnemy).entrySet()) {
            if (anotherEntry.getValue().contains(ship)) {
                shipsByEnnemy.get(anotherEntry.getKey()).remove(ship);
            }
        }
    }

    private Collection<Ship> foundShipsAround(Collection<Ship> ships, Location ennemy) {
        Collection<Ship> near = Sets.newHashSet();
        for (Ship ship : ships) {
            if (isNearAttack(ship.getLocation(), ennemy)) {
                near.add(ship);
            }
        }
        return near;
    }

    private boolean isNearAttack(Location ship, Location ennemy) {
        return Math.abs(ship.getX() - ennemy.getX()) <= 4 &&
                Math.abs(ship.getY() - ennemy.getY()) <= 4;
    }

    private static class MySucessorComputer implements SuccessorComputer<Location> {
        private final int width;
        private final int height;

        public MySucessorComputer(int width, int height) {
            this.width = width;
            this.height = height;
        }


        @Override
        public Collection<Location> computeSuccessor(Node<Location> node) {
            Location index = node.getIndex();
            int x = index.getX();
            int y = index.getY();
            final List<Location> resultat = new ArrayList<Location>();
            if (x > 0) {
                resultat.add(new Location(x - 1, y));
            }
            if (x < width ) {
                resultat.add(new Location(x + 1, y));
            }

            if (y > 0) {
                resultat.add(new Location(x, y - 1));
            }
            if (y < height ) {
                resultat.add(new Location(x, y + 1));
            }
            if(node.getParent() != null) {
                resultat.remove(node.getParent().getIndex());
            }
            return resultat;
        }
    }

    private static class MyNodeFactory extends NodeFactory<Location> {
        private final int[][] matrix;

        public MyNodeFactory(int[][] matrix) {
            this.matrix = matrix;
        }

        @Override
        protected double computeReel(Location parentIndex, Location index) {
            if(parentIndex != null && parentIndex.equals(index)) {
                return 0;
            }

            if(0 == matrix[(int) index.getY()][(int) index.getX()]) {
                return 1;
            }
            return Double.MAX_VALUE;
        }

        @Override
        protected double computeTheorique(Location index, Location goal) {
            return Math.abs(index.getX()-goal.getX()) + Math.abs(index.getY()-goal.getY());
        }
    }
}
