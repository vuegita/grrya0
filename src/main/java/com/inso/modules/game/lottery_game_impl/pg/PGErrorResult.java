package com.inso.modules.game.lottery_game_impl.pg;

import com.inso.framework.bean.ErrorResult;

public enum PGErrorResult implements ErrorResult {

    ERR_INVALID_Request(
            1034,
            "Invalid request!"
            ),

    ERR_Server_error(
            1200,
            "Server error!"
    ),

    ERR_ACTION_FAILED(
            1035,
            "Action Failed!"
    ),

    ERR_INVALID_Player_session(
            1300,
            "Invalid player session token!"
    ),

    ERR_PlayerNotFoundException(
            3004,
            "PlayerNotFoundException!"
    ),

    ERR_WalletNotFoundException(
            3005,
            "WalletNotFoundException!"
    ),

    ERR_BonusNotFoundException(
            3008,
            "BonusNotFoundException!"
    ),

    ERR_FreeGameNotFoundException(
            3009,
            "FreeGameNotFoundException!"
    ),

    ERR_NotEnoughFreeGameException(
            3019,
            "NotEnoughFreeGameException!"
    ),

    ERR_BetNotFoundException(
            3021,
            "BetNotFoundException!"
    ),

    ERR_SnapshotNotFoundException(
            3055,
            "SnapshotNotFoundException!"
    ),

    ERR_BetLimitExceededException(
            3059,
            "BetLimitExceededException!"
    ),

    ERR_TransactionRolledBackException(
            3062,
            "TransactionRolledBackException!"
    ),

    ERR_BetFailedException(
            3073,
            "BetFailedException!"
    ),

    ERR_NotEnoughBalanceException(
            3200,
            "NotEnoughBalanceException!"
    ),

    ERR_NotEnoughBonusBalanceException(
            3201,
            "NotEnoughBonusBalanceException!"
    ),

    ERR_NotEnoughCashBalanceException(
            3202,
            "NotEnoughCashBalanceException!"
    ),

    ERR_MaximumBetLimitReachErrorCode(
            3294,
            "MaximumBetLimitReachErrorCode!"
    ),

    ERR_BalanceDecreasesLimitReachErrorCode(
            3295,
            "BalanceDecreasesLimitReachErrorCode!"
    ),



    ;

    private int code;
    private String error;

    PGErrorResult(int code, String error)
    {
        this.code = code;
        this.error = error;
    }

    @Override
    public String getSPError() {
        return error;
    }

    @Override
    public String getYDError() {
        return null;
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
