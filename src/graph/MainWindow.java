package graph;

import algorithm.*;
import chart.MyChartWindow;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.LinkedList;

public class MainWindow extends JFrame {

    private JPanel contentPane;
	private SparseMultigraph<MyVertex, MyEdge> g;

	private JSpinner vertexSpinner;
    private JSpinner minSpinner;
    private JSpinner maxSpinner;
    private JSpinner iterationsSpinner;
    private JSpinner starterSpinner;

    private final JSlider levelSlider;

    private final JTextField levelInput;

    private final JComboBox<CrossingScheme> shcemeChooser;

    // Labels for a few spinners
    private JLabel lblNumberOfIterations;
    private JLabel lblMinNumber;
    private JLabel lblMaxNumberOf;
    private JLabel lblStarterPopulation;

    private JCheckBox debugMode;
    
    private JComboBox<String> algorithmList;

	private LinkedList<MyVertex> vertexList = new LinkedList<MyVertex>();

    private MyVertexFactory vFactory = MyVertexFactory.getInstance();
	private MyEdgeFactory eFactory = MyEdgeFactory.getInstance();

    private JPanel graphPanel;

    private XYSeries[] series = new XYSeries[4];

    private AlgorithmThread thread = null;

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

        vertexSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));
		panelUp.add(vertexSpinner);

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
		algorithmList.addItem("Extended Algorithm");
        algorithmList.addItem("Basic Algorithm");
        algorithmList.setSelectedIndex(2);
        algorithmList.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        switch (algorithmList.getSelectedIndex()) {
                            case 0:
                                lblNumberOfIterations.setVisible(false);
                                lblMinNumber.setVisible(false);
                                lblMaxNumberOf.setVisible(false);
                                lblStarterPopulation.setVisible(false);
                                starterSpinner.setVisible(false);
                                iterationsSpinner.setVisible(false);
                                minSpinner.setVisible(false);
                                maxSpinner.setVisible(false);
                                break;
                            case 1:
                                lblNumberOfIterations.setVisible(true);
                                lblMinNumber.setVisible(true);
                                lblMaxNumberOf.setVisible(true);
                                lblStarterPopulation.setVisible(true);
                                starterSpinner.setVisible(true);
                                iterationsSpinner.setVisible(true);
                                minSpinner.setVisible(true);
                                maxSpinner.setVisible(true);
                                break;
                            case 2:
                                lblNumberOfIterations.setVisible(true);
                                lblMinNumber.setVisible(false);
                                lblMaxNumberOf.setVisible(false);
                                lblStarterPopulation.setVisible(true);
                                starterSpinner.setVisible(true);
                                iterationsSpinner.setVisible(true);
                                minSpinner.setVisible(false);
                                maxSpinner.setVisible(false);
                                break;
                        }
                    }
                }
        );
        panelUp.add(algorithmList);
		btnFindPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                findPath();
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
		panel.add(panelDown, BorderLayout.CENTER);

		lblNumberOfIterations = new JLabel("Number of Iterations");
		panelDown.add(lblNumberOfIterations);

		iterationsSpinner = new JSpinner(new SpinnerNumberModel(1000, 100, 20000, 1));
		panelDown.add(iterationsSpinner);
		
		lblMinNumber = new JLabel("min. number of specimen");
		panelDown.add(lblMinNumber);

		minSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 100, 1));
		panelDown.add(minSpinner);
		
		lblMaxNumberOf = new JLabel("max. number of specimen");
		panelDown.add(lblMaxNumberOf);

		maxSpinner = new JSpinner(new SpinnerNumberModel(200, 50, 1000, 1));
		panelDown.add(maxSpinner);
		
	    lblStarterPopulation = new JLabel("starter population");
		panelDown.add(lblStarterPopulation);

		starterSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
		//starterSpinner.setValue(10);
		panelDown.add(starterSpinner);

        JPanel panelSliders = new JPanel();
        panel.add(panelSliders, BorderLayout.SOUTH);

        JLabel lblMutationLevel = new JLabel("mutation levelSlider");
                panelSliders.add(lblMutationLevel);
                
                        levelSlider = new JSlider(0, 100, 1);
                        panelSliders.add(levelSlider);
                        
                                levelInput = new JTextField("1%");
                                panelSliders.add(levelInput);
                                levelInput.addFocusListener(new FocusAdapter() {
                                    @Override
                                    public void focusLost(FocusEvent e) {
                                        updateValues();
                                    }
                                });
                                levelInput.addActionListener(new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        updateValues();
                                    }
                                });
                                
                                        levelInput.setPreferredSize(new Dimension(40, (int) maxSpinner.getPreferredSize().getHeight()));
                                        levelInput.setHorizontalAlignment(SwingConstants.RIGHT);
                        levelSlider.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                levelInput.setText(levelSlider.getValue() + "%");
                            }
                        });

        shcemeChooser = new JComboBox<CrossingScheme>(new CrossingScheme[] {new Best(), new RandomUnit(), new Gaussian()});
        panelDown.add(shcemeChooser);

        levelInput.setPreferredSize(new Dimension(40, (int) maxSpinner.getPreferredSize().getHeight()));
        levelInput.setHorizontalAlignment(SwingConstants.RIGHT);
        panelDown.add(levelInput);
        
		graphPanel = new JPanel();
		contentPane.add(graphPanel, BorderLayout.CENTER);

        series[0] = new XYSeries("Best Adaptation");
        series[1] = new XYSeries("Worst Adaptation");
        series[2] = new XYSeries("Best/Expected");
        series[3] = new XYSeries("Worst/Expected");

        final XYSeriesCollection[] dataset = new XYSeriesCollection[4];
        dataset[0] = new XYSeriesCollection(series[0]);
        dataset[1] = new XYSeriesCollection(series[1]);
        dataset[2] = new XYSeriesCollection(series[2]);
        dataset[3] = new XYSeriesCollection(series[3]);
        new Thread() {
            @Override
            public void run() {
                new MyChartWindow(dataset);
            }
        }.start();

		setupMenu();
		this.setVisible(true);
	}

    private void updateValues() {
        String input = levelInput.getText().replaceAll("\\s", "");
        if (input.matches("1?\\d?\\d%?"))
            levelSlider.setValue(Integer.parseInt(input.replaceAll("%","")));
        else levelInput.setText(levelSlider.getValue() + "%");
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    private void findPath() {
            if (thread == null) {
                thread = new AlgorithmThread(g, graphPanel, getAlgorithm());
                thread.start();
            } else {
                thread.setGraph(g);
                thread.setAlgorithm(getAlgorithm());
                thread.setGraphPanel(graphPanel);
            }
        synchronized (thread) {
            thread.wakeup();
        }
    }

    private Algorithm getAlgorithm() {
        Algorithm al = null;
        switch (algorithmList.getSelectedIndex()) {
            case 0:
                al = new NonGenetic(g);
                break;
            case 1:
                series[0].clear();
                series[1].clear();
                al = new FirstVer(g, (Integer) starterSpinner.getValue(), (Integer) iterationsSpinner.getValue(),
                        (Integer) minSpinner.getValue(), (Integer) maxSpinner.getValue(), series);
                if (debugMode.isSelected()) ((FirstVer) al).setDebugModeOn();
                else ((FirstVer) al).setDebugModeOff();
                break;
            case 2:
                series[0].clear();
                series[1].clear();
                series[2].clear();
                series[3].clear();
                al = new SecondVer(g, (Integer) starterSpinner.getValue(), (Integer) iterationsSpinner.getValue(), series, levelSlider.getValue()/100d, (CrossingScheme) shcemeChooser.getSelectedItem());
                break;
        }
        return al;
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

        CircleLayout<MyVertex, MyEdge> layout = new CircleLayout<MyVertex, MyEdge>(g);
		layout.setSize(new Dimension(800, 500));
        VisualizationViewer<MyVertex, MyEdge> vv = new VisualizationViewer<MyVertex, MyEdge>(layout);
		vv.setPreferredSize(new Dimension(850, 550));
		// Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<MyVertex>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<MyEdge>());


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
