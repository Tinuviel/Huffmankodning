import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Läser in en fil bit för bit
 * @author Lovisa Colérus
 * 2016
 *
 */
public class BitFileReader {
	private RandomAccessFile file;
	private int nextByte;
	private long fileLength;
	private int placePointer;
	private long pointer; 
	
	/**
	 * hämtar filen
	 * sätter längden på fileLength
	 * sätter pekarna på rätt plats
	 * läser in en byte
	 * @param file
	 */
	public BitFileReader(RandomAccessFile file){
		this.pointer = 0;
		this.file = file;
		this.placePointer = 1;
		try{
			this.fileLength = this.file.length();
			this.nextByte = this.file.read();
		}catch(IOException e){
			System.out.println("det gick fel");
			this.fileLength = 0;
		}
		
	}
	/**
	 * @return fileLength
	 */
	public long getFileLength (){
		return this.fileLength;
	}
	
	/**
	 * Stänger filen
	 * @throws IOException
	 */
	public void close() throws IOException{
		this.file.close();
	}
	
	
	/**
	 * gör en array med bits från en byte via readNextbit()
	 * @return
	 * @throws IOException
	 */
	public boolean[] getBitArray() throws IOException{
		boolean bitArray[] = new boolean[8];
		for(int i = 0; i < bitArray.length; ++i){
			bitArray[i] = readNextBit();
		}
		return bitArray;
	}
	
	/**
	 * sätter pekaren på rätt ställe
	 * om den kallas med (0,0) börjar den om
	 * @param bytePointer
	 * @param bytePlace
	 * @throws IOException
	 */
	public void setPointer (long bytePointer, int bytePlace) throws IOException{
		if(bytePointer <= this.fileLength){
			this.file.seek(bytePointer);
			this.pointer = bytePointer;
			this.placePointer = this.file.read();
			if(0 <= bytePlace && bytePlace <= 7){
				this.placePointer = 1 << bytePlace;
				
			}else{
				System.out.println("pointerrequest out of bounds");
			}
		}
	}
	
	
	/**
	 * Läser bits från en byte, går igenom en hel byte
	 *  och läser in nästa byte när den nuvarande är slut
	 * @return
	 * @throws IOException
	 */
	public boolean readNextBit() throws IOException{
		byte b;
		int i= 0;
		if(this.placePointer > 128){
			if(this.pointer < this.fileLength){
				this.nextByte = this.file.read();
				this.placePointer = 1;
			}else{
				System.out.println("Out of bounds");
				throw new IOException();
			}
		}
		//kollar om bit:en är 0 eller 1 mha '&'
		boolean bitValue = ((this.placePointer & this.nextByte) != 0);
		this.placePointer = this.placePointer << 1;
		return bitValue;
		
		
		
	}
	
	
	
}
