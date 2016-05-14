package DataStructure;



public class ListVertex{
	private double x, y, z;	
	private double s, t;  // x, y
	private double[] sKnots;  // x
	private double[] tKnots;  // y
	ListVertex rnext = null;
	private boolean hasLineRight = false;
	ListVertex upnext = null;
	private boolean hasLineUp = false;
	
	public ListVertex(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setHasLineRight(){
		this.hasLineRight = true;
	}
	
	public boolean hasLineRight(){
		return this.hasLineRight;
	}
	
	public void setHasLineUp(){
		this.hasLineUp = true;
	}
	
	public boolean hasLineUp(){
		return this.hasLineUp;
	}
	
	public double getX(){
		return this.x;
	}
	
	public double getY(){
		return this.y;
	}
	
	public double getZ(){
		return this.z;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public void setZ(double z){
		this.z = z;
	}
	
	public void setKnot(double s, double t){
		this.s = s;
		this.t = t;
	}
	
	public double getS(){
		return this.s;
	}
	
	public double getT(){
		return this.t;
	}
	
	public void setSKnots(double[] sKnots){
		this.sKnots = sKnots;
	}
	
	public double[] getSKnots(){
		return this.sKnots;
	}
	
	public double[] getTKnots(){
		return this.tKnots;
	}
	
	public void setTKnots(double[] tKnots){
		this.tKnots = tKnots;
	}
	
	public void setRightNext(ListVertex rNext){
		this.rnext = rNext;
	}
	
	public ListVertex getRightNext(){
		return this.rnext;
	}
	
	public void setUpNext(ListVertex upNext){
		this.upnext = upNext;
	}
	
	public ListVertex getUpNext(){
		return this.upnext;
	}
	
	   //数乘
	public static ListVertex multiPoint(double multi, ListVertex point){
		ListVertex result = new ListVertex(0.0, 0.0, 0.0); 
		double x = point.getX();
		 double y = point.getY();
		 double z = point.getZ();
		 result.setX(multi * x);
		 result.setY(multi * y);
		 result.setZ(multi * z);
	     return result;
	}
	   
	   //mark=0,两个向量相加，mark=1，前一个向量减去后一个向量
	public static ListVertex addPoint(ListVertex addPoint, ListVertex point, int mark){
		ListVertex result;
		if(mark == 0){
			result = new ListVertex(addPoint.getX() + point.getX(), addPoint.getY() + point.getY(), addPoint.getZ() + point.getZ());
			return result;
		}else{
			result = new ListVertex(addPoint.getX() - point.getX(), addPoint.getY() - point.getY(), addPoint.getZ() - point.getZ());
			return result;
		}
	}
	
}