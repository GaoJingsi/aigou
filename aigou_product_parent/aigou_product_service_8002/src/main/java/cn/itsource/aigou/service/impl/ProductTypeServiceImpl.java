package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.domain.ProductType;
import cn.itsource.aigou.mapper.ProductMapper;
import cn.itsource.aigou.mapper.ProductTypeMapper;
import cn.itsource.aigou.service.IProductTypeService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品目录 服务实现类
 * </p>
 *
 * @author wbtest
 * @since 2019-02-27
 */
@Service
public class ProductTypeServiceImpl extends ServiceImpl<ProductTypeMapper, ProductType> implements IProductTypeService {

    @Autowired
    private ProductTypeMapper productTypeMapper;

    /**
     * @return
     */
    @Override
    public List<ProductType> treeData() {
            // 要得到name和儿子

        //return treeDataRecursion(0L);
        return treeDataLoop();
    }

    /**
     * 使用循环方式:
     *   我们期望发送一条sql,把所有的子子孙孙的结构搞出来,但是搞不出来的;
     *   但是我们可以发送一条sql:把所有的数据拿回来,存在内存中,我可以写代码组装他的结构(在内存中完成的).
     * @return
     */
    private List<ProductType> treeDataLoop() {
        //1:获取所有的数据:
        List<ProductType> allProductType = productTypeMapper.selectList(null);

        //2:用于存在每一个对象和他的一个标识的 Long:id
        Map<Long,ProductType> map=new HashMap<>();
        for (ProductType productType : allProductType) {
            map.put(productType.getId(), productType);
        }

        //最终想要的结果:
        List<ProductType> result = new ArrayList<>();
        //3:遍历
        for (ProductType productType : allProductType) {
            //组装结构: productType:每一个对象:
            Long pid = productType.getPid();
            if(pid==0){
                result.add(productType);
            }else{
                // 找自己的老子,把自己添加到老子的儿子中
                ProductType parent=map.get(pid);// where id =pid
               /* //我老子的儿子
                List<ProductType> children = parent.getChildren();
                //把我自己放入老子的儿子中
                children.add(productType);*/
                parent.getChildren().add(productType);
            }
        }
        return result;
    }

    /**
     *
     * 查询无限极的树装数据:
     select * from t_product_type where pid= ?????

     先得到一级目录:
     得到0的儿子;
     遍历这个目录:
     分别的他的儿子:
     遍历这个儿子目录的儿子
     ....
     递归的遍历下去,只到没有儿子就返回.

     treeDataRecursion:就是获取儿子:谁的儿子?

     递归:性能很差的,每次都要发送sql,会发送多条sql:怎么优化??????
     ====>问题是发了很多条sql才导致性能差,我发一条把所有的数据都拿回就好了
     * @return
     */
    private List<ProductType> treeDataRecursion(Long pid) {
      //treeDataRecursion:获取传入参数的儿子
        //获取第一级目录
        List<ProductType> children =  getAllChildren(pid);// [1,100]

        //没有儿子
        if(children==null||children.size()==0)
        {
            //没有而自己就返回自己
            return children;
        }
        //有儿子
        for (ProductType child : children) {
            // child: 1
            //查询1的儿子
            List<ProductType> allChildren = treeDataRecursion(child.getId());// 1的儿子:
           // 把1的儿子给1
            child.setChildren(allChildren);

        }
        return children;
    }

    /**
     * 查询指定pid的儿子
     * @param pid
     * @return
     */
    private List<ProductType> getAllChildren(long pid) {
        // select * from t_product_type where pid= ?????
        Wrapper<ProductType> wrapper = new EntityWrapper<>();
        wrapper.eq("pid", pid); //select * from t_product_type where pid = #{pid}
       return  productTypeMapper.selectList(wrapper);
    }


}
