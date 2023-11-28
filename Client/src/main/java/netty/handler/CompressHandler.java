package netty.handler;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDirectBufferCompressingStream;
import com.github.luben.zstd.ZstdOutputStream;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.compression.CompressionException;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import netty.compressor.Compressor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CompressHandler extends ChannelOutboundHandlerAdapter {

    private boolean doCompress = false;
    private long compressedLength = 0L;
    private long finalLength = 0L;
    private int compressionLevel = 3;
    private FileSpec fs;



//    ByteArrayOutputStream byteArrayOutputStream;
//    ZstdOutputStream zstdOutputStream;
//    ZstdDirectBufferCompressingStream stream;
//    ByteBuffer buffer;
    ByteBuf buf;
    ByteBuffer bufNio;

    private Compressor comp;


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        // 메타 데이터
        if (header.getType() == Header.TYPE_META) {
            fs = new FileSpec(data);
            doCompress = fs.isCompressed();

            // init compressor
            if(doCompress) {
                System.out.println("Compressing...");
//                byteArrayOutputStream = new ByteArrayOutputStream();
//                zstdOutputStream = new ZstdOutputStream(byteArrayOutputStream);

                int bufferSize = ZstdDirectBufferCompressingStream.recommendedOutputBufferSize();
                buf = ctx.alloc().directBuffer(bufferSize);
                bufNio = buf.internalNioBuffer(0, buf.writableBytes());
//                stream = new ZstdDirectBufferCompressingStream(bufNio, compressionLevel);

                comp = new Compressor(bufNio, compressionLevel);
            }
        }

        // 데이터 압축
        else if(doCompress && header.getType() == Header.TYPE_DATA) {
            int len = header.getLength();
            compressedLength += len;

            // 압축 1
//            td.setDataAndLength(compress(data));

            // 압축 2, 3
//            ByteBuf target = ctx.alloc().directBuffer(len);
//            compress(data, target);
//            td.setDataAndLength(target);
//            finalLength += header.getLength();
//            target.release();

            // 압축 : 스트림
//            td.setDataAndLength(compress(data));

            // 압축 3
            buf.clear();
            bufNio.clear();

            comp.compress(data);
            buf.writerIndex(bufNio.position());
            td.setDataAndLength(buf);
            finalLength += header.getLength();

            // 마지막 블록 압축 후 압축 결과 서버에 알리기
            if(fs.getSize() == compressedLength) {
                fs.setSize(finalLength);
                ctx.writeAndFlush(new TransferData(fs));

                buf.release();
            }

        }

        ctx.writeAndFlush(td);
    }

    /**
     * 압축
     * @param origin before compression
     * @param target result of compression
     * @return length of compressed data
     */
//    private int compress(ByteBuf origin, ByteBuf target) {
//        ByteBuffer originNio = origin.internalNioBuffer(0, origin.readableBytes());
//        target.writeBytes(Zstd.compress(originNio, compressionLevel));
//        return target.readableBytes();
//    }

    /**
     * stream 압축
     * @param data
     * @return byte[]
     * @throws IOException
     */
/*    public byte[] compress(ByteBuf data) throws IOException {
        // 압축된 데이터를 저장할 ByteArrayOutputStream 생성
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // ZstdOutputStream을 이용해 압축된 데이터를 byteArrayOutputStream에 기록
//        ZstdOutputStream zstdOutputStream = new ZstdOutputStream(byteArrayOutputStream);




        int len = data.readableBytes();

        byte[] buffer = new byte[len];
        data.readBytes(buffer);
        zstdOutputStream.write(buffer);
        zstdOutputStream.flush();

        // 압축된 데이터를 ByteBuf로 변환하여 반환
        byte[] compressedBytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.reset();

        finalLength += compressedBytes.length;

        return compressedBytes;
    }*/

    /**
     * 압축 2
     * @param origin
     * @param target
     * @return
     */
/*    private int compress(ByteBuf origin, ByteBuf target) {
        int bufSize = (int)Zstd.compressBound(origin.readableBytes());
        target.ensureWritable(bufSize); // 버퍼 사이즈 확보

        ByteBuffer originNio = origin.internalNioBuffer(0, origin.readableBytes());
        ByteBuffer targetNio = target.internalNioBuffer(0, target.writableBytes());

        int result = 0;

        try {
            result = Zstd.compress(targetNio, originNio, compressionLevel);
        } catch (Exception e) {
            throw new CompressionException(e);
        }

        target.writerIndex(result);
        return result;
    }*/

    /**
     * 압축 3
     */
/*    private ByteBuf compress(ByteBuf origin) throws IOException {
        stream.compress(origin.internalNioBuffer(0, origin.readableBytes()));
        stream.flush();

        buf.writerIndex(bufNio.position());
        return buf.retain();
    }*/

}
