package com.inso.modules.admin.controller.game;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.task_checkin.model.TaskCheckinOrderInfo;
import com.inso.modules.game.task_checkin.service.TaskCheckinOrderService;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class TaskCheckinOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private TaskCheckinOrderService mTaskCheckinOrderService;

    @RequiresPermissions("root_game_task_checkin_list")
    @RequestMapping("root_game_task_checkin")
    public String toList(Model model, HttpServletRequest request)
    {
        return "admin/game/game_task_checkin_order_list";
    }

    @RequiresPermissions("root_game_task_checkin_list")
    @RequestMapping("getGameTaskCheckinOrderList")
    @ResponseBody
    public String getGameTaskCheckinOrderList()
    {
        String time = WebRequest.getString("time");

        String orderno = WebRequest.getString("orderno");
        String username = WebRequest.getString("username");

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = -1;
        if(!StringUtils.isEmpty(username))
        {
            UserInfo userInfo = mUserService.findByUsername(false, username);
            if(userInfo == null)
            {
                template.setData(RowPager.getEmptyRowPager());
                return template.toJSONString();
            }
            userid = userInfo.getId();
        }

        RowPager<TaskCheckinOrderInfo> rowPager = mTaskCheckinOrderService.queryScrollPage(pageVo,orderno,userid,-1);

        template.setData(rowPager);
        return template.toJSONString();
    }

}
