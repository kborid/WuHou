package com.digitalchina.liangjiang.wxapi;

/**
 * @author kborid
 * @date 2017/2/9 0009
 */
public interface IRouterUri {
    @RouterUri(routerUri = "xl://goods:8888/goodsDetail") //请求Url地址
    void jumpToGoodsDetail(@RouterParam("goodsId") String goodsId, @RouterParam("des") String des);//参数商品Id 商品描述
}
