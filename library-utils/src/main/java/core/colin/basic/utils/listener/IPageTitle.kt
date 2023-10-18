package core.colin.basic.utils.listener

/**
 * Author: PL
 * Date: 2022/7/21
 * Desc: 公共页面接口标题接口
 */
interface IPageTitle {

    fun getPageTitle(): String?

    fun getPageValue(): String? {
        return null
    }
}