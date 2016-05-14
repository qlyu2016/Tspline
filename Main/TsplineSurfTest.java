package Main;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import DataStructure.BSplineSurf;
import DataStructure.ListVertex;
import DataStructure.TsplineSurf;
import DataStructure.Vertex;


public class TsplineSurfTest implements GLEventListener{
	private TsplineSurf tSpline;
	private GLU glu;
    private GL2 gl;
    private double ritix = 0;
    private double ritiy = 0;
    private static String TITLE = "Tensor-product Bezier Surface";
    private static int CANVAS_HEIGHT = 600;
    private static int CANVAS_WIDTH = 600;
    private double mouseX = -1.0d;
    private double mouseY = -1.0d;
    private double mouseXNow;
    private double mouseYNow;
    private double centerx = 0;
    private double centery = 0;
    private double centerz = 0;
    private double scale = 0;
    private static MouseDrag mouse;
    private File file;
    
    public void setMouse(MouseDrag mouse){
    	this.mouse = mouse;
    }
	
	public void initTspline() throws IOException{

		
		BufferedReader bf = new BufferedReader(new FileReader("D://D3/Tspline.txt"));
		
		String tempLine = bf.readLine();
		
		tempLine = bf.readLine();
		String[] check = tempLine.split(" ");
		int row, col;
		row = Integer.parseInt(check[1]);
		col = Integer.parseInt(check[3]);
		
		tempLine = bf.readLine();
		check = tempLine.split(" ");
		double[] knotParay = new double[check.length - 1];
		for(int i = 1; i < check.length; i ++){
			knotParay[i - 1] = Double.parseDouble(check[i]);
		}
		
		tempLine = bf.readLine();
		check = tempLine.split(" ");
		double[] knotParax = new double[check.length - 1];
		for(int i = 1; i < check.length; i ++){
			knotParax[i - 1] = Double.parseDouble(check[i]);
		}
		
		tempLine = bf.readLine();
		check = tempLine.split(" ");
	    int controlPNumber = Integer.parseInt(check[1]);
	    
		this.tSpline = new TsplineSurf(col, row, controlPNumber);
		this.tSpline.setKnotParax(knotParax);
		this.tSpline.setKnotParay(knotParay);
		
		int count = 0;
		ListVertex[] headRight = tSpline.getHeadRight();
		ListVertex[] tailRight = headRight;
		ListVertex[] headUp = tSpline.getHeadUp();
		ListVertex[] tailUp = headUp;
		while(!(tempLine = bf.readLine()).contains("edge")){
			check = tempLine.split(" ");
			ListVertex point = new ListVertex(Double.parseDouble(check[1]), Double.parseDouble(check[2])
					, Double.parseDouble(check[3]));
			double s = Double.parseDouble(check[6]);
			double t = Double.parseDouble(check[8]);
			point.setKnot(s, t);
			this.tSpline.setControlP(point, count);
			count ++;
		}
	
		while(!(tempLine = bf.readLine()).contains("edge")){
			check = tempLine.split(" ");
			int from = Integer.parseInt(check[1]);
			int to = Integer.parseInt(check[2]);
            tSpline.setRightLine(from, to);
		}
		
		while((tempLine = bf.readLine()) != null){
			check = tempLine.split(" ");
			int from = Integer.parseInt(check[1]);
			int to = Integer.parseInt(check[2]);
			tSpline.setUpLine(from, to);
		}
		
        tSpline.IntersectAll();
		
		bf.close();
	}
	
	public void resize(){
		
        ListVertex[] controlPoint = tSpline.getContorlP();
        
        for(int i = 0; i < controlPoint.length; i ++){
        	ListVertex point = controlPoint[i];
            this.centerx += point.getX(); 
            this.centery += point.getY(); 
            this.centerz += point.getZ(); 
        }
        
		this.centerx /= tSpline.getControlPNumber();
		this.centery /= tSpline.getControlPNumber();
		this.centerz /= tSpline.getControlPNumber();
        
        for(int i = 0; i < tSpline.getControlPNumber(); i ++){
			double tmp = (controlPoint[i].getX() - this.centerx) * (controlPoint[i].getX() - this.centerx) 
					+ (controlPoint[i].getY() - this.centery) * (controlPoint[i].getY() - this.centery)  
					+ (controlPoint[i].getZ() - this.centerz) * (controlPoint[i].getZ() - this.centerz);
			tmp = Math.pow(tmp, 0.5d);
		    if(this.scale < tmp){
		    	this.scale = tmp;
		    }
        }
	}
	
