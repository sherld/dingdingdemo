package com.handler;

import com.alibaba.fastjson.JSONObject;
import com.config.DingDingConstant;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.model.SFEvent;
import com.service.ApprovalService;
import com.service.SalesforceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(CallbackHandler.class);

    private ApprovalService approvalService;

    private SalesforceService salesforceService;

    public CallbackHandler(ApprovalService approvalService, SalesforceService salesforceService) {
        this.approvalService = approvalService;
        this.salesforceService = salesforceService;
    }

    public void parse(JSONObject event) {
        logger.info("Event body: " + event.toJSONString());
        String eventType = event.getString("EventType");
        String type = event.getString("type");
        if ("bpms_instance_change".equals(eventType) && "finish".equals(type)) {
            // Approved
            if (event.containsKey("result") && event.getString("result").equals("agree")) {
                String processCode = event.getString("processCode");
                if (DingDingConstant.PROCESS_CODE_TRIP.equals(processCode)) {
                    processOutInstance(event.getString("processInstanceId"));
                }
            }
        }
    }

    public void processOutInstance(String processInstanceId) {
        OapiProcessinstanceGetResponse.ProcessInstanceTopVo outInstance = approvalService.getById(processInstanceId);
        SFEvent sfEvent = approvalService.parseOutInstance(outInstance);
        salesforceService.sync(sfEvent);
    }
}
