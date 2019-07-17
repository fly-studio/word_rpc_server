package org.fly.rpc_server.executor;

import com.google.protobuf.Message;
import com.sun.istack.Nullable;
import org.fly.core.function.FunctionUtils;
import org.fly.core.text.json.Jsonable;
import org.fly.rpc_server.setting.Setting;
import org.fly.rpc_server.struct.Request;
import org.fly.rpc_server.struct.Response;
import org.fly.rpc_server.struct.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Executor {

    private final static Logger logger = LoggerFactory.getLogger(Executor.class);

    private final Map<String, Class> classes = new HashMap<>();

    public Executor() {
    }

    public void exec(Request request)
    {
        Message message = null;
        try {

            if (request != null)
                 message = _exec(request);

        } catch (Throwable e)
        {
            logger.error(e.getMessage(), e);
            message = exception(e);
        }

        reply(request, message);
    }


    private void reply(@Nullable  Request request, @Nullable Message message)
    {
        if (request == null || request.context == null || !request.context.channel().isActive())
            return;

        Response response = new Response<Rpc.Response>();
        response.ack = request.ack;
        response.context = request.context;
        response.version = request.version;
        response.protocol = request.protocol;
        response.data = message;

        request.context.channel().writeAndFlush(response);
    }

    private Message _exec(Request request) throws Throwable
    {
        if (request == null || request.data == null)
            return null;

        Class clazz;

        if (request.data instanceof Rpc.Request)
        {
            Rpc.Request rpcRequest = (Rpc.Request) request.data;

            if (Setting.config.debug)
                logger.debug("call {}.{}({})", rpcRequest.getClassName(), rpcRequest.getFunctionName(), rpcRequest.getParametersList().toString());

            synchronized (classes)
            {
                String clazzName = "org.fly.rpc_server.executor.Api$" + rpcRequest.getClassName();
                clazz = classes.get(clazzName);

                if (clazz == null)
                {
                    clazz = Class.forName(clazzName);
                    classes.put(rpcRequest.getClassName(), clazz);
                }
            }

            Object result = FunctionUtils.callStaticMethod(clazz, rpcRequest.getFunctionName(), rpcRequest.getParametersList().toArray());

            return Rpc.Response.newBuilder()
                    .setCode(0)
                    .setData(Jsonable.Builder.toJson(Api.objectMapper, result))
                    .build();
        }

        return null;
    }

    private Message exception(Throwable e)
    {
        //Exception stack
        StackTraceElement[] elements = e.getStackTrace();
        StringJoiner sj = new StringJoiner("\n");
        for (StackTraceElement element: elements
             ) {
            sj.add(element.toString());
        }

        return Rpc.Response.newBuilder()
                .setCode(1)
                .setMessage(e.toString())
                .setData(Setting.config.debug ? sj.toString() : null)
                .build();
    }

}
