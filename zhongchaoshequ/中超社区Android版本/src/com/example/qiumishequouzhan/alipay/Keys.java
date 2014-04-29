/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.example.qiumishequouzhan.alipay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

    //合作身份者id，以2088开头的16位纯数字
    public static final String DEFAULT_PARTNER = "2088311215713310";

    //收款支付宝账号
    public static final String DEFAULT_SELLER = "wisports@163.com";

    //商户私钥，自助生成
    public static final String PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALMb6Ebe+oMMA8QorC7LqMMon/X3HzfEdHbh3HhPUXcqQiB4W8DIzVxPestmj/jghgHWo2rp3I4t8MNEMgVGKJrXLgFxv2kIlFk8A7MFafRQGRgVYrV82KdX4hywo1IjCeQJND5tBiEv+JICILh0tLsIldXqs4ZH7LkwU7P023qBAgMBAAECgYB1s9VFHauLCw4+jmv4pKXW+o+EWE4Pm+7dYTg9aAKlSQQDypShcFRkYWbU3CWo7AOkmaTy0ZS7ar1Z1Sr6BE0RuqdVy7Qwnv/AKzuAn4EVDqqg2DqW7mmrxBEq95Qt0eUr5OzHuTVAd5JqJZp64tgwD8CxpFM0J0t+ExAsJe2pAQJBAODvXTbQlYRmKcn3w7PSCpykVLXWxoDlE+XYvxVmuVSCXPVBTBU4WyjRwNL7SmctrzV/7cSV84aSmID+X9MutXkCQQDL2Fpc363DqTugAh22WPMkRtqcCXpIrJcxGQo995bwm17Jz3HYAAbcdLjWSk3aDqY0a+XJ+iTGWOIXIKUZ79NJAkEAsnd4f+tTFi9w7Jw2nFAEt2/y/iugZ9hZxz4HrlVIqnKcisL6OMP0IBj8YumaiO7IWyxbXGSVSaq6cW5iXU5sEQJBAJrY5wvV/rG7RMfv05JJT6onCLmvmWzLbq6lTtpz0f1EiWTUaK6klpGxORfPqgQTTL5VmGEt2/GwY08eRUzGmcECQB855q3f24zfpWnznqLsG+a4iU22qJELfGOJCrVdCAcNHQ+6PHnHiFxcDu7EBsRy+r3vtSBZEMZXK4m+6Dpll0E=";

    public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

}