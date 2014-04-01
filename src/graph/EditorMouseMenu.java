/*
 * EditorMouseMenu.java
 *
 * Created on March 21, 2007, 10:34 AM; Updated May 29, 2007
 *
 * Copyright 2007 Grotto Networking
 */

package graph;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import graph.GraphElements.MyEdge;
import graph.GraphElements.MyEdgeFactory;
import graph.GraphElements.MyVertex;
import graph.GraphElements.MyVertexFactory;

import java.awt.Dimension;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JSeparator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Illustrates the use of custom edge and vertex classes in a graph editing application.
 * Demonstrates a new graph mouse plugin for bringing up popup menus for vertices and
 * edges.
 * @author Dr. Greg M. Bernstein
 */
public class EditorMouseMenu {
    
	static JSpinner vertexSpinner;
	static LinkedList<MyVertex> vertexList = new LinkedList<MyVertex>();
	static SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g = new SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge>();
	static JFrame frame;
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        frame = new JFrame("Editing and Mouse Menu Demo");
        MyVertexFactory vFactory = MyVertexFactory.getInstance();
        MyEdgeFactory eFactory = MyEdgeFactory.getInstance();
    //    SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g = new SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge>();
        //Wykonać kolejkcję wierzchołków - potrzebne do dodawania krawędzi
     //   Pair<MyVertex> para = new Pair<GraphElements.MyVertex>(vFactory.create(), vFactory.create());
  /*      LinkedList<MyVertex> lista = new LinkedList<MyVertex>();
        for (int i=0;i<5;i++) {
        	lista.add(vFactory.create());
        	g.addVertex(lista.get(i));
        }*/
      //  g.addEdge(eFactory.create(), lista);
       // g.addVertex(para.getFirst());
       // g.addVertex(para.getSecond());
       // g.addEdge(eFactory.create(), para);
       // g.addVertex(vFactory.create());
        
        
        // Layout<V, E>, VisualizationViewer<V,E>
//        Map<GraphElements.MyVertex,Point2D> vertexLocations = new HashMap<GraphElements.MyVertex, Point2D>();
     //   Layout<GraphElements.MyVertex, GraphElements.MyEdge> layout = new StaticLayout(g);
        Layout<GraphElements.MyVertex, GraphElements.MyEdge> layout = new CircleLayout<GraphElements.MyVertex, GraphElements.MyEdge>(g);
        layout.setSize(new Dimension(300,300));
        VisualizationViewer<GraphElements.MyVertex,GraphElements.MyEdge> vv = 
                new VisualizationViewer<GraphElements.MyVertex,GraphElements.MyEdge>(layout);
        vv.setPreferredSize(new Dimension(350,350));
        // Show vertex and edge labels
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        // Create a graph mouse and add it to the visualization viewer
        /*EditingModalGraphMouse gm = new EditingModalGraphMouse(vv.getRenderContext(), 
                 GraphElements.MyVertexFactory.getInstance(),
                GraphElements.MyEdgeFactory.getInstance()); */
        EditingModalGraphMouse<MyVertex, MyEdge> gm = new EditingModalGraphMouse<GraphElements.MyVertex, GraphElements.MyEdge>(
        		vv.getRenderContext(),
        		vFactory,
        		eFactory);
        		
        // Set some defaults for the Edges...
        GraphElements.MyEdgeFactory.setDefaultCapacity(192.0);
        GraphElements.MyEdgeFactory.setDefaultWeight(5.0);
        // Trying out our new popup menu mouse plugin...
        PopupVertexEdgeMenuMousePlugin myPlugin = new PopupVertexEdgeMenuMousePlugin();
        // Add some popup menus for the edges and vertices to our mouse plugin.
        JPopupMenu edgeMenu = new MyMouseMenus.EdgeMenu(frame);
        JPopupMenu vertexMenu = new MyMouseMenus.VertexMenu();
        myPlugin.setEdgePopup(edgeMenu);
        myPlugin.setVertexPopup(vertexMenu);
        gm.remove(gm.getPopupEditingPlugin());  // Removes the existing popup editing plugin
        
        gm.add(myPlugin);   // Add our new plugin to the mouse
        
        vv.setGraphMouse(gm);
        
        
        //JFrame frame = new JFrame("Editing and Mouse Menu Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        
        JButton btnGenerate = new JButton("Generate");
        btnGenerate.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if ((int)vertexSpinner.getValue() > 0) generateGraph();
        	}
        });
        panel.add(btnGenerate);
        
        JLabel lblVertex = new JLabel("Vertex");
        panel.add(lblVertex);
        
        vertexSpinner = new JSpinner();
        panel.add(vertexSpinner);
        frame.getContentPane().add(vv);
        
        // Let's add a menu for changing mouse modes
        JMenuBar menuBar = new JMenuBar();
        JMenu modeMenu = gm.getModeMenu();
        modeMenu.setText("Mouse Mode");
        modeMenu.setIcon(null); // I'm using this in a main menu
        modeMenu.setPreferredSize(new Dimension(80,20)); // Change the size so I can see the text
        
        menuBar.add(modeMenu);
        frame.setJMenuBar(menuBar);
        gm.setMode(ModalGraphMouse.Mode.EDITING); // Start off in editing mode
        frame.pack();
        frame.setVisible(true);    
    }

	protected static void generateGraph() {
		  MyVertexFactory vFactory = MyVertexFactory.getInstance();
		  vertexList = new LinkedList<MyVertex>();
		  g = new SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge>();
		  for (int i=0;i<(int)vertexSpinner.getValue();i++) {
	        	vertexList.add(vFactory.create());
	        	g.addVertex(vertexList.get(i));
	        }
		vertexSpinner.getValue();
		frame.repaint();
	}
	
	
    
}
