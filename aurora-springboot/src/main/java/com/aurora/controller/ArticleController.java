package com.aurora.controller;

import com.aurora.annotation.OptLog;
import com.aurora.model.dto.*;
import com.aurora.enums.FilePathEnum;
import com.aurora.service.ArticleService;
import com.aurora.strategy.context.ArticleImportStrategyContext;
import com.aurora.strategy.context.UploadStrategyContext;
import com.aurora.model.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.util.List;

import static com.aurora.constant.OptTypeConstant.*;

/**
 * 文章模块控制器
 *
 * 前台接口（游客可访问）：
 * - /articles/**：文章列表、文章详情、归档、搜索
 * 后台接口（需要管理员权限）：
 * - /admin/articles/**：文章的增删改查、置顶推荐、导入导出、图片上传
 *
 * 涉及的设计模式：
 * - 策略模式：UploadStrategyContext（图片上传走 MinIO/OSS 策略）、
 *            ArticleImportStrategyContext（文章导入走不同格式策略）
 */
@Api(tags = "文章模块")
@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UploadStrategyContext uploadStrategyContext;

    @Autowired
    private ArticleImportStrategyContext articleImportStrategyContext;

    @ApiOperation("获取置顶和推荐文章")
    @GetMapping("/articles/topAndFeatured")
    public ResultVO<TopAndFeaturedArticlesDTO> listTopAndFeaturedArticles() {
        return ResultVO.ok(articleService.listTopAndFeaturedArticles());
    }

    @ApiOperation("获取所有文章")
    @GetMapping("/articles/all")
    public ResultVO<PageResultDTO<ArticleCardDTO>> listArticles() {
        return ResultVO.ok(articleService.listArticles());
    }

    @ApiOperation("根据分类id获取文章")
    @GetMapping("/articles/categoryId")
    public ResultVO<PageResultDTO<ArticleCardDTO>> getArticlesByCategoryId(@RequestParam Integer categoryId) {
        return ResultVO.ok(articleService.listArticlesByCategoryId(categoryId));
    }

    @ApiOperation("根据id获取文章")
    @GetMapping("/articles/{articleId}")
    public ResultVO<ArticleDTO> getArticleById(@PathVariable("articleId") Integer articleId) {
        return ResultVO.ok(articleService.getArticleById(articleId));
    }

    @ApiOperation("校验文章访问密码")
    @PostMapping("/articles/access")
    public ResultVO<?> accessArticle(@Valid @RequestBody ArticlePasswordVO articlePasswordVO) {
        // 私密文章：校验前端提交的密码是否正确
        articleService.accessArticle(articlePasswordVO);
        return ResultVO.ok();
    }

    @ApiOperation("根据标签id获取文章")
    @GetMapping("/articles/tagId")
    public ResultVO<PageResultDTO<ArticleCardDTO>> listArticlesByTagId(@RequestParam Integer tagId) {
        return ResultVO.ok(articleService.listArticlesByTagId(tagId));
    }

    @ApiOperation("获取所有文章归档")
    @GetMapping("/archives/all")
    public ResultVO<PageResultDTO<ArchiveDTO>> listArchives() {
        return ResultVO.ok(articleService.listArchives());
    }

    @ApiOperation("获取后台文章")
    @GetMapping("/admin/articles")
    public ResultVO<PageResultDTO<ArticleAdminDTO>> listArticlesAdmin(ConditionVO conditionVO) {
        // 后台文章列表（含逻辑删除的文章、条件查询）
        return ResultVO.ok(articleService.listArticlesAdmin(conditionVO));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @ApiOperation("保存和修改文章")
    @PostMapping("/admin/articles")
    public ResultVO<?> saveOrUpdateArticle(@Valid @RequestBody ArticleVO articleVO) {
        // 新增/修改文章（同一接口，通过 id 是否为空判断）
        articleService.saveOrUpdateArticle(articleVO);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @ApiOperation("修改文章是否置顶和推荐")
    @PutMapping("/admin/articles/topAndFeatured")
    public ResultVO<?> updateArticleTopAndFeatured(@Valid @RequestBody ArticleTopFeaturedVO articleTopFeaturedVO) {
        articleService.updateArticleTopAndFeatured(articleTopFeaturedVO);
        return ResultVO.ok();
    }

    @ApiOperation("删除或者恢复文章")
    @PutMapping("/admin/articles")
    public ResultVO<?> updateArticleDelete(@Valid @RequestBody DeleteVO deleteVO) {
        // 逻辑删除/恢复（isDelete 字段翻转）
        articleService.updateArticleDelete(deleteVO);
        return ResultVO.ok();
    }

    @OptLog(optType = DELETE)
    @ApiOperation(value = "物理删除文章")
    @DeleteMapping("/admin/articles/delete")
    public ResultVO<?> deleteArticles(@RequestBody List<Integer> articleIds) {
        // 彻底从数据库删除（逻辑删除的才走这里）
        articleService.deleteArticles(articleIds);
        return ResultVO.ok();
    }

    @OptLog(optType = UPLOAD)
    @ApiOperation("上传文章图片")
    @ApiImplicitParam(name = "file", value = "文章图片", required = true, dataType = "MultipartFile")
    @PostMapping("/admin/articles/images")
    public ResultVO<String> saveArticleImages(MultipartFile file) {
        // 走上传策略（MinIO/OSS），返回图片URL
        return ResultVO.ok(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.ARTICLE.getPath()));
    }

    @ApiOperation("根据id查看后台文章")
    @ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "Integer")
    @GetMapping("/admin/articles/{articleId}")
    public ResultVO<ArticleAdminViewDTO> getArticleBackById(@PathVariable("articleId") Integer articleId) {
        // 后台编辑文章时，查出文章完整信息（含标签、分类）
        return ResultVO.ok(articleService.getArticleByIdAdmin(articleId));
    }

    @OptLog(optType = UPLOAD)
    @ApiOperation(value = "导入文章")
    @PostMapping("/admin/articles/import")
    public ResultVO<?> importArticles(MultipartFile file, @RequestParam(required = false) String type) {
        // 批量导入文章（Markdown等格式，走导入策略）
        articleImportStrategyContext.importArticles(file, type);
        return ResultVO.ok();
    }

    @OptLog(optType = EXPORT)
    @ApiOperation(value = "导出文章")
    @ApiImplicitParam(name = "articleIdList", value = "文章id", required = true, dataType = "List<Integer>")
    @PostMapping("/admin/articles/export")
    public ResultVO<List<String>> exportArticles(@RequestBody List<Integer> articleIds) {
        // 导出文章为文件（返回文件URL列表）
        return ResultVO.ok(articleService.exportArticles(articleIds));
    }

    @ApiOperation(value = "搜索文章")
    @GetMapping("/articles/search")
    public ResultVO<List<ArticleSearchDTO>> listArticlesBySearch(ConditionVO condition) {
        // 全文搜索（走 Elasticsearch，高亮关键词）
        return ResultVO.ok(articleService.listArticlesBySearch(condition));
    }

}
