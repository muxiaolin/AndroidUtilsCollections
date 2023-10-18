package core.colin.basic.utils.regex;

import com.blankj.utilcode.util.LogUtils;

/**
 * 身份证的工具类
 */
public class IdCardUtil {

    private String idCardNum = null;
    private int error = 0;


    /**
     * 构造方法。
     *
     * @param idCardNum
     */
    public IdCardUtil(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getIdCardNum() {
        return idCardNum == null ? null : idCardNum.toUpperCase();
    }

    public static boolean isIDcardCorrect(String idCardNum) {
        return new IdCardUtil(idCardNum).isCorrect() == 0;
    }

    /**
     * 校验身份证是否正确
     *
     * @return 0：正确
     */
    public boolean isValidate() {
        return isCorrect() == 0;
    }


    /**
     * 校验身份证是否正确
     *
     * @return 0：正确
     */
    public int isCorrect() {
        error = IdCardUtils.isValidatedAllIdcard(idCardNum);
        LogUtils.d("身份证检验[" + idCardNum + "]:" + getErrMsg());
        return error;
    }


    public String getErrMsg() {
        return IdCardUtils.getErrMsg(error);
    }


    /**
     * 是否为空。
     *
     * @return true: null  false: not null;
     */
    public boolean isEmpty() {
        if (this.idCardNum == null)
            return true;
        else
            return this.idCardNum.trim().length() <= 0;
    }


    /**
     * 得到身份证的省份代码。
     *
     * @return 省份代码。
     */
    public String getProvince() {
        return error == 0 ? this.idCardNum.substring(0, 2) : "";
    }

    /**
     * 得到身份证的城市代码。
     *
     * @return 城市代码。
     */
    public String getCity() {
        return error == 0 ? this.idCardNum.substring(2, 4) : "";
    }

    /**
     * 得到身份证的区县代码。
     *
     * @return 区县代码。
     */
    public String getCountry() {
        return error == 0 ? this.idCardNum.substring(4, 6) : "";
    }

    /**
     * 身份证长度。
     *
     * @return
     */
    public int getLength() {
        return this.isEmpty() ? 0 : this.idCardNum.length();
    }

    /**
     * 得到身份证的出生年份。
     *
     * @return 出生年份。
     */
    public String getYear() {
        if (error != 0)
            return "";

        if (this.getLength() == 15) {
            return "19" + this.idCardNum.substring(6, 8);
        } else {
            return this.idCardNum.substring(6, 10);
        }
    }

    /**
     * 得到身份证的出生月份。
     *
     * @return 出生月份。
     */
    public String getMonth() {
        if (error != 0)
            return "";

        if (this.getLength() == 15) {
            return this.idCardNum.substring(8, 10);
        } else {
            return this.idCardNum.substring(10, 12);
        }
    }

    /**
     * 得到身份证的出生日子。
     *
     * @return 出生日期。
     */
    public String getDay() {
        if (error != 0)
            return "";

        if (this.getLength() == 15) {
            return this.idCardNum.substring(10, 12);
        } else {
            return this.idCardNum.substring(12, 14);
        }
    }

    /**
     * 得到身份证的出生日期。
     *
     * @return 出生日期。
     */
    public String getBirthday() {
        if (error != 0)
            return "";

        if (this.getLength() == 15) {
            return "19" + this.idCardNum.substring(6, 12);
        } else {
            return this.idCardNum.substring(6, 14);
        }
    }

    /**
     * 得到身份证的出生年月。
     *
     * @return 出生年月。
     */
    public String getBirthMonth() {
        return getBirthday().substring(0, 6);
    }

    /**
     * 得到身份证的顺序号。
     *
     * @return 顺序号。
     */
    public String getOrder() {
        if (error != 0)
            return "";

        if (this.getLength() == 15) {
            return this.idCardNum.substring(12, 15);
        } else {
            return this.idCardNum.substring(14, 17);
        }
    }

    /**
     * 得到性别。
     */
    public boolean isFemale() {
        if (error != 0)
            return false;

        if ("男".equals(getSex())) {
            return false;
        } else return "女".equals(getSex());
    }

    /**
     * 得到性别。
     *
     * @return 性别：1－男  2－女
     */
    public String getSex() {
        if (error != 0)
            return "";

        int p = Integer.parseInt(getOrder());
        if (p % 2 == 1) {
            return "男";
        } else {
            return "女";
        }
    }

    /**
     * 得到性别值。
     *
     * @return 性别：1－男  2－女
     */
    public String getSexValue() {
        if (error != 0)
            return "";

        int p = Integer.parseInt(getOrder());
        if (p % 2 == 1) {
            return "1";
        } else {
            return "2";
        }
    }


}