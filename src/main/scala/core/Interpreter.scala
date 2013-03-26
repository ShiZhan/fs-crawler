/**
 * command interpreter
 */
package core

/**
 * @author ShiZhan
 * translate domain specific command into SPARQL query
 */
object Interpreter {

  def interpreterGeneric(cmd: String) = cmd

  def interpreterPosix(cmd: String) = cmd

  def interpreterUnknown(cmd: String) = "unsupported command"

}