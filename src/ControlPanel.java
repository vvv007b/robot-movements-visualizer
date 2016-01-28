import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bocharov_n on 14.10.15.
 */
public class ControlPanel extends JPanel {

    private List<Camera> cameraList;
    private List<JButton> removeButtonList = new ArrayList<JButton>();

    public ControlPanel() {
        super();

        this.setPreferredSize(new Dimension(280, 480));


        this.cameraList = new ArrayList<Camera>();
        Camera defaultCamera = new Camera("Default",320,240,90,20,90);
//        addCamera(defaultCamera);
        defaultCamera.setPreferredSize(defaultCamera.getPreferredSize());
        this.add(defaultCamera);
        this.setLayout(new GridLayout(0,1));
//        this.setLayout(new SpringLayout());

//        SpringUtilities.makeCompactGrid(this, this.getComponentCount(), 1, 5, 5, 5, 5);
        this.validate();
        this.repaint();
    }

    private void addCamera(Camera camera) {
        JButton removeButton = new JButton("Delete camera");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = removeButtonList.indexOf(e.getSource());
                cameraList.remove(index);
                removeButtonList.remove(index);
                updateControlPanel();
            }
        });
        this.cameraList.add(camera);
        this.removeButtonList.add(removeButton);
    }

    private void updateControlPanel(){
        this.removeAll();
        for( int i = 0; i < cameraList.size(); i++ ) {
            this.add(cameraList.get(i));
            this.add(removeButtonList.get(i));
            this.add(new JSeparator(JSeparator.HORIZONTAL));
        }
//        System.out.print(cameraList.size());
//        this.add(addCameraButton);
        SpringUtilities.makeCompactGrid(this, this.getComponentCount(), 1, 5, 5, 5, 5);
        this.validate();
        this.repaint();

    }

    public List<Camera> getCameraList() {
        return cameraList;
    }

    public void setCameraList(List<Camera> cameraList) {
        this.cameraList = cameraList;
    }

}
