package DataStructure;




public class BicBezierSurf{
	//��������
	private Vertex[][] controlPoint;  
	private int row;
	private int col;
	
	
	//��ʼ����������
	public BicBezierSurf(int row, int col){
	    this.controlPoint = new Vertex[row][col];	
	    this.row = row;
	    this.col = col;
	}
	
	//����������ֵ
	public void setControlP(Vertex point, int i, int j){
		this.controlPoint[i][j] = point;
	}
	
	public Vertex[][] getControlP(){
		return this.controlPoint;
	}
	
	public int getRow(){
		return this.row;
	}
	
	public int getCol(){
	    return this.col;
	}
	
	//�������߼���ʽ��P(s,t),����Ϊ(s,t)ʱ��������ϵ�һ���㡣
	public Vertex calSurface(double s, double t){
		Vertex result;
		double[][] Bmis = new double[1][this.col];
		double[][] Bnjt = new double[this.row][1];
		for(int k = 0; k < this.col; k ++){
			Bmis[0][k] = this.calBernsPoly(this.col - 1, k, s);
		}
		for(int k = 0; k < this.row; k ++){
			Bnjt[k][0] = this.calBernsPoly(this.row - 1, k, t);
		}
		
		Vertex[][] temp = this.matrixAMulB(Bmis, this.controlPoint, 1);
		temp = this.matrixAMulB(Bnjt, temp, 0);
		
		
		if(temp.length == 1 && temp[0].length == 1){
		    result = temp[0][0];
			return result;
		}else{
			System.out.println("Error in calSurface,problem with result's col and row.");
			return null;
		}
	}
	
	//����Bernstein polynomial
    public double calBernsPoly(int n, int i, double para){
    	double result;
    	int coef = 1;
    	for(int k = n; k > n - i; k --){
    		coef = coef * k;
    	}
    	for(int k = 1; k <= i; k ++){
    		coef = coef / k;
    	}
    	
    	result = coef * Math.pow(para, i) * Math.pow(1 - para, n - i);
    	
    	return result;
    }
	
    //�÷ָ�ݹ鷽�����Bezier������һ��
    public Vertex calDeSurf(double s, double t){
    	Vertex[] result = new Vertex[this.row];
    	
    	for(int i = 0; i < this.row; i ++){
    	    result[i] = this.calDeCurve(t, this.controlPoint[i]);
    	}
    	
    	result[0] = this.calDeCurve(s, result);
    	
    	return result[0];
    }
    
    //�÷ָ�ݹ鷽�����Bezier������һ��
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
    
    public Vertex calChaZhi(Vertex P1, Vertex P2, double para){
    	Vertex result;
    	double x, y, z;
    	
    	x = para * P2.getX() + (1 - para) * P1.getX();
    	y = para * P2.getY() + (1 - para) * P1.getY();
    	z = para * P2.getZ() + (1 - para) * P1.getZ();
    	result = new Vertex(x, y, z, 1);
    	
    	return result;
    }
    
	//���㸡��������A����vertex����B(mark=1)����vertex����B���Ը���������A(otherwise)
	public Vertex[][] matrixAMulB(double[][] A, Vertex[][] B, int mark){
		int Arow,Acol,Brow,Bcol;
		//����A,B����Ĵ�С
		Arow = A.length;
		Acol = A[0].length;
		Brow = B.length;
		Bcol = B[0].length;

		if(mark == 1){
		    Vertex[][] result = new Vertex[Arow][Bcol];
		    
			if(Acol != Brow){
				System.out.println("Can't do matrix multiply!");
				return null;
			}
			
			for(int i = 0; i < Arow; i ++){
				for(int j = 0; j < Bcol; j ++){
					result[i][j] = new Vertex(0, 0, 0, 1);
				}
			}
			
			for(int i = 0; i < Arow; i ++){
				for(int j = 0; j < Bcol; j ++){
					for(int k = 0; k < Acol; k ++){
					    result[i][j].setX(result[i][j].getX() + A[i][k] * B[k][j].getX());
					    result[i][j].setY(result[i][j].getY() + A[i][k] * B[k][j].getY());
					    result[i][j].setZ(result[i][j].getZ() + A[i][k] * B[k][j].getZ());
					}
				}
			}
			return result;
			
		}else{
			Vertex[][] result = new Vertex[Brow][Acol];
			
			if(Arow != Bcol){
				System.out.println("Can't do matrix multiply!");
				return null;
			}
		
			for(int i = 0; i < Brow; i ++){
				for(int j = 0; j < Acol; j ++){
					result[i][j] = new Vertex(0, 0, 0, 1);
				}
			}
			
			for(int i = 0; i < Brow; i ++){
				for(int j = 0; j < Acol; j ++){
					for(int k = 0; k < Arow; k ++){
					    result[i][j].setX(result[i][j].getX() + B[i][k].getX() * A[k][j]);
					    result[i][j].setY(result[i][j].getY() + B[i][k].getY() * A[k][j]);
					    result[i][j].setZ(result[i][j].getZ() + B[i][k].getZ() * A[k][j]);
					}
				}
			}
			
			return result;
		}

	} 
	
	//�������A����(mark=1)���߼�ȥ(otherwise)B
	public double[][] matrixAPlusB(double[][] A, double[][] B, int mark){
		int Arow,Acol,Brow,Bcol;
		//����A,B����Ĵ�С
		Arow = A.length;
		Acol = A[0].length;
		Brow = B.length;
		Bcol = B[0].length;
		double[][] result = new double[Arow][Acol];
		
		if(Acol != Bcol || Arow != Brow){
			System.out.println("Can't do matrix plus!");
			return null;
		}
		
		for(int i = 0; i < Arow; i ++){
			for(int j = 0; j < Bcol; j ++){
				result[i][j] = 0;
			}
		}
		
		if(mark == 1){      //����ӷ�
		   for(int i = 0; i < Arow; i ++){
			   for(int j = 0; j < Bcol; j ++){
				    result[i][j] = A[i][j] + B[i][j];
			   }
		   }
	    }else{          //�������
			   for(int i = 0; i < Arow; i ++){
				   for(int j = 0; j < Bcol; j ++){
					    result[i][j] = A[i][j] - B[i][j];
				   }
			   }
		}
		
		return result;
	}
	
	
}