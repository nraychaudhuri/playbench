// @SOURCE:/Users/phausel/workspace/play2/samples/scala/helloworld/conf/routes
// @HASH:b7fe0437d16147eb9b2fc2e297fa0893519fd3c4
// @DATE:Wed Aug 08 21:41:42 EDT 2012


import play.core._
import play.core.Router._

import play.api.mvc._


import Router.queryString


// @LINE:12
// @LINE:9
// @LINE:6
package controllers {

// @LINE:9
// @LINE:6
class ReverseApplication {
    


 
// @LINE:9
def sayHello(): Call = {
   Call("GET", Routes.prefix + { Routes.defaultPrefix} + "hello")
}
                                                        
 
// @LINE:6
def index(): Call = {
   Call("GET", Routes.prefix)
}
                                                        

                      
    
}
                            

// @LINE:12
class ReverseAssets {
    


 
// @LINE:12
def at(file:String): Call = {
   Call("GET", Routes.prefix + { Routes.defaultPrefix} + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
}
                                                        

                      
    
}
                            
}
                    


// @LINE:12
// @LINE:9
// @LINE:6
package controllers.javascript {

// @LINE:9
// @LINE:6
class ReverseApplication {
    


 
// @LINE:9
def sayHello : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.sayHello",
   """
      function() {
      return _wA({method:"GET", url:"""" + Routes.prefix + { Routes.defaultPrefix} + """" + "hello"})
      }
   """
)
                                
 
// @LINE:6
def index : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.index",
   """
      function() {
      return _wA({method:"GET", url:"""" + Routes.prefix + """"})
      }
   """
)
                                

                      
    
}
                

// @LINE:12
class ReverseAssets {
    


 
// @LINE:12
def at : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Assets.at",
   """
      function(file) {
      return _wA({method:"GET", url:"""" + Routes.prefix + { Routes.defaultPrefix} + """" + "assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
      }
   """
)
                                

                      
    
}
                
}
          


// @LINE:12
// @LINE:9
// @LINE:6
package controllers.ref {

// @LINE:9
// @LINE:6
class ReverseApplication {
    


 
// @LINE:9
def sayHello(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.sayHello(), HandlerDef(this, "controllers.Application", "sayHello", Seq())
)
                              
 
// @LINE:6
def index(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Seq())
)
                              

                      
    
}
                            

// @LINE:12
class ReverseAssets {
    


 
// @LINE:12
def at(path:String, file:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Seq())
)
                              

                      
    
}
                            
}
                    
        