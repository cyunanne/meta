package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.cipher.AES256Cipher;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;

public class DecryptHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf plain;
    private boolean doDecrypt = false;
    private AES256Cipher cipher;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        // 메타 데이터 FileSpec 생성 및 전달
        if (header.getType() == Header.TYPE_META) {
            FileSpec fs = new FileSpec(data);
            doDecrypt = fs.isEncrypted();

            if (doDecrypt && cipher == null) {
                System.out.println("Decrypting...");
                cipher = new AES256Cipher(Cipher.DECRYPT_MODE, fs.getKey(), fs.getIv());
                plain = Unpooled.directBuffer(Short.MAX_VALUE);
            }

            ctx.fireChannelRead(fs);
            return;
        }

        // 파일 데이터 복호화
        else if (doDecrypt && header.getType() == Header.TYPE_DATA) {
            int len = header.getLength();

            plain.clear();
            plain.ensureWritable(len + 16);
            ByteBuffer pNio = plain.internalNioBuffer(0, plain.writableBytes());

            if (header.isEof()) {
                cipher.doFinal(data.nioBuffer(), pNio);
                header.setEof(true);
                cipher = null;
            } else {
                cipher.update(data.nioBuffer(), pNio);
            }

            plain.writerIndex(pNio.position());
            td.setDataAndLength(plain);
        }

        ctx.fireChannelRead(td);
    }

}