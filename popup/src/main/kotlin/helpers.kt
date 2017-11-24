import kotlin.js.Promise

external val browser: Browser

external class Browser {
    val tabs: Tabs
    val extension: Extension
}

external class Tabs {
    fun executeScript(def: Script): Promise<List<Any>>
    fun insertCSS(id: Int, details: CssDetails): Promise<Unit>
    fun removeCSS(id: Int, details: CssDetails): Promise<Unit>
    fun sendMessage(id: Int, message: dynamic): Any
    fun query(info: Query): Promise<Array<Tab>>
}

external class Extension {
    fun getURL(s: String): String
}

class Tab(val id: Int)

class Script(val file: String)
class CssDetails(val code: String)
class Query(val active: Boolean, val currentWindow: Boolean)