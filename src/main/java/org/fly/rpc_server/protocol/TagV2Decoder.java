package org.fly.rpc_server.protocol;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.fly.core.io.protobuf.ProtobufParser;
import org.fly.rpc_server.setting.Setting;
import org.fly.rpc_server.struct.Request;
import org.fly.rpc_server.struct.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.HexDumpEncoder;

import java.util.List;

public class TagV2Decoder extends ByteToMessageDecoder {
    private final static Logger logger = LoggerFactory.getLogger(TagV2Decoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        byteBuf.markReaderIndex();

        if (Setting.config.debug)
        {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.duplicate().readBytes(bytes);
            logger.debug("Recv from: {} \n{}", channelHandlerContext.channel().remoteAddress(), new HexDumpEncoder().encode(bytes));
        }

        Request<Rpc.Request> request = new Request<>();

        request.context = channelHandlerContext;
        request.ack = byteBuf.readUnsignedShort();
        request.version = byteBuf.readUnsignedShort();
        byteBuf.readBytes(request.protocol, 0,2);

        long length = byteBuf.readUnsignedInt();

        byte[] body = new byte[(int)length];
        byteBuf.readBytes(body);

        try
        {
            ProtobufParser<Rpc.Request> parser = new ProtobufParser<>(Rpc.Request.class);
            request.data = parser.deserialize(body);
        } catch (InvalidProtocolBufferException e)
        {
            request.data = null;
        }

        list.add(request);

    }
}
