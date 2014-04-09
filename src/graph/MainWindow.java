package graph;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import graph.GraphElements.MyEdge;
import graph.GraphElements.MyEdgeFactory;
import graph.GraphElements.MyVertex;
import graph.GraphElements.MyVertexFactory;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private SparseMultigraph<MyVertex, MyEdge> g;

	private JSpinner vertexSpinner;

	private LinkedList<MyVertex> vertexList = new LinkedList<MyVertex>();

	private Layout<GraphElements.MyVertex, GraphElements.MyEdge> layout;

    private MyVertexFactory vFactory;
	private MyEdgeFactory eFactory;

	private VisualizationViewer<GraphElements.MyVertex,GraphElements.MyEdge> vv;
	
	private JPanel graphPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

	//	vFactory = MyVertexFactory.getInstance();
	//	eFactory = MyEdgeFactory.getInstance();

		//	g = new SparseMultigraph<>();

		//	this.setupGraph();

		// Create a graph mouse and add it to the visualization viewer
		/*EditingModalGraphMouse gm = new EditingModalGraphMouse(vv.getRenderContext(), 
                 GraphElements.MyVertexFactory.getInstance(),
                GraphElements.MyEdgeFactory.getInstance()); */
		/*		EditingModalGraphMouse<MyVertex, MyEdge> gm = new EditingModalGraphMouse<GraphElements.MyVertex, GraphElements.MyEdge>(
				vv.getRenderContext(),
				vFactory,
				eFactory);

		// Set some defaults for the Edges...
		GraphElements.MyEdgeFactory.setDefaultCapacity(192.0);
		GraphElements.MyEdgeFactory.setDefaultWeight(5.0);
		// Trying out our new popup menu mouse plugin...
		PopupVertexEdgeMenuMousePlugin myPlugin = new PopupVertexEdgeMenuMousePlugin();
		// Add some popup menus for the edges and vertices to our mouse plugin.
		JPopupMenu edgeMenu = new MyMouseMenus.EdgeMenu(this);
		JPopupMenu vertexMenu = new MyMouseMenus.VertexMenu();
		myPlugin.setEdgePopup(edgeMenu);
		myPlugin.setVertexPopup(vertexMenu);
		gm.remove(gm.getPopupEditingPlugin());  // Removes the existing popup editing plugin

		gm.add(myPlugin);   // Add our new plugin to the mouse

		vv.setGraphMouse(gm);*/


		//JFrame frame = new JFrame("Editing and Mouse Menu Demo");

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((Integer)vertexSpinner.getValue() > 0) generateGraph();
			}
		});
		panel.add(btnGenerate);

		JLabel lblVertex = new JLabel("Vertex");
		panel.add(lblVertex);

		vertexSpinner = new JSpinner();
        vertexSpinner.setValue(4);
		panel.add(vertexSpinner);

        JButton btnShowGraph = new JButton("GraphToSystemOut");
        btnShowGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printGraphToSysOut();
            }
        });

        panel.add(btnShowGraph);

        JButton btnFindPath = new JButton("FindCycle");
        btnFindPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LinkedList<MyVertex> path = findPath();
                if (!(path == null)) {
                    for (int i = 0; i < path.size() - 1; i++) {
                        MyEdge edge = g.findEdge(path.get(i), path.get(i + 1));
                        edge.setAsPartOfCycle();
                    }
                    MyEdge edge = g.findEdge(path.get(path.size()-1),path.get(0));
                    edge.setAsPartOfCycle();
                    // Still don't know how to redraw graph...
                    contentPane.repaint();
                }
                System.out.println(path);
            }
        });

        panel.add(btnFindPath);

		graphPanel = new JPanel();
		contentPane.add(graphPanel, BorderLayout.CENTER);

		//		contentPane.add(vv);

		// Let's add a menu for changing mouse modes
		/*		JMenuBar menuBar = new JMenuBar();
		JMenu modeMenu = gm.getModeMenu();
		modeMenu.setText("Mouse Mode");
		modeMenu.setIcon(null); // I'm using this in a main menu
		modeMenu.setPreferredSize(new Dimension(120,20)); // Change the size so I can see the text

		menuBar.add(modeMenu);
		this.setJMenuBar(menuBar);
		gm.setMode(ModalGraphMouse.Mode.EDITING); // Start off in editing mode
		this.pack();*/

		this.setVisible(true);    
	}

    private LinkedList<MyVertex> findPath() {
        Algorithm al = new NonGenetic(g,vertexList.get(0));
        return al.getCycle();
    }

    protected void generateGraph() {
		vFactory = MyVertexFactory.getInstance();
		eFactory = MyEdgeFactory.getInstance();
		vertexList = new LinkedList<MyVertex>();
		int number = (Integer)vertexSpinner.getValue();
		double probability = 0.8;
		g = new SparseMultigraph<MyVertex, MyEdge>();
		for (int i=0;i<number;i++) {
			vertexList.add(vFactory.create());
			g.addVertex(vertexList.get(i));
		}
		for(int i=0; i<number; i++){
			for(int j=i+1; j<number; j++){
				if(Math.random() < probability) {
					g.addEdge(eFactory.create(), vertexList.get(i), vertexList.get(j));

				} 
			}
		}

        printGraphToSysOut();
		//	vv.repaint();
		//	contentPane.repaint();
		setupGraph();
		this.pack();
//        System.out.println(vv.get);
		//	contentPane.repaint();
	//	contentPane.repaint();
	}

	private void setupGraph() {
		//		g.addVertex(vFactory.create());
		//		g.addVertex(vFactory.create());


		contentPane.remove(graphPanel);
		graphPanel = new JPanel();
	//	graphPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	//	graphPanel.setLayout(new BorderLayout(0, 0));
		
		
		layout = new CircleLayout<MyVertex, MyEdge>(g);
		layout.setSize(new Dimension(500,500));
		vv = new VisualizationViewer<GraphElements.MyVertex,GraphElements.MyEdge>(layout);
		vv.setPreferredSize(new Dimension(550,550));
		// Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());


        Transformer<MyEdge,Paint> edgeTransform = new Transformer<MyEdge, Paint>() {
            @Override
            public Paint transform(MyEdge myEdge) {
                if (myEdge.isPartOfCycle()) return Color.RED;
                else return Color.BLACK;
            }
        };

        EditingModalGraphMouse<MyVertex, MyEdge> gm = new EditingModalGraphMouse<GraphElements.MyVertex, GraphElements.MyEdge>(
				vv.getRenderContext(),
				vFactory,
				eFactory);

        vv.getRenderContext().setEdgeDrawPaintTransformer(edgeTransform);

        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.N);


		GraphElements.MyEdgeFactory.setDefaultCapacity(192.0);
		GraphElements.MyEdgeFactory.setDefaultWeight(5.0);
		// Trying out our new popup menu mouse plugin...
		PopupVertexEdgeMenuMousePlugin myPlugin = new PopupVertexEdgeMenuMousePlugin();
		// Add some popup menus for the edges and vertices to our mouse plugin.
		JPopupMenu edgeMenu = new MyMouseMenus.EdgeMenu(this);
		JPopupMenu vertexMenu = new MyMouseMenus.VertexMenu();
		myPlugin.setEdgePopup(edgeMenu);
		myPlugin.setVertexPopup(vertexMenu);
		gm.remove(gm.getPopupEditingPlugin());  // Removes the existing popup editing plugin

		gm.add(myPlugin);   // Add our new plugin to the mouse

		vv.setGraphMouse(gm);

		graphPanel.add(vv);

		JMenuBar menuBar = new JMenuBar();
		JMenu modeMenu = gm.getModeMenu();
		modeMenu.setText("Mouse Mode");
		modeMenu.setIcon(null); // I'm using this in a main menu
		modeMenu.setPreferredSize(new Dimension(120,20)); // Change the size so I can see the text

		menuBar.add(modeMenu);
		this.setJMenuBar(menuBar);
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING); // Start off in editing mode
		
		contentPane.add(graphPanel);

	}

    private void printGraphToSysOut() {
        for (MyVertex vertex : g.getVertices()) {
            System.out.println(vertex);
            for (MyEdge edge : g.getIncidentEdges(vertex)) {
                if (g.getSource(edge) == vertex) {
                    System.out.print(edge);
          //          if (Math.random() < 0.5) edge.setAsPartOfCycle();
                }
            }
            System.out.println();
        }
        System.out.println("---------------------------------------------");
    }
}
