package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

public class CompressHandler extends ChannelOutboundHandlerAdapter {

    private boolean doCompress = false;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        // 메타 데이터
        if (header.getType() == Header.TYPE_META) {
            FileSpec fs = new FileSpec(data);
            doCompress = fs.isEncrypted();
        }

        // 데이터 압축
        else if(doCompress && header.getType() == Header.TYPE_DATA) {
            // TODO 데이터 압축 & TransferData 갱신
        }

        ctx.writeAndFlush(td);
    }

}
