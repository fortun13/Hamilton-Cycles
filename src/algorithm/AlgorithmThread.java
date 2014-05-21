package algorithm;

import edu.uci.ics.jung.graph.SparseMultigraph;
import graph.GraphElements;

import javax.swing.*;
import java.util.LinkedList;

/**
 * Created by Fortun on 2014-05-21.
 */

public class AlgorithmThread extends Thread {

    private SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g;
    private JPanel graphPanel;
    private Algorithm al;
    private  LinkedList<GraphElements.MyVertex> path = new LinkedList<GraphElements.MyVertex>();

    public boolean sleeping = false;

    public AlgorithmThread (SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g, JPanel panel, Algorithm al) {
        this.g = g;
        graphPanel = panel;
        this.al = al;
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                sleeping = true;
                while (true) {
                    wait();
                    path = al.getCycle();
                    refreshPanel();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void refreshPanel() {
        for (GraphElements.MyEdge myEdge : g.getEdges()) myEdge.setNotAsPartOfCycle();
        if (path != null) {
            for (int i = 0; i < path.size() - 1; i++)
                g.findEdge(path.get(i), path.get(i + 1)).setAsPartOfCycle();
        }
        graphPanel.repaint();
        System.out.println(path);
    }

    public void setGraph(SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g) {
        this.g = g;
    }

    public void setGraphPanel(JPanel graphPanel) {
        this.graphPanel = graphPanel;
    }

    public void setAlgorithm(Algorithm al) {
        this.al = al;
    }
}
