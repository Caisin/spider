package com.caisin.wuxiaworld;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AiCaiGou {


    @Test
    public void testRg() throws Exception{
        String path = "C:\\Users\\CC\\IdeaProjects\\spider\\猪场信息-种猪.xlsx";
        ExcelReader reader = ExcelUtil.getReader(path);
        List<Map<String, Object>> maps = reader.readAll();
        HashMap<String, Object> hashMap = new HashMap<>();
        for (Map<String, Object> map : maps) {
            hashMap.put(String.valueOf(map.get("phone")),map);
        }
        ExcelWriter writer = ExcelUtil.getWriter("C:/Users/CC/IdeaProjects/spider/猪场信息-种猪-去重.xlsx");
        Collection<Object> data = hashMap.values();
        writer.write(data);
        writer.flush();
        writer.close();
        System.out.println("去重前"+maps.size()+" 去重后"+data.size());
    }
    @Test
    public void testZz() throws Exception{
        String baseUrl="https://b2b.baidu.com/s/a" +
                "?ajax=1&csrf_token=82620e7eb06698ad88b10187564e7953&logid=2133729246510916272" +
                "&fid=0,1606049938952&_=1606054985552&o=0" +
                "&q=%s&p=%s&sa=&mk=全部结果&f=[]&s=30&adn=1&resType=product" +
                "&fn={\"select_param0\":\"品牌\",\"select_param1\":\"体高\",\"select_param2\":\"胴体重\"}";

        ArrayList<String> urls = new ArrayList<>();
        //最大页数*3
        String keyWord = "生猪";
        for (int i = 1; true; i++) {
            String url= String.format(baseUrl, keyWord,i);
            try {
                String s = HttpUtil.get(url);
                JSONObject ret = JSONUtil.parseObj(s);
                JSONObject data = ret.getJSONObject("data");
                JSONArray productList = data.getJSONArray("productList");
                if (productList.size()==0) {
                    break;
                }
                for (Object o : productList) {
                    JSONObject item= (JSONObject) o;
                    String jUrl = item.getStr("jUrl");
                    urls.add(jUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            System.out.println("获取第" + i+" 页数据成功");
        }
        int size = urls.size();
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        int i=0;
        for (String url : urls) {
            if (StrUtil.isEmpty(url)) {
                continue;
            }
            Connection connection = null;
            try {
                connection = JsoupUtil.getConnection(url);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error url = " + url);
                continue;
            }
            Document document = connection.get();
            Elements title = document.getElementsByTag("title");
            String html = title.html();
            Map<String, Object> contactInfo = getContactInfo(document.html());
            contactInfo.put("title",html);
            maps.add(contactInfo);
            i++;
            System.out.println("总共: " + size+" 执行完第 "+i);
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        for (Map<String, Object> map : maps) {
            hashMap.put(String.valueOf(map.get("phone")),map);
        }
        ExcelWriter writer = ExcelUtil.getWriter("C:/Users/CC/IdeaProjects/spider/猪场信息-"+keyWord+"去重.xlsx");
        Collection<Object> data = hashMap.values();
        writer.write(data);
        writer.flush();
        writer.close();
        ExcelWriter writerOrg = ExcelUtil.getWriter("C:/Users/CC/IdeaProjects/spider/猪场信息-"+keyWord+".xlsx");
        writerOrg.write(maps);
        writerOrg.flush();
        writerOrg.close();
        System.out.println("去重前"+maps.size()+" 去重后"+data.size());

    }

    @Test
    public void testData(){
        String data="<!doctype html>\n" +
                "<html>\n" +
                " <head>\n" +
                "  <meta charset=\"UTF-8\"> \n" +
                "  <title>二元种猪 检疫严格二元种猪价格繁育基地 优良牧业</title> \n" +
                "  <meta name=\"keywords\" content=\"二元种猪批发价格、二元种猪供应厂家直销、二元种猪行情报价、二元种猪供求信息\"> \n" +
                "  <meta name=\"description\" content=\"本公司生产销售二元种猪等，还有更多二元种猪相关的最新专业产品参数、实时报价、市场行情、优质商品批发、供应厂家等信息。您还可以在平台免费查询报价、发布询价信息、查找商机等。\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "  <meta property=\"og:type\" content=\"product\"> \n" +
                "  <meta property=\"og:image\" content=\"https://t7.baidu.com/it/u=2325816717,502076587&amp;fm=199&amp;app=68&amp;f=JPEG?w=750&amp;h=750&amp;s=5BD03AD54007615DD021FDA6030070D2\"> \n" +
                "  <meta property=\"og:title\" content=\"二元种猪 检疫严格二元种猪价格繁育基地 优良牧业\"> \n" +
                "  <meta property=\"og:description\" content=\"二元种猪 检疫严格二元种猪价格繁育基地 优良牧业 品种:种猪, 品牌:优良, 产地/厂家:山东临沂, 体重:20公斤-30公斤左右, 成活率:99.9%, 可售卖地:全国, 动物种类:猪, 发货方式:可电话咨询, 检疫是否达标:是, 可售卖地:全国, 用途:养殖\"> \n" +
                "  <link rel=\"dns-prefetch\" href=\"//ts.bdimg.com\">\n" +
                "  <link rel=\"dns-prefetch\" href=\"//himg.bdimg.com\">\n" +
                "  <link rel=\"shortcut icon\" href=\"https://b2b.baidu.com/favicon.ico\" type=\"image/x-icon\">\n" +
                "  <script src=\"https://code.bdstatic.com/npm/spy-client@2.0.3/dist/spy-head.min.js\"></script>\n" +
                "  <script>/* eslint-disable */\n" +
                "        // 线上 or 线下 用于性能与异常打点\n" +
                "        window.SITE_ENV = location.host === 'b2b.baidu.com' ? 'online' : 'offline';\n" +
                "\n" +
                "        var dim = {\n" +
                "            biz: 'pc',\n" +
                "            env: window.SITE_ENV\n" +
                "        };\n" +
                "\n" +
                "        __spyHead && __spyHead.init({\n" +
                "            pid: '18_101',\n" +
                "            resourceError: {\n" +
                "                group: 'resourceError',\n" +
                "                sample: 1,\n" +
                "                handler: function (data) {\n" +
                "                    data.dim = dim;\n" +
                "                    data.info.href = location.href;\n" +
                "                }\n" +
                "            },\n" +
                "            jsError: {\n" +
                "                group: 'jsError',\n" +
                "                sample: 1,\n" +
                "                handler: function (data) {\n" +
                "                    data.dim = dim;\n" +
                "                    data.info.href = location.href;\n" +
                "                }\n" +
                "            },\n" +
                "            whiteScreenError: {\n" +
                "                group: 'whiteScreenError',\n" +
                "                sample: 1,\n" +
                "                // 一旦以下逻辑不满足，就认为白屏\n" +
                "                // 1. document.querySelector(selector)的元素包含document.querySelector(selector).querySelector(subSelector)\n" +
                "                // 2. document.querySelector(selector)的高度大于屏幕高度的2/3\n" +
                "                selector: 'body',\n" +
                "                subSelector: '[data-qa=static-ok]',\n" +
                "                timeout: 6000,\n" +
                "                handler: function(data) {\n" +
                "                    data.dim = dim;\n" +
                "                    data.info.href = location.href;\n" +
                "                }\n" +
                "            }\n" +
                "        });\n" +
                "        /* eslint-enable */</script>\n" +
                "  <script type=\"text/javascript\" src=\"https://passport.baidu.com/passApi/js/uni_wrapper.js?cdnversion=202011222242\"></script>\n" +
                "  <link href=\"/static/pc/css/land.138010664122e4bb32bd1f0baec4f15e.css\" rel=\"stylesheet\">\n" +
                "  <script>/* eslint-disable */\n" +
                "        var _hmt = _hmt || [];\n" +
                "        (function () {\n" +
                "            var hm = document.createElement('script');\n" +
                "            hm.src = '//hm.baidu.com/hm.js?77fe88a330b395c39e37e1ea1cea9a8c';\n" +
                "            hm.setAttribute('async', 'async');\n" +
                "            hm.setAttribute('defer', 'defer');\n" +
                "            var s = document.getElementsByTagName('script')[0];\n" +
                "            s.parentNode.insertBefore(hm, s);\n" +
                "        })();\n" +
                "        /* eslint-enable */</script>\n" +
                " </head>\n" +
                " <body>\n" +
                "  <!--[if lte IE 8]>\n" +
                "    <style>* {margin: 0;padding: 0;}body {font: 14px/1.5 \"Helvetica Neue\", Helvetica, \"PingFang SC\", \"Hiragino Sans GB\", \"Microsoft YaHei\", \"\\5FAE\\8F6F\\96C5\\9ED1\", Arial, sans-serif;background-color: #f7f7f7;}.ie-header {height: 88px;border-bottom: 1px solid #eee;width: 100%;background-color: #fff;}.ie-header-inner {width: 990px;margin: 0 auto;}.ie-logo {margin-top: 19px;}.ie-warning {padding: 0 40px 10px 40px;text-align: center;font-size: 32px;}.ie-warning-sub {color: #666;text-align: center;margin-top: 0;margin-bottom: 40px;}.ie-content {width: 830px;margin: 16px auto 40px;background-color: #fff;padding: 40px 80px 80px 80px;}.ie-content-browser {overflow: hidden;}.ie-content-browser-item {width: 33%;float: left;text-align: center;margin-top: 20px;}.ie-content-browser-item p {font-size: 16px;color: #333;}.ie-content-browser-item a {font-size: 16px;color: #fff;display: inline-block;width: 180px;height: 40px;line-height: 40px;background-color: #EF1F1F;margin-top: 20px;text-decoration: none;}</style><div class=\"ie-header\"><div class=\"ie-header-inner\"><img class=\"ie-logo\" height=\"50px\" width=\"166px\" src=\"/static/pc/files/ie/logo.png\"/></div></div><div class=\"ie-content\"><h2 class=\"ie-warning\">您使用的浏览器版本过低</h2><p class=\"ie-warning-sub\">当前浏览器可能存在安全风险，为了保障您的采购体验，请立即升级浏览器！</p><div class=\"ie-content-browser\"><div class=\"ie-content-browser-item\"><img class=\"chrome\" height=\"110px\" width=\"110px\" src=\"/static/pc/files/ie/chrome.png\"/><p>Chrome 浏览器</p><a href=\"https://www.google.cn/chrome\">下载</a></div><div class=\"ie-content-browser-item\"><img class=\"chrome\" height=\"110px\" width=\"110px\" src=\"/static/pc/files/ie/ff.png\"/><p>火狐浏览器</p><a href=\"http://www.firefox.com.cn/download/\">下载</a></div><div class=\"ie-content-browser-item\"><img class=\"chrome\" height=\"110px\" width=\"110px\" src=\"/static/pc/files/ie/ie.png\"/><p>最新版 IE 浏览器</p><a href=\"https://support.microsoft.com/zh-cn/help/17621/internet-explorer-downloads\">下载</a></div></div></div>\n" +
                "    <![endif]-->\n" +
                "  <div id=\"app\"></div>\n" +
                "  <script>/* eslint-disable */\n" +
                "        window.loginStatus = {\"is_login\":0,\"user_name\":null,\"passDomain\":\"https:\\/\\/passport.baidu.com\",\"avatar\":\"\",\"id_type\":null,\"csrf_token\":\"947a456af9b249fcaf9cb51f907324c9\"};\n" +
                "        window.pageData = {\"log_url\":\"https:\\/\\/b2b.baidu.com\\/slog\\/a.gif\",\"b2bPage\":\"https:\\/\\/b2b.baidu.com\",\"imallPage\":\"https:\\/\\/imall.baidu.com\",\"b2b_sid\":\"162_192_201\",\"search_type\":[{\"name\":\"\\u5546\\u54c1\",\"url_prx\":\"\\/s\",\"url_aprx\":\"\\/s\\/a\"},{\"name\":\"\\u5382\\u5bb6\",\"url_prx\":\"\\/c\",\"url_aprx\":\"\\/c\\/a\"}],\"hasOrder\":0};\n" +
                "        window.pageQuery = {\"id\":\"13b18b6e60b92865e1f609b00944613310\",\"from\":\"seo\"};\n" +
                "        window.logId = '2133603669855113522';\n" +
                "        window.data = {\"query\":\"\",\"iswapurl\":\"0\",\"provider\":{\"name\":\"\\u4e34\\u6c82\\u4f18\\u826f\\u7267\\u4e1a\\u6709\\u9650\\u516c\\u53f8\",\"status\":\"\\u8425\\u4e1a\\u4e2d\",\"regCap\":\"66.8\\u4e07\\u5143\",\"regAddr\":\"\\u4e34\\u6c82\\u5e02\\u8392\\u5357\\u53bf\\u76f8\\u6c9f\\u9547\\u4e09\\u4e49\\u793e\\u533a\\u6768\\u5bb6\\u4e09\\u4e49\\u53e3\\u67512-448\\u53f7\",\"regData\":\"2020-06-18\",\"entType\":\"\\u6709\\u9650\\u8d23\\u4efb\\u516c\\u53f8(\\u81ea\\u7136\\u4eba\\u72ec\\u8d44)\",\"scope\":\"\\u4ed4\\u732a\\u3001\\u732a\\u82d7\\u3001\\u751f\\u732a\\u3001\\u79cd\\u732a\\u3001\\u6bcd\\u732a\\u7e41\\u80b2\\u3001\\u8fd0\\u8f93\\u53ca\\u9500\\u552e\\u3002\\uff08\\u4f9d\\u6cd5\\u987b\\u7ecf\\u6279\\u51c6\\u7684\\u9879\\u76ee\\uff0c\\u7ecf\\u76f8\\u5173\\u90e8\\u95e8\\u6279\\u51c6\\u540e\\u65b9\\u53ef\\u5f00\\u5c55\\u7ecf\\u8425\\u6d3b\\u52a8\\uff09\",\"jumpUrl\":\"https:\\/\\/xin.baidu.com\\/detail\\/compinfo?pid=xlTM-TogKuTwI4yQzSOIxc0NyMBO4P3knAmd&source=1034\",\"logo\":\"https:\\/\\/ss1.baidu.com\\/6ONXsjip0QIZ8tyhnq\\/it\\/u=1223303830,2715713976&fm=179&app=42&f=JPEG?w=400&h=400&s=11022BF95A1310C850252DFB0300C012\"},\"tpProvider\":{\"name\":\"\\u4e34\\u6c82\\u4f18\\u826f\\u7267\\u4e1a\\u6709\\u9650\\u516c\\u53f8\",\"address_v2\":\"\\u5c71\\u4e1c\\u4e34\\u6c82\",\"from\":\"\\u627e\\u5546\\u7f51\"},\"resType\":\"\",\"tpath\":\"\",\"coreQuery\":\"\\u4e8c\\u5143\\u79cd\\u732a\",\"tp\":{\"name\":\"\\u627e\\u5546\\u7f51\",\"logo\":\"https:\\/\\/b2b-amis.cdn.bcebos.com\\/%E6%89%BE%E5%95%86%E7%BD%91.png\",\"abilities\":[{\"title\":\"\\u5f00\\u5e97\\u5165\\u9a7b\",\"slogan\":\"\\u7531\\u7231\\u91c7\\u8d2d\\u6388\\u6743\\uff0c\\u53ef\\u63d0\\u4f9b\\u4ee3\\u7406\\u5165\\u9a7b\\u7231\\u91c7\\u8d2d\\u5f00\\u5e97\\u670d\\u52a1\",\"jumpUrl\":\"https:\\/\\/b2b.baidu.com\\/feedback?from=index_banner&ver=2\"},{\"title\":\"\\u670d\\u52a1\\u4ecb\\u7ecd\",\"slogan\":\"\\u7531\\u627e\\u5546\\u7f51\\u63d0\\u4f9b\\u670d\\u52a1\\uff0c\\u63d0\\u4f9b\\u7ad9\\u5185\\u7ad9\\u5916\\u63a8\\u5e7f\\u3001\\u591a\\u70b9\\u66dd\\u5149\\u3001\\u76f4\\u8fbe\\u5546\\u673a\\u7b49\\u670d\\u52a1\",\"jumpUrl\":\"https:\\/\\/www.zhaosw.com\\/sugoutong\"},{\"title\":\"\\u5173\\u4e8e\\u627e\\u5546\",\"slogan\":\"\\u627e\\u5546\\u7f51\\uff0c\\u4e13\\u6ce8\\u4e8e\\u4e3a\\u4e2d\\u5c0f\\u4f01\\u4e1a\\u63d0\\u4f9b\\u5546\\u673a\\u7684\\u5e73\\u53f0\",\"jumpUrl\":\"https:\\/\\/www.zhaosw.com\\/help\\/doc\\/detail\\/10503_1\"}]},\"item\":{\"id\":\"13b18b6e60b92865e1f609b009446133\",\"fullName\":\"\\u4e8c\\u5143\\u79cd\\u732a \\u68c0\\u75ab\\u4e25\\u683c\\u4e8c\\u5143\\u79cd\\u732a\\u4ef7\\u683c\\u7e41\\u80b2\\u57fa\\u5730 \\u4f18\\u826f\\u7267\\u4e1a\",\"xzhid\":\"30841587\",\"dedup_id\":\"c2c3c39088014425ba53887e95097ce9\",\"priceList\":[{\"price\":\"660.00\",\"priceCurrency\":\"\\u5143\",\"minValue\":\"2\",\"maxValue\":\"\",\"unitCode\":\"\\u5934\"}],\"picUrl\":\"https:\\/\\/t7.baidu.com\\/it\\/u=2325816717,502076587&fm=199&app=68&f=JPEG?w=750&h=750&s=5BD03AD54007615DD021FDA6030070D2\",\"picList\":[\"https:\\/\\/t7.baidu.com\\/it\\/u=2325816717,502076587&fm=199&app=68&f=JPEG?w=750&h=750&s=5BD03AD54007615DD021FDA6030070D2\",\"https:\\/\\/t8.baidu.com\\/it\\/u=3804158344,2178146277&fm=199&app=68&f=JPEG?w=750&h=750&s=04C02AF94236718C1A80F4D9030090F1\",\"https:\\/\\/t8.baidu.com\\/it\\/u=4052109838,1044788500&fm=199&app=68&f=JPEG?w=750&h=750&s=09206B93CA333284D2B458A1030070C2\",\"https:\\/\\/t7.baidu.com\\/it\\/u=563564811,3878980200&fm=199&app=68&f=JPEG?w=750&h=750&s=338A69A1CA9E18CE4B19A0D9030040D3\",\"https:\\/\\/t8.baidu.com\\/it\\/u=2473255258,3964670587&fm=199&app=68&f=JPEG?w=750&h=750&s=19030BDBD637078C4B016DF303006053\"],\"videoList\":[{\"video_id\":\"13b18b6e60b92865e1f609b009446133fa2372e97cd7119a9723576dcd123ee2\",\"src\":\"http:\\/\\/bj.bcebos.com\\/b2b-video\\/4631fcdb632d2d4dcede1db2bbb4f0fe_transcode.mp4?authorization=bce-auth-v1\\/93a524086d8e4688834066e88a88c363\\/2020-06-30T09:12:46Z\\/-1\\/host\\/f2242998a2ad30087a8a336b982eeb216e4f050d8582d3601368c10c782150d5\",\"poster\":\"https:\\/\\/t7.baidu.com\\/it\\/u=2325816717,502076587&fm=199&app=68&f=JPEG?w=750&h=750&s=5BD03AD54007615DD021FDA6030070D2\"}],\"fhLocation\":\"\\u5c71\\u4e1c\\u7701 \\u4e34\\u6c82\\u5e02 \\u8392\\u5357\\u53bf\",\"contact_info\":\"ijVpeXvU*9gsoZV-NgyZW2Z80toiZdioWUr3l2JJ4mC8GlK4TTOQi0NfJgmRnYidgGvoVX8oibKma6RU04EWJUhgHHsdl-Faq8w-kfUKww4\",\"hasQQ\":0,\"hasPhone\":1,\"contact\":\"\\u5f20\\u6587\\u670b\",\"category\":\"\\u98df\\u54c1\\u519c\\u4e1a;\\u5bb6\\u79bd\\u5bb6\\u755c;\\u732a\",\"brandName\":\"\\u4f18\\u826f\",\"homePage\":\"https:\\/\\/youliang.zhaosw.com\",\"view_times\":\"299\",\"inquiry_times\":\"0\",\"from\":{\"name\":\"\\u627e\\u5546\\u7f51\",\"icon\":null},\"url\":\"https:\\/\\/www.zhaosw.com\\/product\\/detail\\/43150066?bdb2b8a2d=2133603669855113522\",\"jumpUrl\":\"https:\\/\\/b2b.baidu.com\\/b2bsearch\\/jump?url=https%3A%2F%2Fwww.zhaosw.com%2Fproduct%2Fdetail%2F43150066&query=13b18b6e60b92865e1f609b009446133&logid=2133603669855113522&srcId=27729&brand=%E4%BC%98%E8%89%AF&category=%E9%A3%9F%E5%93%81%E5%86%9C%E4%B8%9A%3B%E5%AE%B6%E7%A6%BD%E5%AE%B6%E7%95%9C%3B%E7%8C%AA&sv_cr=0&uign=28ee1e575beec1476a6ab9476aa781ff&iid=13b18b6e60b92865e1f609b009446133&timeSignOri=1606056178&xzhid=30841587&miniId=8469&ii_pos=0\",\"from_site_url\":\"https:\\/\\/www.zhaosw.com\",\"detail_v2\":\"<p align=\\\"center\\\"><span><b>\\u4ed4\\u732a\\u4ef7\\u683c \\u5e38\\u5e74\\u76f4\\u4f9b\\u4e09\\u5143\\u732a\\u82d7\\u54c1\\u8d28\\u4fdd\\u8bc1 \\u5168\\u56fd\\u76f4\\u4f9b<\\/b><\\/span><br\\/><\\/p><p align=\\\"center\\\">\\u4ef7\\u683c\\u968f\\u5e02\\u573a\\u884c\\u60c5\\u53d8\\u52a8\\u800c\\u53d8\\u52a8\\uff0c\\u5177\\u4f53\\u4ee5\\u7535\\u8bdd\\u54a8\\u8be2\\u4e3a\\u6807\\u51c6\\uff01<span><b><br\\/><\\/b><\\/span><\\/p><p align=\\\"center\\\"><img src=\\\"https:\\/\\/t7.baidu.com\\/it\\/u=451900221,3554964760&fm=199&app=68&f=JPEG?w=750&h=1583&s=E5C86AB34C70708078AD14C9030090B1\\\"\\/><img src=\\\"https:\\/\\/t7.baidu.com\\/it\\/u=2599319232,2505222767&fm=199&app=68&f=JPEG?w=750&h=1582&s=45F010D35CE0BC8E10A150C90300F062\\\"\\/><img src=\\\"https:\\/\\/t7.baidu.com\\/it\\/u=1353518100,1704800532&fm=199&app=68&f=JPEG?w=750&h=1583&s=4DF018D318F4FC8C8A2004CB0300A062\\\"\\/><img src=\\\"https:\\/\\/t8.baidu.com\\/it\\/u=2178759106,1579545341&fm=199&app=68&f=JPEG?w=750&h=1583&s=013158937EE17A88345CD1E20300C031\\\"\\/><img src=\\\"https:\\/\\/t7.baidu.com\\/it\\/u=3887908833,1208523993&fm=199&app=68&f=JPEG?w=750&h=1582&s=192A6A9366683AA90098D9520300A0F0\\\"\\/><img src=\\\"https:\\/\\/t9.baidu.com\\/it\\/u=4044087257,2084136944&fm=199&app=68&f=JPEG?w=750&h=1583&s=69AEAA5214AA5AA47A6DA44B0300A0F5\\\"\\/><br\\/><\\/p>\",\"qid\":\"2133603669855113522\",\"last_update\":\"1599138326\",\"lginfo\":\"\",\"meta\":[{\"k\":\"\\u54c1\\u79cd\",\"v\":\"\\u79cd\\u732a\"},{\"k\":\"\\u54c1\\u724c\",\"v\":\"\\u4f18\\u826f\"},{\"k\":\"\\u4ea7\\u5730\\/\\u5382\\u5bb6\",\"v\":\"\\u5c71\\u4e1c\\u4e34\\u6c82\"},{\"k\":\"\\u4f53\\u91cd\",\"v\":\"20\\u516c\\u65a4-30\\u516c\\u65a4\\u5de6\\u53f3\"},{\"k\":\"\\u6210\\u6d3b\\u7387\",\"v\":\"99.9%\"},{\"k\":\"\\u53ef\\u552e\\u5356\\u5730\",\"v\":\"\\u5168\\u56fd\"},{\"k\":\"\\u52a8\\u7269\\u79cd\\u7c7b\",\"v\":\"\\u732a\"},{\"k\":\"\\u53d1\\u8d27\\u65b9\\u5f0f\",\"v\":\"\\u53ef\\u7535\\u8bdd\\u54a8\\u8be2\"},{\"k\":\"\\u68c0\\u75ab\\u662f\\u5426\\u8fbe\\u6807\",\"v\":\"\\u662f\"},{\"k\":\"\\u53ef\\u552e\\u5356\\u5730\",\"v\":\"\\u5168\\u56fd\"},{\"k\":\"\\u7528\\u9014\",\"v\":\"\\u517b\\u6b96\"}],\"cpaMember\":2,\"cpaDuration\":\"1\",\"spotCertify\":0,\"prodTag\":\"\\u4e8c\\u5143\\u79cd\\u732a;\\u4e8c\\u5143\\u79cd\\u732a\\u4ef7\\u683c;\\u79cd\\u732a\",\"tag_list\":[{\"name\":\"\\u5b9e\\u5730\\u9a8c\\u5382\",\"type\":\"verify_senior\"},{\"name\":\"\\u4f18\\u826f\\u54c1\\u724c\",\"type\":\"tag\"}],\"skuType\":\"\",\"senior_type\":1,\"sid\":\"12322035073642155535\",\"productStock\":0,\"allowPurchase\":0,\"wiseQrcodeUrl\":\"https:\\/\\/b2b.baidu.com\\/m\\/land?id=13b18b6e60b92865e1f609b00944613310\",\"wiseShopQrcodeUrl\":\"https:\\/\\/b2b.baidu.com\\/m\\/shop\\/index?xzhid=30841587&name=%E4%B8%B4%E6%B2%82%E4%BC%98%E8%89%AF%E7%89%A7%E4%B8%9A%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8\"},\"sellerInfo\":{\"avatar\":\"\",\"contactName\":\"\\u6731\\u6653\\u4e3d\",\"jobTitle\":\"\",\"wechatNumber\":\"\",\"externalAddress\":\"\\u4e34\\u6c82\\u5e02\\u8392\\u5357\\u53bf\\u76f8\\u6c9f\\u9547\\u4e09\\u4e49\\u793e\\u533a\\u6768\\u5bb6\\u4e09\\u4e49\\u53e3\\u67512-448\\u53f7\",\"address_v2\":\"\\u5c71\\u4e1c\\u4e34\\u6c82\"},\"btmSellerInfo\":{\"contact\":\"\\u5f20\\u6587\\u670b\",\"phone\":\"18763729000\",\"email\":\"\",\"address\":\"\\u4e34\\u6c82\\u5e02\\u8392\\u5357\\u53bf\\u76f8\\u6c9f\\u9547\\u4e09\\u4e49\\u793e\\u533a\\u6768\\u5bb6\\u4e09\\u4e49\\u53e3\\u67512-448\\u53f7\",\"addressV2\":null},\"collectInfo\":{\"colId\":\"13b18b6e60b92865e1f609b009446133\",\"colType\":1,\"bHasCollect\":false,\"bHasLimit\":false}};\n" +
                "        window.status = '0';\n" +
                "        window.msg = '';\n" +
                "        window.SITE_DOMAIN = 'b2b';\n" +
                "        /* eslint-enable */</script>\n" +
                "  <script type=\"text/javascript\" src=\"/static/pc/js/land.c2e3ba2ca17641452368.js\"></script>\n" +
                " </body>\n" +
                "</html>";

        Pattern compile = Pattern.compile("window.data..*;");
        Matcher matcher = compile.matcher(data);
        if (matcher.find()) {
            String group = matcher.group();
            String replace = group.replace("window.data = ", "");
            String substring = replace.substring(0, replace.length() - 1);
            JSONObject jsonObject = JSONUtil.parseObj(substring);
            JSONObject btmSellerInfo = jsonObject.getJSONObject("btmSellerInfo");
            System.out.println("btmSellerInfo = " + btmSellerInfo);

        }
    }

    public Map<String,Object> getContactInfo(String html){
        Pattern compile = Pattern.compile("window.data..*;");
        Matcher matcher = compile.matcher(html);
        HashMap<String, Object> ret = new HashMap<>();
        if (matcher.find()) {
            String group = matcher.group();
            String replace = group.replace("window.data = ", "");
            String substring = replace.substring(0, replace.length() - 1);
            JSONObject jsonObject = JSONUtil.parseObj(substring);
            JSONObject btmSellerInfo = jsonObject.getJSONObject("btmSellerInfo");
           ret.putAll(btmSellerInfo);
        }
        return ret;
    }

}
