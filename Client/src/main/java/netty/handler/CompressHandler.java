package netty.handler;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CompressHandler extends ChannelOutboundHandlerAdapter {

    private boolean doCompress = false;
    private long compressedLength = 0L;
    private long finalLength = 0L;
    private int compressionLevel = 3;
    private FileSpec fs;



    ByteArrayOutputStream byteArrayOutputStream;
    ZstdOutputStream zstdOutputStream;


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        // 메타 데이터
        if (header.getType() == Header.TYPE_META) {
            fs = new FileSpec(data);
            doCompress = fs.isCompressed();

            if(doCompress) {
                System.out.println("Compressing...");
                byteArrayOutputStream = new ByteArrayOutputStream();
                zstdOutputStream = new ZstdOutputStream(byteArrayOutputStream);
            }
        }

        // 데이터 압축
        else if(doCompress && header.getType() == Header.TYPE_DATA) {
            int len = header.getLength();
            compressedLength += len;

            td.setDataAndLength(compress(data));

            // 마지막 블록 압축 후 압축 결과 서버에 알리기
//            if(fs.getSize() == compressedLength) {
//                fs.setSize(finalLength);
//                ctx.writeAndFlush(new TransferData(fs));
//            }

        }

        ctx.writeAndFlush(td);
    }

    /**
     * 압축
     * @param origin before compression
     * @param target result of compression
     * @return length of compressed data
     */
    private int compress(ByteBuf origin, ByteBuf target) {
        ByteBuffer originNio = origin.internalNioBuffer(0, origin.readableBytes());
        target.writeBytes(Zstd.compress(originNio, compressionLevel));
        return target.readableBytes();
    }


    public byte[] compress(ByteBuf data) throws IOException {
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
    }

}
