package cn.itsource.aigou.service;

import cn.itsource.aigou.domain.ProductType;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 商品目录 服务类
 * </p>
 *
 * @author wbtest
 * @since 2019-02-27
 */
public interface IProductTypeService extends IService<ProductType> {

    /**
     * tree
     * 数据
     * @return
     */
    List<ProductType> treeData();
}
