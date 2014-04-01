package graph;

import java.awt.event.MouseEvent;

import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;

public class DummyMousePlugin extends AbstractGraphMousePlugin {

    public DummyMousePlugin() {
        this(MouseEvent.BUTTON1_MASK);
    }
    
    public DummyMousePlugin(int modifiers) {
        super(modifiers);
    }

}
