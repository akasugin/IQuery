package com.iquery.demo.Bot;

import com.baidu.dueros.bot.BaseBot;
import com.baidu.dueros.data.request.IntentRequest;
import com.baidu.dueros.data.request.LaunchRequest;
import com.baidu.dueros.data.request.SessionEndedRequest;
import com.baidu.dueros.data.response.OutputSpeech;
import com.baidu.dueros.data.response.Reprompt;
import com.baidu.dueros.data.response.card.TextCard;
import com.baidu.dueros.model.Response;
import com.iquery.demo.common.KdApiOrderDistinguish;
import com.iquery.demo.common.KdniaoTrackQueryAPI;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ExpressQueryBot extends BaseBot{
    /**
     * 重写BaseBot构造方法
     *
     * @param request
     *            servlet Request作为参数
     * @throws IOException
     *             抛出异常
     */
    public ExpressQueryBot(HttpServletRequest request) throws IOException {
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
    public ExpressQueryBot(String request) throws IOException {
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
        TextCard textCard = new TextCard("欢迎进入快递查询系统");
        // 设置链接地址
        textCard.setUrl("www:....");
        // 设置链接内容
        textCard.setAnchorText("setAnchorText");
        // 添加引导话术
        textCard.addCueWord("欢迎进入");

        // 新建返回的语音内容
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "欢迎进入快递查询系统");

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
        if ("express_query".equals(intentRequest.getIntentName())) {
            // 判断NLU解析解析后是否存在这个槽位
            if (getSlot("query_subject") == null) {
                // 询问槽位query_subject
                ask("query_subject");
                return askSubject();
            } else if (getSlot("sys.number") == null) {
                // 询问快递单号槽位
                ask("sys.number");
                return askNumber();
            } else {
                // 具体查询结果
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
        TextCard textCard = new TextCard("感谢使用快递查询服务");
        textCard.setAnchorText("setAnchorText");
        textCard.addCueWord("欢迎再次使用");

        // 构造OutputSpeech
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "欢迎再次使用快递查询服务");

        // 构造Response
        Response response = new Response(outputSpeech, textCard);

        return response;
    }

    /**
     * 询问快递单号
     *
     * @return Response 返回Response
     */
    private Response askNumber() {

        TextCard textCard = new TextCard("您的单号是多少呢?");
        textCard.setUrl("www:......");
        textCard.setAnchorText("setAnchorText");
        textCard.addCueWord("您的单号是多少呢?");

        setSessionAttribute("key_1", "value_1");
        setSessionAttribute("key_2", "value_2");

        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "您的单号是多少呢?");

        Reprompt reprompt = new Reprompt(outputSpeech);

        Response response = new Response(outputSpeech, textCard, reprompt);

        return response;
    }

    /**
     * 询问查询主体
     *
     * @return Response 返回Response
     */
    private Response askSubject() {

        TextCard textCard = new TextCard("您是想查快递么?");
        textCard.setUrl("www:......");
        textCard.setAnchorText("链接文本");
        textCard.addCueWord("您是想查快递么?");

        // 设置会话信息
        setSessionAttribute("key_1", "value_1");
        setSessionAttribute("key_2", "value_2");

        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "您是想查快递么?");

        // 构造reprompt
        Reprompt reprompt = new Reprompt(outputSpeech);

        Response response = new Response(outputSpeech, textCard, reprompt);

        return response;
    }


    /**
     * 查询单号
     *
     * @return Response
     */
    private Response query() {

        //快递单号数字部分
        String number = getSlot("sys.number");
        //快递单号前的的字母
        String letter = "";
        if(getSlot("query_letter") != null)
            letter = getSlot("query_letter");
        //快递单号
        String express_number = letter + number;

        String ret = "您的快递";

        //查询单号是否存在和单号所属物流公司
        KdApiOrderDistinguish api = new KdApiOrderDistinguish();
        try {
            String tracesjson = api.getOrderTracesByJson(express_number);
            JSONObject tracesobj = JSONObject.fromObject(tracesjson);
            JSONArray shippers = JSONArray.fromObject(tracesobj.get("Shippers"));
            JSONObject shippersarray = JSONObject.fromObject(shippers.get(0));
            Object shippercode = shippersarray.get("ShipperCode");


            KdniaoTrackQueryAPI tqapi = new KdniaoTrackQueryAPI();
            try {
                String tracks = tqapi.getOrderTracesByJson(shippercode.toString(), express_number);
                JSONObject tracksobj = JSONObject.fromObject(tracks);
                JSONArray traces = JSONArray.fromObject(tracksobj.get("Traces"));
                JSONObject recentTrace = JSONObject.fromObject(traces.get(traces.size()-1));
                Object recentAcceptStation = recentTrace.get("AcceptStation");

                ret += recentAcceptStation.toString();

            } catch (Exception e) {
                e.printStackTrace();
                ret = "出错";
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = "出错";
        }

        TextCard textCard = new TextCard(ret);
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