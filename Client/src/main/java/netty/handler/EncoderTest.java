package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.cipher.AES256Cipher;
import netty.common.Header;
import netty.common.TransferData;

import javax.crypto.Cipher;
import java.util.List;

public class EncoderTest extends MessageToMessageEncoder<TransferData> {

//    private static int BLOCK_SIZE = 8192;

    private AES256Cipher cipher = new AES256Cipher(Cipher.ENCRYPT_MODE);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, TransferData td, List<Object> list) throws Exception {
        // TODO 암호화

        Header header = td.getHeader();
        ByteBuf data = td.getData();
        
        // TODO Encrypt 비트가 1인지 확인

        byte[] plain = new byte[header.getLength()];
        data.readBytes(plain);
        byte[] enc = header.isEof() ? cipher.doFinal(plain) : cipher.update(plain);
        
        list.add(td.setData(enc));

        // ByteBuf가 넘어왔을 때
//        int len = byteBuf.readableBytes();
//        byte[] plain = new byte[len];
//        byteBuf.readBytes(plain);
//
//        byte[] enc = len < BLOCK_SIZE ? cipher.doFinal(plain) : cipher.update(plain);
//        list.add(Unpooled.wrappedBuffer(enc));
    }
}

