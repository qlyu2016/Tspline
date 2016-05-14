package Main;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;

import DataStructure.BSplineSurf;
import DataStructure.BicBezierSurf;
import DataStructure.Vertex;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;



public class BSplineSurfTest implements GLEventListener{
	private GLU glu;
    private GL2 gl;
    private double ritix = 0;
    private double ritiy = 0;
    private static String TITLE = "B-spline Surface";
    private static int CANVAS_HEIGHT = 600;
    private static int CANVAS_WIDTH = 600;
    private BSplineSurf bsplineSurf;
    private double mouseX = -1.0d;
    private double mouseY = -1.0d;
    private double mouseXNow;
    private double mouseYNow;
    private double centerx = 0;
    private double centery = 0;
    private double centerz = 0;
    private double scale = 0;
    private static MouseDrag mouse;
    
    
    public void initBSplineSurf(File file) throws IOException{
    	BufferedReader bf = new BufferedReader(new FileReader("D:/D3/BSplineSurf.txt"));
        int knotsNumber;
        int knottNumber;
    	String temp; //读取文件中下一行
    	String[] check;  //将temp进行split
    	
    	temp = bf.readLine();
    	check = temp.split(" ");
    	knotsNumber = Integer.parseInt(check[1]);
    	
    	temp = bf.readLine();
    	check = temp.split(" ");
    	knottNumber = Integer.parseInt(check[1]);
    	
    	temp = bf.readLine();
    	check = temp.split(" ");

    	double[] knots = new double[knotsNumber];
    	for(int i = 0; i < check.length; i ++){
    		knots[i] = Double.parseDouble(check[i]);
    	}
    	
    	temp = bf.readLine();
    	check = temp.split(" ");

    	double[] knott = new double[knottNumber];
    	for(int i = 0; i < check.length; i ++){
    		knott[i] = Double.parseDouble(check[i]);
    	}
    	
    	bsplineSurf = new BSplineSurf(knots, knott);

    	for(int i = 0; i < knottNumber - 4; i ++){
    		for(int j = 0; j < knotsNumber - 4; j ++){
               temp = bf.readLine();
               check = temp.split(" ");
    		   Vertex point1 = new Vertex(Double.parseDouble(check[1]), Double.parseDouble(check[2]), Double.parseDouble(check[3]), 1);
    		   bsplineSurf.setControlP(point1, j, knottNumber - 5 - i);
               this.centerx += point1.getX(); 
               this.centery += point1.getY(); 
               this.centerz += point1.getZ(); 
    	    }
    	}
    	
    	int controlPNum = (knotsNumber - 4) * (knottNumber - 4); 
    	this.centerx /= (double) controlPNum;
    	this.centery /= (double) controlPNum;
    	this.centerz /= (double) controlPNum;
    	
    	Vertex[][] controlF = bsplineSurf.getControlPBs();
    	for(int i = 0; i < knotsNumber - 4; i ++){
    		for(int j = 0; j < knottNumber - 4; j ++){
    			double tmp = (controlF[i][j].getX() - this.centerx) * (controlF[i][j].getX() - this.centerx) 
    					+ (controlF[i][j].getY() - this.centery) * (controlF[i][j].getY() - this.centery)  
    					+ (controlF[i][j].getZ() - this.centerz) * (controlF[i][j].getZ() - this.centerz);
    			tmp = Math.pow(tmp, 0.5d);
    		    if(this.scale < tmp){
    		    	this.scale = tmp;
    		    }
    		}
    	 }
    	
    	
    	bf.close();
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
	    
	    gl.glBegin(GL2.GL_TRIANGLES);
	        int segmentsNum = 20;
	        int segmenttNum = 20;
	        double knotsRange = bsplineSurf.getKnotsRange();
	        double knottRange = bsplineSurf.getKnottRange();
	        double[] knots = bsplineSurf.getKnots();
	        double[] knott = bsplineSurf.getKnott();
	        for(int i = 0; i < segmentsNum - 1; i ++){
	        	for(int j = 0; j < segmenttNum - 1; j ++){

	        	    Vertex temp1 = bsplineSurf.calBSpline((double)i * knotsRange / (double) segmentsNum + knots[3], (double)j * knottRange / (double) segmenttNum + knott[3]);
	        	    Vertex temp2 = bsplineSurf.calBSpline((double)(i + 1) * knotsRange / (double) segmentsNum + knots[3], (double)j * knottRange / (double) segmenttNum + knott[3]);
	        	    Vertex temp3 = bsplineSurf.calBSpline((double)(i + 1) * knotsRange / (double) segmentsNum + knots[3], (double)(j + 1) * knottRange / (double) segmenttNum + knott[3]);
	        		Vertex normal = findNormal(temp1.getX(), temp1.getY(), temp1.getZ()
	        				, temp2.getX(), temp2.getY(), temp2.getZ()
	        				, temp3.getX(), temp3.getY(), temp3.getZ());
	        		gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());
	        	    gl.glVertex3d(temp1.getX(), temp1.getY(), temp1.getZ());
	        	    gl.glVertex3d(temp2.getX(), temp2.getY(), temp2.getZ());
	        	    gl.glVertex3d(temp3.getX(), temp3.getY(), temp3.getZ());
	        	    
	        	    Vertex temp4 = bsplineSurf.calBSpline((double)i * knotsRange / (double) segmentsNum + knots[3]
	        	    		, (double)(j + 1) * knottRange / (double) segmenttNum + knott[3]);
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
	    BSplineSurfTest surf = new BSplineSurfTest();
	    canvas.addGLEventListener(surf);
	    surf.initBSplineSurf(null);
	    mouse = new MouseDrag();
	    canvas.addMouseMotionListener(mouse);
	    
	    frame.getContentPane().add(canvas);
	      
	    frame.setTitle(BicBezSurfTest.TITLE);
	    frame.setSize(new Dimension(BicBezSurfTest.CANVAS_HEIGHT, BicBezSurfTest.CANVAS_WIDTH));
	    frame.setVisible(true);
        animator.start();
	}
}