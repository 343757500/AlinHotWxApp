package com.mikuwxc.autoreply.wcutil;

import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class ArrayUtil {

    static class KMPMatcher {
        private byte[] bytePattern;
        private int[] failure;
        private int matchPoint;

        KMPMatcher() {
        }

        public void computeFailure4Byte(byte[] bArr) {
            this.bytePattern = bArr;
            int length = this.bytePattern.length;
            this.failure = new int[length];
            int i = 0;
            int i2 = 1;
            while (i2 < length) {
                while (i > 0 && this.bytePattern[i] != this.bytePattern[i2]) {
                    i = this.failure[i - 1];
                }
                if (this.bytePattern[i] == this.bytePattern[i2]) {
                    i++;
                }
                this.failure[i2] = i;
                i2++;
            }
        }

        public int indexOf(byte[] bArr, int i) {
            int i2 = 0;
            if (bArr.length != 0 && i <= bArr.length) {
                while (i < bArr.length) {
                    while (i2 > 0 && this.bytePattern[i2] != bArr[i]) {
                        i2 = this.failure[i2 - 1];
                    }
                    if (this.bytePattern[i2] == bArr[i]) {
                        i2++;
                    }
                    if (i2 == this.bytePattern.length) {
                        this.matchPoint = (i - this.bytePattern.length) + 1;
                        return this.matchPoint;
                    }
                    i++;
                }
            }
            return -1;
        }

        public int lastIndexOf(byte[] bArr, int i) {
            this.matchPoint = -1;
            if (bArr.length == 0 || i > bArr.length) {
                return -1;
            }
            int length = bArr.length;
            int i2 = 0;
            int i3 = i;
            int i4 = i;
            while (i3 < length) {
                while (i2 > 0 && this.bytePattern[i2] != bArr[i3]) {
                    i2 = this.failure[i2 - 1];
                }
                if (this.bytePattern[i2] == bArr[i3]) {
                    i2++;
                }
                if (i2 == this.bytePattern.length) {
                    this.matchPoint = (i3 - this.bytePattern.length) + 1;
                    if (bArr.length - i3 <= this.bytePattern.length) {
                        return this.matchPoint;
                    }
                    i2 = i3;
                    i3 = i4;
                    i4 = length;
                    length = 0;
                } else if (i4 == 0 || i3 + 1 != length) {
                    int i5 = i3;
                    i3 = i4;
                    i4 = length;
                    length = i2;
                    i2 = i5;
                } else {
                    i3 = 0;
                    length = i2;
                    i2 = -1;
                }
                i = i2 + 1;
                i2 = length;
                length = i4;
                i4 = i3;
                i3 = i;
            }
            return this.matchPoint;
        }

        public int lastIndexOfWithNoLoop(byte[] bArr, int i) {
            this.matchPoint = -1;
            if (bArr.length == 0 || i > bArr.length) {
                return -1;
            }
            int i2 = 0;
            while (i < bArr.length) {
                while (i2 > 0 && this.bytePattern[i2] != bArr[i]) {
                    i2 = this.failure[i2 - 1];
                }
                if (this.bytePattern[i2] == bArr[i]) {
                    i2++;
                }
                if (i2 == this.bytePattern.length) {
                    this.matchPoint = (i - this.bytePattern.length) + 1;
                    if (bArr.length - i <= this.bytePattern.length) {
                        return this.matchPoint;
                    }
                    i2 = 0;
                }
                i++;
            }
            return this.matchPoint;
        }
    }

    public static void append(byte[] bArr, int i, byte[] bArr2) {
        System.arraycopy(bArr2, 0, bArr, i, bArr2.length);
    }

    public static byte[] append(byte[] bArr, byte b) {
        byte[] obj = new byte[(bArr.length + 1)];
        System.arraycopy(bArr, 0, obj, 0, bArr.length);
        obj[bArr.length] = b;
        return obj;
    }

    public static byte[] append(byte[] bArr, byte[] bArr2) {
        byte[] obj = new byte[(bArr.length + bArr2.length)];
        System.arraycopy(bArr, 0, obj, 0, bArr.length);
        System.arraycopy(bArr2, 0, obj, bArr.length, bArr2.length);
        return obj;
    }

    public static byte[] arrayReplace(byte[] bArr, byte[] bArr2, byte[] bArr3, int i) throws UnsupportedEncodingException {
        int indexOf = indexOf(bArr, bArr2, i);
        if (indexOf == -1) {
            return bArr;
        }
        byte[] obj = new byte[((bArr.length + bArr3.length) - bArr2.length)];
        System.arraycopy(bArr, 0, obj, 0, indexOf);
        System.arraycopy(bArr3, 0, obj, indexOf, bArr3.length);
        System.arraycopy(bArr, bArr2.length + indexOf, obj, bArr3.length + indexOf, (bArr.length - indexOf) - bArr2.length);
        indexOf += bArr3.length;
        return obj.length - indexOf > bArr3.length ? arrayReplace(obj, bArr2, bArr3, indexOf) : obj;
    }

    public static byte[] char2byte(String str, char... cArr) {
        Charset forName = Charset.forName(str);
        CharBuffer allocate = CharBuffer.allocate(cArr.length);
        allocate.put(cArr);
        allocate.flip();
        return forName.encode(allocate).array();
    }

    public static byte[] copyOfRange(byte[] bArr, int i, int i2) {
        int i3 = i2 - i;
        if (i3 < 0) {
            throw new IllegalArgumentException(i + ">" + i2);
        }
        byte[] obj = new byte[i3];
        System.arraycopy(bArr, i, obj, 0, Math.min(bArr.length - i, i3));
        return obj;
    }

    public static int indexOf(byte[] bArr, byte[] bArr2) {
        return indexOf(bArr, bArr2, 0);
    }

    public static int indexOf(byte[] bArr, byte[] bArr2, int i) {
        KMPMatcher kMPMatcher = new KMPMatcher();
        kMPMatcher.computeFailure4Byte(bArr2);
        return kMPMatcher.indexOf(bArr, i);
    }

    public static int lastIndexOf(byte[] bArr, byte[] bArr2) {
        return lastIndexOf(bArr, bArr2, 0);
    }

    public static int lastIndexOf(byte[] bArr, byte[] bArr2, int i) {
        KMPMatcher kMPMatcher = new KMPMatcher();
        kMPMatcher.computeFailure4Byte(bArr2);
        return kMPMatcher.lastIndexOf(bArr, i);
    }
}