package graph;

import edu.uci.ics.jung.graph.SparseMultigraph;

import java.util.LinkedList;


/**
 * @author Jakub Fortunka
 *
 */
public class Algorithms {

    int count=0,no=1;
    SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g;
    int N;
    boolean[] visitedVertex;
    LinkedList<GraphElements.MyVertex> path = new LinkedList<GraphElements.MyVertex>();
    GraphElements.MyVertex start;

    public Algorithms(SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g, GraphElements.MyVertex starter) {
        N = g.getVertexCount();
        visitedVertex = new boolean[N];
        for (int i=0;i<N;i++) visitedVertex[i]=false;
        this.g = g;
        start = starter;

    }

    public LinkedList<GraphElements.MyVertex> getCycle() {
        if (searchForCycle(start)) return path;
        else return null;
    }

    private boolean searchForCycle(GraphElements.MyVertex v) {
        visitedVertex[v.getNumber()] = true;
        count++;
        for (GraphElements.MyVertex nextVertex : g.getSuccessors(v)) {
            if (nextVertex.getNumber() == start.getNumber()) {
                if (count == N) {
                    path.add(0,v);
                    return true;
                }
            }
            if (!visitedVertex[nextVertex.getNumber()]) {
                if (searchForCycle(nextVertex)) {
                    path.add(no,v);
                    no++;
                    return true;
                }
            }
        }
        visitedVertex[v.getNumber()] = false;
        count--;
        return false;
    }


}