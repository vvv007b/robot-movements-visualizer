package ru.mcst.RobotGroup.PathsFinding;

import ru.mcst.RobotGroup.PathsLinking.GUI;

import javax.swing.*;
import java.awt.*;


public class main_class /*extends JFrame*/{
    //Surface s;
    //Control_Panel cp;

	public main_class(){
				
		//setTitle("Robot");
		
		Surface s=new Surface();
        //cp=new Control_Panel(s);
        s.setPreferredSize(new Dimension(0, 0));
        //cp.setPreferredSize(cp.getPreferredSize());

//        JScrollPane scrollPaneForSurface = new JScrollPane(s);
//        scrollPaneForSurface.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        scrollPaneForSurface.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollPaneForSurface.getVerticalScrollBar().setUnitIncrement(10);
//        scrollPaneForSurface.getHorizontalScrollBar().setUnitIncrement(10);
                
//        String[] keystrokeNames = {"UP","DOWN","LEFT","RIGHT"};
//        for(int i=0; i<keystrokeNames.length; ++i)
//        	scrollPaneForSurface.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(keystrokeNames[i]), "none");

//        JScrollPane scrollPaneForControlPanel = new JScrollPane(cp);
//        scrollPaneForControlPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        scrollPaneForControlPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollPaneForControlPanel.getVerticalScrollBar().setUnitIncrement(10);
//        scrollPaneForControlPanel.getHorizontalScrollBar().setUnitIncrement(10);
        
//        scrollPaneForSurface.setFocusable(false);
//        scrollPaneForControlPanel.setFocusable(false);
//        cp.setFocusable(false);
//        s.setFocusable(true);
//        s.requestFocusInWindow();
        
//        setLayout(new BorderLayout());
//        add(scrollPaneForControlPanel, BorderLayout.EAST);
//        add(scrollPaneForSurface, BorderLayout.CENTER);
        
//        setSize(1001, 720);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setResizable(true);
//		setLocationRelativeTo(null);

	}
	
	public static void main(String[] args)	{
		SwingUtilities.invokeLater(new Runnable() {
			
            public void run() {
//				main_class sk = new main_class();
//                sk.setVisible(true);
                Surface surface=new Surface();
                MainWindow mainWindow=new MainWindow(surface);
				GUI gui = new GUI();
				gui.setVisible(true);


			}			
		});
	}
}