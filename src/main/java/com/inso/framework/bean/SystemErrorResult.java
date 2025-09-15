package com.inso.framework.bean;

/**
 * 数据接口返回模板
 */
public enum SystemErrorResult implements ErrorResult{

	SUCCESS(
			200,
			"success",
			"success",
			"success"
			),

	SUCCESS_NOT_SHOW_ERR(
			199,
			"",
			"",
			""
	),

	ERR_SYSTEM(
			-1,
			"System error!",
			"Error del sistema!",
			"सिस्टम में गड़बड़ी!"
			),
	ERR_PARAMS(
			-2,
			"params error",
			"Error de parametros",
			"पैराम्स त्रुटि"
			),
	ERR_NODATA(
			-3,
			"no data",
			"Sin datos",
			"कोई डेटा उपलब्ध नहीं है"
			),
	ERR_REQUESTS(
			-4,
			"frequent operation in a short time",
			"Operacion frecuente en poco tiempo",
			"कम समय में लगातार संचालन"
			),
	ERR_SYS_BUSY(
			-5,
			"System busy, try again later!",
			"Sistema ocupado intento de nuevo mas tarde!",
			"सिस्टम व्यस्त है, बाद में पुन: प्रयास करें!"
			),
	ERR_SYS_FIRST(
			-6,
			" There are many people at present, please try again!",
			"Hay muchas personas en este momento intentelo de nuevo!",
			"इस समय बहुत से लोग हैं, कृपया पुनः प्रयास करें !"
			),
	ERR_SYS_DATA(
			-7,
			"data error!",
			"error de datos!",
			"डेटा त्रुटि !"
			),
	ERR_SYS_GOOGLE(
			-8,
			"Google code error!",
			"Error codigo Google !",
			"गूगल कोड त्रुटि!"
			),
	ERR_SYS_MAINTAINED(
			-9,
			"System is being maintained! please try again later !",
			"El sistema esta en mantenimiento por favor intentelo mas tarde !",
			"व्यवस्था की जा रही है! कृपया बाद में पुन: प्रयास करें !"
	),
	ERR_SYS_OPT_ADD(
			-10,
			"save error!",
			"Guardar error!",
			"त्रुटि सहेजें !"
			),
	ERR_SYS_OPT_DEL(
			-11,
			"delete error!",
			"borrar error!",
			"त्रुटि हटाएं !"
			),
	ERR_SYS_OPT_UPD(
			-12,
			"update error!",
			"Error de actualizacion!",
			"अद्यतन त्रुटि !"
			),
	ERR_SYS_OPT_ILEGAL(
			-13,
			"Ilegal operation!",
			"Operacion ilegal!",
			"अवैध संचालन !"
			),
	ERR_SYS_OPT_FAILURE(
			-14,
			"Operation fail!",
			"Operacion fallida!",
			"ऑपरेशन विफल !"
			),
	ERR_SYS_OPT_FORBID(
			-15,
			"Operation disabled!",
			"Operacion inhabilitada!",
			"ऑपरेशन अक्षम !"
			),
	ERR_SYS_OPT_FINISHED(
			-16,
			"Operation Finished!",
			"Operacion finalizada!",
			"ऑपरेशन समाप्त !"
			),

	ERR_EXIST(
			-17,
			"Exists error",
			"Existe un error",
			"त्रुटि मौजूद है"
			),
	ERR_EXIST_NOT(
			-18,
			"Not exists!",
			"No existe!",
			"मौजूद नहीं !"
			),
	ERR_INVALID(
			-19,
			"expires",
			"expiro",
			"समय सीमा समाप्त"
			),
	ERR_DISABLE(
			-20,
			"Has disabled",
			"Ha inhabilitado",
			"अक्षम कर दिया है"
			),
	ERR_VERIFY_IMAGE_CODE(
			-21,
			"err verify code",
			"error en la verificacion del codigo",
			"त्रुटि सत्यापन कोड"
			),
	ERR_CARD_UPD(
			-22,
			"The modification interval must be greater than 1 day",
			"El intervalo de modificacion debe swr superior a un dia",
			"संशोधन अंतराल 1 दिन से अधिक होना चाहिए"
			),
	ERR_NEWPWD(
			-23,
			"The new password cannot be the same as the original password",
			"La nueva contraseña no puede ser la misma que la contraseña original",
			"नया पासवर्ड मूल पासवर्ड के समान नहीं हो सकता"
			),
	
	ERR_CUSTOM(
			-100,
			"custom error!",
			"Error personalizado!",
			"कस्टम त्रुटि!"
			),
	THIRD_OR_BANK_PAYOUT_SYSTEM_ERROR(
			-24,
			"提交失败，具体原因请看提现记录, 如有疑问联系上游!",
			"Tres partes o el sistema bancario está en mantenimiento!",
			"तीन पक्ष या बैंक प्रणाली रखरखाव के अधीन है !"
	),

	ERR_NATIVE_OR_TOKEN20(
			-24,
			"出款失败, 可能的原因: 请确保出款钱包的主币和出款代币有足够的余额，余额不足请充值。如果不是以上原因，请在\"提现记录\"中查看具体错误原因。排除错误之后请在\"提现记录\" 中点击 \"重新发起提现\"。!",
			"!",
			""
	),

	ERR_PRODUCT_MAINTAINED(
			-26,
			"Currecnt product is being maintained !",
			"El sistema esta en mantenimiento por favor intentelo mas tarde !",
			"व्यवस्था की जा रही है! कृपया बाद में पुन: प्रयास करें !"
	),

	ERR_USER_NO_EXIST(
			-27,
			"用户不存在!",
			"!",
			""
	),
	ERR_USER_EXIST_AGENT(
			-28,
			"用户已注册在别的代理下，已是别的代理的用户!",
			"!",
			""
	),

	ERR_HAS_FINISHED(
			-29,
			"Has Finished!",
			"!",
			""
	),

	;

	
	private int code;
	private String error;
	private String spError;
	private String ydError;
	public String getError(){return error;}
	public int getCode(){return code;}

	@Override
	public String getSPError() {
		return spError;
	}

	@Override
	public String getYDError() {
		return ydError;
	}

	private SystemErrorResult(int code, String error,String spError,String ydError){
		this.code = code;
		this.error = error;
		this.spError = spError;
		this.ydError = ydError;
	}
	
}
