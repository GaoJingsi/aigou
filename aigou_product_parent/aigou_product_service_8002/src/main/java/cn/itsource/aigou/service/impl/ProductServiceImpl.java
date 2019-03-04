package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.domain.Brand;
import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.domain.ProductExt;
import cn.itsource.aigou.mapper.ProductExtMapper;
import cn.itsource.aigou.mapper.ProductMapper;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品 服务实现类
 * </p>
 *
 * @author wbtest
 * @since 2019-02-27
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private ProductMapper productMapper;


    @Autowired
    private ProductExtMapper productExtMapper;

    ///  分布式项目事务:非常蛋疼

    @Override
    public boolean insert(Product entity) {
        //要保存product表,同时保存:productExt mybatis的时候,保存一条数据后,自动返回他的注解:insert中配置三个属性
        boolean b = super.insert(entity);
        ProductExt productExt = entity.getProductExt();

        // mp应该会返回保存数据的主键
        productExt.setProductId(entity.getId());
        productExtMapper.insert(productExt);
        return b;
    }

    @Override
    public PageList<Product> selectQuery(ProductQuery query) {
        //分页查询: 以前在分页查询的时候:需要两个请求:总的条数和当前分页的数据
        //1:设置总的页数
        PageList<Product> pageList = new PageList<>();
        long totalcount = productMapper.queryPageCount(query);
        if (totalcount > 0) {
            pageList.setTotal(totalcount);
            //2:设置当前分页数据:
            // Mapper.xml中查询的是分页的数据:rows
            List<Product> products = productMapper.queryPage(query);
            pageList.setRows(products);
        }


        return pageList;
    }
}
