import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 
 * @author Lovisa Col�rus
 * 2016
 *
 */
public class BitFileWriter {
	private RandomAccessFile file;
	private int nextByte;
	private int placePointer;

	/**
	 * Skriver en fil bit f�r bit
	 * @param file
	 */
	public BitFileWriter(RandomAccessFile file){
		try{
			file.setLength(0);
		}catch(IOException e){
			System.out.println("kunde inte s�tta fill�ngden till 0");
		}
		this.file = file;
		this.nextByte = 0;
		this.placePointer = 1;
	}
	
	/**
	 * writes a bit to a file
	 * @param bit
	 * @throws IOException
	 */
	public void writeNextBit(boolean bit) throws IOException{
		if(bit){
			this.nextByte = this.placePointer | this.nextByte;
		}
		this.placePointer = this.placePointer << 1;
		if(this.placePointer > 128){
			this.file.writeByte(this.nextByte);
			this.placePointer = 1;
			this.nextByte = 0;
		}
			
		
	}
	
	public void writeBitArray(boolean[] bitArray) throws IOException{
		for(int i = bitArray.length-1; i>-1; --i){
			writeNextBit(bitArray[i]);
		}
	}
	
	/**
	 * st�nger filen
	 * @throws IOException
	 */
	public void close() throws IOException{
		while(this.placePointer != 1){
			writeNextBit(false);
		}
		this.file.close();
	}
	
}
