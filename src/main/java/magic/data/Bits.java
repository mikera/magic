package magic.data;

/**
 * Class containing static bitwise utility functions
 * 
 * @author Mike
 *
 */
public final class Bits {
	
	/**
	 * Count  the number of set bits in a 32-bit integer
	 * 
	 * @param n
	 * @return Number of bits set, from 0 to 32
	 */
	public static int countSetBits(int n) {
		n=(n&(0x55555555))+((n>>>1)&0x55555555);
		n=(n&(0x33333333))+((n>>>2)&0x33333333);
		n=((n+(n>>4))&0x0F0F0F0F);
		n=((n+(n>>8)));
		n=((n+(n>>16)));
		return n&0x3F;
	}
	
	/**
	 * Alternative count set bits operation
	 * 
	 * currently seems slightly slower
	 * @param n
	 * @return
	 */
	public static int countSetBits2(int n) {
		return Integer.bitCount(n);
	}

	/**
	 * Rounds an integer up to the next power of two
	 * Leaves the value unchanged if it is already a power of two
	 * 
	 * @param n
	 * @return
	 */
	public static int roundUpToPowerOfTwo(int n) {
		n = n - 1;
		n = fillBitsRight(n);
		n = n + 1;
		return n;
	}
	
	public static long roundUpToPowerOfTwo(long n) {
		n = n - 1;
		n = fillBitsRight(n);
		n = n + 1;
		return n;
	}
	
	public static int roundDownToPowerOfTwo(int n) {
		return n & (~(fillBitsRight(n>>>1)));	
	}
	
	public static long roundDownToPowerOfTwo(long n) {
		return n & (~(fillBitsRight(n>>>1)));
	}
	
	
	/** 
	 * Returns the number of bits required to fully represent the signed number (including sign)
	 * @param a
	 * @return
	 */
	public static int significantSignedBits(long a) {
		if (a<0) a=-a-1;
		for (int i=0; i<64; i++) {
			long v=(1L<<i)-1;
			if (a<=v) return (i+1);
		}
		return 64;
	}
	
	/** 
	 * Returns the number of bits required to fully represent the unsigned number
	 * 
	 * i.e. 32 minus the number of leading binary zeros
	 *  
	 * @param a
	 * @return
	 */
	public static int significantUnsignedBits(int a) {
		return 32-countLeadingZeros(a);
	}
	
	/**
	 * Tests whether a value is a power of two when considered as an unsigned integer value
	 * 
	 * @param a
	 * @return
	 */
	public static boolean isUnsignedPowerOfTwo(long a) {
		return (a&(a-1))==0;
	}
	
	/**
	 * Counts the number of trailing zeros
	 * 
	 * @param a
	 * @return
	 */
	public static int countTrailingZeros(long a) {
		int al=(int)a;
		if (al==0) return (32+countTrailingZeros((int)(a>>>32)));
		return countTrailingZeros(al);
	}
	
	/**
	 * Count the number of trailing (low-order) zero bits in an int
	 * 
	 * Returns 32 if entire integer is zero.
	 * 
	 * @param a
	 * @return
	 */
	public static int countTrailingZeros(int a) {
		int r=0;
		if ((a&0xFFFF)==0) {r+=16; a>>>=16;}
		if ((a&0xFF)==0) {r+=8; a>>>=8;}
		if ((a&0xF)==0) {r+=4; a>>>=4;}
		if ((a&0x3)==0) {r+=2; a>>>=2;}
		if ((a&0x1)==0) {r+=1; a>>>=1;}
		if ((a&0x1)==0) {r+=1; }
		return r;
	}
	
	public static int countLeadingZeros(long a) {
		int ah=(int)(a>>>32);
		if (ah==0) return (32+countLeadingZeros((int)(a)));
		return countLeadingZeros(ah);
	}
	
	/**
	 * Count the number of leading (high order) zeros in an int
	 * 
	 * returns 32 if entire int is zero
	 * 
	 * @param a
	 * @return
	 */
	public static int countLeadingZeros(int a) {
		if (a==0) return 32;
		int r=0;
		if ((a&0xFFFF0000)==0) {r+=16;} else {a>>>=16;}
		if ((a&0xFF00)==0) {r+=8;} else {a>>>=8;}
		if ((a&0xF0)==0) {r+=4;} else {a>>>=4;}
		if ((a&0xC)==0) {r+=2;} else {a>>>=2;}
		if ((a&0x2)==0) {r+=1;} else {a>>>=1;}
		return r;
	}
	
