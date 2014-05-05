package algorithm;

import edu.uci.ics.jung.graph.SparseMultigraph;
import graph.GraphElements;
import org.jfree.data.xy.XYSeries;

import java.util.*;

/**
 * Implementation of genetic algorithm based on ny view on the matter
 * Created by Marek on 2014-04-18.
 */
public class FirstVer implements Algorithm {

    private SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g;
    Collection<GraphElements.MyVertex> vertices;
    private final Random randomize = new Random();

    private int starterPopulation;
    private int numberOfIterations;
    private int minimalPopulation;
    private int maximumPopulation;

    private static boolean debugMode = true;

    private XYSeries[] series;

    /**
     * Constructor witch is used only tu set necessary parameters
     * @param g graph of connections between "cities"
     */
    public FirstVer(SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g) {
        this(g,10,1000,2,200, null);
    }

    /**
     *
     * @param g graph of connections between "cities"
     * @param starterPopulation number of specimen in population by which algorithm will start
     * @param numberOfIterations number of iterations of algorithm
     * @param minimalPopulation minimal number of specimen in population in one generation
     * @param maximumPopulation maximal number of specimen in population in one generation
     */
    public FirstVer(SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g,
                    int starterPopulation, int numberOfIterations, int minimalPopulation, int maximumPopulation,
                    XYSeries[] series) {
        this.g = g;
        vertices = g.getVertices();
        this.starterPopulation = starterPopulation;
        this.numberOfIterations = numberOfIterations;
        this.minimalPopulation = minimalPopulation;
        this.maximumPopulation = maximumPopulation;
        this.series = series;
    }

    /**
     * Main function witch is used to find cycle in graph
     * @return best found trace; in the best case it will be a Hamilton-cycle
     */
    @Override
    public LinkedList<GraphElements.MyVertex> getCycle() {

        HashSet<Unit> population = new HashSet<Unit>();

        for (int i = 0; i < starterPopulation; i++) population.add(new Unit());

        for (int i = 0; i < numberOfIterations; i++) {
            int matches = 0, deaths = 0, births = 0; //debug variables
            for (Unit unit : population) {
                if (unit.longestPath.size() == g.getVertexCount() + 1) {

                    HashMap<ArrayList<City>, Integer> species = new HashMap<ArrayList<City>, Integer>();
                    for (Unit u : population) {
                        Integer a = species.get(u.genome);
                        species.put(u.genome, a == null ? 1 : a + 1);
                    }
//        spices output
                    for (Map.Entry<ArrayList<City>, Integer> entry : species.entrySet()) {
                        System.out.println("entry = " + entry);
                    }

                    return unit.longestVertexList(); // I'm perfect
                }

                for (Unit partner : population) {
                    if (unit == partner) continue;  //don't try to inject into itself
                    if (unit.match(partner)) {
                        if (debugMode) ++matches;
                        break; //inject my genome if I likes him
                    }
                }
            }

            HashSet<Unit> reducedPopulation = new HashSet<Unit>(population);

            for (Unit unit : population) {
                // Warn: population size could change in earlier loop, so calculation must occur on each loop,
                // or modify it accordingly
                double environmentUtilisation = (double)(reducedPopulation.size() - minimalPopulation)/maximumPopulation;
                double str = (0.95 + randomize.nextDouble()/10)* unit.strength(), //strength with 10% random variation
                        //required strength varied with environment utilisation
                        objective = (environmentUtilisationCurve(environmentUtilisation)*vertices.size());
//                System.out.println("unit.strength = " + unit.strength() +
//                        " EUC = " + environmentUtilisationCurve(environmentUtilisation) +
//                        " str = " + str +
//                        " obj = " + objective);
                if (str < objective)
                //required strength varied with environment utilisation
                {
                    if (debugMode) ++deaths;
                    reducedPopulation.remove(unit);
                }
            }
            population = reducedPopulation;

            HashSet<Unit> extendedPopulation = new HashSet<Unit>();
            for (Unit unit : population) {
                Unit baby = unit.deliver();
                if (baby != null) {
                    if (debugMode) ++births;
                    extendedPopulation.add(baby);
                }
            }
            population.addAll(extendedPopulation);

            int meanAge = 0;
            for (Unit unit : population) {
                meanAge += unit.age;
            }

            // debug output
            if (debugMode) {
                System.out.println("population = " + population.size() +
                        " matches = " + matches +
                        " deaths = " + deaths +
                        " births = " + births +
                        " mean age = " + meanAge/population.size());
                if (series != null) {
                    series[0].add(i, population.size());
                    series[1].add(i,deaths);
                }
            }
        }

        HashMap<ArrayList<City>, Integer> species = new HashMap<ArrayList<City>, Integer>();
        for (Unit u : population) {
            Integer a = species.get(u.genome);
            species.put(u.genome, a == null ? 1 : a + 1);
        }
//        spices output
        for (Map.Entry<ArrayList<City>, Integer> entry : species.entrySet()) {
            System.out.println("entry = " + entry);
        }

        LinkedList<GraphElements.MyVertex> longest = new LinkedList<GraphElements.MyVertex>();
        for (Unit unit : population) {
            if (unit.longestPath.size() > longest.size()) longest = unit.longestVertexList();
        }
        return longest;
    }

    /**
     * function witch modifies one distribution of environmentUtilisation to another
     * @param environmentUtilisation percentage utilisation of environment
     * @return productivity of environment
     */
    private double environmentUtilisationCurve(double environmentUtilisation) {
        return ((Math.atan(10 * environmentUtilisation - 5) * 1.2) - 0.1);
    }

    /**
     * Class witch represents an unit of population
     */
    private class Unit {
        public final LinkedList<City> longestPath;
        private final ArrayList<City> genome;
        private ArrayList<City> newGenome = null;
        private int age;

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
         * @return result of mating
         */
        public boolean match(Unit partner) {
            HashSet<City> tmp = new HashSet<City>(partner.longestPath),
                    meDiffPartner = new HashSet<City>(longestPath),
                    union = new HashSet<City>(longestPath);
            meDiffPartner.removeAll(tmp);
            union.addAll(tmp);

            if (union.size() == 0) return false; // no neighbours

            // strength of like is seen as intersection/union of genomes
            return randomize.nextInt(100) <
                            (100*(longestPath.size() - meDiffPartner.size())) / union.size() && partner.inject(genome);
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
         * Shows how strength unit is
         * @return numerical representation of units strength
         */
        public double strength() {
            return longestPath.size()*survivorCurve(++age);
        }

        /**
         * Modifies linear distribution of age to some other distribution closer to real experience
         * @param age age of unit
         * @return probability of unit demise
         */
        private double survivorCurve(double age) {
            double y = (age - 50)*4/50, x = age + 2;
            return (1 - Math.atan(y)/2)*(2 + (-Math.pow(2,(1/x))));
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
            final double mutationLevel = 0.01;
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

    public void setDebugModeOn() {
        debugMode = true;
    }

    public void setDebugModeOff() {
        debugMode = false;
    }
}
