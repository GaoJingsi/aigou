package cn.itsource.aigou.controller;


import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.FastDfsApiOpr;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/common")
public class FastDfsController {


    /**
     * 文件的上传
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public AjaxResult upload(@RequestBody MultipartFile file) {
        //原始名字
        String originalFilename = file.getOriginalFilename();
        System.out.println(originalFilename);// 77.jpg
        String extName = FilenameUtils.getExtension(originalFilename);
        System.out.println(extName);// jpg
        //FastDfs的工具类的封装:
        try {
            String filePath = FastDfsApiOpr.upload(file.getBytes(), extName);
            return AjaxResult.me().setSuccess(true).setMsg("上传成功").setObject(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMsg("上传失败:" + e.getMessage());
        }

    }

    /**
     * 删除思路:
     * 要告诉人家删哪一个东西:
     * ip/groupname/filename
     *
     * 服务接口定义地址: /common/delete/{filePath}
     * 在访问的时候:输入的请求地址: /common/delete/group1/M00/00/01/rBAHllx6l32AHfQtAAEu5hxxfm4554.jpg,
     *  由于我们的参数中本身就有/,对请求路径进行了干扰,所以我们换一个思路:
     *
     *
     *  我们不在请求地址中直接传我的参数,使用key--value的形式传
     *  http://localhost:9527/aigou/common/common/delete?filePath=group1%2FM00%2F00%2F01%2FrBAHllx6l32AHfQtAAEu5hxxfm4554.jpg
     *
     * @return
     */

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public AjaxResult delete(@RequestParam("filePath") String filePath) {
        // filePath  = /group1/M00/00/01/rBAHllx6l32AHfQtAAEu5hxxfm4554.jpg
        // group1/M00/00/01/rBAHllx6l32AHfQtAAEu5hxxfm4554.jpg
        try {
            // 前台在传的时候: 第一个是 /
            // filePath = /group1/M00/00/01/rBAHllx6scGAUV9WAACIceIBrog953.jpg
            System.out.println(filePath);
            String substring = filePath.substring(1);//  group1/M00/00/01/rBAHllx6scGAUV9WAACIceIBrog953.jpg
            String groupName = substring.substring(0, substring.indexOf("/"));
            //   group1
            //  M00/00/01/rBAHllx6l32AHfQtAAEu5hxxfm4554.jpg
            // 从开始位置截取到最后
            String fileName = substring.substring(substring.indexOf("/") + 1);

            int deleteResult = FastDfsApiOpr.delete(groupName, fileName);
            if (deleteResult == 0) {
                //删除成功
                return AjaxResult.me().setSuccess(true).setMsg("删除成功");
            } else {
                return AjaxResult.me().setSuccess(false).setMsg("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMsg("删除失败:" + e.getMessage());
        }

    }

    public static void main(String[] args) {
        String filePath = "group1/M00/00/01/rBAHllx6l32AHfQtAAEu5hxxfm4554.jpg";

        // group1
        //  filePath.indexOf("/")  获取filePath字符串中第一个"/"的位置(索引) substring(int beginIndex, int endIndex))
        String substring = filePath.substring(0, filePath.indexOf("/"));

        //   group1
        //  M00/00/01/rBAHllx6l32AHfQtAAEu5hxxfm4554.jpg
        // 从开始位置截取到最后
        String substring1 = filePath.substring(filePath.indexOf("/") + 1);

        System.out.println(substring);

        System.out.println(substring1);

    }
}
