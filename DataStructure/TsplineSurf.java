package DataStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Jama.Matrix;


//使用的是三次B样条基函数
public class TsplineSurf{
	private ListVertex[] controlP;
	private ListVertex[] headRight;
	private ListVertex[] headUp;
	private List<ListVertex> newControlP = new ArrayList<>();
	private double[] knotParax;
	private double[] knotParay;
	private double knotRangex;
	private double knotRangey;
	private int controlPNumber;
	private int newControlPNum;
	private Matrix M12;
	private List<Vertex> tempCij = new ArrayList<Vertex>();
	
	private int xrow;
	private int ycol;
	
	//初始化三阶T样条曲面，设置Tmesh的x和y方向网格个数和控制点个数
	public TsplineSurf(int xrow, int ycol, int controlPNumber){
		this.xrow = xrow;
		this.ycol = ycol;
		this.controlPNumber = controlPNumber;
		
		this.headRight = new ListVertex[this.ycol];
		this.headUp = new ListVertex[this.xrow];
		this.controlP = new ListVertex[controlPNumber];
	}
	
	//设置s-knot vector和t-knot vector的值
	public void setKnotParay(double[] knotPara){
			this.knotParay = knotPara;
			this.knotRangey = knotPara[knotPara.length - 4] - knotPara[3];
			for(int i = 0; i < headRight.length; i ++){
				headRight[i] = new ListVertex(0.0, 0.0, 0.0);
				headRight[i].setKnot(-1, this.knotParay[i + 2]);
			}
	}
	
	public void setKnotParax(double[] KnotPara){
		this.knotParax = KnotPara;
		this.knotRangex = KnotPara[KnotPara.length - 4] - KnotPara[3];
		for(int i = 0; i < headUp.length; i ++){
			headUp[i] = new ListVertex(0.0, 0.0, 0.0);
			headUp[i].setKnot(this.knotParax[i + 2], -1);
		}
	}
	
	//给控制网格赋值
	public void setControlP(ListVertex point, int i){
		this.controlP[i] = point;
		int positionx = findInKnot(point.getS(), getPartof(knotParax, 2, this.xrow));
		int positiony = findInKnot(point.getT(), getPartof(knotParay, 2, this.ycol));
		ListVertex nodetempx;
		ListVertex nodetempy;
		
		nodetempx = this.headUp[positionx];
		while(nodetempx.upnext != null){
			nodetempx = nodetempx.upnext;
		}
		nodetempx.upnext = point;

		nodetempy = this.headRight[positiony];
		while(nodetempy.rnext != null){
			nodetempy = nodetempy.rnext;
		}
		nodetempy.rnext = point;
	}
	
	public void setRightLine(int from, int to){
		int positiony = findInKnot(this.controlP[from].getT()
				, getPartof(knotParay, 2, this.ycol));
		ListVertex nodetempy = this.headRight[positiony];
		
		while(nodetempy != null){
			if(nodetempy == this.controlP[from]){
				this.controlP[from].setHasLineRight();
				break;
			}
			nodetempy = nodetempy.rnext;
		}
		
	}
	
	public void setUpLine(int from, int to){
		int positionx = findInKnot(this.controlP[from].getS()
				, getPartof(knotParax, 2, this.xrow));
		ListVertex nodetempx = this.headUp[positionx];
		
		while(nodetempx != null){
			if(nodetempx == this.controlP[from]){
				this.controlP[from].setHasLineUp();
				break;
			}
			nodetempx = nodetempx.upnext;
		}
	}
	
	public ListVertex[] getContorlP(){
		return this.controlP;
	}
	
	public ListVertex[] getHeadRight(){
		return this.headRight;
	}
	
	public ListVertex[] getHeadUp(){
		return this.headUp;
	}
	
	public int getxRow(){
		return this.xrow;
	}
	
	public int getyCol(){
	    return this.ycol;
	}
	
	public double getKnotRangex(){
		return this.knotRangex;
	}
	
	public double getKnotRangey(){
		return this.knotRangey;
	}
	
	public double[] getKnotParax(){
		return this.knotParax;
	}
	
	public double[] getKnotParay(){
		return this.knotParay;
	}
	
	public int getControlPNumber(){
		return this.controlPNumber;
	}
	
