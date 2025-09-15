package com.inso.modules.passport.returnwater;

import com.alibaba.druid.util.LRUCache;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.user.model.InviteFriendStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.business.service.DayPresentOrderService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 邀请好友管理器
 */
@Component
public class ReturnWaterSelfManager {





}
