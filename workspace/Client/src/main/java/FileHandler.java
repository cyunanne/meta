import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class FileHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // 파일 로드 및 압축
        System.out.println("FileHandler visited");

        ctx.write(msg);
        ctx.fireChannelRead((String)msg);
    }

}