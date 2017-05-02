package com.yunfei.wh.net.bean;

import java.io.Serializable;

/**
 * 地址管理，用户地址列表
 *
 * @author LiaoBo
 */
public class UserAddrs implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String id;            // 1,
    public String userid;        // f840bc2e730e4c838b0265177e59966f,
    public String name;        // 测试,
    public String province;    // 510000,
    public String city;        // 510100,
    public String area;        // 510100,
    public String provinceCH;    // 四川省,
    public String cityCH;        // 成都市,
    public String areaCH;        // 成都市,
    public String address;    // 测试地址,
    public String phone;        // 13511111111,
    public String tel;        // 13511111111,
    public boolean def;        // false,
    public String createtime;    // Oct 9, 2015 5:41:12 PM
    public String allCopyAddress; // 拼接好的地址

    @Override
    public String toString() {
        return "id:" + id + "\nuserid:" + userid + "\nname:" + name + "\nprovince:" + province + "\ncity:" + city + "\narea:" + area
                + "\nprovinceCH:" + provinceCH + "\ncityCH:" + cityCH + "\nareaCH:" + areaCH + "\naddress:" + address + "\nphone:" + phone + "\ntel:" + tel
                + "\ndef:" + def + "\ncreatetime:" + createtime;
    }
}
