import java.util.Vector;

public class Node {
	int key ;
	Vector<Integer> associate ;
	
	public Node(){
		key = 0 ;
		associate = new Vector<Integer>() ;
	}
	public Node(int k, Vector<Integer> v){
		key = k ;
		associate = v ;
	}
}