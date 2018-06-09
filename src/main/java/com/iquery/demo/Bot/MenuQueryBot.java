package com.iquery.demo.Bot;

import com.baidu.dueros.bot.BaseBot;
import com.baidu.dueros.data.request.IntentRequest;
import com.baidu.dueros.data.request.LaunchRequest;
import com.baidu.dueros.data.request.SessionEndedRequest;
import com.baidu.dueros.data.response.OutputSpeech;
import com.baidu.dueros.data.response.Reprompt;
import com.baidu.dueros.data.response.card.TextCard;
import com.baidu.dueros.model.Response;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.iquery.demo.common.JeheMenuQuery.getRequest1;

public class MenuQueryBot extends BaseBot{
    /**
     * 重写BaseBot构造方法
     *
     * @param request
     *            servlet Request作为参数
     * @throws IOException
     *             抛出异常
     */
    public MenuQueryBot(HttpServletRequest request) throws IOException {
        super(request);
    }

    /**
     * 重写BaseBot构造方法
     *
     * @param request
     *            Request字符串
     * @throws IOException
     *             抛出异常
     */
    public MenuQueryBot(String request) throws IOException {
        super(request);
    }

    /**
     * 重写onLaunch方法，处理onLaunch对话事件
     *
     * @param launchRequest
     *            LaunchRequest请求体
     * @see BaseBot#onLaunch(LaunchRequest)
     */
    @Override
    protected Response onLaunch(LaunchRequest launchRequest) {

        // 新建文本卡片
        TextCard textCard = new TextCard("欢迎进入菜谱查询系统");
        // 设置链接地址
        textCard.setUrl("www:....");
        // 设置链接内容
        textCard.setAnchorText("setAnchorText");
        // 添加引导话术
        textCard.addCueWord("欢迎进入");

        // 新建返回的语音内容
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "欢迎进入菜谱查询系统");

        // 构造返回的Response
        Response response = new Response(outputSpeech, textCard);

        return response;
    }

    /**
     * 重写onInent方法，处理onInent对话事件
     *
     * @param intentRequest
     *            IntentRequest请求体
     * @see BaseBot#onInent(IntentRequest)
     */
    @Override
    protected Response onInent(IntentRequest intentRequest) {

        // 判断NLU解析的意图名称是否匹配
        if ("menu_query".equals(intentRequest.getIntentName())) {
            // 判断NLU解析解析后是否存在这个槽位
            if (getSlot("name") == null) {
                // 询问槽位name
                ask("name");
                return askName();
            } else {
                //查询结果
                return query();
            }
        }

        return null;
    }

    /**
     * 重写onSessionEnded事件，处理onSessionEnded对话事件
     *
     * @param sessionEndedRequest
     *            SessionEndedRequest请求体
     * @see BaseBot#onSessionEnded(SessionEndedRequest)
     */
    @Override
    protected Response onSessionEnded(SessionEndedRequest sessionEndedRequest) {

        // 构造TextCard
        TextCard textCard = new TextCard("感谢使用菜单查询服务");
        textCard.setAnchorText("setAnchorText");
        textCard.addCueWord("欢迎再次使用");

        // 构造OutputSpeech
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "欢迎再次使用所得税服务");

        // 构造Response
        Response response = new Response(outputSpeech, textCard);

        return response;
    }

    /**
     * 询问菜名
     *
     * @return Response 返回Response
     */
    private Response askName() {

        TextCard textCard = new TextCard("您想问哪道菜的做法呢?");
        textCard.setUrl("www:......");
        textCard.setAnchorText("链接文本");
        textCard.addCueWord("您想问哪道菜的做法呢?");

        // 设置会话信息
        setSessionAttribute("key_1", "value_1");
        setSessionAttribute("key_2", "value_2");

        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "您想问哪道菜的做法呢?");

        // 构造reprompt
        Reprompt reprompt = new Reprompt(outputSpeech);

        Response response = new Response(outputSpeech, textCard, reprompt);

        return response;
    }


    /**
     * 查询菜谱
     *
     * @return Response
     */
    private Response query() {
        // 获取槽位值
        String name = getSlot("name");
        String ret = "";

        String recipes = JSONObject.fromObject(getRequest1(name)).getString("data");
        Object data = (JSONArray.fromObject(recipes)).get(0);
        Object steps = JSONObject.fromObject(data).get("steps");

        JSONArray step = JSONArray.fromObject(steps);
        Object[] os = new Object[step.size()];
        for (int i = 0; i < os.length; i++) {
            os[i] = JSONObject.fromObject(step.get(i)).get("step");
            ret += os[i];
        }

        TextCard textCard = new TextCard(ret);
        System.out.println(ret);
        textCard.setAnchorText("setAnchorText");
        textCard.addCueWord("查询成功");

        setSessionAttribute("key_1", "value_1");
        setSessionAttribute("key_2", "value_2");

        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, ret);

        Reprompt reprompt = new Reprompt(outputSpeech);

        // 主动结束会话
        this.endDialog();

        Response response = new Response(outputSpeech, textCard, reprompt);

        return response;
    }
}