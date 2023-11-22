package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.cipher.AES256Cipher;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import javax.crypto.Cipher;

public class DecryptHandler extends ChannelInboundHandlerAdapter {

    private long fileSize = 0L;
    private long received = 0L;
    private boolean doDecrypt = false;
    private AES256Cipher cipher;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        // 메타 데이터 FileSpec 생성 및 전달
        if(header.getType() == Header.TYPE_META) {
            FileSpec fs = new FileSpec(byteBuf);
            fileSize = fs.getSize();
            doDecrypt = fs.isEncrypted();

            if(doDecrypt) {
                cipher = new AES256Cipher(Cipher.DECRYPT_MODE);
//                cipher.setKey(fs.getKey());
//                cipher.setIv(fs.getIv());
            }

            ctx.fireChannelRead(fs);
            return;
        }

        // 파일 데이터 복호화
        else if (doDecrypt && header.getType() == Header.TYPE_DATA) {
            int len = byteBuf.readableBytes();
            received += len;

            byte[] enc = new byte[len];
            byteBuf.readBytes(enc);

            byte[] plain = received >= fileSize ? cipher.doFinal(enc) : cipher.update(enc);
            td.setDataAndLength(plain);
        }

        ctx.fireChannelRead(td);
    }

}