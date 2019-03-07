package cn.itsource.aigou.service;

import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品 服务类
 * </p>
 *
 * @author wbtest
 * @since 2019-02-27
 */
public interface IProductService extends IService<Product> {

    PageList<Product> selectQuery(ProductQuery query);

    /**
     *
     * @param productId 商品的id
     * @param skuProperties  sku的属性表里的数据
     * @param skuDatas  sku的值
     */
    void addSku(Object productId, List<Map<String, Object>> skuProperties, List<Map<String, Object>> skuDatas);
}
