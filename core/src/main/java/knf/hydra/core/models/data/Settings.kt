package knf.hydra.core.models.data

import android.os.Parcelable
import knf.hydra.core.models.data.Setting.*
import kotlinx.parcelize.Parcelize

/**
 * Represents the settings screen for the module
 *
 * @property sections The list of sections
 */
data class SettingPage(val sections: List<Section>)

/**
 * Represents a section in the settings screen
 *
 * @property name Section name
 * @property settings The list of settings for this section
 */
data class Section(val name: String, val settings: List<Setting<*>>)

/**
 * Base setting
 *
 * @see TextInput
 * @see LoginCookies
 * @see Switch
 * @see SingleChoice
 * @see MultiChoice
 */
sealed class Setting<T>(val title:String, val description: String?, val key: String, val default: T){

    /**
     * Represents a setting to input text
     *
     * @property inputValidation Optional callback to check user input before saving
     *
     * @param title Setting title
     * @param description Setting description
     * @param key Setting key
     * @param default Setting default value
     */
    class TextInput(title: String, description: String?, key: String, default: String, val inputValidation: (String) -> Boolean = { true }): Setting<String>(title, description, key, default)

    /**
     * Represents a setting that opens a login screen with the provided url
     *
     * @property loginData Login data to be used
     * @property cookieValidation Optional callback to check if the captured cookies are valid
     * @property cookieTransform Optional callback to transform the captured cookies before saving
     *
     * @param title Setting title
     * @param description Setting description
     * @param key Setting key
     * @param default Setting default value
     */
    class LoginCookies(title: String, description: String?, key: String, default: String, val loginData: LoginData, val cookieValidation: (String) -> Boolean = { true }, val cookieTransform: (String) -> String = { it }): Setting<String>(title, description, key, default)

    /**
     * Represents a simple switch setting
     *
     * @param title Setting title
     * @param description Setting description
     * @param key Setting key
     * @param default Setting default value
     */
    class Switch(title: String, description: String?, key: String, default: Boolean): Setting<Boolean>(title, description, key, default)

    /**
     * Represents a simple single choice setting
     *
     * @property options The options to display
     *
     * @param title Setting title
     * @param description Setting description
     * @param key Setting key
     * @param default Setting default value
     */
    class SingleChoice(title: String, description: String?, key: String, default: String, val options: List<ChoiceItem>): Setting<String>(title, description, key, default)

    /**
     * Represents a simple multi choice setting
     *
     * @property options The options to display
     *
     * @param title Setting title
     * @param description Setting description
     * @param key Setting key
     * @param default Setting default value
     */
    class MultiChoice(title: String, description: String?, key: String, default: String, val options: List<ChoiceItem>): Setting<String>(title, description, key, default)
}

/**
 * Represents a choice item in the single and multi choice setting
 *
 * @property name Display name
 * @property value Option value
 */
data class ChoiceItem(val name: String, val value: String)

/**
 * Represents the login data used in [LoginCookies]
 *
 * @property baseUrl Url used to search for cookies "CookieManager.getCookie([baseUrl])"
 * @property loginUrl Url for the login webpage
 * @property desiredCookie Cookie key to search, when the key is found and the [LoginCookies.cookieValidation] is true the app will consider the login as successful
 */
@Parcelize
class LoginData(var baseUrl: String = "", var loginUrl: String = "", var desiredCookie: String = ""): Parcelable{
    fun isValid() = baseUrl.isNotBlank() && loginUrl.isNotBlank() && desiredCookie.isNotBlank()
}




