package com.inso.modules.admin.config.shiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.Permission;
import com.inso.modules.admin.core.service.AdminService;
import com.inso.modules.admin.core.service.MenuService;
import com.inso.modules.admin.core.service.PermissionService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;

/**
 * @author XXX
 * @create 2018-11-02 15:22
 */
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private AdminService adminService;

    @Autowired
    private MenuService menuService;
    
    @Autowired
    private PermissionService mPermissionService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    private CacheManager mCache = CacheManager.getInstance();

    private static Map<String, SimpleAuthorizationInfo> mAuthMaps = Maps.newHashMap();

    private static SimpleAuthorizationInfo mDefaultMerchangeAuthorizationInfo = new SimpleAuthorizationInfo();

    @Override
    public String getName() {
        return "shiroRealm";
    }
    //权限认证
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Object principal = principalCollection.getPrimaryPrincipal();
        if(principal instanceof Admin)
        {
            Admin admin = (Admin) principal;

            String key = getKey(MyUserPwdToken.Type.ADMIN, admin.getAccount());

            SimpleAuthorizationInfo simpleAuthorizationInfo = mAuthMaps.get(key);
            if(simpleAuthorizationInfo == null)
            {
                List<String> permissionStringList = new ArrayList<>();
                List<Permission> permissionList= mPermissionService.queryAllByRoleid(admin.getRoleid());
                
//                List<Menu> listByRoleId = menuService.findListByRoleid(admin.getRoleid());
                permissionStringList.add("/alibaba888/Liv2sky3soLa93vEr62/toWelcome");
                permissionStringList.add("/alibaba888/Liv2sky3soLa93vEr62/toIndex");
                if (!CollectionUtils.isEmpty(permissionList)){
                    for (Permission model : permissionList) {
                    	permissionStringList.add(model.getKey());
                    }

                }
                simpleAuthorizationInfo = new SimpleAuthorizationInfo();
                simpleAuthorizationInfo.addStringPermissions(permissionStringList);

                mAuthMaps.put(key, simpleAuthorizationInfo);
            }
            return simpleAuthorizationInfo;
        }
        else if(principal instanceof UserInfo)
        {
            // 商户后台默认列出所有菜单
            return mDefaultMerchangeAuthorizationInfo;
        }
        return null;
    }

    //用户认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
        AuthenticationToken authenticationToken) throws AuthenticationException {
        MyUserPwdToken token = (MyUserPwdToken) authenticationToken;

        String account = token.getUsername();
        if(MyUserPwdToken.Type.ADMIN == token.getmType())
        {
            String password=new String(token.getPassword());
            Admin admin = adminService.findAdminInfoByID(false, account);
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(admin,password, this.getName());

            String key = getKey(MyUserPwdToken.Type.ADMIN, admin.getAccount());
            mAuthMaps.remove(key);
            return simpleAuthenticationInfo;
        }
        else if(MyUserPwdToken.Type.AGENT == token.getmType())
        {
            UserInfo userInfo = mUserService.findByUsername(false, account);

            UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());


            if(userType == UserInfo.UserType.AGENT)
            {
                userInfo.setAgentid(userInfo.getId());
            }
            else
            {
                // 如果有代理，设置代理id
                UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
                userInfo.setAgentid(userAttr.getAgentid());
            }

            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(userInfo, StringUtils.getEmpty(), this.getName());
            mAuthMaps.remove(getKey(token.getmType(), userInfo.getName()));
            return simpleAuthenticationInfo;
        }
        return null;
    }

    private static String getKey(MyUserPwdToken.Type type, String accountName)
    {
        return type.toString() + accountName;
    }


    public static void stickAllIgnoreSuperAdmin()
    {
        String superAdminKey = getKey(MyUserPwdToken.Type.ADMIN, Admin.DEFAULT_ADMIN_GOPLE);
        SimpleAuthorizationInfo value = mAuthMaps.get(superAdminKey);

        String ny4timeAdminKey = getKey(MyUserPwdToken.Type.ADMIN, Admin.DEFAULT_ADMIN_NY4TIME);
        SimpleAuthorizationInfo ny4timeValue = mAuthMaps.get(ny4timeAdminKey);

        mAuthMaps.clear();
        if(value != null)
        {
            mAuthMaps.put(superAdminKey, value);
        }
        if(ny4timeValue != null)
        {
            mAuthMaps.put(ny4timeAdminKey, ny4timeValue);
        }
    }

    public static void stickAgent(String agentname)
    {
        String key = getKey(MyUserPwdToken.Type.AGENT, agentname);
        mAuthMaps.remove(key);
    }
}