	public static int countLeadingZeros2(int a) {
		return 32-Integer.bitCount(fillBitsRight(a));
	}
	
	/**
	 * Sign extends a number of low bits to fill an entire 32-bit integer
	 * 
	 * @param a
	 * @param bits Number of low bits to sign-extend
	 * @return
	 */
	public static int signExtend(int a, int bits) {
		int shift=32-bits;
		return (a<<shift)>>shift;
	}

	/**
	 * Returns an integer containing the lowest "1" bit only form the input value
	 * 
	 * Returns zero if no bit is set.
	 * 
	 * @param a
	 * @return
	 */
	public static int lowestSetBit(int a) {
		return (a & (-a));
	}
	
	/**
	 * Gets the index of the lowest set bit
	 * 
	 * Returns 32 if no bits set
	 * 
	 * @param a
	 * @return
	 */
	public static int lowestSetBitIndex(int a) {
		return countTrailingZeros(a);
	}
	
	/**
	 * Returns an integer containing the highest "1" bit only form the input value
	 * 
	 * Returns zero if no bit is set.
	 * 
	 * @param a
	 * @return
	 */	
	public static int highestSetBit(int a) {
		return a&(~fillBitsRight(a>>>1));
	}
	
	/**
	 * Gets the index of the highest set bit in a given int
	 * 
	 * returns -1 if no bit set
	 * 
	 * @param a
	 * @return
	 */
	public static int highestSetBitIndex(int a) {
		return 31-countLeadingZeros(a);
	}
	
	/**
	 * Gets the n'th set bit 
	 * 
	 * Returns 0 if no bit is set or if n exceeds the total number of set bits
	 * 
	 * @param a integer containing bits to search
	 * @param bitno position of bit from 1 to 32
	 * @return integer containing just the n'th set bit
	 */
	public static int getNthSetBit(int a, int bitno) {
		if (bitno<=0) return 0;
		for (int i=1; i<bitno; i++) {
			a^=(a & (-a));
		}
		return (a&(-a));
	}
	
	/**
	 * Gets the position (index) of the n'th set bit 
	 * 
	 * @param a integer containing bits to search
	 * @param bitno position of bit from 1 to 32
	 * @return integer containing just the n'th set bit
	 */
	public static int getNthSetBitIndex(int a, int bitno) {
		if (bitno<=0) return 0;
		for (int i=1; i<bitno; i++) {
			a^=(a & (-a));
		}
		return Bits.countTrailingZeros(a);
	}
	
	/**
	 * Reverses the bits of an int value
	 * 
	 * @param a
	 * @return
	 */
	public static int reverseBits(int a) {
		a = ((a >>> 1)  & 0x55555555) | ((a << 1)  & 0xAAAAAAAA);
		a = ((a >>> 2)  & 0x33333333) | ((a << 2)  & 0xCCCCCCCC);
		a = ((a >>> 4)  & 0x0F0F0F0F) | ((a << 4)  & 0xF0F0F0F0);
		a = ((a >>> 8)  & 0x00FF00FF) | ((a << 8)  & 0xFF00FF00);
		a = ((a >>> 16) & 0x0000FFFF) | ((a << 16) & 0xFFFF0000);	
		return a;
	}
	
	/**
	 * Reverses the bits of a long value
	 * 
	 * @param a
	 * @return
	 */
	public static long reverseBits(long a) {	
		return (((0xFFFFFFFFL&reverseBits((int)a)))<<32)^(0xFFFFFFFFL&reverseBits((int)(a>>>32)));
	}
	

	/**
	 * Sets all bits to the right of the highest set bit
	 * 
	 * @param n
	 * @return
	 */
	public static long fillBitsRight(long n) {
		n = n | (n >> 1);
		n = n | (n >> 2);
		n = n | (n >> 4);
		n = n | (n >> 8);
		n = n | (n >> 16);
		n = n | (n >> 32);
		return n;
	}
	
	/**
	 * Sets all bits to the right of the highest set bit
	 * 
	 * @param n
	 * @return
	 */
	public static int fillBitsRight(int n) {
		n = n | (n >> 1);
		n = n | (n >> 2);
		n = n | (n >> 4);
		n = n | (n >> 8);
		n = n | (n >> 16);
		return n;
	}
	
