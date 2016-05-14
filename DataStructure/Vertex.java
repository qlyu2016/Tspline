package DataStructure;





public class Vertex{
   private double x, y, z, w;	
   private double s, t;  // x, y
   private double[] sKnots;  // x
   private double[] tKnots;  // y
   private int i; //i
   private int j; //j
   
   public Vertex(double x, double y, double z, double w){
	   this.x = x;
	   this.y = y;
	   this.z = z;
	   this.w = w;
   }
   
   public void setI(int i){
      this.i = i;
   }
   
   public int getI(){
	  return this.i;
   }
   
   public void setJ(int j){
	  this.j = j;
   }
	   
   public int getJ(){
	  return this.j;
   }
   
   public void setSKnots(double[] sKnot){
	   this.sKnots = sKnot;
   }
   
   public double[] getSKnots(){
	   return this.sKnots;
   }
   
   public double[] getTKnots(){
	   return this.tKnots;
   }
   
   public void setTKnots(double[] tKnot){
	   this.tKnots = tKnot;
   }
   
   public void setKnot(double s, double t){
	   this.s = s;
	   this.t = t;
   }
   
   public double getKnots(){
	   return this.s;
   }
   
   public double getKnott(){
	   return this.t;
   }
   
   public double getW(){
	   return this.w;
   }
   
   public double getX(){
	   return this.x;
   }
   
   public void setX(double x){
	   this.x = x;
   }
   
   public double getY(){
	   return this.y;
   }
   
   public void setY(double y){
	   this.y = y;
   }
   
   public double getZ(){
	   return this.z;
   }
   
   public void setZ(double z){
	   this.z = z;
   }
   
   //数乘
   public static Vertex multiPoint(double multi, Vertex point){
	   Vertex result;
	   result = new Vertex(multi * point.getX(), multi * point.getY(), multi * point.getZ(), 1.0);
       return result;
   }
   
   //mark=0,两个向量相加，mark=1，前一个向量减去后一个向量
   public static Vertex addPoint(Vertex addPoint, Vertex point, int mark){
		Vertex result;
		if(mark == 0){
		    result = new Vertex(addPoint.getX() + point.getX(), addPoint.getY() + point.getY(), addPoint.getZ() + point.getZ(), 1.0);
		    return result;
		}else{
			result = new Vertex(addPoint.getX() - point.getX(), addPoint.getY() - point.getY(), addPoint.getZ() - point.getZ(), 1.0);
		    return result;
		}
   }
   
}