	public void setM12ControlPOn(){
		double[][] Matrix = new double[this.controlPNumber][this.newControlPNum];
		for(int k = 0; k < tempCij.size(); k ++){
			Vertex tempV = tempCij.get(k);
			double Cji = tempV.getZ();
			double s = tempV.getKnots();
			double t = tempV.getKnott();
			int i = tempV.getI();
			
			Matrix[i][i] = Cji;
		}
		
		this.M12 = new Matrix(Matrix);
		
		for(int i = 0; i < this.controlPNumber; i ++){
			double x = 0, y = 0, z = 0;
			for(int j = 0; j < this.newControlPNum; j ++){
			    x += M12.get(i, j) * this.controlP[j].getX();
			    y += M12.get(i, j) * this.controlP[j].getY();
			    z += M12.get(i, j) * this.controlP[j].getZ();
			}
			this.controlP[i].setX(x);
			this.controlP[i].setY(y);
			this.controlP[i].setZ(z);
		}
	}
	
	//control point insertion in T-mesh
	public void doTsInsertion(ListVertex point){
		for(int i = 0; i < this.controlP.length; i ++){
			this.newControlP.add(this.controlP[i]);
		}
		
		List<ListVertex> insertP = new ArrayList<>();
		insertP.add(point);
		
		while(insertP.size() > 0)
		{
		   //insert a ControlPoint
		   ListVertex pointN = insertP.remove(0);
		   this.newControlP.add(pointN);
		   
		   double s = pointN.getS();
		   double t = pointN.getT();
		   
		   int positionx = findInKnot(s, getPartof(this.knotParax, 2, this.xrow));
		   int positiony = findInKnot(t, getPartof(this.knotParay, 2, this.ycol));
		   
		   ListVertex htemp = this.headRight[positiony];
		   while(htemp.rnext != null && htemp.getS() <= s && htemp.rnext.getS() >= s){
			   htemp = htemp.rnext;
		   }
		   
		   double[][] tempBlendRight = null;
		   double c0rt = -1;
		   double d0rt = -1;
		   if(htemp.getTKnots() != null){
		      tempBlendRight = refineBlendF(t, htemp.getTKnots(), positiony);
		      c0rt = tempBlendRight[2][0];
		      d0rt = tempBlendRight[2][1];
		   }
		   ListVertex temp = htemp.rnext;
		   htemp.rnext = pointN;
		   htemp = htemp.rnext;
		   htemp.rnext = temp;
		   
		   htemp = this.headUp[positionx];
		   while(htemp.upnext != null && htemp.getT() <= t && htemp.upnext.getT() >= t){
			   htemp = htemp.upnext;
		   }
		   double[][] tempBlendUp = null;
		   double c0up = -1;
		   double d0up = -1;
		   
		   if(htemp.getSKnots() != null){
		      tempBlendUp = refineBlendF(s, htemp.getSKnots(), positionx);
		      c0up = tempBlendUp[2][0];
		      d0up = tempBlendUp[2][1];
		   }
		   
		   if(tempBlendUp != null && tempBlendRight != null){
		      getSettled(tempBlendUp[0][2], tempBlendRight[0][2], c0up, c0rt, htemp);
		      getSettled(tempBlendUp[0][2], tempBlendRight[1][2], c0up, d0rt, htemp);
		      getSettled(tempBlendUp[1][2], tempBlendRight[0][2], d0up, c0rt, htemp);
		      getSettled(tempBlendUp[1][2], tempBlendRight[1][2], d0up, d0rt, htemp);
		   }
		   
		   temp = htemp.upnext;
		   htemp.upnext = pointN;
		   htemp = htemp.upnext;
		   htemp.upnext = temp;
		   double[] sKnots = {0, 0, 0, 0, 0};
		   pointN.setSKnots(sKnots);
		   double[] tKnots = {0, 0, 0, 0, 0};
		   pointN.setTKnots(tKnots);
		   
		}
		
		this.controlP = new ListVertex[this.newControlP.size()];
		for(int i = 0; i < this.newControlP.size(); i ++){
			this.controlP[i] = this.newControlP.get(i);
		}
	}
	
	public void getSettled(double s, double t, double c0, double d0, ListVertex pre){
		ListVertex result = new ListVertex(0, 0, 0);
		
		result.setX(ListVertex.multiPoint(c0 * d0, pre).getX());
		result.setY(ListVertex.multiPoint(c0 * d0, pre).getY());
		result.setZ(ListVertex.multiPoint(c0 * d0, pre).getZ());
		
		result.setHasLineRight();
		result.rnext = pre;
	}
	
	public boolean isDoubleSame(double[] target, double[] data){
		if(target.length != data.length){
			return false;
		}else{
			for(int i = 0; i < target.length; i ++){
				if(target[i] != data[i]){
					return false;
				}
			}
		}
		return true;
	}
	
