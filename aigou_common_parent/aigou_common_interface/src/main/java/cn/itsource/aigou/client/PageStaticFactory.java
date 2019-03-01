package cn.itsource.aigou.client;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PageStaticFactory implements FallbackFactory<PageStaticClient> {
    @Override
    public PageStaticClient create(Throwable throwable) {
        return new PageStaticClient() {
            @Override
            public void getPageStatic(Map<String, Object> map) {

            }
        };
    }
}
