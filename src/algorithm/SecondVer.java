package algorithm;

import com.sun.istack.internal.NotNull;
import edu.uci.ics.jung.graph.SparseMultigraph;
import graph.GraphElements;

import java.util.*;

/**
 * Implementation of genetic algorithm based on Myceks' view on the matter
 * Created by Marek on 2014-04-18.
 */
public class SecondVer implements Algorithm {
    private SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g;
    Collection<GraphElements.MyVertex> vertices;
    private final Random randomize = new Random();

    private int starterPopulation;
    private int numberOfIterations;

    /**
     *
     * @param g graph of connections between "cities"
     * @param populationSize number of population size
     * @param numberOfIterations number of iterations of algorithm
     */
    public SecondVer(SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g,
                     int populationSize, int numberOfIterations) {
        this.g = g;
        vertices = g.getVertices();
        this.starterPopulation = populationSize;
        this.numberOfIterations = numberOfIterations;
    }

    /**
     * Main function witch is used to find cycle in graph
     * @return best found trace; in the best case it will be a Hamilton-cycle
     */
    @Override
    public LinkedList<GraphElements.MyVertex> getCycle() {

        ArrayList<Unit> population = new ArrayList<Unit>();

        for (int i = 0; i < starterPopulation; i++) population.add(new Unit());

        int iteration = 0;
        while (true){
            ++iteration;

            // Deaths
            Collections.sort(population);
            if (population.get(0).longestPath.size() == g.getVertexCount() + 1       // Ideal unit found
                    || iteration == numberOfIterations)                         // Iteration limit reached
                break;
            for (int j = starterPopulation - 1; j >= starterPopulation/2; j--) population.remove(j);

            // Matching
            for (Unit unit : population) unit.match(population.get(randomize.nextInt(starterPopulation / 2)));

            Unit baby;
            // Births
            for (int j = 0; j < starterPopulation / 2; j++) {
                if ((baby = population.get(j).deliver())==null){
                    continue;
                }
                population.add(baby);
            }
        }
        System.out.println("iteration = " + iteration);
        return population.get(0).longestVertexList();
    }

    /**
     * Class witch represents an unit of population
     */
    private class Unit implements Comparable {
        public final LinkedList<City> longestPath;
        private final ArrayList<City> genome;
        private ArrayList<City> newGenome = null;

        /**
         * Constructor used to create preset unit
         * @param genome product of crossing two other genomes or any other preset genome
         */
        private Unit(ArrayList<City> genome) {
            this.genome = genome;
            longestPath = calculateMaxTrace();
        }

        /**
         * Constructor used to create a random unit
         */
        Unit() {
            genome = new ArrayList<City>(g.getVertexCount());

            //Creating vertexes
            for (GraphElements.MyVertex vertex : vertices) {
                genome.add(new City(vertex));
            }

            longestPath = calculateMaxTrace();
        }

        /**
         * Method used by constructors to find out longest path in graph witch is strongest trait of unit
         * @return longest path in analysed graph
         */
        private LinkedList<City> calculateMaxTrace() {
            HashMap<City, LinkedList<City>> traces = new HashMap<City, LinkedList<City>>(genome.size());

            //Each city is end point of one path:
            for (City city : genome) {
                LinkedList<City> tmp = new LinkedList<City>();
                tmp.add(city);
                traces.put(city, tmp);
            }

            LinkedList<City> longestTrace = new LinkedList<City>();
            for (City city : genome) {
                if (traces.get(city).size() > 1) continue;      //City already set in earlier steps

                LinkedList<City> nextTrace = null, myTrace;
                do {
                    City next = findCity(city.nextLocation);    // null should never appear;
                    if (next == null) break;                    // no neighbours, no trace

                    myTrace = traces.get(city);
                    //Break if there is cycle, but not end->start
                    if (myTrace.contains(next) && myTrace.peekLast() != next) break;

                    nextTrace = traces.get(next);

                    if (nextTrace.size() == 1                       // next trace not set yet
                            || nextTrace.size() < myTrace.size()    // my trace is longer than others strike nodes
                            || longestTrace.peekFirst() == next     // just stick to the path
                            || (nextTrace.size() > 1 && nextTrace.get(1) == city)) {     // I'm editing my trace
                        nextTrace = new LinkedList<City> (myTrace);
                        nextTrace.addFirst(next);
                        traces.put(next,nextTrace);
                        city = next;                            //in-depth search
                    } else break;                               //next point have better trace
                } while (true);
                if (nextTrace != null && nextTrace.size() > longestTrace.size()) longestTrace = nextTrace;
            }
            return longestTrace;
        }

