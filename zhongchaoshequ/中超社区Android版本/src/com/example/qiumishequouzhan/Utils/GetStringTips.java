package com.example.qiumishequouzhan.Utils;

/**
 * Created by Manc on 14-4-15.
 */
public class GetStringTips {
    public static String GetString(int errcode)
    {
        String mesg = "";
        switch (errcode) {
            case 1:
                mesg = "没有赋值";
                break;
            case 3:
                mesg = "验证码错误";
                break;
            case 5:
                mesg = "用户不存在";
                break;
            case 7:
                mesg = "密码错误";
                break;
            case 9:
                mesg = "此邮箱已经存在!";
                break;
            case 11:
                mesg = "用户昵称重复";
                break;
            case 13:
                mesg = "系统错误";
                break;
            case 15:
                mesg = "邮箱错误不符合规则";
                break;
            case 17:
                mesg = "用户昵称不符合规则";
                break;
            case 18:
                mesg = "用户密码不符合规范";
                break;
            case 20:
                mesg = "用户输入两次密码不等";
                break;
            case 22:
                mesg = "用户不在线";
                break;
            case 24:
                mesg = "用户没有在阵营中";
                break;
            case 26:
                mesg = "消息过长";
                break;
            case 28:
                mesg = "阵营不存在";
                break;
            case 30:
                mesg = "用户已经在该阵营中";
                break;
            case 32:
                mesg = "输入了一个错误的值";
                break;
            case 34:
                mesg = "用户金币不足";
                break;
            case 36:
                mesg = "用户参加的约会信息过期";
                break;
            case 38:
                mesg = "约会已关闭";
                break;
            case 40:
                mesg = "这个约会不是自己的，所以不能关闭";
                break;
            case 42:
                mesg = "用户没有联系方式";
                break;
            case 44:
                mesg = "不能参加自己的约会";
                break;
            case 46:
                mesg = "用户已支持过";
                break;
            case 47:
                mesg = "没有这个约会";
                break;
            case 49:
                mesg = "不是正确的约会类型";
                break;
            case 51:
                mesg = "填写的购买物品列表为空";
                break;
            case 53:
                mesg = "这个商品不存在";
                break;
            case 55:
                mesg = "文件不存在";
                break;
            case 57:
                mesg = "不存在的照片信息";
                break;
            case 59:
                mesg = "照片不属于此用户";
                break;
            case 61:
                mesg = "不存在的竞猜序号";
                break;
            case 63:
                mesg = "竞猜消息以完成";
                break;
            case 65:
                mesg = "用户相册已满";
                break;
            case 66:
                mesg = "电话号码错误";
                break;
            case 67:
                mesg = "商品已售完";
                break;
            case 68:
                mesg = "用户已领取金币请勿重复领取";
                break;
            case 69:
                mesg = "不存在的第三方类型";
                break;
            case 70:
                mesg = "球星不存在";
                break;
            case 71:
                mesg = "用户已经买过球星";
                break;
            case 72:
                mesg = "输入有误";
                break;
            case 73:
                mesg = "金币溢出";
                break;
            case 74:
                mesg = "用户可以卖出的球星数量不足";
                break;
            case 76:
                mesg = "竞拍不存在";
                break;
            case 77:
                mesg = "竞拍结束";
                break;
            case 78:
                mesg = "输入的消息编号错误";
                break;
            case 79:
                mesg = "权限不足";
                break;
            case 80:
                mesg = "任务奖励已经领取过";
                break;
            case 81:
                mesg = "任务没有完成";
                break;
            case 82:
                mesg = "目前没有这个任务";
                break;
            case 83:
                mesg = "不允许加入的阵营";
                break;
            case 84:
                mesg = "错误的赛程";
                break;
            case 85:
                mesg = "竞猜未开启或者已经过时,不能下注";
                break;
            case 86:
                mesg = "提交的对赌错误,或者已经有人加入过了";
                break;
            case 87:
                mesg = "对赌不能被结算，因为系统还没有结算/发布结算的不是你本人/当前的状态不能被结算";
                break;
            case 88:
                mesg = "超级波胆编号错误";
                break;
            case 89:
                mesg = "回复消息时主消息不存在";
                break;
            case 90:
                mesg = "检测用户参与竞猜的合法性标志编号";
                break;
            case 91:
                mesg = "超过最大对赌限制";
                break;
            case 92:
                mesg = "账户被锁定不能登录";
                break;
            case 93:
                mesg = "已存在";
                break;
            case 94:
                mesg = "条件不成立";
                break;
            case 95:
                mesg = "用户旧密码错误";
                break;
            case 96:
                mesg = "电子邮件地址错误";
                break;
        }
        return mesg;
    }
}
