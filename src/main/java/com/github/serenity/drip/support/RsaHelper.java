package com.github.serenity.drip.support;

import java.math.BigInteger;

public class RsaHelper {

    // e n d 值
    private BigInteger E, D, N;

    public RsaHelper(Integer minE, Long minP, Long minN) {
        this.init(minE, minP, minN);
    }

    public RsaHelper() {
        this.init(null, null, null);
    }

    /**
     * 根据原始ID算出一个唯一对应的乱序ID
     *
     * @param input 输入数字
     * @return 随机范围小于N的一个和ID一一对应乱序数字
     */
    public long codec(long input) {
        BigInteger bin = BigInteger.valueOf(input);
        BigInteger result = bin.modPow(E, N);
        return result.longValue();
    }

    /**
     * 乱序ID解为实际原始ID
     *
     * @param input 输入数字
     * @return 原始ID
     */
    public long decode(long input) {
        BigInteger bin = BigInteger.valueOf(input);
        BigInteger result = bin.modPow(D, N);
        return result.longValue();
    }

    /**
     * 算出一套可以使用的 E/N/D， 用于数字的变换和反解
     */
    private void init(Integer minE, Long minP, Long minN) {
        // P值最小界限，取出第一个大于该值的P
        if (minP == null) minP = 312394L;
        // N值的最大界限，根据该值算出最大的Q，使P*Q小于该值
        if (minN == null) minN = (long) Integer.MAX_VALUE;
        // E值最小界限，取出第一个大于该值的E
        if (minE == null) minE = 76132;

        BigInteger range = BigInteger.valueOf(minN);
        BigInteger P = makeP(minP);
        BigInteger Q = makeQ(P, range);
        BigInteger N = P.multiply(Q);
        BigInteger OLA = makeOla(P, Q);
        BigInteger E = makeE(OLA, minE);
        BigInteger D = makeD(E, OLA);

        this.D = D;
        this.E = E;
        this.N = N;
    }

    // TOOL Gen constants
    private static BigInteger makeP(long minP) {
        return BigInteger.valueOf(minP).nextProbablePrime();
    }

    private static BigInteger makeQ(BigInteger p, BigInteger maxN) {
        long readyQ = maxN.divide(p).longValue();
        while (true) {
            if (BigInteger.valueOf(readyQ).isProbablePrime(1)) {
                return BigInteger.valueOf(readyQ);
            }
            readyQ--;
        }
    }

    private static BigInteger makeE(BigInteger OLA, int minE) {
        while (true) {
            if (BigInteger.valueOf(minE).gcd(OLA).equals(BigInteger.ONE)) {
                return BigInteger.valueOf(minE);
            }
            minE++;
        }
    }

    private static BigInteger makeD(BigInteger e, BigInteger ola) {
        return e.modInverse(ola);
    }

    private static BigInteger makeOla(BigInteger p, BigInteger q) {
        return p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    }


}
