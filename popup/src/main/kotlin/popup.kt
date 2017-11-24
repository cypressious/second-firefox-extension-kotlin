import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.js.Promise

const val CSS_HIDE_PAGE = """
    body > :not(.beastify-image) {
        display: none;
    }
"""

const val SCRIPT_PATH = "/content_script/build/classes/kotlin/main/min"

fun main(args: Array<String>) {
    Promise.all(arrayOf(
            browser.tabs.executeScript(
                    Script("$SCRIPT_PATH/kotlin.js")),
            browser.tabs.executeScript(
                    Script("$SCRIPT_PATH/content_script.js"))
    ))
            .then({ listenForClicks() })
            .catch(::reportExecuteScriptError)
}

fun listenForClicks() {
    document.addEventListener("click", { e ->

        val target = e.target as? Element ?: return@addEventListener

        fun beastNameToURL(beastName: String) = when (beastName) {
            "Frog" -> browser.extension.getURL("beasts/frog.jpg")
            "Snake" -> browser.extension.getURL("beasts/snake.jpg")
            "Turtle" -> browser.extension.getURL("beasts/turtle.jpg")
            else -> null
        }

        fun beastify(tabs: Array<Tab>) {
            browser.tabs.insertCSS(CssDetails(CSS_HIDE_PAGE))
                    .then({
                        val url = beastNameToURL(target.textContent as String)
                        browser.tabs.sendMessage(tabs[0].id, jsObject {
                            command = "beastify"
                            beastURL = url
                        } as Any)
                    })
        }

        fun reset(tabs: Array<Tab>) {
            browser.tabs.removeCSS(CssDetails(CSS_HIDE_PAGE))
                    .then({
                        browser.tabs.sendMessage(tabs[0].id, jsObject {
                            command = "reset"
                        } as Any)
                    })
        }

        fun reportError(error: Any) = console.error("Could not beastify: $error")

        val callback = when {
            target.classList.contains("beast") -> ::beastify
            target.classList.contains("reset") -> ::reset
            else -> return@addEventListener
        }

        browser.tabs.query(QueryInfo(active = true, currentWindow = true))
                .then(callback)
                .catch(::reportError)
    })
}

fun reportExecuteScriptError(error: Throwable) {
    document.querySelector("#popup-content")?.classList?.add("hidden")
    document.querySelector("#error-content")?.classList?.remove("hidden")
    console.error("Failed to execute beastify content script: ${error.message}")
}