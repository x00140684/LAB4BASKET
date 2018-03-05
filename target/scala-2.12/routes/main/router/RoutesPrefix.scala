
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/wdd/webapps/lab4/conf/routes
// @DATE:Mon Mar 05 14:39:09 GMT 2018


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
