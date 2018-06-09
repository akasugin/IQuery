package com.iquery.demo.controller;

import com.iquery.demo.Bot.AirConditionBot;
import com.iquery.demo.Bot.ExpressQueryBot;
import com.iquery.demo.Bot.MenuQueryBot;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class IQueryController {
    @RequestMapping(value = "/expressQuery")
    public String expressQuery(@RequestBody String data) throws IOException {
        // 根据request创建Bot
        ExpressQueryBot bot = new ExpressQueryBot(data);

        // 打开签名验证
//         bot.enableVerify();

        // 线下调试时，可以关闭签名验证
        bot.disableVerify();

        try {
//            // 调用bot的run方法
            String responseJson = bot.run();
            return responseJson;
        } catch (Exception e) {
            return "{\"status\":1,\"msg\":\"\"}";
        }
    }

    @RequestMapping(value = "/menuQuery")
    public String menuQuery(@RequestBody String data) throws IOException {
        // 根据request创建Bot
        MenuQueryBot bot = new MenuQueryBot(data);

        // 打开签名验证
        // bot.enableVerify();

//        线下调试时，可以关闭签名验证
        bot.disableVerify();

        try {
            // 调用bot的run方法
            String responseJson = bot.run();
            return responseJson;
        } catch (Exception e) {
            return "{\"status\":1,\"msg\":\"\"}";
        }
    }

    @RequestMapping(value = "/airCondition")
    public String AirCondition(@RequestBody String data) throws IOException {
        // 根据request创建Bot
        AirConditionBot bot = new AirConditionBot(data);

        // 打开签名验证
        // bot.enableVerify();

//        线下调试时，可以关闭签名验证
        bot.disableVerify();

        try {
            // 调用bot的run方法
            String responseJson = bot.run();
            return responseJson;
        } catch (Exception e) {
            return "{\"status\":1,\"msg\":\"\"}";
        }
    }
}
