import org.w3c.dom.Element
import kotlin.browser.document

const val hidePage = """
    body > :not(.beastify-image) {
        display: none;
    }
"""

const val scriptPath = "/content_script/build/classes/kotlin/main/min"

fun listenForClicks() {
    document.addEventListener("click", { e ->

        val target = e.target as Element

        fun beastNameToURL(beastName: String) = when (beastName) {
            "Frog" -> browser.extension.getURL("beasts/frog.jpg")
            "Snake" -> browser.extension.getURL("beasts/snake.jpg")
            "Turtle" -> browser.extension.getURL("beasts/turtle.jpg")
            else -> null
        }

        fun beastify(tabs: Array<Tab>) {
            browser.tabs.insertCSS(CssDetails(hidePage))
                    .then({
                        val url = beastNameToURL(target.textContent as String)
                        browser.tabs.sendMessage(tabs[0].id, jsObject {
                            command = "beastify"
                            beastURL = url
                        })
                    })
        }

        fun reset(tabs: Array<Tab>) {
            browser.tabs.removeCSS(CssDetails(hidePage))
                    .then({
                        browser.tabs.sendMessage(tabs[0].id, jsObject {
                            command = "reset"
                        })
                    })
        }

        fun reportError(error: Any) {
            console.error("Could not beastify: $error")
        }


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


fun main(args: Array<String>) {
    browser.tabs.executeScript(ScriptDefinition("$scriptPath/kotlin.js"))
            .then({
                browser.tabs.executeScript(ScriptDefinition("$scriptPath/content_script.js"))
                        .then({ listenForClicks() })
            })
            .catch(::reportExecuteScriptError)
}

