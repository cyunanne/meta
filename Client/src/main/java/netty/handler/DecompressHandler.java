package netty.handler;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DecompressHandler extends ChannelInboundHandlerAdapter {

    private boolean doCompress = false;
    private long fileSize = 0L;
    private long finalLength = 0L;
    private int compressionLevel = 3;
    private FileSpec fs;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {

        // 메타 데이터
        if(msg instanceof FileSpec) {
            FileSpec fs = (FileSpec) msg;
            fileSize = fs.getSize();
            doCompress = fs.isCompressed();

            if(doCompress) System.out.println("Decompressing...");

            ctx.fireChannelRead(fs);
            return;
        }

        // 파일 데이터 압축 해제
        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        if (doCompress && header.getType() == Header.TYPE_DATA) {
//            int len = header.getLength();
//
//            ByteBuf buffer = ctx.alloc().directBuffer(len * 2);
//            finalLength += decompress(data, buffer);
//            td.setDataAndLength(buffer);
//            buffer.release();

            td.setDataAndLength(decompress(data));
        }

        ctx.fireChannelRead(td);
    }

    /**
     * 압축 해제
     * @param origin before decompression
     * @param target result of decompression
     * @return length of decompressed data
     */
    private int decompress(ByteBuf origin, ByteBuf target) {

//

//        boolean test1 = origin.isDirect();
//        boolean test2 = testBuf.isDirect();
//        boolean test3 = target.isDirect();

        int len = origin.readableBytes();


        ByteBuf directBuf = origin.alloc().directBuffer(len);
        ByteBuffer originNio = directBuf.internalNioBuffer(0, len); // src
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(len * 2); // des

        boolean test4 = originNio.isDirect();

        int result = Zstd.decompress(directBuffer, originNio);

        directBuf.release();
        return result;
    }

    public static ByteBuf decompress(ByteBuf compressedData) throws IOException {
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
    }


}