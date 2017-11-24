import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    if (window["hasRun"] == true) {
        return
    }
    window.asDynamic()["hasRun"] = true

    browser.runtime.onMessage.addListener { message ->
        if (message.command === "beastify") {
            insertBeast(message.beastURL as String)
        } else if (message.command === "reset") {
            removeExistingBeasts()
        }
    }
}

fun insertBeast(beastURL: String) {
    removeExistingBeasts()

    val beastImage = document.createElement("img") as HTMLElement
    beastImage.run {
        setAttribute("src", beastURL)
        style.height = "100vh"
        className = "beastify-image"
    }

    document.body?.appendChild(beastImage)
}

fun removeExistingBeasts() {
    val existingBeasts = document.querySelectorAll(".beastify-image")

    for (beast in existingBeasts.asList()) {
        beast.parentNode?.removeChild(beast)
    }
}