import kotlin.js.Promise

external val browser: Browser

external class Browser {
    val tabs: Tabs
    val extension: Extension
}

external class Tabs {
    fun executeScript(def: ScriptDefinition) : Promise<List<Any>>
    fun insertCSS(details: CssDetails): Promise<Unit>
    fun removeCSS(details: CssDetails): Promise<Unit>
    fun sendMessage(id: Int, any: Any): Any
    fun query(info: QueryInfo) : Promise<Array<Tab>>
}

external class Extension {
    fun getURL(s: String): String
}

class Tab(val id: Int)

class ScriptDefinition(val file: String)
class CssDetails(val code: String)
class QueryInfo(active: Boolean, currentWindow: Boolean)

inline fun jsObject(init: dynamic.() -> Unit): dynamic {
    val o = js("{}")
    init(o)
    return o
}