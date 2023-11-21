package netty.handler;

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

        // 메타 데이터
        if (msg instanceof FileSpec) {
            FileSpec fs = (FileSpec) msg;
            doCompress = fs.isEncrypted();
            ctx.writeAndFlush(fs);
            return;
        }

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();

        // 데이터 압축
        if(doCompress && header.getType() == Header.TYPE_DATA) {
            // TODO 데이터 압축 & TransferData 갱신
        }

        ctx.writeAndFlush(td);
    }

}
