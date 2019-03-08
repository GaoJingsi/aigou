package cn.itsource.aigou.mapper;

import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.query.ProductQuery;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品 Mapper 接口
 * </p>
 *
 * @author wbtest
 * @since 2019-02-27
 */
public interface ProductMapper extends BaseMapper<Product> {

    long queryPageCount(ProductQuery query);

    List<Product> queryPage(ProductQuery query);

    //ids 和上架时间
    void updateOnSaleBatch(Map<String,Object> params);

    void updateOffSaleBatch(Map<String, Object> params);
}
