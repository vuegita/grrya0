package com.inso.modules.game.brtp.model;

/**
 * 牌型
 *
 */
public enum TPCardType {

    BAO_ZHI(1), // 豹子
    SHUN_JIN(2), // 顺金：花色相同的相连三张牌。
    JIN_HUA(3), // 金花：三张花色相同的牌。
    SHUN_ZHI(4), // 顺子：三张花色不全相同的相连三张牌。
    DUI_ZHI(5), // 三张牌中有两张点数同样大小的牌。
    SINGLE(6), // 单张：除以上牌型的牌。
    ;

    private int index;

    /**
     * 牌型大小，数字越小，牌就越大
     * @param index 定义数字不能重复
     */
    private TPCardType(int index)
    {
        this.index = index;
    }

}
