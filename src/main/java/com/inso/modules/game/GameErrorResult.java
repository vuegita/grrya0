package com.inso.modules.game;

import com.inso.framework.bean.ErrorResult;

public enum GameErrorResult implements ErrorResult {

    ERR_TMP_CLOSED(
            30001,
            "Service temporarily closed!",
            "Servicio cerrado temporalmente!",
            "सेवा अस्थायी रूप से बंद !"
            ),
    ERR_LIMIT_BET_MONEY(
            30002,
            "Abnormal bet amount!",
            "El dinero de la apuesta limite esta entre 10 y 50000!",
            "सीमा शर्त पैसा १० और ५०००० के बीच है!"
            ),
    ERR_CURRENT_ISSUE_FINISH(
            30003,
            "Current issue has finished !",
            "El problema actual ha terminado !",
            "वर्तमान अंक समाप्त हो गया है !"
            ),
    ERR_LIMIT_TOTAL_AMOUNT(
            300041,
            "All transaction amount of issue has limit !",
            "Toda la cantidad de transacion de la emision tiene limite !",
            "इश्यू की सभी लेन-देन राशि की सीमा है !"
            ),
    ERR_LIMIT_USER_AMOUNT(
            300042,
            "All transaction amount of user has limit !",
            "Toda la cantidad de transaciones del usuario tienen limite !",
            "उपयोगकर्ता की सभी लेन-देन राशि की सीमा है !"
            ),
    ERR_BET_ITEM(
            300043,
            "Invest item error !",
            "Error de articulo de apuesta !",
            "बेट आइटम त्रुटि !"
            ),

    ERR_BET_EXIST(
            300044,
            "You have already purchased it and cannot buy it again!",
            "Ya lo ha comprado y no puede volverlo a comprar!",
            "आप इसे पहले ही खरीद चुके हैं और इसे दोबारा नहीं खरीद सकते !"
            ),

    ERR_CHECKIN_EXIST(
            30005,
            "Checked in",
            "Registrado",
            "आगमन की सूचना दिया"
            ),


    ERR_REDP_STAFF_LIMIT_MAX_MONEY_OF_SINGLE(
            31001,
            "Single transaction exceeds the limit !",
            "Una sola transacción supera el límite !",
            "एकल लेनदेन सीमा से अधिक है !"
            ),
    ERR_REDP_STAFF_LIMIT_MAX_MONEY_OF_DAY(
            31002,
            "Today's total amount has reached the upper limit !",
            "El monto total de hoy ha alcanzado el límite superior !",
            "आज की कुल राशि ऊपरी सीमा पर पहुंच गई है !"
            ),
    ERR_REDP_STAFF_LIMIT_MAX_COUNT_OF_DAY(
            31003,
            "The number of times today has reached the limit  !",
            "El número de veces que hoy ha alcanzado el límite !",
            "आज की संख्या सीमा तक पहुंच गई है !"
            ),
    ERR_REDP_STAFF_LIMIT_UN_CONFIG(
            31004,
            "Current Staff un config, pls contact admin or agent!",
            "Personal actual un config, póngase en contacto con el administrador o el agente!",
            "वर्तमान स्टाफ अन कॉन्फिग, pls संपर्क व्यवस्थापक या एजेंट!"
            ),
    ERR_REDP_SALES_LESS_MINIMUM_SALES(
            31005,
            "Sales share is less than minimum sales !",
            "La cuota de ventas es inferior a las ventas mínimas !",
            "बिक्री का हिस्सा न्यूनतम बिक्री से कम है !"
    ),

    ERR_RED_PACKAGE_TREASURE_BOX_EMPTY(
            31006,
            "The treasure box is empty, try faster next time.",
            null,
            null
    ),

    ERR_ROCKET_UN_CASHOUT_STAGE(
            31007,
            "The game is not at this stage! ",
            null,
            null
    ),

    ERR_GAME_OVER(
            31008,
            "The current game is over! ",
            null,
            null
    ),

    ERR_GAME_NOT_STARTED(
            31009,
            "The game not started!",
            null,
            null
    ),


    ;

    private int code;
    private String error;
    private transient String spError;
    private String ydError;
    @Override
    public String getSPError() {
        return spError;
    }

    @Override
    public String getYDError() {
        return ydError;
    }

    GameErrorResult(int code, String error,String spError,String ydError)
    {
        this.code = code;
        this.error = error;
        this.spError=spError;
        this.ydError=ydError;
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public int getCode() {
        return code;
    }
}
