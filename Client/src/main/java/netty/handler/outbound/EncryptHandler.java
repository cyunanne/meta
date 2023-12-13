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

import javax.crypto.Cipher;
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

        // 메타 데이터
        if (header.isMetadata()) {
            fs = new FileSpec(data);
            fileSize = fs.getOriginalFileSize();
            doEncrypt = fs.isEncrypted();

            if (doEncrypt) {
                logger.info("Encrypting Started: " + fs.getFilePath());
                enc = Unpooled.directBuffer(Header.CHUNK_SIZE + 16);
                cipher = new AES256Cipher(Cipher.ENCRYPT_MODE);
                fs.setKey(cipher.getKey()).setIv(cipher.getIv());
                td.setDataAndLength(fs.toByteBuf()); // key, iv 추가 된만큼 길이 증가
            }
        }

        // 파일 데이터 암호화
        if (doEncrypt && header.isData()) {
            int len = header.getLength();
            transferred += len;

            enc.clear();
            enc.ensureWritable(len + 16);
            ByteBuffer encNio = enc.internalNioBuffer(0, enc.writableBytes());

            if (header.isEof() || transferred == fileSize) {
                cipher.doFinal(data.nioBuffer(), encNio);

                header.setEof(true);
                clearVariables();

                logger.info("Encrypting Finished: " + fs.getFilePath());

            } else {
                cipher.update(data.nioBuffer(), encNio);
            }

            enc.writerIndex(encNio.position());
            td.setDataAndLength(enc.duplicate());
        }

        ctx.writeAndFlush(td);
    }

    private void clearVariables() {
        cipher = null;
        doEncrypt = false;
        transferred = 0L;
    }

}
