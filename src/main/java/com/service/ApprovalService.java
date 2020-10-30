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
        logger.debug("Calling ApprovalService.getById()");
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
        logger.debug("Calling ApprovalService.parseOutInstance()");
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

}
