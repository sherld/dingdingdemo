package com.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.config.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiProcessinstanceGetRequest;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.util.AccessTokenUtil;
import com.util.LogFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalService.class);

    public OapiProcessinstanceGetResponse.ProcessInstanceTopVo getById(String id) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_PROCESSINSTANCE_GET);
            OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
            request.setProcessInstanceId(id);
            OapiProcessinstanceGetResponse response = client.execute(request, AccessTokenUtil.getToken());

            if (response.getErrcode().longValue() != 0) {
                throw new IllegalStateException("Cannot retrieve approval instance: " + response.getErrmsg());
            }

            return response.getProcessInstance();
        } catch (Exception e) {
            String errLog = LogFormatter.getKVLogData(LogFormatter.LogEvent.END,
                    LogFormatter.KeyValue.getNew("instanceId", id));
            logger.info(errLog,e);
            throw new RuntimeException(errLog, e);
        }
    }

    public void parseOutInstance(OapiProcessinstanceGetResponse.ProcessInstanceTopVo instance) {
        logger.debug("Instance data: " + JSON.toJSONString(instance));

        String reason = null;
        LocalDate startTime = null;
        LocalDate endTime = null;

        List<OapiProcessinstanceGetResponse.FormComponentValueVo> formComponentValueVos = instance.getFormComponentValues();
        for (OapiProcessinstanceGetResponse.FormComponentValueVo formComponentValueVo : formComponentValueVos) {
            if (formComponentValueVo.getName().equals("外出事由")) {
                reason = formComponentValueVo.getValue();
            }
            if (formComponentValueVo.getName().equals("外出")) {
                String value = formComponentValueVo.getValue();
                JSONArray outSuite = JSON.parseArray(value);
                for (int i = 0; i < outSuite.size(); i++) {
                    JSONObject item = outSuite.getJSONObject(i);
                    JSONObject props = item.getJSONObject("props");
                    String label = props.getString("label");
                    if (label.equals("开始时间")) {
                        String itemValue = item.getString("value");
                        startTime = LocalDate.parse(itemValue);
                    } else if (label.equals("结束时间")) {
                        String itemValue = item.getString("value");
                        endTime = LocalDate.parse(itemValue);
                    }
                }
            }
        }

        logger.info("OutInstance reason: " + reason);
        logger.info("OutInstance start time: " + startTime.toString());
        logger.info("OutInstance end time: " + endTime.toString());
    }

    public static void main(String[] args) {
        LocalDate startTime = null;
        LocalDate endTime = null;

        JSONArray outSuite = JSON.parseArray("[{\"componentName\":\"DDSelectField\",\"componentType\":\"DDSelectField\",\"props\":{\"bizAlias\":\"type\",\"holidayOptions\":[],\"id\":\"DDSelectField-K2BO2D1A\",\"label\":\"外出类型\",\"required\":true}},{\"componentName\":\"DDDateField\",\"componentType\":\"DDDateField\",\"props\":{\"bizAlias\":\"startTime\",\"holidayOptions\":[],\"id\":\"DDDateField-K2BO2D1B\",\"label\":\"开始时间\",\"placeholder\":\"请选择\",\"required\":true},\"value\":\"2020-10-23\"},{\"componentName\":\"DDDateField\",\"componentType\":\"DDDateField\",\"props\":{\"bizAlias\":\"finishTime\",\"holidayOptions\":[],\"id\":\"DDDateField-K2BO2D1C\",\"label\":\"结束时间\",\"placeholder\":\"请选择\",\"required\":true},\"value\":\"2020-10-24\"},{\"componentName\":\"NumberField\",\"componentType\":\"NumberField\",\"extValue\":\"{\\\"compressedValue\\\":\\\"1f8b0800000000000000ad904f4bc34010c5bfcb9c57499a346c722b06b1602f1a0f223d0cc9d62e6e77c3fe514ac8777737b525945841bab799373befcdaf831a45ed045ab6520d832226d0308b5c3c7263a178eba0711a2d5772294bdcfb81db2822a3e683721a0a3a74370cadd3cc40d101b6add8df6bb5abf82eeccda2248d936878e4a0566aa4a5f3f447ab051af3cceab0df0c11986c16b556c6575ef7d5e43f6351dbd1dc504fb8f76b02dc3cb170df068561043e9936de0d0af0e7414f608be62ec4384db402e5b281e2c603fa52faa3f4c40e7b133a3b260842305c71e96cc090d2a827574598e7e96f08e7494cff8370f4ef2f8447f72b234c334a2f215c937384b349847176d63e6dc9b351ff45729f1bcac52bf854ce6c2b7cf735f4df785133dd0e030000\\\",\\\"unit\\\":\\\"DAY\\\",\\\"extension\\\":\\\"{\\\\\\\"tag\\\\\\\":\\\\\\\"\\\\\\\"}\\\",\\\"_from\\\":\\\"2020-10-23\\\",\\\"pushTag\\\":\\\"\\\",\\\"detailList\\\":[{\\\"classInfo\\\":{\\\"hasClass\\\":false,\\\"sections\\\":[{\\\"endAcross\\\":0,\\\"startTime\\\":1603413000000,\\\"endTime\\\":1603445400000,\\\"startAcross\\\":0}]},\\\"workDate\\\":1603382400000,\\\"isRest\\\":false,\\\"workTimeMinutes\\\":480,\\\"approveInfo\\\":{\\\"fromAcross\\\":0,\\\"toAcross\\\":0,\\\"fromTime\\\":1603413000000,\\\"durationInDay\\\":1,\\\"toTime\\\":1603445400000,\\\"durationInHour\\\":8}},{\\\"classInfo\\\":{\\\"hasClass\\\":false,\\\"sections\\\":[{\\\"endAcross\\\":0,\\\"startTime\\\":1603499400000,\\\"endTime\\\":1603531800000,\\\"startAcross\\\":0}]},\\\"workDate\\\":1603468800000,\\\"isRest\\\":false,\\\"workTimeMinutes\\\":480,\\\"approveInfo\\\":{\\\"fromAcross\\\":0,\\\"toAcross\\\":0,\\\"fromTime\\\":1603499400000,\\\"durationInDay\\\":1,\\\"toTime\\\":1603531800000,\\\"durationInHour\\\":8}}],\\\"durationInDay\\\":2,\\\"_to\\\":\\\"2020-10-24\\\",\\\"isModifiable\\\":true,\\\"durationInHour\\\":16}\",\"props\":{\"bizAlias\":\"duration\",\"disable\":true,\"holidayOptions\":[],\"id\":\"NumberField-K2BO2D1D\",\"label\":\"时长\",\"placeholder\":\"请输入时长\",\"required\":true},\"value\":\"2\"}]");
        for (int i = 0; i < outSuite.size(); i++) {
            JSONObject item = outSuite.getJSONObject(i);
            JSONObject props = item.getJSONObject("props");
            String label = props.getString("label");
            if (label.equals("开始时间")) {
                String itemValue = item.getString("value");
                startTime = LocalDate.parse(itemValue);
            } else if (label.equals("结束时间")) {
                String itemValue = item.getString("value");
                endTime = LocalDate.parse(itemValue);
            }
        }
    }
}
