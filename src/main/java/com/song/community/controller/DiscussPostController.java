package com.song.community.controller;

import com.song.community.entity.Comment;
import com.song.community.entity.DiscussPost;
import com.song.community.entity.User;
import com.song.community.service.CommentService;
import com.song.community.service.DiscussPostService;
import com.song.community.service.UserService;
import com.song.community.utils.CommunityConstant;
import com.song.community.utils.CommunityUtils;
import com.song.community.utils.HostHolder;
import com.song.community.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author Kycni
 * @date 2022/2/24 20:09
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        // 对用户登录状态进行判断
        if (user == null) {
            return CommunityUtils.getJSONString(403, "您还没有登录哦");   
        }
        // 设置帖子信息
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateDate(new Date());
        // 将帖子插入数据库
        discussPostService.addDiscussPost(discussPost);
        
        return CommunityUtils.getJSONString(0, "发布成功!");
    }
    
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDetailPage (@PathVariable("discussPostId") int discussPostId,
                                 Model model, Page page) {
        // 显示详情页
        DiscussPost post = discussPostService.findDiscussPost(discussPostId);
        if (post!=null) {
            User user = userService.findUserById(post.getUserId());
            model.addAttribute("post", post);
            model.addAttribute("user", user);

            // 显示评论列表
            page.setLimit(5);
            page.setRows(post.getCommentCount());
            page.setPath("/discuss/detail/" + discussPostId);
            // 评论帖子列表
            List<Comment> commentList = 
                    commentService.findCommentList(COMMENT_POST,discussPostId, page.getOffset(),page.getLimit());
            List<Map<String, Object>> commentVoList = new ArrayList<>();
            if (commentList!=null) {
                for (Comment comment : commentList) {
                    Map<String, Object> commentVo = new HashMap<>();
                    commentVo.put("comment", comment);
                    commentVo.put("user", userService.findUserById(comment.getUserId()));
                    
                    // 评论回复列表
                    List<Comment> replyList = commentService.findCommentList(COMMENT_REPLY, comment.getId(), 0, Integer.MAX_VALUE);
                    List<Map<String, Object>> replyVoList = new ArrayList<>();
                    if (replyList != null) {
                        for (Comment reply : replyList) {
                            Map<String,Object> replyVo = new HashMap<>();
                            replyVo.put("reply", reply);
                            replyVo.put("user", userService.findUserById(reply.getUserId()));
                            User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                            replyVo.put("target", target);
                            replyVoList.add(replyVo);
                        }
                    }
                    commentVo.put("replies", replyVoList);
                    // 回复数
                    int replyCount = commentService.findCommentCount(COMMENT_REPLY, comment.getId());
                    commentVo.put("replyCount", replyCount);
                    commentVoList.add(commentVo);
                }
            }
            model.addAttribute("comments", commentVoList);
        }
        return "/site/discuss-detail";
    }
}
