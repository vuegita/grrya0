package com.inso.modules.admin.core.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.google.GoogleAuthenticator;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.AdminErrorResult;
import com.inso.modules.admin.core.helper.CoreAdminHelper;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.Role;
import com.inso.modules.admin.core.service.AdminService;
import com.inso.modules.admin.core.service.RoleService;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.web.eventlog.EventLogManager;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import jodd.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class AdminController {

    @Autowired
    private AdminService mAdminService;

    @Autowired
    private RoleService mRoleService;

    @RequiresPermissions("root_sys_admin_list")
    @RequestMapping("root_sys_admin")
    public String toAdminListPage()
    {

        return "admin/core/sys/admin_list";
    }

    @RequiresPermissions("root_sys_admin_list")
    @RequestMapping("getAdminList")
    @ResponseBody
    public String getAdminList()
    {
        String loginAdmin = CoreAdminHelper.getAdminName();

        String username = WebRequest.getString("username");
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        ApiJsonTemplate template = new ApiJsonTemplate();

        String ignoreAccount = null;
        if(!Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(loginAdmin))
        {
            // 登陆名内部账号，则直接隐藏
            ignoreAccount = Admin.DEFAULT_ADMIN_NY4TIME;
        }

        RowPager<Admin> rowPager = mAdminService.queryScrollPage(pageVo, username, ignoreAccount);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_sys_admin_add")
    @RequestMapping("toAddOrEditAdminPage")
    public String toAddOrEditAdminPage(Model model)
    {
        String account = WebRequest.getString("account");
        Admin admin = mAdminService.findAdminInfoByID(false, account);
        if(admin != null)
        {
            model.addAttribute("newAdmin", admin);
        }

        String loginAccount = AdminAccountHelper.getAdmin().getAccount();
        List<Role> roleList = mRoleService.queryAll();
        for(Role role : roleList)
        {
            if(Role.DEFAULT_SUPER_ADMIN_ROLE_NAME.equalsIgnoreCase(role.getName()))
            {
                if(!Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(loginAccount))
                {
                    roleList.remove(role);
                    break;
                }
            }
        }
        model.addAttribute("roleList", roleList);
        return "admin/core/sys/admin_edit";
    }

    @RequiresPermissions("root_sys_admin_add")
    @RequestMapping("addAdmin")
    @ResponseBody
    public String addAdmin()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        String account = WebRequest.getString("account");
        String password = WebRequest.getString("password");
        String roleName = WebRequest.getString("roleName");
        String remark = WebRequest.getString("remark");
        String remoteip = WebRequest.getRemoteIP();

        boolean addSuccess = false;
        try {
            if (StringUtils.isEmpty(account)) {
                apiJsonTemplate.setJsonResult(AdminErrorResult.NONE_ACCOUNT);
                return apiJsonTemplate.toJSONString();
            }

            if (StringUtils.isEmpty(password)) {
                apiJsonTemplate.setJsonResult(AdminErrorResult.NONE_ACCOUNT);
                return apiJsonTemplate.toJSONString();
            }

            if (StringUtils.isEmpty(roleName)) {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(!WhiteIPManager.getInstance().verify(remoteip))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
                return apiJsonTemplate.toJSONString();
            }

            Role role = mRoleService.findByName(roleName);
            if(role == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(role.getName().equalsIgnoreCase(Role.DEFAULT_SUPER_ADMIN_ROLE_NAME))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            Admin admin = mAdminService.findAdminInfoByID(false, account);
            if (null != admin) {
                apiJsonTemplate.setJsonResult(AdminErrorResult.ACCOUNT_EXISTS);
                return apiJsonTemplate.toJSONString();
            }

            //至少8位，不能以admin root manage 命名相关账号
            if (account.length() < 6) {
                apiJsonTemplate.setJsonResult(AdminErrorResult.ERR_ACCOUNTLENGTH);
                return apiJsonTemplate.toJSONString();
            }
            if (account.toLowerCase().contains("admin") || account.toLowerCase().contains("root") || account.toLowerCase().contains("manage")) {
                apiJsonTemplate.setJsonResult(AdminErrorResult.ERR_ACCOUNCONTAINERRORINFO);
                return apiJsonTemplate.toJSONString();
            }

            mAdminService.addAdmin(account, password, null, null, role.getId(), remark);
            addSuccess = true;
        } finally {
            EventLogManager.getInstance().addAdminLog(WebEventLogType.ADMIN_ADD, "account = " + account +  " add result = " + addSuccess);
        }
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_sys_admin_edit")
    @RequestMapping("editAdmin")
    @ResponseBody
    public String editAdmin()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        String account = WebRequest.getString("account");
        String password = WebRequest.getString("password");
        String roleName = WebRequest.getString("roleName");
        String remark = WebRequest.getString("remark");

        String remoteip = WebRequest.getRemoteIP();

        boolean addSuccess = false;

        try {
            if(!WhiteIPManager.getInstance().verify(remoteip))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
                return apiJsonTemplate.toJSONString();
            }

            if (StringUtils.isEmpty(account)) {
                apiJsonTemplate.setJsonResult(AdminErrorResult.NONE_ACCOUNT);
                return apiJsonTemplate.toJSONString();
            }

            Admin admin = mAdminService.findAdminInfoByID(false, account);
            if (null == admin) {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if (StringUtils.isEmpty(roleName)) {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            Role role = mRoleService.findByName(roleName);
            if(role == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(!StringUtil.isEmpty(password))
            {
                mAdminService.updatePassword(account, password);
            }

            if(!StringUtil.isEmpty(remark) && !remark.equalsIgnoreCase(admin.getRemark()))
            {
                mAdminService.updateRemark(account, remark);
            }

            if(role.getId() != admin.getRoleid())
            {
                mAdminService.updateRole(account, role.getId());
            }

            addSuccess = true;

        } finally {
            EventLogManager.getInstance().addAdminLog(WebEventLogType.ADMIN_EDIT, "account =" + account + ", edit result = " + addSuccess);
        }

        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_sys_admin_edit")
    @RequestMapping("toAdminGoogleKey")
    public String toGoogleKey(Model model) {
        String loginAdmin = CoreAdminHelper.getAdminName();

        String account = WebRequest.getString("account");
        String remoteip = WebRequest.getRemoteIP();

        // inple 管理员只能inple才能操作
        if(!(Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(loginAdmin) || MyEnvironment.isDev()))
        {
            return "toError";
        }

        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "toError";
        }

        Admin admin = mAdminService.findAdminInfoByID(false, account);
        if (StringUtils.isEmpty(admin.getGooglekey())) {

            String googleKey = GoogleAuthenticator.generateSecretKey();
            if(Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(admin.getAccount()))
            {
                if(MyEnvironment.isProd())
                {
                    googleKey = Admin.GOOGLE_KEY_INPLE_PROD;
                }
                else
                {
                    googleKey = Admin.GOOGLE_KEY_INPLE_TEST;
                }
            }
            admin.setGooglekey(googleKey);
            mAdminService.updateGoogleKey(account, googleKey);
        }
        model.addAttribute("admin", admin);
        return "admin/core/sys/admin_google_key";
    }

    @RequiresPermissions("root_sys_admin_edit")
    @RequestMapping(value = "getGoogleKeyEWM")
    @ResponseBody
    public void getGoogleKeyEWM(HttpServletResponse response) {
        MyConfiguration conf = MyConfiguration.getInstance();
        String projectName = conf.getString("project.name");

        String qcodeUrl = "otpauth://totp/%s?secret=%s";
        String account = WebRequest.getString("account");
        String remoteip = WebRequest.getRemoteIP();

        try {
            if(!WhiteIPManager.getInstance().verify(remoteip))
            {
                return;
            }

            // inple 管理员只能inple才能操作
            String loginAdmin = CoreAdminHelper.getAdminName();
            if(!(Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(loginAdmin) || MyEnvironment.isDev()))
            {
                // 超级管理员可以查看其它账户
                return;
            }

            if(Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(account))
            {
                // 超级管理员禁止查看
                return;
            }

            Admin admin = mAdminService.findAdminInfoByID(false, account);
            String url = String.format(qcodeUrl, admin.getAccount() + "@" + projectName + "-" + MyEnvironment.getEnv(), admin.getGooglekey());
            if (url != null && !"".equals(url)) {
                ServletOutputStream stream = null;
                try {
                    int width = 200;
                    int height = 200;
                    stream = response.getOutputStream();
                    QRCodeWriter writer = new QRCodeWriter();
                    BitMatrix m = writer.encode(url, BarcodeFormat.QR_CODE, height, width);
                    MatrixToImageWriter.writeToStream(m, "png", stream);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (stream != null) {
                        try {
                            stream.flush();
                            stream.close();
                        } catch (IOException e) {
                        }

                    }
                }
            }
        } finally {
            EventLogManager.getInstance().addAdminLog(WebEventLogType.ADMIN_GETGOOGLEKEYEWM, "getGoogleKeyEWM = " + account);
        }
    }

    @RequiresPermissions("root_sys_admin_delete")
    @RequestMapping(value = "deleteAdmin", method = RequestMethod.POST)
    @ResponseBody
    public String deleteAdmin() {
        String account = WebRequest.getString("account");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        try {
            if(StringUtils.isEmpty(account))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            // 不能删除超级管理员
            Admin currentAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
//        if(Admin.DEFAULT_ADMIN_GOPLE.equalsIgnoreCase(account))
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
//            return apiJsonTemplate.toJSONString();
//        }
            // 不能删除内部管理员
            if(Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(account))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return apiJsonTemplate.toJSONString();
            }

            Admin admin = mAdminService.findAdminInfoByID(false, account);

            // 不能删除自己
            if(admin.getAccount().equalsIgnoreCase(currentAdmin.getAccount()))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return apiJsonTemplate.toJSONString();
            }

            mAdminService.deleteAdmin(account);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            EventLogManager.getInstance().addAdminLog(WebEventLogType.ADMIN_DEL, "delete account = " + account);
        }
        //删除之后强制退出
        return apiJsonTemplate.toJSONString();
    }

}
