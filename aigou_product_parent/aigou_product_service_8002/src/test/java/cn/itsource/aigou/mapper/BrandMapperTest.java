package cn.itsource.aigou.mapper;

import cn.itsource.aigou.ProductServiceApplication_8002;
import cn.itsource.aigou.query.BrandQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductServiceApplication_8002.class)
public class BrandMapperTest {
    @Autowired
    private BrandMapper brandMapper;
    @Test
    public void queryPageCount() {
        System.out.println("43554++++++++++++++++");
        System.out.println(brandMapper.queryPageCount(new BrandQuery()));
    }
}