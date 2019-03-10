package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.client.PageStaticClient;
import cn.itsource.aigou.client.RedisClient;
import cn.itsource.aigou.constants.GlobelConstants;
import cn.itsource.aigou.domain.Brand;
import cn.itsource.aigou.domain.ProductType;
import cn.itsource.aigou.mapper.BrandMapper;
import cn.itsource.aigou.mapper.ProductTypeMapper;
import cn.itsource.aigou.service.IProductTypeService;
import cn.itsource.aigou.util.StrUtils;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Autowired
    private BrandMapper brandMapper;


    //注入RedisClient:
    @Autowired
    private RedisClient redisClient;


    @Autowired
    private PageStaticClient pageStaticClient;


    /**
     * @return
     */
   /* @Override
    public List<ProductType> treeData() {
            // 要得到name和儿子

        //return treeDataRecursion(0L);
        return treeDataLoop();

    }*/

    @Override
    public List<ProductType> treeData() {
        //先根据key,从redis获取:我是producttype的服务提供者,我要调用公共服务的redis,则是redis的消费者:
        //java内部的服务的调用,就应该使用feign或者ribbon:选中feign:
        //feign的使用:是在消费者,注入接口,就象调用本地接口一样

        //判断是否有结果:有就直接返回,没有就从数据库获取,存入redis,并返回
        String jsonArrStr = redisClient.get(GlobelConstants.REDIS_PRODUCTTYPE_KEY);
        if(StringUtils.isEmpty(jsonArrStr)){
            //没有就从数据库获取,存入redis,并返回
            List<ProductType> productTypes = treeDataLoop();
            jsonArrStr= JSONArray.toJSONString(productTypes);
            //redis存入
            redisClient.set(GlobelConstants.REDIS_PRODUCTTYPE_KEY,jsonArrStr );
            System.out.println("from========db===============");
            return productTypes;
        }else{
            //有:有就直接返回
            //json的数组字符串--->json数组
            System.out.println("from========cache===============");
            return JSONArray.parseArray(jsonArrStr, ProductType.class);
        }

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


    @Override
    public boolean updateById(ProductType entity) {
        //修改:本身数据的修改不会变;修改完后,重新生成模板:
        //1:数据修改:
        boolean b = super.updateById(entity);

        //2:模板的生成:此时此时,这个是模板的消费者:消费模板的提供者:
        //这个是java后台内部的服务的消费:feign/ribbon(采纳feign)
        //feign:注入模板接口,调用

        //逻辑实现:
        //2.1:先生成改变数据的html页面:productType
        Map<String,Object> mapProductType=new HashMap<>();
        List<ProductType> productTypes = treeDataLoop();
        mapProductType.put(GlobelConstants.PAGE_MODE, productTypes);//这里页面需要的是所有的产品类型数据
        //哪一个模板
        mapProductType.put(GlobelConstants.PAGE_TEMPLATE, "F:\\idea\\aigou_parent\\aigou_common_parent\\aigou_common_interface\\src\\main\\resources\\template\\product.type.vm");
        //根据模板生成的页面的地址:
        mapProductType.put(GlobelConstants.PAGE_TEMPLATE_HTML, "F:\\idea\\aigou_parent\\aigou_common_parent\\aigou_common_interface\\src\\main\\resources\\template\\product.type.vm.html");

        pageStaticClient.getPageStatic(mapProductType);

        //2.2:再生成home的html页面:
        Map<String,Object> mapHome=new HashMap<>();
        //数据:$model.staticRoot
        Map<String,String> staticRootMap=new HashMap<>();
        staticRootMap.put("staticRoot", "F:\\idea\\aigou_parent\\aigou_common_parent\\aigou_common_interface\\src\\main\\resources\\");
        mapHome.put(GlobelConstants.PAGE_MODE, staticRootMap);//这里页面需要的是目录的根路径
        //哪一个模板
        mapHome.put(GlobelConstants.PAGE_TEMPLATE, "F:\\idea\\aigou_parent\\aigou_common_parent\\aigou_common_interface\\src\\main\\resources\\template\\home.vm.ban");
        //根据模板生成的页面的地址:
        mapHome.put(GlobelConstants.PAGE_TEMPLATE_HTML, "F:\\idea\\aigou-vue-web\\aigou-web\\home.html");

        pageStaticClient.getPageStatic(mapHome);

        return b;
    }


    @Override
    public List<Map<String, Object>> getCrumbs(Long productTypeId) {
        List<Map<String, Object>> mapList=new ArrayList<>();
        //1:通过productTypeId获取到这条数据:  id 3     pid  2  path .1.2.3.
        ProductType productType = productTypeMapper.selectById(productTypeId);

        //2:获取到所有的层级:path
        String path = productType.getPath();
        List<Long> longs = StrUtils.splitStr2LongArr(path, "\\.");// [1,2,3]

        for (Long id : longs) {
            //2.1:组装每一个对象的自己和他的兄弟姐妹
            Map<String,Object> map=new HashMap<>();
            // 1,2,3
            // 2.1.1:获取到自己
            ProductType own = productTypeMapper.selectById(id);
            Long pid = own.getPid();//自己的老子:
            //2.1.2:找自己的老子的所有的儿子(包含了自己):
            List<ProductType> productTypeList = productTypeMapper.selectList(new EntityWrapper<ProductType>().eq("pid", pid));
            for (ProductType productType1 : productTypeList) {
                //2.1.3:根据id来判断是否是自己:获取自己的兄弟姐妹
                Long currentId = productType1.getId();
                if(currentId.longValue()==own.getId().longValue()){
                    //移除自己
                    productTypeList.remove(productType1);
                    break;
                }
            }
            map.put("ownerProductType", own);//自己
            map.put("otherProductTypes", productTypeList);//自己的兄弟姐妹
            mapList.add(map);
        }

        return mapList;
    }

    @Override
    public List<Brand> getBrands(Long productTypeId) {
        //通过分类获取他的所有的品牌:  分类和品牌关系:   1:*
        return  brandMapper.selectList(new EntityWrapper<Brand>().eq("product_type_id", productTypeId));
    }

}
