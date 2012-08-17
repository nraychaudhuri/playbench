
package views.html

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import play.api.i18n._
import play.api.mvc._
import play.api.data._
import views.html._
/**/
object index extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[Form[scala.Tuple3[String, Int, Option[String]]],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(helloForm: Form[(String,Int,Option[String])]):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.48*/("""

"""),format.raw/*4.1*/("""
"""),_display_(Seq[Any](/*5.2*/main(title = "The 'helloworld' application")/*5.46*/ 

    {_display_(Seq[Any](format.raw/*5.48*/("""

    <h1>Configure your 'Hello world':</h1>

    """),_display_(Seq[Any](/*9.6*/form(action = routes.Application.sayHello, args = 'id -> "helloform")/*9.75*/ 

        {_display_(Seq[Any](format.raw/*9.77*/("""

        """),_display_(Seq[Any](/*11.10*/inputText(
            field = helloForm("name"),
            args = '_label -> "What's your name?", 'placeholder -> "World"
        ))),format.raw/*14.10*/("""

        """),_display_(Seq[Any](/*16.10*/inputText(
            field = helloForm("repeat"),
            args = '_label -> "How many times?", 'size -> 3, 'placeholder -> 10
        ))),format.raw/*19.10*/("""

        """),_display_(Seq[Any](/*21.10*/select(
            field = helloForm("color"), 
            options = options(
                "" -> "Default",
                "red" -> "Red",
                "green" -> "Green",
                "blue" -> "Blue"
            ),
            args = '_label -> "Choose a color"
        ))),format.raw/*30.10*/("""

        <p class="buttons">
            <input type="submit" id="submit">
        <p>

    """)));42})),format.raw/*36.6*/("""

""")))})),format.raw/*38.2*/("""
"""))}
    }
    
    def render(helloForm:Form[scala.Tuple3[String, Int, Option[String]]]) = apply(helloForm)
    
    def f:((Form[scala.Tuple3[String, Int, Option[String]]]) => play.api.templates.Html) = (helloForm) => apply(helloForm)
    
    def ref = this

}
                /*
                    -- GENERATED --
                    DATE: Wed Aug 08 21:41:44 EDT 2012
                    SOURCE: /Users/phausel/workspace/play2/samples/scala/helloworld/app/views/index.scala.html
                    HASH: 3d139c503fe238ea9e4f52a7ec9a74fb7a55d4c1
                    MATRIX: 546->1|685->47|713->66|749->68|801->112|840->114|925->165|1002->234|1041->236|1088->247|1244->381|1291->392|1454->533|1501->544|1808->829|1933->923|1967->926
                    LINES: 19->1|23->1|25->4|26->5|26->5|26->5|30->9|30->9|30->9|32->11|35->14|37->16|40->19|42->21|51->30|57->36|59->38
                    -- GENERATED --
                */
            