    //计算平面的法向量
    public Vertex findNormal(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3){
    	Vertex normal;
    	double vx, vy, vz, ux, uy, uz, length;
    	vx = x2 - x1;
    	vy = y2 - y1;
    	vz = z2 - z1;
    	ux = x3 - x2;
    	uy = y3 - y2;
    	uz = z3 - z2;
    	length = Math.pow((vx - ux) * (vx - ux) + (vy - uy) * (vy - uy) + (vz - uz) * (vz - uz), 0.5d);
    	
    	//叉乘向量u和v，得到法向量。
    	normal = new Vertex((vy * uz - vz * uy) / length, (vz * ux - vx * uz) / length, (vx * uy - vy * ux) / length, 1);
    	
    	return normal;
    }
	



	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		 gl = drawable.getGL().getGL2();
			
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();              //set the current matrix to identity matrix
		    //glu.gluPerspective(45.0f, 1.0f, 0.1f, 100.0f);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
		    gl.glLoadIdentity();
		    
		    if(this.mouseX == -1.0d){
		        this.mouseX = mouse.getXNow();
		        this.mouseY = mouse.getYNow();
		        //System.out.println("mouseX : " + this.mouseX + "mouseY : " + this.mouseY);
		    }else{
		        ritix += this.mouseXNow - this.mouseX;
		        ritiy += this.mouseYNow - this.mouseY;
		        this.mouseX = this.mouseXNow;
		        this.mouseY = this.mouseYNow;
		        this.mouseXNow = mouse.getXNow();
		        this.mouseYNow = mouse.getYNow();
		        //System.out.println("mouseX : " + this.mouseX + " mouseY : " + this.mouseY
		        //		           + "\n" + "mouseXNow : " + this.mouseXNow + "mouseYNow : " + this.mouseYNow);
		    }
		    
		  //设置灯光
		    float SHINE_ALL_DIRECTIONS = 1;
		    
		    float[] lightPos1 = {50.0f, 50.0f, -100.0f, SHINE_ALL_DIRECTIONS};
		    float[] lightColorAmbient1 = {0.3f, 0.3f, 0.3f, 1f};
		    float[] lightColorDiffuse1 = {0.7f, 0.7f, 0.7f, 1f};
		    
		    float[] lightPos2 = {-50.0f, -50.0f, 100.0f, 1.0f};
		    float[] lightColorAmbient2 = {0.3f, 0.3f, 0.3f, 1f};
		    float[] lightColorDiffuse2 = {0.7f, 0.7f, 0.7f, 1f};
		    
		    gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos1, 0);
		    gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient1, 0);
		    gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightColorDiffuse1, 0);
		    gl.glEnable(GL2.GL_LIGHT1);
		    
		    gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, lightPos2, 0);
		    gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, lightColorAmbient2, 0);
		    gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, lightColorDiffuse2, 0);
		    gl.glEnable(GL2.GL_LIGHT2);
		    
		    gl.glEnable(GL2.GL_LIGHTING);
		    gl.glEnable(GL2.GL_RESCALE_NORMAL);
		    
	        float[] rgba = {0.3f, 0.5f, 1f};
	        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
	        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, rgba, 0);
	        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0.5f);
		    
		    gl.glRotated(ritix, 1.0f, 0.0f, 0.0f);
		    gl.glRotated(ritiy, 0.0f, 1.0f, 0.0f);
		    gl.glTranslated((-this.centerx) / (1.5d * this.scale), (-this.centery) / (1.5d * this.scale), (-this.centerz) / (1.5d * this.scale));
		    gl.glScaled(1.0d / (1.5d * this.scale), 1.0d / (1.5d * this.scale), 1.0d / (1.5d * this.scale));
		    
		    gl.glColor3d(1.0d, 0.0d, 0.0d);
		    
		    gl.glBegin(GL.GL_TRIANGLES);
		       
	            int segmentsNum = 10;
	            int segmenttNum = 10;
	            double knotRange1 = tSpline.getKnotRangex();
	            double knotRange2 = tSpline.getKnotRangey();
	            double[] knotPara1 = tSpline.getKnotParax();
	            double[] knotPara2 = tSpline.getKnotParay();
	            for(int i = 0; i < segmentsNum - 1; i ++){
	        	   for(int j = 0; j < segmentsNum - 1; j ++){

	        	       ListVertex temp1 = tSpline.calTSplineSurf((double)i * knotRange1 / (double) segmentsNum + knotPara1[3]
	        	    		   , (double)j * knotRange2 / (double) segmenttNum + knotPara2[3]);
	        	       ListVertex temp2 = tSpline.calTSplineSurf((double)(i + 1) * knotRange1 / (double) segmentsNum + knotPara1[3]
	        	    		   , (double)j * knotRange2 / (double) segmenttNum + knotPara2[3]);
	        	       ListVertex temp3 = tSpline.calTSplineSurf((double)(i + 1) * knotRange1 / (double) segmentsNum + knotPara1[3]
	        	    		   , (double)(j + 1) * knotRange2 / (double) segmenttNum + knotPara2[3]);
	        		   Vertex normal = findNormal(temp1.getX(), temp1.getY(), temp1.getZ()
	        				, temp2.getX(), temp2.getY(), temp2.getZ()
	        				, temp3.getX(), temp3.getY(), temp3.getZ());
	        		gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());
	        	    gl.glVertex3d(temp1.getX(), temp1.getY(), temp1.getZ());
	        	    gl.glVertex3d(temp2.getX(), temp2.getY(), temp2.getZ());
	        	    gl.glVertex3d(temp3.getX(), temp3.getY(), temp3.getZ());
	        	    
	        	    ListVertex temp4 = tSpline.calTSplineSurf((double)i * knotRange1 / (double) segmentsNum + knotPara1[3]
	        	    		, (double)(j + 1) * knotRange2 / (double) segmenttNum + knotPara2[3]);
                    normal = findNormal(temp1.getX(), temp1.getY(), temp1.getZ()
                    		, temp3.getX(), temp3.getY(), temp3.getZ()
                    		, temp4.getX(), temp4.getY(), temp4.getZ());
	        		gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());
	        	    gl.glVertex3d(temp1.getX(), temp1.getY(), temp1.getZ());
	        	    gl.glVertex3d(temp3.getX(), temp3.getY(), temp3.getZ());
	        	    gl.glVertex3d(temp4.getX(), temp4.getY(), temp4.getZ());
                    
