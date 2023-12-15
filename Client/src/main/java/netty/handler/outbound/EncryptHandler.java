package netty.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.cipher.AES256Cipher;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.nio.ByteBuffer;

public class EncryptHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(EncryptHandler.class);
    private long fileSize = 0L;
    private long transferred = 0L;
    private boolean doEncrypt = false;
    private ByteBuf enc;
    private AES256Cipher cipher;
    private FileSpec fs;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        switch (header.getType()) {

            case Header.TYPE_META:
                fs = new FileSpec(data);
                fileSize = fs.getCurrentFileSize();
                doEncrypt = fs.isEncrypt();
                if (doEncrypt) initCipher();
                td.setDataAndLength(fs.toByteBuf()); // key, iv 추가 된만큼 길이 증가
                break;

            case Header.TYPE_DATA:
                if (!doEncrypt) break;
                encrypt(header, data);
                td.setDataAndLength(enc.duplicate());
                if (header.isEof()) clearProperties(); // 파일 끝
                break;

            case Header.TYPE_SIG: break;
            case Header.TYPE_MSG: break;

            default: logger.error("알 수 없는 데이터 타입");
        }

        ctx.writeAndFlush(td);
    }

    private void initCipher() {
        enc = Unpooled.directBuffer(Header.CHUNK_SIZE + 16);
        cipher = new AES256Cipher(Cipher.ENCRYPT_MODE);
        fs.setKey(cipher.getKey()).setIv(cipher.getIv());
        logger.debug("Cipher for encryption has been created: " + fs.getFilePath());
        logger.info("Encryption has been Started: " + fs.getFilePath()); // 위치 이동 예정
    }

    private void encrypt(Header header, ByteBuf data) throws Exception {
        transferred += header.getLength();
        enc.clear().ensureWritable(header.getLength() + 16);
        ByteBuffer encNio = enc.internalNioBuffer(0, enc.writableBytes());

        if (header.isEof() || transferred == fileSize) { // 압축된 파일은 transferred != fileSize
            cipher.doFinal(data.nioBuffer(), encNio);
            header.setEof(true);
        } else {
            cipher.update(data.nioBuffer(), encNio);
        }
        enc.writerIndex(encNio.position());
    }

    private void clearProperties() {
        cipher = null;
        doEncrypt = false;
        transferred = 0L;
        logger.info("Encryption Finished: " + fs.getFilePath());
    }

}