	//insert a knot in knot vector knotPara if knot is not existed in knotPara
	//otherwise return knotPara, if para is out of the knotRange, return null
	public double[] insertKnotPara(double para, double[] knotPara){
		if(findInKnot(para, knotPara) > -1){
			double[] knots = getPartof(knotPara, 2, knotPara.length - 5);
			int positionx = findInKnot(para, knotPara) + 2;
			if(positionx == -1){
				System.out.println("Illegal Insertion.");
				return null;
			}else{
				//double[] tempKnot = knotPara;
				int length = knotPara.length + 1;
				double[] result = new double[length];
				for(int i = 0; i <= positionx; i ++){
					result[i] = knotPara[i];
				}
				result[positionx + 1] = para;
				for(int i = positionx + 2; i < length; i ++){
					result[i] = knotPara[i - 1];
				}
				return result;
			}
		}else{
			return knotPara;
		}
	}
	
	//insert the point into controlPoint in right order
	public void insertControlP(double s, double t){
		
		ListVertex[] controlPoint = this.controlP;
		this.controlPNumber ++;
		this.controlP = new ListVertex[this.controlPNumber];
		
		for(int i = 0; i < this.controlPNumber - 1; i ++){
            this.controlP[i] = controlPoint[i];
		}
		ListVertex newPoint = new ListVertex(0.0, 0.0, 0.0);
		newPoint.setKnot(s, t);
		this.controlP[this.controlPNumber - 1] = newPoint;
		
	}
	
	//if the order of object is bigger than target(include equal)
	public boolean isBigger(double targets, double targett, double objects, double objectt){
		if(objectt > targett){
			return true;
		}else if(objectt < targett){
			return false;
		}else{
			if(objects > targets){
				return true;
			}else if(objects < targets){
				return false;
			}else{
				return true;
			}
		}
	}
	
	public int findInKnot(double para, double[] knot){
		for(int i = 0; i < knot.length; i ++){
			if(knot[i] == para)
				return i;
		}
		return -1;
	}
	
	//question...
	public ListVertex calTSplineSurf(double s, double t){
		ListVertex result = new ListVertex(0, 0, 0);
		double Bst;
		
		for(int i = 0; i < this.controlPNumber; i ++){
			
			ListVertex htemp = this.controlP[i];
				
			double Ns, Nt;
 
			Ns = NurbsBasisCurve(s, htemp.getSKnots());
			Nt = NurbsBasisCurve(t, htemp.getTKnots());
			Bst = Ns * Nt;
			    
			result = ListVertex.addPoint(result, ListVertex.multiPoint
			    	(Bst, htemp), 0);
		}
        
        return result;
	}
	
	public void IntersectAll(){
		for(int i = 0; i < this.ycol; i ++){
			ListVertex htemp = this.headRight[i].rnext;
			while(htemp != null){
				
				this.Intersect(htemp);
                htemp = htemp.rnext; 
			}
		}
	}
	
	public void Intersect(ListVertex point){
         int positiony = findInKnot(point.getT(), getPartof(knotParay, 2, this.ycol));
         int positionx = findInKnot(point.getS(), getPartof(knotParax, 2, this.xrow));
         boolean[] isIntersectR = new boolean[this.knotParax.length];
         boolean[] isIntersectU = new boolean[this.knotParay.length];
         for(int i = 0; i < this.knotParax.length; i ++){
        	 if(i >= 2 && i <= this.knotParax.length - 3)
        	     isIntersectR[i] = isIntersectR(positiony, i - 2);
        	 else if(i < 2){
        		 isIntersectR[i] = true;
        	 }else{
        		 isIntersectR[i] = true;
        	 }
         }
         
//         System.out.println("s:" + point.getS() + " t:" + point.getT());
//         System.out.println(Arrays.toString(this.knotParax) + " " + Arrays.toString(isIntersectR) + " " + positionx + 2);
         point.setSKnots(calKnots(this.knotParax, isIntersectR, positionx + 2));
         
         for(int i = 0; i < this.knotParay.length; i ++){
        	 if(i >= 2 && i < this.knotParay.length - 2)
        	     isIntersectU[i] = isIntersectU(positionx, i - 2);
        	 else if(i < 2){
        		 isIntersectU[i] = true;
        	 }else{
        		 isIntersectU[i] = true;
        	 }
         }
         
         point.setTKnots(calKnots(this.knotParay, isIntersectU, positiony + 2));
	}
	
	public double[] calKnots(double[] knots, boolean[] isIntersect, int position){
		int count = 2;
		int p = position;
		double[] result = new double[5];
		
		while(count < 5){
			if(isIntersect[p] == true){
				result[count] = knots[p];
				count ++;
			}
			p ++;
		}
		
		count = 1;
		p = position - 1;
		while(count >= 0){
			if(isIntersect[p] == true){
				result[count] = knots[p];
				count --;
			}
			p --;
		}
		
		return result;
	}

