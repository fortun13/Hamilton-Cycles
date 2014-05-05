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
    private JSpinner minSpinner;
    private JSpinner maxSpinner;
    private JSpinner iterationsSpinner;
    private JSpinner starterSpinner;
    
    private JCheckBox debugMode;
    
    private JComboBox<String> algorithmList;

	private LinkedList<MyVertex> vertexList = new LinkedList<MyVertex>();

	private Layout<GraphElements.MyVertex, GraphElements.MyEdge> layout;

	private MyVertexFactory vFactory = MyVertexFactory.getInstance();
	private MyEdgeFactory eFactory = MyEdgeFactory.getInstance();

	private VisualizationViewer<GraphElements.MyVertex, GraphElements.MyEdge> vv;

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
		setBounds(100, 100, 600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		setTitle("Hamilton Cycle");

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panelUp = new JPanel();
		panel.add(panelUp, BorderLayout.NORTH);

		JButton btnGenerate = new JButton("Generate");
		panelUp.add(btnGenerate);

		JLabel lblVertex = new JLabel("Vertex");
		panelUp.add(lblVertex);

		vertexSpinner = new JSpinner();
		panelUp.add(vertexSpinner);
		vertexSpinner.setValue(4);

		JButton btnShowGraph = new JButton("GraphToSystemOut");
		panelUp.add(btnShowGraph);

		JButton btnFindPath = new JButton("FindCycle");
		panelUp.add(btnFindPath);
		
		debugMode = new JCheckBox("DEBUG");
		debugMode.setSelected(true);
		panelUp.add(debugMode);
		
		JLabel lblVersionOfAlgorithm = new JLabel("Version of Algorithm");
		panelUp.add(lblVersionOfAlgorithm);
		
		algorithmList = new JComboBox<String>();
		algorithmList.addItem("Simple Algorithm");
		algorithmList.addItem("Genetic Algorithm");
		panelUp.add(algorithmList);
		btnFindPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (MyEdge myEdge : g.getEdges()) myEdge.setNotAsPartOfCycle();
				LinkedList<MyVertex> path = findPath();
				if (!(path == null)) {
					for (int i = 0; i < path.size() - 1; i++) {
						MyEdge edge = g.findEdge(path.get(i), path.get(i + 1));
						edge.setAsPartOfCycle();
					}
					//                    MyEdge edge = g.findEdge(path.get(path.size() - 1), path.get(0));
					//                    edge.setAsPartOfCycle();
					contentPane.repaint();
				}
				System.out.println(path);
			}
		});
		btnShowGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				printGraphToSysOut();
			}
		});
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((Integer) vertexSpinner.getValue() > 0) generateGraph();
			}
		});

		JPanel panelDown = new JPanel();
		panel.add(panelDown, BorderLayout.SOUTH);

		JLabel lblNumberOfIterations = new JLabel("Number of Iterations");
		panelDown.add(lblNumberOfIterations);

		iterationsSpinner = new JSpinner();
		iterationsSpinner.setValue(1000);
		panelDown.add(iterationsSpinner);
		
		JLabel lblMinNumber = new JLabel("min. number of specimen");
		panelDown.add(lblMinNumber);
		
		minSpinner = new JSpinner();
		minSpinner.setValue(2);
		panelDown.add(minSpinner);
		
		JLabel lblMaxNumberOf = new JLabel("max. number of specimen");
		panelDown.add(lblMaxNumberOf);
		
		maxSpinner = new JSpinner();
		maxSpinner.setValue(200);
		panelDown.add(maxSpinner);
		
		JLabel lblStarterPopulation = new JLabel("starter population");
		panelDown.add(lblStarterPopulation);
		
		starterSpinner = new JSpinner();
		starterSpinner.setValue(10);
		panelDown.add(starterSpinner);

		graphPanel = new JPanel();
		contentPane.add(graphPanel, BorderLayout.CENTER);
		setupMenu();
		this.setVisible(true);
	}

	private LinkedList<MyVertex> findPath() {
        Algorithm al = null;
        if (algorithmList.getSelectedIndex() == 0) al = new NonGenetic(g);
		else {
            al = new FirstVer(g,(Integer) starterSpinner.getValue(),
                    (Integer)iterationsSpinner.getValue(), (Integer)minSpinner.getValue(), (Integer)maxSpinner.getValue());
            if (debugMode.isSelected()) ((FirstVer)al).setDebugModeOn();
            else ((FirstVer)al).setDebugModeOff();
        }
		return al.getCycle();
	}

	protected void generateGraph() {
		MyEdgeFactory.resetFactory();
		MyVertexFactory.resetFactory();

		vertexList = new LinkedList<MyVertex>();
		int number = (Integer) vertexSpinner.getValue();
		double probability = 0.5;
		g = new SparseMultigraph<MyVertex, MyEdge>();
		for (int i = 0; i < number; i++) {
			vertexList.add(vFactory.create());
			g.addVertex(vertexList.get(i));
		}
		for (int i = 0; i < number; i++) {
			for (int j = i + 1; j < number; j++) {
				if (Math.random() < probability) {
					g.addEdge(eFactory.create(), vertexList.get(i), vertexList.get(j));
				}
			}
		}

		printGraphToSysOut();
		setupGraph();
		this.pack();
	}

	private void setupGraph() {

		contentPane.remove(graphPanel);
		graphPanel = new JPanel();

		layout = new CircleLayout<MyVertex, MyEdge>(g);
		layout.setSize(new Dimension(500, 500));
		vv = new VisualizationViewer<GraphElements.MyVertex, GraphElements.MyEdge>(layout);
		vv.setPreferredSize(new Dimension(550, 550));
		// Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());


		Transformer<MyEdge, Paint> edgeTransform = new Transformer<MyEdge, Paint>() {
			@Override
			public Paint transform(MyEdge myEdge) {
				if (myEdge.isPartOfCycle()) return Color.GREEN;
				else return Color.BLACK;
			}
		};

		EditingModalGraphMouse<MyVertex, MyEdge> gm = new EditingModalGraphMouse<GraphElements.MyVertex, GraphElements.MyEdge>(
				vv.getRenderContext(),
				vFactory,
				eFactory);

		vv.getRenderContext().setEdgeDrawPaintTransformer(edgeTransform);

		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.N);

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
		modeMenu.setPreferredSize(new Dimension(120, 20)); // Change the size so I can see the text

		menuBar.add(modeMenu);
		this.setJMenuBar(menuBar);
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING); // Start off in editing mode

		contentPane.add(graphPanel);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.EAST);

	}

	private void printGraphToSysOut() {
		for (MyVertex vertex : g.getVertices()) {
			System.out.println(vertex);
			for (MyEdge edge : g.getIncidentEdges(vertex)) {
				if (g.getSource(edge) == vertex) System.out.print(edge);
			}
			System.out.println();
		}
		System.out.println("---------------------------------------------");
	}

	/* Działa analogicznie do generateGraph tylko tworzy graf o jednym wierzchołku. Próba stworzenia "pustego" grafu
	 * zakończona niepowodzeniem - po dorysowaniu krawędzi i wierzchołków nie da się szukać cyklu, graf dalej jest traktowany jako pusty.
	 * Pytanie jak to obejść? - Krzysiek*/
	private void setupMenu() {
		MyEdgeFactory.resetFactory();
		MyVertexFactory.resetFactory();
		vertexList = new LinkedList<MyVertex>();
		g = new SparseMultigraph<MyVertex, MyEdge>();
		//    vertexList.add(vFactory.create());
		//    g.addVertex(vertexList.get(0));
		setupGraph();
		this.pack();
		printGraphToSysOut();
	}
}
