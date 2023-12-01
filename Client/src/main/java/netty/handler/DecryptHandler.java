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

public class DecryptHandler extends ChannelInboundHandlerAdapter {

    private boolean doDecrypt = false;
    private AES256Cipher cipher;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        // 메타 데이터 FileSpec 생성 및 전달
        if (header.getType() == Header.TYPE_META) {
            FileSpec fs = new FileSpec(byteBuf);
            doDecrypt = fs.isEncrypted();

            if (doDecrypt) {
                System.out.println("Decrypting...");
                cipher = new AES256Cipher(Cipher.DECRYPT_MODE, fs.getKey(), fs.getIv());
            }

            ctx.fireChannelRead(fs);
            return;
        }

        // 파일 데이터 복호화
        else if (doDecrypt && header.getType() == Header.TYPE_DATA) {
            int len = byteBuf.readableBytes();
            byte[] enc = new byte[len];
            byteBuf.readBytes(enc);

            byte[] plain;
            if (header.isEof()) {
                plain = cipher.doFinal(enc);
            } else {
                plain = cipher.update(enc);
            }
            ByteBuf buf = Unpooled.directBuffer(plain.length).writeBytes(plain);
            td.setDataAndLength(buf);
            buf.release();
        }

        ctx.fireChannelRead(td);
    }

}