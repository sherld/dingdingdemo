package com.handler;

import com.service.ApprovalService;
import com.service.SalesforceService;
import org.junit.Before;
import org.junit.Test;

public class CallbackHandlerTest {

    private ApprovalService approvalService;

    private SalesforceService salesforceService;

    private CallbackHandler callbackHandler;

    @Before
    public void setUp() {
        approvalService = new ApprovalService();
        salesforceService = new SalesforceService();
        callbackHandler = new CallbackHandler(approvalService, salesforceService);
    }


    @Test
    public void testProcessOut() {
        // given
        String processInstanceId = "f461cb5d-869d-4efb-85e7-031e2750b875";

        // when
        callbackHandler.processOutInstance(processInstanceId);

        // then
    }
}
