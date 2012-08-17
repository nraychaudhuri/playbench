
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
object hello extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template3[String,Int,Option[String],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(name: String, repeat: Int, color: Option[String]):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.52*/("""

"""),_display_(Seq[Any](/*3.2*/main("Here is the result:")/*3.29*/ {_display_(Seq[Any](format.raw/*3.31*/("""

    <ul style="color: """),_display_(Seq[Any](/*5.24*/color/*5.29*/.getOrElse("inherited"))),format.raw/*5.52*/("""">
        """),_display_(Seq[Any](/*6.10*/for(_ <- 1 to repeat) yield /*6.31*/ {_display_(Seq[Any](format.raw/*6.33*/("""
            <li>Hello """),_display_(Seq[Any](/*7.24*/name)),format.raw/*7.28*/("""!</li>
        """)))})),format.raw/*8.10*/("""
    </ul>

    <p class="buttons">
        <a href=""""),_display_(Seq[Any](/*12.19*/routes/*12.25*/.Application.index)),format.raw/*12.43*/("""">Back to the form</a>
    </p>

""")))})),format.raw/*15.2*/("""
"""))}
    }
    
    def render(name:String,repeat:Int,color:Option[String]) = apply(name,repeat,color)
    
    def f:((String,Int,Option[String]) => play.api.templates.Html) = (name,repeat,color) => apply(name,repeat,color)
    
    def ref = this

}
                /*
                    -- GENERATED --
                    DATE: Wed Aug 08 21:41:44 EDT 2012
                    SOURCE: /Users/phausel/workspace/play2/samples/scala/helloworld/app/views/hello.scala.html
                    HASH: 9dd613b68013ec66178dc406294b2b00af9bd148
                    MATRIX: 524->1|651->51|688->54|723->81|762->83|822->108|835->113|879->136|926->148|962->169|1001->171|1060->195|1085->199|1132->215|1222->269|1237->275|1277->293|1342->327
                    LINES: 19->1|22->1|24->3|24->3|24->3|26->5|26->5|26->5|27->6|27->6|27->6|28->7|28->7|29->8|33->12|33->12|33->12|36->15
                    -- GENERATED --
                */
            