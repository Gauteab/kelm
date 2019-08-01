import org.w3c.dom.Element
import kotlin.browser.document

fun main() = sandbox.render()

fun view(model: Model) =
        Sandbox.Html.div {
            button { +model.count.toString() }
            button {
                +"+"
                onClick = Msg.Inc
            }
            button {
                +"-"
                onClick = Msg.Dec
            }
        }

fun update(msg: Msg, model: Model) =
        when (msg) {
            Msg.Inc -> model.copy(count = model.count + 1)
            Msg.Dec -> model.copy(count = model.count - 1)
        }

data class Model(val count: Int)

sealed class Msg {
    object Inc : Msg()
    object Dec : Msg()
}

val sandbox = Sandbox (
        model = Model(0),
        view = ::view,
        update = ::update
)

class Sandbox (
        var model: Model,
        val view: (Model) -> Html,
        val update: (Msg, Model) -> Model
) {

    fun render() {
        val root = document.getElementById("root")!!
        val html = view(model)
        root.innerHTML = ""
        root.appendChild(html.toElement())

    }

    fun send(m: Msg) {
        model = update(m, model)
        render()
    }

    class Html (val name: String) {

        val children: MutableList<Html> = mutableListOf()
        var innerText: String? = null
        var onClick: Msg? = null

        operator fun String.unaryPlus() {
            innerText = this
        }

        fun toElement(): Element {
            val e = document.createElement(name)
            e.textContent = innerText
            e.addEventListener("click", { if (onClick != null)  sandbox.send(onClick!!) else Unit })
            for (c in children) e.appendChild(c.toElement())
            return e
        }

        fun button(block: Html.() -> Unit): Html {
            val btn = Html("button")
            btn.block()
            children.add(btn)
            return btn
        }

        companion object {
            fun div(init: Html.()->Unit): Html {
                val div = Html("button")
                div.init()
                return div
            }
        }
    }
}

