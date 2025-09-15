package   com.inso.modules.passport;

import com.inso.framework.bean.ErrorResult;

/**
 * 
 * @author Administrator
 * dfad
 */
public enum UserErrorResult implements ErrorResult{
	ERR_ACCESSTOKEN_INVALID(
			20001,
			"invalid access token",
			"Token de acceso invalido",
			"अमान्य प्रवेश टोकन"
			),
	ERR_LOGINTOKEN_INVALID(
			20002,
			"invalid login token",
			"Token de inicio de sesion no valido",
			"अमान्य लॉगिन टोकन"
			),
	ERR_PHONE(
			20003,
			"Phone error",
			"Error telefono",
			"फ़ोन त्रुटि"
			),
	ERR_PHON_EXIST (
			20004,
			"Phone has exists!",
			"El telefono existe!",
			"फोन मौजूद है !"
			),
	ERR_EMAIL(
			20005,
			"Email error!",
			"Eror correo!",
			"ईमेल त्रुटि !"
			),
	ERR_EMAIL_EXIST(
			20006,
			"Email exists!",
			"El correo existe!",
			"ईमेल मौजूद है !"
			),
	ERR_PWD(
			20007,
			"Invalid Password !",
			"Error contraseña!",
			"पासवर्ड त्रुटि !"
			),
	ERR_REG_FAIR(
			200081,
			"Register Error!",
			"Registrar error!",
			"रजिस्टर त्रुटि !"
			),
	ERR_ACCOUNT_DISABLE(
			200082,
			"You've violated the Term of Use, the account has been freeze. Please contact customer service for assistance.",
			"La cuenta se ha desctivado!",
			"खाता अक्षम हो गया है !"
			),
	ERR_ADD_CARD_SIZE_LIMIT(
			200091,
			"add card is limit 10!",
			"Añadir targeta el limite es 10!",
			"कार्ड जोड़ें सीमा 10 . है !"
			),
	//input error login pwd too many times, and the account is disable!
	ERR_INPUT_LOGIN_PWD_ERR_TIMES(
			200092,
			"Enter the invalid password several times, for protect account security, Temporarily freeze the account. Please contact customer service.!",
			"Error de entrada PWD demasiadas veces y la cuenta esta inhabilitada!",
			"इनपुट त्रुटि लॉगिन pwd बहुत बार, और खाता अक्षम है !"
	),
	ERR_ACCOUNT_OR_PWD(
			20010,
			"account or pwd error!",
			"Error de cuenta o contraseña!",
			"खाता या पीडब्ल्यूडी त्रुटि !"
			),
	ERR_BANK_ACCOUNT(
			20011,
			"bank account error!",
			"Error de cuenta bancaria!",
			"बैंक खाता त्रुटि !"
	),
	ERR_BANK_IFSC(
			20012,
			"err bank ifsc, (standard IFSC format) - length 11, first four IFSC and 5th 0!",
			"Error banco IFSC, (Formato IFSC estandar) - longitud 11, primeros cuartos IFSC y quinto 0!",
			"गलत बैंक आईएफएससी, (मानक आईएफएससी प्रारूप) - लंबाई 11, पहले चार आईएफएससी और 5वां 0!"
			),
	ERR_BANK_NAME(
			20013,
			"beneficiary name error, 5 < name <= 50!",
			"error nombre beneficiario, 5 < nombre <= 50!",
			"लाभार्थी का नाम त्रुटि, 5 <नाम <= 50!"
			),


	ERR_ACCOUNT_NOT_EXIST(
			20021,
			"Account not exists!",
			"La cuenta no existe!",
			"खाता मौजूद नहीं है!"
			),
	//	La cuenta esta congelada
	ERR_ACCOUNT_DISABLED(
			20022,
			"Account is freeze!",
			"Demasiadas contraseñas incorrectas, inténtelo de nuevo más tarde !",
			"अकाउंट फ्रीज हो गया है!"
			),

	ERR_RELATION_LAYER(
			20031,
			"The layer is limit 10 !",
			"La capa es limite 10 !",
			"परत 10 . की सीमा है!"
	),
	ERR_RELATION_ROLE(
			20032,
			"update relation role error !",
			"Error del rol de actualizacion !",
			"अद्यतन संबंध भूमिका त्रुटि !"
	),

	// enough
	ERR_PAY_NOT_ENOUGH_BALANCE(
			20033,
			"not enough balance!",
			"no hay suficiente equilibrio!",
			"पर्याप्त संतुलन नहीं !"
	),

	// enough
	ERR_Withdrawal_NOT_ENOUGH_BALANCE(
			20034,
			"Incorrect withdrawal amount!",
			"Monto de retiro incorrecto!",
			"गलत निकासी राशि !"
	),

