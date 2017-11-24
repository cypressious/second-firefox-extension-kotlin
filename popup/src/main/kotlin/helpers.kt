import kotlin.js.Promise

external val browser: Browser

external class Browser {
    val tabs: Tabs
    val extension: Extension
}

external class Tabs {
    fun executeScript(def: Script): Promise<List<Any>>
    fun insertCSS(details: CssDetails): Promise<Unit>
    fun removeCSS(details: CssDetails): Promise<Unit>
    fun sendMessage(id: Int, any: Any): Any
    fun query(info: QueryInfo): Promise<Array<Tab>>
}

external class Extension {
    fun getURL(s: String): String
}

class Tab(val id: Int)

class Script(val file: String)
class CssDetails(val code: String)
class QueryInfo(val active: Boolean, val currentWindow: Boolean)

inline fun jsObject(init: dynamic.() -> Unit): dynamic {
    val o = js("{}")
    init(o)
    return o
}