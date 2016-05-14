package DataStructure;


//三次B样条曲线（现在还没有写到曲面）
public class BSplineSurf{
	//控制网格
	private static int MAXSIZE = 100;
	private Vertex[][] controlPointBs;
    private int rowx;
    private int coly;
    private double[] knots;
    private double[] knott;
    private int knotsNumber = 0;
    private int knottNumber = 0;
    private double knotsRange;
    private double knottRange;
	
    //set knot vector
	public BSplineSurf(double[] knots, double[] knott){
		this.knots = knots;
		this.knott = knott;
		this.knotsNumber = knots.length;
		this.knottNumber = knott.length;
		this.controlPointBs = new Vertex[this.knotsNumber - 4][this.knottNumber - 4];
        this.rowx = this.knotsNumber - 4;
        this.coly = this.knottNumber - 4;
		
		this.knotsRange = this.knots[this.knotsNumber - 4] - this.knots[3];
		this.knottRange = this.knott[this.knottNumber - 4] - this.knott[3];
	}
	
	public void setControlP(Vertex point, int i, int j){
        this.controlPointBs[i][j] = point;
	}
	
	public Vertex[][] getControlPBs(){
		return this.controlPointBs;
	}
	
	public double getKnotsRange(){
		return this.knotsRange;
	}
	
	public double getKnottRange(){
		return this.knotsRange;
	}
	
	public double[] getKnots(){
		return this.knots;
	}
	
	public double[] getKnott(){
		return this.knott;
	}
	
	public double[][] NurbsBasis(double para1, double para2){
		double[][] result = new double[this.rowx][this.coly];
		double[] NurbusBasis1 = new double[this.rowx];
		double[] NurbusBasis2 = new double[this.coly];
		
		for(int i = 0; i < this.rowx; i ++){
			NurbusBasis1[i] = NurbsBasisCurve(para1, i, 3, this.knots);
		}
		
		for(int i = 0; i < this.coly; i ++){
			NurbusBasis2[i] = NurbsBasisCurve(para2, i, 3, this.knott);
		}
        
        for(int i = 0; i < this.rowx; i ++){
        	for(int j = 0; j < this.coly; j ++){
                result[i][j] = NurbusBasis1[i] * NurbusBasis2[j];
        	}
        }
        
        return result;
	}
	
	public static double NurbsBasisCurve(double para, int i, int degree, double[] knots){
			
		if(degree == 0){
			if(para >= knots[i] && para < knots[i + 1]){
				return 1;
			}else{
				return 0;
			}
		}else{
            double subRe1 = 0;
            double subRe2 = 0;
			if(para > knots[i])
			     subRe1 = (para - knots[i]) * NurbsBasisCurve(para, i, degree - 1, knots) / (knots[i + degree] - knots[i]);
			if(para < knots[i + degree + 1])
			     subRe2 = (knots[i + degree + 1] - para) * NurbsBasisCurve(para, i + 1, degree - 1, knots) / (knots[i + degree + 1] - knots[i + 1]);
			
			return subRe1 + subRe2;
		}
	}
	
	//计算B样条一个点
	public Vertex calBSpline(double s, double t){
		Vertex result = new Vertex(0.0, 0.0, 0.0, 1.0);
		if((s < this.knots[3] || s > this.knots[this.knotsNumber - 4])
				&& (t < this.knott[3] || t > this.knott[this.knottNumber - 4])){
			System.out.println("You can't find this point on surface.");
			return null;
			
		}else{
			
			double[][] NurbsBasis = NurbsBasis(s, t);
			
			for(int i = 0; i < this.rowx; i ++){
				for(int j = 0; j < this.coly; j ++){
					result = Vertex.addPoint(result, Vertex.multiPoint(NurbsBasis[i][j], this.controlPointBs[i][j]), 0);
				}
			}
			
			
			return result;
		}
	}
	
	
    //用分割递归方法求解Bezier曲线上一点
    public Vertex calDeCurve(double para, Vertex[] controlP){
    	int length = controlP.length;
    	Vertex[] result = new Vertex[length];
    	
    	for(int i = 0; i < length; i ++){
    		result[i] = controlP[i];
    	}
    	
    	int temp = length;
    	for(int i = 0; i < length; i ++){
    		for(int j = 0; j < temp - 1; j ++){
    			result[j] = this.calChaZhi(result[j], result[j + 1], para);
    		}
    		temp --;
    	}
    	
    	return result[0];
    }
	
    //求得在P1和P2之间的para处的插值
    public Vertex calChaZhi(Vertex P1, Vertex P2, double para){
    	Vertex result;
    	double x, y, z;
    	
    	x = para * P2.getX() + (1 - para) * P1.getX();
    	y = para * P2.getY() + (1 - para) * P1.getY();
    	z = para * P2.getZ() + (1 - para) * P1.getZ();
    	result = new Vertex(x, y, z, 1);
    	
    	return result;
    }
	
    //找到插入的参数在knot vector的哪个位置
	public int findIndex(double[] knot, double target, int knotNumber){
		int result;
		int i;
		if(knot[2] > target){
			System.out.println("The number you insert is too small.");
			return -100;
		}else{
		   for(i = 0; i <= knotNumber; i ++){
			   if(target >= knot[i] && target <= knot[i + 1]){
				     return i;
			   }
		}
		    System.out.println("The number you insert is too large.");
			return -100;
		}
	}
	
	public static void main(String args[]){
		double[] knots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
		double result = 0;
		for(int i = 0; i < 6; i ++){
			result += BSplineSurf.NurbsBasisCurve(3.2d, i, 3, knots);
		    System.out.println(result);
		}
	}
	
}