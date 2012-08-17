// @SOURCE:/Users/phausel/workspace/play2/samples/scala/helloworld/conf/routes
// @HASH:b7fe0437d16147eb9b2fc2e297fa0893519fd3c4
// @DATE:Wed Aug 08 21:41:42 EDT 2012

package controllers

object routes {
	val Application = new controllers.ReverseApplication();
	val Assets = new controllers.ReverseAssets();
	 object javascript {
		val Application = new controllers.javascript.ReverseApplication();
		val Assets = new controllers.javascript.ReverseAssets();    
	 }   
	object ref {
		val Application = new controllers.ref.ReverseApplication();
		val Assets = new controllers.ref.ReverseAssets();    
	} 
}
                