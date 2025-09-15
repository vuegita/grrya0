package com.inso.modules.passport.invite_stats.controller;

import com.inso.framework.spring.utils.ServerUtils;
import com.inso.framework.utils.Base64Utils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.invite_stats.InviteStatsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/passport/userApi")
@Controller
public class InviteController {

    @Autowired
    private InviteStatsManager mInviteStatsManager;

    @RequestMapping(path = "/register/{key}/{toUrl}")
    public String actionToPayinCallback(@PathVariable(value = "key") String key, @PathVariable(value = "toUrl") String toUrl)
    {
        String decodeUrl = null;
        if(!StringUtils.isEmpty(toUrl))
        {
            decodeUrl = Base64Utils.decode(toUrl);
        }

        if(!StringUtils.isEmpty(key))
        {
            mInviteStatsManager.addCount(key);
        }

        if(!StringUtils.isEmpty(decodeUrl))
        {
            return "redirect:" + decodeUrl;
        }
        String baseUrl = ServerUtils.getDomain();
        return "redirect:" + baseUrl;
    }

    @RequestMapping(path = "/register2/{key}")
    public String register2(@PathVariable(value = "key") String key)
    {
        if(StringUtils.isEmpty(key) || !RegexUtils.isLetterDigit(key))
        {
            return null;
        }

        if(!StringUtils.isEmpty(key))
        {
            mInviteStatsManager.addCount(key);
        }

        // https://sunjogo.com/?page=register&ref=3c0sjy || https://sunjogo.com/passport/userApi/register2/3c0sjy
        String baseUrl = ServerUtils.getDomain();
        String url = baseUrl + "?page=register&ref=" + key;
        return "redirect:" + url;
    }

}
