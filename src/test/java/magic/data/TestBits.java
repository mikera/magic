package magic.data;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.util.Rand;

public class TestBits {
	@Test public void testRoundUp() {
		assertEquals(16,Bits.roundUpToPowerOfTwo(15));
		assertEquals(16,Bits.roundUpToPowerOfTwo(16));
		assertEquals(32,Bits.roundUpToPowerOfTwo(17));
		assertEquals(1,Bits.roundUpToPowerOfTwo(1));
		assertEquals(0,Bits.roundUpToPowerOfTwo(0));
	}
	
	@Test public void testRoundDown() {
		assertEquals(16,Bits.roundDownToPowerOfTwo(17));
		assertEquals(16,Bits.roundDownToPowerOfTwo(31));
		assertEquals(32,Bits.roundDownToPowerOfTwo(33));
		assertEquals(2,Bits.roundDownToPowerOfTwo(2));
		assertEquals(1,Bits.roundDownToPowerOfTwo(1));
		assertEquals(0,Bits.roundDownToPowerOfTwo(0));
		
		// not sure what should happen in negative case?
	}
	
	@Test public void testSigBits() {
		assertEquals(1,Bits.significantSignedBits(0));
		assertEquals(1,Bits.significantSignedBits(-1));

		assertEquals(4,Bits.significantSignedBits(4));
		assertEquals(3,Bits.significantSignedBits(3));
		
		assertEquals(3,Bits.significantSignedBits(-4));

	}
	
	@Test public void testSignExtend() {
		assertEquals(-1,Bits.signExtend(1,1));
		assertEquals(1,Bits.signExtend(1,2));
		assertEquals(-1,Bits.signExtend(3,2));
		assertEquals(-2,Bits.signExtend(2,2));

	}
	
	@Test public void testTrailingZeros() {
		assertEquals(0,Bits.countTrailingZeros(0x1L));
		assertEquals(1,Bits.countTrailingZeros(2));
		assertEquals(16,Bits.countTrailingZeros(0xFFFF0000));
		assertEquals(0,Bits.countTrailingZeros(-1));
		assertEquals(0,Bits.countTrailingZeros(-1L));
		assertEquals(32,Bits.countTrailingZeros(0xFFFFFFFF00000000L));
		assertEquals(48,Bits.countTrailingZeros(0xFFFF000000000000L));
		assertEquals(64,Bits.countTrailingZeros(0L));
		assertEquals(32,Bits.countTrailingZeros(0));

	}
	
	@Test public void testLeadingZeros() {
		assertEquals(63,Bits.countLeadingZeros(0x1L));
		assertEquals(30,Bits.countLeadingZeros(2));
		assertEquals(0,Bits.countLeadingZeros(-1));
		assertEquals(0,Bits.countLeadingZeros(-1L));
		assertEquals(32,Bits.countLeadingZeros(0x00000000FFFFFFFFL));
		assertEquals(48,Bits.countLeadingZeros(0xFFFFL));
		assertEquals(64,Bits.countLeadingZeros(0L));
		assertEquals(32,Bits.countLeadingZeros(0));

	}
	
	@Test public void testLowestSetBit() {
		assertEquals(0,Bits.lowestSetBit(0));
		assertEquals(1,Bits.lowestSetBit(0x1FFF));
		assertEquals(0x80000000,Bits.lowestSetBit(0x80000000));
		assertEquals(2,Bits.lowestSetBit(6));
	}
	
	@Test public void testLowestSetBits() {
		assertEquals(0,Bits.lowestSetBit(0));
		assertEquals(4,Bits.lowestSetBit(12));
		assertEquals(Integer.MIN_VALUE,Bits.lowestSetBit(Integer.MIN_VALUE));
		assertEquals(1,Bits.lowestSetBit(Integer.MAX_VALUE));
		
		assertEquals(32,Bits.lowestSetBitIndex(0));
		assertEquals(0,Bits.lowestSetBitIndex(1));
		assertEquals(2,Bits.lowestSetBitIndex(4));
		assertEquals(31,Bits.lowestSetBitIndex(Integer.MIN_VALUE));
	}

	@Test public void testHighestSetBit() {
		assertEquals(0,Bits.highestSetBit(0));
		assertEquals(0x1000,Bits.highestSetBit(0x1FFF));
		assertEquals(0x80000000,Bits.highestSetBit(0x80000000));
	}
	
