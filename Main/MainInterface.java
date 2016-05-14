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
	    //��¼���λ��   
		JFrame frame = new JFrame("Test");
	      
        //���֣�BorderLayout
        BorderLayout bl = new BorderLayout();   
	    frame.setLayout(bl);

	    //ѡ����壬���й��ܣ�ѡ���ļ�������һ��ϸ�֡�
        JPanel menu = new JPanel();  
        JLabel title = new JLabel("T-mesh", JLabel.CENTER);
        JButton nextSub = new JButton("Local Refinement"); 
	    JButton fileSelect = new JButton("File Select");
	  
        //ѡ����Ҫϸ�ֵ��ļ�
	    fileSelect.addActionListener(new ActionListener(){
		    @Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				  JFileChooser jFC = new JFileChooser();
			      jFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			      jFC.showDialog(new JLabel(), "ѡ��");
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
			            System.out.println("�ļ���:" + file.getAbsolutePath()); 
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
	      
	      //��ִ��һ��ϸ��
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
	      
	      
	      //���ѡ�п�����ת���ܣ�δʵ�֣�
	      MouseDrag mListener = new MouseDrag();
	      line.setMouse(mListener);
	      //����JOGL���档
	      canvas.addGLEventListener(line);
	      canvas.addMouseMotionListener(mListener);
	      
	      
	      frame.add(menu, BorderLayout.NORTH);
	      frame.add(canvas, BorderLayout.CENTER);
	      
	      frame.setTitle("T-spline");
       	  frame.setSize(500, 600);
	      frame.setVisible(true);
	      
	}
}