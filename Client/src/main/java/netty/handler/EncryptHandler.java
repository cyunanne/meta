package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.cipher.AES256Cipher;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import javax.crypto.Cipher;

public class EncryptHandler extends ChannelOutboundHandlerAdapter {

    private long fileSize = 0L;
    private long transferred = 0L;
    private boolean doEncrypt = false;

    private AES256Cipher cipher;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        // 메타 데이터
        if (header.getType() == Header.TYPE_META) {
            FileSpec fs = new FileSpec(data);
            fileSize = fs.getSize();
            doEncrypt = fs.isEncrypted();

            if(doEncrypt) {
                cipher = new AES256Cipher(Cipher.ENCRYPT_MODE);
                fs.setKey(cipher.getKey());
                fs.setIv(cipher.getIv());
                td.setDataAndLength(fs.toByteBuf()); // key, iv 추가 된만큼 길이 증가
            }
        }

        // 파일 데이터 암호화
        else if (doEncrypt && header.getType() == Header.TYPE_DATA) {
            int len = header.getLength();
            transferred += len;

            byte[] plain = new byte[len];
            data.readBytes(plain);

            byte[] enc = transferred == fileSize ? cipher.doFinal(plain) : cipher.update(plain);
            td.setDataAndLength(enc); // 암호화 후 데이터 길이가 달라질 수 있음
        }

        ctx.writeAndFlush(td);
    }

}

