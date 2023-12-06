package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.cipher.AES256Cipher;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;

public class EncryptHandler extends ChannelOutboundHandlerAdapter {

    private long fileSize = 0L;
    private long transferred = 0L;
    private boolean doEncrypt = false;
    private ByteBuf enc;
    private AES256Cipher cipher;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        // 메타 데이터
        if (header.getType() == Header.TYPE_META) {
            FileSpec fs = new FileSpec(data);
            fileSize = fs.getOriginalFileSize();
            doEncrypt = fs.isEncrypted();

            if (doEncrypt && cipher == null) {
                System.out.println("Encrypting...");
                enc = Unpooled.directBuffer(Short.MAX_VALUE);
                cipher = new AES256Cipher(Cipher.ENCRYPT_MODE);
                fs.setKey(cipher.getKey());
                fs.setIv(cipher.getIv());
                td.setDataAndLength(fs.toByteBuf()); // key, iv 추가 된만큼 길이 증가
            }
        }

        // 파일 데이터 암호화
        if (doEncrypt && header.getType() == Header.TYPE_DATA) {
            int len = header.getLength();
            transferred += len;

            enc.clear();
            enc.ensureWritable(len + 16);
            ByteBuffer encNio = enc.internalNioBuffer(0, enc.writableBytes());

            if (header.isEof() || transferred == fileSize) {
                header.setEof(true);
                cipher.doFinal(data.nioBuffer(), encNio);
                clearVariables();
            } else {
                cipher.update(data.nioBuffer(), encNio);
            }

            enc.writerIndex(encNio.position());
            td.setDataAndLength(enc);
        }

        ctx.writeAndFlush(td);
    }

    private void clearVariables() {
        cipher = null;
        doEncrypt = false;
        transferred = 0L;
    }

}
