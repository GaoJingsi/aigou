package cn.itsource.aigou.controller;

import cn.itsource.aigou.service.IBrandService;
import cn.itsource.aigou.domain.Brand;
import cn.itsource.aigou.query.BrandQuery;
import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // restful风格
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    public IBrandService brandService;

    /**
     * 保存和修改公用的
     *
     * @param brand 传递的实体
     * @return Ajaxresult转换结果
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody Brand brand) {
        try {
            if (brand.getId() != null) {
                brandService.updateById(brand);
            } else {
                brandService.insert(brand);
            }
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setMsg("保存对象失败！" + e.getMessage());
        }
    }

    /**
     * 删除对象信息
     *  controller方法的地址:= 类上的requestmapper的值+方法的requestmapping的值:
     *  @RequestMapping("/brand") +@RequestMapping(value = "/{id}"
     *   "/brand/{id}"
     *  restful风格L:
     *   地址不应该有动词:定位资源===>只是一个规范,没有法律效应,可以随便写的.
     *   而真正做事情的是:http的请求:
     *  GET  http://localhost/user/1  ==>获取一个用户
     *  delete  http://localhost/user/1  ==>根据id删除一个用户
     *  get  http://localhost/users  ==>获取所有的用户
     *  ....
     *
     *
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)  //"brand/{id}"
    public AjaxResult delete(@PathVariable("id") Long id) {
        try {
            brandService.deleteById(id);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setMsg("删除对象失败！" + e.getMessage());
        }
    }


    // "/brand/{id}"
    //获取用户
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Brand get(@PathVariable("id") Long id) {
        return brandService.selectById(id);
    }


    /**
     * 查看所有的员工信息
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Brand> list() {

        return brandService.selectList(null);
    }


    /**
     * 分页查询数据
     *
     * @param query 查询对象
     * @return PageList 分页对象
     */
    @RequestMapping(value = "/json", method = RequestMethod.POST)
    public PageList<Brand> json(@RequestBody BrandQuery query) {

         // page是mp的:mp对多表的关联查询不尽人意,我们用以前的方式自己写:
        //定义一个service:query-->pageList ===>Mapper接口:定义这样一个方法===>Mapper.xml:
        //xml: sql的关联查询: 需要封装一个关联对象,也要做条件:
       /* Page<Brand> page = new Page<Brand>(query.getPage(), query.getRows());
      //查询条件:
       Wrapper w=new EntityWrapper<>();
        w.like("name", query.getKeyword());
        // sql==>
        page = brandService.selectPage(page);*/

        PageList<Brand> pageList= brandService.queryPage(query);
        return pageList;
       /* Page<Brand> page = new Page<Brand>(query.getPage(), query.getRows());
        page = brandService.selectPage(page);
        return new PageList<Brand>(page.getTotal(), page.getRecords());*/
    }
}
