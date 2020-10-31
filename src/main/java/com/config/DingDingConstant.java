package com.config;

/**
 * 项目中的常量定义类
 */
public class DingDingConstant {
    /**
     * 企业corpid, 需要修改成开发者所在企业
     */
    public static final String CORP_ID = "ding280bd794307b29944ac5d6980864d335";
    /**
     * 应用的AppKey，登录开发者后台，点击应用管理，进入应用详情可见
     */
    public static final String APPKEY = "dingbijn6ttzr1gqilgs";
    /**
     * 应用的AppSecret，登录开发者后台，点击应用管理，进入应用详情可见
     */
    public static final String APPSECRET = "MZTMOuCjYZmABAHPS0wfbTyY_wvfosM-eJUssA2i0DrSBXJhYOU2EgIrABWXtRva";

    /**
     * 数据加密密钥。用于回调数据的加密，长度固定为43个字符，从a-z, A-Z, 0-9共62个字符中选取,您可以随机生成
     */
    public static final String ENCODING_AES_KEY = "xxxxxxxxlvdhntotr3x9qhlbytb18zyz5zxxxxxxxxx";

    /**
     * 加解密需要用到的token，企业可以随机填写。如 "12345"
     */
    public static final String TOKEN = "12345";

    /**
     * 应用的agentdId，登录开发者后台可查看
     */
    public static final Long AGENTID = 923976441L;

    /**
     * 审批模板唯一标识，可以在审批管理后台找到
     */
    public static final String PROCESS_CODE = "***";

    /**
     * 回调host
     */
    public static final String CALLBACK_URL_HOST = "***";

    /**
     * 出差process_code
     */
    public static final String PROCESS_CODE_TRIP = "PROC-F7DE4D75-F477-4647-B89F-3B49307F5AC9";
}
