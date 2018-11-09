import extensionTypes.InjectDetails
import org.w3c.dom.Element
import tabs.QueryInfo
import webextensions.browser
import kotlin.browser.document
import kotlin.js.Promise

const val SCRIPT_PATH = "/content_script/build/kotlin-js-min/main"

fun main(args: Array<String>) {
    Promise.all(arrayOf(
            browser.tabs.executeScript(
                    details = InjectDetails(file = "$SCRIPT_PATH/kotlin.js")),
            browser.tabs.executeScript(
                    details = InjectDetails(file = "$SCRIPT_PATH/declarations.js")),
            browser.tabs.executeScript(
                    details = InjectDetails(file = "$SCRIPT_PATH/content_script.js"))
    ))
            .then({ listenForClicks() })
            .catch(::reportExecuteScriptError)
}

fun getUrl(name: String?): String {
    val relative = "beasts/${name?.toLowerCase()}.jpg"
    return browser.extension.getURL(relative)
}

fun listenForClicks() {
    document.addEventListener("click", { e ->
        val target = e.target as? Element ?: return@addEventListener

        browser.tabs.query(QueryInfo(active = true, currentWindow = true))
                .then({ tabs -> handleClick(target, tabs[0].id!!) })
                .catch(::reportError)
    })
}

const val CSS_HIDE_PAGE = """
    body > :not(.beastify-image) {
        display: none;
    }
"""

fun handleClick(target: Element, id: Int) {
    if (target.classList.contains("beast")) {
        val url = getUrl(target.textContent)

        browser.tabs.insertCSS(id, InjectDetails(code = CSS_HIDE_PAGE))
        browser.tabs.sendMessage(id, jsObject {
            command = "beastify"
            beastURL = url
        })
    } else {
        browser.tabs.removeCSS(id, InjectDetails(code = CSS_HIDE_PAGE))
        browser.tabs.sendMessage(id, jsObject {
            command = "reset"
        })
    }
}

inline fun jsObject(init: dynamic.() -> Unit): dynamic {
    val o = js("{}")
    init(o)
    return o
}

fun reportError(error: Any) = console.error("Could not beastify: $error")

fun reportExecuteScriptError(error: Throwable) {
    document.querySelector("#popup-content")?.classList?.add("hidden")
    document.querySelector("#error-content")?.classList?.remove("hidden")
    console.error("Failed to execute beastify content script: ${error.message}")
}