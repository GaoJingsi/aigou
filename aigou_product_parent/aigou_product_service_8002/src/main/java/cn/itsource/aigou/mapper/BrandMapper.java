package cn.itsource.aigou.mapper;

import cn.itsource.aigou.domain.Brand;
import cn.itsource.aigou.query.BrandQuery;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;

/**
 * <p>
 * 品牌信息 Mapper 接口
 * </p>
 *
 * @author wbtest
 * @since 2019-02-27
 */
public interface BrandMapper extends BaseMapper<Brand> {

    /**
     * 分页条件数据
     * @param query
     * @return
     */
    List<Brand> queryPage( BrandQuery query);


    /**
     * 分页条件查询:总的条数
     * @param query
     * @return
     */
   long queryPageCount( BrandQuery query);
}
