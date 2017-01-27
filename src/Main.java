import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class Main {
//	private static Scanner in;
	private static DataInputStream d;

	public static void main(String[] args) throws IOException {
//		Compression();
//		Decompression();
	}
	
	public static void Compression(int numOfLevels , String imagePath) throws IOException{
//		in = new Scanner(System.in);
//		System.out.println("Enter The number of quantization levels: ");
//		int numOfLevels = in.nextInt() ;
		
		numOfLevels = (int) Math.pow(2, numOfLevels) ;
		
		String path = imagePath ;
		int [][] pixels = ImageRW.readImage(path) ;
		int height = pixels.length ;
		int width = pixels[0].length ;
		
		Vector<Node> v = new Vector<Node>() ;
		Vector<Integer> pixels1D = new Vector<Integer>() ;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				pixels1D.add(pixels[i][j]) ;
			}
		}
		int sum = 0 ;
		for (int i = 0; i < pixels1D.size(); i++) {
			sum += pixels1D.elementAt(i) ;
		}
		int average = sum/(pixels1D.size()) ;
		
		Node n = new Node() ;
		n.key = average ;
		n.associate = pixels1D ;
		v.add(n);
		
		while(v.size() < numOfLevels){
			for (int i = 0; i < v.size(); i++) {
				Node N1 = new Node() ;
				Node N2 = new Node() ;
				
				N1.key = v.get(i).key -1 ;
				N2.key = v.get(i).key +1 ;
				
				v.remove(i) ;
				v.add(i, N1);
				v.add(i+1, N2);
				i++ ;
			}
			
			for (int i = 0; i < pixels1D.size(); i++) {
				int min = Integer.MAX_VALUE ;
				int res = Integer.MAX_VALUE ;
				for (int j = 0; j < v.size(); j++) {
					if(Math.abs(pixels1D.get(i) - v.get(j).key) < res){
						res = Math.abs(pixels1D.get(i) - v.get(j).key) ;
						min = v.get(j).key ;
					}
				}
				for (int j = 0; j < v.size(); j++) {
					if(v.get(j).key == min){
						v.get(j).associate.add(pixels1D.get(i)) ;
						break ;
					}
				}
			}
			
			for (int i = 0; i < v.size(); i++) {
				sum = 0 ;
				if(v.get(i).associate.size() != 0){							// handling condition
					for (int j = 0; j < v.get(i).associate.size(); j++) {
						sum += v.get(i).associate.get(j) ;
					}
					v.get(i).key = sum/v.get(i).associate.size() ;
				}
			}	
		}
		
		boolean change = false ;
		int counter = 0 ;
		while(!change && counter <= 5){
			change=false;
			Vector<Node> prev = new Vector<Node>() ;
			for (int i = 0; i < v.size(); i++) {
				prev.add(v.get(i)) ;
			}
			for (int i = 0; i < v.size(); i++) {
				v.get(i).associate.clear();
			}
			for (int i = 0; i < pixels1D.size(); i++) {
				int min = Integer.MAX_VALUE ;
				int res = Integer.MAX_VALUE ;
				for (int j = 0; j < v.size(); j++) {
					if(Math.abs(pixels1D.get(i) - v.get(j).key) < res){
						res = Math.abs(pixels1D.get(i) - v.get(j).key) ;
						min = v.get(j).key ;
					}
				}
				for (int j = 0; j < v.size(); j++) {
					if(v.get(j).key == min){
						v.get(j).associate.add(pixels1D.get(i)) ;
						break ;
					}
				}
			}
			for (int i = 0; i < v.size(); i++) {	
				sum = 0 ;
				average = 0 ;
				if(v.get(i).associate.size() != 0){								// handling condition
					for (int j = 0; j < v.get(i).associate.size(); j++) {
						sum += v.get(i).associate.get(j) ;
					}
					average = sum/v.get(i).associate.size();
					v.get(i).key = average ;
				}
			}
			for (int i = 0; i < v.size() && !change; i++) {
				if(v.get(i).associate.size() == prev.get(i).associate.size()){
					for (int j = 0; j <v.get(i).associate.size() ; j++) {
						if(v.get(i).associate.get(j) != prev.get(i).associate.get(j)){
							change = true ;
							break ;
						}
					}
				}
				else{
					change = true ;
					break ;
				}
			}	
			counter++ ;
		}
		
		Vector<Node> ranges = new Vector<Node>() ;
		Node n2 = new Node() ;
		n2.key = v.get(0).key ;
		n2.associate.add(0, 0) ;
		n2.associate.add(1, ( (0 +v.get(1).key) /2) -1) ;
		ranges.add(n2) ;
		
		for (int i = 1; i < v.size()-1; i++) {
			Node n3 = new Node() ;
			n3.key = v.get(i).key ;
			n3.associate.add(0, ranges.get(i-1).associate.get(1)+1);
			n3.associate.add(1, (v.get(i).key + v.get(i+1).key) /2);
			ranges.add(n3) ;
		}
		
		int max = 0 ;
		for (int i = 0; i < pixels1D.size(); i++) {
			if (pixels1D.get(i) > max)
				max = pixels1D.get(i) ;
		}
		Node n4 = new Node() ;
		n4.key = v.get(v.size()-1).key ;
		n4.associate.add(0, ranges.get(ranges.size()-1).associate.get(1)+1);
		n4.associate.add(1, max+1);
		ranges.add(n4) ;
		
		for (int i = 0; i < ranges.size(); i++) {
			System.out.println(ranges.get(i).key + " " + ranges.get(i).associate);
		}

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				for (int j2 = 0; j2 < ranges.size(); j2++) {
					if(pixels[i][j] >= ranges.get(j2).associate.get(0) && pixels[i][j] <= ranges.get(j2).associate.get(1)){
						pixels[i][j] = j2 ;
					}
				}
			}
		}
		
		FileOutputStream f = new FileOutputStream("pixels.txt") ;
		DataOutputStream d = new DataOutputStream(f);
		
		d.writeInt(ranges.size());
		for (int i = 0; i < ranges.size(); i++) {
			d.writeInt(ranges.get(i).key);
		}
		
		d.writeInt(width);
		d.writeInt(height);
//		System.out.println(width + "  " + height);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				d.writeInt(pixels[i][j]);			
			}
		}
		d.close();
	}
	
	public static void Decompression() throws IOException{
		
		FileInputStream f = new FileInputStream("pixels.txt") ;
		d = new DataInputStream(f);
		
		int Qsize = d.readInt() ;
		Vector<Integer> quantizer = new Vector<Integer>() ;
		int x = 0 ;
		for (int i = 0; i <Qsize; i++) {
			x = d.readInt() ;
			quantizer.add(x) ;
		}
		int width = d.readInt() ;
		int height = d.readInt() ;
		
		int[][] pixels = new int [height][width] ;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				x = d.readInt() ;
				if(x >= Qsize) break ;
				pixels[i][j] = quantizer.get(x) ;
			}	
		}
		d.close();
		String compressedImagePath = "compressed.jpg" ;
		ImageRW.writeImage(pixels, compressedImagePath , width , height);
	}
}