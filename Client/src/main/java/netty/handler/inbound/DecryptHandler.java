package netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.cipher.AES256Cipher;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;

public class DecryptHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(DecryptHandler.class);
    private boolean doDecrypt = false;
    private AES256Cipher cipher;
    private ByteBuf plain; // 복호화 된 데이터
    private FileSpec fs;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        switch (header.getType()) {

            case Header.TYPE_META:
                fs = new FileSpec(data);
                doDecrypt = fs.isEncrypt();
                if (doDecrypt) initCipher();
                break;

            case Header.TYPE_DATA:
                if (!doDecrypt) break;
                decrypt(header, data);
                td.setDataAndLength(plain.duplicate());
                if (header.isEof()) clearProperties(); // 파일 끝
                break;

            case Header.TYPE_SIG: break;
            case Header.TYPE_MSG: break;

            default: logger.error("알 수 없는 데이터 타입");
        }

        ctx.fireChannelRead(td);
    }

    private void initCipher() {
        cipher = new AES256Cipher(Cipher.DECRYPT_MODE, fs.getKey(), fs.getIv());
        plain = Unpooled.directBuffer(Header.CHUNK_SIZE);
        logger.debug("Cipher for decryption has been created: " + fs.getFilePath());
        logger.info("Decryption has been Started: " + fs.getFilePath()); // 위치 이동 예정
    }

    private void decrypt(Header header, ByteBuf data) throws Exception {
        plain.clear().ensureWritable(header.getLength() + 16);
        ByteBuffer pNio = plain.internalNioBuffer(0, plain.writableBytes());

        if (header.isEof()) cipher.doFinal(data.nioBuffer(), pNio);
        else                cipher.update(data.nioBuffer(), pNio);

        plain.writerIndex(pNio.position());
    }

    private void clearProperties() {
        cipher = null;
        ReferenceCountUtil.release(plain);
        logger.info("Decryption finished: " + fs.getFilePath());
    }

}