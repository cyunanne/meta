package netty.handler;

import com.github.luben.zstd.ZstdDirectBufferCompressingStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import netty.compressor.Decompressor;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DecompressHandler extends ChannelInboundHandlerAdapter {

    private boolean doCompress = false;
    private long fileSize = 0L;
    private long finalLength = 0L;
    private int compressionLevel = 3;

    long received = 0L;
    private FileSpec fs;

//    ByteArrayInputStream byteArrayInputStream;
//    ZstdInputStream zstdInputStream;
//    ByteArrayOutputStream byteArrayOutputStream;

//    private ZstdDirectBufferDecompressingStream stream;
//    ByteBuf buf;
//    ByteBuffer bufNio;

    private Decompressor decomp;
    ByteBuf buf;
    ByteBuffer bufNio;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {

        // 메타 데이터
        if(msg instanceof FileSpec) {
            FileSpec fs = (FileSpec) msg;
            fileSize = fs.getSize();
            doCompress = fs.isCompressed();

            if(doCompress) {
                System.out.println("Decompressing...");

                int bufferSize = ZstdDirectBufferCompressingStream.recommendedOutputBufferSize() * 2;
                buf = ctx.alloc().directBuffer(bufferSize);
                bufNio = buf.internalNioBuffer(0, buf.writableBytes());
                decomp = new Decompressor();
            }

            ctx.fireChannelRead(fs);
            return;
        }

        // 파일 데이터 압축 해제
        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        received += header.getLength();

        if (doCompress && header.getType() == Header.TYPE_DATA) {
            buf.clear();
            bufNio.clear();

            // 원래 파일 크기 이상으로 들어옴, 큰파일 안댐
//            decomp.decompress(data, buf);
//            finalLength += buf.readableBytes();
//            td.setDataAndLength(buf.copy());

            // 데이터는 다 들어오는데 파일이 안열림 => 다 null로 저장됨
//            int cnt = decomp.decompress(data, bufNio);
//            buf.writerIndex(bufNio.position());
//            finalLength += buf.readableBytes();
//            td.setDataAndLength(buf.retain());

            // 작은파일은 되고 큰파일은 안됨
//            ByteBuffer buffer = decomp.decompress(data);
//            finalLength += buffer.position();
//            buffer.flip();
//            td.setDataAndLength(Unpooled.wrappedBuffer(buffer));

            // 됨...ㅠㅠ
            ByteBuffer buffer = decomp.decompress(data, bufNio);
            finalLength += buffer.position();
            buffer.flip();
            td.setDataAndLength(Unpooled.wrappedBuffer(buffer));

//            decomp.decompress(data, bufNio);
//            buf.writerIndex(bufNio.position());
//            finalLength += buf.readableBytes();
//            td.setDataAndLength(buf);

//            System.out.println("r : " + received);
//            System.out.println("f : " + finalLength);

            if(finalLength == fileSize) {
                buf.release();
                decomp.setFinalize(true);
            }
        }
        ctx.fireChannelRead(td);

    }

    /**
     * 압축 해제
     * @param origin before decompression
     * @param target result of decompression
     * @return length of decompressed data
     */
/*
    private int decompress(ByteBuf origin, ByteBuf target) {
        int len = origin.readableBytes();

        ByteBuf directBuf = origin.alloc().directBuffer(len);
        ByteBuffer originNio = directBuf.internalNioBuffer(0, len); // src
        ByteBuffer targetNio = target.internalNioBuffer(0, target.writableBytes());
//        ByteBuffer directBuffer = ByteBuffer.allocateDirect(len * 2); // des

        boolean test4 = originNio.isDirect();

        int result = Zstd.decompress(targetNio, originNio);

        directBuf.release();
        return result;
    }
*/

/*    public byte[] decompress(ByteBuf compressedData) throws IOException {
        // 압축 해제된 데이터를 저장할 ByteArrayOutputStream 생성

        // direct memory -> heap memory
        int len = compressedData.readableBytes();
        byte[] compData = new byte[len];
        compressedData.readBytes(compData);

        // 압축 해제를 위한 입력 스트림 생성
        byteArrayInputStream = new ByteArrayInputStream(compData);
//        byteArrayInputStream.read(compData);
        zstdInputStream = new ZstdInputStream(byteArrayInputStream);

        byte[] buffer = new byte[len];
        zstdInputStream.read(buffer);
        byteArrayOutputStream.write(buffer);

        return byteArrayOutputStream.toByteArray();
    }*/

/*    public static ByteBuf decompress(ByteBuf compressedData) throws IOException {
        // 압축 해제된 데이터를 저장할 ByteArrayOutputStream 생성
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // direct memory -> heap memory
        int len = compressedData.readableBytes();
        byte[] compData = new byte[len];
        compressedData.readBytes(compData);

        // 압축 해제를 위한 입력 스트림 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compData);
        ZstdInputStream zstdInputStream = new ZstdInputStream(byteArrayInputStream);

        byte[] buffer = new byte[len];
        zstdInputStream.read(buffer);
        byteArrayOutputStream.write(buffer);

        // 압축 해제된 데이터를 ByteBuf로 변환하여 반환
        return Unpooled.wrappedBuffer(byteArrayOutputStream.toByteArray());
    }*/

//    private ByteBuf decompress(ByteBuf compressedData) throws IOException {
//
//
//        int result = stream.read(bufNio);
//        buf.setIndex(result, result);
////        buf.writerIndex(result);
////        buf.readerIndex(result);
////        stream.flush();
//
////        buf.writerIndex(bufNio.position());
////        return buf.retain();
//
//        return buf.retain();
//    }


}