	/**
	 * To the right, to the right....
	 * 
	 * That's how I roll.
	 * 
	 * @param v
	 * @param count
	 * @return
	 */
	public static int rollRight(int v, int count) {
		return Integer.rotateRight(v, count);
		//count&=31;
		//v=(v>>>count)|(v<<(32-count));
		//return v;
	}
	
	/**
	 * To the left, to the left....
	 * 
	 * That's how I roll.
	 * 
	 * @param v
	 * @param count
	 * @return
	 */
	public static int rollLeft(int v, int count) {
		return Integer.rotateLeft(v, count);
		//count&=31;
		//v=(v<<count)|(v>>>(32-count));
		//return v;
	}
	
	/**
	 * Returns the bitwise parity of an integer value
	 * 
	 * @param v
	 * @return
	 */
	public static int parity(int v) {
		v ^= v >> 16;
		v ^= v >> 8;
		v ^= v >> 4;
		v &= 0xf;
		return (0x6996 >> v) & 1;
	}

	/**
	 * Decodes a long using zigzag decoding
	 * 
	 * @param n
	 * @return
	 */
	public static long zigzagDecodeLong(final long n) {
		return (n >>> 1) ^ -(n & 1);
	}

	/**
	 * Decodes an integer using zigzag decoding
	 * 
	 * @param n
	 * @return
	 */
	public static int zigzagDecodeInt(final int n) {
	return (n >>> 1) ^ -(n & 1);
	}

	/**
	 * Encodes a long using zigzag decoding, i.e. so that all positive and negative integers are mapped onto positive unsigned integers.
	 * 
	 * 0, 1, 2, 3 => 0, 2, 4, 6
	 * -1, -2, -3 => 1, 3, 5
	 * 
	 * @param n
	 * @return
	 */
	public static long zigzagEncodeLong(final long n) {
		return (n << 1) ^ (n >> 63);
	}

	/**
	 * Encodes an int using zigzag decoding, i.e. so that all positive and negative integers are mapped onto positive unsigned integers.
	 * 
	 * 0, 1, 2, 3 => 0, 2, 4, 6
	 * -1, -2, -3 => 1, 3, 5
	 * 
	 * @param n
	 * @return
	 */
	public static int zigzagEncodeInt(final int n) {
		return (n << 1) ^ (n >> 31);
	}
	
	public static int nextIntWithSameBitCount(int a) {
		int lowest = a & -a;
		int t=a+lowest; 
		
		if (t==0) return (a >>> (lowestSetBitIndex(lowest))); // overflow case
		
		int changedBits=(a^t); // changed bits from addition, i.e. lost 1s plus one new 1, starting just above lowest
		int lostCount = countSetBits(changedBits)-2; 
		int result= (t | ((1<<lostCount)-1));
		return  result;
	}
	
	/**
	 * Converts an integer value to a 32-character binary string.
	 * 
	 * @param a
	 * @return
	 */
	public static String toBinaryString(int a) {
		char[] chars= new char[32];
		for (int i=0; i<32; i++) {
			chars[i]=((a&Integer.MIN_VALUE)==0)?'0':'1';
			a<<=1;
		}
		return new String(chars);
	}
	
	/**
	 * Gets the sign bit of a floating point value
	 */
	public static int signBit(float f) {
		return (Float.floatToIntBits(f)>>>31);
	}
	
	/**
	 * Creates a long value out of high and low integer components
	 * 
	 * @param high
	 * @param low
	 * @return
	 */
	public static long toLong(int high, int low) {
		return (((long)high)<<32)|low;
	}
	
	/**
	 * Extracts the high 32-bit word from a long value
	 * 
	 * @param v
	 * @return
	 */
	public static int highWord(long v) {
		return (int)(v>>32);
	}
	
	/**
	 * Extracts the low 32-bit word from a long value
	 * @param v
	 * @return
	 */
	public static int lowWord(long v) {
		return (int)(v);
	}
	
	/**
	 * Converts an long value to a 64-character binary string.
	 * 
	 * @param a
	 * @return
	 */
	public static String toBinaryString(long a) {
		char[] chars= new char[64];
		for (int i=0; i<64; i++) {
			chars[i]=((a&Long.MIN_VALUE)==0)?'0':'1';
			a<<=1;
		}
		return new String(chars);
	}
}
