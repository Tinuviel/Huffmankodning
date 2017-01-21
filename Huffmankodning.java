import java.io.*;
import java.util.*;
/**
 * 
 * @author Lovisa Colérus
 * 2016
 * kodar eller decodar en fil med huffmankodning
 *
 */
public class Huffmankodning {
	public HashMap<Integer, Integer> freqOfByte;
	public File file;
	public boolean[] bitArray = new boolean[8];
	public boolean[] huffCodeArray;
	public HashMap<Integer, boolean[]> huffCodeOfByte;
	public ArrayList<Node> listOfNodes = new ArrayList<Node>();
	PriorityQueue<Node> nodes;
	public ArrayList<Node> nodeList = new ArrayList<Node>();
	
	
	
	public Huffmankodning(String fileName){
		this.file = new File(fileName);
		this.freqOfByte = new HashMap<Integer, Integer>();
		this.nodes =  new PriorityQueue<Node>();
		this.huffCodeOfByte = new HashMap<Integer, boolean[]>();
	}
	
	/**
	 * lägger in bytes i en hashmap
	 * och håller koll på hur många gånger de förekommer i filen
	 * @param b
	 */
	public static void addFrequencyOfAByte(HashMap<Integer, Integer> fq, int b){
		if(fq.containsKey(b)){
			int freq = fq.get(b)+1;
			fq.remove(b);
			fq.put(b, freq);
		}else{
			fq.put(b, 1);
		}
	}
	
	/**
	 * tar in bytes från filen och lägger in dem i hashmappen
	 * för att se vem som har högst frekvens
	 * 
	 * @throws IOException 
	 */
	public HashMap<Integer, Integer> freqOfBytes() throws IOException{
		HashMap<Integer, Integer> fq = new HashMap<>();
		RandomAccessFile raf = new RandomAccessFile(this.file, "rw");
		for(int i = 0; i<this.file.length(); ++i){
			int b = raf.read();				
			addFrequencyOfAByte(fq, b);
		}
		raf.close();
		return fq;
	}
	
	/**
	 * skapar noderna från en hashmap över noder och deras frekvens
	 * @param hm
	 */
	public ArrayList<Node> makeNodes(HashMap<Integer, Integer> fq){
		ArrayList<Node> nodeList = new ArrayList<>();
		fq.forEach((k,v) -> nodeList.add(new Node((int)k,(int)v)));
		return nodeList;
	}

	/**
	 * gör huffmanträdet över alla bytes/noder
	 * @return
	 */
	public Node makeHuffmanTree(ArrayList<Node> nodeList) throws IOException{
		Node root = null;
		Node combined = null;
		RandomAccessFile raf= new RandomAccessFile(file, "r");
		
		
		
		this.nodes = new PriorityQueue<>(nodeList);
		if(this.nodes.size() == 1){
			Node least1 = new Node(0,0);
			Node least2 = nodes.poll();
			combined = new Node(0, least1.freq+least2.freq);
			combined.left = least1;
			combined.right = least2;
			least1.parent = combined;
			least2.parent = combined;
			nodes.add(combined);
		}
		
		while(this.nodes.size() > 1){
			Node least1 = nodes.poll();
			Node least2 = nodes.poll();
			combined = new Node(0, least1.freq+least2.freq);
			combined.left = least1;
			combined.right = least2;
			least1.parent = combined;
			least2.parent = combined;
			nodes.add(combined);
		}
		
		root = combined;
		return root;
		
	
	}
	
