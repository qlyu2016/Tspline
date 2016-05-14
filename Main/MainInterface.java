package Main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

public class MainInterface extends JFrame{
	private static File file;
	private static TsplineSurfTest line = new TsplineSurfTest();
    private static GLJPanel canvas = new GLJPanel();
    private static FPSAnimator animator = new FPSAnimator(canvas, 0, false);
	
	public static void main(String args[]) throws IOException{
	    //记录鼠标位置   
		JFrame frame = new JFrame("Test");
	      
        //布局：BorderLayout
        BorderLayout bl = new BorderLayout();   
	    frame.setLayout(bl);

	    //选项面板，具有功能：选择文件，进行一次细分。
        JPanel menu = new JPanel();  
        JLabel title = new JLabel("T-mesh", JLabel.CENTER);
        JButton nextSub = new JButton("Local Refinement"); 
	    JButton fileSelect = new JButton("File Select");
	  
        //选择需要细分的文件
	    fileSelect.addActionListener(new ActionListener(){
		    @Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				  JFileChooser jFC = new JFileChooser();
			      jFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			      jFC.showDialog(new JLabel(), "选择");
			      file = jFC.getSelectedFile();
			      
			      if(file.isFile() && file.getName().endsWith(".txt")){ 
			    	    
			    	  if(file.getName().equals("Tspline.txt")){
			    	    line.setFileOn(file);
			    	    try {
							line.initTspline();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	    line.resize();
			    	    animator.start();
			            System.out.println("文件夹:" + file.getAbsolutePath()); 
			    	  }

			      }else{
			    	  System.out.println("Type Error.");
			    	  JDialog dialog = new JDialog(frame, "Warning");
			          dialog.setSize(new Dimension(300,200));
			          JLabel warning = new JLabel("Please put in a file ends with '.txt'", JLabel.CENTER);
			    	  dialog.add(warning);
			          dialog.setVisible(true);
			      } 
			        System.out.println(jFC.getSelectedFile().getName());  
			    }  
	     });
	      
	      //再执行一次细分
	      nextSub.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(file.isFile() && file.getName().endsWith(".txt")){ 
		    	    animator.stop();
		    	    line.perform();
		    	    animator.start();
		      }else{
		    	  System.out.println("Type Error.");
		    	  JDialog dialog = new JDialog(frame, "Warning");
		          dialog.setSize(300, 200);
		          JLabel warning = new JLabel("Please put in a file ends with '.txt'", JLabel.CENTER);
		    	  dialog.add(warning);
		          dialog.setVisible(true);
		      } 
			}
	    	  
	      });
	      
	      
	      menu.setLayout(new BorderLayout());
	      menu.add(fileSelect, BorderLayout.WEST);
	      menu.add(nextSub, BorderLayout.EAST);
	      menu.add(title);
	      
	      
	      //鼠标选中可以旋转功能（未实现）
	      MouseDrag mListener = new MouseDrag();
	      line.setMouse(mListener);
	      //设置JOGL界面。
	      canvas.addGLEventListener(line);
	      canvas.addMouseMotionListener(mListener);
	      
	      
	      frame.add(menu, BorderLayout.NORTH);
	      frame.add(canvas, BorderLayout.CENTER);
	      
	      frame.setTitle("T-spline");
       	  frame.setSize(500, 600);
	      frame.setVisible(true);
	      
	}
}