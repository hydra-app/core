package knf.hydra.core.models.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class SettingPage(val sections: List<Section>)

data class Section(val name: String, val settings: List<Setting<*>>)

sealed class Setting<T>(val title:String, val description: String?, val key: String, val default: T){
    class TextInput(title: String, description: String?, key: String, default: String, val inputValidation: (String) -> Boolean = { true }): Setting<String>(title, description, key, default)
    class LoginCookies(title: String, description: String?, key: String, default: String, val loginData: LoginData, val cookieValidation: (String) -> Boolean = { true }, val cookieTransform: (String) -> String = { it }): Setting<String>(title, description, key, default)
    class Switch(title: String, description: String?, key: String, default: Boolean): Setting<Boolean>(title, description, key, default)
    class SingleChoice(title: String, description: String?, key: String, default: String, val options: List<ChoiceItem>): Setting<String>(title, description, key, default)
    class MultiChoice(title: String, description: String?, key: String, default: String, val options: List<ChoiceItem>): Setting<String>(title, description, key, default)
}
data class ChoiceItem(val name: String, val value: String)

@Parcelize
class LoginData(var baseUrl: String = "", var loginUrl: String = "", var desiredCookie: String = ""): Parcelable{
    fun isValid() = baseUrl.isNotBlank() && loginUrl.isNotBlank() && desiredCookie.isNotBlank()
}