	/**
	 * skapar en hashmap över noderna och deras huffmankoder i trädet
	 * @param n
	 * @param prelHuffCode
	 * @param depth
	 */
	public void huffmanCodes(Node n, long prelHuffCode, int depth){
		if(n.left == null && n.right == null){
			
			huffCodeArray = new boolean[depth];
			for(int i = 0; i < depth; ++i){
				int place = (int) Math.pow(2, i);
				huffCodeArray[i] = ((prelHuffCode & place )!=0);
			}
			huffCodeOfByte.put(n.b, huffCodeArray);
			
		}else{
			huffmanCodes(n.left, prelHuffCode*2 +1, depth+1);
			huffmanCodes(n.right, prelHuffCode*2, depth+1);
		}
	}
	
	
	/**
	 * kodar filen med huffmankodning
	 * @param nodeList
	 */
	public void encode(ArrayList<Node> nodeList){
		try{
			RandomAccessFile rafRead = new RandomAccessFile(this.file, "r");
			RandomAccessFile rafWrite = new RandomAccessFile(this.file+".encrypted", "rw");
			BitFileReader bfr = new BitFileReader(rafRead);
			BitFileWriter bfw = new BitFileWriter(rafWrite);
			BufferedWriter treeWriter = new BufferedWriter(new FileWriter(this.file+".tree"));
			treeWriter.flush();
			bfr.setPointer(0, 0);
			Node n = this.makeHuffmanTree(nodeList);
			huffmanCodes(n, 0, 0);
			this.writeToFiles(rafRead, bfw, treeWriter, nodeList);
			
			treeWriter.close();
			
		}catch(FileNotFoundException e){
			System.out.println("File not found");
		}catch(IOException e){
			System.out.print("Unexpected error");
		}
		
	}
	
	
	/**
	 * Skriver ut i filerna
	 * @param raf
	 * @param bfw
	 * @param treeWriter
	 * @param listOfNodes
	 * @throws IOException
	 */
	public void writeToFiles(RandomAccessFile raf, BitFileWriter bfw, BufferedWriter treeWriter, ArrayList<Node> listOfNodes) throws IOException{
		
		for(int j = 0; j < listOfNodes.size(); ++j){
			for(int i = 0; i < listOfNodes.get(j).bitArray.length ; ++i){
				if(listOfNodes.get(j).bitArray[i]){
					treeWriter.write('1');
				}else{
					treeWriter.write('0');
				}
			}
			treeWriter.append(' ');
			for(int i = huffCodeOfByte.get(listOfNodes.get(j).b).length-1; i > -1; --i){
				if(huffCodeOfByte.get(listOfNodes.get(j).b)[i]){
					treeWriter.write('1');
				}else{
					treeWriter.write('0');
				}
			}
			treeWriter.write(" " + listOfNodes.get(j).freq);
			if(!(j==listOfNodes.size()-1)){
				treeWriter.write("\n");
			}
		}
		raf.seek(0);
		for(int i = 0; i<this.file.length(); ++i){
			int b = raf.read();
			boolean[] bitArray = huffCodeOfByte.get(b);
			bfw.writeBitArray(bitArray);

			
		}		
		bfw.close();
	}
	
	
	/**
	 * dekodar filen som är kodad med huffmankodning
	 * @param nodeLi
	 * @throws IOException
	 */
	public void decode() throws IOException{
		Scanner treeReader = new Scanner(new File(this.file+".tree"), "UTF-8");
		ArrayList<Node> nodeList = new ArrayList<>();
		RandomAccessFile raf = new RandomAccessFile(this.file+".decrypted", "rw");
		BitFileReader bfr = new BitFileReader(new RandomAccessFile(this.file+".encrypted", "r"));
		BitFileWriter bfw = new BitFileWriter(raf);
		while(treeReader.hasNextLine()){
			int code = Integer.parseInt(treeReader.next(), 2);
			treeReader.next();
			int sum = treeReader.nextInt();
			nodeList.add(new Node(code, sum));
		}
		
		Node root = makeHuffmanTree(nodeList);
		raf.setLength(0);
		for(int i = 0; i < root.freq; ++i){;
			bfw.writeBitArray(root.lookUpLeafFromCode(bfr));
		}
		treeReader.close();
		bfr.close();
		bfw.close();
		raf.close();
	}

	
	public static void main(String[] args) throws IOException{
		Huffmankodning hk = new Huffmankodning(args[0]);
		
		if(args[1].equals("encode")){
			HashMap<Integer, Integer> fq = hk.freqOfBytes();
			ArrayList<Node> nodeList = hk.makeNodes(fq);
			hk.encode(nodeList);
			System.out.println("File encoded");
		}else if(args[1].equals("decode")){
			hk.decode();
			System.out.println("File decoded");
		}else{
			System.out.println("wrong second argument");
		}
		
		
		
	}
}