        /**
         * Transforms an location represented as MyVertex into City representation
         * @param location position in graph
         * @return City representation in graph; null if null location or no location found on graph
         */
        private City findCity(GraphElements.MyVertex location) {
            if (location == null) return null;

            for (City city : genome) {
                if (city.location.equals(location)) return city;
            }
            return null;
        }

        /**
         * Checks if unit likes a partner and do his job if so.
         * The more similar units are, the more probability of liking is.
         * Of course, there is lots of absolute hazard
         * @param partner another unit object
         */
        public void match(Unit partner) {
            inject(partner.genome);
        }

        /**
         * Method used to set new genome from partner
         * @param genome genome of partner
         * @return true if no genome injected before
         */
        boolean inject(ArrayList<City> genome) {
            if (newGenome == null) {
                newGenome = genome;
                return true;
            }
            return false;
        }

        /**
         * Mixes up genomes end spawn new unit
         * @return result of merging two units
         */
        public Unit deliver() {
            if (newGenome == null) return null;

            // crossing
            int crossingPoint = randomize.nextInt(genome.size());
            ArrayList<City> babyGenome = new ArrayList<City>();
            babyGenome.addAll(genome.subList(0,crossingPoint));
            babyGenome.addAll(newGenome.subList(crossingPoint, newGenome.size()));

            // mutation
            final double mutationLevel = 0.1;
            for (int i = 0; i < babyGenome.size(); i++) {
                if (mutationLevel < randomize.nextDouble()) continue;

                City toChange = babyGenome.get(i);
                babyGenome.set(i, new City(toChange.location, toChange.randomNeighbour()));
            }

            newGenome = null;
            return new Unit(babyGenome);
        }

        /**
         * Transforms an City list to MyVertex list
         * @return MyVertex list representation of City list
         */
        public LinkedList<GraphElements.MyVertex> longestVertexList() {
            LinkedList<GraphElements.MyVertex> result = new LinkedList<GraphElements.MyVertex>();

            for (City city : longestPath) result.add(city.location);

            return result;
        }

        @Override
        public int compareTo(@NotNull Object o) {
            Unit unit = (Unit) o;

            if (unit == null) return 0;
            
            return unit.longestPath.size() - longestPath.size();
        }
    }

    /**
     * Class that represents en vertex in graph
     */
    private class City {
        private GraphElements.MyVertex location, nextLocation;

        /**
         * Constructor used to create preset Cities
         * @param location location of the city
         * @param nextLocation sign to the next town
         */
        private City(GraphElements.MyVertex location, GraphElements.MyVertex nextLocation) {
            this.location = location;
            this.nextLocation = nextLocation;
        }

        /**
         * Constructor of city with random next town
         * @param location position of city in graph
         */
        public City(GraphElements.MyVertex location) {
            this.location = location;
            nextLocation = randomNeighbour();
        }

        /**
         * Search for random neighbour.
         * @return random neighbour or null if no neighbours
         */
        private GraphElements.MyVertex randomNeighbour() {
            int count = g.getNeighborCount(location);
            if (count == 0) return null;
            int next = randomize.nextInt(count);
            int i = 0;
            for (GraphElements.MyVertex neighbor : g.getNeighbors(location))
                if (i++ == next) return neighbor;
            return null;
        }

        /**
         * The way of method operation is too obvious to write a single word about it.
         * @return Easier to understand than super.toString() string representation
         */
        @Override
        public String toString() {
            return location.getName() + "->" + nextLocation.getName();
        }
    }

}