	ERR_SYSMNET(
			20035,
			"System is being maintained! please try again later !",
			"El sistema se está manteniendo! Inténtelo de nuevo más tarde !",
			"व्यवस्था की जा रही है! कृपया बाद में पुन: प्रयास करें !"
	),

	ERR_AMOUNT_PARAMS (
			20036,
			"Amount params error",
			"Error de parámetros de cantidad",
			"राशि पैरामीटर त्रुटि"
	),

	ERR_CHANNEL_PARAMS (
			20037,
			"Channel Params error",
			"Error de parámetros de canal",
			"चैनल पैराम्स त्रुट"
	),
	ERR_CHANNEL_MAINTAINED_PARAMS (
			20038,
			"Channel is maintained, pls try again later!",
			"El canal se mantiene, inténtalo de nuevo más tarde!",
			"चैनल का रखरखाव किया जाता है, कृपया बाद में पुन: प्रयास करें!"
	),

	ERR_RECHARGE_BELOW_MINIMUM (
			20039,
			"Current recharge amount is less than the min limit amount!",
			"La cantidad de recarga actual es menor que la cantidad límite mínima !",
			"वर्तमान रिचार्ज राशि न्यूनतम सीमा राशि से कम है!"
	),

	ERR_MAX_WITHDRAW_TIME (
			20040,
			"Maximum number of withdrawals over a day",
			"Número máximo de retiros en un día",
			"एक दिन में निकासी की अधिकतम संख्या"
	),

	ERR_MAX_WITHDRAW_AMOUNT_DAY (
			20041,
			"Maximum withdrawal amount exceeding one day",
			"Monto máximo de retiro superior a un día",
			"अधिकतम निकासी राशि एक दिन से अधिक"
	),

	ERR_NOT_ENOUGH_CODE (
			20042,
			"Not enough code amount, also need code amount ",
			"Cantidad de código insuficiente, también se necesita cantidad de código",
			"पर्याप्त कोड राशि नहीं, कोड राशि की भी आवश्यकता है "
	),
	ERR_MAX_WITHDRAW_AMOUNT(
			20043,
			"Exceeded the maximum withdrawal amount",
			"Excedió la cantidad máxima de retiro",
			"अधिकतम निकासी राशि को पार कर गय"
	),
	ERR_MIN_WITHDRAW_AMOUNT(
			20044,
			"Below the minimum withdrawal amount",
			"Por debajo del monto mínimo de retiro",
			"न्यूनतम निकासी राशि से कम"
	),

	ERR_WITHDRAW_TIME(
			20045,
			"Current time is not service for withdraw !",
			"La hora actual no es un servicio para retirar !",
			"वर्तमान समय निकासी के लिए सेवा नहीं है !"
	),

	ERR_REGISTER_TIME(
			20046,
			"The current time is not the registration time !",
			"La hora actual no es la hora de registro !",
			"वर्तमान समय पंजीकरण का समय नहीं है !"
	),

	ERR_INVALID_VIP(
			20047,
			"Invalid vip or expires !",
			"Invalid vip or expires !",
			"वर्तमान समय पंजीकरण का समय नहीं है !"
	),

	ERR_PAYOUT_NOT_WORKING_GOOGLE_WEEKENDS(
			20048,
			"Weekends is a non-working day and bank holiday, the withdrawal service is temporarily closed .",
			"Weekends is a non-working day and bank holiday, the withdrawal service is temporarily closed .",
			"वर्तमान समय पंजीकरण का समय नहीं है !"
	),

	ERR_EXISTS_WITHDRAWL_ORDER(
			20049,
			"Your withdrawal is being processed, please do not submit it multiple times!  !",
			"Your withdrawal is being processed, please do not submit it multiple times!  !",
			"वर्तमान समय पंजीकरण का समय नहीं है !"
	),

	ERR_COIN_NETWORK_TYPE(
			20050,
			"Err network!",
			"Err network!",
			"वर्तमान समय पंजीकरण का समय नहीं है !"
	),

	ERR_UNBIND_GOOGLE(
			20050,
			"Incorrect 2FA verification code.",//Err un bind Google-MFA!
			null,
			null
	),

	;

	private int code;
	private String msg;
	private transient String spError;
	private transient String ydError;

	@Override
	public String getSPError() {
		return spError;
	}

	@Override
	public String getYDError() {
		return ydError;
	}


	private UserErrorResult(int code, String msg,String spError,String ydError )
	{
		this.code = code;
		this.msg = msg;
		this.spError = spError;
		this.ydError = ydError;
	}
	@Override
	public String getError() {
		return msg;
	}
	@Override
	public int getCode() {
		return code;
	}



}
