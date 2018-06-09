package com.iquery.demo.service;

import com.iquery.demo.common.MyEasyJsonUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Created with IntelliJ IDEA.
 * User: red
 * Date: 2018/3/3
 * Time: 下午5:16
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class MessageSender {
    private final static String QUEUE_NAME = "aircondition";
    public void send(Object object){
        String controlJson = MyEasyJsonUtil.json2string(object);
        try {
            //创建连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            //设置RabbitMQ相关信息
            factory.setHost("111.231.137.44");
            factory.setUsername("admin");
            factory.setPassword("123456");
            factory.setPort(5672);
            //创建一个新的连接
            Connection connection = factory.newConnection();
            //创建一个通道
            Channel channel = connection.createChannel();
            //  声明一个队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//            String message = "Hello RabbitMQ";
            //发送消息到队列中
            channel.basicPublish("", QUEUE_NAME, null, controlJson.getBytes("UTF-8"));
            System.out.println("Producer Send +'" + controlJson + "'");
            //关闭通道和连接
            channel.close();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
