/**
 * MessageDigest functions
 */
package util

import java.security.{ MessageDigest => MD }

/**
 * @author ShiZhan
 * Java MessageDigest wrapper
 */
object Hash {

  private val md5Instance = MD.getInstance("MD5")

  def getMD5(s: String) =
    md5Instance.digest(s.getBytes).map("%02x".format(_)).mkString

}