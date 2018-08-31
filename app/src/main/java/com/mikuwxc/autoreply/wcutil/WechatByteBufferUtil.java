package com.mikuwxc.autoreply.wcutil;

import java.nio.ByteBuffer;

public class WechatByteBufferUtil {
    private ByteBuffer mqn;
    private boolean mqo;

    public final int be(byte[] bArr) {
        int i = 0;
        if (bArr == null || bArr.length == 0) {
            i = 1;
        } else if (bArr[0] != (byte) 123) {
            i = 1;
        } else if (bArr[bArr.length - 1] != (byte) 125) {
            i = 1;
        } else {
            boolean z = false;
        }
        if (i != 0) {
            this.mqn = null;
            return -1;
        }
        this.mqn = ByteBuffer.wrap(bArr);
        this.mqn.position(1);
        this.mqo = false;
        return 0;
    }

    public final void bmK() throws Exception {
        if (this.mqo) {
            throw new Exception("Buffer For Build");
        }
        short s = this.mqn.getShort();
        if (s > (short) 2048) {
            this.mqn = null;
            throw new Exception("Buffer String Length Error");
        } else if (s != (short) 0) {
            this.mqn.position(s + this.mqn.position());
        }
    }

    public final boolean bmL() {
        return this.mqn.limit() - this.mqn.position() <= 1;
    }

    public final byte[] getBuffer() throws Exception {
        if (this.mqo) {
            throw new Exception("Buffer For Build");
        }
        short s = this.mqn.getShort();
        if (s > (short) 2048) {
            this.mqn = null;
            throw new Exception("Buffer String Length Error");
        } else if (s == (short) 0) {
            return new byte[0];
        } else {
            byte[] bArr = new byte[s];
            this.mqn.get(bArr, 0, s);
            return bArr;
        }
    }

    public final int getInt() throws Exception {
        if (!this.mqo) {
            return this.mqn.getInt();
        }
        throw new Exception("Buffer For Build");
    }

    public final long getLong() throws Exception {
        if (!this.mqo) {
            return this.mqn.getLong();
        }
        throw new Exception("Buffer For Build");
    }

    public final String getString() throws Exception {
        if (this.mqo) {
            throw new Exception("Buffer For Build");
        }
        short s = this.mqn.getShort();
        if (s > (short) 2048) {
            this.mqn = null;
            throw new Exception("Buffer String Length Error");
        } else if (s == (short) 0) {
            return "";
        } else {
            byte[] bArr = new byte[s];
            this.mqn.get(bArr, 0, s);
            return new String(bArr, "UTF-8");
        }
    }

    public final void tL(int i) {
        this.mqn.position(this.mqn.position() + i);
    }
}