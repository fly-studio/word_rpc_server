package org.fly.rpc_server.protocol;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.fly.rpc_server.setting.Setting;
import org.fly.rpc_server.struct.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.HexDumpEncoder;

public class TagV2Encoder extends MessageToByteEncoder<Response> {
    private final static Logger logger = LoggerFactory.getLogger(TagV2Encoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf out) throws Exception {
        if (response == null)
            return;

        Message data = response.data;

        byte[] bytes = data != null ? data.toByteArray() : new byte[0];

        out.writeShort(response.ack);
        out.writeShort(response.version);
        out.writeBytes(response.protocol);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);

        if (Setting.config.debug)
        {
            byte[] bytes1 = new byte[out.readableBytes()];
            out.duplicate().markReaderIndex().readBytes(bytes1);
            logger.debug("Send to: {} \n{}", ctx.channel().remoteAddress(), new HexDumpEncoder().encode(bytes1));
        }

    }

}
