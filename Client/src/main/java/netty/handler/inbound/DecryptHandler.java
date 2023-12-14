package netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
    private ByteBuf plain;
    private FileSpec fs;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        // 메타 데이터 FileSpec 생성 및 전달
        if (header.isMetadata()) {
            fs = new FileSpec(data);
            doDecrypt = fs.isEncrypt();
            ctx.fireChannelRead(fs);

            // Cipher 초기화
            if (doDecrypt) {
                logger.info("Decrypting Started: " + fs.getFilePath());
                cipher = new AES256Cipher(Cipher.DECRYPT_MODE, fs.getKey(), fs.getIv());
                plain = Unpooled.directBuffer(Header.CHUNK_SIZE);
            }

            return;
        }

        // 파일 데이터 복호화
        else if (doDecrypt && header.isData()) {
            int len = header.getLength();

            plain.clear();
            plain.ensureWritable(len + 16);
            ByteBuffer pNio = plain.internalNioBuffer(0, plain.writableBytes());

            if (header.isEof()) {
                cipher.doFinal(data.nioBuffer(), pNio);

                header.setEof(true);
                clearVariables();

                logger.info("Decrypting Finished: " + fs.getFilePath());

            } else {
                cipher.update(data.nioBuffer(), pNio);
            }

            plain.writerIndex(pNio.position());
            td.setDataAndLength(plain.duplicate());
        }

        ctx.fireChannelRead(td);
    }

    private void clearVariables() {
        cipher = null;;
    }

}