	public boolean isIntersectR(int positiony, int indexX){
         double t = headRight[positiony].getT();
         ListVertex head = headUp[indexX].upnext;
         
         while(head != null && head.getUpNext() != null){
        	 if(head.getT() <= t && head.getUpNext().getT() >= t && head.hasLineUp()){
        		 return true;
        	 }
        	 
        	 head = head.getUpNext();
         }
         
         return false;
	}
	
	public boolean isIntersectU(int positionx, int indexY){
        double s = headUp[positionx].getS();
        ListVertex head = headRight[indexY].rnext;
        
        while(head != null && head.getRightNext() != null){
       	 if(head.getS() <= s && head.getRightNext().getS() >= s && head.hasLineRight()){
       		 return true;
       	 }
       	 
       	 head = head.getRightNext();
        }
        
        return false;
	}
	
	//data数组中从start开始截取length个
	public static double[] getPartof(double[] data, int start, int length){
		double[] result = new double[length];
		
		for(int i = 0; i < length; i ++){
			result[i] = data[start + i];
		}
		
		return result;
	}
	
    public double[][] refineBlendF(double para, double[] Knots, int position){
    	double[][] result = new double[3][5];
    	
    	switch(position){
		   case 0:{
			   double c0 = (para - Knots[0]) / (Knots[3] - Knots[0]);
			   double d0 = 1;
			   double[] knot1 = {Knots[0], para, Knots[1], Knots[2], Knots[3]};
			   double[] knot2 = {para, Knots[1], Knots[2], Knots[3], Knots[4]};
			   result[2][0] = c0;   result[2][1] = d0;
			   result[0] = knot1; 
			   result[1] = knot2;
			   return result;
		   }
		   case 1:{
			   double c1 = (para - Knots[0]) / (Knots[3] - Knots[0]);
			   double d1 = (Knots[4] - para) / (Knots[4] - Knots[1]);
			   double[] knot1 = {Knots[0], Knots[1], para, Knots[2], Knots[3]};
			   double[] knot2 = {Knots[1], para, Knots[2], Knots[3], Knots[4]};
			   result[2][0] = c1;   result[2][1] = d1;
			   result[0] = knot1;
			   result[1] = knot2;
			   return result;
		   }
		   case 2:{
			   double c2 = (para - Knots[0]) / (Knots[3] - Knots[0]);
			   double d2 = (Knots[4] - para) / (Knots[4] - Knots[1]);
			   double[] knot1 = {Knots[0], Knots[1], Knots[2], para, Knots[3]};
			   double[] knot2 = {Knots[1], Knots[2], para, Knots[3], Knots[4]};
			   result[2][0] = c2;   result[2][1] = d2;
			   result[0] = knot1;
			   result[1] = knot2;
			   return result;

		   }
		   case 3:{
			   double c3 = 1;
			   double d3 = (Knots[4] - para) / (Knots[4] - Knots[1]);
			   double[] knot1 = {Knots[0], Knots[1], Knots[2], Knots[3], para};
			   double[] knot2 = {Knots[1], Knots[2], Knots[3], para, Knots[4]};
			   result[2][0] = c3;   result[2][1] = d3;
			   result[0] = knot1;
			   result[1] = knot2;
			   return result;
		   }
		   default: return null;
		   }
    }
   
	public static double NurbsBasisCurve(double para, double[] knots){
		if(knots.length < 2)
			return -1;
		
		if(knots.length == 2){
			if(para >= knots[0] && para < knots[knots.length - 1]){
				return 1;
			}else{
				return 0;
			}
		}else{
            double subRe1 = 0;
            double subRe2 = 0;
            double[] knots1 = getPartof(knots, 0, knots.length - 1);
            double[] knots2 = getPartof(knots, 1, knots.length - 1);
            
			if(para > knots1[0] && para <= knots1[knots1.length - 1])
				if(knots1[knots1.length - 1] != knots1[0])
			        subRe1 = (para - knots1[0]) * NurbsBasisCurve(para, knots1) / (knots1[knots1.length - 1] - knots1[0]);
			if(para >= knots2[0] && para < knots2[knots1.length - 1])
				if(knots2[knots2.length - 1] != knots2[0])
			        subRe2 = (knots2[knots2.length - 1] - para) * NurbsBasisCurve(para, knots2) / (knots2[knots2.length - 1] - knots2[0]);
			
			return subRe1 + subRe2;
		}
	}
	
	public static void main(String args[]){
		double[] knots = {0.0, 0.0, 0.0, 1.0, 6.0};
		
		System.out.println(NurbsBasisCurve(1d, knots));
	} 
	
}