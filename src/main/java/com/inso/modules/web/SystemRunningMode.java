package com.inso.modules.web;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import org.springframework.ui.Model;

public enum  SystemRunningMode {

    // BC
    BC("bc"),

    //
    FUNDS ("funds"),

    //
    CRYPTO ("crypto"), // 数字货币
    ;

    private static boolean isDEV = MyEnvironment.isDev();
    private static SystemRunningMode mSystemRunningMode = null;

    public static boolean isDevOrTest = "1cent".equalsIgnoreCase(MyConfiguration.getInstance().getString("project.name")) || MyEnvironment.isDev();

    private String key;
    private SystemRunningMode(String key)
    {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static SystemRunningMode getType(String key)
    {
        SystemRunningMode[] values = SystemRunningMode.values();
        for(SystemRunningMode type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    public static SystemRunningMode getSystemConfig()
    {
        if(mSystemRunningMode != null)
        {
            return mSystemRunningMode;
        }

        MyConfiguration conf = MyConfiguration.getInstance();
        String mode = conf.getString("system.running.mode");

        mSystemRunningMode = getType(mode);
        return mSystemRunningMode;
    }

    /**
     * 资金盘模式
     * @return
     */
    public static boolean isFundsMode()
    {
        SystemRunningMode mode = getSystemConfig();
        return mode == SystemRunningMode.FUNDS || isDevOrTest;
    }

    /**
     * BC盘模式
     * @return
     */
    public static boolean isBCMode()
    {
        SystemRunningMode mode = getSystemConfig();
        return mode == SystemRunningMode.BC || isDevOrTest;//|| isDEV
    }

    /**
     * BC盘模式
     * @return
     */
    public static boolean isCryptoMode()
    {
        SystemRunningMode mode = getSystemConfig();
        return mode == SystemRunningMode.CRYPTO || isDevOrTest;
    }

    public static void addModel(Model model)
    {
        SystemRunningMode mode = getSystemConfig();
        model.addAttribute("systemRunningMode", mode.getKey());
    }


}
