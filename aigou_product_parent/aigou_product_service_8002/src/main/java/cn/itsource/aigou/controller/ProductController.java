package cn.itsource.aigou.controller;

import cn.itsource.aigou.doc.ProductDoc;
import cn.itsource.aigou.domain.ProductExt;
import cn.itsource.aigou.domain.Specification;
import cn.itsource.aigou.service.IProductExtService;
import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.service.ISpecificationService;
import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.PageList;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    public IProductService productService;

    @Autowired
    public ISpecificationService specificationService;

    @Autowired
    public IProductExtService productExtService;

    /**
     * 保存和修改公用的
     *
     * @param product 传递的实体
     * @return Ajaxresult转换结果
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody Product product) {
        //放置我的数据本来是上架状态:绕过前端的验证,应该在这里做一个验证:
        // 通过id去查询数据库:看我数据库的状态是下架才能让他修改,如果是上架状态:抛一个异常出去.
        ///
        try {
            if (product.getId() != null) {
                productService.updateById(product);
            } else {
                productService.insert(product);
            }
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setMsg("保存对象失败！" + e.getMessage());
        }
    }

    /**
     * 删除对象信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable("id") Long id) {
        try {
            productService.deleteById(id);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setMsg("删除对象失败！" + e.getMessage());
        }
    }

    //获取用户
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Product get(@PathVariable("id") Long id) {
        return productService.selectById(id);
    }


    /**
     * 查看所有的员工信息
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Product> list() {

        return productService.selectList(null);
    }


    /**
     * 分页查询数据
     *
     * @param query 查询对象
     * @return PageList 分页对象
     */
    @RequestMapping(value = "/json", method = RequestMethod.POST)
    public PageList<Product> json(@RequestBody ProductQuery query) {
        //自己的关联查询:
        /*Page<Product> page = new Page<Product>(query.getPage(),query.getRows());
            page = productService.selectPage(page);
            return new PageList<Product>(page.getTotal(),page.getRecords());*/

        return productService.selectQuery(query);
    }
    //sku的属性的获取

    /**
     * sku的属性的获取:
     *
     * @param productTypeId
     * @return
     */
    @RequestMapping(value = "/skuProperties/{productTypeId}", method = RequestMethod.GET)
    public List<Specification> skuProperties(@PathVariable("productTypeId") Long productTypeId) {
        //要判断是新增还是修改: 判断是对当前产品的显示属性是添加还是新增:
        List<Specification> specifications = getSpecifications(productTypeId, 2L);
        return specifications;

    }

    /**
     * let params =
     * {"productId": productId, "skuProperties": this.skuProperties,"skuDatas":this.skuDatas};
     * <p>
     * productId:63
     * skuProperties:
     * [
     * {id=33, specName=颜色, type=2, productTypeId=9, value=null,skuValues=[yellow, green]},
     * {id=34, specName=尺寸, type=2, productTypeId=9, value=null, skuValues=[26, 96]}
     * ]
     * <p>
     * skuDatas:
     * [
     * {颜色=yellow, 尺寸=26, price=26, availableStock=26},
     * {颜色=yellow, 尺寸=96, price=96, availableStock=96},
     * {颜色=green, 尺寸=26, price=62, availableStock=62},
     * {颜色=green, 尺寸=96, price=69, availableStock=69}
     * ]
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/skuProperties", method = RequestMethod.POST)
    public AjaxResult saveSkuProperties(@RequestBody Map<String, Object> map) {
        try {
            //目的是更新:t_product_ext的viewProperties
            //1:接收前台的数据  java.lang.Integer cannot be cast to java.lang.Long
            Object productId = map.get("productId");
            System.out.println("productId:" + productId);// productId:63
            //前台传过来的添加的显示属性的list
            List<Map<String, Object>> skuProperties = (List<Map<String, Object>>) map.get("skuProperties");// List<>
            System.out.println("skuProperties:" + skuProperties);
            // skuDatas
            List<Map<String, Object>> skuDatas = (List<Map<String, Object>>) map.get("skuDatas");
            System.out.println("skuDatas:" + skuDatas);

            //调用方法:
            productService.addSku(productId, skuProperties, skuDatas);
            return AjaxResult.me().setMsg("显示属性保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMsg("显示属性保存失败:" + e.getMessage());
        }

    }

    /**
     * 查询属性表的公共抽取
     *
     * @param productTypeId 商品类型
     * @param type          显示属性还是sku属性:  type=1  显示属性;type=2  sku属性
     * @return
     */
    private List<Specification> getSpecifications(Long productTypeId, Long type) {
        //没有就是添加:
        Wrapper<Specification> wrapper = new EntityWrapper<>();
        wrapper.eq("product_type_id", productTypeId);
        wrapper.eq("type", type);
        // specifications的多个值:
        List<Specification> specifications = specificationService.selectList(wrapper);
        for (Specification specification : specifications) {
            System.out.println(specification);
        }
        return specifications;
    }

    //根据产品分类获取这个分类的显示属性:服务是给前台调用:不搞feign
    @RequestMapping(value = "/viewProperties/{productTypeId}/{productId}", method = RequestMethod.GET)
    public List<Specification> viewProperties(@PathVariable("productTypeId") Long productTypeId,
                                              @PathVariable("productId") Long productId) {
        //要判断是新增还是修改: 判断是对当前产品的显示属性是添加还是新增:
        //productExt表中有viewProperties:修改 :需要前台传productId给我
        ProductExt productExt = productExtService.selectOne(new EntityWrapper<ProductExt>().eq("productId", productId));

        //str == null || "".equals(str);
        if (productExt != null && !StringUtils.isEmpty(productExt.getViewProperties())) {
            //有显示属性:是修改
            String strArrJson = productExt.getViewProperties();
            return JSONArray.parseArray(strArrJson, Specification.class);
        } else {
            //没有就是添加:
            List<Specification> specifications = getSpecifications(productTypeId, 1L);
            //specifications:
            return specifications;
        }


    }

    //// 把这个存到t_product_ext表的一个viewProperties,需要根据productId进行过滤:
    // 后台要接收多个参数,我们提交一般用post请求,那么可以通过RequestBody接收前台参数
    //但是RequestBody只能有一个识别才ok,我这里有多个参数,不行.应该考虑把我们参数的接收变成一个接收对象:
    //方案1:提供一个domain,设置前台传递的两个属性 方案2:使用map
    @RequestMapping(value = "/viewProperties", method = RequestMethod.POST)
    public AjaxResult saveViewProperties(@RequestBody Map<String, Object> map) {
        try {
            //目的是更新:t_product_ext的viewProperties
            //1:接收前台的数据  java.lang.Integer cannot be cast to java.lang.Long
            Object productId = map.get("productId");
            //前台传过来的添加的显示属性的list
            List<Specification> viewProperties = (List<Specification>) map.get("viewProperties");// List<>

            //2:viewProperties转换成json字符:
            String toJSONString = JSONArray.toJSONString(viewProperties);

            //3:更新到productExt的viewProperties中:需要的就是json的字符串
            ProductExt productExt = new ProductExt();
            productExt.setViewProperties(toJSONString);
            productExtService.update(productExt, new EntityWrapper<ProductExt>().eq("productId", productId));
            return AjaxResult.me().setMsg("显示属性保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMsg("显示属性保存失败:" + e.getMessage());
        }

    }


    /**
     * 上下架的操作
     *
     * @param map ids:操作的id   1,2,3    ;  optType:1 上架请求   2下架请求
     * @return
     */
    @RequestMapping(value = "/productSale", method = RequestMethod.POST)
    public AjaxResult productSale(@RequestBody Map<String, Object> map) {
        Object ids1 = map.get("ids");
        Object optType = map.get("optType");
        if (ids1 != null && optType != null) {
            try {
                String ids = ids1.toString();
                Long opt = Long.valueOf(optType.toString());
                if (opt == 1) {
                    productService.onSale(ids,opt);
                } else if (opt == 2) {
                    productService.offSale(ids,opt);
                }
                return AjaxResult.me().setSuccess(true).setMsg("上下架成功");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return AjaxResult.me().setSuccess(false).setMsg("上下架失败");
            }

        } else {
            return AjaxResult.me().setSuccess(false).setMsg("请传入正确请求参数");
        }

    }

    //商品高级查询:"/product/product/queryProducts",this.queryParams)

    /**
     * 品牌,分类,价格:最高价和最低价,排序的字段,和排序的方式,查询关键字
     "keyword":'',
     "productType":null,
     "brandId":null,
     "priceMin":null,
     "priceMax":null,
     "sortField":'',
     "sortType":"desc",
     "page":1,
     "rows":12
     * @param parmas
     */
    @RequestMapping(value = "/queryProducts",method = RequestMethod.POST)
    public PageList<ProductDoc>  queryProductFromEs(@RequestBody Map<String,Object> parmas){
        //调用es的查询
      return   productService.queryProductFromEs(parmas);

    }
}
