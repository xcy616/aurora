package com.aurora.controller;

import com.aurora.annotation.AccessLimit;
import com.aurora.annotation.OptLog;
import com.aurora.model.dto.CommentAdminDTO;
import com.aurora.model.dto.CommentDTO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.dto.ReplyDTO;
import com.aurora.service.CommentService;
import com.aurora.model.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.aurora.constant.OptTypeConstant.*;

/**
 * 评论模块控制器
 *
 * 前台：发表评论（带限流）、获取评论、回复、热门评论
 * 后台：评论列表、审核、删除（管理员权限）
 */
@Api(tags = "评论模块")
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @AccessLimit(seconds = 60, maxCount = 3)  // 限流：60秒内最多评论3次
    @OptLog(optType = SAVE)
    @ApiOperation("添加评论")
    @PostMapping("/comments/save")
    public ResultVO<?> saveComment(@Valid @RequestBody CommentVO commentVO) {
        commentService.saveComment(commentVO);
        return ResultVO.ok();
    }

    @ApiOperation("获取评论")
    @GetMapping("/comments")
    public ResultVO<PageResultDTO<CommentDTO>> getComments(CommentVO commentVO) {
        // 根据评论类型获取（文章评论/友链评论/说说评论）
        return ResultVO.ok(commentService.listComments(commentVO));
    }

    @ApiOperation(value = "根据commentId获取回复")
    @GetMapping("/comments/{commentId}/replies")
    public ResultVO<List<ReplyDTO>> listRepliesByCommentId(@PathVariable("commentId") Integer commentId) {
        return ResultVO.ok(commentService.listRepliesByCommentId(commentId));
    }

    @ApiOperation("获取前六个评论")
    @GetMapping("/comments/topSix")
    public ResultVO<List<CommentDTO>> listTopSixComments() {
        // 首页展示的热门评论
        return ResultVO.ok(commentService.listTopSixComments());
    }

    @ApiOperation(value = "查询后台评论")
    @GetMapping("/admin/comments")
    public ResultVO<PageResultDTO<CommentAdminDTO>> listCommentBackDTO(ConditionVO conditionVO) {
        // 后台评论管理：分页 + 条件查询
        return ResultVO.ok(commentService.listCommentsAdmin(conditionVO));
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "审核评论")
    @PutMapping("/admin/comments/review")
    public ResultVO<?> updateCommentsReview(@Valid @RequestBody ReviewVO reviewVO) {
        // 开启评论审核时，管理员通过/驳回评论
        commentService.updateCommentsReview(reviewVO);
        return ResultVO.ok();
    }

    @OptLog(optType = DELETE)
    @ApiOperation(value = "删除评论")
    @DeleteMapping("/admin/comments")
    public ResultVO<?> deleteComments(@RequestBody List<Integer> commentIdList) {
        commentService.removeByIds(commentIdList);
        return ResultVO.ok();
    }

}
