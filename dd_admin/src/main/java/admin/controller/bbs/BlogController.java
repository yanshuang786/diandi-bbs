package admin.controller.bbs;

import com.yan.bbs.entity.vo.BlogVO;
import com.yan.bbs.service.BlogService;
import com.yan.dd_common.core.R;
import com.yan.dd_common.utils.ThrowableUtils;
import com.yan.dd_common.validator.group.GetList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author yanshuang
 * @date 2023/4/28 15:21
 */
@RestController
@RequestMapping("/blog")
@Api(value = "博客相关接口", tags = {"博客相关接口"})
public class BlogController {

    @Autowired
    private BlogService blogService;

    @ApiOperation(value = "获取博客列表", notes = "获取博客列表")
    @PostMapping("/getList")
    public R getList(@Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) throws ExecutionException, InterruptedException {

        ThrowableUtils.checkParamArgument(result);
        return R.success(blogService.getPageList(blogVO));
    }


//    @ApiOperation(value = "增加博客", notes = "增加博客")
//    @PostMapping("/addBlog")
//    public R add(@Validated({Insert.class}) @RequestBody BlogVO blogVO, BindingResult result) {
//
//        // 参数校验
//        ThrowableUtils.checkParamArgument(result);
//        return blogService.addAdminBlog(blogVO);
//    }
//
//    @ApiOperation(value = "本地博客上传", notes = "本地博客上传")
//    @PostMapping("/uploadLocalBlog")
//    public String uploadPics(@RequestBody List<MultipartFile> filedatas) throws IOException {
//
//        return blogService.uploadLocalBlog(filedatas);
//    }
//
//    @ApiOperation(value = "编辑博客", notes = "编辑博客")
//    @PostMapping("/edit")
//    public R edit(@Validated({Update.class}) @RequestBody BlogVO blogVO, BindingResult result) {
//
//        // 参数校验
//        ThrowableUtils.checkParamArgument(result);
//        return blogService.editBlog(blogVO);
//    }
//
//
//    @ApiOperation(value = "推荐博客排序调整", notes = "推荐博客排序调整")
//    @PostMapping("/editBatch")
//    public String editBatch(@RequestBody List<BlogVO> blogVOList) {
//        return blogService.editBatch(blogVOList);
//    }
//
//    @ApiOperation(value = "删除博客", notes = "删除博客", response = String.class)
//    @PostMapping("/delete")
//    public String delete(@Validated({Delete.class}) @RequestBody BlogVO blogVO, BindingResult result) {
//        // 参数校验
//        ThrowableUtils.checkParamArgument(result);
//        return blogService.deleteBlog(blogVO);
//    }
//
//    @ApiOperation(value = "删除选中博客", notes = "删除选中博客")
//    @PostMapping("/deleteBatch")
//    public String deleteBatch(@RequestBody List<BlogVO> blogVoList) {
//        return blogService.deleteBatchBlog(blogVoList);
//    }

}
