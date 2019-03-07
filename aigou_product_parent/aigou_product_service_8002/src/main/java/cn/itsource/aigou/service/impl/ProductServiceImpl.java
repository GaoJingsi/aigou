package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.domain.ProductExt;
import cn.itsource.aigou.domain.Sku;
import cn.itsource.aigou.mapper.ProductExtMapper;
import cn.itsource.aigou.mapper.ProductMapper;
import cn.itsource.aigou.mapper.SkuMapper;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.util.PageList;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private SkuMapper skuMapper;

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

    @Override
    public void addSku(Object productId, List<Map<String, Object>> skuProperties, List<Map<String, Object>> skuDatas) {
        //1:通过productId根据ProductExt表中的SkuProperties字段
        ProductExt entity =new ProductExt();
        entity.setSkuProperties(JSONArray.toJSONString(skuProperties));

        Wrapper<ProductExt> wrapper=new EntityWrapper<>();
        wrapper.eq("productId", productId);
        productExtMapper.update(entity,wrapper );

        //2:保存sku表:一个是sku表的自身的字段:价格,库存等... 还有就是SkuProperties,skuIndex
        // skuDatas:是多个sku值的数组
    /*    [
        {颜色=yellow, 尺寸=26, price=26, availableStock=26},
        {颜色=yellow, 尺寸=96, price=96, availableStock=96},
        {颜色=green, 尺寸=26, price=62, availableStock=62},
        {颜色=green, 尺寸=96, price=69, availableStock=69}
 ]*/

        for (Map<String, Object> skuData : skuDatas) {
            //是一个sku对象:
            Sku sku = new Sku();
            // 2.1:设置productId
            sku.setProductId(Long.valueOf(productId.toString()));

            Set<Map.Entry<String, Object>> skuDataEntry = skuData.entrySet();
            // skuData={颜色=yellow, 尺寸=26, price=26, availableStock=26},
            List< Map<String,Object>> otherList = new ArrayList<>();//装sku值中对应的属性表中的相关信息
            for (Map.Entry<String, Object> entry : skuDataEntry) {
                //每次应该重新创建一个map
                Map<String,Object> otherMap = new HashMap<>();
                String key = entry.getKey();
                Object value = entry.getValue();
                // 颜色=yellow
                //2.2:自身的属性设置:
                if("price".equals(key)){
                    sku.setPrice(Integer.valueOf(value.toString()));
                }else if("availableStock".equals(key)){
                    sku.setAvailableStock(Integer.valueOf(value.toString()));
                }else{
                    //其它的属性 颜色=yellow
                    otherMap.put(key, value);
                    otherList.add(otherMap);
                }
            }


            //============skuProperties设置开始============
            // otherList:[{"颜色":"yellow"},{"尺寸":"26"}]
            //最终的skuProperties的值: [{"id":33,"key":"颜色","value":"yellow"}]
            List<Map<String, Object>> skuList=new ArrayList<>();
            System.out.println("otherList==="+otherList);
            for (Map<String, Object> om : otherList) {
                // {"颜色":"yellow"}
                Map<String,Object> mm = new HashMap<>();
                Set<Map.Entry<String, Object>> entries = om.entrySet();
                String properKey ="";
                for (Map.Entry<String, Object> entry : entries) {
                    properKey=entry.getKey();
                    mm.put("key",properKey );
                    mm.put("value",entry.getValue() );
                }
                Long propertyId = getPropId(properKey,skuProperties);
                mm.put("id",propertyId );
                skuList.add(mm);
            }
            //设置SkuProperties:
            sku.setSkuProperties(JSONArray.toJSONString(skuList));
            //============skuProperties设置结束============

            //============skuIndex设置开始============
            //
            StringBuffer sb = new StringBuffer();
            for (Map<String, Object> om : skuList) {
                // om : {"id":33,"key":"颜色","value":"yellow"}
                //获取属性id
                Object proId = om.get("id");
                Object value = om.get("value");
                Integer index = getIndex(proId,value,skuProperties);
                System.out.println("idnex=="+index);
                sb.append(index).append("_");
            }
            // sb 1_2_4_
            //去掉最后一个_
            String sbStr =   sb.toString();
            sbStr =  sbStr.substring(0, sb.lastIndexOf("_"));
            sku.setSkuIndex(sbStr);
            System.out.println("============skuIndex设置结束============");

            //sku的保存:在前面都是在构造这个sku的各个字段值
            skuMapper.insert(sku);

        }




    }

    /**
     *
     * @param proId 属性的id
     * @param value 属性的value
     * @param skuProperties list
     * @return
     */
    private Integer getIndex(Object proId , Object value, List<Map<String, Object>> skuProperties) {
        for (Map<String, Object> skuProperty : skuProperties) {
            //{id=33, specName=颜色, type=2, productTypeId=9, value=null,skuValues=[yellow, green]}
            Long id = Long.valueOf( skuProperty.get("id").toString());
            Long pro = Long.valueOf(proId.toString());
            // java.lang.Integer cannot be cast to java.lang.Long
            if(id.longValue()==pro.longValue()){
                List<String> skuValues = (List<String>) skuProperty.get("skuValues");
                int index = 0;
                for (String skuValue : skuValues) {
                    if(skuValue.equals(value.toString())){
                        return index;
                    }
                    index++;
                }

            }
        }
        return null;
    }

    /**
     * 根据属性的key获取这个属性的id
     * @param properKey
     * @param skuProperties
     * @return
     */
    private Long getPropId(String properKey, List<Map<String, Object>> skuProperties) {
        for (Map<String, Object> skuProperty : skuProperties) {
            String specName = (String)skuProperty.get("specName");
            if(specName.equals(properKey)){
                return   Long.valueOf(skuProperty.get("id").toString());
            }
        }
        return null;
    }
}
