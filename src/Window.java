import javax.swing.*;
import java.awt.*;

/**
 * Created by bocharov_n on 14.10.15.
 */
public class Window extends JFrame{

    private ControlPanel controlPanel;

    private Map map;

    public Window(){
        super();
        this.setLayout(new FlowLayout());
        this.controlPanel = new ControlPanel();
        this.map = new Map();
//        JScrollPane scroll = new JScrollPane(controlPanel);
        this.add(map);
//        this.add(scroll);
//        this.add(gui);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public void setControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

}