//	        	    System.out.println("i:" + i + "  j:" + j);
//	        	    System.out.println("x:" + temp1.getX() + "  y:" + temp1.getY() + "  z:" + temp1.getZ());
//	        	    System.out.println("x:" + temp2.getX() + "  y:" + temp2.getY() + "  z:" + temp2.getZ());
//	        	    System.out.println("x:" + temp3.getX() + "  y:" + temp3.getY() + "  z:" + temp3.getZ());
//	        	    System.out.println("x:" + temp4.getX() + "  y:" + temp4.getY() + "  z:" + temp4.getZ());
	            }
	        }
	            
	        gl.glEnd();
	        
	        gl.glColor3d(0.0, 0.0, 1.0);
	        
	        
		    
	}
	
	public Vertex[] findLineInCP(double para, Vertex[] controlP, String target){
		List<Vertex> temp = new ArrayList<Vertex>();

		   for(int i = 0; i < controlP.length; i ++){
			   if(target == "x"){
			       if(controlP[i].getX() == para){
				      temp.add(controlP[i]);
			      }
			   }else{
				   if(controlP[i].getY() == para){
					   temp.add(controlP[i]);
				   }
			   }
		   }
		   
		Vertex[] result = new Vertex[temp.size()];
		
		for(int i = 0; i < temp.size(); i ++){
			result[i] = temp.get(i);
		}
		
		return result;
	} 

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		  gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
	      glu = new GLU();                         // get GL Utilities
	      gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f); // set background (clear) color
	      gl.glClearDepth(1.0f);			// 设置深度缓存
	  	  gl.glEnable(GL.GL_DEPTH_TEST);	// 启用深度测试
	  	  gl.glDepthFunc(GL.GL_LEQUAL);		//the type of depth test
	      gl.glShadeModel(GL2.GL_SMOOTH);   // blends colors nicely, and smoothes out lighting// ----- Your OpenGL initialization code here -----
	      gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);			// 告诉系统对透视进行修正
	
	}



	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String args[]) throws IOException{
		JFrame frame = new JFrame();
	    
	    GLCanvas canvas = new GLCanvas();
        FPSAnimator animator = new FPSAnimator(canvas, BicBezSurfTest.CANVAS_WIDTH, false);
	    canvas.setPreferredSize(new Dimension(BicBezSurfTest.CANVAS_HEIGHT, BicBezSurfTest.CANVAS_WIDTH));
	    TsplineSurfTest surf = new TsplineSurfTest();
	    canvas.addGLEventListener(surf);
	    surf.initTspline();
	    ListVertex point = new ListVertex(0, 0, 0);
	    point.setKnot(4, 1);
	    surf.tSpline.doTsInsertion(point);
	    surf.resize();
	    mouse = new MouseDrag();
	    canvas.addMouseMotionListener(mouse);
	    
	    frame.getContentPane().add(canvas);
	      
	    frame.setTitle(BicBezSurfTest.TITLE);
	    frame.setSize(new Dimension(BicBezSurfTest.CANVAS_HEIGHT, BicBezSurfTest.CANVAS_WIDTH));
	    frame.setVisible(true);
        animator.start();
	}

	public void setFileOn(File file) {
		// TODO Auto-generated method stub
		this.file = file;
	}

	public void perform() {
		// TODO Auto-generated method stub
        ListVertex point = new ListVertex(3.0d, 4.0d, 3.0d);
        point.setKnot(4, 1);
        this.tSpline.doTsInsertion(point);
	}
	
}