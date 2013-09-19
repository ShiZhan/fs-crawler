/**
 * MessageDigest functions
 */
package util

import java.security.MessageDigest

/**
 * @author ShiZhan
 * Java MessageDigest wrapper
 */
object Hash {

  private val md5 = MessageDigest.getInstance("MD5")
  private val sha = MessageDigest.getInstance("SHA-1")
  private val sha256 = MessageDigest.getInstance("SHA-256")

  def getMD5(s: String) =
    md5.digest(s.getBytes).map("%02x".format(_)).mkString

}