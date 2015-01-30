package apollo.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static String crypt(String str) {
		return crypt(str, "utf-8");
	}

	public static String crypt(String str, String charsetName) {
		if (str == null || str.length() == 0) {
			throw new IllegalArgumentException(
					"String to encript cannot be null or zero length");
		}

		StringBuffer hexString = new StringBuffer();

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes(charsetName));
			byte[] hash = md.digest();

			for (int i = 0; i < hash.length; i++) {
				if ((0xff & hash[i]) < 0x10) {
					hexString.append("0"
							+ Integer.toHexString((0xFF & hash[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & hash[i]));
				}
			}
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("" + ex);
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("" + ex);
		}

		return hexString.toString();
	}
}

// public class MD5 {
// /*
// * 下面这些S11-S44实际上是一个4*4的矩阵，在原始的C实现中是用#define 实现的， 这里把它们实现成为static
// * final是表示了只读，切能在同一个进程空间内的多个 Instance间共享
// */
// static final int S11 = 7;
// static final int S12 = 12;
// static final int S13 = 17;
// static final int S14 = 22;
//
// static final int S21 = 5;
// static final int S22 = 9;
// static final int S23 = 14;
// static final int S24 = 20;
//
// static final int S31 = 4;
// static final int S32 = 11;
// static final int S33 = 16;
// static final int S34 = 23;
//
// static final int S41 = 6;
// static final int S42 = 10;
// static final int S43 = 15;
// static final int S44 = 21;
//
// static final byte[] PADDING = { -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
// 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
// 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
// 0, 0, 0, 0, 0, 0, 0 };
// /*
// * 下面的三个成员是MD5计算过程中用到的3个核心数据，在原始的C实现中 被定义到MD5_CTX结构中
// *
// */
// private long[] state = new long[4]; // state (ABCD)
// private long[] count = new long[2]; // number of bits, modulo 2^64 (lsb
// // first)
// private byte[] buffer = new byte[64]; // input buffer
//
// /*
// * digestHexStr是MD5的唯一一个公共成员，是最新一次计算结果的 16进制ASCII表示.
// */
// public String digestHexStr;
//
// /*
// * digest,是最新一次计算结果的2进制内部表示，表示128bit的MD5值.
// */
// private byte[] digest = new byte[16];
//
// /*
// * getMD5ofStr是类MD5最主要的公共方法，入口参数是你想要进行MD5变换的字符串
// * 返回的是变换完的结果，这个结果是从公共成员digestHexStr取得的．
// */
// public String encode(String inbuf) {
// md5Init();
// md5Update(inbuf.getBytes(), inbuf.length());
// md5Final();
// digestHexStr = "";
// for (int i = 0; i < 16; i++) {
// digestHexStr += byteHEX(digest[i]);
// }
// return digestHexStr;
//
// }
//
// // 这是MD5这个类的标准构造函数，JavaBean要求有一个public的并且没有参数的构造函数
// public MD5() {
// md5Init();
//
// return;
// }
//
// /* md5Init是一个初始化函数，初始化核心变量，装入标准的幻数 */
// private void md5Init() {
// count[0] = 0L;
// count[1] = 0L;
// // /* Load magic initialization constants.
//
// state[0] = 0x67452301L;
// state[1] = 0xefcdab89L;
// state[2] = 0x98badcfeL;
// state[3] = 0x10325476L;
//
// return;
// }
//
// /*
// * F, G, H ,I 是4个基本的MD5函数，在原始的MD5的C实现中，由于它们是
// * 简单的位运算，可能出于效率的考虑把它们实现成了宏，在java中，我们把它们 实现成了private方法，名字保持了原来C中的。
// */
//
// private long F(long x, long y, long z) {
// return (x & y) | ((~x) & z);
//
// }
//
// private long G(long x, long y, long z) {
// return (x & z) | (y & (~z));
//
// }
//
// private long H(long x, long y, long z) {
// return x ^ y ^ z;
// }
//
// private long I(long x, long y, long z) {
// return y ^ (x | (~z));
// }
//
// /*
// * FF,GG,HH和II将调用F,G,H,I进行近一步变换 FF, GG, HH, and II transformations for
// * rounds 1, 2, 3, and 4. Rotation is separate from addition to prevent
// * recomputation.
// */
//
// private long FF(long a, long b, long c, long d, long x, long s, long ac) {
// a += F(b, c, d) + x + ac;
// a = ((int) a << s) | ((int) a >>> (32 - s));
// a += b;
// return a;
// }
//
// private long GG(long a, long b, long c, long d, long x, long s, long ac) {
// a += G(b, c, d) + x + ac;
// a = ((int) a << s) | ((int) a >>> (32 - s));
// a += b;
// return a;
// }
//
// private long HH(long a, long b, long c, long d, long x, long s, long ac) {
// a += H(b, c, d) + x + ac;
// a = ((int) a << s) | ((int) a >>> (32 - s));
// a += b;
// return a;
// }
//
// private long II(long a, long b, long c, long d, long x, long s, long ac) {
// a += I(b, c, d) + x + ac;
// a = ((int) a << s) | ((int) a >>> (32 - s));
// a += b;
// return a;
