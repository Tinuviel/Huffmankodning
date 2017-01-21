import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * L�ser in en fil bit f�r bit
 * @author Lovisa Col�rus
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
	 * h�mtar filen
	 * s�tter l�ngden p� fileLength
	 * s�tter pekarna p� r�tt plats
	 * l�ser in en byte
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
	 * St�nger filen
	 * @throws IOException
	 */
	public void close() throws IOException{
		this.file.close();
	}
	
	
	/**
	 * g�r en array med bits fr�n en byte via readNextbit()
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
	 * s�tter pekaren p� r�tt st�lle
	 * om den kallas med (0,0) b�rjar den om
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
	 * L�ser bits fr�n en byte, g�r igenom en hel byte
	 *  och l�ser in n�sta byte n�r den nuvarande �r slut
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
		//kollar om bit:en �r 0 eller 1 mha '&'
		boolean bitValue = ((this.placePointer & this.nextByte) != 0);
		this.placePointer = this.placePointer << 1;
		return bitValue;
		
		
		
	}
	
	
	
}
