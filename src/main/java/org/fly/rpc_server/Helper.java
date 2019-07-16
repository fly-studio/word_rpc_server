package org.fly.rpc_server;

import org.fly.rpc_server.executor.Api;
import org.fly.rpc_server.setting.Setting;

public class Helper {

    public static void main( String[] args )
    {
        try {

            Setting.readSettings();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println(Api.NLPTokenizer.analyze("今天也是魔法少女京子和激动得跳起来转圈圈的刺猬头伊藤甜甜的一天！"));
    }
}
