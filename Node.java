import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;
/**
 * Nodes for the HuffmanTree
 * @author Lovisa Colérus
 * 2016
 *
 */
public class Node implements Comparable{
	Node left;
	Node right;
	Node parent;
	int b;
	long sum;
	int freq;
	boolean[] bitArray;
		
		
	public Node(int b, int freq){
		this.b = b;
		this.freq = freq;
		this.bitArray = new boolean[8];
		this.makeBitArray();
	}

	/**
	 * makes the bitArray of the byte in the node
	 */
	public void makeBitArray(){
		int i = 1;
		int j = 0;
		while(i < 129){
			if((this.b & i) != 0){
				this.bitArray[j] = true;
			}else{
				this.bitArray[j] = false;
			}
			++j;
			i = (int) Math.pow(2, j);
			
		}
	}
	
	/**
	 * returns the node from the huffmancode
	 * @param bfr
	 * @return
	 * @throws IOException
	 */
	public boolean[] lookUpLeafFromCode(BitFileReader bfr) throws IOException{
		if(this.left == null && this.right == null){
			return bitArray;
		}else{
			boolean b = bfr.readNextBit();
			if(b){
				return this.left.lookUpLeafFromCode(bfr);
			}else{
				return this.right.lookUpLeafFromCode(bfr);
			}
		}
	}

	/**
	 * compares two nodes to eachother
	 */
	@Override
	public int compareTo(Object o){
		Node n = (Node)o;
		if(this.freq < n.freq){
			return -1;
		}else if(this.freq > n.freq){
			return 1;
		}else{
			return 0;
		}
		
	}


	
	
	
	
	
	
	
}
