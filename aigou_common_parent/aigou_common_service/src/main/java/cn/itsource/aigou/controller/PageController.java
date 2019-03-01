package cn.itsource.aigou.controller;

import cn.itsource.aigou.client.PageStaticClient;
import cn.itsource.aigou.constants.GlobelConstants;
import cn.itsource.aigou.util.VelocityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/common")
public class PageController implements PageStaticClient {

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    @Override
    public void getPageStatic(@RequestBody Map<String, Object> map) {
        //逻辑实现:
        Object model = map.get(GlobelConstants.PAGE_MODE);
        String templateFilePathAndName = (String) map.get(GlobelConstants.PAGE_TEMPLATE);
        String targetFilePathAndName = (String) map.get(GlobelConstants.PAGE_TEMPLATE_HTML);
        System.out.println("model==="+model);
        System.out.println("templateFilePathAndName==="+templateFilePathAndName);
        System.out.println("targetFilePathAndName==="+targetFilePathAndName);

        VelocityUtils.staticByTemplate(model, templateFilePathAndName, targetFilePathAndName);
    }
}
