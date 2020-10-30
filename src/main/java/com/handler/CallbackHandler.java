package com.handler;

import com.alibaba.fastjson.JSONObject;
import com.config.Constant;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.service.ApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(CallbackHandler.class);

    private ApprovalService approvalService;

    public CallbackHandler(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    public void parse(JSONObject event) {
        logger.info("Event body: " + event.toJSONString());
        String eventType = event.getString("EventType");
        String type = event.getString("type");
        if ("bpms_instance_change".equals(eventType) && "finish".equals(type)) {
            // Approved
            if (event.containsKey("result") && event.getString("result").equals("agree")) {
                String processCode = event.getString("processCode");
                if (Constant.PROCESS_CODE_TRIP.equals(processCode)) {
                    processOut(event.getString("processInstanceId"));
                }
            }
        }
    }

    private void processOut(String processInstanceId) {
        OapiProcessinstanceGetResponse.ProcessInstanceTopVo outInstance = approvalService.getById(processInstanceId);
        approvalService.parseOutInstance(outInstance);
    }
}
