import java.util.Scanner;
public class Notakto {
	
	//funzioni per controllare la presenza di x nelle varie direzioni
	
	public static boolean chkUpLeftUpLeft(boolean[][] brd, int x, int y){
		boolean lost = false;
		int x1 = x-1;
		int y1 = y-1;
		int x2 = x-2;
		int y2 = y-2;
		if(x1>=0 && x2>=0 && y1>=0 && y2>=0){
			if(brd[x1][y1]&&brd[x2][y2]){
				lost = true;
			}			
		}
		return lost;
	}
	
	public static boolean chkUpUp(boolean[][] brd, int x, int y){
		boolean lost = false;
		int x1 = x-1;
		int x2 = x-2;
		if(x1>=0 && x2>=0 ){
			if(brd[x1][y]&&brd[x2][y]){
				lost = true;
			}			
		}
		return lost;
	}
	
	public static boolean chkUpRightUpRight(boolean[][] brd, int x, int y,int n){
		boolean lost = false;
		int x1 = x-1;
		int y1 = y+1;
		int x2 = x-2;
		int y2 = y+2;
		if(x1>=0 && x2>=0 && y1<n && y2<n){
			if(brd[x1][y1]&&brd[x2][y2]){
				lost = true;
			}			
		}
		return lost;
	}
	
	public static boolean chkRightRight(boolean[][] brd, int x, int y, int n){
		boolean lost = false;
		int y1 = y+1;
		int y2 = y+2;
		if(y1<n && y2<n ){
			if(brd[x][y1]&&brd[x][y2]){
				lost = true;
			}			
		}
		return lost;
	}
	public static boolean chkDownRightDownRight(boolean[][] brd, int x, int y,int n){
		boolean lost = false;
		int x1 = x+1;
		int y1 = y+1;
		int x2 = x+2;
		int y2 = y+2;
		if(x1<n && x2<n && y1<n && y2<n){
			if(brd[x1][y1]&&brd[x2][y2]){
				lost = true;
			}			
		}
		return lost;
	}
	public static boolean chkDownDown(boolean[][] brd, int x, int y, int n){
		boolean lost = false;
		int x1 = x+1;
		int x2 = x+2;
		if(x1<n && x2<n ){
			if(brd[x1][y]&&brd[x2][y]){
				lost = true;
			}			
		}
		return lost;
	}
	
	public static boolean chkDownLeftDownLeft(boolean[][] brd, int x, int y,int n){
		boolean lost = false;
		int x1 = x+1;
		int y1 = y-1;
		int x2 = x+2;
		int y2 = y-2;
		if(x1<n && x2<n && y1>=0 && y2>=0){
			if(brd[x1][y1]&&brd[x2][y2]){
				lost = true;
			}			
		}
		return lost;
	}
	public static boolean chkLeftLeft(boolean[][] brd, int x, int y){
		boolean lost = false;
		int y1 = y-1;
		int y2 = y-2;
		if(y1>=0 && y2>=0 ){
			if(brd[x][y1]&&brd[x][y2]){
				lost = true;
			}			
		}
		return lost;
	}
	public static boolean chkUpLeftDownRight(boolean[][] brd, int x, int y,int n){
		boolean lost = false;
		int x1 = x-1;
		int y1 = y-1;
		int x2 = x+1;
		int y2 = y+1;
		if(x1>=0 && x2<n && y1>=0 && y2<n){
			if(brd[x1][y1]&&brd[x2][y2]){
				lost = true;
			}			
		}
		return lost;
	}
	public static boolean chkUpDown(boolean[][] brd, int x, int y,int n){
		boolean lost = false;
		int x1 = x-1;
		int x2 = x+1;
		if(x1>=0 && x2<n){
			if(brd[x1][y]&&brd[x2][y]){
				lost = true;
			}			
		}
		return lost;
	}
	public static boolean chkUpRightDownLeft(boolean[][] brd, int x, int y,int n){
		boolean lost = false;
		int x1 = x-1;
		int y1 = y+1;
		int x2 = x+1;
		int y2 = y-1;
		if(x1>=0 && x2<n && y1<n && y2>=0){
			if(brd[x1][y1]&&brd[x2][y2]){
				lost = true;
			}			
		}
		return lost;
	}
	public static boolean chkLeftRight(boolean[][] brd, int x, int y,int n){
		boolean lost = false;
		int y1 = y-1;
		int y2 = y+1;
		if(y1>=0 && y2<n){
			if(brd[x][y1]&&brd[x][y2]){
				lost = true;
			}			
		}
		return lost;
	}
	
	
	
	
	 // main
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		// inserimento board size
		System.out.println("Enter board size: ");
		int n = scan.nextInt();
		boolean [][] board = new boolean [n][n];
		
		//inizializaazione matrice
		for(int i=0; i<n;i++){
			for(int j=0; j<n;j++){
				board[i][j]= false;
			}
		}
		//ciclo infinito di gioco
		while(true){
			//stampa la tabella
			for(int i=0; i<n;i++){
				for(int j=0; j<n;j++){
					if(board[i][j]){
						System.out.print("[x] ");
					}
					else{
						System.out.print("[ ] ");
					}
				}
				System.out.println("");
			}
			
			//inserimento coordinate per la x da inserire
			System.out.println("Enter row number (1-"+(n)+"): ");
			int x = (scan.nextInt()-1);
			System.out.println("Enter column number (1-"+(n)+"): ");
			int y = (scan.nextInt()-1);
			
			if(x>=0 && x<n && y >=0 && y<n && !board[x][y]){
				board[x][y]= true;
				
				//controllo presenza di altre x
				if(chkUpLeftUpLeft(board,x,y)){
					break;
				}
				else if(chkUpUp(board,x,y)){
					break;
				}
				else if(chkUpRightUpRight(board,x,y,n)){
					break;
				}
				else if(chkRightRight(board,x,y,n)){
					break;
				}
				else if(chkDownRightDownRight(board,x,y,n)){
					break;
				}
				else if(chkDownDown(board,x,y,n)){
					break;
				}
				else if(chkDownLeftDownLeft(board,x,y,n)){
					break;
				}
				else if(chkLeftLeft(board,x,y)){
					break;
				}
				else if(chkUpLeftDownRight(board,x,y,n)){
					break;
				}
				else if(chkUpDown(board,x,y,n)){
					break;
				}
				else if(chkUpRightDownLeft(board,x,y,n)){
					break;
				}
				else if(chkLeftRight(board,x,y,n)){
					break;
				}

			}
			else{
				System.out.println("Invalid!");
			}			
		}
		// stampa finale della matrice
		for(int i=0; i<n;i++){
			for(int j=0; j<n;j++){
				if(board[i][j]){
					System.out.print("[x] ");
				}
				else{
					System.out.print("[ ] ");
				}
			}
			System.out.println("");
		}
		System.out.println("You lost!");
	}
	
	
	

}