	@Test public void testHighestSetBits() {
		assertEquals(0,Bits.highestSetBit(0));
		assertEquals(8,Bits.highestSetBit(12));
		assertEquals(Integer.MIN_VALUE,Bits.highestSetBit(Integer.MIN_VALUE));
		assertEquals(Integer.MIN_VALUE,Bits.highestSetBit(-1));
		
		assertEquals(-1,Bits.highestSetBitIndex(0));
		assertEquals(0,Bits.highestSetBitIndex(1));
		assertEquals(2,Bits.highestSetBitIndex(4));
		assertEquals(31,Bits.highestSetBitIndex(Integer.MIN_VALUE));
	}

	@Test public void testGetNthSetBit() {
		assertEquals(1,Bits.getNthSetBit(0xFF, 1));
		assertEquals(0x80,Bits.getNthSetBit(0xFF, 8));
		assertEquals(0,Bits.getNthSetBit(0xFF, 32));
		assertEquals(0,Bits.getNthSetBit(0xFF, 9));
		assertEquals(0x80000000,Bits.getNthSetBit(0xFFFFFFFF, 32));
		assertEquals(0,Bits.getNthSetBit(0xFFFFFFFF, 33));
	}
	
	@Test public void testGetNthSetBitIndex() {
		assertEquals(0,Bits.getNthSetBitIndex(0xFF, 1));
		assertEquals(7,Bits.getNthSetBitIndex(0xFF, 8));
		
		int r=Rand.nextInt();
		int bn=Rand.d(Integer.bitCount(r));
		assertEquals(Bits.getNthSetBit(r, bn),1<<Bits.getNthSetBitIndex(r, bn));
	}
	
	@Test public void testReverse() {
		assertEquals(0x000F0000,Bits.reverseBits(0x0000F000));
		assertEquals(Integer.toHexString(((int)0xFF0FF0F0L)),Integer.toHexString(Bits.reverseBits(((int)0x0F0FF0FFL))));
		assertEquals(Long.toHexString(0x0FFFFF0F00FF0FF0L),Long.toHexString(Bits.reverseBits(0x0FF0FF00F0FFFFF0L)));
		assertEquals(((int)0xFF0FF0F0L),Bits.reverseBits(((int)0x0F0FF0FFL)));
		
		int x=Rand.nextInt();
		assertEquals(x,Bits.reverseBits(Bits.reverseBits(x)));

		long xl=Rand.nextLong();
		assertEquals(xl,Bits.reverseBits(Bits.reverseBits(xl)));
	}
	
	@Test public void testParity() {
		assertEquals(0,Bits.parity(0));
		assertEquals(1,Bits.parity(Integer.MAX_VALUE));
		assertEquals(0,Bits.parity(0x0000F000));
		assertEquals(1,Bits.parity(0x0100F000));
	}
	
	@Test public void testFloatSign() {
		assertEquals(0,Bits.signBit(1.0f));
		assertEquals(1,Bits.signBit(-1.0f));
		assertEquals(0,Bits.signBit(Float.POSITIVE_INFINITY));
		assertEquals(1,Bits.signBit(Float.NEGATIVE_INFINITY));
	}
	
	@Test public void testRoll() {
		assertEquals(0xFFF0000F,Bits.rollLeft(0x0000FFFF,20));
		assertEquals(0xFFF0000F,Bits.rollLeft(0x0000FFFF,52));
		assertEquals(0xFFF0000F,Bits.rollLeft(0xFFF0000F,0));
		
		assertEquals(0xFFF0000F,Bits.rollRight(0x0000FFFF,12));
		assertEquals(0xFFF0000F,Bits.rollRight(0x0000FFFF,44));
		assertEquals(0xFFF0000F,Bits.rollRight(0xFFF0000F,0));
	
	}

	@Test public void testCountBits() {
		assertEquals(1,Bits.countSetBits(0x00010000));
		assertEquals(32,Bits.countSetBits(-1));
		assertEquals(8,Bits.countSetBits(0xF000000F));

	}
	
	@Test public void testBinaryString() {
		assertEquals("00000000000000001111111100000000",Bits.toBinaryString(0xFF00));
		assertEquals("11111111111111111111111111111111",Bits.toBinaryString(-1));
		assertEquals("00000000000000000000000000000000",Bits.toBinaryString(0));
	}
	
	@Test public void testNextIntWithSameBitCount() {
		assertEquals(2,Bits.nextIntWithSameBitCount(1));
		assertEquals(0,Bits.nextIntWithSameBitCount(0));
		assertEquals(9,Bits.nextIntWithSameBitCount(6));
		assertEquals(1,Bits.nextIntWithSameBitCount(Integer.MIN_VALUE));
		assertEquals(0xBFFFFFFF,Bits.nextIntWithSameBitCount(Integer.MAX_VALUE));
	}
}
