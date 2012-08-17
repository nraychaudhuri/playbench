
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
object main extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[String,Html,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title: String)(content: Html):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.32*/("""

<!DOCTYPE html>

<html>
    <head>
        <title>"""),_display_(Seq[Any](/*7.17*/title)),format.raw/*7.22*/("""</title>
        <link rel="stylesheet" media="screen" href=""""),_display_(Seq[Any](/*8.54*/routes/*8.60*/.Assets.at("stylesheets/main.css"))),format.raw/*8.94*/("""">
        <link rel="shortcut icon" type="image/png" href=""""),_display_(Seq[Any](/*9.59*/routes/*9.65*/.Assets.at("images/favicon.png"))),format.raw/*9.97*/("""">
        <script src=""""),_display_(Seq[Any](/*10.23*/routes/*10.29*/.Assets.at("javascripts/jquery-1.6.4.min.js"))),format.raw/*10.74*/("""" type="text/javascript"></script>
    </head>
    <body>

        <header>
            <a href=""""),_display_(Seq[Any](/*15.23*/routes/*15.29*/.Application.index)),format.raw/*15.47*/("""">"""),_display_(Seq[Any](/*15.50*/title)),format.raw/*15.55*/("""</a>
        </header>

        <section>
            """),_display_(Seq[Any](/*19.14*/content)),format.raw/*19.21*/("""
        </section>

    </body>
</html>
"""))}
    }
    
    def render(title:String,content:Html) = apply(title)(content)
    
    def f:((String) => (Html) => play.api.templates.Html) = (title) => (content) => apply(title)(content)
    
    def ref = this

}
                /*
                    -- GENERATED --
                    DATE: Wed Aug 08 21:41:44 EDT 2012
                    SOURCE: /Users/phausel/workspace/play2/samples/scala/helloworld/app/views/main.scala.html
                    HASH: 7416ff1db63dca84048c60d3b8858becef8337c7
                    MATRIX: 509->1|616->31|704->84|730->89|827->151|841->157|896->191|992->252|1006->258|1059->290|1120->315|1135->321|1202->366|1336->464|1351->470|1391->488|1430->491|1457->496|1548->551|1577->558
                    LINES: 19->1|22->1|28->7|28->7|29->8|29->8|29->8|30->9|30->9|30->9|31->10|31->10|31->10|36->15|36->15|36->15|36->15|36->15|40->19|40->19
                    -- GENERATED --
                */
            